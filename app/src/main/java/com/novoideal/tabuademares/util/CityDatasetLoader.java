package com.novoideal.tabuademares.util;

import android.content.Context;

import com.novoideal.tabuademares.model.LocationParam;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class CityDatasetLoader {

    private static List<LocationParam> cache = null;

    public static synchronized List<LocationParam> load(Context context) {
        if (cache != null) {
            return cache;
        }
        cache = parse(context);
        return cache;
    }

    private static List<LocationParam> parse(Context context) {
        try {
            InputStream is = context.getAssets().open("cidades_litoraneas.json");
            byte[] buffer = new byte[is.available()];
            is.read(buffer);
            is.close();

            String json = new String(buffer, "UTF-8");
            JSONObject root = new JSONObject(json);
            JSONArray cities = root.getJSONArray("cities");

            List<LocationParam> result = new ArrayList<>(cities.length());
            for (int i = 0; i < cities.length(); i++) {
                JSONObject city = cities.getJSONObject(i);
                String name = city.getString("name");
                String state = city.getString("state");
                double lat = city.getDouble("lat");
                double lng = city.getDouble("lng");
                int cptecCode = city.optInt("cptecCode", 0);

                LocationParam lp = new LocationParam(cptecCode, 0, name + " - " + state, 0, lat, lng);
                result.add(lp);
            }
            return Collections.unmodifiableList(result);
        } catch (Exception e) {
            return Collections.emptyList();
        }
    }
}
