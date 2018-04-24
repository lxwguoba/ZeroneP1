package com.zerone.zeronep1test.domain.finish;

/**
 * Created by on 2018/4/24 0024 09 23.
 * Author  LiuXingWen
 */

public class FinishActivity  {
    private  int stateCode;

    private  Class aClass;


    public FinishActivity(int stateCode, Class aClass) {
        this.stateCode = stateCode;
        this.aClass = aClass;
    }

    public int getStateCode() {
        return stateCode;
    }

    public void setStateCode(int stateCode) {
        this.stateCode = stateCode;
    }

    public Class getaClass() {
        return aClass;
    }

    public void setaClass(Class aClass) {
        this.aClass = aClass;
    }
}
