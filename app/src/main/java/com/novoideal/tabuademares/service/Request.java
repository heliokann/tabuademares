package com.novoideal.tabuademares.service;

import android.content.Context;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.util.Xml;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.novoideal.tabuademares.R;

import org.joda.time.DateTime;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.android.volley.Request.Method;


/**
 * Created by Helio on 13/07/2017.
 */

public class Request {

    public static View rootView;
    private static Map<Integer, String> cache = new HashMap<>();
    ;

    public static void clearCache() {
        cache.clear();
    }

    private static void updateLabel(int elementID, String value) {
        cache.put(elementID, value);
        ((TextView) rootView.findViewById(elementID)).setText(value);
    }


    public static void windRequest(AppCompatActivity ctx) throws AuthFailureError {
        String url = "http://api.openweathermap.org/data/2.5/weather?APPID=774a44e054ada12ca9a45c2eabd53aa6&id=3468615";
        doRequest(url, ctx, R.id.wind);
    }

    public static void moonRequest(AppCompatActivity ctx) throws AuthFailureError {
        String url = "https://burningsoul-moon-v1.p.mashape.com";
        doRequest(url, ctx, R.id.moon_phase);
    }

    public static void extremesRequest(AppCompatActivity ctx) throws AuthFailureError {
        String url = "https://www.worldtides.info/api?extremes=&lat=-22.87944&lon=-42.018608&key=644e03a8-135d-4480-97ce-fef244faae28";
        doRequest(url, ctx, R.id.low_water);
        doRequest(url, ctx, R.id.hide_tide);
    }

    public static void updateWind(JSONObject response) {
        int wind = 0;
        try {
            wind = response.getJSONObject("wind").optInt("deg");
            Double speed = response.getJSONObject("wind").optDouble("speed");
            updateLabel(R.id.wind, rootView.getContext().getString(R.string.wind, wind, speed));
        } catch (JSONException e) {
            Toast.makeText(rootView.getContext(), "Deu ruim no wind: " + e.getMessage(), Toast.LENGTH_LONG).show();
        }
    }

    public static void updateMoon(JSONObject response) {
        String stage = response.optString("stage");
        if ("waning".equals(stage)) {
            stage = rootView.getContext().getString(R.string.waning);
        } else if ("waxing".equals(stage)) {
            stage = rootView.getContext().getString(R.string.waxing);
        }
        Double age = response.optDouble("age");
        updateLabel(R.id.moon_phase, rootView.getContext().getString(R.string.moon_phase, stage, age));
    }


    public static void updateExtremes(JSONObject response) {
        try {
            JSONArray extremes = response.getJSONArray("extremes");
            String low = "";
            String high = "";
            DateTime now = DateTime.now();
            NumberFormat nf = new DecimalFormat("#.##");
            for (int i = 0; i < extremes.length(); i++) {
                JSONObject extreme = extremes.getJSONObject(i);
                DateTime exDate = new DateTime(extreme.getString("date"));
                if (exDate.getDayOfMonth() == now.getDayOfMonth()) {
                    if (extreme.getString("type").equals("Low")) {
                        low += exDate.toString("HH:mm") + " (" + nf.format(extreme.getDouble("height") + 0.45) + "m)    ";
                    } else {
                        high += exDate.toString("HH:mm") + " (" + nf.format(extreme.getDouble("height") + 0.45) + "m)    ";
                    }
                }
            }
            updateLabel(R.id.low_water, rootView.getContext().getString(R.string.low_water, low));
            updateLabel(R.id.hide_tide, rootView.getContext().getString(R.string.hide_tide, high));
        } catch (JSONException e) {
            Toast.makeText(rootView.getContext(), "Deu ruim no extremes: " + e.getMessage(), Toast.LENGTH_LONG).show();
        }
    }


    public static void doRequest(final String url, final Context ctx, final int elementID) throws AuthFailureError {
        if (cache.containsKey(elementID)) {
            updateLabel(elementID, cache.get(elementID));
            return;
        }

        JsonObjectRequest jsObjRequest = new JsonObjectRequest
                (Method.GET, url, null, new Response.Listener<JSONObject>() {

                    @Override
                    public void onResponse(JSONObject response) {
//                       System.out.println("Deu bom: " + response);
                        switch (elementID) {
                            case R.id.moon_phase:
                                Request.updateMoon(response);
                                break;
                            case R.id.low_water:
                            case R.id.hide_tide:
                                Request.updateExtremes(response);
                                break;
                            case R.id.wind:
                                Request.updateWind(response);
                                break;
                            default:
                                Toast.makeText(ctx, "Deu ruim no request: " + url, Toast.LENGTH_LONG).show();
                        }
                    }
                }, new Response.ErrorListener() {

                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Toast.makeText(ctx, "Deu ruim no request: " + url + "\n " + error.getMessage(), Toast.LENGTH_LONG).show();
                    }

                }) {

            /**
             * Passing some request headers
             */
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                HashMap<String, String> headers = new HashMap<String, String>();
                headers.put("Content-Type", "application/json");
                headers.put("X-Mashape-Key", "TO9BQLIKVqmshVCQaA25CtETEuwtp1jjX2qjsnU32aifjyF3NI");
                return headers;
            }
        };

        RequestSender.getInstance(ctx).addToRequestQueue(jsObjRequest);
    }

//    new DownloadXmlTask().execute(URL);


}

// class DownloadXmlTask extends AsyncTask<String, Void, String> {
//    @Override
//    protected String doInBackground(String... urls) {
//        try {
//            return loadXmlFromNetwork(urls[0]);
//        } catch (IOException e) {
//            return e.getMessage();
//        } catch (XmlPullParserException e) {
//            return e.getMessage();
//        }
//    }
//
//    @Override
//    protected void onPostExecute(String result) {
//    }
//}