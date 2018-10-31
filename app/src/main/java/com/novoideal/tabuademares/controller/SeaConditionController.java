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

import org.joda.time.DateTime;
import org.joda.time.Days;

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

//        SeaCondition[] today = new SeaCondition[3];

        for (SeaCondition condition : result) {

//            System.out.println("Mesmo dia = " + (condition.getDate().getDay() == city.getDate().getDay()) + " - Período: " + condition.getPeriod());
            if(Days.daysBetween(new DateTime(condition.getDate()), new DateTime(city.getDate())).getDays() != 0){
                continue;
            }

            System.out.println("Mesmo dia = " + (condition.getDate().getDay() == city.getDate().getDay()) + " - Período: " + condition.getPeriod());

            switch (condition.getPeriod()){
                case "manha" :
                case "12" :
//                    today[0] = condition;
                    updateCondiction(condition, R.id.a_m, R.id.s_m, R.id.w_m);
                    break;
                case "tarde" :
                case "18" :
//                    today[1] = condition;
                    updateCondiction(condition, R.id.a_t, R.id.s_t, R.id.w_t);
                    break;
                case "noite" :
                case "21" :
//                    today[2] = condition;
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
        new SeaConditionService(this.getContext()).cleanCondiction(city);
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
