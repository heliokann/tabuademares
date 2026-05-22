package com.novoideal.tabuademares.service;

import android.content.Context;
import android.util.Log;

import com.novoideal.tabuademares.dao.SeaConditionDao;
import com.novoideal.tabuademares.model.LocationParam;
import com.novoideal.tabuademares.model.SeaCondition;

import java.util.List;


public class SeaConditionService {

    private SeaConditionDao seaConditionDao;
    private Context context;

    public SeaConditionService(Context context) {
        this.context = context;
        seaConditionDao = new SeaConditionDao(context);
    }

    public List<SeaCondition> geCondition(LocationParam city) throws Exception {
        List<SeaCondition> conditions = seaConditionDao.geCondition(city);
        if (conditions != null && !conditions.isEmpty()) {
            return conditions;
        }

        conditions = new OpenMeteoMarineService().getConditions(city);

        if (!conditions.isEmpty()) {
            saveSeaCondiction(conditions);
        }

        return conditions;
    }

    private void saveSeaCondiction(List<SeaCondition> conditions) {
        for (SeaCondition condition : conditions) {
            if (!seaConditionDao.contains(condition)) {
                seaConditionDao.addNew(condition);
            }
        }
    }

    public void cleanCondiction(LocationParam city) {
        seaConditionDao.clearCondiction(city);
    }
}
