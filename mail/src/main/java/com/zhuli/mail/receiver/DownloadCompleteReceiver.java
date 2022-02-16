package com.zhuli.mail.receiver;

import android.app.DownloadManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.widget.Toast;

//import com.zhuli.mail.util.AppUpdate;
import com.zhuli.mail.util.DownloadUtil;

public class DownloadCompleteReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {

        //点击下载通知栏
        if (intent.getAction().equals(DownloadManager.ACTION_NOTIFICATION_CLICKED)) {
            String extraID = DownloadManager.EXTRA_NOTIFICATION_CLICK_DOWNLOAD_IDS;
            long[] references = intent.getLongArrayExtra(extraID);
            for (long reference : references) {
                if (reference == DownloadUtil.myReference) {
                    //处理 如果还未完成下载，用户点击Notification ，跳转到下载中心
                    Intent viewDownloadIntent = new Intent(DownloadManager.ACTION_VIEW_DOWNLOADS);
                    viewDownloadIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    context.startActivity(viewDownloadIntent);
                }
            }
        } else if (intent.getAction().equals(DownloadManager.ACTION_DOWNLOAD_COMPLETE)) {//下载完成通知
            long completeDownloadId = intent.getLongExtra(DownloadManager.EXTRA_DOWNLOAD_ID, -1);
            if (completeDownloadId == DownloadUtil.myReference) {
                Cursor cursor = DownloadUtil.downloadManager.query(new DownloadManager.Query()
                        .setFilterById(completeDownloadId));
                cursor.moveToFirst();
                String filePath = cursor.getString(cursor.getColumnIndex(DownloadManager.COLUMN_LOCAL_URI));
                cursor.close();
                if (filePath != null) {
                    if (filePath.contains(context.getPackageName())) {
                        if (filePath.endsWith("apk")) {
//                            AppUpdate.installAPK(context, filePath);

                        } else {
                            Toast.makeText(context, "下载完成", Toast.LENGTH_SHORT).show();
                        }
                    }
                } else {
                    Toast.makeText(context, "网络不给力", Toast.LENGTH_SHORT).show();
                }
            }
        }


//        if (DownloadManager.ACTION_DOWNLOAD_COMPLETE.equals(intent.getAction())) {
//            long downloadId = intent.getLongExtra(DownloadManager.EXTRA_DOWNLOAD_ID, -1);
//            DownloadManager downloadManager = (DownloadManager) context.getSystemService(context.DOWNLOAD_SERVICE);
//            String type = downloadManager.getMimeTypeForDownloadedFile(downloadId);
//            if (TextUtils.isEmpty(type)) {
//                type = "*/*";
//            }
//            Uri uri = downloadManager.getUriForDownloadedFile(downloadId);
//            if (uri != null) {
//                Intent handlerIntent = new Intent(Intent.ACTION_VIEW);
//                handlerIntent.setDataAndType(uri, type);
//                context.startActivity(handlerIntent);
//            }
//        }

    }


}
