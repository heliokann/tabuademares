package com.novoideal.tabuademares.controller;

import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import com.novoideal.tabuademares.R;
import com.novoideal.tabuademares.model.SeaCondition;
import com.novoideal.tabuademares.model.CityCondition;
import com.novoideal.tabuademares.service.SeaConditionService;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by Helio on 14/08/2017.
 */

public class SeaConditionController {

    private CityCondition city;

    private static Map<String, SeaCondition> cacheWeater = new HashMap<>();
    private View rootView = null;

    public static void clearCache() {
        cacheWeater.clear();
    }

    public SeaConditionController(View view) {
        this.rootView = view;
    }

    protected void updateLabel(int elementID, String value) {
        ((TextView) rootView.findViewById(elementID)).setText(value);
    }

    public void updateWeather(SeaCondition w, int agitation, int swell, int wind) {
        updateLabel(agitation, w.getAgitation());
        updateLabel(swell, w.getSewll() + ", " + w.getHeight() + "m");
        updateLabel(wind, w.getWind_dir() + ", " + w.getWind() + " nós");
    }

    public void callback(List<SeaCondition> result) {
        if(result == null || result.isEmpty()) {
            return;
        }

        for (SeaCondition weater : result) {
            switch (weater.getPeriod()){
                case "manha" :
                    updateWeather(weater, R.id.a_m, R.id.s_m, R.id.w_m);
                    break;
                case "tarde" :
                    updateWeather(weater, R.id.a_t, R.id.s_t, R.id.w_t);
                    break;
                case "noite" :
                    updateWeather(weater, R.id.a_n, R.id.s_n, R.id.w_n);
                    break;
            }
        }

    }

    public void request(CityCondition city) {
        this.city = city;
        new AsyncUpdater().execute(this);
    }

    public CityCondition getCity() {
        return city;
    }

    public Context getContext() {
        return rootView.getContext();
    }

}

class AsyncUpdater extends AsyncTask<SeaConditionController, Void, List<SeaCondition>> {

    private SeaConditionController controller;

    @Override
    protected List<SeaCondition> doInBackground(SeaConditionController... controllers) {
        this.controller = controllers[0];
        try {
            return new SeaConditionService(controller.getContext()).geCondition(controller.getCity());
        } catch (Exception e) {
            Log.e(AsyncUpdater.class.getCanonicalName(), e.getMessage());
        }
        return null;
    }

    @Override
    protected void onPostExecute(List<SeaCondition> result) {
        controller.callback(result);
    }
}
