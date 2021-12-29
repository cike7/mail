package com.zhuli.mail.file;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;

import androidx.core.content.FileProvider;

import java.io.File;

/**
 * Copyright (C) 王字旁的理
 * Date: 2021/12/30
 * Description:
 * Author: zl
 */
public class ProcessingApkFile implements ActingProcessing {

    private Context context;

    public ProcessingApkFile(Context context) {
        this.context = context;
    }

    @Override
    public void startProcessingFile(String path, FileProcessingCompleteListener listener) {
        Intent intent = new Intent(Intent.ACTION_VIEW);
        Uri uri;
        // 判断版本大于等于7.0
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            // "net.csdn.blog.ruancoder.fileprovider"即是在清单文件中配置的authoritiescom.thinker.member.bull.android_bull_member
            uri = FileProvider.getUriForFile(context, context.getPackageName() + ".fileprovider", new File(path));
            // 给目标应用一个临时授权
            intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
        } else {
            uri = Uri.fromFile(new File(path));
        }

        intent.setDataAndType(uri, "application/vnd.android.package-archive");
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        context.startActivity(intent);
    }
}
