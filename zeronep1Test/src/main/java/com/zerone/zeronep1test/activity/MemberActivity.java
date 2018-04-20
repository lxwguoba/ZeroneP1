package com.zerone.zeronep1test.activity;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import com.alibaba.fastjson.JSON;
import com.zerone.zeronep1test.R;
import com.zerone.zeronep1test.baseacticity.BaseAppActivity;
import com.zerone.zeronep1test.branch.ListMemberlistItem;
import com.zerone.zeronep1test.contanst.IpConfig;
import com.zerone.zeronep1test.db.impl.SessionTabeDao;
import com.zerone.zeronep1test.domain.HuiYuanInfoBean;
import com.zerone.zeronep1test.domain.HuiYuanInfoBean_XianChang;
import com.zerone.zeronep1test.utils.LoadingUtils;
import com.zerone.zeronep1test.utils.MapUtilsSetParam;
import com.zerone.zeronep1test.utils.NetUtils;
import com.zerone.zeronep1test.utils.Utils;
import com.zyao89.view.zloading.ZLoadingDialog;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Created by on 2018/2/2 0002 16 15.
 * Author  LiuXingWen
 */

public class MemberActivity extends BaseAppActivity {

    private TextView showtext;
    private Intent intent;
    private List<HuiYuanInfoBean.MemberlistBean> list;
    private int status;
    private ListView memberlist;
    private ListMemberlistItem madapter;
    private ZLoadingDialog dailog;
    private EditText userandphone_edt;
    private TextView sure_user;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_member);
        dailog = LoadingUtils.getDailog(MemberActivity.this, Color.RED, "获取会员中。。。。");

        intent = getIntent();
        list = new ArrayList<>();
        initView();
        LoadingData();
        initListenner();
    }

    private void initListenner() {

        sure_user.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String nameandphone = userandphone_edt.getText().toString().trim();
                if (nameandphone!=null&&nameandphone.length()>0){
                    dailog.show();
                    Map<String, String> map = MapUtilsSetParam.getMap(MemberActivity.this);
                    map.put("opp", "getmember");
                    map.put("memberid","" );
                    map.put("keyword",nameandphone);
                    NetUtils.netWorkByMethodPost(MemberActivity.this, map, IpConfig.URL, handler, 13);
                }
            }
        });


        memberlist.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                HuiYuanInfoBean.MemberlistBean hmb = new HuiYuanInfoBean.MemberlistBean();
                hmb.setId(list.get(position).getId());
                hmb.setWeid(list.get(position).getWeid());
                hmb.setBranchid(list.get(position).getBranchid());
                hmb.setShareid(list.get(position).getShareid());
                hmb.setFrom_user(list.get(position).getFrom_user());
                hmb.setRealname(list.get(position).getRealname());
                hmb.setMobile(list.get(position).getMobile());
                hmb.setMyqq(list.get(position).getMyqq());
                hmb.setCommission(list.get(position).getCommission());
                hmb.setZhifu(list.get(position).getZhifu());
                hmb.setContent(list.get(position).getContent());
                hmb.setCreatetime(list.get(position).getCreatetime());
                hmb.setFlag(list.get(position).getFlag());
                hmb.setLevelid(list.get(position).getLevelid());
                hmb.setFlagtime(list.get(position).getFlagtime());
                hmb.setCredit2(list.get(position).getCredit2());
                hmb.setCredit1(list.get(position).getCredit1());
                hmb.setStatus(list.get(position).getStatus());
                hmb.setClickcount(list.get(position).getClickcount());
                hmb.setMbbinded(list.get(position).getMbbinded());
                hmb.setBirthdate(list.get(position).getBirthdate());
                hmb.setRemark(list.get(position).getRemark());
                hmb.setIsworking(list.get(position).getIsworking());
                hmb.setAvatar(list.get(position).getAvatar());
                hmb.setTitle(list.get(position).getTitle());
                //把返回数据存入Intent
                intent.putExtra("huiyuaninfo_data",hmb);
                //设置返回数据
                MemberActivity.this.setResult(RESULT_OK, intent);
                //关闭Activity
                MemberActivity.this.finish();
                Utils.getACache(MemberActivity.this).remove("huiyuaninf");
                Utils.getACache(MemberActivity.this).remove("huiyuanifoxianchang");
            }
        });
    }

    private void initView() {
        memberlist = (ListView) findViewById(R.id.memberlist);
        madapter = new ListMemberlistItem(this,list);
        memberlist.setAdapter(madapter);
        userandphone_edt = (EditText) findViewById(R.id.userandphone_edt);
        sure_user = (TextView) findViewById(R.id.sure_user);
    }


    private void LoadingData() {
        //0为会员的展示 1为现场顾客
        status = intent.getIntExtra("status",0);
        if (status ==0){
            SessionTabeDao sessionTabeDao  = new SessionTabeDao(MemberActivity.this);
            Map<String, String> map = MapUtilsSetParam.getMap(MemberActivity.this);
            map.put("opp", "getmember");
            map.put("memberid","" );
            map.put("realname","");
            map.put("mobile", "");
            try {
                String url= IpConfig.URL+"act=module&"+"name=bj_qmxk&"+"do=app_api&"+"op=android_app&"+"opp=getmember&"+"session="+sessionTabeDao.getSession()+"&memberid="
                        +""+"&realname="+""+"&mobile="+"";
                Log.i("URL",url);
            } catch (Exception e) {
                e.printStackTrace();
            }
            dailog.show();
            NetUtils.netWorkByMethodPost(this, map, IpConfig.URL, handler, 10);
        }else {
            Map<String, String> xianchang = MapUtilsSetParam.getMap(MemberActivity.this);
            xianchang.put("opp", "getviewing");
            xianchang.put("branchid", Utils.getBranch(MemberActivity.this).getId());
            NetUtils.netWorkByMethodPost(this, xianchang, IpConfig.URL,  handler,11);
            dailog.show();
        }

    }


    Handler  handler  = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what){
                case 10:
                    String  huiyuanifo= (String) msg.obj;
                    Utils.getACache(MemberActivity.this).put("huiyuanifo",huiyuanifo);
                    HuiYuanInfoBean huiYuanInfo = JSON.parseObject(huiyuanifo, HuiYuanInfoBean.class);
                    if (huiYuanInfo.getMembernum()>0){
                        List<HuiYuanInfoBean.MemberlistBean> hm =huiYuanInfo.getMemberlist();
                        for (int i=0;i<hm.size();i++){
                            HuiYuanInfoBean.MemberlistBean hmb = new HuiYuanInfoBean.MemberlistBean();
                            hmb.setId(hm.get(i).getId());
                            hmb.setWeid(hm.get(i).getWeid());
                            hmb.setBranchid(hm.get(i).getBranchid());
                            hmb.setShareid(hm.get(i).getShareid());
                            hmb.setFrom_user(hm.get(i).getFrom_user());
                            hmb.setRealname(hm.get(i).getRealname());
                            hmb.setMobile(hm.get(i).getMobile());
                            hmb.setMyqq(hm.get(i).getMyqq());
                            hmb.setCommission(hm.get(i).getCommission());
                            hmb.setZhifu(hm.get(i).getZhifu());
                            hmb.setContent(hm.get(i).getContent());
                            hmb.setCreatetime(hm.get(i).getCreatetime());
                            hmb.setFlag(hm.get(i).getFlag());
                            hmb.setLevelid(hm.get(i).getLevelid());
                            hmb.setFlagtime(hm.get(i).getFlagtime());
                            hmb.setCredit2(hm.get(i).getCredit2());
                            hmb.setCredit1(hm.get(i).getCredit1());
                            hmb.setStatus(hm.get(i).getStatus());
                            hmb.setClickcount(hm.get(i).getClickcount());
                            hmb.setMbbinded(hm.get(i).getMbbinded());
                            hmb.setBirthdate(hm.get(i).getBirthdate());
                            hmb.setRemark(hm.get(i).getRemark());
                            hmb.setIsworking(hm.get(i).getIsworking());
                            hmb.setAvatar(hm.get(i).getAvatar());
                            hmb.setTitle(hm.get(i).getTitle());
                            list.add(hmb);
                        }
                    }
                    madapter.notifyDataSetChanged();
                    dailog.dismiss();
                    break;
                case 11:
                    String  huiyuanifoxianchang= (String) msg.obj;
                    Log.d("HuiYuanInfo", "member===="+huiyuanifoxianchang);
                    Utils.getACache(MemberActivity.this).put("huiyuanifoxianchang",huiyuanifoxianchang);
                    HuiYuanInfoBean_XianChang huiYuanInfoxianchang = JSON.parseObject( huiyuanifoxianchang,HuiYuanInfoBean_XianChang.class);
                    List<HuiYuanInfoBean_XianChang.MemberlistBean> hm =huiYuanInfoxianchang .getMemberlist();
                    if (huiYuanInfoxianchang.getResult()>0){
                        for (int i=0;i<hm.size();i++){
                            HuiYuanInfoBean.MemberlistBean hmb = new HuiYuanInfoBean.MemberlistBean();
                            hmb.setId(hm.get(i).getId());
                            hmb.setWeid(hm.get(i).getWeid());
                            hmb.setBranchid(hm.get(i).getBranchid());
                            hmb.setShareid(hm.get(i).getShareid());
                            hmb.setFrom_user(hm.get(i).getFrom_user());
                            hmb.setRealname(hm.get(i).getRealname());
                            hmb.setMobile(hm.get(i).getMobile());
                            hmb.setMyqq(hm.get(i).getMyqq());
                            hmb.setCommission(hm.get(i).getCommission());
                            hmb.setZhifu(hm.get(i).getZhifu());
                            hmb.setContent(hm.get(i).getContent());
                            hmb.setCreatetime(hm.get(i).getCreatetime());
                            hmb.setFlag(hm.get(i).getFlag());
                            hmb.setLevelid(hm.get(i).getLevelid());
                            hmb.setFlagtime(hm.get(i).getFlagtime());
                            hmb.setCredit2(hm.get(i).getCredit2());
                            hmb.setCredit1(hm.get(i).getCredit1());
                            hmb.setStatus(hm.get(i).getStatus());
                            hmb.setClickcount(hm.get(i).getClickcount());
                            hmb.setMbbinded(hm.get(i).getMbbinded());
                            hmb.setBirthdate(hm.get(i).getBirthdate());
                            hmb.setRemark(hm.get(i).getRemark());
                            hmb.setIsworking(hm.get(i).getIsworking());
                            hmb.setAvatar(hm.get(i).getAvatar());
                            hmb.setTitle(hm.get(i).getTitle());
                            list.add(hmb);
                        }
                    }
                  madapter.notifyDataSetChanged();
                  dailog.dismiss();
                    break;

                //搜
                case 13:
                    String searchJson= (String) msg.obj;
                    list.clear();
                    HuiYuanInfoBean huiYuanInf = JSON.parseObject(searchJson, HuiYuanInfoBean.class);
                    if (huiYuanInf.getMembernum()>0){
                        List<HuiYuanInfoBean.MemberlistBean> h =huiYuanInf.getMemberlist();
                        for (int i=0;i<h.size();i++){
                            HuiYuanInfoBean.MemberlistBean hmb = new HuiYuanInfoBean.MemberlistBean();
                            hmb.setId(h.get(i).getId());
                            hmb.setWeid(h.get(i).getWeid());
                            hmb.setBranchid(h.get(i).getBranchid());
                            hmb.setShareid(h.get(i).getShareid());
                            hmb.setFrom_user(h.get(i).getFrom_user());
                            hmb.setRealname(h.get(i).getRealname());
                            hmb.setMobile(h.get(i).getMobile());
                            hmb.setMyqq(h.get(i).getMyqq());
                            hmb.setCommission(h.get(i).getCommission());
                            hmb.setZhifu(h.get(i).getZhifu());
                            hmb.setContent(h.get(i).getContent());
                            hmb.setCreatetime(h.get(i).getCreatetime());
                            hmb.setFlag(h.get(i).getFlag());
                            hmb.setLevelid(h.get(i).getLevelid());
                            hmb.setFlagtime(h.get(i).getFlagtime());
                            hmb.setCredit2(h.get(i).getCredit2());
                            hmb.setCredit1(h.get(i).getCredit1());
                            hmb.setStatus(h.get(i).getStatus());
                            hmb.setClickcount(h.get(i).getClickcount());
                            hmb.setMbbinded(h.get(i).getMbbinded());
                            hmb.setBirthdate(h.get(i).getBirthdate());
                            hmb.setRemark(h.get(i).getRemark());
                            hmb.setIsworking(h.get(i).getIsworking());
                            hmb.setAvatar(h.get(i).getAvatar());
                            hmb.setTitle(h.get(i).getTitle());
                            list.add(hmb);
                        }
                    }else{
                        if (status==0){
                            String huiyuaninfo = Utils.getACache(MemberActivity.this).getAsString("huiyuaninfo");
                            setlistdata(huiyuaninfo);
                        }else{
                            String   huiyuanifoxianchan=Utils.getACache(MemberActivity.this).getAsString("huiyuanifoxianchang");
                            setlistdata(huiyuanifoxianchan);
                        }
                    }
                  madapter.notifyDataSetChanged();
                  dailog.dismiss();
                    break;
            }
        }
    };


    public void  setlistdata(String listjson){
        HuiYuanInfoBean huiYuanInfo = JSON.parseObject(listjson, HuiYuanInfoBean.class);
        if (huiYuanInfo.getMembernum()>0){
            List<HuiYuanInfoBean.MemberlistBean> hm =huiYuanInfo.getMemberlist();
            for (int i=0;i<hm.size();i++){
                HuiYuanInfoBean.MemberlistBean hmb = new HuiYuanInfoBean.MemberlistBean();
                hmb.setId(hm.get(i).getId());
                hmb.setWeid(hm.get(i).getWeid());
                hmb.setBranchid(hm.get(i).getBranchid());
                hmb.setShareid(hm.get(i).getShareid());
                hmb.setFrom_user(hm.get(i).getFrom_user());
                hmb.setRealname(hm.get(i).getRealname());
                hmb.setMobile(hm.get(i).getMobile());
                hmb.setMyqq(hm.get(i).getMyqq());
                hmb.setCommission(hm.get(i).getCommission());
                hmb.setZhifu(hm.get(i).getZhifu());
                hmb.setContent(hm.get(i).getContent());
                hmb.setCreatetime(hm.get(i).getCreatetime());
                hmb.setFlag(hm.get(i).getFlag());
                hmb.setLevelid(hm.get(i).getLevelid());
                hmb.setFlagtime(hm.get(i).getFlagtime());
                hmb.setCredit2(hm.get(i).getCredit2());
                hmb.setCredit1(hm.get(i).getCredit1());
                hmb.setStatus(hm.get(i).getStatus());
                hmb.setClickcount(hm.get(i).getClickcount());
                hmb.setMbbinded(hm.get(i).getMbbinded());
                hmb.setBirthdate(hm.get(i).getBirthdate());
                hmb.setRemark(hm.get(i).getRemark());
                hmb.setIsworking(hm.get(i).getIsworking());
                hmb.setAvatar(hm.get(i).getAvatar());
                hmb.setTitle(hm.get(i).getTitle());
                list.add(hmb);
            }
        }
    }
}
