package com.novoideal.tabuademares.controller;

import android.view.View;
import android.widget.Toast;

import com.novoideal.tabuademares.R;
import com.novoideal.tabuademares.controller.base.AbstractController;
import com.novoideal.tabuademares.controller.base.BaseController;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by Helio on 14/08/2017.
 */

public class MoonController extends AbstractController implements BaseController {

    private String url = "https://burningsoul-moon-v1.p.mashape.com";

    public MoonController(View view) {
        super(view);
    }

    @Override
    public Map<String, String> getHeaders()  {
        HashMap<String, String> headers = new HashMap<String, String>();
        headers.put("Content-Type", "application/json");
        headers.put("X-Mashape-Key", "TO9BQLIKVqmshVCQaA25CtETEuwtp1jjX2qjsnU32aifjyF3NI");
        return headers;
    }

    @Override
    public void callback(int elementID, JSONObject response) {
        String stage = response.optString("stage");
        if ("waning".equals(stage)) {
            stage = getContext().getString(R.string.waning);
        } else if ("waxing".equals(stage)) {
            stage = getContext().getString(R.string.waxing);
        }
        Double age = response.optDouble("age");
        updateLabel(R.id.moon_phase, getContext().getString(R.string.moon_phase, stage, age));
    }

    @Override
    public void request() {
        doRequest(url, R.id.moon_phase, this);
    }

    @Override
    public String getURL() {
        return url;
    }
}
