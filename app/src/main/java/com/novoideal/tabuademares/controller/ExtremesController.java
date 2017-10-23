package com.novoideal.tabuademares.controller;

import android.content.Context;
import android.view.View;
import android.widget.TextView;

import com.novoideal.tabuademares.R;
import com.novoideal.tabuademares.model.ExtremeTide;
import com.novoideal.tabuademares.service.CityCondition;
import com.novoideal.tabuademares.service.ExtremesService;

import org.joda.time.DateTime;

import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.List;

/**
 * Created by Helio on 14/08/2017.
 */

public class ExtremesController  {

    private String baseUrl = "https://www.worldtides.info/api?key=644e03a8-135d-4480-97ce-fef244faae28&extremes=";
    private String url = "https://www.worldtides.info/api?key=644e03a8-135d-4480-97ce-fef244faae28&extremes=&lat=-22.87944&lon=-42.01860";
    private CityCondition city;
    public static View rootView = null;

    public ExtremesController(View view) {
        if (view != null) {
            this.rootView = view;
        }
    }

    public void request(CityCondition city) {
        city = city != null ? city : CityCondition.defaultCity;
        this.city = city;
        url = baseUrl + "&lat=" + city.getLatitude() + "&lon=" + city.getLongetude();

        List<ExtremeTide> result = new ExtremesService(this).geCondition(city);

        if (!result.isEmpty()) {
            populateView(result);
        }
    }

    public void populateView(List<ExtremeTide> result) {
        String low = "";
        String hight = "";
        DateTime now = DateTime.now();

        for (ExtremeTide extreme : result) {
            NumberFormat nf = new DecimalFormat("#.##");
            DateTime exDate = new DateTime(extreme.getDate());
            if (exDate.getDayOfMonth() == now.getDayOfMonth()) {
                if (extreme.getType().equals("Low")) {
                    low += extreme + "    ";
                } else {
                    hight += extreme + "    ";
                }
            }
            ((TextView) rootView.findViewById(R.id.low_water)).setText(getContext().getString(R.string.low_water, low));
            ((TextView) rootView.findViewById(R.id.hight_tide)).setText(getContext().getString(R.string.hight_tide, hight));
        }
    }

    public CityCondition getCity() {
        return city;
    }


    public String getURL() {
        return url;
    }

    public Context getContext() {
        return rootView.getContext();
    }
}


//class ExtremeAsyncUpdater extends AsyncTask<ExtremesController, Void, List<ExtremeTide>> {
//
//    private ExtremesController controller;
//
//    @Override
//    protected List<ExtremeTide> doInBackground(ExtremesController... controllers) {
//        this.controller = controllers[0];
//        try {
//            return new ExtremesService(controller.getContext()).geCondition(controller.getCity());
//        } catch (Exception e) {
//            Log.e(AsyncUpdater.class.getCanonicalName(), e.getMessage());
//        }
//        return null;
//    }
//
//    @Override
//    protected void onPostExecute(List<ExtremeTide> result) {
//        controller.populateView(result);
//    }
//}
