package com.novoideal.tabuademares.service;

import org.joda.time.DateTime;
import org.joda.time.Days;
import org.joda.time.LocalDate;

import java.util.Date;

/**
 * Created by Helio on 21/10/2017.
 */

public class CityCondition {

    private String name;
    private Integer codeSeaCondition;
    private Integer woeId;
    private Date date;
    private Double latitude;
    private Double longetude;

    public static final CityCondition defaultCity = new CityCondition(1059, 426480, "Cabo Frio",
            LocalDate.now().toDate(), -22.87944, -42.018608);

    public CityCondition(Integer codeSeaCondition, Integer woeId, String name, Date date,
                         Double latitude, Double longetude) {
        this.codeSeaCondition = codeSeaCondition;
        this.woeId = woeId;
        this.name = name;
        this.date = date;
        this.latitude = latitude;
        this.longetude = longetude;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Integer getCodeSeaCondition() {
        return codeSeaCondition;
    }

    public void setCodeSeaCondition(Integer codeSeaCondition) {
        this.codeSeaCondition = codeSeaCondition;
    }

    public Integer getWoeId() {
        return woeId;
    }

    public void setWoeId(Integer woeId) {
        this.woeId = woeId;
    }

    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }

    public Double getLongetude() {
        return longetude;
    }

    public void setLongetude(Double longetude) {
        this.longetude = longetude;
    }

    public Double getLatitude() {
        return latitude;
    }

    public void setLatitude(Double latitude) {
        this.latitude = latitude;
    }

    public Integer days() {
        return Days.daysBetween(DateTime.now(), new DateTime(date)).getDays();
    }

    @Override
    public String toString() {
        return name;
    }
}
