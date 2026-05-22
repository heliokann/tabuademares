package com.novoideal.tabuademares.controller;

import android.content.Context;
import android.view.View;
import android.widget.GridView;

import com.novoideal.tabuademares.adapter.ExtremeViewAdapter;
import com.novoideal.tabuademares.R;
import com.novoideal.tabuademares.model.ExtremeTide;
import com.novoideal.tabuademares.model.LocationParam;
import com.novoideal.tabuademares.service.ExtremesService;

import org.joda.time.DateTime;
import org.joda.time.LocalDate;

import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Helio on 14/08/2017.
 */

public class ExtremesController  {

    private static final String STORMGLASS_BASE = "https://api.stormglass.io/v2/tide/extremes/point";
    private String url = STORMGLASS_BASE;
    private LocationParam city;
    public View rootView;

    public ExtremesController(View view, LocationParam city) {
        this.rootView = view;
        this.city = city;
    }

    public void request() {
        long startUnix = new LocalDate(city.getDate()).toDateTimeAtStartOfDay().getMillis() / 1000;
        long endUnix = startUnix + 86400;
        url = STORMGLASS_BASE
                + "?lat=" + city.getLatitude()
                + "&lng=" + city.getLongetude()
                + "&start=" + startUnix
                + "&end=" + endUnix;

        List<ExtremeTide> result = new ExtremesService(this).geCondition(city);

        if (!result.isEmpty()) {
            populateView(result);
        }
    }

    public void populateView(List<ExtremeTide> result) {
        String low = "";
        String high = "";
        DateTime cityDate = new DateTime(city.getDate());
        List<ExtremeTide> today = new ArrayList<>();

        for (ExtremeTide extreme : result) {
            NumberFormat nf = new DecimalFormat("#.##");
            DateTime exDate = new DateTime(extreme.getDate());
            if (exDate.getDayOfMonth() == cityDate.getDayOfMonth()) {
                today.add(extreme);
                if (extreme.getType().equals("Low")) {
                    low += extreme + "    ";
                } else {
                    high += extreme + "    ";
                }
            }
        }

//        ((TextView) rootView.findViewById(R.id.low_water)).setText(getContext().getString(R.string.low_water, low));
//        ((TextView) rootView.findViewById(R.id.hight_tide)).setText(getContext().getString(R.string.hight_tide, high));

        createGridView(today);
    }

    public void createGridView(List<ExtremeTide> today) {
        GridView gv = rootView.findViewById(R.id.grid_extreme);
        gv.setNumColumns(today.size());
        gv.setAdapter(new ExtremeViewAdapter(rootView, today));
        ((ExtremeViewAdapter) gv.getAdapter()).notifyDataSetChanged();
        gv.invalidateViews();
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