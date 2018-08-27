package com.mantic.control.listener;

import android.app.Activity;
import android.content.Intent;
import android.widget.Toast;
import com.mantic.antservice.listener.GetUnbindDeviceInfoListener;
import com.mantic.control.activity.MainActivity;
import com.mantic.control.R;


public class GetDeviceInfoListener implements GetUnbindDeviceInfoListener {
    private final static String TAG = "GetDeviceInfoListener";
    private Activity mContext;

    public GetDeviceInfoListener(Activity context) {
        mContext = context;
    }

    @Override
    public void onSuccess(String uuid) {
        Intent it = new Intent(mContext, MainActivity.class);
        mContext.startActivityForResult(it, 1001);
        mContext.finish();
    }

    @Override
    public void onFailed() {
        /*临时添加代码防止进不到主界面，正式版本要去掉*/
        Intent it = new Intent(mContext, MainActivity.class);
        mContext.startActivityForResult(it, 1001);
        mContext.finish();
        Toast.makeText(mContext, mContext.getString(R.string.uuid_token_err), Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onError() {
        Toast.makeText(mContext, mContext.getString(R.string.uuid_token_err), Toast.LENGTH_SHORT).show();
    }
}
