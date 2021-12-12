package com.zhuli.mail.mail;

import android.util.Log;

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

    public static void e(String text) {
        if (DEBUG) {
            Log.e(TAG, "" + text);
        }
    }
}
