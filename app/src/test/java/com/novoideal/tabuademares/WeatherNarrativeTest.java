package com.novoideal.tabuademares;

import com.novoideal.tabuademares.service.WeatherService;

import org.junit.Test;

import static org.junit.Assert.assertTrue;

public class WeatherNarrativeTest {

    @Test
    public void narrative_containsMaxTemperature() {
        String n = WeatherService.buildNarrative("Céu limpo.", 32, 22, "NE", 15);
        assertTrue(n.contains("Máxima: 32°C"));
    }

    @Test
    public void narrative_containsMinTemperature() {
        String n = WeatherService.buildNarrative("Céu limpo.", 32, 22, "NE", 15);
        assertTrue(n.contains("Mínima: 22°C"));
    }

    @Test
    public void narrative_minAppearsAfterMax() {
        String n = WeatherService.buildNarrative("Céu limpo.", 32, 22, "NE", 15);
        assertTrue(n.indexOf("Máxima") < n.indexOf("Mínima"));
    }

    @Test
    public void narrative_containsWindInfo() {
        String n = WeatherService.buildNarrative("Céu limpo.", 32, 22, "SW", 20);
        assertTrue(n.contains("Vento SW a 20 km/h"));
    }

    @Test
    public void narrative_startsWithCondition() {
        String n = WeatherService.buildNarrative("Chuva leve.", 28, 20, "S", 10);
        assertTrue(n.startsWith("Chuva leve."));
    }

    @Test
    public void narrative_equalMinMax_doesNotCrash() {
        String n = WeatherService.buildNarrative("Nublado.", 25, 25, "N", 5);
        assertTrue(n.contains("Máxima: 25°C"));
        assertTrue(n.contains("Mínima: 25°C"));
    }

    @Test
    public void narrative_containsBothTemperatures() {
        String n = WeatherService.buildNarrative("Ensolarado.", 35, 24, "NE", 18);
        assertTrue(n.contains("Máxima: 35°C"));
        assertTrue(n.contains("Mínima: 24°C"));
    }
}
