package com.novoideal.tabuademares;

import androidx.test.ext.junit.runners.AndroidJUnit4;
import androidx.test.platform.app.InstrumentationRegistry;

import com.novoideal.tabuademares.controller.WeatherController;
import com.novoideal.tabuademares.dao.LocationParamDao;
import com.novoideal.tabuademares.dao.WeatherDao;
import com.novoideal.tabuademares.model.LocationParam;
import com.novoideal.tabuademares.model.Weather;
import com.novoideal.tabuademares.service.WeatherService;
import com.novoideal.tabuademares.test.R;

import org.apache.commons.io.IOUtils;
import org.joda.time.DateTime;
import org.joda.time.LocalDate;
import org.json.JSONObject;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.io.InputStream;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith(AndroidJUnit4.class)
public class WeatherInstrumentedTest {

    @Mock WeatherController controller;
    @Mock WeatherDao weatherDao;
    @Mock LocationParamDao locationParamDao;

    WeatherService weatherService;

    @Before
    public void setup() {
        MockitoAnnotations.initMocks(this);

        when(controller.getURL()).thenReturn("https://api.weather.com/v2/turbo/vt1dailyforecast?apiKey=d522aa97197fd864d36b418f39ebb323&format=json&language=pt-BR&units=m&geocode=-22.89%2C-42.03");
        when(weatherDao.geCondition((LocationParam) any())).thenReturn(new ArrayList<Weather>());
        when(controller.getCity()).thenReturn(LocationParam.defaultCity);
        when(controller.getContext()).thenReturn(InstrumentationRegistry.getInstrumentation().getTargetContext());

        weatherService = new WeatherService(weatherDao, locationParamDao, controller);
    }

    @Test
    public void weatherCallback_parsesCorrectNumberOfItems() throws Exception {
        InputStream is = InstrumentationRegistry.getInstrumentation().getContext()
                .getResources().openRawResource(R.raw.cabo_frio_weather);
        JSONObject response = new JSONObject(IOUtils.toString(is, Charset.forName("UTF-8")));

        weatherService.callback(response);
        ArgumentCaptor<List<Weather>> result = ArgumentCaptor.forClass(List.class);
        verify(controller).populateView(result.capture());

        assertEquals(14, result.getValue().size());
    }

    @Test
    public void weatherCallback_firstItemHasCorrectValues() throws Exception {
        InputStream is = InstrumentationRegistry.getInstrumentation().getContext()
                .getResources().openRawResource(R.raw.cabo_frio_weather);
        JSONObject response = new JSONObject(IOUtils.toString(is, Charset.forName("UTF-8")));

        weatherService.callback(response);
        ArgumentCaptor<List<Weather>> result = ArgumentCaptor.forClass(List.class);
        verify(controller).populateView(result.capture());

        Weather w = result.getValue().get(0);
        DateTime dateTime = new DateTime("2017-11-06T07:00:00-0200");

        assertEquals("PENSAR", w.getCity());
        assertEquals("AM nublado/ PM sol", w.getCondition());
        assertEquals("Nublado de manhã, seguido de sol à tarde. Máxima de 24°C. Ventos S de 15 a 30 km/h.", w.getNarrative());
        assertEquals(24, w.getTemperature());
        assertEquals("S", w.getWindDir());
        assertEquals(171, w.getWindDegree());
        assertEquals(24, w.getWindSpeed());
        assertEquals(-22.89, w.getLat(), 0.001);
        assertEquals(-42.03, w.getLon(), 0.001);
        assertEquals("day", w.getType());
        assertEquals(new LocalDate(dateTime).toDate(), w.getDate());
    }
}
