package com.novoideal.tabuademares.controller;

import android.os.AsyncTask;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.novoideal.tabuademares.R;
import com.novoideal.tabuademares.controller.base.AbstractController;
import com.novoideal.tabuademares.controller.base.BaseController;
import com.novoideal.tabuademares.model.Weather;
import com.novoideal.tabuademares.service.XmlWeatherService;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by Helio on 14/08/2017.
 */

public class SwellController {

    private String url = "http://servicos.cptec.inpe.br/XML/cidade/1059/dia/0/ondas.xml";

    private static Map<String, Weather> cacheWeater = new HashMap<>();
    public static View rootView = null;

    public static void clearCache() {
        cacheWeater.clear();
    }

    public SwellController(View view) {
        if (view != null) {
            this.rootView = view;
        }
    }

    protected void updateLabel(int elementID, String value) {
        ((TextView) rootView.findViewById(elementID)).setText(value);
    }

    public void updateWeather(String period, int agitation, int swell, int wind) {
        Weather w = cacheWeater.get(period);
        updateLabel(agitation, w.getAgitation());
        updateLabel(swell, w.getSewll() + ", " + w.getHeight() + "m");
        updateLabel(wind, w.getWind_dir() + ", " + w.getWind() + " nós");
    }

    private void updateFromCache() {
        updateWeather("manha", R.id.a_m, R.id.s_m, R.id.w_m);
        updateWeather("tarde", R.id.a_t, R.id.s_t, R.id.w_t);
        updateWeather("noite", R.id.a_n, R.id.s_n, R.id.w_n);
    }


    public void callback(List<Weather> result) {
        if (cacheWeater.isEmpty()) {
            for (Weather weater : result) {
                cacheWeater.put(weater.getPeriod(), weater);
            }
        }

        updateFromCache();
    }

    public void request() {
        if(hasCache()){
            updateFromCache();
            return;
        }

        new DownloadXmlTask().execute(this);
    }

    public String getURL() {
        return url;
    }

    public boolean hasCache() {
        return !cacheWeater.isEmpty();
    }

}

class DownloadXmlTask extends AsyncTask<SwellController, Void, List<Weather>> {

    private SwellController controller;

    @Override
    protected List<Weather> doInBackground(SwellController... controllers) {
        this.controller = controllers[0];
        try {
            return new XmlWeatherService().getWeathers(controller.getURL());
        } catch (Exception e) {
        }
        return null;
    }

    @Override
    protected void onPostExecute(List<Weather> result) {
        controller.callback(result);
    }
}
