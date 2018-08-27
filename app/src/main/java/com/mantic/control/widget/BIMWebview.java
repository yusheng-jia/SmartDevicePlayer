package com.mantic.control.widget;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.net.http.SslError;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.webkit.CookieManager;
import android.webkit.HttpAuthHandler;
import android.webkit.SslErrorHandler;
import android.webkit.WebResourceRequest;
import android.webkit.WebResourceResponse;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import com.baidu.iot.sdk.IoTSDKManager;
import com.baidu.iot.sdk.iam.AccessToken;
import com.mantic.control.api.Url;
import com.mantic.control.utils.Glog;
import com.mantic.control.utils.IoTAppConfigMgr;
import com.mantic.control.utils.MD5Util;
import com.mantic.control.utils.OkHttpUtils;
import com.mantic.control.utils.SharePreferenceUtil;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;

import okhttp3.Response;

/**
 * author: jayson
 * blog: http://blog.csdn.net/jia4525036
 * time: 2017/12/6.
 * desc:
 */

public class BIMWebview extends WebView {
    public static final String TAG = "IAMWebView";
    public static final boolean DEBUG = true;
    private static final String KEY_APP_KEY = "com.baidu.dueriot.APP_KEY";
    private static final String KEY_SECRET_KEY = "com.baidu.dueriot.SECRET_KEY";
    public final static String IAM_LOGIN_BASE_URL = "https://openapi-iot.baidu.com/v1/account/authorize?response_type=code&state=step1&redirect_uri=" + Url.ACCOUNT_URL+"oauth/2.0/baidu/kmyx/login_success&display=mobile";
    public final static String IAM_LOGIN_CODE_BASE_URL = Url.ACCOUNT_URL + "oauth/2.0/baidu/kmyx/login_success?";
    private static final int MSG_SUCCESS = 1;
    private static final int MSG_FAIL = 2;

    private String mClientid;
    private String mClientSecret;

    private ProgressDialog progressBar = new ProgressDialog(this.getContext());

    public interface LoginCallback {
        public void onFinish(boolean isSuccess);
    }

    //TODO 需要替换State字段，和URL
    private String PARAM_STATE = null;
    private String URL = null;
    private LoginCallback mCallback;

    private Handler handler = new Handler(Looper.getMainLooper()){
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case MSG_SUCCESS:
                    mCallback.onFinish(true);
                    break;
                case MSG_FAIL:
                    mCallback.onFinish(false);
                    IoTSDKManager.getInstance().logout();
                    break;
            }
        }
    };

    private WebViewClient mWebviewClient = new WebViewClient() {
        @Override
        public boolean shouldOverrideUrlLoading(WebView view, String url) {
            Glog.i(TAG,"shouldOverrideUrlLoading -> url: " + url);
            String code = parseCode(url);
            if (!TextUtils.isEmpty(code)) {
                getAccessToken(code);
                return true;
            }
            return super.shouldOverrideUrlLoading(view, url);
        }

        @Override
        public void onPageStarted(WebView view, String url, Bitmap favicon) {
            super.onPageStarted(view, url, favicon);
        }

        @Override
        public void onPageFinished(WebView view, String url) {
            super.onPageFinished(view, url);
            if (progressBar.isShowing()) {
                progressBar.dismiss();
            }
            CookieManager cookieManager = CookieManager.getInstance();
            String cookieStr = cookieManager.getCookie(url);
            IoTAppConfigMgr.setIAMCookie(getContext(), cookieStr);
        }

        @Override
        public void onReceivedHttpError(WebView view, WebResourceRequest request, WebResourceResponse errorResponse) {
            super.onReceivedHttpError(view, request, errorResponse);
        }

        @Override
        public void onReceivedHttpAuthRequest(WebView view, HttpAuthHandler handler, String host, String realm) {
            super.onReceivedHttpAuthRequest(view, handler, host, realm);
        }

        @Override
        public void onReceivedSslError(WebView view, SslErrorHandler handler, SslError error) {
            handler.proceed();
        }
    };

    public BIMWebview(Context context) {
        super(context);
    }

    public BIMWebview(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public void login(LoginCallback callback, String loginWaitings) {
        progressBar = ProgressDialog.show(this.getContext(), null, loginWaitings);
        login(callback);
    }

    public void login(LoginCallback callback) {
        this.mCallback = callback;
        if (this.mCallback == null) {
            throw new RuntimeException("Need a LoginCallback !");
        }
        this.clearHistory();
        this.clearFormData();
        this.clearCache(true);
        this.setWebViewClient(mWebviewClient);
        WebSettings settings = getSettings();
        settings.setJavaScriptEnabled(true);
        settings.setCacheMode(WebSettings.LOAD_NO_CACHE);
        URL = assembleLoginUrl();
        this.loadUrl(URL);
    }

    public void setAkSk(String clientid, String clientSecret) {
        this.mClientid = clientid;
        this.mClientSecret = clientSecret;
    }

    @Override
    public void setWebViewClient(WebViewClient client) {
        super.setWebViewClient(mWebviewClient);
    }

    private boolean parseToken(String urlRef) {
        if (TextUtils.isEmpty(urlRef)) return false;
        String[] params = urlRef.split("&");
        if (params != null) {
            Map<String, String> map = new HashMap<>();
            for (String param : params) {
                String[] keyAndValue = param.split("=");
                if (keyAndValue != null && keyAndValue.length == 2) {
                    map.put(keyAndValue[0], keyAndValue[1]);
                }
            }
            if (PARAM_STATE.equals(map.get(AccessToken.KEY_STATE))) {
                IoTAppConfigMgr.putString(getContext(), AccessToken.KEY_ACCESS_TOKEN, map.get(AccessToken.KEY_ACCESS_TOKEN));
                IoTAppConfigMgr.putString(getContext(), AccessToken.KEY_REFRESH_TOKEN, map.get(AccessToken.KEY_REFRESH_TOKEN));
                IoTAppConfigMgr.putString(getContext(), AccessToken.KEY_EXPIRES_IN, map.get(AccessToken.KEY_EXPIRES_IN));
                return true;
            }
        }
        return false;
    }

    private String parseCode(String urlStr) {
        try {
            URL url = new URL(urlStr);
            String query = url.getQuery();
            if (!TextUtils.isEmpty(query)) {
                String[] params = query.split("&");
                if (params != null && params.length > 0) {
                    for (String param : params) {
                        String[] keyAndValue = param.split("=");
                        if (keyAndValue.length == 2) {
                            if ("code".equals(keyAndValue[0])) {
                                return keyAndValue[1];
                            }
                        }
                    }
                }
            }
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }
        return null;
    }

    private String assembleLoginUrl() {
        StringBuilder loginUrlSb = new StringBuilder();
        loginUrlSb.append(IAM_LOGIN_BASE_URL);
        String clientid = !TextUtils.isEmpty(mClientid) ? mClientid : getMetaValue(getContext(), KEY_APP_KEY);
        loginUrlSb.append("&client_id=" + clientid);
        if (DEBUG) {
            Glog.i(TAG, "loginUrl: " + loginUrlSb.toString());
        }
        return loginUrlSb.toString();
    }

    private void getAccessToken(String code) {
        StringBuilder codeUrlSb = new StringBuilder();
        codeUrlSb.append(IAM_LOGIN_CODE_BASE_URL);
        codeUrlSb.append("&code=" + code);
//        String clientid = !TextUtils.isEmpty(mClientid) ? mClientid : getMetaValue(getContext(), KEY_APP_KEY);
//        String clientSecret = !TextUtils.isEmpty(mClientSecret) ? mClientSecret : getMetaValue(getContext(), KEY_SECRET_KEY);
//        codeUrlSb.append("&client_id=" + clientid);
//        codeUrlSb.append("&client_secret=" + clientSecret);
        final String codeUrl = codeUrlSb.toString();
        if (DEBUG) {
            Glog.i(TAG, "codeUrl: " + codeUrl);
        }


        OkHttpUtils.get(codeUrl,new OkHttpUtils.ResultCallback<Response>(){
            @Override
            public void onFailure(Exception e) {
                handler.sendEmptyMessage(MSG_FAIL);
            }

            @Override
            public void onSuccess(Response response) {
//                handler.sendEmptyMessage(MSG_SUCCESS);
                Glog.i(TAG, "onSuccess:" + response.headers());

                String access_token = response.header("access_token");
                String expires_in = response.header("expires_in");
                String refresh_token = response.header("refresh_token");
                String avatar = response.header("avatar");
                String nickname = response.header("nickname");

                Glog.i(TAG,"access_token:" + access_token);
                Glog.i(TAG,"expires_in:" + expires_in);
                Glog.i(TAG,"refresh_token:" + refresh_token);

                Glog.i(TAG,"Atatar:" + avatar);
                Glog.i(TAG,"Nickname:" + MD5Util.unicodeDecode(nickname));

                IoTAppConfigMgr.putString(getContext(), AccessToken.KEY_ACCESS_TOKEN, access_token);
                IoTAppConfigMgr.putString(getContext(), AccessToken.KEY_REFRESH_TOKEN, refresh_token);
                IoTAppConfigMgr.putString(getContext(), AccessToken.KEY_EXPIRES_IN, expires_in);
                SharePreferenceUtil.setUserName(getContext(),MD5Util.unicodeDecode(nickname));
                SharePreferenceUtil.setUserPhoto(getContext(),avatar);
                SharePreferenceUtil.setKeyAutoKey(getContext(),"baidu");

                handler.sendEmptyMessage(MSG_SUCCESS);

            }
        });

//        new Thread() {
//            @Override
//            public void run() {
//                HttpURLConnection connection = null;
//                try {
//                    URL url = new URL(codeUrl);
//                    connection = (HttpURLConnection) url.openConnection();
//                    connection.setRequestMethod("GET");
//                    connection.setConnectTimeout(8000);
//                    connection.setReadTimeout(8000);
//
//                    InputStream in = connection.getInputStream();
//                    BufferedReader bufReader = new BufferedReader(new InputStreamReader(in));
//                    StringBuilder response = new StringBuilder();
//                    String line = null;
//                    while ((line = bufReader.readLine()) != null) {
//                        response.append(line);
//                    }
//
//                    Glog.i(TAG,"response: " + response);
////                    JSONObject responseJson = new JSONObject(response.toString());
////                    IoTAppConfigMgr.putString(getContext(), AccessToken.KEY_ACCESS_TOKEN, responseJson.optString(AccessToken.KEY_ACCESS_TOKEN));
////                    IoTAppConfigMgr.putString(getContext(), AccessToken.KEY_REFRESH_TOKEN, responseJson.optString(AccessToken.KEY_REFRESH_TOKEN));
////                    IoTAppConfigMgr.putString(getContext(), AccessToken.KEY_EXPIRES_IN, responseJson.optString(AccessToken.KEY_EXPIRES_IN));
//                    handler.sendEmptyMessage(MSG_SUCCESS);
//                } catch (Exception e) {
//                    handler.sendEmptyMessage(MSG_FAIL);
//                    e.printStackTrace();
//                } finally {
//                    if (connection != null) {
//                        connection.disconnect();
//                    }
//                }
//            }
//        }.start();
    }

    private String getMetaValue(Context context, String metaKey) {
        Bundle metaData = null;
        String metaValue = null;
        if (context == null || metaKey == null) {
            return null;
        }
        try {
            ApplicationInfo ai = context.getPackageManager()
                    .getApplicationInfo(context.getPackageName(), PackageManager.GET_META_DATA);
            if (null != ai) {
                metaData = ai.metaData;
            }
            if (null != metaData) {
                metaValue = metaData.getString(metaKey);
            }
        } catch (PackageManager.NameNotFoundException e) {
        }
        return metaValue;
    }
}
