package com.mantic.control.wxapi;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.widget.Toast;

import com.baidu.iot.sdk.IoTSDKManager;
import com.baidu.iot.sdk.iam.AccessToken;
import com.mantic.control.ManticApplication;
import com.mantic.control.R;
import com.mantic.control.activity.LoadingActivity;
import com.mantic.control.api.Url;
import com.mantic.control.utils.AccountSettings;
import com.mantic.control.utils.DeviceIdUtil;
import com.mantic.control.utils.Glog;
import com.mantic.control.utils.MD5Util;
import com.mantic.control.utils.OkHttpUtils;
import com.mantic.control.utils.SharePreferenceUtil;
import com.tencent.mm.opensdk.modelbase.BaseReq;
import com.tencent.mm.opensdk.modelbase.BaseResp;
import com.tencent.mm.opensdk.modelmsg.SendAuth;
import com.tencent.mm.opensdk.openapi.IWXAPIEventHandler;

import okhttp3.Response;

public class WXEntryActivity extends Activity implements IWXAPIEventHandler {
    private static final String TAG = "WXEntryActivity";
    /*微信原生登录功能*/
//    public static final String WX_AUTH_LOGIN_URL = "https://api.weixin.qq.com/sns/oauth2/access_token";
//    public static final String WX_USERINFO_URL = "https://api.weixin.qq.com/sns/userinfo";
//    public static final String WX_APP_ID = "wx150fdc4e94a2e1d0";
//    public static final String WX_APP_KEY = "07c2fb0c223a0edf25b7a87d419adeab";
    /*微信原生登录功能*/

    /*对接百度PASS的微信功能*/
    public static final String WX_COOMAAN_LOGIN_URL = Url.ACCOUNT_URL + "oauth/2.0/wechat/kmyx/login_success";
    /*对接百度PASS的微信功能*/

    private String accessToken, openId;
    private AccountSettings mAccountSettings;
    private String mDeviceId;
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        ((ManticApplication)getApplicationContext()).api.handleIntent(getIntent(), this);
        mAccountSettings = AccountSettings.getInstance(this);
        mDeviceId=DeviceIdUtil.getPhoneDeviceId(getApplicationContext());
//        ActivityManager.getAppManager().addActivity(this);
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);

    }

    @Override
    public void onReq(BaseReq baseReq) {

    }

    @Override
    public void onResp(BaseResp resp) {
        int result = 0;

        Glog.i(TAG, "baseresp.getType = " + resp.getType() + "resp.errCode:" + resp.errCode);
        if (resp.errCode == BaseResp.ErrCode.ERR_OK) {
            SendAuth.Resp newResp = (SendAuth.Resp) resp;
            //获取微信传回的code
            String code = newResp.code;
            Glog.i(TAG," -- >code: " + code);
            if (!TextUtils.isEmpty(newResp.code)) {
                //获取用户数据
                checkLogin(code);
            }
            result = R.string.errcode_success;
            Glog.i(TAG, "bbbbbbbbbbbbbbbbbbbbbbbbj");
        } else {
            switch (resp.errCode) {

                case BaseResp.ErrCode.ERR_USER_CANCEL:
                    result = R.string.errcode_cancel;
                    break;
                case BaseResp.ErrCode.ERR_AUTH_DENIED:
                    result = R.string.errcode_deny;
                    break;
                case BaseResp.ErrCode.ERR_UNSUPPORT:
                    result = R.string.errcode_unsupported;
                    break;
                default:
                    result = R.string.errcode_unknown;
                    break;
            }

            finish();
        }
        Glog.i(TAG, "teset------");
        Toast.makeText(WXEntryActivity.this, result, Toast.LENGTH_SHORT).show();

    }

    private void goToGetMsg() {
        Toast.makeText(this, "goToGetMsg", Toast.LENGTH_LONG).show();

    }


    public void checkLogin(String code) {
        //获取授权 access_token

        StringBuffer loginUrl = new StringBuffer();
//        loginUrl.append(WX_AUTH_LOGIN_URL).append("?appid=")
//                .append(WX_APP_ID).append("&secret=")
//                .append(WX_APP_KEY).append("&code=").append(code)
//                .append("&grant_type=authorization_code");

        loginUrl.append(WX_COOMAAN_LOGIN_URL).append("?code=")
                .append(code);

        getInfo(1, loginUrl.toString());


    }

    private void getInfo(final int type, String path) {

        OkHttpUtils.get(path, new OkHttpUtils.ResultCallback<Response>() {

            @Override
            public void onFailure(Exception e) {
                Toast.makeText(WXEntryActivity.this, "登录失败", Toast.LENGTH_SHORT).show();
                Glog.i(TAG, "onFailure:" + e);
//                ActivityManager.getAppManager().finishActivity(WXEntryActivity.this);
            }

            @Override
            public void onSuccess(Response response) {
                Glog.i(TAG, "onSuccess:" + response);
                Glog.i(TAG,"Headers:" + response.header("access_token"));
//                JSONObject grantObj = null;
//                try {
//                    grantObj = new JSONObject(response);
//                } catch (JSONException e) {
//                    e.printStackTrace();
//                }
//                if (type == 1) {
//                    String errcode = grantObj.optString("errcode");
//                    openId = grantObj.optString("openid");
//                    accessToken = grantObj.optString("access_token");
//                    String expiresIn = grantObj.optString("expires_in");
//                    String refreshToken = grantObj.optString("refresh_token");
//                    String scope = grantObj.optString("scope");
//                    Glog.i(TAG, "openId:" + openId + ";errcode:" + errcode + ";accessToken:" + accessToken);
//                    StringBuffer userUrl = new StringBuffer();
//                    userUrl.append(WX_USERINFO_URL).append("?access_token=").append(accessToken).append("&openid=").append(openId);
//
//                    getInfo(2, userUrl.toString());
//                } else {
//
//                    String nickname = grantObj.optString("nickname");
//                    String sex = grantObj.optString("sex");
//                    String userImg = grantObj.optString("headimgurl");
//                    String unionid = grantObj.optString("unionid");
//
//                    List<OkHttpUtils.Param> params = new ArrayList<OkHttpUtils.Param>();
//                    params.add(new OkHttpUtils.Param("openId", openId));
//                    params.add(new OkHttpUtils.Param("accessToken", accessToken));
//                    params.add(new OkHttpUtils.Param("type", "TXWX"));
//                    params.add(new OkHttpUtils.Param("screenName", nickname));
//                    params.add(new OkHttpUtils.Param("gender", sex));
//                    params.add(new OkHttpUtils.Param("profileImageUrl", userImg));
//                    params.add(new OkHttpUtils.Param("meId", mDeviceId));
//                    String path = AccountConstant.BASE_URL + "regis/thirdAcc";
//                    doLogin(path, params);
//                }

                String access_token = response.header("access_token");
                String expires_in = response.header("expires_in");
                String refresh_token = response.header("refresh_token");
                String avatar = response.header("avatar");
                String nickname = response.header("nickname");
//                AccountSettings.getInstance(getApplicationContext()).setAutoKey("txwx");
//                AccountSettings.getInstance(getApplicationContext()).setHeadUrl(avatar);
//                AccountSettings.getInstance(getApplicationContext()).setNickName(MD5Util.unicodeDecode(nickname));
                SharePreferenceUtil.setKeyAutoKey(getApplicationContext(),"wechat");
                SharePreferenceUtil.setUserName(getApplicationContext(),MD5Util.unicodeDecode(nickname));
                SharePreferenceUtil.setUserPhoto(getApplicationContext(),avatar);

                AccessToken accessToken = new AccessToken();
                accessToken.setAccessToken(access_token);
                if (!TextUtils.isEmpty(expires_in) && !"null".equals(expires_in)) {
                    accessToken.setExpiresTime(Long.parseLong(expires_in));
                }

                accessToken.setRefreshToken(refresh_token);
                IoTSDKManager.getInstance().setAccessToken(accessToken);

                Intent intent1 = new Intent(WXEntryActivity.this, LoadingActivity.class).setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK|Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(intent1);
                finish();

            }

        });

    }

//    private void doLogin(String path, List<OkHttpUtils.Param> paramList) {
//        OkHttpUtils.post(path, new OkHttpUtils.ResultCallback<String>() {
//            @Override
//            public void onFailure(Exception e) {
//
//                Toast.makeText(WXEntryActivity.this, "登录失败", Toast.LENGTH_SHORT).show();
//                Glog.i(TAG, "login onFailure:" + e.getMessage());
//            }
//
//            @Override
//            public void onSuccess(String response) {
//                AccountInfo accountInfo= AccountInfo.parseFrom(response, WXEntryActivity.this);
//                accountInfo.setDeviceId(mDeviceId);
//                mAccountSettings.setAccountInfo(accountInfo);
//                Toast.makeText(WXEntryActivity.this, "登录成功", Toast.LENGTH_SHORT).show();
//
//
////                if(IoTSDKManager.getInstance().isLogin()) {//判断百度是否登录
////                    Intent intent = new Intent(WXEntryActivity.this, BaiduLoginActivity.class);
////                    startActivity(intent);
////                    finish();
////                }else{
//                Intent intent1 = new Intent(WXEntryActivity.this, ManticBindActivity.class);
//                startActivity(intent1);
//                finish();
////                }
//
//            }
//        }, paramList);

//    }


}