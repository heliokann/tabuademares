package com.novoideal.tabuademares.service;

import android.content.Context;

import com.novoideal.tabuademares.dao.LocationParamDao;
import com.novoideal.tabuademares.model.LocationParam;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;


/**
 * Created by Helio on 15/11/2017.
 */

public class LocationParamService {

    private LocationParamDao locationParamDao;

    public LocationParamService(Context context) {
        locationParamDao = new LocationParamDao(context);
    }

    public List<LocationParam> geLocations(LocationParam city) throws Exception {
        return locationParamDao.geLocationParams(city);
    }

    public List<LocationParam> geLocations() {
        return locationParamDao.geLocationParams();
    }

    public List<LocationParam> geLocations(List<LocationParam> origin, int day) {
        List<LocationParam> clone = new ArrayList<>(origin.size());
        for (LocationParam location: origin) {
            clone.add(location.clone(day));
        }
        return clone;
    }

    public void saveIfNew(LocationParam city) {
        if (!locationParamDao.contains(city)) {
            locationParamDao.addNew(city);
        }
    }

    private void saveLocationParam(List<LocationParam> conditions) {
        for (LocationParam condition : conditions) {
            if (!locationParamDao.contains(condition)) {
                locationParamDao.addNew(condition);
            }
        }
    }

    public Date getLastUpdated(LocationParam locationParam) {
        try {
            locationParam = locationParamDao.getById(locationParam);
        } catch (SQLException e) {
        }
        locationParam.setUpdated(locationParam.getUpdated() == null ? new Date() : locationParam.getUpdated());
        return locationParam.getUpdated();
    }

    public void updateSelected(LocationParam currentLocation) {
        locationParamDao.updateSelected(currentLocation);
    }

    /**
     * Persiste a cidade (se nova) e a marca como selecionada usando o id real
     * da linha no banco. Cidades vindas do dataset embarcado chegam com id 0;
     * resolver a linha persistida evita marcar a seleção em "where id=0", que
     * não atinge nenhuma linha. Retorna a linha persistida (com id real).
     */
    public LocationParam saveAndSelect(LocationParam city) {
        saveIfNew(city);
        LocationParam persisted = locationParamDao.findPersisted(city);
        if (persisted != null) {
            locationParamDao.updateSelected(persisted);
            return persisted;
        }
        locationParamDao.updateSelected(city);
        return city;
    }

    public void touch(LocationParam city) {
        locationParamDao.touch(city);
    }
}
