package com.zhuli.mail.receiver;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.graphics.BitmapFactory;
import android.os.Handler;
import android.os.Message;

import androidx.annotation.NonNull;

import com.zhuli.mail.MailActivity;
import com.zhuli.mail.R;


/**
 * Copyright (C) 王字旁的理
 * Date: 9/24/2021
 * Description: 消息刷新主线程
 * Author: zl
 */
public class ReceiveHandler extends Handler {

    private final NotificationManager notificationManager;

    private final Context context;

    public ReceiveHandler(Context content) {
        this.context = content;
        //创建一个通知管理器
        notificationManager = (NotificationManager) content.getSystemService(Context.NOTIFICATION_SERVICE);
    }

    @Override
    public void handleMessage(@NonNull Message msg) {
        super.handleMessage(msg);
        if (notificationManager != null) {
            PendingIntent contentIntent = PendingIntent.getActivity(context, msg.what, new Intent(context, MailActivity.class), PendingIntent.FLAG_UPDATE_CURRENT);
            Notification notification = new Notification.Builder(context)
                    .setContentTitle(msg.obj != null ? (String) msg.obj : "网络已连接")
                    .setContentText("点击返回邮箱")
                    .setWhen(System.currentTimeMillis())
                    .setSmallIcon(R.mipmap.ic_drive_file)
                    .setLargeIcon(BitmapFactory.decodeResource(context.getResources(), R.mipmap.ic_drive_file))
                    .setContentIntent(contentIntent)
                    .build();
            notificationManager.notify(0, notification);
        }
    }
}
