package com.catering.zerone.p1mi.activity;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.TextView;
import com.catering.zerone.p1mi.R;
import com.catering.zerone.p1mi.adapter.branch.BranchSelectedAdapter;
import com.catering.zerone.p1mi.adapter.branch.tableadapter.TableCatListViewAdapter;
import com.catering.zerone.p1mi.adapter.branch.tableadapter.TableDSelectedAdapter;
import com.catering.zerone.p1mi.baseacticity.BaseAppActivity;
import com.catering.zerone.p1mi.contanst.ContantData;
import com.catering.zerone.p1mi.contanst.IpConfig;
import com.catering.zerone.p1mi.db.impl.BranchDao;
import com.catering.zerone.p1mi.domain.TableCatringBean;
import com.catering.zerone.p1mi.domain.TableDBean;
import com.catering.zerone.p1mi.utils.GetGson;
import com.catering.zerone.p1mi.utils.LoadingUtils;
import com.catering.zerone.p1mi.utils.MapUtilsSetParam;
import com.catering.zerone.p1mi.utils.NetUtils;
import com.catering.zerone.p1mi.utils.Utils;
import com.google.gson.Gson;
import com.zyao89.view.zloading.ZLoadingDialog;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Created by on 2018/1/23 0023 13 53.
 * Author  LiuXingWen
 */

public class TableActivity extends BaseAppActivity {

    private BranchDao branchDao;
    private ZLoadingDialog dailog;
    //餐桌的分类-房间
    private ListView listcatroom;
    private List<TableCatringBean.DataBean> listroom;
    private TableCatListViewAdapter tableCatAdapter;
    //桌子
    private RecyclerView listtable;
    private  List<TableDBean.DataBean> listt;
    private TableDSelectedAdapter taAdapter;
    private static View mContentView = null;
    private LinearLayout parentview;
    private PopupWindow mPopupWindow;
    private TextView tablenamepop;
    private TextView peoplecountpop;
    private EditText count_peoplepop;
    private Button quxiaopop;
    private Button quedingaopop;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_table_selected);
        branchDao = new BranchDao(this);
        initView();
        loadingTableData();
        initListViewListenner();
        initTableDListenner();
        popWindowListenner();
    }

    private void popWindowListenner() {
        quxiaopop.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mPopupWindow.dismiss();
            }
        });
        quedingaopop.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                TableDBean.DataBean  tableInfo =(TableDBean.DataBean) Utils.getACache(TableActivity.this).getAsObject("tableInfo");
                String peoplecount = count_peoplepop.getText().toString().trim();
                Intent intent = new Intent();
                intent.putExtra("tableInfo", tableInfo);
                intent.putExtra("peoplecount",peoplecount);
                setResult(RESULT_OK, intent);
                mPopupWindow.dismiss();
                TableActivity.this.finish();
            }
        });

    }

    /**
     * 桌子的点击事件
     */
    private void initTableDListenner() {
        taAdapter.setOnItemClickListener(new BranchSelectedAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(View view, int position) {
                Message message  = new Message();
                 message.what=ContantData.POPSHOW;
                 message.obj=listt.get(position);
                 handler.sendMessage(message);
                Utils.getACache(TableActivity.this).put("tableInfo", listt.get(position));
            }
        });
    }

    /**
     * listview的点击事件
     */
    private void initListViewListenner() {
        listcatroom.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                initNetTableList(listroom.get(position).getId() + "");
            }
        });

    }

    /**
     *
     */
    private void initView() {

        listt=new ArrayList<>();
        listcatroom = (ListView) findViewById(R.id.table_carting);
        listtable = (RecyclerView) findViewById(R.id.table_recycle);
        GridLayoutManager manager = new GridLayoutManager(this, 2);
        listtable.setLayoutManager(manager);
        taAdapter = new TableDSelectedAdapter(listt);
        listtable.setAdapter(taAdapter);
        if(mContentView == null){
            mContentView = LayoutInflater.from(TableActivity.this).inflate(R.layout.activity_table_d_pop, null);
        }

        tablenamepop =(TextView) mContentView.findViewById(R.id.tablename);
        peoplecountpop = (TextView)mContentView.findViewById(R.id.table_per_count);
        count_peoplepop = (EditText) mContentView.findViewById(R.id.count_people);
        quxiaopop = (Button)mContentView.findViewById(R.id.quxiao);
        quedingaopop = (Button)mContentView.findViewById(R.id.queding);
        parentview = (LinearLayout) findViewById(R.id.view);
    }

    //远程读取桌子分类数据
    private void loadingTableData() {
        Map<String, String> map = MapUtilsSetParam.getMap(TableActivity.this);
        map.put("opp", "gettablecatenew");
        try {
            map.put("branchid", Utils.getBranch(TableActivity.this).getId());
        } catch (Exception e) {
            e.printStackTrace();
        }
        dailog = LoadingUtils.getDailog(TableActivity.this, Color.RED, "获取数据中。。。。");
        dailog.show();
        NetUtils.netWorkByMethodPost(TableActivity.this, map, IpConfig.URL, handler, ContantData.GETTABLECATRRESPONSE);
    }

    //远程读取桌子数据
    private void initNetTableList(String roomid) {
        Map<String, String> map = MapUtilsSetParam.getMap(TableActivity.this);
        map.put("branchid",Utils.getBranch(TableActivity.this).getId());
        map.put("opp", "gettablenew");
        map.put("roomid", roomid);
        NetUtils.netWorkByMethodPost(TableActivity.this, map, IpConfig.URL, handler, ContantData.GETTABLED);
    }
      Handler handler  = new Handler(){
          @Override
          public void handleMessage(Message msg) {
              super.handleMessage(msg);
              switch (msg.what){
                  case ContantData.GETTABLECATRRESPONSE:
                      String tablecatjson= (String) msg.obj;
                      Gson gson = GetGson.getGson();
                      TableCatringBean   tableCatringBean = gson.fromJson(tablecatjson, TableCatringBean.class);
                      listroom=tableCatringBean.getData();
                      tableCatAdapter = new TableCatListViewAdapter(TableActivity.this, listroom);
                      listcatroom.setAdapter(tableCatAdapter);
                      initNetTableList(tableCatringBean.getData().get(0).getId()+"");
                      break;
                  case ContantData.GETTABLED:
                        String tablejson= (String) msg.obj;
                        Gson gson01 = GetGson.getGson();
                        TableDBean tableDBean = gson01.fromJson(tablejson, TableDBean.class);
                        listt.clear();
                         for (int i=0;i<tableDBean.getData().size();i++){
                             listt.add(tableDBean.getData().get(i));
                         }
                        taAdapter.notifyDataSetChanged();
                        dailog.dismiss();
                      break;

                  case ContantData.POPSHOW:
                        TableDBean.DataBean  dataBean=(TableDBean.DataBean)msg.obj;
                        tablenamepop.setText("桌子名称："+dataBean.getTablename());
                        peoplecountpop.setText("桌子可座人数：12");
                        setPopWindow();
                      break;
              }
          }
      };
      public void setPopWindow(){
          mPopupWindow = new PopupWindow(mContentView, LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.MATCH_PARENT, true);
          mPopupWindow.setTouchable(true);
          mPopupWindow.setOutsideTouchable(false);
          mPopupWindow.setBackgroundDrawable(new BitmapDrawable(getResources(), (Bitmap) null));
          mPopupWindow.showAtLocation(parentview,Gravity.CENTER,0,0);
      }

}
