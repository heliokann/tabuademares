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

import java.sql.SQLException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by Helio on 21/10/2017.
 */

public class LocationParamDao extends OrmLiteSqliteOpenHelper {

    private static final String DATABASE_NAME = "tabuaMares_location.db";
    private static final int DATABASE_VERSION = 1;

    private Dao<LocationParam, Integer> dao = null;
    private RuntimeExceptionDao<LocationParam, Integer> runtimeDao = null;

    public LocationParamDao(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db, ConnectionSource connectionSource) {
        try {
            Log.i(LocationParamDao.class.getName(), "onCreate");
            int create =  TableUtils.createTableIfNotExists(connectionSource, LocationParam.class);
            if (create > 0) {
                LocationParam lp = new LocationParam(3464, 455891, "Niterói", 0, -22.909309, -43.072231);
                addNew(lp);
                addNew(LocationParam.defaultCity);
            }
        } catch (SQLException e) {
            Log.e(LocationParamDao.class.getName(), "Can't create database", e);
            throw new RuntimeException(e);
        }
    }

    public void addNew(LocationParam data) {
        RuntimeExceptionDao<LocationParam, Integer> dao = getRuntimeDao();
        dao.create(data);
        Log.i(LocationParamDao.class.getName(), "created new entries in onCreate: ");
    }


    @Override
    public void onUpgrade(SQLiteDatabase db, ConnectionSource connectionSource, int oldVersion, int newVersion) {
        try {
            Log.i(LocationParamDao.class.getName(), "onUpgrade");
            TableUtils.dropTable(connectionSource, LocationParam.class, true);
            // after we drop the old databases, we create the new ones
            onCreate(db, connectionSource);
        } catch (SQLException e) {
            Log.e(LocationParamDao.class.getName(), "Can't drop databases", e);
            throw new RuntimeException(e);
        }
    }

    public Dao<LocationParam, Integer> getDao() throws SQLException {
        if (dao == null) {
            dao = getDao(LocationParam.class);
        }
        return dao;
    }

    public RuntimeExceptionDao<LocationParam, Integer> getRuntimeDao() {
        if (runtimeDao == null) {
            runtimeDao = getRuntimeExceptionDao(LocationParam.class);
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
        TableUtils.dropTable(connectionSource, LocationParam.class, true);
        Log.i(LocationParamDao.class.getName(), "onCreate");
        TableUtils.createTableIfNotExists(connectionSource, LocationParam.class);
    }

    public List<LocationParam> geLocationParams(LocationParam city) {
        Map m = new HashMap();
        m.put("latitude", city.getLatitude());
        m.put("longitude", city.getLongetude());
        return getRuntimeDao().queryForFieldValues(m);
    }

    public boolean contains(LocationParam locationParam) {
        return getRuntimeDao().queryRawValue("select count(*) from locationParam where latitude=? and longitude=?",
                ""+ locationParam.getLatitude(), ""+ locationParam.getLongetude()) > 0;
    }

    public List<LocationParam> geLocationParams() {
        return getRuntimeDao().queryForAll();
    }

    public synchronized int updateExtremeParams(LocationParam city) {
        return getRuntimeDao().updateRaw("update locationParam set latExtreme=?, longExtreme=? where id=?",
                city.getLatExtreme().toString(), city.getLongExtreme().toString(), "" + city.getId());
    }

    public synchronized int updateWeatherParams(LocationParam city) {
        return getRuntimeDao().updateRaw("update locationParam set latWeather=?, longWeather=? where id=?",
                city.getLatWeather().toString(), city.getLongWeather().toString(), "" + city.getId());
    }
}
