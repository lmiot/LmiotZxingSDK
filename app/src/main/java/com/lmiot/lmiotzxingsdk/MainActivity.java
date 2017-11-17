package com.lmiot.lmiotzxingsdk;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import com.lmiot.androidtools_library.SDK.ZxingSdk;
import com.lmiot.androidtools_library.zxing.CaptureActivity;


public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        TextView textView= (TextView) findViewById(R.id.id_test);

        textView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ZxingSdk.startScan(MainActivity.this, new ZxingSdk.onResultLitener() {
                    @Override
                    public void result(String result) {
                        Log.d("MainActivity", result);
                    }
                });
            }
        });
    }
}
