package com.novoideal.tabuademares.model;

/**
 * Created by Helio on 21/10/2017.
 */

import com.j256.ormlite.field.DatabaseField;

import org.joda.time.DateTime;

import java.util.Date;

public class Weather {

    @DatabaseField(generatedId = true)
    int id;

    @DatabaseField
    private String city;
    @DatabaseField
    private String uf;
    @DatabaseField
    private String type; // night, day
    @DatabaseField
    private Date date;
    @DatabaseField
    private Date time;
    @DatabaseField
    private int windSpeed;
    @DatabaseField
    private int windDegree;
    @DatabaseField
    private int temperature;
    @DatabaseField
    private String windDir;
    @DatabaseField
    private String condition;
    @DatabaseField
    private String narrative;
    @DatabaseField
    private Double lat;
    @DatabaseField
    private Double lon;

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public String getUf() {
        return uf;
    }

    public void setUf(String uf) {
        this.uf = uf;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public int getWindSpeed() {
        return windSpeed;
    }

    public void setWindSpeed(int windSpeed) {
        this.windSpeed = windSpeed;
    }

    public Date getDate() {
        return date;
    }

    public String getStrFullDate() {
        return new DateTime(time).toString("yyyy-MM-dd HH:mm:ss.SSSSSS");
    }

    public Date getTime() {
        return time;
    }

    public void setTime(Date time) {
        this.time = time;
    }

    public void setDate(Date date) {
        this.date = date;
    }

    public Double getLat() {
        return lat;
    }

    public void setLat(Double lat) {
        this.lat = lat;
    }

    public Double getLon() {
        return lon;
    }

    public void setLon(Double lon) {
        this.lon = lon;
    }

    public int getWindDegree() {
        return windDegree;
    }

    public void setWindDegree(int windDegree) {
        this.windDegree = windDegree;
    }

    public int getTemperature() {
        return temperature;
    }

    public void setTemperature(int temperature) {
        this.temperature = temperature;
    }

    public String getWindDir() {
        return windDir;
    }

    public void setWindDir(String windDir) {
        this.windDir = windDir;
    }

    public String getNarrative() {
        return narrative;
    }

    public void setNarrative(String narrative) {
        this.narrative = narrative;
    }

    public String getCondition() {
        return condition;
    }

    public void setCondition(String condition) {
        this.condition = condition;
    }

    @Override
    public String toString() {
        return narrative;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Weather)) return false;

        Weather weather = (Weather) o;

        if (windSpeed != weather.windSpeed) return false;
        if (windDegree != weather.windDegree) return false;
        if (temperature != weather.temperature) return false;
        if (type != null && !type.equals(weather.type)) return false;
        if (date != null && !date.equals(weather.date)) return false;
        if (time != null && !time.equals(weather.time)) return false;
        if (windDir != null && !windDir.equals(weather.windDir)) return false;
        if (condition != null && !condition.equals(weather.condition)) return false;
        if (narrative != null && !narrative.equals(weather.narrative)) return false;
        if (lat != null && !lat.equals(weather.lat)) return false;
        if (lon != null && !lon.equals(weather.lon)) return false;

        return true;
    }

    @Override
    public int hashCode() {
        int result = city.hashCode();
        result = 31 * result + uf.hashCode();
        result = 31 * result + type.hashCode();
        result = 31 * result + date.hashCode();
        result = 31 * result + time.hashCode();
        result = 31 * result + windSpeed;
        result = 31 * result + windDegree;
        result = 31 * result + temperature;
        result = 31 * result + windDir.hashCode();
        result = 31 * result + condition.hashCode();
        result = 31 * result + narrative.hashCode();
        result = 31 * result + lat.hashCode();
        result = 31 * result + lon.hashCode();
        return result;
    }
}