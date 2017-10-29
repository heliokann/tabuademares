package com.novoideal.tabuademares.model;

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
    private Double latitude;
    private Double longetude;
    private int days = 0;

    public static final CityCondition defaultCity = new CityCondition(1059, 426480, "Cabo Frio", 0, -22.87944, -42.018608);

    public CityCondition() {

    }

    public CityCondition(Integer codeSeaCondition, Integer woeId, String name, int days,
                         Double latitude, Double longetude) {
        this.codeSeaCondition = codeSeaCondition;
        this.woeId = woeId;
        this.name = name;
        this.days = days;
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
        return new LocalDate().plusDays(days).toDate();
    }

    public CityCondition setDays(int days) {
        this.days = days;
        return this;
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
        return days;
    }

    public CityCondition clone(int days) {
        CityCondition clone = new CityCondition();
        clone.codeSeaCondition = this.codeSeaCondition;
        clone.woeId = this.woeId;
        clone.name = this.name;
        clone.days = days;
        clone.latitude = this.latitude;
        clone.longetude = this.longetude;
        return clone;
    }


    @Override
    public String toString() {
        return name + " - " + new DateTime(getDate()).toString("dd/MM/yyyy");
    }

    public void setDate(Date date) {
        days = Days.daysBetween(new LocalDate(), new LocalDate(date)).getDays();
    }
}
