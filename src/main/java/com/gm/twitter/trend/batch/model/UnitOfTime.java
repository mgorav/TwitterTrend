package com.gm.twitter.trend.batch.model;

import java.io.Serializable;

public enum UnitOfTime implements Serializable {

    MINUTE, DAY, HOUR, MONTH, YEAR;
    private UnitOfTime() {
        
    }

    public static UnitOfTime fromCode(String code) {
        for(UnitOfTime unitOfTime: UnitOfTime.values()){
           if(unitOfTime.name().equalsIgnoreCase(code)) {
               return unitOfTime;
           }
        }
        return null;
    }
}
