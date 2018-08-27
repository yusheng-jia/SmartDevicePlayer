package com.mantic.control.fragment;

import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.JavascriptInterface;
import android.webkit.WebChromeClient;
import android.webkit.WebResourceError;
import android.webkit.WebResourceRequest;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import com.mantic.control.R;
import com.mantic.control.activity.JdShoppingActivity;
import com.mantic.control.activity.JdSmartHomeActivity;
import com.mantic.control.utils.Glog;
import com.mantic.control.utils.SharePreferenceUtil;

/**
 * author: jayson
 * blog: http://blog.csdn.net/jia4525036
 * time: 2018/1/10.
 * desc:
 */

public class MySkillsFragment extends BaseFragment{
    private WebView mWebview;
    WebSettings mWebSettings;
    //测试服务器 http://40.125.202.164/
    //正式服务器 http://maan.ai/
    public static String SKILL_URL = "http://maan.ai/";
    private String url = "apps/aiskill.html";
    private String jdUrl = "apps/aiskillJd.html";
//    TextView beginLoading,endLoading,loading,mtitle;

    @Override
    protected void initView(View view) {
        super.initView(view);
        mWebview = (WebView) view.findViewById(R.id.webview_skill);

        mWebSettings = mWebview.getSettings();
        mWebSettings.setLayoutAlgorithm(WebSettings.LayoutAlgorithm.NARROW_COLUMNS);
        mWebSettings.setUseWideViewPort(true);
        mWebSettings.setJavaScriptEnabled(true);

        if (SharePreferenceUtil.getJdSkillSwitch(getContext())){
            mWebview.loadUrl(SKILL_URL+jdUrl);
        }else {
            mWebview.loadUrl(SKILL_URL+url);
        }


        //设置不用系统浏览器打开,直接显示在当前Webview
        mWebview.setWebViewClient(new WebViewClient() {
            @Override
            public boolean shouldOverrideUrlLoading(WebView view, String url) {
                Glog.i("jys","url: " + url);
                view.loadUrl(url);
                return true;
            }
        });
        mWebview.addJavascriptInterface(new JSInterface(),"tonew");
        //设置WebChromeClient类
        mWebview.setWebChromeClient(new WebChromeClient() {


            //获取网站标题
            @Override
            public void onReceivedTitle(WebView view, String title) {
                System.out.println("标题在这里");
            }


            //获取加载进度
            @Override
            public void onProgressChanged(WebView view, int newProgress) {
                if (newProgress < 100) {
                    String progress = newProgress + "%";
                } else if (newProgress == 100) {
                    String progress = newProgress + "%";
                }
            }
        });


        //设置WebViewClient类
        mWebview.setWebViewClient(new WebViewClient() {
            //设置加载前的函数
            @Override
            public void onPageStarted(WebView view, String url, Bitmap favicon) {

                System.out.println("开始加载了" + url);

            }

            //设置结束加载函数
            @Override
            public void onPageFinished(WebView view, String url) {

            }
            @Override
            public void onReceivedError(WebView view, WebResourceRequest request, WebResourceError error) {
                super.onReceivedError(view, request, error);
                view.loadUrl("file:///android_asset/error.html");
            }
        });

    }

    @Override
    protected int setLayoutResourceId() {
        return R.layout.fragment_skills;
    }

    @Override
    public void onDestroy() {
        if (mWebview != null) {
            mWebview.loadDataWithBaseURL(null, "", "text/html", "utf-8", null);
            mWebview.clearHistory();

            ((ViewGroup) mWebview.getParent()).removeView(mWebview);
            mWebview.destroy();
            mWebview = null;
        }
        super.onDestroy();
    }

    class JSInterface{
        //JavaScript调用此方法
        @JavascriptInterface
        public void startFragment(String url){
            Glog.i("jys","skill -> startFragment" + url);
            if (url.equals("../skilldetail/jdhome.html")){
                Intent intent = new Intent(getContext(), JdSmartHomeActivity.class);
                startActivity(intent);
            }else if (url.equals("../skilldetail/jdshopping.html")){
                Intent intent = new Intent(getContext(), JdShoppingActivity.class);
                startActivity(intent);
            }else {
                SkillDetailFragment sdFragment = new SkillDetailFragment();
                Bundle bundle = new Bundle();
                bundle.putString("url",url);
                sdFragment.setArguments(bundle);
                if(getActivity() instanceof FragmentEntrust){
                    ((FragmentEntrust) getActivity()).pushFragment(sdFragment,"skill_detail");
                }
            }
        }
        @JavascriptInterface
        public void startAllFragment(String url){
            Glog.i("jys","skill -> startAllFragment");
            SkillAllFragment allFragment = new SkillAllFragment();
            if (url != null) {
                Bundle bundle = new Bundle();
                bundle.putString("url",url);
                allFragment.setArguments(bundle);
                if(getActivity() instanceof FragmentEntrust){
                    ((FragmentEntrust) getActivity()).pushFragment(allFragment,"skill_all");
                }
            }

        }
        @JavascriptInterface
        public void reLoad(){
            Glog.i("jys","detail -> reLoad");
            mWebview.post(new Runnable() {
                @Override
                public void run() {
                    if (SharePreferenceUtil.getJdSkillSwitch(getContext())){
                        mWebview.loadUrl(SKILL_URL+jdUrl);
                    }else {
                        mWebview.loadUrl(SKILL_URL+url);
                    }
                }
            });
        }
    }
}
