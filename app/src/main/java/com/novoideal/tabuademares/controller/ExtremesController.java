package com.novoideal.tabuademares.controller;

import android.content.Context;
import android.view.View;
import android.widget.TextView;

import com.novoideal.tabuademares.R;
import com.novoideal.tabuademares.model.LocationParam;
import com.novoideal.tabuademares.model.ExtremeTide;
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
    private LocationParam city;
    public View rootView;

    public ExtremesController(View view, LocationParam city) {
        this.rootView = view;
        this.city = city;
    }

    public void request() {
        url = baseUrl + "&lat=" + city.getLatitude() + "&lon=" + city.getLongetude();

        List<ExtremeTide> result = new ExtremesService(this).geCondition(city);

        if (!result.isEmpty()) {
            populateView(result);
        }
    }

    public void populateView(List<ExtremeTide> result) {
        String low = "";
        String high = "";
        DateTime cityDate = new DateTime(city.getDate());

        for (ExtremeTide extreme : result) {
            NumberFormat nf = new DecimalFormat("#.##");
            DateTime exDate = new DateTime(extreme.getDate());
            if (exDate.getDayOfMonth() == cityDate.getDayOfMonth()) {
                if (extreme.getType().equals("Low")) {
                    low += extreme + "    ";
                } else {
                    high += extreme + "    ";
                }
            }
        }

        ((TextView) rootView.findViewById(R.id.low_water)).setText(getContext().getString(R.string.low_water, low));
        ((TextView) rootView.findViewById(R.id.hight_tide)).setText(getContext().getString(R.string.hight_tide, high));
    }

    public LocationParam getCity() {
        return city;
    }


    public String getURL() {
        return url;
    }

    public Context getContext() {
        return rootView.getContext();
    }

    public void update() {
        new ExtremesService(this).cleanCondiction(city);
    }
}