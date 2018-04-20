package com.zerone.zeronep1test.activity;

import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.widget.RadioGroup;
import com.githang.statusbar.StatusBarCompat;
import com.zerone.zeronep1test.baseacticity.BaseAppActivity;

import java.util.List;

/**
 * Created by on 2018/1/31 0031 10 42.
 * Author  LiuXingWen
 */

public class OrderActivity extends BaseAppActivity {
    private List<Fragment> list;
    private RadioGroup rg;
    private ViewPager vp;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        StatusBarCompat.setStatusBarColor(this, Color.parseColor("#ffffff"));
    }
}
