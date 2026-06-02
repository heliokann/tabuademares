package com.novoideal.tabuademares.service;

import com.novoideal.tabuademares.model.ExtremeTide;
import com.novoideal.tabuademares.model.LocationParam;

import org.joda.time.LocalDate;
import org.junit.Test;

import java.util.List;

import static org.junit.Assert.*;

public class TabuadeMaresScraperServiceTest {

    private static final TabuadeMaresScraperService SERVICE = new TabuadeMaresScraperService();

    private static String buildHtml(String dateStr, String... tideCells) {
        StringBuilder cells = new StringBuilder();
        for (String cell : tideCells) cells.append(cell);
        return "<html><body><table>"
                + "<tr onclick=\"javascript:Day('" + dateStr + "');\">"
                + cells
                + "</tr>"
                + "</table></body></html>";
    }

    private static String highTideCell(String time, String height) {
        return "<td class=\"tabla_mareas_marea\">"
                + "<div class=\"tabla_mareas_marea_hora\">" + time + "</div>"
                + "<span class=\"tabla_mareas_marea_altura_numero\">" + height + "</span>"
                + "</td>";
    }

    private static String lowTideCell(String time, String height) {
        return "<td class=\"tabla_mareas_marea\">"
                + "<div class=\"tabla_mareas_marea_hora\">" + time + "</div>"
                + "<div class=\"tabla_mareas_marea_bajamar\"></div>"
                + "<span class=\"tabla_mareas_marea_altura_numero\">" + height + "</span>"
                + "</td>";
    }

    private LocationParam cityForToday() {
        LocationParam city = new LocationParam();
        city.setName("Cabo Frio");
        city.setTabuademaresPath("/br/rio-de-janeiro/cabo-frio");
        // days=0 → getDate() returns today
        return city;
    }

    // --- happy path ---

    @Test
    public void parse_fourExtremes_returnsAll() {
        String today = new LocalDate().toString("yyyy-MM-dd");
        String html = buildHtml(today,
                highTideCell("03:15", "1,1"),
                lowTideCell("09:40", "0,2"),
                highTideCell("15:50", "1,3"),
                lowTideCell("22:05", "0,1"));

        List<ExtremeTide> result = SERVICE.parseFromHtml(html, cityForToday());

        assertEquals(4, result.size());
    }

    @Test
    public void parse_highTide_typeIsHigh() {
        String today = new LocalDate().toString("yyyy-MM-dd");
        String html = buildHtml(today, highTideCell("06:00", "1,5"));

        List<ExtremeTide> result = SERVICE.parseFromHtml(html, cityForToday());

        assertFalse(result.get(0).isLow());
    }

    @Test
    public void parse_lowTide_typeIsLow() {
        String today = new LocalDate().toString("yyyy-MM-dd");
        String html = buildHtml(today, lowTideCell("12:30", "0,3"));

        List<ExtremeTide> result = SERVICE.parseFromHtml(html, cityForToday());

        assertTrue(result.get(0).isLow());
    }

    @Test
    public void parse_timeAndHeight_parsedCorrectly() {
        String today = new LocalDate().toString("yyyy-MM-dd");
        String html = buildHtml(today, highTideCell("14:45", "1,23"));

        List<ExtremeTide> result = SERVICE.parseFromHtml(html, cityForToday());

        ExtremeTide tide = result.get(0);
        assertEquals(14, tide.getHour());
        assertEquals(45, tide.getMinute());
        assertEquals(1.23, tide.getHeight(), 0.001);
    }

    @Test
    public void parse_heightWithCommaDecimal_parsedCorrectly() {
        String today = new LocalDate().toString("yyyy-MM-dd");
        String html = buildHtml(today, highTideCell("08:00", "2,05"));

        List<ExtremeTide> result = SERVICE.parseFromHtml(html, cityForToday());

        assertEquals(2.05, result.get(0).getHeight(), 0.001);
    }

    // --- error cases ---

    @Test
    public void parse_wrongDate_returnsEmpty() {
        String html = buildHtml("1990-01-01", highTideCell("06:00", "1,0"));

        List<ExtremeTide> result = SERVICE.parseFromHtml(html, cityForToday());

        assertTrue(result.isEmpty());
    }

    @Test
    public void parse_emptyHtml_returnsEmpty() {
        List<ExtremeTide> result = SERVICE.parseFromHtml("<html><body></body></html>", cityForToday());

        assertTrue(result.isEmpty());
    }

    @Test
    public void parse_missingHeightSpan_cellSkipped() {
        String today = new LocalDate().toString("yyyy-MM-dd");
        String badCell = "<td class=\"tabla_mareas_marea\">"
                + "<div class=\"tabla_mareas_marea_hora\">06:00</div>"
                + "</td>";
        String html = buildHtml(today, badCell, highTideCell("12:00", "1,0"));

        List<ExtremeTide> result = SERVICE.parseFromHtml(html, cityForToday());

        assertEquals(1, result.size());
        assertEquals(12, result.get(0).getHour());
    }

    @Test
    public void parse_missingHoraDiv_cellSkipped() {
        String today = new LocalDate().toString("yyyy-MM-dd");
        String badCell = "<td class=\"tabla_mareas_marea\">"
                + "<span class=\"tabla_mareas_marea_altura_numero\">1,0</span>"
                + "</td>";
        String html = buildHtml(today, badCell, highTideCell("18:00", "1,2"));

        List<ExtremeTide> result = SERVICE.parseFromHtml(html, cityForToday());

        assertEquals(1, result.size());
        assertEquals(18, result.get(0).getHour());
    }

    // --- edge cases ---

    @Test
    public void parse_nullPath_returnsEmptyWithoutCrash() throws Exception {
        LocationParam city = new LocationParam();
        city.setName("Sem cidade");
        city.setTabuademaresPath(null);

        List<ExtremeTide> result = SERVICE.scrape(city);

        assertTrue(result.isEmpty());
    }

    @Test
    public void parse_cityName_propagatedToExtreme() {
        String today = new LocalDate().toString("yyyy-MM-dd");
        String html = buildHtml(today, highTideCell("06:00", "1,0"));

        List<ExtremeTide> result = SERVICE.parseFromHtml(html, cityForToday());

        assertEquals("Cabo Frio", result.get(0).getCity());
    }
}
