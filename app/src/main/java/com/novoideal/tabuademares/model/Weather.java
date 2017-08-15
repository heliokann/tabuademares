package com.novoideal.tabuademares.model;

import java.util.Date;

/**
 * Created by Helio on 08/08/2017.
 */

public class Weather {

    private String cidade;
    private String uf;
    private String updated;
    private String period;
    private String date;
    private String agitation;
    private Double height;
    private String sewll;
    private Double wind;
    private String wind_dir;

    public Weather() {

    }

    public String getCidade() {
        return cidade;
    }

    public void setCidade(String cidade) {
        this.cidade = cidade;
    }

    public String getUf() {
        return uf;
    }

    public void setUf(String uf) {
        this.uf = uf;
    }

    public String getUpdated() {
        return updated;
    }

    public void setUpdated(String updated) {
        this.updated = updated;
    }

    public String getPeriod() {
        return period;
    }

    public void setPeriod(String period) {
        this.period = period;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getAgitation() {
        return agitation;
    }

    public void setAgitation(String agitation) {
        this.agitation = agitation;
    }

    public Double getHeight() {
        return height;
    }

    public void setHeight(Double height) {
        this.height = height;
    }

    public String getSewll() {
        return sewll;
    }

    public void setSewll(String sewll) {
        this.sewll = sewll;
    }

    public Double getWind() {
        return wind;
    }

    public void setWind(Double wind) {
        this.wind = wind;
    }

    public String getWind_dir() {
        return wind_dir;
    }

    public void setWind_dir(String wind_dir) {
        this.wind_dir = wind_dir;
    }

}
