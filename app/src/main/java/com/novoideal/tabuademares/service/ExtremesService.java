package com.novoideal.tabuademares.service;

import android.os.AsyncTask;
import android.util.Log;
import android.widget.Toast;

import com.novoideal.tabuademares.controller.ExtremesController;
import com.novoideal.tabuademares.dao.ExtremesDao;
import com.novoideal.tabuademares.model.ExtremeTide;
import com.novoideal.tabuademares.model.LocationParam;

import java.util.ArrayList;
import java.util.List;

public class ExtremesService {

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

        new ScrapeTask(city).execute();
        return new ArrayList<>();
    }

    public void cleanCondiction(LocationParam city) {
        extremesDao.clearCondiction(city);
    }

    private void saveConditions(List<ExtremeTide> conditions) {
        for (ExtremeTide condition : conditions) {
            if (!extremesDao.contains(condition)) {
                extremesDao.addNew(condition);
            }
        }
    }

    private class ScrapeTask extends AsyncTask<Void, Void, List<ExtremeTide>> {

        private final LocationParam city;

        ScrapeTask(LocationParam city) {
            this.city = city;
        }

        @Override
        protected List<ExtremeTide> doInBackground(Void... voids) {
            try {
                return new TabuadeMaresScraperService().scrape(city);
            } catch (Exception e) {
                Log.e("ExtremesService", "Scrape failed: " + e.getMessage(), e);
                return new ArrayList<>();
            }
        }

        @Override
        protected void onPostExecute(List<ExtremeTide> result) {
            if (result != null && !result.isEmpty()) {
                saveConditions(result);
                controller.populateView(result);
            } else if (city.getTabuademaresPath() != null && !city.getTabuademaresPath().isEmpty()) {
                Toast.makeText(controller.getContext(),
                        "Sem dados de maré para esta data", Toast.LENGTH_SHORT).show();
            }
        }
    }
}
