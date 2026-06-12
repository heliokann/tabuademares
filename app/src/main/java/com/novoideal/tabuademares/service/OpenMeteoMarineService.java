package com.novoideal.tabuademares.service;

import com.novoideal.tabuademares.model.LocationParam;
import com.novoideal.tabuademares.model.SeaCondition;

import org.joda.time.LocalDate;
import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

public class OpenMeteoMarineService {

    private static final String MARINE_URL =
            "https://marine-api.open-meteo.com/v1/marine";
    private static final String WEATHER_URL =
            "https://api.open-meteo.com/v1/forecast";

    private static final int[] PERIOD_HOURS = {6, 12, 18};
    private static final String[] PERIOD_NAMES = {"manha", "tarde", "noite"};

    public List<SeaCondition> getConditions(LocationParam city) throws Exception {
        String latLon = "latitude=" + city.getLatitude() + "&longitude=" + city.getLongetude();
        String suffix = "&timezone=America/Sao_Paulo&forecast_days=3";

        JSONObject marine = fetchJson(MARINE_URL + "?" + latLon
                + "&hourly=wave_height,swell_wave_height,swell_wave_direction" + suffix);
        JSONObject atmo = fetchJson(WEATHER_URL + "?" + latLon
                + "&hourly=windspeed_10m,winddirection_10m" + suffix);

        return parse(marine, atmo, city);
    }

    private List<SeaCondition> parse(JSONObject marine, JSONObject atmo, LocationParam city)
            throws Exception {
        List<SeaCondition> result = new ArrayList<>();

        JSONObject mh = marine.getJSONObject("hourly");
        JSONArray times = mh.getJSONArray("time");
        JSONArray waveH = mh.getJSONArray("wave_height");
        JSONArray swellH = mh.getJSONArray("swell_wave_height");
        JSONArray swellD = mh.getJSONArray("swell_wave_direction");

        JSONObject ah = atmo.getJSONObject("hourly");
        JSONArray windSpeed = ah.getJSONArray("windspeed_10m");
        JSONArray windDir = ah.getJSONArray("winddirection_10m");

        LocalDate targetDate = new LocalDate(city.getDate());

        for (int p = 0; p < PERIOD_HOURS.length; p++) {
            int targetHour = PERIOD_HOURS[p];

            for (int i = 0; i < times.length(); i++) {
                String t = times.getString(i); // "2026-05-22T06:00"
                LocalDate d = LocalDate.parse(t.substring(0, 10));
                int h = Integer.parseInt(t.substring(11, 13));

                if (d.equals(targetDate) && h == targetHour) {
                    double wH = waveH.optDouble(i, 0.5);
                    double sH = swellH.optDouble(i, 0.0);
                    int sD = swellD.optInt(i, 0);
                    double wSpd = windSpeed.optDouble(i, 0.0);
                    int wD = windDir.optInt(i, 0);

                    SeaCondition sc = new SeaCondition();
                    sc.setPeriod(PERIOD_NAMES[p]);
                    sc.setDate(targetDate.toDate());
                    sc.setCity(city.getName());
                    sc.setAgitation(heightToAgitation(wH));
                    sc.setHeight(Math.round(sH * 10.0) / 10.0);
                    sc.setSewll(degreesToCompass(sD));
                    sc.setWind(Math.round(wSpd * 0.54 * 10.0) / 10.0); // km/h → nós
                    sc.setWind_dir(degreesToCompass(wD));

                    result.add(sc);
                    break;
                }
            }
        }

        return result;
    }

    private static String heightToAgitation(double height) {
        if (height < 0.5) return "Calmo";
        if (height < 1.0) return "Fraco";
        if (height < 2.0) return "Moderado";
        if (height < 3.0) return "Forte";
        return "Muito Forte";
    }

    private static String degreesToCompass(int degrees) {
        String[] dirs = {"N", "NE", "E", "SE", "S", "SW", "W", "NW"};
        return dirs[(int) Math.round(((double) (degrees % 360)) / 45) % 8];
    }

    private static JSONObject fetchJson(String urlStr) throws Exception {
        URL url = new URL(urlStr);
        HttpURLConnection conn = (HttpURLConnection) url.openConnection();
        conn.setReadTimeout(10000);
        conn.setConnectTimeout(15000);
        conn.setRequestMethod("GET");
        conn.connect();

        InputStream stream = conn.getInputStream();
        BufferedReader buffer = new BufferedReader(new InputStreamReader(stream, "UTF-8"));
        StringBuilder sb = new StringBuilder();
        String line;
        while ((line = buffer.readLine()) != null) sb.append(line);
        stream.close();

        return new JSONObject(sb.toString());
    }
}
