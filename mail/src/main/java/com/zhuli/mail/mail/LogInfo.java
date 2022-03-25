package com.zhuli.mail.mail;

import android.annotation.SuppressLint;
import android.content.Context;
import android.text.TextUtils;
import android.util.Log;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.charset.StandardCharsets;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Copyright (C) 王字旁的理
 * Date: 2021/12/12
 * Description:Log日志封装类
 * Author: zl
 */
public class LogInfo {

    //开关
    public static final boolean DEBUG = true;
    //TAG
    public static final String TAG = "MailTest";

    private static String path;
    private final static String logName = "logInfo.txt";

    private static LogInfo instance;

    private LogInfo(String path) {
        LogInfo.path = path;
    }

    public static void init(Context context) {
        if (instance == null) {
            synchronized (LogInfo.class) {
                if (instance == null) {
                    instance = new LogInfo(context.getExternalFilesDir("").getAbsolutePath() + File.separator + logName);
                }
            }
        }
    }

    @SuppressLint("SimpleDateFormat")
    public static <T> void e(T text) {
        if (DEBUG) {
            String dateString = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date());
            Log.e(TAG, "" + text);
            if (instance != null) {
                instance.setLogInfo(dateString + " : " + text + "\n");
            }
        }
    }

    private void setLogInfo(String msg) {
        if (!TextUtils.isEmpty(path)) {
            File file = new File(path);
            OutputStream os = null;
            try {
                os = new FileOutputStream(file, true);
                os.write(msg.getBytes(StandardCharsets.UTF_8));

            } catch (IOException e) {
                e.printStackTrace();
            } finally {
                if (os != null) {
                    try {
                        os.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }
        }
    }

    public static String getLogInfo() {
        Log.e(TAG, path);
        File file = new File(path);
        if (!file.exists()) {
            return "file not exists!";
        }
        StringBuilder log = new StringBuilder();
        InputStream is = null;
        try {
            is = new FileInputStream(file);
            byte[] bytes = new byte[1024];
            while (is.read(bytes) != -1) {
                log.append(new String(bytes, StandardCharsets.UTF_8));
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (is != null) {
                try {
                    is.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        return log.toString();
    }

}
