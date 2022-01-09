package com.zhuli.mail;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.os.Environment;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.style.URLSpan;
import android.text.util.Linkify;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.JavascriptInterface;
import android.webkit.JsResult;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.zhuli.mail.adapter.FileItemAdapter;
import com.zhuli.mail.file.CallbackProcessingListener;
import com.zhuli.mail.mail.LogInfo;
import com.zhuli.mail.mail.MailUtil;
import com.zhuli.mail.util.NetworkDiagnosisUtil;
import com.zhuli.mail.util.PathUtil;
import com.zhuli.mail.util.PermissionUtil;
import com.zhuli.mail.util.WifiContentUtil;
import com.zhuli.mail.util.ZipUtils;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.FormBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class MailActivity extends AppCompatActivity {

    private EditText toAddEt;

    private RecyclerView recyclerView;

    private FileItemAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_mail);

        toAddEt = findViewById(R.id.toAddEt);
        recyclerView = findViewById(R.id.recycler_files_layout);

        NetworkDiagnosisUtil.init(this);
        WifiContentUtil.registerReceiverWifi(this).setSpareWifi("Tenda_74E7D0", "wifi123456");
        PermissionUtil.verifyStoragePermissions(this);

        recyclerView.setLayoutManager(new GridLayoutManager(this, 6));
        adapter = new FileItemAdapter(new ArrayList<>(), new ArrayList<>());
        recyclerView.setAdapter(adapter);

    }


    /**
     * 申请权限结果返回处理
     */
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        PermissionUtil.confirmPermissions(this, requestCode, permissions, grantResults);
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 1 && resultCode == Activity.RESULT_OK) {
            if (data != null) {
                adapter.updateData(PathUtil.getPaths(this, data));
            }
        }
    }


    /**
     * 发送邮件
     *
     * @param view
     */
    public void sendFileMail(View view) {

        if (NetworkDiagnosisUtil.getNetworkState()) {
            if (adapter.getFilePaths().size() > 0) {
                List<File> files = new ArrayList<>();
                for (int i = 0; i < adapter.getFilePaths().size(); i++) {
                    if (adapter.getFilePaths().get(i) != null && adapter.getFilePaths().get(i).length() > 0) {
                        files.add(new File(adapter.getFilePaths().get(i)));
                    }
                }
                if (files.size() > 0) {
                    MailUtil.send(toAddEt.getText().toString(), files);
                    Toast.makeText(this, "邮件发送成功!", Toast.LENGTH_SHORT).show();
                    adapter.clear();
                } else {
                    LogInfo.e("选择邮件为空");
                }
            }
        } else {
            NetworkDiagnosisUtil.notNetworkConnect();
        }
    }

    WebView webView;

    public void receiveMail(View view) {

//        String url = "https://www.icloud.com/attachment/?u=https%3A%2F%2Fcvws.icloud-content.com%2FB%2FAXEh4BE-iF5vcfnarAVOVYqawFJbAbMhbSkX-QlQnI5_3RNbuhqfH__W%2F%24%7Bf%7D%3Fo%3DAn0TMsKeH-colLDMedGE7m1tW9IbBBUK1lhOuu3exJPB%26v%3D1%26x%3D3%26a%3DCAogCGac-_mYoh4qsMjBVyRGS0VlG8HhxyR_GBBieN6EdlQSehCJ_oLX4y8YiY7-qu0vIgEAKgkC6AMA_wj_lI1SBJrAUltaBJ8f_9ZqJwBflRqI5OmKiLVSDlPLD7kYfIBsbQtsIn5H0boh-sqGqdpBP5HE7nInJRJvLTpu7kz-AyLAmm92qBRdQEYOm52IKJzGP5ESetg1pmCxv1De%26e%3D1644257314%26fl%3D%26r%3D54EE7538-5C59-4A70-9607-A0ED6886D5C1-1%26k%3D%24%7Buk%7D%26ckc%3Dcom.apple.largeattachment%26ckz%3D61315739-26D5-4268-B154-0C05337B07E3%26p%3D52%26s%3DSbnprN4aOkxJN7fWAkggRcOPv8o&uk=DbCf7HWscHzCy5P9S0RfyA&f=OPPO-GOlf-EN-APK-1.0-202201071407.apk&sz=25405521";
        String url = "https://qiye.aliyun.com/alimail/openLinks/downloadMimeMetaDiskBigAttach?id=netdiskid%3Av001%3Afile%3ADzzzzzzNqYC%3BOnqg0AtYuYaXRvNTRJ5F2ho3XRdl6eIIZdro5pRpVnX4YDQizm%2BbN65EXLWK5jJfJ9wqVGjl21r%2Fzxp6zyXztV3Plpx6%2FykcCJB81hXuSMJdpGXGNd46Mg%3D%3D ";
//        TextView textView = new TextView(this);
//        textView.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT,100));
//        URLSpan span = new URLSpan(url);
//        SpannableString spannable = new SpannableString(url);
//        spannable.setSpan(span, 0, url.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
//        textView.setText(spannable);
//        textView.setTextSize(22);
//        Linkify.addLinks(textView,Linkify.WEB_URLS);

        webView = findViewById(R.id.web_view);
        webView.loadUrl(url);

        webView.addJavascriptInterface(this, "android");//添加js监听 这样html就能调用客户端
        webView.setWebChromeClient(webChromeClient);
        webView.setWebViewClient(webViewClient);

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

//        MailUtil.receive((CallbackProcessingListener<String>) t -> {
//            LogInfo.e("收到邮件" + t);
//        });

    }


    //WebViewClient主要帮助WebView处理各种通知、请求事件
    private WebViewClient webViewClient = new WebViewClient() {
        @Override
        public void onPageFinished(WebView view, String url) {//页面加载完成

        }

        @Override
        public void onPageStarted(WebView view, String url, Bitmap favicon) {//页面开始加载

        }

        @Override
        public boolean shouldOverrideUrlLoading(WebView view, String url) {
            Log.i("ansen", "拦截url:" + url);
            if (url.equals("http://www.google.com/")) {
                Toast.makeText(MailActivity.this, "国内不能访问google,拦截该url", Toast.LENGTH_LONG).show();
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

        //获取网页标题
        @Override
        public void onReceivedTitle(WebView view, String title) {
            super.onReceivedTitle(view, title);
            Log.i("ansen", "网页标题:" + title);
        }

        //加载进度回调
        @Override
        public void onProgressChanged(WebView view, int newProgress) {

        }
    };

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        LogInfo.e(keyCode + "是否有上一个页面:" + webView.canGoBack());
        if (webView.canGoBack() && keyCode == KeyEvent.KEYCODE_BACK) {//点击返回按钮的时候判断有没有上一页
            webView.goBack(); // goBack()表示返回webView的上一页面
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }

    /**
     * JS调用android的方法
     *
     * @param str
     * @return
     */
    @JavascriptInterface //仍然必不可少
    public void getClient(String str) {
        Log.i("ansen", "html调用客户端:" + str);
    }


    /**
     * 添加文件
     *
     * @param view
     */
    public void getFile(View view) {

        Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
        // 所有类型
        intent.setType("*/*");
        //默认类别
        intent.addCategory(Intent.CATEGORY_DEFAULT);
        //允许多个
        intent.putExtra(Intent.EXTRA_ALLOW_MULTIPLE, true);
        //标志授予读取 URI 权限
        intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
        try {
            startActivityForResult(Intent.createChooser(intent, "请选择文件"), 1);
        } catch (ActivityNotFoundException e) {
            e.printStackTrace();
            Toast.makeText(MailActivity.this, "请安装文件管理器", Toast.LENGTH_SHORT).show();
        }

    }


    //压缩文件
    public void compressionFile(View view) {

        @SuppressLint("SimpleDateFormat")
        SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd");//设置日期格式
        // 将本地的目标文件夹的文件，统一打包成test.zip,
        // 查找本地缓存的图片目录文件夹路径
//        String filepath = getApplication().getExternalFilesDir() + File.separator + df.format(new Date()) + "-" + System.currentTimeMillis() + ".zip";
        String filepath = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS).getPath() + File.separator + df.format(new Date()) + "-" + System.currentTimeMillis() + ".zip";
        LogInfo.e(filepath);
        //批量压缩到指定文件夹下并命名为test.zip
        try {
            ZipUtils.zipFiles(adapter.getFilePaths(), filepath);
        } catch (IOException e) {
            e.printStackTrace();
        }

    }


}
