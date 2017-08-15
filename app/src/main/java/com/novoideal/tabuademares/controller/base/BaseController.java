package com.novoideal.tabuademares.controller.base;

import android.content.Context;
import android.view.View;

import com.novoideal.tabuademares.R;

import org.json.JSONObject;

import java.util.Map;

/**
 * Created by Helio on 14/08/2017.
 */

public interface BaseController {

    public void callback(int elementID, JSONObject response);

    public void request();

    public Map<String, String> getHeaders();

    public View getRootView();

    public Context getContext();

    public String getURL();

}
