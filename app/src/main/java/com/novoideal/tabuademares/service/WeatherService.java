package com.novoideal.tabuademares.service;

import android.widget.Toast;

import com.novoideal.tabuademares.controller.WeatherController;
import com.novoideal.tabuademares.dao.LocationParamDao;
import com.novoideal.tabuademares.dao.WeatherDao;
import com.novoideal.tabuademares.model.LocationParam;
import com.novoideal.tabuademares.model.Weather;

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
            double lat = response.getDouble("latitude");
            double lon = response.getDouble("longitude");

            city.setLatWeather(lat);
            city.setLongWeather(lon);
            locationParamDao.updateWeatherParams(city);

            JSONObject daily = response.getJSONObject("daily");
            JSONArray times = daily.getJSONArray("time");
            JSONArray codes = daily.getJSONArray("weathercode");
            JSONArray temperatures = daily.getJSONArray("temperature_2m_max");
            JSONArray windSpeeds = daily.getJSONArray("windspeed_10m_max");
            JSONArray windDirs = daily.getJSONArray("winddirection_10m_dominant");

            LocalDate nowDate = new LocalDate();

            for (int i = 0; i < times.length(); i++) {
                LocalDate localDate = LocalDate.parse(times.getString(i));
                int code = codes.getInt(i);
                int temp = (int) temperatures.getDouble(i);
                int speed = (int) Math.round(windSpeeds.getDouble(i));
                int degree = windDirs.getInt(i);
                String dir = degreesToCompass(degree);
                String[] parts = wmoCondition(code);

                Weather weather = new Weather();
                weather.setCity("--");
                weather.setLat(lat);
                weather.setLon(lon);
                weather.setTemperature(temp);
                weather.setWindSpeed(speed);
                weather.setWindDegree(degree);
                weather.setWindDir(dir);
                weather.setCondition(parts[0]);
                weather.setNarrative(parts[1] + " Máxima: " + temp + "°C. Vento " + dir + " a " + speed + " km/h.");
                weather.setType("day");
                weather.setDate(localDate.toDate());
                weather.setTime(localDate.toDateTimeAtStartOfDay().toDate());

                if (nowDate.getDayOfMonth() == localDate.getDayOfMonth()) {
                    locationParamDao.touch(city);
                }
                weathers.add(weather);
            }
        } catch (JSONException e) {
            Toast.makeText(getContext(), "Problema ao acessar weather: " + e.getMessage(), Toast.LENGTH_LONG).show();
        }

        saveSeaCondiction(weathers);
        controller.populateView(weathers);
    }

    private static String[] wmoCondition(int code) {
        if (code == 0) return new String[]{"Ensolarado", "Céu limpo e ensolarado."};
        if (code == 1) return new String[]{"Ensolarado", "Predominantemente céu limpo."};
        if (code == 2) return new String[]{"Parcialmente nublado", "Céu parcialmente nublado."};
        if (code == 3) return new String[]{"Nublado", "Céu nublado."};
        if (code == 45 || code == 48) return new String[]{"Nublado", "Nevoeiro."};
        if (code == 51) return new String[]{"Parcialmente nublado", "Chuvisco leve."};
        if (code == 53) return new String[]{"Parcialmente nublado", "Chuvisco moderado."};
        if (code == 55) return new String[]{"Nublado", "Chuvisco intenso."};
        if (code == 61) return new String[]{"Chuva leve", "Chuva leve."};
        if (code == 63) return new String[]{"Chuva", "Chuva moderada."};
        if (code == 65) return new String[]{"Chuva", "Chuva intensa."};
        if (code >= 71 && code <= 77) return new String[]{"Nublado", "Precipitação sólida."};
        if (code == 80) return new String[]{"Chuva leve", "Pancadas de chuva leve."};
        if (code == 81) return new String[]{"Chuva", "Pancadas de chuva."};
        if (code == 82) return new String[]{"Chuva", "Pancadas de chuva fortes."};
        if (code >= 85 && code <= 86) return new String[]{"Nublado", "Neve com rajadas."};
        if (code == 95) return new String[]{"Tempestade", "Tempestade com trovões."};
        if (code == 96 || code == 99) return new String[]{"Tempestade", "Tempestade com granizo."};
        return new String[]{"Parcialmente nublado", "Condição não disponível."};
    }

    private static String degreesToCompass(int degrees) {
        String[] dirs = {"N", "NE", "E", "SE", "S", "SW", "W", "NW"};
        return dirs[(int) Math.round(((double) (degrees % 360)) / 45) % 8];
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
