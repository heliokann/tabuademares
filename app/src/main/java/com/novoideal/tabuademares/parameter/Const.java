package com.novoideal.tabuademares.parameter;

import java.util.HashMap;
import java.util.Map;

public class Const {
    public static final Map<String, String > WOEID = new HashMap<>();

    //http://woeid.rosselliot.co.nz/lookup/CITY
    static {
        WOEID.put("426480", "Cabo Frio");
        WOEID.put("455825", "Rio de Janeiro");
        WOEID.put("455891", "Niterói");
        WOEID.put("439356", "Mangaratiba");
        WOEID.put("456038", "Angra dos Reis");
    }

    //https://query.yahooapis.com/v1/public/yql?q=select * from weather.forecast where woeid in (select woeid from geo.places(1) where text='são paulo, sp')
    //https://query.yahooapis.com/v1/public/yql?&format=json&q=select * from weather.forecast where woeid = 426480 and u = 'c'
    //https://developer.yahoo.com/weather/
}