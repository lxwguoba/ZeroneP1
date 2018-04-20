package com.zerone.zeronep1test.activity;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;

import com.githang.statusbar.StatusBarCompat;
import com.zerone.zeronep1test.R;
import com.zerone.zeronep1test.baseacticity.BaseAppActivity;
import com.zerone.zeronep1test.branch.WaiterListAdapter;
import com.zerone.zeronep1test.db.impl.WorkerTabeDao;
import com.zerone.zeronep1test.domain.Worker;
import com.zerone.zeronep1test.utils.Utils;

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
