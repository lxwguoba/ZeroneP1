package com.catering.zerone.p1mi.activity;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;

import com.catering.zerone.p1mi.R;
import com.catering.zerone.p1mi.adapter.branch.WaiterListAdapter;
import com.catering.zerone.p1mi.baseacticity.BaseAppActivity;
import com.catering.zerone.p1mi.db.impl.WorkerTabeDao;
import com.catering.zerone.p1mi.domain.Worker;
import com.catering.zerone.p1mi.utils.Utils;
import com.githang.statusbar.StatusBarCompat;

import java.util.List;

/**
 * Created by on 2018/1/26 0026 16 24.
 * Author  LiuXingWen
 * 接待员选择交接班
 */

public class CheckWaiterActivity extends BaseAppActivity {
    private List<Worker> worker;
    private ListView waiterlist;
    private WaiterListAdapter wladapter;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_checkwaiter);
        StatusBarCompat.setStatusBarColor(this, Color.parseColor("#ffffff"));
        LoadingWaiterData();
        initView();
    }
    /**
     * view的初始化
     */
    private void initView() {
        waiterlist = (ListView) findViewById(R.id.waiterlist);
        wladapter = new WaiterListAdapter(this,worker);
        waiterlist.setAdapter(wladapter);
        waiterlist.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Utils.getACache(CheckWaiterActivity.this).put("waiter",worker.get(position));

                Intent  intent  =  new Intent(CheckWaiterActivity.this, NewMainActivity.class);
                startActivity(intent);
                CheckWaiterActivity.this.finish();
            }
        });
    }
    /**
     * 从数据库中获取接单员
     */
    private void LoadingWaiterData() {
        WorkerTabeDao wtd= new WorkerTabeDao(CheckWaiterActivity.this);
        try {
            worker = wtd.getWorker();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}
