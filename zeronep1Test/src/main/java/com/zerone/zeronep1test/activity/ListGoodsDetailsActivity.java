package com.zerone.zeronep1test.activity;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.TextView;
import android.widget.Toast;
import com.alibaba.fastjson.JSON;
import com.bumptech.glide.Glide;
import com.zerone.zeronep1test.R;
import com.zerone.zeronep1test.Util;
import com.zerone.zeronep1test.baseacticity.BaseAppActivity;
import com.zerone.zeronep1test.branch.ListGoodsDetailsAdapter;
import com.zerone.zeronep1test.contanst.ContantData;
import com.zerone.zeronep1test.contanst.IpConfig;
import com.zerone.zeronep1test.domain.GoodsBeanUp;
import com.zerone.zeronep1test.domain.GoodsUp;
import com.zerone.zeronep1test.domain.HuiYuanInfoBean;
import com.zerone.zeronep1test.domain.PrintBean;
import com.zerone.zeronep1test.domain.PrintItem;
import com.zerone.zeronep1test.domain.TableDBean;
import com.zerone.zeronep1test.domain.TableItem;
import com.zerone.zeronep1test.domain.Worker;
import com.zerone.zeronep1test.event.AcceptMessage;
import com.zerone.zeronep1test.event.MessageEvent;
import com.zerone.zeronep1test.utils.AidlUtil;
import com.zerone.zeronep1test.utils.LoadingUtils;
import com.zerone.zeronep1test.utils.MapUtilsSetParam;
import com.zerone.zeronep1test.utils.NetUtils;
import com.zerone.zeronep1test.utils.Utils;
import com.zerone.zeronep1test.utils.UtilsTime;
import com.zyao89.view.zloading.ZLoadingDialog;
import org.greenrobot.eventbus.EventBus;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import de.hdodenhof.circleimageview.CircleImageView;

//import com.catering.zerone.p1mi.event.MessageEvent;

/**
 * Created by on 2018/1/24 0024 18 44.
 * Author  LiuXingWen
 */

public class ListGoodsDetailsActivity extends BaseAppActivity {
    private ListView listView;
    private TextView usegoods;
    private TextView tablename;
    private TextView shoptotalmoney;
    private TextView shopCount;
    private MessageEvent mess;
    private String peoplecount;
    private TableDBean.DataBean tableBean;
    private List<GoodsBeanUp> list;
    private ListGoodsDetailsAdapter lgdAdapter;
    private ZLoadingDialog dailog;
    private Button sure_order;
    private ImageView check_member;
    private LinearLayout parentview;
    private PopupWindow mPopupWindow;
    private View mContentView;
    private Worker worker;
    private Button member;
    private Button x_member;
    private CircleImageView viphead;
    private TextView vipname;
    private LinearLayout vipinfo;
    private EditText beizhu;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_listgoodsdetails);
        worker = (Worker) Utils.getACache(ListGoodsDetailsActivity.this).getAsObject("waiter");
        Intent intent = getIntent();
        submitOrder();
        mess = (MessageEvent) intent.getSerializableExtra("goods");
        peoplecount = Utils.getACache(this).getAsString("peoplecount");
        tableBean = (TableDBean.DataBean) Utils.getACache(this).getAsObject("tableBean");
        initView();
        initListenner();

    }

    private void initListenner() {
        check_member.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //启动对话框
                setPopWindow();

            }
        });


        member.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent  = new Intent(ListGoodsDetailsActivity.this,MemberActivity.class);
                 intent.putExtra("status",0);
                 startActivityForResult(intent, ContantData.GETVIPINFO);
                mPopupWindow.dismiss();
            }
        });

        x_member.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent  = new Intent(ListGoodsDetailsActivity.this,MemberActivity.class);
                intent.putExtra("status",1);
                startActivityForResult(intent,ContantData.GETVIPINFO);
                mPopupWindow.dismiss();
            }
        });
    }

    private void initView() {
        beizhu = (EditText) findViewById(R.id.beizhu);
        vipinfo = (LinearLayout) findViewById(R.id.vipinfo);
        viphead = (CircleImageView) findViewById(R.id.viphead);
        vipname = (TextView) findViewById(R.id.vipname);
        parentview = (LinearLayout) findViewById(R.id.parentview);
        check_member = (ImageView) findViewById(R.id.check_member);
        listView = (ListView) findViewById(R.id.goodslist);
        usegoods = (TextView) findViewById(R.id.usegoods);
        tablename = (TextView) findViewById(R.id.tablename);
        shoptotalmoney = (TextView) findViewById(R.id.shoptotalmoney);
        sure_order = (Button) findViewById(R.id.sure_order);
        shopCount = (TextView) findViewById(R.id.shopCount);
        if (mess != null) {
            shoptotalmoney.setText("￥" + mess.price + "");
            shopCount.setText(mess.num + "");
            list = mess.list;
            lgdAdapter = new ListGoodsDetailsAdapter(this, list);
            listView.setAdapter(lgdAdapter);
        }
        if (peoplecount != null) {
            usegoods.setText(peoplecount + "");
        }
        if (tableBean != null) {
            tablename.setText(tableBean.getTablename());
        }

        sure_order.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sure_order.setEnabled(false);
                dailog.show();
                submitIsOk();
            }
        });
        //=========================================================
        if (mContentView==null){
            mContentView = LayoutInflater.from(this).inflate(R.layout.activity_member_selected_pop,null);
        }
        ImageView imageView  = (ImageView) mContentView.findViewById(R.id.qr_code);
         Log.i("URL",worker.getReception_qr());
         if (worker!=null){
             Glide.with(this).load(worker.getReception_qr()).placeholder(R.mipmap.icon_app_logo).into(imageView);
         }

         member = (Button) mContentView.findViewById(R.id.member);
        x_member = (Button) mContentView.findViewById(R.id.x_member);
         //=========================================================
    }

    /**
     * 本店散客用在选择分店时就决定了（待考虑）
     */
    public void submitOrder() {
        dailog = LoadingUtils.getDailog(ListGoodsDetailsActivity.this, Color.RED, "提交数据中。。。。");
        dailog.show();
        Map<String, String> mapSanke = MapUtilsSetParam.getMap(ListGoodsDetailsActivity.this);
        mapSanke.put("branchid", Utils.getBranch(ListGoodsDetailsActivity.this).getId());
        mapSanke.put("opp", "getsanke");
        NetUtils.netWorkByMethodPost(ListGoodsDetailsActivity.this, mapSanke, IpConfig.URL, handler, ContantData.GETSANKE);
    }


    Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case ContantData.SUBMITORDER:
                    String jsons = (String) msg.obj;
                    try {
                        JSONObject jsonObject = new JSONObject(jsons);
                        Log.i("URL",jsonObject.toString());
                        int status = jsonObject.getInt("status");
                        if (status == 0) {
                            //下单失败
                            Toast.makeText(ListGoodsDetailsActivity.this, jsonObject.getString("data"), Toast.LENGTH_SHORT).show();
                        } else if (status == 1) {
                            //下单成功
                            Toast.makeText(ListGoodsDetailsActivity.this, "下单成功", Toast.LENGTH_SHORT).show();
                            EventBus.getDefault().post(new AcceptMessage("购物清单",0));
                            Intent intent = new Intent();
                            intent.putExtra("code",200);
                            setResult(RESULT_OK, intent);
                            String orderid=jsonObject.getString("data");
                            //获取订单详情打印订单小票
                             printandgetorder(orderid);
                        }
                        Utils.getACache(ListGoodsDetailsActivity.this).remove("peoplecount");
                        Utils.getACache(ListGoodsDetailsActivity.this).remove("tableBean");
                        Utils.getACache(ListGoodsDetailsActivity.this).remove("optionsbean");

                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                    dailog.dismiss();
                    sure_order.setEnabled(true);
                    break;

                case ContantData.GETSANKE:
                    String sankeJson = (String) msg.obj;
                    try {
                        JSONObject jsonObject = new JSONObject(sankeJson);
                        String id = jsonObject.getString("id");
                        //需要改动 刘兴文改
                        Utils.getACache(ListGoodsDetailsActivity.this).put("sanke", id);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    dailog.dismiss();
                    break;
                case ContantData.GETORDERLISTDETAILS:
                    String orderdetailsJson = (String) msg.obj;
                    Log.i("TAG", "orderjson7777=" + orderdetailsJson);
                    try {
                        PrintBean printBean  = new PrintBean();
                        JSONObject    json = new JSONObject(orderdetailsJson);
                        //订单编号
                        String ordersn = json.getJSONObject("data").getJSONObject("item").getString("ordersn");
                        printBean.setOrdersn(ordersn);
                        String createTime = json.getJSONObject("data").getJSONObject("item").getString("createtime");
                        long time = Long.parseLong(createTime);
                        //下单时间
                        String ordertime= UtilsTime.getTime(time);
                        printBean.setCreateTime(ordertime);
                        String  pmoney="";

                        if ("1.00".equals(json.getJSONObject("data").getJSONObject("item").getString("sale"))) {
                            pmoney=json.getJSONObject("data").getJSONObject("item").getString("price")+"";
                        } else {
                            double yhzk = Double.parseDouble(json.getJSONObject("data").getJSONObject("item").getString("sale")) * 10;
                            double ysje = Double.parseDouble(json.getJSONObject("data").getJSONObject("item").getString("price")) * Double.parseDouble(json.getJSONObject("data").getJSONObject("item").getString("sale"));
                             pmoney=ysje+"";
                        }
                        printBean.setPmoney(pmoney);
                        printBean.setRemark(json.getJSONObject("data").getJSONObject("item").getString("remark"));
                        String table_name=json.getJSONObject("data").getJSONObject("tableinfo").getString("tablename");
                        printBean.setTable_name(table_name);

                        //餐位费
                        String canweifei=json.getJSONObject("data").getJSONObject("item").getString("seat_fee");
                        printBean.setCanweifei(canweifei);
                        //订单状态

                          String orderTuype="";
                        if ("0".equals(json.getJSONObject("data").getJSONObject("item").getString("status"))) {
                            orderTuype="待付款";
                        } else if ("1".equals(json.getJSONObject("data").getJSONObject("item").getString("status")))
                        {
                            orderTuype="已付款";
                        }

                        printBean.setOrderTuype(orderTuype);
                        JSONArray jsonArray = json.getJSONObject("data").getJSONObject("item").getJSONArray("goods");
                         List<PrintItem> plist=new ArrayList<>();
                        if (jsonArray != null) {
                            for (int i = 0; i < jsonArray.length(); i++) {
                                 PrintItem pi =new PrintItem();
                                 pi.setGcount(jsonArray.getJSONObject(i).getString("total"));
                                 pi.setGoodsname(jsonArray.getJSONObject(i).getString("title"));
                                 pi.setGprice(jsonArray.getJSONObject(i).getString("price"));
                                  if ("".equals(jsonArray.getJSONObject(i).getString("optionname"))){
                                      pi.setOptions("");
                                  }else {
                                      pi.setOptions(jsonArray.getJSONObject(i).getString("optionname"));
                                  }
                                 plist.add(pi);
                            }
                        }
                        printBean.setList(plist);
                        for (int i=0;i<2;i++){
                             if (i==0){
                                 print("本店保留",printBean);
                             }else {
//                                 print("用户留存",printBean);
                             }
                        }
                        setPay(pmoney);
                        finish();
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                    break;

                case 2000:
                    vipinfo.setVisibility(View.VISIBLE);
                    HuiYuanInfoBean.MemberlistBean hmb = (HuiYuanInfoBean.MemberlistBean) msg.obj;
                    vipname.setText(hmb.getRealname());
                    Glide.with(ListGoodsDetailsActivity.this).load(hmb.getAvatar()).into(viphead);
                    String  memberid= hmb.getId();
                    Utils.getACache(ListGoodsDetailsActivity.this).put("memberid",memberid);
                    break;
            }
        }
    };

    /**
     * 获取订单详情并且打印小票
     */
    private void printandgetorder(String orderid) {
        Map<String, String> map = MapUtilsSetParam.getMap(this);
        map.put("opp", "getorderdetail");
        //获取分店id
        map.put("branchid", Utils.getBranch(this).getId());
        map.put("orderid", orderid);
        NetUtils.netWorkByMethodPost(this, map, IpConfig.URL, handler,ContantData.GETORDERLISTDETAILS);

    }

    /**
     * 打印机的参数设置
     * @param name 标题的设置[顾客保留、本店留存]
     * @param
     */
    private void print(String name,PrintBean pb) {
    //-----------------------------最大分割线-----------------------
        LinkedList<TableItem> flinelist=new LinkedList<>();
        TableItem fline=new TableItem();
        String[] flinecon={"*******************","",""};
        int[] flinealt={0,2,2};
        int[] flinewid = new int[]{1,0,0};
        fline.setText(flinecon);
        fline.setAlign(flinealt);
        fline.setWidth(flinewid);
        flinelist.add(fline);

        //-------------------标题的打印------------------------
        LinkedList<TableItem> head=new LinkedList<>();
        TableItem ti01=new TableItem();
        String[] headti01={name,"",""};
        int[] headalt={0,2,2};
        int[] headwid = new int[]{1,0,0};
        ti01.setText(headti01);
        ti01.setAlign(headalt);
        ti01.setWidth(headwid);
        //-----------分割线-------------
        TableItem ti02=new TableItem();
        String[] headti02={"*******************","",""};
        int[] headalt02={0,2,2};
        int[] headwid02 = new int[]{1,0,0};
        ti02.setText(headti02);
        ti02.setAlign(headalt02);
        ti02.setWidth(headwid02);
        //------------------------
        TableItem ti03=new TableItem();
        String[] headti03={""," 现场订单",""};
        int[] headalt03={1,1,2};
        int[] headwid03 = new int[]{0,4,0};
        ti03.setText(headti03);
        ti03.setAlign(headalt03);
        ti03.setWidth(headwid03);

        TableItem ti04=new TableItem();
        String[] headti04={"","桌子["+pb.getTable_name()+"]",""};
        int[] headalt04={1,1,2};
        int[] headwid04 = new int[]{0,4,0};
        ti04.setText(headti04);
        ti04.setAlign(headalt04);
        ti04.setWidth(headwid04);

        TableItem ti05=new TableItem();
        String[] headti05={"","支付状态：["+pb.getOrderTuype()+"]",""};
        int[] headalt05={1,1,2};
        int[] headwid05 = new int[]{0,4,0};
        ti05.setText(headti05);
        ti05.setAlign(headalt05);
        ti05.setWidth(headwid05);
        //----------------------------------
        head.add(ti01);
        head.add(ti02);
        head.add(ti03);
        head.add(ti02);
        head.add(ti04);
        head.add(ti05);
        head.add(ti02);
        //-------------------标题的打印------------------------
        LinkedList<TableItem> title=new LinkedList<>();
        TableItem t0=new TableItem();
        TableItem t1=new TableItem();
        String[] st={"菜品名称","价格","数量"};
        int[] alt={0,2,2};
        int[] wid = new int[]{3,2,2};
        String[] t11={"","---------------------",""};
        int[] alt1={0,2,2};
        int[] wid1 = new int[]{0,1,0};
        t1.setAlign(alt);
        t1.setText(t11);
        t1.setWidth(wid1);
        t0.setAlign(alt1);
        t0.setText(st);
        t0.setWidth(wid);
        title.add(t1);
        title.add(t0);
        title.add(t1);
        LinkedList<TableItem> datalist=new LinkedList<>();
        for (int i=0;i<pb.getList().size();i++){
            TableItem  ti = new TableItem();
            String[] str={pb.getList().get(i).getGoodsname(),"￥"+pb.getList().get(i).getGprice(),"x"+pb.getList().get(i).getGcount()};
            ti.setText(str);
            int[] al={0,0,2};
            ti.setAlign(al);
            int[] width = new int[]{3,2,1};
            ti.setWidth(width);
            datalist.add(ti);
            Log.i("ULRL",pb.getList().get(i).getOptions());
            if (!"null".equals(pb.getList().get(i).getOptions())){
                TableItem  ti0 = new TableItem();
                String[] str0={"规格：",pb.getList().get(i).getOptions(),""};
                ti0.setText(str0);
                int[] al0={0,0,2};
                ti0.setAlign(al0);
                int[] width0 = new int[]{2,5,0};
                ti0.setWidth(width0);
                datalist.add(ti0);
            }
        }
        TableItem tcontent=new TableItem();
        String[] tcon={"","-------------------------",""};
        int[] altcon={0,1,2};
        int[] widcon = new int[]{0,1,0};
        tcontent.setText(tcon);
        tcontent.setAlign(altcon);
        tcontent.setWidth(widcon);
        datalist.add(tcontent);

        LinkedList<TableItem> beizhulist=new LinkedList<>();
        TableItem beizhulistBean=new TableItem();
        String[] beizhuBeanCont={"备注",pb.getRemark(),""};
        int[] beizhuBeanalt={0,0,2};
        int[] beizhuBeanwide = new int[]{1,3,0};
        beizhulistBean.setText(beizhuBeanCont);
        beizhulistBean.setAlign(beizhuBeanalt);
        beizhulistBean.setWidth(beizhuBeanwide);
        beizhulist.add(beizhulistBean);
        //------------------------底部下单时间和订单编号-----------------------------
        LinkedList<TableItem> orderprint=new LinkedList<>();
        TableItem orderprintBean=new TableItem();
        String[] orderprintBeanCont={"订单编号:",pb.getOrdersn(),""};
        int[] orderprintBeanalt={0,0,2};
        int[] orderprintBeanwide = new int[]{1,2,0};
        orderprintBean.setText(orderprintBeanCont);
        orderprintBean.setAlign(orderprintBeanalt);
        orderprintBean.setWidth(orderprintBeanwide);
        TableItem orderprintBean01=new TableItem();

        String[] orderprintBeanCont01={"下单时间:",pb.getCreateTime(),""};
        int[] orderprintBeanalt01={0,0,0};
        int[] orderprintBeanwide01= new int[]{1,2,0};
        orderprintBean01.setText(orderprintBeanCont01);
        orderprintBean01.setAlign(orderprintBeanalt01);
        orderprintBean01.setWidth(orderprintBeanwide01);
        orderprint.add(orderprintBean);
        orderprint.add(orderprintBean01);
        //------------------------底部下单时间和订单编号-----------------------------
        //---------------------------金额--餐位费--------------------------------------
        LinkedList<TableItem> moneylist=new LinkedList<>();
        TableItem monc=new TableItem();
        if (peoplecount==null){
            peoplecount="0";
        }
        String[] moncCon={"餐位费","","￥"+pb.getCanweifei()};
        int[] moncalt={0,2,2};
        int[] moncwid ={1,0,1};
        monc.setText(moncCon);
        monc.setAlign(moncalt);
        monc.setWidth(moncwid);

        TableItem monTi=new TableItem();
        String price="0.00";
        if (mess != null) {
            price=(mess.price+(Double.parseDouble(peoplecount)*4.00))+"";
        }
        String[] monTiCon={"总计：","","￥"+pb.getPmoney()};
        int[] monTialt={0,2,2};
        int[] monTiwid ={1,0,2};
        monTi.setText(monTiCon);
        monTi.setAlign(monTialt);
        monTi.setWidth(monTiwid);

        moneylist.add(monc);
        moneylist.add(monTi);

        //---------------------------金额----------------------------------
        AidlUtil.getInstance().printTable(head,40,true);
        AidlUtil.getInstance().printTable(title,36,true);
        AidlUtil.getInstance().printTable(datalist,30,false);
        AidlUtil.getInstance().printTable(orderprint,26,false);
        AidlUtil.getInstance().printTable(beizhulist,30,false);
        AidlUtil.getInstance().printTable(flinelist,40,false);
        AidlUtil.getInstance().printTable(moneylist,42,true);
        AidlUtil.getInstance().printTable(flinelist,40,false);
        AidlUtil.getInstance().printQr("www.01nnt.com",10,2);
        AidlUtil.getInstance().print3Line();

    }


    /**
     * 可以提交
     */
    private void submitIsOk() {

        Map<String, String> makeOrdermMap = MapUtilsSetParam.getMap(ListGoodsDetailsActivity.this);
        //接待员ID
        String waiter = Utils.getACache(ListGoodsDetailsActivity.this).getAsString("waiter");
        makeOrdermMap.put("workerid", waiter);
        //把货物封装成jsonarray数组集合

        List<GoodsUp> listup = new ArrayList<GoodsUp>();
        for (int i = 0; i < list.size(); i++) {
            GoodsUp gu = new GoodsUp();
            gu.setGoodsid(list.get(i).getId());
            gu.setNumber(list.get(i).getG_count());
            if("1".equals(list.get(i).getHasoption())){
                gu.setOptionid(list.get(i).getOptions().getOptionid()+"");
            }else {
                gu.setOptionid("");
            }
            listup.add(gu);
        }
        String listString = JSON.toJSONString(list);
        String listJSOn = JSON.toJSONString(listup);
        String value = null;
        try {
            value = new String(listJSOn.getBytes(), "utf-8");
        } catch (Exception e) {
            e.printStackTrace();
        }
        makeOrdermMap.put("opp", "ordersubmit");
        makeOrdermMap.put("branchid", Utils.getBranch(ListGoodsDetailsActivity.this).getId());
        String memberid = Utils.getACache(ListGoodsDetailsActivity.this).getAsString("memberid");
        if (memberid!=null&&memberid.length()>0){
            makeOrdermMap.put("memberid", memberid);
        }else {
            makeOrdermMap.put("memberid", Utils.getACache(ListGoodsDetailsActivity.this).getAsString("sanke"));
        }
        makeOrdermMap.put("goodslist", value);
        //需要新的接口
        makeOrdermMap.put("tableid", tableBean.getId());
        makeOrdermMap.put("datenum", peoplecount);

        String trim = beizhu.getText().toString().trim();

         if (trim.length()>0&&trim!=null){
             makeOrdermMap.put("remark",trim);
         }else {
             makeOrdermMap.put("remark","暂无备注！！！");
         }


        Log.i("URL", Utils.getBranch(ListGoodsDetailsActivity.this).getId()+":::"+Utils.getACache(ListGoodsDetailsActivity.this).getAsString("sanke")+"::::"+value);
        NetUtils.netWorkByMethodPost(this, makeOrdermMap, IpConfig.URL, handler, ContantData.SUBMITORDER);
    }
    public void setPopWindow(){
        mPopupWindow = new PopupWindow(mContentView, LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.MATCH_PARENT, true);
        mPopupWindow.setTouchable(true);
        mPopupWindow.setOutsideTouchable(false);
        mPopupWindow.setBackgroundDrawable(new BitmapDrawable(getResources(), (Bitmap) null));
        mPopupWindow.showAtLocation(parentview, Gravity.CENTER,0,0);
    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == ContantData.GETVIPINFO && resultCode == RESULT_OK) {
            //清空购物车和总价
            HuiYuanInfoBean.MemberlistBean hmb = (HuiYuanInfoBean.MemberlistBean) data.getSerializableExtra("huiyuaninfo_data");
             Message message  = new Message();
             message.what=2000;
             message.obj=hmb;
            handler.sendMessage(message);

        }
    }

//    启动支付
    public  void setPay(String money){
        Log.i("UUUU",money);
        double dmoney= Double.parseDouble(money)*100;
        long mone = new Double(dmoney).longValue();
//      long  mone =Long.parseLong(dmoney+"");
        Intent intent = new Intent("sunmi.payment.L3");
        String transId = System.currentTimeMillis()+ "";
//        intent.putExtra("transId",transId);
        intent.putExtra("transType", 13);
//        用户自选
//        intent.putExtra("paymentType", "-1");
//        try {
//            intent.putExtra("amount", mone);
//        } catch (Exception e) {
//            Toast.makeText(this, "消费金额填写错误", Toast.LENGTH_SHORT).show();
//            return;
//        }
        intent.putExtra("appId", getPackageName());
        if (Util.isIntentExisting(intent, this)) {
            startActivity(intent);
        } else {
            Toast.makeText(this, "此机器上没有安装L3应用", Toast.LENGTH_SHORT).show();
        }
    }
}
