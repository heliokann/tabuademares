package com.novoideal.tabuademares.controller;

import android.view.View;
import android.widget.TextView;

import com.novoideal.tabuademares.R;
import com.novoideal.tabuademares.model.LocationParam;

import org.shredzone.commons.suncalc.MoonIllumination;

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
            return "da lua cheia";
        }

        if (age > 19 && age < 25) {
            return "de quarto minguante";
        }

        return "Pouca influência";
    }

    private String getPhase(Double age) {
        if (age >= 29 || age <= 1) {
            return "Nova";
        }

        if (age >= 7 && age <= 8) {
            return "Quarto Crescente";
        }

        if (age >= 14 && age <= 15) {
            return "Cheia";
        }

        if (age >= 21 && age <= 22) {
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
        Double age = MoonIllumination.of(city.getDate()).getPhase() * 29.5308;
        TextView textView = (TextView) rootView.findViewById(R.id.moon_phase);
//        textView.setText(rootView.getContext().getString(R.string.moon_phase, getPhase(age), age));
        textView.setText(getPhase(age));

        textView = (TextView) rootView.findViewById(R.id.moon_hold);
//        textView.setText(rootView.getContext().getString(R.string.moon_hold, getHold(age)));
        textView.setText("Influência: lua " + getHold(age));
    }

}
