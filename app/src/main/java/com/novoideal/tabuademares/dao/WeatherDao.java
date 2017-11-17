package com.novoideal.tabuademares.dao;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import com.j256.ormlite.android.apptools.OrmLiteSqliteOpenHelper;
import com.j256.ormlite.dao.Dao;
import com.j256.ormlite.dao.RuntimeExceptionDao;
import com.j256.ormlite.support.ConnectionSource;
import com.j256.ormlite.table.TableUtils;
import com.novoideal.tabuademares.model.LocationParam;
import com.novoideal.tabuademares.model.Weather;

import org.joda.time.DateTime;
import org.joda.time.LocalDate;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by Helio on 21/10/2017.
 */

public class WeatherDao extends OrmLiteSqliteOpenHelper {

    private static final String DATABASE_NAME = "tabuaMares_weather.db";
    private static final int DATABASE_VERSION = 1;

    private Dao<Weather, Integer> dao = null;
    private RuntimeExceptionDao<Weather, Integer> runtimeDao = null;

    public WeatherDao(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db, ConnectionSource connectionSource) {
        try {
            Log.i(WeatherDao.class.getName(), "onCreate");
            TableUtils.createTableIfNotExists(connectionSource, Weather.class);
        } catch (SQLException e) {
            Log.e(WeatherDao.class.getName(), "Can't create database", e);
            throw new RuntimeException(e);
        }
    }

    public void addNew(Weather data) {
        RuntimeExceptionDao<Weather, Integer> dao = getRuntimeDao();
        dao.create(data);
        Log.i(WeatherDao.class.getName(), "created new entries in onCreate: ");
    }


    @Override
    public void onUpgrade(SQLiteDatabase db, ConnectionSource connectionSource, int oldVersion, int newVersion) {
        try {
            Log.i(WeatherDao.class.getName(), "onUpgrade");
            TableUtils.dropTable(connectionSource, Weather.class, true);
            // after we drop the old databases, we create the new ones
            onCreate(db, connectionSource);
        } catch (SQLException e) {
            Log.e(WeatherDao.class.getName(), "Can't drop databases", e);
            throw new RuntimeException(e);
        }
    }

    public Dao<Weather, Integer> getDao() throws SQLException {
        if (dao == null) {
            dao = getDao(Weather.class);
        }
        return dao;
    }

    public RuntimeExceptionDao<Weather, Integer> getRuntimeDao() {
        if (runtimeDao == null) {
            runtimeDao = getRuntimeExceptionDao(Weather.class);
        }
        return runtimeDao;
    }

    @Override
    public void close() {
        super.close();
        dao = null;
        runtimeDao = null;
    }

    public void dropAndCreate() throws SQLException {
        ConnectionSource connectionSource = getDao().getConnectionSource();
        TableUtils.dropTable(connectionSource, Weather.class, true);
        Log.i(WeatherDao.class.getName(), "onCreate");
        TableUtils.createTableIfNotExists(connectionSource, Weather.class);
    }

    public List<Weather> geCondition(LocationParam city) {
        if (city.getLatWeather() == null || city.getLongWeather() == null) {
            return new ArrayList<>(0);
        }

        Map m = new HashMap();
        m.put("lat", city.getLatWeather());
        m.put("lon", city.getLongWeather());
        m.put("date", city.getDate());
        return getRuntimeDao().queryForFieldValues(m);
    }

    public boolean contains(Weather weather) {
        return getRuntimeDao().queryRawValue("select count(*) from weather where lat=? and lon=? and date=? and type=?",
                ""+ weather.getLat(), ""+ weather.getLon(),
                new DateTime(weather.getDate()).toString("yyyy-MM-dd HH:mm:ss.SSSSSS"),
                weather.getType()) > 0;
    }

    public int clearBefore(LocalDate now) {
        return getRuntimeDao().updateRaw("delete from weather where date < ?", now.toString("yyyy-MM-dd"));
    }

    public int clearCondiction(LocationParam param) {
        return getRuntimeDao().updateRaw("delete from weather where date = ?", param.getFullDateStr());
    }
}
