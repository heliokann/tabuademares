package com.novoideal.tabuademares;

import com.novoideal.tabuademares.model.LocationParam;

import org.junit.Test;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

public class SkipReloadSameCityTest {

    private static LocationParam city(String name) {
        return new LocationParam(null, null, name, 0, -22.0, -42.0);
    }

    @Test
    public void isSameCity_sameName_returnsTrue() {
        assertTrue(MainActivity.isSameCity(city("Cabo Frio"), city("Cabo Frio")));
    }

    @Test
    public void isSameCity_differentName_returnsFalse() {
        assertFalse(MainActivity.isSameCity(city("Cabo Frio"), city("Angra dos Reis")));
    }

    @Test
    public void isSameCity_currentNull_returnsFalse() {
        assertFalse(MainActivity.isSameCity(null, city("Cabo Frio")));
    }

    @Test
    public void isSameCity_emptyNames_returnsTrue() {
        assertFalse(MainActivity.isSameCity(city(""), city("Cabo Frio")));
        assertTrue(MainActivity.isSameCity(city(""), city("")));
    }
}
