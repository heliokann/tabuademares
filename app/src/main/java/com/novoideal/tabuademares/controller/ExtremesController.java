package com.novoideal.tabuademares.controller;

import android.content.Context;
import android.view.View;
import android.widget.GridView;

import com.novoideal.tabuademares.adapter.ExtremeViewAdapter;
import com.novoideal.tabuademares.R;
import com.novoideal.tabuademares.model.ExtremeTide;
import com.novoideal.tabuademares.model.LocationParam;
import com.novoideal.tabuademares.service.ExtremesService;

import org.joda.time.LocalDate;

import java.util.ArrayList;
import java.util.List;

public class ExtremesController {

    private LocationParam city;
    public View rootView;

    public ExtremesController(View view, LocationParam city) {
        this.rootView = view;
        this.city = city;
    }

    public void request() {
        List<ExtremeTide> result = new ExtremesService(this).geCondition(city);
        if (!result.isEmpty()) {
            populateView(result);
        }
    }

    public void populateView(List<ExtremeTide> result) {
        LocalDate cityDate = new LocalDate(city.getDate());
        List<ExtremeTide> today = new ArrayList<>();

        for (ExtremeTide extreme : result) {
            if (new LocalDate(extreme.getDate()).equals(cityDate)) {
                today.add(extreme);
            }
        }

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

    public Context getContext() {
        return rootView.getContext();
    }

    public void update() {
        new ExtremesService(this).cleanCondiction(city);
    }
}
