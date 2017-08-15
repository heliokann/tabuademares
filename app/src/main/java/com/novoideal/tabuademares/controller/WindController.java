package com.novoideal.tabuademares.controller;

import android.view.View;
import android.widget.Toast;

import com.novoideal.tabuademares.R;
import com.novoideal.tabuademares.controller.base.AbstractController;
import com.novoideal.tabuademares.controller.base.BaseController;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by Helio on 14/08/2017.
 */

public class WindController extends AbstractController implements BaseController {

    private String url = "http://api.openweathermap.org/data/2.5/weather?APPID=774a44e054ada12ca9a45c2eabd53aa6&id=3468615";

    public WindController(View view) {
        super(view);
    }

    @Override
    public void callback(int elementID, JSONObject response) {
        int wind = 0;
        try {
            wind = response.getJSONObject("wind").optInt("deg");
            Double speed = response.getJSONObject("wind").optDouble("speed");
            updateLabel(R.id.wind, getContext().getString(R.string.wind, wind, speed));
        } catch (JSONException e) {
            Toast.makeText(getContext(), "Deu ruim no wind: " + e.getMessage(), Toast.LENGTH_LONG).show();
        }
    }

    @Override
    public void request() {
        doRequest(url, R.id.wind, this);
    }

    @Override
    public String getURL() {
        return url;
    }
}
