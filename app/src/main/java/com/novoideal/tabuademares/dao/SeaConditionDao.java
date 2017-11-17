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
import com.novoideal.tabuademares.model.LocationParam;
import com.novoideal.tabuademares.model.SeaCondition;

import org.joda.time.DateTime;
import org.joda.time.LocalDate;

import java.sql.SQLException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class SeaConditionDao extends OrmLiteSqliteOpenHelper {

    private static final String DATABASE_NAME = "tabuaMares_seaCondiction.db";
    private static final int DATABASE_VERSION = 1;

    private Dao<SeaCondition, Integer> dao = null;
    private RuntimeExceptionDao<SeaCondition, Integer> runtimeDao = null;

    public SeaConditionDao(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db, ConnectionSource connectionSource) {
        try {
            Log.i(SeaConditionDao.class.getName(), "onCreate");
            TableUtils.createTableIfNotExists(connectionSource, SeaCondition.class);
        } catch (SQLException e) {
            Log.e(SeaConditionDao.class.getName(), "Can't create database", e);
            throw new RuntimeException(e);
        }
    }

    public void addNew(SeaCondition data) {
        RuntimeExceptionDao<SeaCondition, Integer> dao = getRuntimeDao();
        dao.create(data);
        Log.i(SeaConditionDao.class.getName(), "created new entries in onCreate: ");
    }


    @Override
    public void onUpgrade(SQLiteDatabase db, ConnectionSource connectionSource, int oldVersion, int newVersion) {
        try {
            Log.i(SeaConditionDao.class.getName(), "onUpgrade");
            TableUtils.dropTable(connectionSource, SeaCondition.class, true);
            // after we drop the old databases, we create the new ones
            onCreate(db, connectionSource);
        } catch (SQLException e) {
            Log.e(SeaConditionDao.class.getName(), "Can't drop databases", e);
            throw new RuntimeException(e);
        }
    }

    public Dao<SeaCondition, Integer> getDao() throws SQLException {
        if (dao == null) {
            dao = getDao(SeaCondition.class);
        }
        return dao;
    }

    public RuntimeExceptionDao<SeaCondition, Integer> getRuntimeDao() {
        if (runtimeDao == null) {
            runtimeDao = getRuntimeExceptionDao(SeaCondition.class);
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
        TableUtils.dropTable(connectionSource, SeaCondition.class, true);
        Log.i(SeaConditionDao.class.getName(), "onCreate");
        TableUtils.createTableIfNotExists(connectionSource, SeaCondition.class);
    }

    public List<SeaCondition> geCondition(LocationParam city) {
        Map m = new HashMap();
        m.put("city", city.getName());
        m.put("date", city.getDate());
        return getRuntimeDao().queryForFieldValues(m);
    }

    public boolean contains(SeaCondition condition) {
        return getRuntimeDao().queryRawValue("select count(*) from seacondition where city=? and date=? and period=?",
                condition.getCity(), new DateTime(condition.getDate()).toString("yyyy-MM-dd HH:mm:ss.SSSSSS"), condition.getPeriod()) > 0;
    }

    public int clearBefore(LocalDate now) {
        return getRuntimeDao().updateRaw("delete from seacondition where date < ?", now.toString("yyyy-MM-dd"));
    }

    public int clearCondiction(LocationParam param) {
        return getRuntimeDao().updateRaw("delete from seacondition where date = ?", param.getFullDateStr());
    }
}