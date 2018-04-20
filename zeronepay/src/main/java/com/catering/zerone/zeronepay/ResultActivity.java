package com.catering.zerone.zeronepay;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;
import android.widget.Toast;

/**
 * @author xurong on 2017/5/15.
 */

public class ResultActivity extends Activity {

    private static final String TAG = "ResultActivity";

    TextView result_tv;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_result);
        result_tv = (TextView) findViewById(R.id.result_tv);
        int resultCode = getIntent().getIntExtra("resultCode", -1);
        String errorMsg = getIntent().getStringExtra("errorMsg");
        String resultInfo = getIntent().getStringExtra("resultInfo");

        if (resultCode == 0) {
            // 交易成功
            Toast.makeText(this, "交易成功, 具体信息请查看控制台的Log", Toast.LENGTH_SHORT).show();
            Log.e(TAG, "交易成功");

        } else if (resultCode == -1) {
            // 交易失败
            Toast.makeText(this, errorMsg, Toast.LENGTH_SHORT).show();
            Log.e(TAG, errorMsg);
        }
        result_tv.setText("Result:" + resultInfo);
    }
}
