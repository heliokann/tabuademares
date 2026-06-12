package com.novoideal.tabuademares;

import android.content.Context;
import android.widget.Filter;

import androidx.test.ext.junit.runners.AndroidJUnit4;
import androidx.test.platform.app.InstrumentationRegistry;

import com.novoideal.tabuademares.model.LocationParam;
import com.novoideal.tabuademares.ui.FilterableCityAdapter;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.Arrays;
import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

import static org.junit.Assert.*;

@RunWith(AndroidJUnit4.class)
public class FilterableCityAdapterTest {

    private Context context;
    private List<LocationParam> cities;
    private FilterableCityAdapter adapter;

    @Before
    public void setUp() throws InterruptedException {
        context = InstrumentationRegistry.getInstrumentation().getTargetContext();
        cities = Arrays.asList(
                new LocationParam(1059, 0, "Cabo Frio - RJ", 0, -22.87944, -42.018608),
                new LocationParam(3464, 0, "Niterói - RJ", 0, -22.909309, -43.072231),
                new LocationParam(0, 0, "Florianópolis - SC", 0, -27.5954, -48.548),
                new LocationParam(0, 0, "Salvador - BA", 0, -12.9711, -38.5108),
                new LocationParam(0, 0, "João Pessoa - PB", 0, -7.1195, -34.845)
        );
        final List<LocationParam> citiesFinal = cities;
        InstrumentationRegistry.getInstrumentation().runOnMainSync(
                () -> adapter = new FilterableCityAdapter(context, citiesFinal)
        );
    }

    @Test
    public void initialState_showsAllCities() {
        assertEquals(5, adapter.getCount());
    }

    @Test
    public void filter_byExactName_returnsMatch() throws InterruptedException {
        applyFilter("Niterói");
        assertEquals(1, adapter.getCount());
        assertEquals("Niterói - RJ", adapter.getItem(0).getName());
    }

    @Test
    public void filter_withoutAccent_findsAccentedCity() throws InterruptedException {
        applyFilter("florianopolis");
        assertEquals(1, adapter.getCount());
        assertTrue(adapter.getItem(0).getName().contains("Florianópolis"));
    }

    @Test
    public void filter_caseInsensitive_returnsMatch() throws InterruptedException {
        applyFilter("CABO FRIO");
        assertEquals(1, adapter.getCount());
        assertTrue(adapter.getItem(0).getName().contains("Cabo Frio"));
    }

    @Test
    public void filter_partialName_returnsMultipleMatches() throws InterruptedException {
        applyFilter("o");
        assertTrue("Deve retornar mais de um resultado para 'o'", adapter.getCount() > 1);
    }

    @Test
    public void filter_noMatch_returnsZero() throws InterruptedException {
        applyFilter("XxCidadeInexistenteXx");
        assertEquals(0, adapter.getCount());
    }

    @Test
    public void filter_emptyString_restoresAllCities() throws InterruptedException {
        applyFilter("cabo");
        assertEquals(1, adapter.getCount());
        applyFilter("");
        assertEquals(5, adapter.getCount());
    }

    @Test
    public void filter_joaoPessoa_withoutAccentAndTilde() throws InterruptedException {
        applyFilter("joao pessoa");
        assertEquals(1, adapter.getCount());
        assertTrue(adapter.getItem(0).getName().contains("João Pessoa"));
    }

    @Test
    public void filter_byState_rj_returnsBothRJCities() throws InterruptedException {
        applyFilter("RJ");
        assertEquals(2, adapter.getCount());
    }

    private void applyFilter(final String query) throws InterruptedException {
        final CountDownLatch latch = new CountDownLatch(1);
        InstrumentationRegistry.getInstrumentation().runOnMainSync(() ->
                adapter.getFilter().filter(query, count -> latch.countDown())
        );
        latch.await(3, TimeUnit.SECONDS);
    }
}
