package com.zhuli.mail.util;

import android.app.DownloadManager;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.os.Environment;
import android.os.Handler;
import android.widget.ProgressBar;

import java.io.File;
import java.lang.ref.WeakReference;

/**
 * @Description 下载工具
 * @Author zhuli
 * @Date 2021/6/6/2:45 PM
 */
public class DownloadUtil {

    private WeakReference<Context> mContext;

    private ProgressBar progressBar;

    private String downloadFileName = "amzy.apk";
    //下载进度id
    public static long myReference;
    //下载管理
    public static DownloadManager downloadManager;
    //下载通知
    private DownloadManager.Request downloadRequest;
    //下载进度显示线程
    private Handler handler;

    public DownloadUtil(Context context, ProgressBar progressBar, String downloadUrl) {
        this.mContext = new WeakReference<>(context);
        this.progressBar = progressBar;
        initDownload(downloadUrl);
    }


    /**
     * 初始化下载管理
     * @param downloadUrl url
     */
    private void initDownload(String downloadUrl) {
        //获取下载服务
        downloadManager = (DownloadManager) mContext.get().getSystemService(Context.DOWNLOAD_SERVICE);
        //解析url
        Uri uri = Uri.parse(downloadUrl);
        //下载请求
        downloadRequest = new DownloadManager.Request(uri);
        // 设置目标存储在外部目录，一般位置可以用
        downloadRequest.setDestinationInExternalFilesDir(mContext.get(), Environment.DIRECTORY_DOWNLOADS, downloadFileName);
        //下载的文件能被其他应用扫描到
        downloadRequest.allowScanningByMediaScanner();
        //设置被系统的Downloads应用扫描到并管理,默认true
        downloadRequest.setVisibleInDownloadsUi(true);
        //限定在WiFi还是手机网络(NETWORK_MOBILE)下进行下载
        downloadRequest.setAllowedNetworkTypes(DownloadManager.Request.NETWORK_WIFI | DownloadManager.Request.NETWORK_MOBILE);
        // 设置mime类型，这里看服务器配置，一般国家化的都为utf-8编码。
        downloadRequest.setMimeType("application/vnd.android.package-archive");
        //设置notification显示状态
        downloadRequest.setNotificationVisibility(DownloadManager.Request.VISIBILITY_HIDDEN);
        //设置notification的标题
        downloadRequest.setTitle("版本更新通知");
        //设置notification的描述
        downloadRequest.setDescription("");
    }


    /**
     * 开始下载
     */
    public void start() {
        //判断文件是否存在
        File apkFile = new File(mContext.get().getExternalFilesDir("").getAbsolutePath() + "/Download/amzy.apk");
        if(apkFile.exists()){
            apkFile.delete();
        }

        //设置下载进度id
        myReference = downloadManager.enqueue(downloadRequest);

        //下载进度
        handler = new Handler();
        handler.postDelayed(downloadProgress,500);

    }


    /**
     * 下载进度
     */
    private final Runnable downloadProgress = new Runnable() {
        @Override
        public void run() {
            if(getDownloadPercent(myReference) >= 100){
                progressBar.setProgress(progressBar.getMax());
                handler.removeCallbacks(this);
            }else {
                progressBar.setProgress(getDownloadPercent(myReference));
                handler.postDelayed(this,1000);
            }
        }
    };

    /**
     * 设置下载文件名字
     * @param downloadFileName
     */
    public void setDownloadFileName(String downloadFileName) {
        this.downloadFileName = downloadFileName;
        // 设置目标存储在外部目录，一般位置可以用
        downloadRequest.setDestinationInExternalFilesDir(mContext.get(), Environment.DIRECTORY_DOWNLOADS, downloadFileName);
    }

    public void setNotificationTitle(CharSequence title) {
        //设置notification的标题
        downloadRequest.setTitle(title);
    }

    public void setNotificationDescription(CharSequence description) {
        //设置notification的描述
        downloadRequest.setDescription(description);
    }

    /**
     * 设置notification显示状态
     * Request.VISIBILITY_VISIBLE：在下载进行的过程中，通知栏中会一直显示该下载的Notification，当下载完成时，该Notification会被移除，这是默认的参数值。
     * Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED：在下载过程中通知栏会一直显示该下载的Notification，在下载完成后该Notification会继续显示，直到用户点击该
     * Notification或者消除该Notification。
     * Request.VISIBILITY_VISIBLE_NOTIFY_ONLY_COMPLETION：只有在下载完成后该Notification才会被显示。
     * Request.VISIBILITY_HIDDEN：不显示该下载请求的Notification。如果要使用这个参数，需要在应用的清单文件中加上android.permission.DOWNLOAD_WITHOUT_NOTIFICATION
     *
     * @param visibility 显示标识
     */
    public void setNotificationVisibility(int visibility) {
        downloadRequest.setNotificationVisibility(visibility);
    }

    public DownloadManager.Request getDownloadRequest() {
        return downloadRequest;
    }

    /**
     * 下载进度
     * @param downloadId 下载id
     * @return
     */
    private int getDownloadPercent(long downloadId){
        DownloadManager.Query query = new DownloadManager.Query().setFilterById(downloadId);
        Cursor c =  downloadManager.query(query);
        if(c.moveToFirst()){
            int downloadBytesIdx = c.getColumnIndexOrThrow(
                    DownloadManager.COLUMN_BYTES_DOWNLOADED_SO_FAR);
            int totalBytesIdx = c.getColumnIndexOrThrow(
                    DownloadManager.COLUMN_TOTAL_SIZE_BYTES);
            long totalBytes = c.getLong(totalBytesIdx);
            long downloadBytes = c.getLong(downloadBytesIdx);
            return (int) (downloadBytes * 100 / totalBytes);
        }
        return 0;
    }



}
