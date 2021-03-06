package com.catering.zerone.p1mi;

import android.content.Intent;
import android.graphics.Color;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AccelerateInterpolator;
import android.view.animation.Animation;
import android.view.animation.AnimationSet;
import android.view.animation.LinearInterpolator;
import android.view.animation.TranslateAnimation;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RadioGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.catering.zerone.p1mi.activity.CheckWaiterActivity;
import com.catering.zerone.p1mi.activity.ListGoodsDetailsActivity;
import com.catering.zerone.p1mi.baseacticity.BaseAppActivity;
import com.catering.zerone.p1mi.contanst.ContantData;
import com.catering.zerone.p1mi.event.GoodsListEvent;
//import com.catering.zerone.p1mi.event.MessageEvent;
import com.catering.zerone.p1mi.event.MessageEvent;
import com.catering.zerone.p1mi.fragment.FragmentOrderDC;
import com.catering.zerone.p1mi.fragment.FragmentOrderDD;
import com.catering.zerone.p1mi.utils.AnimationUtil;
import com.githang.statusbar.StatusBarCompat;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

public class MainActivity extends BaseAppActivity implements View.OnClickListener {
    private RadioGroup rg;
    private ViewPager vp;
    private List<Fragment> list;
    private RelativeLayout shopCartMain;
    private TextView shopCartNum;
    private TextView totalPrice;
    private TextView noShop;
    private TextView goTOCheckOut;
    private MessageEvent messageEvent;
    private ImageView m_waiter_btn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
//        0eb468
        StatusBarCompat.setStatusBarColor(this, Color.parseColor("#ffffff"));
        initView();
        vp_RG_Event();
        vp_data();
    }

    private void vp_RG_Event() {
        vp.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageSelected(int arg0) {
                switch (arg0) {
                    case 0:
                        rg.check(R.id.order_dc);
                        shopCartMain.startAnimation(
                                AnimationUtil.createInAnimation(MainActivity.this, shopCartMain.getMeasuredHeight()));
                        break;
                    case 1:
                        rg.check(R.id.order_dd);
                        shopCartMain.startAnimation(
                                AnimationUtil.createOutAnimation(MainActivity.this, shopCartMain.getMeasuredHeight()));
                        break;


                }
            }

            @Override
            public void onPageScrolled(int arg0, float arg1, int arg2) {
                // TODO Auto-generated method stub

            }

            @Override
            public void onPageScrollStateChanged(int arg0) {
                // TODO Auto-generated method stub

            }

        });
        rg.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                switch (checkedId) {
                    case R.id.order_dc:
                        vp.setCurrentItem(0);
                        break;
                    case R.id.order_dd:
                        vp.setCurrentItem(1);
                        break;

                }
            }
        });

    }

    private void vp_data() {
        vp.setAdapter(new MyViewPageAdapter(getSupportFragmentManager()));
    }

    private void initView() {
        rg = (RadioGroup) findViewById(R.id.zhuye_group);
        vp = (ViewPager) findViewById(R.id.zhuye_vp);
        list = new ArrayList<Fragment>();
        shopCartMain = (RelativeLayout) findViewById(R.id.shopCartMain);
        shopCartNum = (TextView) findViewById(R.id.shopCartNum);
        totalPrice = (TextView) findViewById(R.id.totalPrice);
        noShop = (TextView) findViewById(R.id.noShop);
        goTOCheckOut = (TextView) findViewById(R.id.goTOCheckOut);
        goTOCheckOut.setOnClickListener(this);
        m_waiter_btn = (ImageView) findViewById(R.id.selected_waiter);
        m_waiter_btn.setOnClickListener(this);

    }

    /**
     * 点击事件的处理
     * @param v
     */
    @Override
    public void onClick(View v) {
         switch (v.getId()){
             case R.id.selected_waiter:
                     Intent intn  = new Intent(MainActivity.this, CheckWaiterActivity.class);
                     startActivity(intn);
                     removeALLActivity();
                 break;

             case R.id.goTOCheckOut:
                 Intent intent = new Intent(MainActivity.this, ListGoodsDetailsActivity.class);
                 intent.putExtra("goods", messageEvent);
                 startActivityForResult(intent, ContantData.MAINMAKEORDERREQUESE);
                 break;

         }
    }

    class MyViewPageAdapter extends FragmentPagerAdapter {
        public MyViewPageAdapter(FragmentManager fm) {
            super(fm);
            list.add(new FragmentOrderDC());
            list.add(new FragmentOrderDD());
        }

        @Override
        public Fragment getItem(int arg0) {
            if (list != null) {
                return list.get(arg0);
            }
            return null;
        }

        @Override
        public int getCount() {
            if (list != null) {
                return list.size();
            }
            return 0;
        }
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        switch (keyCode) {
            case KeyEvent.KEYCODE_BACK:
                exitBy2Click();
                break;
        }
        return true;
    }

    /**
     * 双击退出函数
     */
    private static Boolean isExit = false;
    private LayoutInflater layoutInflater;

    public void exitBy2Click() {
        Timer tExit = null;
        if (isExit == false) {
            isExit = true; // 准备退出
            Toast.makeText(this, "再按一次退出程序", Toast.LENGTH_SHORT).show();
            tExit = new Timer();
            tExit.schedule(new TimerTask() {
                @Override
                public void run() {
                    isExit = false; // 取消退出
                }
            }, 2000); // 如果2秒钟内没有按下返回键，则启动定时器取消掉刚才执行的任务
        } else {
            finish();
        }
    }


    /**
     * 默认选中
     *
     * @param pos 默认选中的页面id
     */
    private void initmorenyemian(int pos) {
        switch (pos) {
            case 0:
                rg.check(R.id.order_dc);
                vp.setCurrentItem(0);
                break;
            case 1:
                rg.check(R.id.order_dd);
                vp.setCurrentItem(1);
                break;

        }
    }


    /**
     * 添加 或者  删除  商品发送的消息处理
     *
     * @param event
     */
    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onMessageEvent(MessageEvent event) {
        if (event != null) {
            messageEvent = event;
            if (event.num > 0) {
                shopCartNum.setText(String.valueOf(event.num));
                shopCartNum.setVisibility(View.VISIBLE);
                totalPrice.setVisibility(View.VISIBLE);
                noShop.setVisibility(View.GONE);
                Log.i("URL",event.num+":::::::::::::::::::::::::::::::::");
            } else {
                shopCartNum.setVisibility(View.GONE);
                totalPrice.setVisibility(View.GONE);
                noShop.setVisibility(View.VISIBLE);
            }
            totalPrice.setText("¥" + String.valueOf(event.price));
        }
    }

    private ViewGroup anim_mask_layout;//动画层

    /**
     * 设置动画（点击添加商品）
     *
     * @param v
     * @param startLocation
     */
    public void setAnim(final View v, int[] startLocation) {
        anim_mask_layout = null;
        anim_mask_layout = createAnimLayout();
        anim_mask_layout.addView(v);//把动画小球添加到动画层
        final View view = addViewToAnimLayout(anim_mask_layout, v, startLocation);
        int[] endLocation = new int[2];// 存储动画结束位置的X、Y坐标
        shopCartNum.getLocationInWindow(endLocation);
        // 计算位移
        int endX = 0 - startLocation[0] + 40;// 动画位移的X坐标
        int endY = endLocation[1] - startLocation[1];// 动画位移的y坐标
        TranslateAnimation translateAnimationX = new TranslateAnimation(0, endX, 0, 0);
        translateAnimationX.setInterpolator(new LinearInterpolator());
        translateAnimationX.setRepeatCount(0);// 动画重复执行的次数
        translateAnimationX.setFillAfter(true);
        TranslateAnimation translateAnimationY = new TranslateAnimation(0, 0, 0, endY);
        translateAnimationY.setInterpolator(new AccelerateInterpolator());
        translateAnimationY.setRepeatCount(0);// 动画重复执行的次数
        translateAnimationY.setFillAfter(true);
        AnimationSet set = new AnimationSet(false);
        set.setFillAfter(false);
        set.addAnimation(translateAnimationY);
        set.addAnimation(translateAnimationX);
        set.setDuration(400);// 动画的执行时间
        view.startAnimation(set);
        // 动画监听事件
        set.setAnimationListener(new Animation.AnimationListener() {
            // 动画的开始
            @Override
            public void onAnimationStart(Animation animation) {
                v.setVisibility(View.VISIBLE);
            }

            @Override
            public void onAnimationRepeat(Animation animation) {
            }

            // 动画的结束
            @Override
            public void onAnimationEnd(Animation animation) {
                v.setVisibility(View.GONE);
            }
        });

    }

    /**
     * 初始化动画图层
     *
     * @return
     */
    private ViewGroup createAnimLayout() {
        ViewGroup rootView = (ViewGroup) this.getWindow().getDecorView();
        LinearLayout animLayout = new LinearLayout(this);
        LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.MATCH_PARENT);
        animLayout.setLayoutParams(lp);
        animLayout.setId(Integer.MAX_VALUE - 1);
        animLayout.setBackgroundResource(android.R.color.transparent);
        rootView.addView(animLayout);
        return animLayout;
    }

    /**
     * 将View添加到动画图层
     *
     * @param parent
     * @param view
     * @param location
     * @return
     */
    private View addViewToAnimLayout(final ViewGroup parent, final View view,
                                     int[] location) {
        int x = location[0];
        int y = location[1];
        LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.WRAP_CONTENT,
                LinearLayout.LayoutParams.WRAP_CONTENT);
        lp.leftMargin = x;
        lp.topMargin = y;
        view.setLayoutParams(lp);
        return view;
    }

    @Override
    public void onStart() {
        super.onStart();
        if (!EventBus.getDefault().isRegistered(this)) {
            EventBus.getDefault().register(this);
        }
    }

    @Override
    public void onStop() {
        super.onStop();
        if (EventBus.getDefault().isRegistered(this)) {
            EventBus.getDefault().unregister(this);
        }
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == ContantData.MAINMAKEORDERREQUESE && resultCode == RESULT_OK) {
            //清空购物车和总价
            Message message = new Message();
            message.what = 1;
            handler.sendMessage(message);

        }
    }


    Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case 1:
                    shopCartNum.setVisibility(View.GONE);
                    totalPrice.setVisibility(View.GONE);
                    noShop.setVisibility(View.VISIBLE);
                    totalPrice.setText("");
                    shopCartNum.setText("");
                    break;
            }
        }
    };
}
