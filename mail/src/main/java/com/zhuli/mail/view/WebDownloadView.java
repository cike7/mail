package com.zhuli.mail.view;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ObjectAnimator;
import android.animation.ValueAnimator;
import android.annotation.SuppressLint;
import android.app.DownloadManager;
import android.content.Context;
import android.content.IntentFilter;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.webkit.DownloadListener;
import android.webkit.JavascriptInterface;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.FrameLayout;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.zhuli.mail.R;
import com.zhuli.mail.mail.LogInfo;
import com.zhuli.mail.receiver.DownloadCompleteReceiver;


/**
 * Copyright (C) 王字旁的理
 * Date: 2021/12/21
 * Description: 消息
 * Author: zl
 */
public class WebDownloadView extends FrameLayout {

    private FrameLayout titleLayout;
    private WebView webView;
    private Animator anim;
    private boolean isOpen = false;

    public WebDownloadView(@NonNull Context context) {
        this(context, null, 0);
    }

    public WebDownloadView(@NonNull Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public WebDownloadView(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        LayoutInflater.from(context).inflate(R.layout.view_web_download, this, true);
        titleLayout = findViewById(R.id.frame_download_title);
        webView = findViewById(R.id.web_download_view);
        initWebView(context);
    }


    private float moveSpeed;
    private float moveStartTime;
    private float startPos;

    @Override
    public boolean dispatchTouchEvent(MotionEvent ev) {

        if (ev.getAction() == MotionEvent.ACTION_DOWN) {

            moveStartTime = System.currentTimeMillis();
            startPos = ev.getRawY();

        } else if (ev.getAction() == MotionEvent.ACTION_UP) {

            moveSpeed = (ev.getRawY() - startPos) / (System.currentTimeMillis() - moveStartTime);

            LogInfo.e("moveSpeed : " + moveSpeed);

            if (ev.getRawY() < titleLayout.getMeasuredHeight() && isOpen) {
                open(null);
            }
        } else if (ev.getAction() == MotionEvent.ACTION_MOVE) {
            //拦截所有父控件Touch事件
            getParent().requestDisallowInterceptTouchEvent(true);


        }
        return super.dispatchTouchEvent(ev);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        anim = getAnimatorDown(this, MeasureSpec.getSize(heightMeasureSpec));
        setTranslationY(MeasureSpec.getSize(heightMeasureSpec));
    }

    public void open(String url) {
        anim.start();
        if (url != null) {
            webView.loadUrl(url);
        }
    }

    private Animator getAnimatorDown(View view, float endValue) {
        LogInfo.e(endValue);
        ValueAnimator animator = ValueAnimator.ofFloat(0, endValue);
        animator.setDuration(300);
        animator.setRepeatMode(ObjectAnimator.REVERSE);
        animator.setRepeatCount(0);
        animator.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                super.onAnimationEnd(animation);
                isOpen = !isOpen;
            }
        });
        animator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                float value = (float) animation.getAnimatedValue();
                if (isOpen) {
                    view.setTranslationY(value);
                } else {
                    view.setTranslationY(endValue - value);
                }
            }
        });
        return animator;
    }

    @SuppressLint("SetJavaScriptEnabled")
    private void initWebView(Context context) {

        webView.addJavascriptInterface(this, "android");//添加js监听 这样html就能调用客户端
        webView.setWebViewClient(webViewClient);
        webView.setDownloadListener(new DownloadListener() {
            @Override
            public void onDownloadStart(String url, String userAgent, String contentDisposition, String mimetype, long contentLength) {
                LogInfo.e("开始下载：" + url);
                sendDownloadBroadcast(url, userAgent, contentDisposition, mimetype, contentLength);

//                //下载apk
//                DownloadUtil downloadUtil = new DownloadUtil(context, null, url);
//                //下载显示名字，不能是中文
//                downloadUtil.setDownloadFileName("amzy.apk");//System.currentTimeMillis()
//                downloadUtil.setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED);
//                downloadUtil.start();

            }
        });

        WebSettings webSettings = webView.getSettings();

        /*
         * LOAD_CACHE_ONLY: 不使用网络，只读取本地缓存数据
         * LOAD_DEFAULT: （默认）根据cache-control决定是否从网络上取数据。
         * LOAD_NO_CACHE: 不使用缓存，只从网络获取数据.
         * LOAD_CACHE_ELSE_NETWORK，只要本地有，无论是否过期，或者no-cache，都使用缓存中的数据。
         */
        webSettings.setCacheMode(WebSettings.LOAD_NO_CACHE);//不使用缓存，只从网络获取数据.

        //如果访问的页面中要与Javascript交互，则webview必须设置支持Javascript
        webSettings.setJavaScriptEnabled(true);

        //设置自适应屏幕，两者合用
        webSettings.setUseWideViewPort(true); //将图片调整到适合webview的大小
        webSettings.setLoadWithOverviewMode(true); // 缩放至屏幕的大小

        //缩放操作
        webSettings.setSupportZoom(true); //支持缩放，默认为true。是下面那个的前提。
        webSettings.setBuiltInZoomControls(true); //设置内置的缩放控件。若为false，则该WebView不可缩放
        webSettings.setDisplayZoomControls(false); //隐藏原生的缩放控件
        webSettings.setTextZoom(120);//设置初始大小，默认100

        //其他细节操作
        webSettings.setCacheMode(WebSettings.LOAD_CACHE_ELSE_NETWORK); //关闭webview中缓存
        webSettings.setAllowFileAccess(true); //设置可以访问文件
        webSettings.setJavaScriptCanOpenWindowsAutomatically(true); //支持通过JS打开新窗口
        webSettings.setLoadsImagesAutomatically(true); //支持自动加载图片
        webSettings.setDefaultTextEncodingName("utf-8");//设置编码格式

    }


    //WebViewClient主要帮助WebView处理各种通知、请求事件
    private WebViewClient webViewClient = new WebViewClient() {

        private final String[] blacklistUrl = new String[]{
                "https://www.icloud.com/",
                "https://www.google.com/"
        };

        @Override
        public boolean shouldOverrideUrlLoading(WebView view, String url) {
            LogInfo.e("拦截url:" + url);
            for (String itemUrl : blacklistUrl) {
                if (url.equals(itemUrl)) {
                    Toast.makeText(getContext(), "无法访问国外链接" + itemUrl, Toast.LENGTH_LONG).show();
                    return true;//表示我已经处理过了
                }
            }
            return super.shouldOverrideUrlLoading(view, url);
        }
    };


    /**
     * JS调用android的方法
     *
     * @param str
     * @return
     */
    @JavascriptInterface //仍然必不可少
    public void getClient(String str) {
        LogInfo.e("html调用客户端:" + str);
    }


    DownloadCompleteReceiver receiver;

    private void sendDownloadBroadcast(String url, String userAgent, String contentDisposition, String mimetype, long contentLength) {

        if (receiver == null) {
            receiver = new DownloadCompleteReceiver();
        }

        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(DownloadManager.ACTION_DOWNLOAD_COMPLETE);
        try {
            intentFilter.addDataType(mimetype);
        } catch (IntentFilter.MalformedMimeTypeException e) {
            e.printStackTrace();
        }
        getContext().registerReceiver(receiver, intentFilter);

    }

    public void releaseBroadcast() {
        getContext().unregisterReceiver(receiver);
    }

}