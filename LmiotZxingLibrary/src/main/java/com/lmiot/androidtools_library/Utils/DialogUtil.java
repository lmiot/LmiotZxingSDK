package com.lmiot.androidtools_library.Utils;

import android.app.Dialog;
import android.content.Context;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.lmiot.androidtools_library.R;
import com.lmiot.androidtools_library.zxing.CaptureActivity;

/**
 * 创建日期：2017-09-30 14:35
 * 作者:Mr Li
 * 描述:对话框工具类
 */
public class DialogUtil {

    public  static  void showSureDialog(Context context,String title,String content){

        final Dialog dailog = LayoutDialogUtil.createDailog(context, R.layout.dialog_sure);
        dailog.setCancelable(true);
        TextView titleText = (TextView) dailog.findViewById(R.id.tv_title);
        TextView contentText = (TextView) dailog.findViewById(R.id.tv_content);
        TextView sureText = (TextView) dailog.findViewById(R.id.tv_sure);
        titleText.setText(title);
        contentText.setText(content);
        sureText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dailog.dismiss();

            }
        });


    }

}
