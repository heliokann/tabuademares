package com.novoideal.tabuademares;

import android.content.Context;

import androidx.test.ext.junit.runners.AndroidJUnit4;
import androidx.test.platform.app.InstrumentationRegistry;

import com.novoideal.tabuademares.dao.LocationParamDao;
import com.novoideal.tabuademares.model.LocationParam;
import com.novoideal.tabuademares.service.LocationParamService;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.sql.SQLException;
import java.util.List;

import static org.junit.Assert.*;

@RunWith(AndroidJUnit4.class)
public class LocationParamServiceTest {

    private LocationParamService service;
    private LocationParamDao dao;

    @Before
    public void setUp() throws SQLException {
        Context context = InstrumentationRegistry.getInstrumentation().getTargetContext();
        dao = new LocationParamDao(context);
        dao.dropAndCreate();
        service = new LocationParamService(context);
    }

    @After
    public void tearDown() throws SQLException {
        dao.dropAndCreate();
        dao.close();
    }

    @Test
    public void saveIfNew_insertsCity() {
        LocationParam city = new LocationParam(0, 0, "Florianópolis - SC", 0, -27.5954, -48.548);
        service.saveIfNew(city);

        List<LocationParam> all = service.geLocations();
        assertEquals(1, all.size());
        assertEquals("Florianópolis - SC", all.get(0).getName());
    }

    @Test
    public void saveIfNew_doesNotDuplicate() {
        LocationParam city = new LocationParam(0, 0, "Salvador - BA", 0, -12.9711, -38.5108);
        service.saveIfNew(city);
        service.saveIfNew(city);

        List<LocationParam> all = service.geLocations();
        assertEquals(1, all.size());
    }

    @Test
    public void saveIfNew_multipleDifferentCities() {
        service.saveIfNew(new LocationParam(1059, 0, "Cabo Frio - RJ", 0, -22.87944, -42.018608));
        service.saveIfNew(new LocationParam(3464, 0, "Niterói - RJ", 0, -22.909309, -43.072231));
        service.saveIfNew(new LocationParam(0, 0, "Recife - PE", 0, -8.0476, -34.877));

        assertEquals(3, service.geLocations().size());
    }

    @Test
    public void geLocations_withDayClones_preservesDayOffset() {
        LocationParam city = new LocationParam(1059, 0, "Cabo Frio - RJ", 0, -22.87944, -42.018608);
        service.saveIfNew(city);

        List<LocationParam> base = service.geLocations();
        List<LocationParam> day2 = service.geLocations(base, 2);

        assertEquals(1, day2.size());
        assertEquals(Integer.valueOf(2), day2.get(0).days());
        assertEquals("Cabo Frio - RJ", day2.get(0).getName());
    }
}
