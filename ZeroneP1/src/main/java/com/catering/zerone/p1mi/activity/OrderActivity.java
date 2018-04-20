package com.catering.zerone.p1mi.activity;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.View;
import android.widget.RadioGroup;

import com.catering.zerone.p1mi.MainActivity;
import com.catering.zerone.p1mi.R;
import com.catering.zerone.p1mi.baseacticity.BaseAppActivity;
import com.catering.zerone.p1mi.contanst.ContantData;
import com.catering.zerone.p1mi.fragment.FragmentOrderDC;
import com.catering.zerone.p1mi.fragment.FragmentOrderDD;
import com.catering.zerone.p1mi.fragment.order.DFFragment;
import com.catering.zerone.p1mi.fragment.order.YCFragment;
import com.catering.zerone.p1mi.fragment.order.YFFragment;
import com.catering.zerone.p1mi.utils.AnimationUtil;
import com.githang.statusbar.StatusBarCompat;

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
