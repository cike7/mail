package com.zhuli.mail.file;

import android.os.Handler;
import android.os.Looper;
import android.os.Message;

import androidx.annotation.NonNull;

/**
 * Copyright (C), 2003-2021, 深圳市图派科技有限公司
 * Date: 2022/1/6
 * Description: 异步任务等待监听
 * Author: zl
 */
public class TaskWaiting {

    private Handler handler;

    private CallbackProcessingListener listener;

    public TaskWaiting() {
        handler = new Handler(Looper.getMainLooper()) {
            @Override
            public void handleMessage(@NonNull Message msg) {
                super.handleMessage(msg);
                if(listener != null){
                    listener.onComplete(msg.what);
                }
            }
        };
    }

    public void setListener(CallbackProcessingListener listener) {
        this.listener = listener;
    }

    public Handler getHandler() {
        return handler;
    }

    public void close() {
        handler.removeCallbacksAndMessages(null);
        handler = null;
    }

}
