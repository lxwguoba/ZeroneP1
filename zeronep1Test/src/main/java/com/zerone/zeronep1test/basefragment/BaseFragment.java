package com.zerone.zeronep1test.basefragment;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;

import org.greenrobot.eventbus.EventBus;

/**
 * Created by on 2018/1/24 0024 14 29.
 * Author  LiuXingWen
 */

public class BaseFragment extends Fragment {

    @Override
    public void onDestroy() {
        super.onDestroy();
        //解绑广播
        if (EventBus.getDefault().isRegistered(this))//加上判断
            EventBus.getDefault().unregister(this);//解除订阅

    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //注册广播
        if(!EventBus.getDefault().isRegistered(this)){//加上判断
            EventBus.getDefault().register(this);//订阅
        }

    }
}
