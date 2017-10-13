package com.novoideal.tabuademares.controller;

import android.view.View;
import android.widget.TextView;

import com.novoideal.tabuademares.R;
import com.novoideal.tabuademares.controller.base.AbstractController;
import com.novoideal.tabuademares.controller.base.BaseController;

import org.joda.time.LocalDate;
import org.json.JSONObject;
import org.shredzone.commons.suncalc.MoonIllumination;

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
    public Map<String, String> getHeaders() {
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

    private String getHold(Double age) {
        if (age > 28 && age < 2) {
            return "de lua nova";
        }

        if (age > 5 && age < 9) {
            return "de quarto crescente";
        }

        if (age > 12 && age < 16) {
            return "da lua cheia";
        }

        if (age > 20 && age < 24) {
            return "de quarto minguante";
        }

        return "";
    }

    private String getPhase(Double age) {
        if (age > 29 || age < 1) {
            return "Nova";
        }

        if (age > 7 && age < 8) {
            return "Quarto Crescente";
        }

        if (age > 14 && age < 15) {
            return "Cheia";
        }

        if (age > 21 && age < 22) {
            return "Quarto Minguante";
        }

        if (age > 1 && age < 7) {
            return "Crescente Côncava";
        }

        if (age > 8 && age < 14) {
            return "Crescente Gibosa";
        }

        if (age > 15 && age < 21) {
            return "Minguante Gibosa";
        }

        if (age > 22 && age < 29) {
            return "Minguante Côncava";
        }

        return "";
    }

    public void request() {
        LocalDate dt = LocalDate.now();
        Double age = MoonIllumination.of(dt.toDate()).getPhase() * 29.5308;
        TextView textView = (TextView) rootView.findViewById(R.id.moon_phase);
        textView.setText(getContext().getString(R.string.moon_phase, getPhase(age), age));

        textView = (TextView) rootView.findViewById(R.id.moon_hold);
        textView.setText(getContext().getString(R.string.moon_hold, getHold(age)));

//        doRequest(url, R.id.moon_phase, this);
    }

    @Override
    public String getURL() {
        return url;
    }
}
