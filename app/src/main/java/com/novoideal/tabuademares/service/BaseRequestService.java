package com.novoideal.tabuademares.service;

import android.content.Context;
import android.util.Log;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.novoideal.tabuademares.util.RequestQueuer;

import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by Helio on 22/10/2017.
 */

public abstract class BaseRequestService {

    private Context context;

    public BaseRequestService(Context context) {
        if (context != null) {
            this.context = context;
        }
    }

    public Context getContext() {
        return context;
    }

    public abstract void callback(JSONObject response);

    public Map<String, String> getHeaders() {
        return new HashMap<>(0);
    }

    public void doRequest(final String url, final BaseRequestService service) {
        JsonObjectRequest jsObjRequest = new JsonObjectRequest
                (Request.Method.GET, url, null, new Response.Listener<JSONObject>() {

                    @Override
                    public void onResponse(JSONObject response) {
                        service.callback(response);
                    }
                }, new Response.ErrorListener() {

                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Log.e(service.getClass().getCanonicalName(), "Deu ruim no request: " + error);
                        Toast.makeText(service.getContext(), "Deu ruim no request: " + url + "\n " + error.getMessage(), Toast.LENGTH_LONG).show();
                    }

                }) {

            /**
             * Passing some request headers
             */
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                return service.getHeaders();
            }
        };

        RequestQueuer.getInstance(service.getContext()).addToRequestQueue(jsObjRequest);
    }
}
