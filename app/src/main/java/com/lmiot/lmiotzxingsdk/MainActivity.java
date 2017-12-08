package com.lmiot.lmiotzxingsdk;

import android.graphics.Bitmap;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;

import com.lmiot.androidtools_library.SDK.ZxingSdk;


public class MainActivity extends AppCompatActivity {

    private ImageView mImageView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Button scan= (Button) findViewById(R.id.id_sacn);
        Button qrcode= (Button) findViewById(R.id.id_create_qrcode);
        mImageView = (ImageView) findViewById(R.id.id_img);

        scan.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                //扫描二维码，并返回结果
                ZxingSdk.startScan(MainActivity.this, new ZxingSdk.onResultLitener() {
                    @Override
                    public void result(String result) {
                        Log.d("二维码扫描结果", result);
                    }
                });

            }
        });
        qrcode.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                //生成二维码
                ZxingSdk.CreateQRCode("https://www.baidu.com/", new ZxingSdk.onQRCodeLitener() {
                    @Override
                    public void result(Bitmap bitmap) {

                        if(bitmap!=null){
                            mImageView.setImageBitmap(bitmap);
                        }

                    }
                });
            }
        });
    }
}
