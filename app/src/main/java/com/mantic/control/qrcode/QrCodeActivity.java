package com.mantic.control.qrcode;

import android.Manifest;
import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.res.AssetFileDescriptor;
import android.graphics.PointF;
import android.hardware.Camera;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.net.Uri;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Vibrator;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.annotation.RequiresApi;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.PermissionChecker;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;

import com.mantic.control.R;
import com.mantic.control.activity.BaseActivity;
import com.mantic.control.activity.Network5GActivity;
import com.mantic.control.activity.NetworkConfigActivity;
import com.mantic.control.qrcode.qrcodereadview.QRCodeReaderView;
import com.mantic.control.utils.Glog;
import com.mantic.control.utils.IoTStringUtils;
import com.mantic.control.utils.SharePreferenceUtil;
import com.mantic.control.widget.CustomDialog;

import java.io.IOException;
import java.util.Map;

/**
 * jiayusheng modify 0711
 *
 */
public class QrCodeActivity extends BaseActivity implements ActivityCompat.OnRequestPermissionsResultCallback, QRCodeReaderView.OnQRCodeReadListener {
    private static final String TAG = QrCodeActivity.class.getSimpleName();
    private static final int MY_PERMISSION_REQUEST_CAMERA = 0;
    public static final String RESULT_TYPE = "result_type";
    public static final String RESULT_STRING = "result_string";
    public static final int RESULT_SUCCESS = 1;
    public static final int RESULT_FAILED = 2;
    public static final int RESULT_FINISH = 3;
    public static final int REQUEST_ENABLE_BT = 4;
    private static final int REQUEST_SYSTEM_PICTURE = 0;
    private static final int REQUEST_PICTURE = 1;
    public static final int MSG_DECODE_SUCCEED = 1;
    public static final int MSG_DECODE_FAIL = 2;

    private QRCodeReaderView qrCodeReaderView;
    /**
     * 声音和振动相关参数
     */
    private static final float BEEP_VOLUME = 0.10f;
    private static final long VIBRATE_DURATION = 200L;
    private MediaPlayer mMediaPlayer;
    private boolean mPlayBeep;
    private boolean mVibrate;
    private ViewGroup mainLayout;
    private boolean hasDecode = true;
    private Context mContext;
    /* 取得默认的蓝牙适配器 */
    private BluetoothAdapter mBtAdapter = BluetoothAdapter.getDefaultAdapter();


    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    @Override
    public void onCreate(Bundle savedInstanceState) {
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        super.onCreate(savedInstanceState);
        mContext = this;
        showTitle(R.string.mantic_bind_title);
//        showNext(getString(R.string.logout));
//        getNext().setOnClickListener(new OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                showLoginOut();
//            }
//        });
        setContentView(R.layout.activity_qr_code);
        mainLayout = (ViewGroup) findViewById(R.id.main_layout);
//        if (isWifi5G()){
//            start5G();
//        }
//        Glog.i(TAG,"Camera 权限:" + selfPermissionGranted(Manifest.permission.CAMERA));
        if (selfPermissionGranted(Manifest.permission.CAMERA)) {
            initQRCodeReaderView();
        } else {
            requestCameraPermission();
        }
        mPlayBeep = true;
        mVibrate = true;
        initBeepSound();
        // If BT is not on, request that it be enabled.
        if (!mBtAdapter.isEnabled()) {
            Intent enableIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
            startActivityForResult(enableIntent, REQUEST_ENABLE_BT);
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    private boolean isWifi5G(){
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP ){
            WifiManager wifiManager = (WifiManager) getApplicationContext().getSystemService(Context.WIFI_SERVICE);
            WifiInfo wifiInfo = wifiManager.getConnectionInfo();
            String frequency = String.valueOf(wifiInfo.getFrequency());
            if (!frequency.isEmpty()){
                String first = frequency.substring(0,1);
                if (first.equals("5")){
                    return true;
                }else {
                    return false;
                }
            }else {
                return false;
            }
        }else {
            return false;
        }
    }
    private void start5G(){
        Intent intent = new Intent(QrCodeActivity.this, Network5GActivity.class);
        startActivity(intent);
    }

    public boolean selfPermissionGranted(String permission) {
        // For Android < Android M, self permissions are always granted.
        boolean result = true;

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {

            if (this.getApplicationInfo().targetSdkVersion >= Build.VERSION_CODES.M) {
                // targetSdkVersion >= Android M, we can
                // use Context#checkSelfPermission
                result = this.checkSelfPermission(permission)
                        == PackageManager.PERMISSION_GRANTED;
            } else {
                // targetSdkVersion < Android M, we have to use PermissionChecker
                result = PermissionChecker.checkSelfPermission(this, permission)
                        == PermissionChecker.PERMISSION_GRANTED;
            }
        }else {
            result = cameraIsCanUse();
        }

        return result;
    }

    /**
     *  返回true 表示可以使用  返回false表示不可以使用
     */
    public boolean cameraIsCanUse() {
        boolean isCanUse = true;
        Camera mCamera = null;
        try {
            mCamera = Camera.open();
            Camera.Parameters mParameters = mCamera.getParameters(); //针对魅族手机
            mCamera.setParameters(mParameters);
        } catch (Exception e) {
            isCanUse = false;
        }

        if (mCamera != null) {
            try {
                mCamera.release();
            } catch (Exception e) {
                e.printStackTrace();
                return isCanUse;
            }
        }
        return isCanUse;
    }

    @Override protected void onResume() {
        super.onResume();

        if (qrCodeReaderView != null) {
            qrCodeReaderView.startCamera();
        }
    }

    @Override protected void onPause() {
        super.onPause();

        if (qrCodeReaderView != null) {
            qrCodeReaderView.stopCamera();
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        // User chose not to enable Bluetooth.
        if (requestCode == REQUEST_ENABLE_BT && resultCode == Activity.RESULT_CANCELED) {
            finish();
//            overridePendingTransition(R.anim.push_right_in, R.anim.push_left_out);
            return;
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    private void initBeepSound() {
        if (mPlayBeep && mMediaPlayer == null) {
            // The volume on STREAM_SYSTEM is not adjustable, and users found it too loud,
            // so we now play on the music stream.
            setVolumeControlStream(AudioManager.STREAM_MUSIC);
            mMediaPlayer = new MediaPlayer();
            mMediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
            mMediaPlayer.setOnCompletionListener(mBeepListener);

            AssetFileDescriptor file = getResources().openRawResourceFd(R.raw.beep);
            try {
                mMediaPlayer.setDataSource(file.getFileDescriptor(), file.getStartOffset(), file.getLength());
                file.close();
                mMediaPlayer.setVolume(BEEP_VOLUME, BEEP_VOLUME);
                mMediaPlayer.prepare();
            } catch (IOException e) {
                mMediaPlayer = null;
            }
        }
    }

    private void playBeepSoundAndVibrate() {
        if (mPlayBeep && mMediaPlayer != null) {
            mMediaPlayer.start();
        }
        if (mVibrate) {
            Vibrator vibrator = (Vibrator) getSystemService(VIBRATOR_SERVICE);
            vibrator.vibrate(VIBRATE_DURATION);
        }
    }

    /**
     * When the beep has finished playing, rewind to queue up another one.
     */
    private final MediaPlayer.OnCompletionListener mBeepListener = new MediaPlayer.OnCompletionListener() {
        public void onCompletion(MediaPlayer mediaPlayer) {
            mediaPlayer.seekTo(0);
        }
    };

    @Override public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,
                                                     @NonNull int[] grantResults) {
        if (requestCode != MY_PERMISSION_REQUEST_CAMERA) {
            return;
        }

        if (grantResults.length == 1 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
//            Snackbar.make(mainLayout, "Camera permission was granted.", Snackbar.LENGTH_SHORT).show();
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M){
                initQRCodeReaderView();
            }else {
                Snackbar.make(mainLayout, "检测相机权限被拒绝，请到设置下开启相机权限重试", Snackbar.LENGTH_LONG).show();
            }

        } else {
            CustomDialog.Builder mBuilder = new CustomDialog.Builder(mContext);
            mBuilder.setTitle(mContext.getString(R.string.dialog_btn_prompt));
            mBuilder.setMessage(mContext.getString(R.string.camera_permission_content));
            mBuilder.setPositiveButton(getString(R.string.goto_setting), new CustomDialog.Builder.DialogPositiveClickListener() {
                @Override
                public void onPositiveClick(final CustomDialog dialog) {
                    dialog.dismiss();
                    Intent intent =  new Intent();
                    intent.setAction(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
                    Uri uri = Uri.fromParts("package", "com.mantic.control", null);
                    intent.setData(uri);
                    startActivity(intent);
                    finish();

                }
            });
            mBuilder.setNegativeButton(getString(R.string.cancel), new CustomDialog.Builder.DialogNegativeClickListener() {
                @Override
                public void onNegativeClick(CustomDialog dialog) {
                    dialog.dismiss();
                    finish();
                }
            });
            mBuilder.create().show();
//            Snackbar.make(mainLayout, "相机授权被拒绝了，请到设置里打开授权功能！", Snackbar.LENGTH_INDEFINITE)
//                    .show();
        }
    }

    private void handleResult(String resultString) {
        Map<String, String> deviceInfo = IoTStringUtils.parseQRCode(resultString);
        String token = deviceInfo.get("token");
        String deviceUuid = deviceInfo.get("deviceUuid");
        String deviceLc = deviceInfo.get("lc");
        Glog.i("jys","token: " + token + "--- deviceUuid: " + deviceUuid + "--- lc: " + deviceLc);
        if (deviceUuid != null) {
            SharePreferenceUtil.setQrCodeId(this,deviceUuid);

        }
        if (token != null){
            SharePreferenceUtil.setDeviceToken(this,token);
        }

        if (deviceLc != null){
            SharePreferenceUtil.setDeviceLc(this,deviceLc.replaceAll("(\\r\\n|\\r|\\n|\\n\\r)",""));
        }else {
            SharePreferenceUtil.setDeviceLc(this,null);
        }

        Intent it = new Intent(this, NetworkConfigActivity.class);
        startActivity(it);
//            Intent resultIntent = new Intent();
//            Bundle bundle = new Bundle();
//            bundle.putInt(RESULT_TYPE, RESULT_SUCCESS);
//            bundle.putString(RESULT_STRING, resultString);
//            resultIntent.putExtras(bundle);
//            QrCodeActivity.this.setResult(RESULT_OK, resultIntent);
        this.finish();
    }

    private void requestCameraPermission() {
        Glog.i(TAG,"requestCameraPermission...");
        if (ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.CAMERA)) {
            Snackbar.make(mainLayout, "Camera access is required to display the camera preview.",
                    Snackbar.LENGTH_INDEFINITE).setAction("OK", new View.OnClickListener() {
                @Override public void onClick(View view) {
                    ActivityCompat.requestPermissions(QrCodeActivity.this, new String[] {
                            Manifest.permission.CAMERA
                    }, MY_PERMISSION_REQUEST_CAMERA);
                }
            }).show();
        } else {
//            Snackbar.make(mainLayout, "Permission is not available. Requesting camera permission.",
//                    Snackbar.LENGTH_SHORT).show();
            ActivityCompat.requestPermissions(this, new String[] {
                    Manifest.permission.CAMERA
            }, MY_PERMISSION_REQUEST_CAMERA);
        }
    }

    private void initQRCodeReaderView() {
        Glog.i(TAG,"initQRCodeReaderView...");
        View content = getLayoutInflater().inflate(R.layout.content_decoder, mainLayout, true);

        qrCodeReaderView = (QRCodeReaderView) content.findViewById(R.id.qrdecoderview);
        qrCodeReaderView.setAutofocusInterval(2000L);
        qrCodeReaderView.setOnQRCodeReadListener(this);
        qrCodeReaderView.setBackCamera();
        qrCodeReaderView.startCamera();
    }

    @Override
    public void onQRCodeRead(String text, PointF[] points) {
        Glog.i(TAG,"RESULT TEXT :    " + text);
        if (text != null && !text.isEmpty() && hasDecode){
            playBeepSoundAndVibrate();
            handleResult(text);
            hasDecode = false;
        }
    }
}