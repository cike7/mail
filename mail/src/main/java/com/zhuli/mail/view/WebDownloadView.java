package com.zhuli.mail.view;

import android.app.AlertDialog;
import android.app.DownloadManager;
import android.content.Context;
import android.content.IntentFilter;
import android.graphics.Bitmap;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Environment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.DownloadListener;
import android.webkit.JavascriptInterface;
import android.webkit.JsResult;
import android.webkit.URLUtil;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.PopupWindow;
import android.widget.Toast;

import com.zhuli.mail.R;
import com.zhuli.mail.mail.LogInfo;
import com.zhuli.mail.receiver.DownloadCompleteReceiver;


/**
 * Copyright (C) 王字旁的理
 * Date: 2021/12/21
 * Description: 消息
 * Author: zl
 */
public class WebDownloadView extends PopupWindow implements View.OnClickListener {

    private WebView webView;
    private boolean isOpen = false;

    private View root;

    public WebDownloadView(Context context) {

        root = LayoutInflater.from(context).inflate(R.layout.popup_web_download, null);
        setContentView(root);

        setWidth(ViewGroup.LayoutParams.MATCH_PARENT);
        setHeight(ViewGroup.LayoutParams.MATCH_PARENT);
        setFocusable(true);
        setBackgroundDrawable(new ColorDrawable());
        //点击背景关闭
        webView = root.findViewById(R.id.web_download_view);

        initWebView();

        root.setTranslationY(0);

        update();


    }


    private void initWebView() {
        //        String url = "https://www.icloud.com/attachment/?u=https%3A%2F%2Fcvws.icloud-content.com%2FB%2FAXEh4BE-iF5vcfnarAVOVYqawFJbAbMhbSkX-QlQnI5_3RNbuhqfH__W%2F%24%7Bf%7D%3Fo%3DAn0TMsKeH-colLDMedGE7m1tW9IbBBUK1lhOuu3exJPB%26v%3D1%26x%3D3%26a%3DCAogCGac-_mYoh4qsMjBVyRGS0VlG8HhxyR_GBBieN6EdlQSehCJ_oLX4y8YiY7-qu0vIgEAKgkC6AMA_wj_lI1SBJrAUltaBJ8f_9ZqJwBflRqI5OmKiLVSDlPLD7kYfIBsbQtsIn5H0boh-sqGqdpBP5HE7nInJRJvLTpu7kz-AyLAmm92qBRdQEYOm52IKJzGP5ESetg1pmCxv1De%26e%3D1644257314%26fl%3D%26r%3D54EE7538-5C59-4A70-9607-A0ED6886D5C1-1%26k%3D%24%7Buk%7D%26ckc%3Dcom.apple.largeattachment%26ckz%3D61315739-26D5-4268-B154-0C05337B07E3%26p%3D52%26s%3DSbnprN4aOkxJN7fWAkggRcOPv8o&uk=DbCf7HWscHzCy5P9S0RfyA&f=OPPO-GOlf-EN-APK-1.0-202201071407.apk&sz=25405521";
        String url = "https://qiye.aliyun.com/alimail/openLinks/downloadMimeMetaDiskBigAttach?id=netdiskid%3Av001%3Afile%3ADzzzzzzNqYC%3BOnqg0AtYuYaXRvNTRJ5F2ho3XRdl6eIIZdro5pRpVnX4YDQizm%2BbN65EXLWK5jJfJ9wqVGjl21r%2Fzxp6zyXztV3Plpx6%2FykcCJB81hXuSMJdpGXGNd46Mg%3D%3D ";

//        webView = getView().findViewById(R.id.web_view);
        webView.loadUrl(url);

        webView.addJavascriptInterface(this, "android");//添加js监听 这样html就能调用客户端
        webView.setWebChromeClient(webChromeClient);
        webView.setWebViewClient(webViewClient);
        webView.setDownloadListener(new DownloadListener() {
            @Override
            public void onDownloadStart(String url, String userAgent, String contentDisposition, String mimetype, long contentLength) {
                LogInfo.e("开始下载：" + url);
//                downloadBySystem(url, contentDisposition, mimetype);
            }
        });

        WebSettings webSettings = webView.getSettings();

        /**
         * LOAD_CACHE_ONLY: 不使用网络，只读取本地缓存数据
         * LOAD_DEFAULT: （默认）根据cache-control决定是否从网络上取数据。
         * LOAD_NO_CACHE: 不使用缓存，只从网络获取数据.
         * LOAD_CACHE_ELSE_NETWORK，只要本地有，无论是否过期，或者no-cache，都使用缓存中的数据。
         */
        webSettings.setCacheMode(WebSettings.LOAD_DEFAULT);//不使用缓存，只从网络获取数据.

        //支持屏幕缩放
        webSettings.setSupportZoom(true);
        webSettings.setBuiltInZoomControls(true);

        //如果访问的页面中要与Javascript交互，则webview必须设置支持Javascript
        webSettings.setJavaScriptEnabled(true);

        //设置自适应屏幕，两者合用
        webSettings.setUseWideViewPort(true); //将图片调整到适合webview的大小
        webSettings.setLoadWithOverviewMode(true); // 缩放至屏幕的大小

        //缩放操作
        webSettings.setSupportZoom(true); //支持缩放，默认为true。是下面那个的前提。
        webSettings.setBuiltInZoomControls(true); //设置内置的缩放控件。若为false，则该WebView不可缩放
        webSettings.setDisplayZoomControls(false); //隐藏原生的缩放控件

        //其他细节操作
        webSettings.setCacheMode(WebSettings.LOAD_CACHE_ELSE_NETWORK); //关闭webview中缓存
        webSettings.setAllowFileAccess(true); //设置可以访问文件
        webSettings.setJavaScriptCanOpenWindowsAutomatically(true); //支持通过JS打开新窗口
        webSettings.setLoadsImagesAutomatically(true); //支持自动加载图片
        webSettings.setDefaultTextEncodingName("utf-8");//设置编码格式
    }


    @Override
    public void onClick(View v) {
        //直接关闭
//        dismiss();
    }


    // 使用系统下载工具进行下载
    private void downloadBySystem(String url, String contentDisposition, String mimeType) {
        // 指定下载地址
        DownloadManager.Request request = new DownloadManager.Request(Uri.parse(url));
        // 允许媒体扫描,根据下载的文件类型加入相册、音乐等媒体库
        request.allowScanningByMediaScanner();
        // 设置通知的显示类型，下载进行时和完成后显示通知
        request.setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED);
        // 允许下载的网络类型（）
        request.setAllowedNetworkTypes(DownloadManager.Request.NETWORK_MOBILE | DownloadManager.Request.NETWORK_WIFI);
        // 允许该记录在下载管理界面可见
        request.setVisibleInDownloadsUi(true);
        // 设置下载中通知栏标题
        request.setTitle("文件更新下载");
        // 设置下载中通知栏描述
        request.setDescription("文件更新下载");
        // 获得SD卡的读取权限（安卓6.0以上需要获取相应的权限）
        // getSDPersimmion();
        // 设置下载文件保存路径和路径文件名
        String fileName = URLUtil.guessFileName(url, contentDisposition, mimeType);
        request.setDestinationInExternalPublicDir(Environment.DIRECTORY_DOWNLOADS, fileName);
        // 获得系统下载管理器
        final DownloadManager downloadManager = (DownloadManager) getContext().getSystemService(getContext().DOWNLOAD_SERVICE);
        // 添加一个下载任务
        long downloadId = downloadManager.enqueue(request);

        receiver = new DownloadCompleteReceiver();
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(DownloadManager.ACTION_DOWNLOAD_COMPLETE);
        getContext().registerReceiver(receiver, intentFilter);

    }

    DownloadCompleteReceiver receiver;

    @Override
    public void setOnDismissListener(OnDismissListener onDismissListener) {
        super.setOnDismissListener(onDismissListener);
//        getContext().unregisterReceiver(receiver);
    }

    //WebViewClient主要帮助WebView处理各种通知、请求事件
    private WebViewClient webViewClient = new WebViewClient() {
        @Override
        public void onPageFinished(WebView view, String url) {
            LogInfo.e("页面加载完成");
        }

        @Override
        public void onPageStarted(WebView view, String url, Bitmap favicon) {
            LogInfo.e("页面开始加载" + url);
        }

        @Override
        public boolean shouldOverrideUrlLoading(WebView view, String url) {
            LogInfo.e("拦截url:" + url);
            if (url.equals("http://www.google.com/")) {
                Toast.makeText(getContext(), "国内不能访问google,拦截该url", Toast.LENGTH_LONG).show();
                return true;//表示我已经处理过了
            }
            return super.shouldOverrideUrlLoading(view, url);
        }

    };

    //WebChromeClient主要辅助WebView处理Javascript的对话框、网站图标、网站title、加载进度等
    private WebChromeClient webChromeClient = new WebChromeClient() {
        //不支持js的alert弹窗，需要自己监听然后通过dialog弹窗
        @Override
        public boolean onJsAlert(WebView webView, String url, String message, JsResult result) {
            AlertDialog.Builder localBuilder = new AlertDialog.Builder(webView.getContext());
            localBuilder.setMessage(message).setPositiveButton("确定", null);
            localBuilder.setCancelable(false);
            localBuilder.create().show();
            //注意:
            //必须要这一句代码:result.confirm()表示:
            //处理结果为确定状态同时唤醒WebCore线程
            //否则不能继续点击按钮
            result.confirm();
            return true;
        }

        @Override
        public void onReceivedTitle(WebView view, String title) {
            super.onReceivedTitle(view, title);
            LogInfo.e("网页标题:" + title);
        }

        @Override
        public void onProgressChanged(WebView view, int newProgress) {
            LogInfo.e("加载进度回调:" + newProgress);
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


    public Context getContext() {
        return getContext();
    }

}