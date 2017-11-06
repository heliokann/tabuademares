package com.novoideal.tabuademares;

import android.support.test.InstrumentationRegistry;
import android.support.test.runner.AndroidJUnit4;

import com.novoideal.tabuademares.controller.WeatherController;
import com.novoideal.tabuademares.dao.WeatherDao;
import com.novoideal.tabuademares.model.CityCondition;
import com.novoideal.tabuademares.model.Weather;
import com.novoideal.tabuademares.service.WeatherService;
import com.novoideal.tabuademares.test.R;

import org.apache.commons.io.IOUtils;
import org.json.JSONObject;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.io.InputStream;
import java.nio.charset.Charset;
import java.util.ArrayList;

import static org.mockito.Matchers.any;
import static org.mockito.Mockito.when;


//@RunWith(MockitoJUnitRunner.class)
@RunWith(AndroidJUnit4.class)
public class WeatherInstrumentedTest {

    @Mock
    WeatherController controller;

    @Mock
    WeatherDao weatherDao;

    WeatherService weatherService;


    @Before
    public void setup() {
        MockitoAnnotations.initMocks(this);

        when(controller.getURL()).thenReturn("https://api.weather.com/v2/turbo/vt1dailyforecast?apiKey=d522aa97197fd864d36b418f39ebb323&format=json&language=pt-BR&units=m&geocode=-22.89%2C-42.03");
        when(weatherDao.geCondition((CityCondition) any())).thenReturn(new ArrayList<Weather>());
        when(controller.getCity()).thenReturn(CityCondition.defaultCity);

        when(controller.getContext()).thenReturn(InstrumentationRegistry.getTargetContext());

        weatherService = new WeatherService(weatherDao, controller);
    }

    @Test
    public void weatherAppContext() throws Exception {
        InputStream is = InstrumentationRegistry.getContext().getResources().openRawResource(R.raw.cabo_frio_weather);
        JSONObject response = new JSONObject(IOUtils.toString(is, Charset.forName("UTF-8")));

        weatherService.callback(response);
    }
}