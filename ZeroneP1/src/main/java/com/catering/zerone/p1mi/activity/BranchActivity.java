package com.catering.zerone.p1mi.activity;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.alibaba.fastjson.JSON;
import com.catering.zerone.p1mi.MainActivity;
import com.catering.zerone.p1mi.R;
import com.catering.zerone.p1mi.adapter.branch.BranchSelectedAdapter;
import com.catering.zerone.p1mi.baseacticity.BaseAppActivity;
import com.catering.zerone.p1mi.contanst.ContantData;
import com.catering.zerone.p1mi.contanst.IpConfig;
import com.catering.zerone.p1mi.db.impl.BranchDao;
import com.catering.zerone.p1mi.db.impl.WorkerTabeDao;
import com.catering.zerone.p1mi.domain.Branch;
import com.catering.zerone.p1mi.domain.Worker;
import com.catering.zerone.p1mi.utils.LoadingUtils;
import com.catering.zerone.p1mi.utils.MapUtilsSetParam;
import com.catering.zerone.p1mi.utils.NetUtils;
import com.catering.zerone.p1mi.utils.Utils;
import com.githang.statusbar.StatusBarCompat;
import com.zyao89.view.zloading.ZLoadingDialog;

import org.json.JSONArray;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by on 2018/1/23 0023 09 58.
 * Author  LiuXingWen
 */

public class BranchActivity extends BaseAppActivity {
    //展示分店用的列表控件
    private RecyclerView recyclerView;
    //存放分店数据的集合
    private List<Branch> branchList;
    private BranchSelectedAdapter fdAdapter;
    private BranchDao branchDao;
    private ZLoadingDialog dailog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_branch);
        StatusBarCompat.setStatusBarColor(this, Color.parseColor("#ffffff"));
        branchDao = new BranchDao(this);
        loadingData();
        initView();

    }

    /**
     *
     */
    private void initView() {
        branchList = new ArrayList<Branch>();
        recyclerView = (RecyclerView) findViewById(R.id.branch_recycle);
        GridLayoutManager manager = new GridLayoutManager(this, 1);
        recyclerView.setLayoutManager(manager);
    }
    /**
     * 网络请求获取分店的数据 并展示数据
     */
    private void loadingData() {
        dailog = LoadingUtils.getDailog(BranchActivity.this, Color.RED, "正在获取分店数据。。。。");
        dailog.show();
        //这个是新的 有待测试
        Map<String, String> map = MapUtilsSetParam.getMap(this);
        map.put("opp", "getbranch");
        NetUtils.netWorkByMethodPost(this, map, IpConfig.URL, handler, ContantData.GETBRANCHRESPONSE);
    }


    Handler handler =new Handler(){
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what){
                case ContantData.GETBRANCHRESPONSE:
                    String respo = (String) msg.obj;
                   try {
                        //这个由于session会过期  所以登录后只有获取了分店数据后才算登录成功。
                        branchList = JSON.parseArray(respo, Branch.class);
                        if (branchList.size() == 0) {
                            Toast.makeText(BranchActivity.this, "该店暂无分店", Toast.LENGTH_SHORT).show();
                            return;
                        }
                        fdAdapter = new BranchSelectedAdapter(branchList);
                        recyclerView.setAdapter(fdAdapter);
                        fdAdapter.notifyDataSetChanged();
                        setRecyclerViewEvent(branchList);
                        //把数据插入数据库中
                        insterinto(branchList);
                   }
                    catch (Exception e) {
                    //出现这个错误是session的值过期 登录后获取不到值 所以我们需要重新登录
                    Toast.makeText(BranchActivity.this, "获取数据失败，别灰心请重新登录即可。", Toast.LENGTH_SHORT).show();
                    BranchActivity.this.finish();
                }
                    break;

                case  ContantData.GETWORKERRESPONSE :
                    String wkJson = (String) msg.obj;
                    Log.i("TAG", "handleMessage: "+wkJson);
                    List<Worker> workerList = new ArrayList<>();
                    try {
                        JSONArray array = new JSONArray(wkJson);
                        if (array!=null){
                            for (int i = 0; i < array.length(); i++) {
                                Worker worker = new Worker();
                                worker.setName(array.getJSONObject(i).getString("name"));
                                worker.setWorkerid(array.getJSONObject(i).getString("workerid"));
                                worker.setReception_qr(array.getJSONObject(i).getString("reception_qr"));
                                String avatar = array.getJSONObject(i).getString("avatar");
                                if ("-1".equals(avatar)){
                                    //默认图片
                                    worker.setIcon_thumb("http://img5.imgtn.bdimg.com/it/u=2021991128,2296584601&fm=27&gp=0.jpg");
                                }else {
                                    worker.setIcon_thumb(avatar);
                                }
                                workerList.add(worker);
                            }
                            insertIntoWorkerTable(workerList);
                            Intent intent = new Intent(BranchActivity.this, CheckWaiterActivity.class);
                            startActivity(intent);
                            finish();
                        }else {
                            Toast.makeText(BranchActivity.this, "暂无工作人员，请去后台设置工作人员后再登录", Toast.LENGTH_SHORT).show();
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    break;
            }
        }
    };
    /**
     * 选择分店的点击事件
     * @param list
     */
    private void setRecyclerViewEvent(final List<Branch> list) {
        fdAdapter.setOnItemClickListener(new BranchSelectedAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(View view, int position) {
                String id = list.get(position).getId();
                Branch branch = list.get(position);
                Utils.getACache(BranchActivity.this).put("branch", branch);
                //我想在获取分店时把接待员获取 并存入数据库
                //获取店铺人员的数据
                Map<String, String> map = MapUtilsSetParam.getMap(BranchActivity.this);
                map.put("opp", "getworker");
                map.put("branchid",branch.getId());
                NetUtils.netWorkByMethodPost(BranchActivity.this, map, IpConfig.URL, handler, ContantData.GETWORKERRESPONSE);
            }
        });
    }

    /**
     * 把数据放入分店数据表中
     *
     * @param branchList
     */
    private void insterinto(List<Branch> branchList) throws Exception {
        //每次添加前给数据清空了
        branchDao.del();
        for (int i = 0; i < branchList.size(); i++) {
            branchDao.add(branchList.get(i));
        }
        dailog.dismiss();
    }

    /**
     * 把数据插入数据库
     * @param workerList
     */
    private void insertIntoWorkerTable(List<Worker> workerList) throws Exception {
        WorkerTabeDao wtd= new WorkerTabeDao(BranchActivity.this);
        wtd.deltable();
        for (int i=0;i<workerList.size();i++){
            wtd.addWorker(workerList.get(i));
        }
    }
}
