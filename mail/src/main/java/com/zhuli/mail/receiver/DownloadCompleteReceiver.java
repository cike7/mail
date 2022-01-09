package com.zhuli.mail.receiver;

import android.app.DownloadManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.text.TextUtils;

public class DownloadCompleteReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        if (DownloadManager.ACTION_DOWNLOAD_COMPLETE.equals(intent.getAction())) {
            long downloadId = intent.getLongExtra(DownloadManager.EXTRA_DOWNLOAD_ID, -1);
            DownloadManager downloadManager = (DownloadManager) context.getSystemService(context.DOWNLOAD_SERVICE);
            String type = downloadManager.getMimeTypeForDownloadedFile(downloadId);
            if (TextUtils.isEmpty(type)) {
                type = "*/*";
            }
            Uri uri = downloadManager.getUriForDownloadedFile(downloadId);
            if (uri != null) {
                Intent handlerIntent = new Intent(Intent.ACTION_VIEW);
                handlerIntent.setDataAndType(uri, type);
                context.startActivity(handlerIntent);
            }
        }
    }
}
