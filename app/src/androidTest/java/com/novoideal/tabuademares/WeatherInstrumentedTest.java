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
        ArgumentCaptor<List<Weather>> result= ArgumentCaptor.forClass(List.class);

        verify(controller).populateView(result.capture());

        assertEquals(14, result.getValue().size());

        DateTime dateTime = new DateTime("2017-11-06T07:00:00-0200");
        Weather weather1 = new Weather();
        weather1.setCity("PENSAR");
        weather1.setCondition("Encoberto");
        weather1.setNarrative("Muito nublado. Máxima de 23°C. Ventos SE de 15 a 30 km/h.");
        weather1.setTemperature(23);
        weather1.setWindDir("SE");
        weather1.setWindDegree(132);
        weather1.setWindSpeed(26);
        weather1.setLat(-22.89);
        weather1.setLon(-42.03);
        weather1.setType("day");
        weather1.setTime(dateTime.toDate());
        weather1.setDate(new LocalDate(dateTime).toDate());

        assertEquals(result.getValue().get(1), weather1);


//        verify(controller).populateView(argThat(result2 -> result2.size() == 8));

    }
}