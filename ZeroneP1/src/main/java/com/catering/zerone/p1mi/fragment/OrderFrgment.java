package com.catering.zerone.p1mi.fragment;

import android.app.Fragment;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

import com.alibaba.fastjson.JSON;
import com.catering.zerone.p1mi.R;
import com.catering.zerone.p1mi.adapter.branch.order.ListOrderAdapter;
import com.catering.zerone.p1mi.adapter.branch.order.OrderListCycleAdapter;
import com.catering.zerone.p1mi.adapter.branch.tableadapter.TableDSelectedAdapter;
import com.catering.zerone.p1mi.contanst.ContantData;
import com.catering.zerone.p1mi.contanst.IpConfig;
import com.catering.zerone.p1mi.domain.DaiFuKuanOrderBean;
import com.catering.zerone.p1mi.utils.LoadingUtils;
import com.catering.zerone.p1mi.utils.MapUtilsSetParam;
import com.catering.zerone.p1mi.utils.NetUtils;
import com.catering.zerone.p1mi.utils.Utils;
import com.zyao89.view.zloading.ZLoadingDialog;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Created by on 2018/1/30 0030 11 31.
 * Author  LiuXingWen
 */

public class OrderFrgment extends Fragment {
    private View view;
    private ListView listcat;
    private RecyclerView ordercycle;
    List<String> liststr;
    private ListOrderAdapter loAdapter;
    private OrderListCycleAdapter cycAdapter;
    private ZLoadingDialog loading_dailog;
    List<DaiFuKuanOrderBean.DataBean.ListBean> getOrderList;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        if (view==null){
            view = inflater.inflate(R.layout.fragment_order_dd,null);
        }
        LoadingData();
        initCatData();
        initView();
        return view;
    }

    /**
     * 网若获取订单信息
     */
    private void LoadingData() {
        getOrderList = new ArrayList<>();
        loading_dailog = LoadingUtils.getDailog(getContext(), Color.RED, "获取订单中。。。。");
        loading_dailog.show();
        //获取order的数据
        Map<String, String> mapOrder = MapUtilsSetParam.getMap(getContext());
        mapOrder.put("opp", "orderlist");
        mapOrder.put("branchid",  Utils.getBranch(getContext()).getId());
        mapOrder.put("page", "1");
        mapOrder.put("status", "0");
        NetUtils.netWorkByMethodPost(getActivity(), mapOrder, IpConfig.URL, handler, ContantData.GETORDERLISTDF);
    }

    /**
     * 订单分类的数据
     */
    private void initCatData() {
        liststr = new ArrayList<String>();
        liststr.add("待付款");
        liststr.add("已付款");
        liststr.add("已完成");
    }

    /**
     * view的初始化
     */
    private void initView() {
        listcat = (ListView) view.findViewById(R.id.order_carting);
        loAdapter = new ListOrderAdapter(getContext(),liststr);
        listcat.setAdapter(loAdapter);
        ordercycle = (RecyclerView)view.findViewById(R.id.order_recycle);
        GridLayoutManager manager = new GridLayoutManager(getContext(), 1);
        ordercycle.setLayoutManager(manager);
        cycAdapter = new OrderListCycleAdapter(getOrderList);
        ordercycle.setAdapter(cycAdapter);
    }

    Handler handler  =  new Handler(){
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
             switch (msg.what){
                 case ContantData.GETORDERLISTDF:
                       String  orderJon= (String) msg.obj;
                     Log.i("URL","=======================================================");
                     Log.d("URL",orderJon);
                     Log.i("URL","=======================================================");
                     String orderJson = (String) msg.obj;
//                    Log.i("URL","orderjson==="+orderJson);
                     DaiFuKuanOrderBean daiFuKuanOrderBean = JSON.parseObject(orderJson, DaiFuKuanOrderBean.class);
                     if (daiFuKuanOrderBean.getStatus() == 0) {
                         return;
                     }
                     List<DaiFuKuanOrderBean.DataBean.ListBean> list = daiFuKuanOrderBean.getData().getList();
                     getOrderList.clear();
                     if (list.size() > 0) {
                         for (int i = 0; i < list.size(); i++) {
                             getOrderList.add(list.get(i));
                         }
                     }
                     cycAdapter.notifyDataSetChanged();
                     loading_dailog.dismiss();
                     break;
             }
        }
    };
}
