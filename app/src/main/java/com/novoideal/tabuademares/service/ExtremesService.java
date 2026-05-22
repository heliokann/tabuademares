package com.novoideal.tabuademares.service;

import android.widget.Toast;

import com.novoideal.tabuademares.controller.ExtremesController;
import com.novoideal.tabuademares.dao.ExtremesDao;
import com.novoideal.tabuademares.dao.LocationParamDao;
import com.novoideal.tabuademares.model.LocationParam;
import com.novoideal.tabuademares.model.ExtremeTide;

import org.joda.time.DateTime;
import org.joda.time.LocalDate;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


/**
 * Created by Helio on 21/10/2017.
 */

public class ExtremesService extends BaseRequestService{

    // Cadastre-se em https://stormglass.io para obter uma chave gratuita (10 req/dia)
    private static final String STORMGLASS_KEY = "YOUR_STORMGLASS_API_KEY_HERE";

    private ExtremesDao extremesDao;
    private LocationParamDao locationParamDao;
    private ExtremesController controller;

    public ExtremesService(ExtremesController controller) {
        super(controller.getContext());
        this.extremesDao = new ExtremesDao(this.getContext());
        this.controller = controller;
        this.locationParamDao = new LocationParamDao(this.getContext());
    }

    @Override
    public void callback(JSONObject response) {
        List<ExtremeTide> extremes = new ArrayList<>();
        try {
            LocationParam city = controller.getCity();

            double lat = city.getLatitude();
            double lon = city.getLongetude();
            String stationName = city.getName();

            JSONObject meta = response.optJSONObject("meta");
            if (meta != null) {
                lat = meta.optDouble("lat", lat);
                lon = meta.optDouble("lng", lon);
                JSONObject station = meta.optJSONObject("station");
                if (station != null) {
                    stationName = station.optString("name", stationName);
                }
            }

            city.setLatExtreme(lat);
            city.setLongExtreme(lon);
            locationParamDao.updateExtremeParams(city);

            JSONArray data = response.getJSONArray("data");
            for (int i = 0; i < data.length(); i++) {
                JSONObject item = data.getJSONObject(i);
                DateTime exDate = new DateTime(item.getString("time"));
                String rawType = item.getString("type"); // "high" or "low"

                ExtremeTide extreme = new ExtremeTide();
                extreme.setCity(stationName);
                extreme.setLat(lat);
                extreme.setLon(lon);
                extreme.setDate(new LocalDate(exDate).toDate());
                extreme.setFullDate(exDate.toDate());
                extreme.setHour(exDate.getHourOfDay());
                extreme.setMinute(exDate.getMinuteOfHour());
                extreme.setType(Character.toUpperCase(rawType.charAt(0)) + rawType.substring(1));
                extreme.setHeight(item.getDouble("height"));

                extremes.add(extreme);
            }
        } catch (JSONException e) {
            Toast.makeText(getContext(), "Deu ruim no extremos: " + e.getMessage(), Toast.LENGTH_LONG).show();
        }

        saveSeaCondiction(extremes);
        controller.populateView(extremes);
    }

    @Override
    public Map<String, String> getHeaders() {
        Map<String, String> headers = new HashMap<>();
        headers.put("Authorization", STORMGLASS_KEY);
        return headers;
    }

    @Override
    public void cleanCondiction(LocationParam city) {
        extremesDao.clearCondiction(city);
    }

    public List<ExtremeTide> geCondition(LocationParam city) {
        List<ExtremeTide> conditions =  extremesDao.geCondition(city);
        if(conditions != null && !conditions.isEmpty()){
            return conditions;
        }

        doRequest(controller.getURL(), this);

        return conditions;


    }

    private void saveSeaCondiction(List<ExtremeTide> conditions) {
        for (ExtremeTide condition: conditions) {
            if (!extremesDao.contains(condition)) {
                extremesDao.addNew(condition);
            }
        }
    }


}
