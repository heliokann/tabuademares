package com.novoideal.tabuademares.service;

import android.widget.Toast;

import com.novoideal.tabuademares.controller.ExtremesController;
import com.novoideal.tabuademares.dao.ExtremesDao;
import com.novoideal.tabuademares.model.ExtremeTide;

import org.joda.time.DateTime;
import org.joda.time.LocalDate;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;


/**
 * Created by Helio on 21/10/2017.
 */

public class ExtremesService extends BaseRequestService{

    private ExtremesDao extremesDao;
    private ExtremesController controller;

    public ExtremesService(ExtremesController controller) {
        super(controller.getContext());
        extremesDao = new ExtremesDao(this.getContext());
        this.controller = controller;
    }

    @Override
    public void callback(JSONObject response) {
        List<ExtremeTide> extremes = new ArrayList<>();
        try {
            JSONArray arrayExtremes = response.getJSONArray("extremes");
            for (int i = 0; i < arrayExtremes.length(); i++) {
                JSONObject jsonExtreme = arrayExtremes.getJSONObject(i);
                DateTime exDate = new DateTime(jsonExtreme.getString("date"));
                ExtremeTide extreme = new ExtremeTide();
                extreme.setCity(response.getString("station"));
                extreme.setLat(response.getDouble("responseLat"));
                extreme.setLon(response.getDouble("responseLon"));

                extreme.setDate(new LocalDate(exDate).toDate());
                extreme.setMinute(exDate.getMinuteOfHour());
                extreme.setHour(exDate.getHourOfDay());
                extreme.setType(jsonExtreme.getString("type"));
                extreme.setHeight(jsonExtreme.getDouble("height") + 0.45);

                extremes.add(extreme);
            }
        } catch (JSONException e) {
            Toast.makeText(getContext(), "Deu ruim no extremos: " + e.getMessage(), Toast.LENGTH_LONG).show();
        }

        saveSeaCondiction(extremes);

        controller.populateView(extremes);
    }

    public List<ExtremeTide> geCondition(CityCondition city) {
        List<ExtremeTide> conditions =  extremesDao.geCondition(city);
        if(conditions != null && !conditions.isEmpty()){
            return conditions;
        }

        doRequest(controller.getURL(), this);

        return conditions;


    }

    private void saveSeaCondiction(List<ExtremeTide> conditions) {
        for (ExtremeTide condition: conditions) {
            extremesDao.addNew(condition);
        }
    }


}
