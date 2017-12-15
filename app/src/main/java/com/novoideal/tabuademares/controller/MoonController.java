package com.novoideal.tabuademares.controller;

import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.novoideal.tabuademares.R;
import com.novoideal.tabuademares.model.LocationParam;

import org.shredzone.commons.suncalc.MoonIllumination;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by Helio on 14/08/2017.
 */

public class MoonController  {

    public View rootView;
    public LocationParam city;

    public MoonController(View view, LocationParam city) {
        this.rootView = view;
        this.city = city;
    }


    private String getHold(Double age) {
        if (age > 27 || age < 3) {
            return "de lua nova";
        }

        if (age > 4 && age < 10) {
            return "de quarto crescente";
        }

        if (age > 11 && age < 17) {
            return "de lua cheia";
        }

        if (age > 19 && age < 25) {
            return "de quarto minguante";
        }

        return "pouca influência de lua";
    }

    private Map getPhase(Double age) {
        Map result = new HashMap();
        if (age >= 29 || age <= 1) {
            result.put("phase", "Nova");
            result.put("ic", R.drawable.moon_new);
            return result;
        }

        if (age >= 7 && age <= 8) {
            result.put("phase", "Quarto Crescente");
            result.put("ic", R.drawable.moon_quarter_crescent);
            return result;
        }

        if (age >= 14 && age <= 15) {
            result.put("phase", "Cheia");
            result.put("ic", R.drawable.moon_full);
            return result;
        }

        if (age >= 21 && age <= 22) {
            result.put("phase", "Quarto Minguante");
            result.put("ic", R.drawable.moon_quarter_decrescent);
            return result;
        }

        if (age > 1 && age < 7) {
            result.put("phase", "Crescente Côncava");
            result.put("ic", R.drawable.moon_crescent);
            return result;
        }

        if (age > 8 && age < 14) {
            result.put("phase", "Crescente Gibosa");
            result.put("ic", R.drawable.moon_gibous_crescent);
            return result;
        }

        if (age > 15 && age < 21) {
            result.put("phase", "Minguante Gibosa");
            result.put("ic", R.drawable.moon_gibous_decrescent);
            return result;
        }

        if (age > 22 && age < 29) {
            result.put("phase", "Minguante Côncava");
            result.put("ic", R.drawable.moon_decrescent);
            return result;
        }

        result.put("phase", "");
        result.put("ic", R.drawable.moon_crescent);
        return result;
    }

    public void request() {
        Double age = MoonIllumination.of(city.getDate()).getPhase() * 29.5308;
        TextView textView = (TextView) rootView.findViewById(R.id.moon_phase);
        Map phase = getPhase(age);
        textView.setText(phase.get("phase").toString());

        textView = (TextView) rootView.findViewById(R.id.moon_hold);
//        textView.setText(rootView.getContext().getString(R.string.moon_hold, getHold(age)));
        textView.setText("Influência: " + getHold(age));

        ImageView imageView = rootView.findViewById(R.id.weather_moon_ic);
        imageView.setImageResource((Integer) phase.get("ic"));

    }

}
