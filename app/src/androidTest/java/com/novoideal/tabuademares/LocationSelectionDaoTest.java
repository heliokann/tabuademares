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

/**
 * Verifica o ciclo salvar→selecionar→restaurar da cidade default.
 * Reproduz o defeito de origem: cidades do dataset embarcado chegam com id 0
 * e o "update ... where id=0" não marcava nenhuma linha como selecionada.
 */
@RunWith(AndroidJUnit4.class)
public class LocationSelectionDaoTest {

    private LocationParamDao dao;
    private LocationParamService service;

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

    private static LocationParam datasetCity(String name, double lat, double lng) {
        LocationParam c = new LocationParam(0, 0, name, 0, lat, lng);
        assertEquals("cidade do dataset deve chegar com id 0", 0, c.getId());
        return c;
    }

    private static long countSelected(List<LocationParam> all) {
        long count = 0;
        for (LocationParam c : all) {
            if (c.getSelected()) {
                count++;
            }
        }
        return count;
    }

    private static String selectedName(List<LocationParam> all) {
        for (LocationParam c : all) {
            if (c.getSelected()) {
                return c.getName();
            }
        }
        return null;
    }

    @Test
    public void saveAndSelect_marksRowWithRealId_notIdZero() {
        LocationParam persisted = service.saveAndSelect(datasetCity("Fortaleza - CE", -3.7319, -38.5267));

        assertNotNull(persisted);
        assertTrue("linha persistida deve ter id real (>0)", persisted.getId() > 0);

        List<LocationParam> all = service.geLocations();
        assertEquals("exatamente uma linha selecionada", 1, countSelected(all));
        assertEquals("Fortaleza - CE", selectedName(all));
    }

    @Test
    public void saveAndSelect_overwritesPreviousSelection() {
        service.saveAndSelect(datasetCity("Fortaleza - CE", -3.7319, -38.5267));
        service.saveAndSelect(datasetCity("Santos - SP", -23.9608, -46.3336));

        List<LocationParam> all = service.geLocations();
        assertEquals(2, all.size());
        assertEquals("apenas a seleção mais recente permanece", 1, countSelected(all));
        assertEquals("Santos - SP", selectedName(all));
    }

    @Test
    public void saveAndSelect_existingCity_resolvesIdAndSelects() {
        // Cidade já presente no banco (inserida antes, sem seleção).
        service.saveIfNew(datasetCity("Recife - PE", -8.0476, -34.877));

        LocationParam persisted = service.saveAndSelect(datasetCity("Recife - PE", -8.0476, -34.877));

        assertTrue("id real resolvido mesmo sem novo insert", persisted.getId() > 0);
        List<LocationParam> all = service.geLocations();
        assertEquals("não duplica a cidade", 1, all.size());
        assertEquals(1, countSelected(all));
        assertEquals("Recife - PE", selectedName(all));
    }

    @Test
    public void emptyDatabase_seedsDefaultCity() {
        assertTrue("primeiro acesso começa vazio", service.geLocations().isEmpty());

        service.saveIfNew(LocationParam.defaultCity.clone(0));

        List<LocationParam> all = service.geLocations();
        assertEquals(1, all.size());
        assertEquals("Cabo Frio", all.get(0).getName());
    }

    @Test
    public void findPersisted_returnsNullForUnknownCity() {
        assertNull(dao.findPersisted(datasetCity("Inexistente - XX", 1.0, 2.0)));
    }
}
