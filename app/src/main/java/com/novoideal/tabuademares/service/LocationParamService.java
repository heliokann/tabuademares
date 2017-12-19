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
}
