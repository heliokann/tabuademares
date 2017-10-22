package com.novoideal.tabuademares.service;

import android.content.Context;

import com.novoideal.tabuademares.dao.SeaConditionDao;
import com.novoideal.tabuademares.model.SeaCondition;

import java.util.List;


/**
 * Created by Helio on 21/10/2017.
 */

public class SeaConditionService {

    private SeaConditionDao seaConditionDao;

    public SeaConditionService(Context context) {
        seaConditionDao = new SeaConditionDao(context);
    }

    public List<SeaCondition> getAllCondiction() {
        return null;
    }

    public List<SeaCondition> geCondition(CityCondition city) throws Exception {
        List<SeaCondition> conditions =  seaConditionDao.geCondition(city);
        if(conditions != null && !conditions.isEmpty()){
            return conditions;
        }

        conditions = new SeaConditionCrawlerService().getWeathers(city);

        saveSeaCondiction(conditions);

        return conditions;


    }

    private void saveSeaCondiction(List<SeaCondition> conditions) {
        for (SeaCondition condition: conditions) {
            seaConditionDao.addNew(condition);
        }
    }


}
