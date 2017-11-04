package com.novoideal.tabuademares.model;

/**
 * Created by Helio on 21/10/2017.
 */

import com.j256.ormlite.field.DatabaseField;

import org.joda.time.DateTime;

import java.text.DecimalFormat;
import java.util.Date;

public class ExtremeTide {

    enum TYPE {low, hight};

    @DatabaseField(generatedId = true)
    int id;

    @DatabaseField
    private String city;
    @DatabaseField
    private String uf;
    @DatabaseField
    private String type;
    @DatabaseField
    private Date date;
    @DatabaseField
    private Date fullDate;
    @DatabaseField
    private Double height;
    @DatabaseField
    private int hour;
    @DatabaseField
    private int minute;
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

    public Double getHeight() {
        return height;
    }

    public void setHeight(Double height) {
        this.height = height;
    }

    public Date getDate() {
        return date;
    }

    public String getStrFullDate() {
        return new DateTime(fullDate).toString("yyyy-MM-dd HH:mm:ss.SSSSSS");
    }

    public Date getFullDate() {
        return fullDate;
    }

    public void setFullDate(Date fullDate) {
        this.fullDate = fullDate;
    }

    public void setDate(Date date) {
        this.date = date;
    }

    public int getHour() {
        return hour;
    }

    public void setHour(int hour) {
        this.hour = hour;
    }

    public int getMinute() {
        return minute;
    }

    public void setMinute(int minute) {
        this.minute = minute;
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

    @Override
    public String toString() {
        return (hour < 10 ? ("0" + hour) : hour) + ":" +
                (minute < 10 ? ("0" + minute) : minute) + " (" +
                new DecimalFormat("#.##").format(height + 0.45) + "m)";
    }
}