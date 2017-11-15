package com.novoideal.tabuademares.controller;

import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import com.novoideal.tabuademares.R;
import com.novoideal.tabuademares.model.LocationParam;
import com.novoideal.tabuademares.model.SeaCondition;
import com.novoideal.tabuademares.service.SeaConditionService;

import java.util.List;

/**
 * Created by Helio on 14/08/2017.
 */

public class SeaConditionController {

    private LocationParam city;
    private View rootView;

    public SeaConditionController(View view, LocationParam city) {
        this.rootView = view;
        this.city= city;
    }

    protected void updateLabel(int elementID, String value) {
        ((TextView) rootView.findViewById(elementID)).setText(value);
    }

    public void updateCondiction(SeaCondition w, int agitation, int swell, int wind) {
        updateLabel(agitation, w.getAgitation());
        updateLabel(swell, w.getSewll() + ", " + w.getHeight() + "m");
        updateLabel(wind, w.getWind_dir() + ", " + w.getWind() + " nós");
    }

    public void callback(List<SeaCondition> result) {
        if(result == null || result.isEmpty()) {
            return;
        }

        for (SeaCondition condition : result) {
            switch (condition.getPeriod()){
                case "manha" :
                    updateCondiction(condition, R.id.a_m, R.id.s_m, R.id.w_m);
                    break;
                case "tarde" :
                    updateCondiction(condition, R.id.a_t, R.id.s_t, R.id.w_t);
                    break;
                case "noite" :
                    updateCondiction(condition, R.id.a_n, R.id.s_n, R.id.w_n);
                    break;
            }
        }

    }

    public void request() {
        new AsyncUpdater().execute(this);
    }

    public LocationParam getCity() {
        return city;
    }

    public Context getContext() {
        return rootView.getContext();
    }

    public void update() {
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
