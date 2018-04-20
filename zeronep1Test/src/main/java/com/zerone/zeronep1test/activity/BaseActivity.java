package com.zerone.zeronep1test.activity;

import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.annotation.StringRes;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;
import com.githang.statusbar.StatusBarCompat;
import com.zerone.zeronep1test.R;
import com.zerone.zeronep1test.application.MyApplication;
import com.zerone.zeronep1test.utils.AidlUtil;
import com.zerone.zeronep1test.utils.BytesUtil;
import sunmi.sunmiui.dialog.DialogCreater;
import sunmi.sunmiui.dialog.EditTextDialog;
/**
 *
 * Created by Administrator on 2017/4/27.
 *
 */

public abstract class BaseActivity extends AppCompatActivity{
    public MyApplication baseApp;

    public MyApplication application;
    private BaseActivity oContext;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        StatusBarCompat.setStatusBarColor(this, Color.parseColor("#8be7b2"));
        baseApp = (MyApplication) getApplication();

        if (application == null) {
            // 得到Application对象
            application = (MyApplication) getApplication();
        }
        // 把当前的上下文对象赋值给BaseActivity
        oContext = this;
        // 调用添加方法
        addActivity();
    }

    /**
     *  添加Activity方法
     */
    public void addActivity() {
        //调用myApplication的添加Activity方法
        application.addActivity(oContext);
    }

    /**
     * 销毁当个Activity方法
     */
    public void removeActivity() {
        application.removeActivity(oContext);// 调用myApplication的销毁单个Activity方法
    }

    /**
     * 销毁所有Activity方法
     */
    public void removeALLActivity() {
        // 调用myApplication的销毁所有Activity方法
        application.removeALLActivity();
    }
    /**
     * 把Toast定义成一个方法  可以重复使用，使用时只需要传入需要提示的内容即可
     * @param text
     */
    public void showToast(String text) {
        Toast.makeText(oContext, text, Toast.LENGTH_SHORT).show();
    }

    /**
     * 设置标题
     * @param title
     */
    void setMyTitle(String title){
        ActionBar actionBar = getSupportActionBar();
        actionBar.setTitle(title);
    }

    public void setMyTitle(@StringRes int title){
        ActionBar actionBar = getSupportActionBar();
        actionBar.setTitle(title);
        if(!baseApp.isAidl()){
            actionBar.setSubtitle("bluetooth®");
        }else{
            actionBar.setSubtitle("");
        }
    }

    public void setBack(){
        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setHomeAsUpIndicator(R.drawable.back);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.hexprint, menu);
        return true;
    }

    EditTextDialog mEditTextDialog;
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        switch (item.getItemId()){
            case R.id.action_print:
                //Toast.makeText(this, "将实现十六进制指令发送", Toast.LENGTH_SHORT).show();
                mEditTextDialog = DialogCreater.createEditTextDialog(this, getResources().getString(R.string.cancel), getResources().getString(R.string.confirm), getResources().getString(R.string.input_order), new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        mEditTextDialog.cancel();
                    }
                }, new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        String text = mEditTextDialog.getEditText().getText().toString();
                        AidlUtil.getInstance().sendRawData(BytesUtil.getBytesFromHexString(text));
                        mEditTextDialog.cancel();
                    }
                }, null);
                mEditTextDialog.show();
                break;
            default:
                return super.onOptionsItemSelected(item);
        }
        return true;
    }
}
