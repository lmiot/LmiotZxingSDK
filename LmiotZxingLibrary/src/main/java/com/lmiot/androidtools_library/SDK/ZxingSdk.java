package com.lmiot.androidtools_library.SDK;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.MultiFormatWriter;
import com.google.zxing.WriterException;
import com.google.zxing.common.BitMatrix;
import com.journeyapps.barcodescanner.BarcodeEncoder;
import com.lmiot.androidtools_library.zxing.CaptureActivity;

/**
 * 创建日期：2017-09-18 15:12
 * 作者:Mr Li
 * 描述:二维码工具类
 */
public class ZxingSdk {

    private static onResultLitener onResultLitener01;


    /**
     * 开始扫描二维码
     * @param context
     * @param onResultLitener
     */
    public static void startScan(Activity context, onResultLitener onResultLitener) {
        context.startActivity(new Intent(context,CaptureActivity.class));
        onResultLitener01 = onResultLitener;
    }

    public static void setResult(String text) {

        if (onResultLitener01 != null) {
            onResultLitener01.result(text);
        }

    }

    public static void QRCode(String str,onQRCodeLitener onQRCodeLitener) {
        Bitmap bitmap = createBitmap(str);
        if(bitmap!=null){
            onQRCodeLitener.result(bitmap);
        }

    }

    private static Bitmap createBitmap(String str) {
        Bitmap bitmap = null;
        BitMatrix result = null;
        MultiFormatWriter multiFormatWriter = new MultiFormatWriter();
        try {
            result = multiFormatWriter.encode(str, BarcodeFormat.QR_CODE, 500, 500);
            // 使用 ZXing Android Embedded 要写的代码
            BarcodeEncoder barcodeEncoder = new BarcodeEncoder();
            bitmap = barcodeEncoder.createBitmap(result);
        }
        catch (WriterException e){
            e.printStackTrace();
        }

        return bitmap;
    }


    public interface  onResultLitener{
        void result(String result);

    }
   public interface  onQRCodeLitener{
        void result(Bitmap bitmap);

    }

}
