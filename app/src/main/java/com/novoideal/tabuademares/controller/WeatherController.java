package com.novoideal.tabuademares.controller;

import android.content.Context;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.novoideal.tabuademares.R;
import com.novoideal.tabuademares.model.LocationParam;
import com.novoideal.tabuademares.model.Weather;
import com.novoideal.tabuademares.service.WeatherService;

import org.joda.time.DateTime;

import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.List;

/**
 * Created by Helio on 14/08/2017.
 */

public class WeatherController {

    private String baseUrl = "https://api.open-meteo.com/v1/forecast?daily=weathercode,temperature_2m_max,temperature_2m_min,windspeed_10m_max,winddirection_10m_dominant&timezone=America/Sao_Paulo&forecast_days=3";
    private String url = baseUrl;
    private LocationParam city;
    public View rootView;

    public WeatherController(View view, LocationParam city) {
        this.rootView = view;
        this.city = city;
    }

    public void request() {
        this.city = city;
        url = baseUrl + "&latitude=" + city.getLatitude() + "&longitude=" + city.getLongetude();

        List<Weather> result = new WeatherService(this).geCondition(city);

        if (!result.isEmpty()) {
            populateView(result);
        }
    }

    public void populateView(List<Weather> result) {
        DateTime cityDate = new DateTime(city.getDate());

        for (Weather weather : result) {
            NumberFormat nf = new DecimalFormat("#.##");
            DateTime exDate = new DateTime(weather.getDate());
            if (exDate.getDayOfMonth() == cityDate.getDayOfMonth()) {
                if (weather.getType().equals("day")) {
                    ((TextView) rootView.findViewById(R.id.weather_narrative)).setText(getContext().getString(R.string.weather_narrative, weather.getNarrative()));
                    ((TextView) rootView.findViewById(R.id.weather_wind)).setText(weather.getWindDir() + ", " + weather.getWindSpeed() + " km/h");
                    ((TextView) rootView.findViewById(R.id.weather_main)).setText(weather.getCondition());
                    ImageView imageView = rootView.findViewById(R.id.weather_ic);
                    imageView.setImageResource(getIcon(weather.getCondition()));
                    return;
                }
            }
        }

    }

    private int getIcon(String condition) {
        if(condition.toLowerCase().contains("ensolarado")){
            return R.drawable.weather_sunny;
        }

        if(condition.toLowerCase().equals("nublado") || condition.toLowerCase().equals("encoberto")){
            return R.drawable.weather_cloud;
        }

        if(condition.toLowerCase().contains("nublado")){
            return R.drawable.weather_partly_cloud;
        }

        if(condition.toLowerCase().contains("tempestade")){
            return R.drawable.weather_storm;
        }

        if(condition.toLowerCase().contains("chuva")) {
            return R.drawable.weather_rain;
        }

        return R.drawable.weather_partly_cloud;
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
        new WeatherService(this).cleanCondiction(city);
    }
}