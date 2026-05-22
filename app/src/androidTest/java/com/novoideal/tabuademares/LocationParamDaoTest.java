package com.novoideal.tabuademares;

import android.content.Context;

import androidx.test.ext.junit.runners.AndroidJUnit4;
import androidx.test.platform.app.InstrumentationRegistry;

import com.novoideal.tabuademares.dao.LocationParamDao;
import com.novoideal.tabuademares.model.LocationParam;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.sql.SQLException;
import java.util.List;

import static org.junit.Assert.*;

@RunWith(AndroidJUnit4.class)
public class LocationParamDaoTest {

    private LocationParamDao dao;

    @Before
    public void setUp() throws SQLException {
        Context context = InstrumentationRegistry.getInstrumentation().getTargetContext();
        dao = new LocationParamDao(context);
        dao.dropAndCreate();
    }

    @After
    public void tearDown() throws SQLException {
        dao.dropAndCreate();
        dao.close();
    }

    @Test
    public void addNew_and_geLocationParams_returnsInsertedCity() {
        LocationParam city = new LocationParam(1059, 0, "Cabo Frio - RJ", 0, -22.87944, -42.018608);
        dao.addNew(city);

        List<LocationParam> result = dao.geLocationParams();
        assertEquals(1, result.size());
        assertEquals("Cabo Frio - RJ", result.get(0).getName());
    }

    @Test
    public void contains_returnsTrueForExistingCity() {
        LocationParam city = new LocationParam(1059, 0, "Cabo Frio - RJ", 0, -22.87944, -42.018608);
        dao.addNew(city);

        assertTrue(dao.contains(city));
    }

    @Test
    public void contains_returnsFalseForUnknownCity() {
        LocationParam city = new LocationParam(0, 0, "Florianópolis - SC", 0, -27.5954, -48.548);
        assertFalse(dao.contains(city));
    }

    @Test
    public void contains_doesNotThrowSQLException_longetudeTyopInColumn() {
        // Regression: column name is 'longetude' (typo in model) — must not crash
        LocationParam city = new LocationParam(0, 0, "Salvador - BA", 0, -12.9711, -38.5108);
        try {
            boolean result = dao.contains(city);
            assertFalse(result);
        } catch (Exception e) {
            fail("contains() lançou exceção inesperada: " + e.getMessage());
        }
    }

    @Test
    public void updateSeaConditionCode_updatesCorrectly() {
        LocationParam city = new LocationParam(0, 0, "Florianópolis - SC", 0, -27.5954, -48.548);
        dao.addNew(city);

        List<LocationParam> inserted = dao.geLocationParams(city);
        assertFalse(inserted.isEmpty());
        LocationParam saved = inserted.get(0);
        saved.setCodeSeaCondition(999);

        int rows = dao.updateSeaConditionCode(saved);
        assertEquals(1, rows);

        List<LocationParam> after = dao.geLocationParams(city);
        assertEquals(Integer.valueOf(999), after.get(0).getCodeSeaCondition());
    }

    @Test
    public void updateExtremeParams_updatesCorrectly() {
        LocationParam city = new LocationParam(1059, 0, "Cabo Frio - RJ", 0, -22.87944, -42.018608);
        dao.addNew(city);

        List<LocationParam> inserted = dao.geLocationParams(city);
        LocationParam saved = inserted.get(0);
        saved.setLatExtreme(-22.9);
        saved.setLongExtreme(-42.1);

        int rows = dao.updateExtremeParams(saved);
        assertEquals(1, rows);
    }

    @Test
    public void geLocationParams_byCity_filtersCorrectly() {
        LocationParam cabofrio = new LocationParam(1059, 0, "Cabo Frio - RJ", 0, -22.87944, -42.018608);
        LocationParam niteroi = new LocationParam(3464, 0, "Niterói - RJ", 0, -22.909309, -43.072231);
        dao.addNew(cabofrio);
        dao.addNew(niteroi);

        List<LocationParam> result = dao.geLocationParams(cabofrio);
        assertEquals(1, result.size());
        assertEquals("Cabo Frio - RJ", result.get(0).getName());
    }
}
