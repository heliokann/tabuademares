package com.novoideal.tabuademares;

import com.novoideal.tabuademares.model.LocationParam;

import org.joda.time.LocalDate;
import org.junit.Test;

import java.util.Date;

import static org.junit.Assert.*;

public class LocationParamTest {

    @Test
    public void defaultCity_hasExpectedValues() {
        LocationParam city = LocationParam.defaultCity;
        assertEquals("Cabo Frio", city.getName());
        assertEquals(Integer.valueOf(1059), city.getCodeSeaCondition());
        assertEquals(-22.87944, city.getLatitude(), 0.00001);
        assertEquals(-42.018608, city.getLongetude(), 0.00001);
    }

    @Test
    public void clone_preservesFields() {
        LocationParam original = new LocationParam(1059, 426480, "Cabo Frio", 0, -22.87944, -42.018608);
        original.setLatExtreme(-22.9);
        original.setLongExtreme(-42.1);
        original.setLatWeather(-22.8);
        original.setLongWeather(-42.0);

        LocationParam clone = original.clone(2);

        assertEquals(original.getName(), clone.getName());
        assertEquals(original.getCodeSeaCondition(), clone.getCodeSeaCondition());
        assertEquals(original.getWoeId(), clone.getWoeId());
        assertEquals(original.getLatitude(), clone.getLatitude());
        assertEquals(original.getLongetude(), clone.getLongetude());
        assertEquals(original.getLatExtreme(), clone.getLatExtreme());
        assertEquals(original.getLongExtreme(), clone.getLongExtreme());
        assertEquals(original.getLatWeather(), clone.getLatWeather());
        assertEquals(original.getLongWeather(), clone.getLongWeather());
        assertEquals(Integer.valueOf(2), clone.days());
    }

    @Test
    public void clone_withDayZero_matchesOriginalDate() {
        LocationParam city = new LocationParam(0, 0, "Florianópolis", 0, -27.5954, -48.548);
        LocationParam clone = city.clone(0);
        assertEquals(new LocalDate().toDate().toString(), clone.getDate().toString());
    }

    @Test
    public void setDays_updatesDateRelativeToToday() {
        LocationParam city = new LocationParam(0, 0, "Salvador", 0, -12.97, -38.51);
        city = city.clone(1);
        Date expected = new LocalDate().plusDays(1).toDate();
        assertEquals(expected.toString(), city.getDate().toString());
    }

    @Test
    public void toString_containsName() {
        LocationParam city = new LocationParam(0, 0, "Recife", 0, -8.04, -34.87);
        assertTrue(city.toString().startsWith("Recife"));
    }

    @Test
    public void getFullDateStr_hasExpectedFormat() {
        LocationParam city = LocationParam.defaultCity;
        String dateStr = city.getFullDateStr();
        assertTrue(dateStr.matches("\\d{4}-\\d{2}-\\d{2} \\d{2}:\\d{2}:\\d{2}\\.\\d+"));
    }

    @Test
    public void setCodeSeaCondition_updatesValue() {
        LocationParam city = new LocationParam(0, 0, "Natal", 0, -5.79, -35.21);
        assertEquals(Integer.valueOf(0), city.getCodeSeaCondition());
        city.setCodeSeaCondition(500);
        assertEquals(Integer.valueOf(500), city.getCodeSeaCondition());
    }

    @Test
    public void noArgConstructor_hasNullCodeSeaCondition() {
        LocationParam city = new LocationParam();
        assertNull(city.getCodeSeaCondition());
    }
}
