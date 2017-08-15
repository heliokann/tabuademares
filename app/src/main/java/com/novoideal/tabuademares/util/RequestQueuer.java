package com.novoideal.tabuademares.util;

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
public class RequestQueuer {
    private static RequestQueuer mInstance;
    private RequestQueue mRequestQueue;
    private static Context mCtx;

    private RequestQueuer(Context context) {
        mCtx = context;
        mRequestQueue = getRequestQueue();
    }

    public static synchronized RequestQueuer getInstance(Context context) {
        if (mInstance == null) {
            mInstance = new RequestQueuer(context);
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