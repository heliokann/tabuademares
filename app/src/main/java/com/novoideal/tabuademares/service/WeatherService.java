package com.novoideal.tabuademares.service;

import android.widget.Toast;

import com.novoideal.tabuademares.controller.WeatherController;
import com.novoideal.tabuademares.dao.LocationParamDao;
import com.novoideal.tabuademares.dao.WeatherDao;
import com.novoideal.tabuademares.model.LocationParam;
import com.novoideal.tabuademares.model.Weather;

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

public class WeatherService extends BaseRequestService{

    private WeatherDao weatherDao;
    private LocationParamDao locationParamDao;
    private WeatherController controller;

    public WeatherService(WeatherDao weatherDao, LocationParamDao locationParamDao, WeatherController controller) {
        super(controller.getContext());
        this.weatherDao = weatherDao;
        this.locationParamDao = locationParamDao;
        this.controller = controller;
    }


    public WeatherService(WeatherController controller) {
        super(controller.getContext());
        weatherDao = new WeatherDao(this.getContext());
        this.locationParamDao = new LocationParamDao(this.getContext());
        this.controller = controller;
    }

    @Override
    public void callback(JSONObject response) {
        List<Weather> weathers = new ArrayList<>();
        try {
            LocationParam city = controller.getCity();
            String[] latLong = response.getString("id").split(",");
            Double lat = Double.parseDouble((latLong[0]));
            Double lon = Double.parseDouble((latLong[1]));

            city.setLatWeather(lat);
            city.setLongWeather(lon);
            locationParamDao.updateWeatherParams(city);

            JSONObject vt1dailyforecast = response.getJSONObject("vt1dailyforecast");
            JSONObject day = vt1dailyforecast.getJSONObject("day");
            JSONArray windSpeed = day.getJSONArray("windSpeed");
            JSONArray windDirDegrees = day.getJSONArray("windDirDegrees");
            JSONArray windDirCompass = day.getJSONArray("windDirCompass");
            JSONArray validDate = vt1dailyforecast.getJSONArray("validDate");
            JSONArray temperature = day.getJSONArray("temperature");
            JSONArray narrative = day.getJSONArray("narrative");
            JSONArray phrase = day.getJSONArray("phrase");

            for (int i = 1; i < validDate.length(); i++) {
                DateTime exDate = new DateTime(validDate.getString(i));
                Weather weather = new Weather();
                // TODO pensar em uma forma melhor
                weather.setCity("PENSAR");
                weather.setLat(lat);
                weather.setLon(lon);
                weather.setTemperature(temperature.getInt(i));
                weather.setNarrative(narrative.getString(i));
                weather.setWindDegree(windDirDegrees.getInt(i));
                weather.setWindSpeed(windSpeed.getInt(i));
                weather.setWindDir(windDirCompass.getString(i));
                weather.setDate(new LocalDate(exDate).toDate());
                weather.setTime(exDate.toDate());
                weather.setType("day");
                weather.setCondition(phrase.getString(i));


                weathers.add(weather);
            }
        } catch (JSONException e) {
            Toast.makeText(getContext(), "Problema ao acessar weather: " + e.getMessage(), Toast.LENGTH_LONG).show();
        }

        saveSeaCondiction(weathers);

        controller.populateView(weathers);
    }

    @Override
    public void cleanCondiction(LocationParam city) {
        weatherDao.clearCondiction(city);
    }

    public List<Weather> geCondition(LocationParam city) {
        List<Weather> conditions =  weatherDao.geCondition(city);
        if(conditions != null && !conditions.isEmpty()){
            return conditions;
        }

        doRequest(controller.getURL(), this);

        return conditions;


    }

    private void saveSeaCondiction(List<Weather> conditions) {
        for (Weather condition: conditions) {
            if (!weatherDao.contains(condition)) {
                weatherDao.addNew(condition);
            }
        }
    }


}
