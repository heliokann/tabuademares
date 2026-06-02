package com.novoideal.tabuademares.service;

import com.novoideal.tabuademares.model.ExtremeTide;
import com.novoideal.tabuademares.model.LocationParam;

import android.util.Log;

import org.joda.time.DateTime;
import org.joda.time.LocalDate;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class TabuadeMaresScraperService {

    private static final String TAG = "TBMScraper";
    private static final String BASE_URL = "https://tabuademares.com";
    private static final int TIMEOUT_MS = 15000;

    // CSS selectors — update here if tabuademares.com changes its HTML structure
    static final String CSS_ROW_ONCLICK = "tr[onclick]";
    static final String CSS_TD_EXTREME  = "td.tabla_mareas_marea";
    static final String CSS_TD_EXTRA    = "td.tabla_mareas_marea_mas_cuatro";
    static final String CSS_DIV_HORA    = "div.tabla_mareas_marea_hora";
    static final String CSS_DIV_BAJAMAR = "div.tabla_mareas_marea_bajamar";
    static final String CSS_SPAN_HEIGHT = "span.tabla_mareas_marea_altura_numero";

    public List<ExtremeTide> scrape(LocationParam city) throws IOException {
        String path = city.getTabuademaresPath();
        if (path == null || path.isEmpty()) {
            return new ArrayList<>();
        }

        String url = BASE_URL + path;
        Log.d(TAG, "Fetching: " + url);
        Document doc = Jsoup.connect(url)
                .userAgent("Mozilla/5.0 (Linux; Android 10; Mobile) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/120.0.0.0 Mobile Safari/537.36")
                .header("Accept-Language", "pt-BR,pt;q=0.9,en;q=0.8")
                .timeout(TIMEOUT_MS)
                .get();

        List<ExtremeTide> result = parseDay(doc, city);
        Log.d(TAG, "Parsed " + result.size() + " extremes for " + city.getName());
        return result;
    }

    List<ExtremeTide> parseFromHtml(String html, LocationParam city) {
        return parseDay(Jsoup.parse(html), city);
    }

    private List<ExtremeTide> parseDay(Document doc, LocationParam city) {
        List<ExtremeTide> result = new ArrayList<>();
        LocalDate targetDate = new LocalDate(city.getDate());
        String dateStr = targetDate.toString("yyyy-MM-dd");

        Element mainRow = findDayRow(doc, dateStr);
        if (mainRow == null) {
            Log.w(TAG, "No row found for date " + dateStr + " (selector: " + CSS_ROW_ONCLICK + ")");
            return result;
        }

        Elements mainCells = mainRow.select(CSS_TD_EXTREME);
        Log.d(TAG, "Found " + mainCells.size() + " tide cells (selector: " + CSS_TD_EXTREME + ")");
        parseTideCells(mainCells, result, targetDate, city.getName());

        Element next = mainRow.nextElementSibling();
        if (next != null) {
            Elements extra = next.select(CSS_TD_EXTRA);
            if (!extra.isEmpty()) {
                Log.d(TAG, "Found " + extra.size() + " extra tide cells (selector: " + CSS_TD_EXTRA + ")");
                parseTideCells(extra, result, targetDate, city.getName());
            }
        }

        return result;
    }

    private Element findDayRow(Document doc, String dateStr) {
        // Primary: tr with onclick="Day('yyyy-MM-dd')"
        for (Element row : doc.select(CSS_ROW_ONCLICK)) {
            if (row.attr("onclick").contains("Day('" + dateStr + "')")) {
                return row;
            }
        }

        // Fallback: any element referencing the date, walk up to enclosing tr
        Log.w(TAG, "Primary selector found no row for " + dateStr + ", trying fallback");
        for (Element el : doc.select("[onclick*=" + dateStr + "], [href*=" + dateStr + "]")) {
            Element row = el.tagName().equals("tr") ? el : el.closest("tr");
            if (row != null) {
                Log.d(TAG, "Fallback found row for date " + dateStr);
                return row;
            }
        }

        return null;
    }

    private void parseTideCells(Elements tds, List<ExtremeTide> result, LocalDate date, String cityName) {
        for (Element td : tds) {
            Element horaDiv = td.selectFirst(CSS_DIV_HORA);
            if (horaDiv == null) {
                Log.w(TAG, "Missing selector: " + CSS_DIV_HORA);
                continue;
            }

            String timeStr = horaDiv.text().trim();
            String[] parts = timeStr.split(":");
            if (parts.length < 2) {
                Log.w(TAG, "Unexpected time format: '" + timeStr + "'");
                continue;
            }

            int hour, minute;
            try {
                hour   = Integer.parseInt(parts[0].trim());
                minute = Integer.parseInt(parts[1].trim());
            } catch (NumberFormatException e) {
                Log.w(TAG, "Could not parse time: '" + timeStr + "'");
                continue;
            }

            boolean isLow = td.selectFirst(CSS_DIV_BAJAMAR) != null;

            Element heightSpan = td.selectFirst(CSS_SPAN_HEIGHT);
            if (heightSpan == null) {
                Log.w(TAG, "Missing selector: " + CSS_SPAN_HEIGHT);
                continue;
            }
            double height;
            try {
                height = Double.parseDouble(heightSpan.text().replace(",", ".").trim());
            } catch (NumberFormatException e) {
                Log.w(TAG, "Could not parse height: '" + heightSpan.text() + "'");
                continue;
            }

            DateTime fullDate = date.toDateTimeAtStartOfDay()
                    .withHourOfDay(hour)
                    .withMinuteOfHour(minute);

            ExtremeTide extreme = new ExtremeTide();
            extreme.setCity(cityName);
            extreme.setDate(date.toDate());
            extreme.setFullDate(fullDate.toDate());
            extreme.setHour(hour);
            extreme.setMinute(minute);
            extreme.setType(isLow ? "Low" : "High");
            extreme.setHeight(height);
            extreme.setLat(0.0);
            extreme.setLon(0.0);

            result.add(extreme);
        }
    }
}
