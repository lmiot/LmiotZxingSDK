package com.lmiot.androidtools_library.zxing;

import android.app.Activity;
import android.app.Dialog;
import android.content.ContentResolver;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.res.AssetFileDescriptor;
import android.graphics.Bitmap;
import android.graphics.Matrix;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.media.MediaPlayer.OnCompletionListener;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Vibrator;
import android.provider.MediaStore;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceHolder.Callback;
import android.view.SurfaceView;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.BinaryBitmap;
import com.google.zxing.DecodeHintType;
import com.google.zxing.MultiFormatReader;
import com.google.zxing.Result;
import com.google.zxing.common.HybridBinarizer;
import com.lmiot.androidtools_library.SDK.ZxingSdk;
import com.lmiot.androidtools_library.R;
import com.lmiot.androidtools_library.Utils.LayoutDialogUtil;
import com.lmiot.androidtools_library.zxing.camera.CameraManager;
import com.lmiot.androidtools_library.zxing.decoding.BitmapLuminanceSource;
import com.lmiot.androidtools_library.zxing.decoding.CaptureActivityHandler;
import com.lmiot.androidtools_library.zxing.decoding.InactivityTimer;
import com.lmiot.androidtools_library.zxing.view.ViewfinderView;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Hashtable;
import java.util.Vector;

public class CaptureActivity extends Activity implements Callback, View.OnClickListener {
    public static final String QR_RESULT = "RESULT";


    private CaptureActivityHandler handler;
    private ViewfinderView viewfinderView;
    private SurfaceView surfaceView;
    private boolean hasSurface;
    private Vector<BarcodeFormat> decodeFormats;
    private String characterSet;
    private InactivityTimer inactivityTimer;
    private MediaPlayer mediaPlayer;
    private boolean playBeep;
    // private static final float BEEP_VOLUME = 0.10f;
    private boolean vibrate;
    CameraManager cameraManager;
    private String mCameraFlag;
    private Intent mIntent;
    private ImageView mBack;
    private ImageView mLight;
    private ImageView mPhoto;
    private boolean mFlag=false;
    private ImageView mCreate;

    /**
     * Called when the activity is first created.
     */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_capture);
        Window window = getWindow();
        //透明状态栏
        window.addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        //透明导航栏
        window.addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_NAVIGATION);
        //屏幕常亮
        window.addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        hasSurface = false;
        inactivityTimer = new InactivityTimer(this);
        intiView();
        mCameraFlag = getIntent().getStringExtra("camera");
    }


    /**
     * 设置页头
     */
    private void intiView() {
        mBack = findViewById(R.id.id_back);
        mLight = findViewById(R.id.id_light);
        mPhoto = findViewById(R.id.id_photo);
        mCreate = findViewById(R.id.id_create);
        surfaceView = findViewById(R.id.surfaceview);
        viewfinderView = findViewById(R.id.viewfinderview);
        mBack.setOnClickListener(this);
        mLight.setOnClickListener(this);
        mPhoto.setOnClickListener(this);
        mCreate.setOnClickListener(this);

    }


    @Override
    protected void onResume() {
        super.onResume();
        if (getRequestedOrientation() != ActivityInfo.SCREEN_ORIENTATION_PORTRAIT) {
            setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        }
        // LightManager.init(getApplication());
        cameraManager = new CameraManager(getApplication());

        viewfinderView.setCameraManager(cameraManager);

        SurfaceHolder surfaceHolder = surfaceView.getHolder();
        if (hasSurface) {
            initCamera(surfaceHolder);
        } else {
            surfaceHolder.addCallback(this);
            surfaceHolder.setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS);
        }
        decodeFormats = null;
        characterSet = null;

        playBeep = true;
        AudioManager audioService = (AudioManager) getSystemService(AUDIO_SERVICE);
        if (audioService.getRingerMode() != AudioManager.RINGER_MODE_NORMAL) {
            playBeep = false;
        }
        initBeepSound();
        vibrate = true;
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (handler != null) {
            handler.quitSynchronously();
            handler = null;
        }
        cameraManager.closeDriver();
    }

    @Override
    protected void onDestroy() {
        inactivityTimer.shutdown();
        super.onDestroy();
    }

    private void initCamera(SurfaceHolder surfaceHolder) {
        try {
            // LightManager.get().openDriver(surfaceHolder);
            cameraManager.openDriver(surfaceHolder);
        } catch (IOException ioe) {
            return;
        } catch (RuntimeException e) {
            return;
        }
        if (handler == null) {
            handler = new CaptureActivityHandler(this, decodeFormats, characterSet);
        }
    }

    @Override
    public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {

    }

    @Override
    public void surfaceCreated(SurfaceHolder holder) {
        if (!hasSurface) {
            hasSurface = true;
            initCamera(holder);
        }

    }

    @Override
    public void surfaceDestroyed(SurfaceHolder holder) {
        hasSurface = false;

    }

    public CameraManager getCameraManager() {
        return cameraManager;
    }

    public ViewfinderView getViewfinderView() {
        return viewfinderView;
    }

    public Handler getHandler() {
        return handler;
    }

    public void drawViewfinder() {
        viewfinderView.drawViewfinder();

    }

    public void handleDecode(Result obj, Bitmap barcode) {
        inactivityTimer.onActivity();
        playBeepSoundAndVibrate();
        showResult(obj);
    }

    private void showResult(final Result rawResult) {

        String text = rawResult.getText();
       //showSureDialog("扫描结果",text);
        ZxingSdk.setResult(text);
        finish();

    }

    private void showSureDialog( String title, String content) {


        final Dialog dailog = LayoutDialogUtil.createDailog(CaptureActivity.this, R.layout.dialog_sure);
        dailog.setCancelable(true);
        TextView titleText = (TextView) dailog.findViewById(R.id.tv_title);
        TextView contentText = (TextView) dailog.findViewById(R.id.tv_content);
        TextView sureText = (TextView) dailog.findViewById(R.id.tv_sure);
        titleText.setText(title);
        contentText.setText(content);
        sureText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                handler.restartPreviewAndDecode();//继续扫描

                dailog.dismiss();

            }
        });



    }


    private void initBeepSound() {
        if (playBeep && mediaPlayer == null) {
            setVolumeControlStream(AudioManager.STREAM_MUSIC);
            mediaPlayer = new MediaPlayer();
            mediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
            mediaPlayer.setOnCompletionListener(beepListener);

            try {
                AssetFileDescriptor fileDescriptor = getAssets().openFd("qrbeep.ogg");
                this.mediaPlayer.setDataSource(fileDescriptor.getFileDescriptor(), fileDescriptor.getStartOffset(),
                        fileDescriptor.getLength());
                this.mediaPlayer.setVolume(0.1F, 0.1F);
                this.mediaPlayer.prepare();
            } catch (IOException e) {
                this.mediaPlayer = null;
            }
        }
    }

    private static final long VIBRATE_DURATION = 200L;

    private void playBeepSoundAndVibrate() {
        if (playBeep && mediaPlayer != null) {
            mediaPlayer.start();
        }
        if (vibrate) {
            Vibrator vibrator = (Vibrator) getSystemService(VIBRATOR_SERVICE);
            vibrator.vibrate(VIBRATE_DURATION);
        }
    }

    /**
     * When the beep has finished playing, rewind to queue up another one.
     */
    private final OnCompletionListener beepListener = new OnCompletionListener() {
        public void onCompletion(MediaPlayer mediaPlayer) {
            mediaPlayer.seekTo(0);
        }
    };

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            setResult(RESULT_CANCELED);
            finish();
            return true;
        } else if (keyCode == KeyEvent.KEYCODE_FOCUS || keyCode == KeyEvent.KEYCODE_CAMERA) {
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }


    @Override
    public void onClick(View view) {
        int id = view.getId();
        if (id == R.id.id_back) {
            finish();

        } else if (id == R.id.id_light) {

            if(mFlag){
               cameraManager.offFlashLight();
                mFlag = false;
            }
            else{
                cameraManager.openFlashLight();
                mFlag = true;
            }




        } else if (id == R.id.id_photo) { //识别二维码
            getPic();
        } else if (id == R.id.id_create) { //生成二维码

            createQR();
        }

    }


    /**
     * 生成二维码
     */
    private void createQR() {
        final Dialog dailog = LayoutDialogUtil.createDailog(CaptureActivity.this, R.layout.dialog_edit);
        dailog.setCancelable(true);
        TextView titleText = (TextView) dailog.findViewById(R.id.tv_title);
        final EditText edit = (EditText) dailog.findViewById(R.id.editText);
        TextView sureText = (TextView) dailog.findViewById(R.id.tv_sure);
        TextView cancle = (TextView) dailog.findViewById(R.id.tv_cancle);
        titleText.setText("生成二维码");
        sureText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String s = edit.getText().toString();
                if(TextUtils.isEmpty(s)){
                    Toast.makeText(CaptureActivity.this, "内容不能为空", Toast.LENGTH_SHORT).show();
                    return;
                }
                ZxingSdk.QRCode(s, new ZxingSdk.onQRCodeLitener() {
                    @Override
                    public void result(Bitmap bitmap) {

                        showBitmap(bitmap);

                    }
                });


                dailog.dismiss();

            }
        });
        cancle.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {


                dailog.dismiss();

            }
        });

    }


    /**
     * 显示二维码
     * @param bitmap
     */
    private void showBitmap(Bitmap bitmap) {
        final Dialog dailog = LayoutDialogUtil.createDailog(CaptureActivity.this, R.layout.dialog_img);
        dailog.setCancelable(true);
        ImageView img = (ImageView) dailog.findViewById(R.id.id_img);
        TextView sureText = (TextView) dailog.findViewById(R.id.tv_sure);
        img.setImageBitmap(bitmap);
        sureText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dailog.dismiss();

            }
        });

    }


    public void getPic() {
        Intent openAlbumIntent = new Intent(Intent.ACTION_GET_CONTENT);
        openAlbumIntent.setType("image/*");
        startActivityForResult(openAlbumIntent, 1001);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        // TODO Auto-generated method stub
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == Activity.RESULT_OK) {
            ContentResolver resolver = getContentResolver();
            // 照片的原始资源地址
            Uri originalUri = data.getData();
            try {
                // 使用ContentProvider通过URI获取原始图片
                Bitmap photo = MediaStore.Images.Media.getBitmap(resolver, originalUri);
                if (photo != null) {
                    Bitmap smallBitmap = zoomBitmap(photo, photo.getWidth() / 2, photo.getHeight() / 2);// 为防止原始图片过大导致内存溢出，这里先缩小原图显示，然后释放原始Bitmap占用的内存
                    photo.recycle(); // 释放原始图片占用的内存，防止out of memory异常发生
//                    String bitmappath = saveFile(smallBitmap, setImageName());

                    MultiFormatReader multiFormatReader = new MultiFormatReader();

                    // 解码的参数
                    Hashtable<DecodeHintType, Object> hints = new Hashtable<DecodeHintType, Object>(2);
                    // 可以解析的编码类型
                    Vector<BarcodeFormat> decodeFormats = new Vector<BarcodeFormat>();
                    if (decodeFormats == null || decodeFormats.isEmpty()) {
                        decodeFormats = new Vector<BarcodeFormat>();

                        Vector<BarcodeFormat> PRODUCT_FORMATS = new Vector<BarcodeFormat>(5);
                        PRODUCT_FORMATS.add(BarcodeFormat.UPC_A);
                        PRODUCT_FORMATS.add(BarcodeFormat.UPC_E);
                        PRODUCT_FORMATS.add(BarcodeFormat.EAN_13);
                        PRODUCT_FORMATS.add(BarcodeFormat.EAN_8);
                        // PRODUCT_FORMATS.zxing_add(BarcodeFormat.RSS14);
                        Vector<BarcodeFormat> ONE_D_FORMATS = new Vector<BarcodeFormat>(PRODUCT_FORMATS.size() + 4);
                        ONE_D_FORMATS.addAll(PRODUCT_FORMATS);
                        ONE_D_FORMATS.add(BarcodeFormat.CODE_39);
                        ONE_D_FORMATS.add(BarcodeFormat.CODE_93);
                        ONE_D_FORMATS.add(BarcodeFormat.CODE_128);
                        ONE_D_FORMATS.add(BarcodeFormat.ITF);
                        Vector<BarcodeFormat> QR_CODE_FORMATS = new Vector<BarcodeFormat>(1);
                        QR_CODE_FORMATS.add(BarcodeFormat.QR_CODE);
                        Vector<BarcodeFormat> DATA_MATRIX_FORMATS = new Vector<BarcodeFormat>(1);
                        DATA_MATRIX_FORMATS.add(BarcodeFormat.DATA_MATRIX);

                        // 这里设置可扫描的类型，我这里选择了都支持
                        decodeFormats.addAll(ONE_D_FORMATS);
                        decodeFormats.addAll(QR_CODE_FORMATS);
                        decodeFormats.addAll(DATA_MATRIX_FORMATS);
                    }
                    hints.put(DecodeHintType.POSSIBLE_FORMATS, decodeFormats);
                    // 设置继续的字符编码格式为UTF8
                    // hints.put(DecodeHintType.CHARACTER_SET, "UTF8");
                    // 设置解析配置参数
                    multiFormatReader.setHints(hints);

                    // 开始对图像资源解码
                    Result rawResult = null;
                    try {
                        rawResult = multiFormatReader.decodeWithState(new BinaryBitmap(new HybridBinarizer(new BitmapLuminanceSource(smallBitmap))));
                    } catch (Exception e) {
                        e.printStackTrace();
                    }

                    if (rawResult != null) {

                     //  showSureDialog("识别结果",rawResult.toString());
                        ZxingSdk.setResult(rawResult.toString());
                        finish();

                    } else {
                        Toast.makeText(this, "图片识别失败.", Toast.LENGTH_SHORT).show();
                        finish();
                    }
                }
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * Resize the bitmap
     *
     * @param bitmap
     * @param width
     * @param height
     * @return
     */
    public static Bitmap zoomBitmap(Bitmap bitmap, int width, int height) {
        int w = bitmap.getWidth();
        int h = bitmap.getHeight();
        Matrix matrix = new Matrix();
        float scaleWidth = ((float) width / w);
        float scaleHeight = ((float) height / h);
        matrix.postScale(scaleWidth, scaleHeight);
        Bitmap newbmp = Bitmap.createBitmap(bitmap, 0, 0, w, h, matrix, true);
        return newbmp;
    }


}