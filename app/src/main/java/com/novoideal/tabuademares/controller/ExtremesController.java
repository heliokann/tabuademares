package com.novoideal.tabuademares.controller;

import android.view.View;
import android.widget.Toast;

import com.novoideal.tabuademares.R;
import com.novoideal.tabuademares.controller.base.AbstractController;
import com.novoideal.tabuademares.controller.base.BaseController;
import com.novoideal.tabuademares.service.CityCondition;

import org.joda.time.DateTime;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.DecimalFormat;
import java.text.NumberFormat;

/**
 * Created by Helio on 14/08/2017.
 */

public class ExtremesController extends AbstractController implements BaseController {

    private String baseUrl = "https://www.worldtides.info/api?key=644e03a8-135d-4480-97ce-fef244faae28&extremes=";
    private String url = "https://www.worldtides.info/api?extremes=key=644e03a8-135d-4480-97ce-fef244faae28&extremes=&lat=-22.87944&lon=-42.01860";

    public ExtremesController(View view) {
        super(view);
    }

    @Override
    public void callback(int elementID, JSONObject response) {
        try {
            JSONArray extremes = response.getJSONArray("extremes");
            String low = "";
            String high = "";
            DateTime now = DateTime.now();
            NumberFormat nf = new DecimalFormat("#.##");
            for (int i = 0; i < extremes.length(); i++) {
                JSONObject extreme = extremes.getJSONObject(i);
                DateTime exDate = new DateTime(extreme.getString("date"));
                if (exDate.getDayOfMonth() == now.getDayOfMonth()) {
                    if (extreme.getString("type").equals("Low")) {
                        low += exDate.toString("HH:mm") + " (" + nf.format(extreme.getDouble("height") + 0.45) + "m)    ";
                    } else {
                        high += exDate.toString("HH:mm") + " (" + nf.format(extreme.getDouble("height") + 0.45) + "m)    ";
                    }
                }
            }
            updateLabel(R.id.low_water, getContext().getString(R.string.low_water, low));
            updateLabel(R.id.hide_tide, getContext().getString(R.string.hide_tide, high));
        } catch (JSONException e) {
            Toast.makeText(getContext(), "Deu ruim no extremos: " + e.getMessage(), Toast.LENGTH_LONG).show();
        }
    }

    @Override
    public void request() {
        request(null);
    }

    public void request(CityCondition city) {
        city = city != null ? city : CityCondition.defaultCity;
        url = baseUrl + "&lat="+city.getLatitude() + "&lon=" + city.getLongetude();

        doRequest(url, R.id.low_water, this);
        doRequest(url, R.id.hide_tide, this);
    }

    @Override
    public String getURL() {
        return url;
    }
}
