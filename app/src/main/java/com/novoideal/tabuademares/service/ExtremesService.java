package com.novoideal.tabuademares.service;

import android.os.Handler;
import android.os.Looper;
import android.util.Log;

import com.novoideal.tabuademares.R;
import com.novoideal.tabuademares.controller.ExtremesController;
import com.novoideal.tabuademares.dao.ExtremesDao;
import com.novoideal.tabuademares.model.ExtremeTide;
import com.novoideal.tabuademares.model.LocationParam;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

public class ExtremesService {

    private static final String TAG = "ExtremesService";
    private static final Executor EXECUTOR = Executors.newSingleThreadExecutor();

    private final ExtremesDao extremesDao;
    private final ExtremesController controller;

    public ExtremesService(ExtremesController controller) {
        this.extremesDao = new ExtremesDao(controller.getContext());
        this.controller = controller;
    }

    public List<ExtremeTide> geCondition(LocationParam city) {
        List<ExtremeTide> conditions = extremesDao.geCondition(city);
        if (conditions != null && !conditions.isEmpty()) {
            return conditions;
        }

        scrapeAsync(city);
        return new ArrayList<>();
    }

    public void retry(LocationParam city) {
        extremesDao.clearCondiction(city);
        scrapeAsync(city);
    }

    public void cleanCondiction(LocationParam city) {
        extremesDao.clearCondiction(city);
    }

    private void scrapeAsync(LocationParam city) {
        Handler mainHandler = new Handler(Looper.getMainLooper());
        EXECUTOR.execute(() -> {
            List<ExtremeTide> result;
            try {
                result = new TabuadeMaresScraperService().scrape(city);
            } catch (Exception e) {
                Log.e(TAG, "Scrape failed: " + e.getMessage(), e);
                result = new ArrayList<>();
            }

            final List<ExtremeTide> finalResult = result;
            mainHandler.post(() -> {
                if (finalResult != null && !finalResult.isEmpty()) {
                    saveConditions(finalResult);
                    controller.populateView(finalResult);
                } else if (city.getTabuademaresPath() != null && !city.getTabuademaresPath().isEmpty()) {
                    List<ExtremeTide> cached = extremesDao.geCondition(city);
                    if (cached != null && !cached.isEmpty()) {
                        controller.populateView(cached);
                    } else {
                        String msg = controller.getContext().getString(R.string.no_tide_data_scrape_error);
                        controller.showScrapeError(msg, () -> retry(city));
                    }
                }
            });
        });
    }

    private void saveConditions(List<ExtremeTide> conditions) {
        for (ExtremeTide condition : conditions) {
            if (!extremesDao.contains(condition)) {
                extremesDao.addNew(condition);
            }
        }
    }
}
