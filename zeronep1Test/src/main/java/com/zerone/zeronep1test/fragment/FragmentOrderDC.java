package com.zerone.zeronep1test.fragment;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import com.eowise.recyclerview.stickyheaders.OnHeaderClickListener;
import com.eowise.recyclerview.stickyheaders.StickyHeadersBuilder;
import com.eowise.recyclerview.stickyheaders.StickyHeadersItemDecoration;
import com.zerone.zeronep1test.R;
import com.zerone.zeronep1test.basefragment.BaseFragment;
import com.zerone.zeronep1test.branch.BigramHeaderAdapter;
import com.zerone.zeronep1test.branch.PersonAdapter;
import com.zerone.zeronep1test.branch.RecycleGoodsCategoryListAdapter;
import com.zerone.zeronep1test.contanst.ContantData;
import com.zerone.zeronep1test.contanst.IpConfig;
import com.zerone.zeronep1test.domain.GoodsListBean;
import com.zerone.zeronep1test.domain.TableDBean;
import com.zerone.zeronep1test.event.AcceptMessage;
import com.zerone.zeronep1test.event.GoodsListEvent;
import com.zerone.zeronep1test.utils.LoadingUtils;
import com.zerone.zeronep1test.utils.MapUtilsSetParam;
import com.zerone.zeronep1test.utils.NetUtils;
import com.zerone.zeronep1test.utils.Utils;
import com.zyao89.view.zloading.ZLoadingDialog;

import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;
import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import static android.app.Activity.RESULT_OK;

/**
 * Created by on 2018/1/23 0023 13 37.
 * Author  LiuXingWen
 */
//implements PersonAdapter.OnShopCartGoodsChangeListener, OnHeaderClickListener
public class FragmentOrderDC extends BaseFragment implements PersonAdapter.OnShopCartGoodsChangeListener, OnHeaderClickListener {
    //商品类别列表
    //商品类别列表
    private List<GoodsListBean.DataEntity.GoodscatrgoryEntity> goodscatrgoryEntities = new ArrayList<>();
    //商品列表
    private List<GoodsListBean.DataEntity.GoodscatrgoryEntity.GoodsitemEntity> goodsitemEntities = new ArrayList<>();
    private View view;
//    private TextView table_btn;
    private RecyclerView mGoodsCateGoryList;
    private RecyclerView recyclerView;
    private PersonAdapter personAdapter;
    private BigramHeaderAdapter headerAdapter;
    //分类的适配器
    private RecycleGoodsCategoryListAdapter mGoodsCategoryListAdapter;
    //recycle的线性布局的样式
    private LinearLayoutManager mLinearLayoutManager;
    //存储含有标题的第一个含有商品类别名称的条目的下表
    private List<Integer> titlePois = new ArrayList<>();
    //上一个标题的小标位置
    private int lastTitlePoi;
    private StickyHeadersItemDecoration top;
    private ZLoadingDialog loading_dailog;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        if (view == null) {
            view = inflater.inflate(R.layout.fragment_order_dc, null);
        }
        initView(view);
        initListenner();
        initLoadingData();
        return view;
    }

    /**
     * 网若获取数据
     */
    private void initLoadingData() {
        /**
         * profile_id ：用户id
         * branchid：分店id
         */
        Map<String, String> map = MapUtilsSetParam.getMap(getContext());
        try {
            map.put("opp", "goodslist");
            map.put("branchid", Utils.getBranch(getContext()).getId());
            map.put("profile_id", "-1");
        } catch (Exception e) {
            e.printStackTrace();

        }
        loading_dailog = LoadingUtils.getDailog(getContext(), Color.RED, "获取商品数据中。。。。");
        loading_dailog.show();
        NetUtils.netWorkByMethodPost(getContext(), map, IpConfig.URL, handler, ContantData.GETGOODSINFO);
    }

    private void initListenner() {

    }

    /**
     * view的实力化
     *
     * @param view
     */
    private void initView(View view) {
//        table_btn = view.findViewById(R.id.checktable);
        mGoodsCateGoryList = (RecyclerView) view.findViewById(R.id.goods_category_list);
        recyclerView = (RecyclerView) view.findViewById(R.id.goods_recycleView);
    }

    /**
     * 启动Activity的返回页面
     *
     * @param requestCode
     * @param resultCode
     * @param data
     */
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == ContantData.REQUESTCODE && resultCode == RESULT_OK) {
            TableDBean.DataBean tableBean = (TableDBean.DataBean) data.getSerializableExtra("tableInfo");
            String peoplecount = data.getStringExtra("peoplecount");
            Utils.getACache(getContext()).put("peoplecount", peoplecount);
            Utils.getACache(getContext()).put("tableBean", tableBean);
            Message message = new Message();
            message.what = 10;
            message.obj = tableBean;
            handler.sendMessage(message);
        }
    }

    Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                //获取到了桌子的数据和人数
                case 10:
                    String peoplecount = Utils.getACache(getContext()).getAsString("peoplecount");
                    TableDBean.DataBean tableBean = (TableDBean.DataBean) msg.obj;
                    break;
                case ContantData.GETGOODSINFO:
                    String goodsinfojson = (String) msg.obj;
                    try {
                        int f = 0;
                        int l = 0;
                        boolean isFirst;
                        JSONObject jsob = new JSONObject(goodsinfojson);
                        int goodsnum = jsob.getInt("goodsnum");
                        JSONArray goodslist = jsob.getJSONArray("goodslist");
//                       //获取分类第一级：：：
                        List<GoodsListBean.DataEntity.GoodscatrgoryEntity> list = new ArrayList<>();
                        for (int i = 0; i < goodslist.length(); i++) {
                            isFirst = true;
                            GoodsListBean.DataEntity.GoodscatrgoryEntity onBean = new GoodsListBean.DataEntity.GoodscatrgoryEntity();
                            JSONObject oneobj = goodslist.getJSONObject(i);
                            onBean.setName(oneobj.getString("name"));
                            onBean.setC_id(oneobj.getInt("id"));
                            List<GoodsListBean.DataEntity.GoodscatrgoryEntity.GoodsitemEntity> onelist = new ArrayList<>();
                            JSONArray gooslist = oneobj.getJSONArray("gooslist");
                            for (int j = 0; j < gooslist.length(); j++) {
                                GoodsListBean.DataEntity.GoodscatrgoryEntity.GoodsitemEntity twoBean = new GoodsListBean.DataEntity.GoodscatrgoryEntity.GoodsitemEntity();
                                JSONObject twoObj = gooslist.getJSONObject(j);
                                twoBean.setHasoption(twoObj.getString("hasoption"));
                                twoBean.setId(twoObj.getString("id"));
                                twoBean.setMarketprice(twoObj.getString("marketprice"));
                                twoBean.setThumb(twoObj.getString("thumb"));
                                twoBean.setTotal(twoObj.getString("total"));
                                twoBean.setTitle(twoObj.getString("title"));
                                twoBean.setYuanjia(twoObj.getString("yuanjia"));
                                twoBean.setTotal_in_cart(twoObj.getInt("total_in_cart"));
                                List<GoodsListBean.DataEntity.GoodscatrgoryEntity.GoodsitemEntity.OptionsBean> option = new ArrayList<>();
                                JSONArray options = twoObj.getJSONArray("options");
                                for (int k = 0; k < options.length(); k++) {
                                    GoodsListBean.DataEntity.GoodscatrgoryEntity.GoodsitemEntity.OptionsBean ob = new GoodsListBean.DataEntity.GoodscatrgoryEntity.GoodsitemEntity.OptionsBean();
                                    JSONObject jsonObject = options.getJSONObject(k);
                                    ob.setOptionid(jsonObject.getInt("optionid"));
                                    ob.setOptionname(jsonObject.getString("optionname"));
                                    option.add(ob);
                                }
                                if (isFirst) {
                                    titlePois.add(l);
                                    isFirst = false;
                                }
                                twoBean.setOptions(option);
                                onelist.add(twoBean);
                                l++;
                                twoBean.setGb_id(f);
                                goodsitemEntities.add(twoBean);
                            }
                            onBean.setGoodsitem(onelist);
                            goodscatrgoryEntities.add(onBean);
                            f++;
                        }
                        initData(list);
                    } catch (Exception e) {
                        e.printStackTrace();
                    } finally {
                        loading_dailog.dismiss();
                    }
                    break;
            }
        }
    };

    @Override
    public void onHeaderClick(View header, long headerId) {
//        头部的点击事件
    }

    @Override
    public void onNumChange() {

    }

    /**
     * 处理滑动 是两个ListView联动
     * 需要改动
     */
    private class MyOnGoodsScrollListener implements AbsListView.OnScrollListener {
        @Override
        public void onScrollStateChanged(AbsListView view, int scrollState) {

        }

        @Override
        public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
            if (!(lastTitlePoi == goodsitemEntities.get(firstVisibleItem).getGb_id())) {
                lastTitlePoi = goodsitemEntities.get(firstVisibleItem).getGb_id();
                mGoodsCategoryListAdapter.setCheckPosition(goodsitemEntities.get(firstVisibleItem).getGb_id());
                mGoodsCategoryListAdapter.notifyDataSetChanged();
            }
        }
    }

    //这个是有用的需要改动
    private void initData(List<GoodsListBean.DataEntity.GoodscatrgoryEntity> list) {
        mGoodsCategoryListAdapter = new RecycleGoodsCategoryListAdapter(goodscatrgoryEntities, getActivity());
        mGoodsCateGoryList.setLayoutManager(new LinearLayoutManager(getContext()));
        mGoodsCateGoryList.setAdapter(mGoodsCategoryListAdapter);
        mGoodsCategoryListAdapter.setOnItemClickListener(new RecycleGoodsCategoryListAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(View view, int position) {
                recyclerView.scrollToPosition(titlePois.get(position) + position + 2);
                mGoodsCategoryListAdapter.setCheckPosition(position);
            }
        });
        mLinearLayoutManager = new LinearLayoutManager(getActivity(), LinearLayoutManager.VERTICAL, false);
        recyclerView.setLayoutManager(mLinearLayoutManager);
        personAdapter = new PersonAdapter(getActivity(), goodsitemEntities, goodscatrgoryEntities,handler);
        personAdapter.setmActivity(getActivity());
        headerAdapter = new BigramHeaderAdapter(getActivity(), goodsitemEntities, goodscatrgoryEntities);
        top = new StickyHeadersBuilder()
                .setAdapter(personAdapter)
                .setRecyclerView(recyclerView)
                .setStickyHeadersAdapter(headerAdapter)
                .setOnHeaderClickListener(this)
                .build();
        recyclerView.addItemDecoration(top);
        recyclerView.setAdapter(personAdapter);
        recyclerView.setOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);
            }

            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
                for (int i = 0; i < titlePois.size(); i++) {
                    if (mLinearLayoutManager.findFirstVisibleItemPosition() >= titlePois.get(i)) {
                        mGoodsCategoryListAdapter.setCheckPosition(i);
                    }
                }
            }
        });

        loading_dailog.dismiss();
    }

    /**
     * 添加 或者  删除  商品发送的消息处理
     *
     * @param event
     */
    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onMessageEvent(GoodsListEvent event) {
        if (event.buyNums.length > 0) {
            for (int i = 0; i < event.buyNums.length; i++) {
                goodscatrgoryEntities.get(i).setBugNum(event.buyNums[i]);
            }
            mGoodsCategoryListAdapter.changeData(goodscatrgoryEntities);
        }
    }



    @Override
    public void onDestroy() {
        super.onDestroy();

    }

    /**
     * 清空数据
     *
     * @param mess
     */
    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onAcceptMessage(AcceptMessage mess) {
        personAdapter.cleardata();
        for (int i = 0; i < goodscatrgoryEntities.size(); i++) {
            goodscatrgoryEntities.get(i).setBugNum(0);
        }
        mGoodsCategoryListAdapter.changeData(goodscatrgoryEntities);
    }
}
