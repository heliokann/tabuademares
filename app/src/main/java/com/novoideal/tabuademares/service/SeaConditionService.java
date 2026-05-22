package com.novoideal.tabuademares.service;

import android.content.Context;
import android.util.Log;

import com.novoideal.tabuademares.dao.LocationParamDao;
import com.novoideal.tabuademares.dao.SeaConditionDao;
import com.novoideal.tabuademares.model.LocationParam;
import com.novoideal.tabuademares.model.SeaCondition;

import java.util.Collections;
import java.util.List;


/**
 * Created by Helio on 21/10/2017.
 */

public class SeaConditionService {

    private SeaConditionDao seaConditionDao;
    private Context context;

    public SeaConditionService(Context context) {
        this.context = context;
        seaConditionDao = new SeaConditionDao(context);
    }

    public List<SeaCondition> getAllCondiction() {
        return null;
    }

    public List<SeaCondition> geCondition(LocationParam city) throws Exception {
        List<SeaCondition> conditions =  seaConditionDao.geCondition(city);
        if(conditions != null && !conditions.isEmpty()){
            return conditions;
        }

        if (city.getCodeSeaCondition() == null || city.getCodeSeaCondition() == 0) {
            int code = new CptecCityLookupService().lookupCode(city.getName());
            if (code > 0) {
                city.setCodeSeaCondition(code);
                new LocationParamDao(context).updateSeaConditionCode(city);
            } else {
                Log.w(SeaConditionService.class.getSimpleName(), "CPTEC code not found for: " + city.getName());
                return Collections.emptyList();
            }
        }

        conditions = new SeaConditionCrawlerService().getWeathers(city);

        if(conditions.isEmpty()){
            return conditions;
        }

        saveSeaCondiction(conditions);

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
