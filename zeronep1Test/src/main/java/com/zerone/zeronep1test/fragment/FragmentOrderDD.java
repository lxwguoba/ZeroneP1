package com.zerone.zeronep1test.fragment;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

import com.zerone.zeronep1test.R;
import com.zerone.zeronep1test.branch.order.ListOrderAdapter;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by on 2018/1/23 0023 13 37.
 * Author  LiuXingWen
 */

public class FragmentOrderDD extends Fragment {
    private View view;
    private ListView listcat;
    private RecyclerView ordercycle;
    List<String>  liststr;
    private ListOrderAdapter loAdapter;

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        if (view==null){
            view = inflater.inflate(R.layout.fragment_order_dd,null);
        }
        initCatData();
        initView();
        return view;
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
    }
}
