package com.catering.zerone.zeronepay;

import android.app.Activity;
import android.content.ComponentName;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity{

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
//        initSpay()
           qidong();
    }

    private void qidong() {
        Intent intent = new Intent("sunmi.payment.L3");
        String transId = System.currentTimeMillis()+ "";
        intent.putExtra("transId",transId);
        intent.putExtra("transType", 0);
        //用户自选
        intent.putExtra("paymentType", "-1");
        try {
            intent.putExtra("amount", Long.parseLong("1"));
        } catch (Exception e) {
            Toast.makeText(this, "消费金额填写错误", Toast.LENGTH_SHORT).show();
            return;
        }
        intent.putExtra("appId", getPackageName());
        if (Util.isIntentExisting(intent, this)) {
            startActivity(intent);
        } else {
            Toast.makeText(this, "此机器上没有安装L3应用", Toast.LENGTH_SHORT).show();
        }
    }

//    private void initSpay() {
////        “0”：银行卡
////“1”：微信支付
////“2”：支付宝
////“3”：盛付通钱包
////“4”：扫一扫
////“5”：银联二维码
//        Intent intent = new Intent();
//        intent.setComponent(new ComponentName("com.shengpay.smartpos.shengpaysdk","com.shengpay.smartpos.shengpaysdk.activity.MainActivity"));
//        intent.putExtra("transName", "0");
//        //调用者的运用包名
//        intent.putExtra("appId", getPackageName());
//        intent.putExtra("appIdB","零壹新科技");
//        intent.putExtra("amount", "000000000001");
//        //支付方式
//        intent.putExtra("barcodeType","0");
//        //商户编号
//        intent.putExtra("orderNoSFT","201802071536");
//        startActivityForResult(intent, 0);
//    }
//
//    @Override
//    protected void onActivityResult(int requestCode
//            , int resultCode, Intent data) {
//        switch(resultCode) {
//            case Activity.RESULT_CANCELED:
////                Log.i("URL",data.toString());
////                String reason = data.getStringExtra("reason");
////                if(reason != null) {
////                    Toast.makeText(this, reason, Toast.LENGTH_SHORT).show();
////                }
//                break;
//            case Activity.RESULT_OK:
//                break;
//        }
//
//        super.onActivityResult(requestCode, resultCode, data);
//    }
}
