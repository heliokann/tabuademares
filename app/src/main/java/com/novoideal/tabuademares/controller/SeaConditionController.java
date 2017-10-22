package com.novoideal.tabuademares.controller;

import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import com.novoideal.tabuademares.R;
import com.novoideal.tabuademares.model.SeaCondition;
import com.novoideal.tabuademares.service.CityCondition;
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
    public static View rootView = null;

    public static void clearCache() {
        cacheWeater.clear();
    }

    public SeaConditionController(View view) {
        if (view != null) {
            this.rootView = view;
        }
    }

    protected void updateLabel(int elementID, String value) {
        ((TextView) rootView.findViewById(elementID)).setText(value);
    }

    public void updateWeather(String period, int agitation, int swell, int wind) {
        SeaCondition w = cacheWeater.get(period);
        updateLabel(agitation, w.getAgitation());
        updateLabel(swell, w.getSewll() + ", " + w.getHeight() + "m");
        updateLabel(wind, w.getWind_dir() + ", " + w.getWind() + " nós");
    }

    private void updateFromCache() {
        updateWeather("manha", R.id.a_m, R.id.s_m, R.id.w_m);
        updateWeather("tarde", R.id.a_t, R.id.s_t, R.id.w_t);
        updateWeather("noite", R.id.a_n, R.id.s_n, R.id.w_n);
    }


    public void callback(List<SeaCondition> result) {
        if (cacheWeater.isEmpty()) {
            for (SeaCondition weater : result) {
                cacheWeater.put(weater.getPeriod(), weater);
            }
        }

        updateFromCache();
    }

    public void request() {
        request(null);
    }

    public void request(CityCondition city) {
        this.city = city != null ? city : CityCondition.defaultCity;
        if(hasCache()){
            updateFromCache();
            return;
        }

        new AsyncUpdater().execute(this);
    }

    public CityCondition getCity() {
        return city;
    }

    public Context getContext() {
        return rootView.getContext();
    }

    public boolean hasCache() {
        return !cacheWeater.isEmpty();
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
