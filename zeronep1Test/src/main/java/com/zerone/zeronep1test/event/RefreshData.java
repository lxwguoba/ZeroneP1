package com.zerone.zeronep1test.event;

import java.io.Serializable;

/**
 * Created by on 2018/4/25 0025 14 47.
 * Author  LiuXingWen
 * 刷新数据时用
 */

public class RefreshData implements Serializable {

        private  String refreshInfo;
        private  int refreshCode;

    public RefreshData(String refreshInfo, int refreshCode) {
        this.refreshInfo = refreshInfo;
        this.refreshCode = refreshCode;
    }

    public String getRefreshInfo() {
        return refreshInfo;
    }

    public void setRefreshInfo(String refreshInfo) {
        this.refreshInfo = refreshInfo;
    }

    public int getRefreshCode() {
        return refreshCode;
    }

    public void setRefreshCode(int refreshCode) {
        this.refreshCode = refreshCode;
    }
}
