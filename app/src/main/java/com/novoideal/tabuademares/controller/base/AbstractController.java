package com.novoideal.tabuademares.controller.base;

import android.content.Context;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.novoideal.tabuademares.MainActivity;
import com.novoideal.tabuademares.R;
import com.novoideal.tabuademares.util.RequestQueuer;

import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by Helio on 14/08/2017.
 */

public abstract class AbstractController implements BaseController {

    private static Map<Integer, String> cache = new HashMap<>();
    public static View rootView = null;

    public AbstractController(View view) {
        if (view != null) {
            this.rootView = view;
        }
    }

    public static void clearCache() {
        cache.clear();
    }

    @Override
    public Context getContext() {
        if(rootView == null){
            return MainActivity.mainActivity;
        }
        return rootView.getContext();
    }

    @Override
    public View getRootView() {
        return rootView;
    }

    protected void updateLabel(int elementID, String value) {
        cache.put(elementID, value);
        ((TextView) getRootView().findViewById(elementID)).setText(value);
    }

    @Override
    public Map<String, String> getHeaders()  {
      return new HashMap<>(0);
    }

    public void doRequest(final String url, final int elementID, final BaseController controller){
        if (cache.containsKey(elementID)) {
            updateLabel(elementID, cache.get(elementID));
            return;
        }

        JsonObjectRequest jsObjRequest = new JsonObjectRequest
                (Request.Method.GET, url, null, new Response.Listener<JSONObject>() {

                    @Override
                    public void onResponse(JSONObject response) {
//                       System.out.println("Deu bom: " + response);
                        controller.callback(elementID, response);
                    }
                }, new Response.ErrorListener() {

                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Toast.makeText(controller.getContext(), "Deu ruim no request: " + url + "\n " + error.getMessage(), Toast.LENGTH_LONG).show();
                    }

                }) {

            /**
             * Passing some request headers
             */
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                return controller.getHeaders();
            }
        };

        RequestQueuer.getInstance(controller.getContext()).addToRequestQueue(jsObjRequest);
    }

}
