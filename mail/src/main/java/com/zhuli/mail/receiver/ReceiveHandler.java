package com.zhuli.mail.receiver;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.graphics.BitmapFactory;
import android.os.Build;
import android.os.Handler;
import android.os.Message;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.core.app.NotificationCompat;

import com.zhuli.mail.MailActivity;
import com.zhuli.mail.R;
import com.zhuli.mail.mail.LogInfo;


/**
 * Copyright (C) 王字旁的理
 * Date: 9/24/2021
 * Description: 消息刷新主线程
 * Author: zl
 */
public class ReceiveHandler {

    private NotificationMassageHandle handle;

    public ReceiveHandler(Context content) {

    }

    public static void sendMessage(String id, String name, String str) {

    }

    private static class NotificationMassageHandle extends Handler {

        private final NotificationManager notificationManager;

        private final Context context;

        public NotificationMassageHandle(Context context) {
            this.context = context;
            //创建一个通知管理器
            this.notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        }

        @Override
        public void handleMessage(@NonNull Message msg) {
            super.handleMessage(msg);
            if (notificationManager != null) {
                String id = "my_channel_01";
                String name = "我是渠道名字";
                NotificationManager notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
                Notification notification;
                //设置要跳转的页面
                Intent intent = new Intent(context, MailActivity.class);
                //要传递到下一个页面的数据
                intent.putExtra("result", "aaa");
                PendingIntent pendingIntent = PendingIntent.getActivity(context, 0, intent, 0);//延迟跳转

                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                    NotificationChannel mChannel = new NotificationChannel(id, name, NotificationManager.IMPORTANCE_LOW);
                    notificationManager.createNotificationChannel(mChannel);
                    notification = new Notification.Builder(context, "default")
                            .setChannelId(id)
                            //设置标题
                            .setContentTitle("中奖五百万！！消息的标题")
                            .setContentText("消息的内容")
                            .setSmallIcon(R.mipmap.ic_drive_file)
                            //点击横幅自动跳转
                            .setContentIntent(pendingIntent)
                            //点击横幅自动消失
                            .setAutoCancel(true)
                            .setLargeIcon(BitmapFactory.decodeResource(context.getResources(), R.mipmap.ic_drive_file))
                            .build();
                } else {
                    NotificationCompat.Builder notificationBuilder = new NotificationCompat.Builder(context, "default")
                            .setContentTitle("中奖五百万！！消息的标题")
                            .setContentText("消息的内容")
                            .setSmallIcon(R.mipmap.ic_drive_file)
                            .setOngoing(true)
                            .setAutoCancel(true)
                            .setContentIntent(pendingIntent)
                            .setLargeIcon(BitmapFactory.decodeResource(context.getResources(), R.mipmap.ic_drive_file))
                            .setChannelId(id);
                    notification = notificationBuilder.build();
                }
                notificationManager.notify(111123, notification);

            }
        }
    }

}
