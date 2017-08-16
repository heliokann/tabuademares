package com.novoideal.tabuademares.controller;

import android.os.AsyncTask;
import android.view.View;
import android.widget.Toast;

import com.novoideal.tabuademares.R;
import com.novoideal.tabuademares.controller.base.AbstractController;
import com.novoideal.tabuademares.controller.base.BaseController;
import com.novoideal.tabuademares.model.Weather;
import com.novoideal.tabuademares.service.XmlWeatherService;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.List;

/**
 * Created by Helio on 14/08/2017.
 */

public class SwellController extends AbstractController implements BaseController {

    private String url = "http://servicos.cptec.inpe.br/XML/cidade/1059/dia/0/ondas.xml";

    public SwellController(View view) {
        super(view);
    }

    @Override
    public void callback(int elementID, JSONObject response) {

    }

    @Override
    public void request() {
        new DownloadXmlTask().execute(this);
    }

    @Override
    public String getURL() {
        return url;
    }

    @Override
    public void doRequest(String url, int elementID, BaseController controller) {
        super.doRequest(url, elementID, controller);
    }
}

class DownloadXmlTask extends AsyncTask<BaseController, Void, List<Weather>> {

    @Override
    protected List<Weather> doInBackground(BaseController... controller) {
        try {
            return new XmlWeatherService().getWeathers(controller[0].getURL());
        } catch (Exception e) {
        }
        return null;
    }

    @Override
    protected void onPostExecute(List<Weather> result) {
    }
}
