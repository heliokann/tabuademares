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

import java.util.ArrayList;
import java.util.List;

public class TabuadeMaresScraperService {

    private static final String TAG = "TBMScraper";
    private static final String BASE_URL = "https://tabuademares.com";

    public List<ExtremeTide> scrape(LocationParam city) throws Exception {
        String path = city.getTabuademaresPath();
        if (path == null || path.isEmpty()) {
            return new ArrayList<>();
        }

        String url = BASE_URL + path;
        Log.d(TAG, "Fetching: " + url);
        Document doc = Jsoup.connect(url)
                .userAgent("Mozilla/5.0 (Android; Mobile)")
                .timeout(15000)
                .get();

        List<ExtremeTide> result = parseDay(doc, city);
        Log.d(TAG, "Parsed " + result.size() + " extremes for " + city.getName());
        return result;
    }

    private List<ExtremeTide> parseDay(Document doc, LocationParam city) {
        List<ExtremeTide> result = new ArrayList<>();
        LocalDate targetDate = new LocalDate(city.getDate());
        String dateStr = targetDate.toString("yyyy-MM-dd");

        Element mainRow = null;
        for (Element row : doc.select("tr[onclick]")) {
            if (row.attr("onclick").contains("Day('" + dateStr + "')")) {
                mainRow = row;
                break;
            }
        }
        if (mainRow == null) return result;

        parseTideCells(mainRow.select("td.tabla_mareas_marea"), result, targetDate, city.getName());

        // 5th extreme lives in the immediately following sibling row
        Element next = mainRow.nextElementSibling();
        if (next != null) {
            Elements extra = next.select("td.tabla_mareas_marea_mas_cuatro");
            if (!extra.isEmpty()) {
                parseTideCells(extra, result, targetDate, city.getName());
            }
        }

        return result;
    }

    private void parseTideCells(Elements tds, List<ExtremeTide> result, LocalDate date, String cityName) {
        for (Element td : tds) {
            Element horaDiv = td.selectFirst("div.tabla_mareas_marea_hora");
            if (horaDiv == null) continue;

            String timeStr = horaDiv.text().trim();
            String[] parts = timeStr.split(":");
            if (parts.length < 2) continue;

            int hour, minute;
            try {
                hour = Integer.parseInt(parts[0].trim());
                minute = Integer.parseInt(parts[1].trim());
            } catch (NumberFormatException e) {
                continue;
            }

            boolean isLow = td.selectFirst("div.tabla_mareas_marea_bajamar") != null;

            Element heightSpan = td.selectFirst("span.tabla_mareas_marea_altura_numero");
            if (heightSpan == null) continue;
            double height;
            try {
                height = Double.parseDouble(heightSpan.text().replace(",", ".").trim());
            } catch (NumberFormatException e) {
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
