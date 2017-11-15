package com.novoideal.tabuademares.model;

import com.j256.ormlite.field.DatabaseField;

import org.joda.time.DateTime;
import org.joda.time.Days;
import org.joda.time.LocalDate;

import java.util.Date;

/**
 * Created by Helio on 21/10/2017.
 */

public class LocationParam {

    @DatabaseField(generatedId = true)
    int id;

    @DatabaseField
    private String name;
    @DatabaseField
    private Integer codeSeaCondition;
    @DatabaseField
    private Integer woeId;
    @DatabaseField
    private Double latitude;
    @DatabaseField
    private Double longetude;
    @DatabaseField
    private Double latExtreme;
    @DatabaseField
    private Double longExtreme;
    @DatabaseField
    private Double latWeather;
    @DatabaseField
    private Double longWeather;
    private int days = 0;

    public static final LocationParam defaultCity = new LocationParam(1059, 426480, "Cabo Frio", 0, -22.87944, -42.018608);

    public LocationParam() {

    }

    public LocationParam(Integer codeSeaCondition, Integer woeId, String name, int days,
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

    public LocationParam setDays(int days) {
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

    public Double getLatExtreme() {
        return latExtreme;
    }

    public void setLatExtreme(Double latExtreme) {
        this.latExtreme = latExtreme;
    }

    public Double getLongExtreme() {
        return longExtreme;
    }

    public void setLongExtreme(Double longExtreme) {
        this.longExtreme = longExtreme;
    }

    public Double getLatWeather() {
        return latWeather;
    }

    public void setLatWeather(Double latWeather) {
        this.latWeather = latWeather;
    }

    public Double getLongWeather() {
        return longWeather;
    }

    public void setLongWeather(Double longWeather) {
        this.longWeather = longWeather;
    }

    public LocationParam clone(int days) {
        LocationParam clone = new LocationParam();
        clone.id = this.id;
        clone.codeSeaCondition = this.codeSeaCondition;
        clone.woeId = this.woeId;
        clone.name = this.name;
        clone.days = days;
        clone.latitude = this.latitude;
        clone.longetude = this.longetude;
        clone.latExtreme = this.latExtreme;
        clone.longExtreme = this.longExtreme;
        clone.latWeather = this.latWeather;
        clone.longWeather = this.longWeather;
        return clone;
    }


    @Override
    public String toString() {
        return name + " - " + new DateTime(getDate()).toString("dd/MM/yyyy");
    }

    public void setDate(Date date) {
        days = Days.daysBetween(new LocalDate(), new LocalDate(date)).getDays();
    }

    public int getId() {
        return id;
    }
}
