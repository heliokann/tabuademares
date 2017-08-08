package com.novoideal.tabuademares.service;

/**
 * Created by Helio on 03/08/2017.
 */


import android.content.Context;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.Volley;

/**
 * Created by genji on 2/12/16 ... the same as android developers
 */
public class RequestSender {
    private static RequestSender mInstance;
    private RequestQueue mRequestQueue;
    private static Context mCtx;

    private RequestSender(Context context) {
        mCtx = context;
        mRequestQueue = getRequestQueue();
    }

    public static synchronized RequestSender getInstance(Context context) {
        if (mInstance == null) {
            mInstance = new RequestSender(context);
        }
        return mInstance;
    }

    public RequestQueue getRequestQueue() {
        if (mRequestQueue == null) {
            // getApplicationContext() is key, it keeps you from leaking the
            // Activity or BroadcastReceiver if someone passes one in.
            mRequestQueue = Volley.newRequestQueue(mCtx.getApplicationContext());
        }
        return mRequestQueue;
    }

    public <T> void addToRequestQueue(Request<T> req) {
        getRequestQueue().add(req);
    }
}