package com.zhuli.mail.file;

import android.content.Context;

import java.util.List;

/**
 * Copyright (C) 王字旁的理
 * Date: 2021/12/30
 * Description: 文件处理工厂
 * Author: zl
 */
public class DocumentProcessingFactory {

    private CallbackProcessingListener listener;

    private Context mContext;

    private static DocumentProcessingFactory instance;

    private DocumentProcessingFactory(Context context) {
        mContext = context;
    }

    public static void init(Context context, CallbackProcessingListener listener) {
        if (instance == null) {
            instance = new DocumentProcessingFactory(context);
        }
        instance.listener = listener;
    }


    /**
     * 设置文件路径
     *
     * @param filePath
     */
    public static void setFilePath(String filePath) {

        if (instance == null) {
            return;
        }

        ActingProcessing<List<String>> imp;

        if (filePath.equals(".apk")) {
            imp = new ProcessingApkFile(instance.mContext);
        } else {
            //zip
            imp = new ProcessingZipFile();
        }

        imp.startProcessingFile(filePath, instance.listener);

    }

}
