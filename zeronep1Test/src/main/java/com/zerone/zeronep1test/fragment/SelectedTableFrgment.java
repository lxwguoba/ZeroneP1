package com.zerone.zeronep1test.fragment;

import android.app.Fragment;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.zerone.zeronep1test.R;
import com.zerone.zeronep1test.branch.BranchSelectedAdapter;
import com.zerone.zeronep1test.branch.tableadapter.TableCatListViewAdapter;
import com.zerone.zeronep1test.branch.tableadapter.TableDSelectedAdapter;
import com.zerone.zeronep1test.contanst.ContantData;
import com.zerone.zeronep1test.contanst.IpConfig;
import com.zerone.zeronep1test.db.impl.BranchDao;
import com.zerone.zeronep1test.domain.TableCatringBean;
import com.zerone.zeronep1test.domain.TableDBean;
import com.zerone.zeronep1test.event.ChangeSelectedTab;
import com.zerone.zeronep1test.utils.AppSharePreferenceMgr;
import com.zerone.zeronep1test.utils.GetGson;
import com.zerone.zeronep1test.utils.LoadingUtils;
import com.zerone.zeronep1test.utils.MapUtilsSetParam;
import com.zerone.zeronep1test.utils.NetUtils;
import com.zerone.zeronep1test.utils.Utils;
import com.zyao89.view.zloading.ZLoadingDialog;

import org.greenrobot.eventbus.EventBus;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Created by on 2018/1/30 0030 11 31.
 * Author  LiuXingWen
 */

public class SelectedTableFrgment extends Fragment {

    private View view;
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
    private View mJieZhangView;
    private LinearLayout jiezhang;
    private Button quxiao;
    private Button queding;
    private PopupWindow mPopupJZWindow;
    private ZLoadingDialog dailogJz;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        if (view==null){
            view = inflater.inflate(R.layout.activity_table_selected,null);
        }
        branchDao = new BranchDao(getActivity());
        initView();
        loadingTableData();
        initListViewListenner();
        initTableDListenner();
        popWindowListenner();
        popWindowJieZhangListenner();
        return view;
    }

    /**
     * 结账
     */
    private void popWindowJieZhangListenner() {



        quxiao.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                mPopupJZWindow.dismiss();
            }
        });

        queding.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dailogJz = LoadingUtils.getDailog(getContext(), Color.RED, "结账中。。。。");
                dailogJz.show();
                Map<String, String> map = MapUtilsSetParam.getMap(getActivity());
                //结账用：
                map.put("opp", "jiezhang");
                map.put("tableid", Utils.getACache(getContext()).getAsString("jz_tableid"));
                map.put("orderid",Utils.getACache(getContext()).getAsString("jz_orderid"));
                map.put("paytype","7");
                NetUtils.netWorkByMethodPost(getContext(), map, IpConfig.URL, handler, ContantData.JIEZHANG);

            }
        });

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
                TableDBean.DataBean  tableInfo =(TableDBean.DataBean) Utils.getACache(getContext()).getAsObject("tableInfo");
                String peoplecount = count_peoplepop.getText().toString().trim();
                Utils.getACache(getContext()).put("peoplecount",peoplecount);
                mPopupWindow.dismiss();
                Toast.makeText(getActivity(), "餐桌已经选择，可以开始点餐", Toast.LENGTH_SHORT).show();
                EventBus.getDefault().post(new ChangeSelectedTab("selectedtable",0));
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
                String using = listt.get(position).getUsing();
                if ("0".equals(using)){
                    Message message  = new Message();
                    message.what=ContantData.POPSHOW;
                    message.obj=listt.get(position);
                    handler.sendMessage(message);
                    Utils.getACache(getContext()).put("tableBean", listt.get(position)); 
                }else {
                    //先获取订单详情 把tableid orderid保存
                    Map<String, String> map = MapUtilsSetParam.getMap(getActivity());
                    map.put("opp", "getordergdlist");
                    map.put("tableid", listt.get(position).getId());
                    NetUtils.netWorkByMethodPost(getContext(), map, IpConfig.URL, handler, ContantData.GETTABLEORDERLIST);
                }
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
                tableCatAdapter.setPosition(position);
                 initNetTableList(listroom.get(position).getId() + "");
                 AppSharePreferenceMgr.put(getActivity(),"roomid",listroom.get(position).getId() + "");
                 tableCatAdapter.notifyDataSetChanged();
            }
        });
    }
    /**
     *
     */
    private void initView() {

        listt=new ArrayList<>();
        listcatroom = (ListView) view.findViewById(R.id.table_carting);
        listtable = (RecyclerView) view.findViewById(R.id.table_recycle);
        GridLayoutManager manager = new GridLayoutManager(getContext(), 2);
        listtable.setLayoutManager(manager);
        taAdapter = new TableDSelectedAdapter(listt);
        listtable.setAdapter(taAdapter);
        //开单
        if(mContentView == null){
            mContentView = LayoutInflater.from(getContext()).inflate(R.layout.activity_table_d_pop, null);
        }

        tablenamepop =(TextView) mContentView.findViewById(R.id.tablename);
        peoplecountpop = (TextView)mContentView.findViewById(R.id.table_per_count);
        count_peoplepop = (EditText) mContentView.findViewById(R.id.count_people);
        quxiaopop = (Button)mContentView.findViewById(R.id.quxiao);
        quedingaopop = (Button)mContentView.findViewById(R.id.queding);
        parentview = (LinearLayout) view.findViewById(R.id.view);
        //结账
        if(mJieZhangView == null){
            mJieZhangView = LayoutInflater.from(getContext()).inflate(R.layout.activity_table_d_jiezhang_pop, null);

        }

        jiezhang = (LinearLayout)mJieZhangView.findViewById(R.id.jiezhang);
        quxiao = (Button) mJieZhangView.findViewById(R.id.quxiao);
        queding = (Button) mJieZhangView.findViewById(R.id.queding);

    }


    //远程读取桌子分类数据
    private void loadingTableData() {
        Map<String, String> map = MapUtilsSetParam.getMap(getContext());
        map.put("opp", "gettablecatenew");
        try {
            map.put("branchid", Utils.getBranch(getContext()).getId());
        } catch (Exception e) {
            e.printStackTrace();
        }
        dailog = LoadingUtils.getDailog(getContext(), Color.RED, "获取数据中。。。。");
        dailog.show();
        NetUtils.netWorkByMethodPost(getContext(), map, IpConfig.URL, handler, ContantData.GETTABLECATRRESPONSE);
    }

    //远程读取桌子数据
    private void initNetTableList(String roomid) {
        Map<String, String> map = MapUtilsSetParam.getMap(getContext());
        map.put("branchid",Utils.getBranch(getContext()).getId());
        map.put("opp", "gettablenew");
        map.put("roomid", roomid);
        AppSharePreferenceMgr.put(getActivity(),"roomid",roomid);
        NetUtils.netWorkByMethodPost(getContext(), map, IpConfig.URL, handler, ContantData.GETTABLED);
    }
    Handler handler  = new Handler(){

        private JSONObject jsonObject;

        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what){
                case ContantData.GETTABLECATRRESPONSE:
                    String tablecatjson= (String) msg.obj;
                    Gson gson = GetGson.getGson();
                    TableCatringBean tableCatringBean = gson.fromJson(tablecatjson, TableCatringBean.class);
                    listroom=tableCatringBean.getData();
                    tableCatAdapter = new TableCatListViewAdapter(getContext(), listroom);
                    listcatroom.setAdapter(tableCatAdapter);
                    //默认时第一个
                    tableCatAdapter.setPosition(0);
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
                case ContantData.GETTABLEORDERLIST:
                    String tableListjson= (String) msg.obj;
                    try {
                        JSONObject jsonObject = new JSONObject(tableListjson);
                        int status = jsonObject.getInt("status");
                         if (status==0){
                             Toast.makeText(getContext(), "订单已过期,请到后台清理！！！", Toast.LENGTH_SHORT).show();
                         }else if (status==1){
                             JSONArray data =  jsonObject.getJSONArray("data");
                             for (int i=0;i<data.length();i++){
                                 String tableid = data.getJSONObject(i).getString("tableid");
                                 String orderid = data.getJSONObject(i).getString("orderid");
                                 Utils.getACache(getContext()).put("jz_tableid",tableid);
                                 Utils.getACache(getContext()).put("jz_orderid",orderid);
                             }
                             setPopJZWindow();
                         }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                    break;
                case ContantData.JIEZHANG:
                    String jiezhangJson= (String) msg.obj;
                    try {
                        JSONObject  jsonObject=new JSONObject(jiezhangJson);
                        int status = jsonObject.getInt("status");
                        if (status==1){
                            Toast.makeText(getContext(), "结账成功", Toast.LENGTH_SHORT).show();
                            //刷新 桌子数据
                            flushtable();
                        }else {
                            Toast.makeText(getContext(), "结账失败", Toast.LENGTH_SHORT).show();
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                    break;

                case ContantData.FLUSH:
                    String table= (String) msg.obj;

                    try {
                        jsonObject = new JSONObject(table);
                        int status = jsonObject.getInt("status");
                        if (status==0){
                         //有待修改！！！！！！！！！！！！！！！！！！2018-02-05
                            Toast.makeText(getActivity(),"刷新桌子转态失败",Toast.LENGTH_SHORT).show();
                        }else {
                            Gson gson02 = GetGson.getGson();
                            TableDBean tableDBean01 = gson02.fromJson(table, TableDBean.class);
                            listt.clear();
                            for (int i=0;i<tableDBean01.getData().size();i++){
                                listt.add(tableDBean01.getData().get(i));
                            }
                            taAdapter.notifyDataSetChanged();
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }finally {
                        dailogJz.dismiss();
                        mPopupJZWindow.dismiss();
                    }
                    break;
            }
        }
    };
    public void setPopWindow(){
        mPopupWindow = new PopupWindow(mContentView, LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.MATCH_PARENT, true);
        mPopupWindow.setTouchable(true);
        mPopupWindow.setOutsideTouchable(false);
        mPopupWindow.setBackgroundDrawable(new BitmapDrawable(getResources(), (Bitmap) null));
        mPopupWindow.showAtLocation(parentview, Gravity.CENTER,0,0);
    }
    public void setPopJZWindow(){
        mPopupJZWindow = new PopupWindow(mJieZhangView, LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.MATCH_PARENT, true);
        mPopupJZWindow.setTouchable(true);
        mPopupJZWindow.setOutsideTouchable(false);
        mPopupJZWindow.setBackgroundDrawable(new BitmapDrawable(getResources(), (Bitmap) null));
        mPopupJZWindow.showAtLocation(parentview, Gravity.CENTER,0,0);
    }

    //刷新桌子的数据

    public  void flushtable(){
        Map<String, String> map = MapUtilsSetParam.getMap(getContext());
        map.put("branchid",Utils.getBranch(getContext()).getId());
        map.put("opp", "gettablenew");
        String roomid = (String) AppSharePreferenceMgr.get(getActivity(), "roomid", "-100");
        if ("-100".equals(roomid)){
            Toast.makeText(getContext(),"获取桌子分类有问题",Toast.LENGTH_SHORT).show();
            return;
        }else {
            map.put("roomid",roomid);
        }
        NetUtils.netWorkByMethodPost(getContext(), map, IpConfig.URL, handler, ContantData.FLUSH);
    }
}
