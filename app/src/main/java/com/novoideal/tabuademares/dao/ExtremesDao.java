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
import com.j256.ormlite.stmt.PreparedQuery;
import com.j256.ormlite.stmt.QueryBuilder;
import com.j256.ormlite.support.ConnectionSource;
import com.j256.ormlite.table.TableUtils;
import com.novoideal.tabuademares.model.ExtremeTide;
import com.novoideal.tabuademares.model.LocationParam;

import org.joda.time.DateTime;
import org.joda.time.LocalDate;

import java.sql.SQLException;
import java.util.ArrayList;
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
        DateTime dt = new DateTime(data.getFullDate());
        dao.updateRaw("delete from extremetide where lat=? and lon=? and fullDate > ? and  fullDate < ?",
                data.getLat().toString(), data.getLon().toString(),
                dt.minusMinutes(5).toString("yyyy-MM-dd HH:mm:ss.SSSSSS"),
                dt.plusMinutes(5).toString("yyyy-MM-dd HH:mm:ss.SSSSSS"));
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

    public List<ExtremeTide> geCondition(LocationParam city) {
        if (city.getLatExtreme() == null || city.getLongExtreme() == null) {
            return new ArrayList<>(0);
        }

        Map m = new HashMap();
        m.put("lat", city.getLatExtreme());
        m.put("lon", city.getLongExtreme());
        m.put("fullDate", city.getDate());

        QueryBuilder<ExtremeTide, Integer> queryBuilder = getRuntimeDao().queryBuilder();
        PreparedQuery pq = null;
        try {
            pq = queryBuilder.where().eq("lat", city.getLatExtreme())
                    .and().eq("lon", city.getLongExtreme())
                    .and().eq("date", city.getDate()).prepare();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return getRuntimeDao().query(pq);
//        return getRuntimeDao().queryForFieldValues(m);
//        return getRuntimeDao().queryRawValue("select * from extremetide where lat=? and lon=? and fullDate=?",
//                city.getLatExtreme().toString(), city.getLongExtreme().toString(), city.getFullDateStr());
    }

    public boolean contains(ExtremeTide extreme) {
//        dao.queryRaw("select * from extremetide where city=? and date='2017-10-28 00:00:00.000000'", "RIO DE JANEIRO").getResults()
        return getRuntimeDao().queryRawValue("select count(*) from extremetide where lat=? and lon=? and fullDate=? and type=?",
                extreme.getLat().toString(), extreme.getLon().toString(), extreme.getStrFullDate(), extreme.getType()) > 0;
    }

    public int clearBefore(LocalDate now) {
        return getRuntimeDao().updateRaw("delete from extremetide where date < ?", now.toString("yyyy-MM-dd"));
    }
    public int clearCondiction(LocationParam param) {
        return getRuntimeDao().updateRaw("delete from extremetide where date = ?", param.getFullDateStr());
    }
}