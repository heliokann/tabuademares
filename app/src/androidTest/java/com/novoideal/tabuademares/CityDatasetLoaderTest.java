package com.novoideal.tabuademares;

import android.content.Context;

import androidx.test.ext.junit.runners.AndroidJUnit4;
import androidx.test.platform.app.InstrumentationRegistry;

import com.novoideal.tabuademares.model.LocationParam;
import com.novoideal.tabuademares.util.CityDatasetLoader;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.List;

import static org.junit.Assert.*;

@RunWith(AndroidJUnit4.class)
public class CityDatasetLoaderTest {

    private Context context;

    @Before
    public void setUp() {
        context = InstrumentationRegistry.getInstrumentation().getTargetContext();
    }

    @Test
    public void load_returnsNonEmptyList() {
        List<LocationParam> cities = CityDatasetLoader.load(context);
        assertNotNull(cities);
        assertTrue("Dataset deve ter ao menos 50 cidades", cities.size() >= 50);
    }

    @Test
    public void load_containsCaboFrio() {
        List<LocationParam> cities = CityDatasetLoader.load(context);
        boolean found = false;
        for (LocationParam city : cities) {
            if (city.getName().contains("Cabo Frio")) {
                found = true;
                assertEquals(Integer.valueOf(1059), city.getCodeSeaCondition());
                assertEquals(-22.87944, city.getLatitude(), 0.001);
                break;
            }
        }
        assertTrue("Cabo Frio deve estar no dataset", found);
    }

    @Test
    public void load_containsNiteroi() {
        List<LocationParam> cities = CityDatasetLoader.load(context);
        boolean found = false;
        for (LocationParam city : cities) {
            if (city.getName().contains("Niterói")) {
                found = true;
                assertEquals(Integer.valueOf(3464), city.getCodeSeaCondition());
                break;
            }
        }
        assertTrue("Niterói deve estar no dataset", found);
    }

    @Test
    public void load_allCitiesHaveValidCoordinates() {
        List<LocationParam> cities = CityDatasetLoader.load(context);
        for (LocationParam city : cities) {
            assertNotNull("Cidade sem nome: " + city, city.getName());
            assertNotNull("Latitude nula: " + city.getName(), city.getLatitude());
            assertNotNull("Longitude nula: " + city.getName(), city.getLongetude());
            assertTrue("Latitude fora do Brasil: " + city.getName(),
                    city.getLatitude() >= -35.0 && city.getLatitude() <= 6.0);
            assertTrue("Longitude fora do Brasil: " + city.getName(),
                    city.getLongetude() >= -75.0 && city.getLongetude() <= -25.0);
        }
    }

    @Test
    public void load_returnsUnmodifiableList() {
        List<LocationParam> cities = CityDatasetLoader.load(context);
        try {
            cities.add(new LocationParam());
            fail("Lista deveria ser imutável");
        } catch (UnsupportedOperationException expected) {
            // correct
        }
    }

    @Test
    public void load_isCached_returnsSameInstance() {
        List<LocationParam> first = CityDatasetLoader.load(context);
        List<LocationParam> second = CityDatasetLoader.load(context);
        assertSame("Deve retornar a mesma instância em cache", first, second);
    }

    @Test
    public void load_allCitiesHaveDaysZero() {
        List<LocationParam> cities = CityDatasetLoader.load(context);
        for (LocationParam city : cities) {
            assertEquals("Cidades do dataset devem ter days=0: " + city.getName(),
                    Integer.valueOf(0), city.days());
        }
    }

    @Test
    public void load_citiesFromMultipleStates() {
        List<LocationParam> cities = CityDatasetLoader.load(context);
        boolean hasRJ = false, hasSC = false, hasBA = false, hasCE = false;
        for (LocationParam city : cities) {
            String name = city.getName();
            if (name.endsWith("- RJ")) hasRJ = true;
            if (name.endsWith("- SC")) hasSC = true;
            if (name.endsWith("- BA")) hasBA = true;
            if (name.endsWith("- CE")) hasCE = true;
        }
        assertTrue("Deve ter cidades de RJ", hasRJ);
        assertTrue("Deve ter cidades de SC", hasSC);
        assertTrue("Deve ter cidades de BA", hasBA);
        assertTrue("Deve ter cidades de CE", hasCE);
    }
}
