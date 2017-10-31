package com.novoideal.tabuademares.dao;

/**
 * Created by Helio on 21/10/2017.
 */

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import com.j256.ormlite.android.apptools.OrmLiteSqliteOpenHelper;
import com.j256.ormlite.dao.Dao;
import com.j256.ormlite.dao.RuntimeExceptionDao;
import com.j256.ormlite.support.ConnectionSource;
import com.j256.ormlite.table.TableUtils;
import com.novoideal.tabuademares.model.CityCondition;
import com.novoideal.tabuademares.model.ExtremeTide;

import org.joda.time.DateTime;

import java.sql.SQLException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ExtremesDao extends OrmLiteSqliteOpenHelper {

    private static final String DATABASE_NAME = "tabuaMares_extremes.db";
    private static final int DATABASE_VERSION = 1;

    private Dao<ExtremeTide, Integer> dao = null;
    private RuntimeExceptionDao<ExtremeTide, Integer> runtimeDao = null;

    public ExtremesDao(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db, ConnectionSource connectionSource) {
        try {
            Log.i(ExtremesDao.class.getName(), "onCreate");
            TableUtils.createTableIfNotExists(connectionSource, ExtremeTide.class);
        } catch (SQLException e) {
            Log.e(ExtremesDao.class.getName(), "Can't create database", e);
            throw new RuntimeException(e);
        }
    }

    public void addNew(ExtremeTide data) {
        RuntimeExceptionDao<ExtremeTide, Integer> dao = getRuntimeDao();
        dao.create(data);
        Log.i(ExtremesDao.class.getName(), "created new entries in onCreate: ");
    }


    @Override
    public void onUpgrade(SQLiteDatabase db, ConnectionSource connectionSource, int oldVersion, int newVersion) {
        try {
            Log.i(ExtremesDao.class.getName(), "onUpgrade");
            TableUtils.dropTable(connectionSource, ExtremeTide.class, true);
            // after we drop the old databases, we create the new ones
            onCreate(db, connectionSource);
        } catch (SQLException e) {
            Log.e(ExtremesDao.class.getName(), "Can't drop databases", e);
            throw new RuntimeException(e);
        }
    }

    public Dao<ExtremeTide, Integer> getDao() throws SQLException {
        if (dao == null) {
            dao = getDao(ExtremeTide.class);
        }
        return dao;
    }

    public RuntimeExceptionDao<ExtremeTide, Integer> getRuntimeDao() {
        if (runtimeDao == null) {
            runtimeDao = getRuntimeExceptionDao(ExtremeTide.class);
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
        TableUtils.dropTable(connectionSource, ExtremeTide.class, true);
        Log.i(ExtremesDao.class.getName(), "onCreate");
        TableUtils.createTableIfNotExists(connectionSource, ExtremeTide.class);
    }

    public List<ExtremeTide> geCondition(CityCondition city) {
        Map m = new HashMap();
        m.put("city", city.getName());
        m.put("date", city.getDate());
        return getRuntimeDao().queryForFieldValues(m);
    }

    public boolean contains(ExtremeTide extreme) {
//        dao.queryRaw("select * from extremetide where city=? and date='2017-10-28 00:00:00.000000'", "RIO DE JANEIRO").getResults()
        return getRuntimeDao().queryRawValue("select count(*) from extremetide where city=? and date=? and type=?",
                extreme.getCity(), new DateTime(extreme.getDate()).toString("yyyy-MM-dd HH:mm:ss.SSSSSS"), extreme.getType()) > 0;
    }
}