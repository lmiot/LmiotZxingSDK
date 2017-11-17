package com.lmiot.androidtools_library.Activity;

import android.app.Activity;
import android.os.Bundle;
import android.os.PersistableBundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;

import com.lmiot.androidtools_library.Utils.ActivityUtil;

/**
 * 创建日期：2017-09-30 10:25
 * 作者:Mr Li
 * 描述:
 */
public class BaseActivity extends AppCompatActivity {

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
       // ActivityUtil.addActivity(this);

    }
}
