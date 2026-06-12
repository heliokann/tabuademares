package com.novoideal.tabuademares;

import com.novoideal.tabuademares.model.LocationParam;

import org.junit.Test;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static org.junit.Assert.assertEquals;

public class MainActivitySelectionTest {

    private static LocationParam city(String name, boolean selected) {
        LocationParam c = new LocationParam(0, 0, name, 0, -22.0, -42.0);
        c.setSelected(selected);
        return c;
    }

    @Test
    public void selectedPositionOf_noSelection_returnsZeroFallback() {
        List<LocationParam> list = Arrays.asList(city("A", false), city("B", false));
        assertEquals(0, MainActivity.selectedPositionOf(list));
    }

    @Test
    public void selectedPositionOf_returnsIndexOfSelected() {
        List<LocationParam> list = Arrays.asList(city("A", false), city("B", true), city("C", false));
        assertEquals(1, MainActivity.selectedPositionOf(list));
    }

    @Test
    public void selectedPositionOf_nullList_returnsZero() {
        assertEquals(0, MainActivity.selectedPositionOf(null));
    }

    @Test
    public void selectedPositionOf_emptyList_returnsZero() {
        assertEquals(0, MainActivity.selectedPositionOf(Collections.<LocationParam>emptyList()));
    }

    @Test
    public void selectedPositionOf_firstSelectedWins() {
        List<LocationParam> list = Arrays.asList(city("A", true), city("B", true));
        assertEquals(0, MainActivity.selectedPositionOf(list));
    }
}
