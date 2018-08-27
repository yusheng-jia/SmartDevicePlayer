package com.mantic.control.fragment;

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
import android.widget.LinearLayout;

import com.mantic.control.R;
import com.mantic.control.utils.Glog;

import static com.mantic.control.fragment.MySkillsFragment.SKILL_URL;

/**
 * author: jayson
 * blog: http://blog.csdn.net/jia4525036
 * time: 2018/1/15.
 * desc:
 */

public class SkillAllFragment extends BaseSlideFragment implements View.OnClickListener {

    private  WebView mWebview;
    WebSettings mWebSettings;
    //    TextView beginLoading,endLoading,loading,mtitle;
    String url;

    @Override
    protected void initView(View view) {
        super.initView(view);
        url = this.getArguments().getString("url");
        mWebview = (WebView) view.findViewById(R.id.webview_skilldetail);
        LinearLayout fragment_skill_detail_back = (LinearLayout) view.findViewById(R.id.fragment_skill_detail_back);
        fragment_skill_detail_back.setOnClickListener(this);

        mWebSettings = mWebview.getSettings();
        mWebSettings.setLayoutAlgorithm(WebSettings.LayoutAlgorithm.NARROW_COLUMNS);
        mWebSettings.setUseWideViewPort(true);
        mWebSettings.setJavaScriptEnabled(true);
        mWebview.addJavascriptInterface(new SkillAllFragment.JSInterface1(),"allto");
        mWebview.loadUrl(SKILL_URL+"apps/"+url);

        //设置不用系统浏览器打开,直接显示在当前Webview
        mWebview.setWebViewClient(new WebViewClient() {
            @Override
            public boolean shouldOverrideUrlLoading(WebView view, String url) {
                Glog.i("jys","url: " + url);
                view.loadUrl(url);
                return true;
            }
        });
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
        return R.layout.fragment_skilldetail;
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

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.fragment_skill_detail_back:
                if (mActivity instanceof FragmentEntrust) {
                    ((FragmentEntrust) mActivity).popFragment(getTag());
                }
                break;
        }
    }

    class JSInterface1{
        //JavaScript调用此方法
        @JavascriptInterface
        public void startFragment(String url){
            Glog.i("jys","all -> startFragment");
            SkillDetailFragment sdFragment = new SkillDetailFragment();
            if (url != null) {
                Bundle bundle = new Bundle();
                bundle.putString("url",url);
                sdFragment.setArguments(bundle);
                if(getActivity() instanceof FragmentEntrust){
                    ((FragmentEntrust) getActivity()).pushFragment(sdFragment,"skill_detail");
                }
            }

        }
    }
}
