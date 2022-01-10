package com.zhuli.mail.util;

import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.provider.Settings;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;

import com.zhuli.mail.mail.LogInfo;


/**
 * Copyright (C) 王字旁的理
 * Date: 2021/12/12
 * Description: 权限工具
 * Author: zl
 */
public class PermissionUtil {

    //先定义
    private static final int REQUEST_EXTERNAL_STORAGE = 1;

    //读取外部存储
    private static final String[] PERMISSIONS_STORAGE = {
            Manifest.permission.READ_EXTERNAL_STORAGE,
            Manifest.permission.WRITE_EXTERNAL_STORAGE,
            Manifest.permission.ACCESS_WIFI_STATE,
            Manifest.permission.ACCESS_FINE_LOCATION,
            Manifest.permission.ACCESS_LOCATION_EXTRA_COMMANDS,
            Manifest.permission.VIBRATE,
            Manifest.permission.REQUEST_IGNORE_BATTERY_OPTIMIZATIONS,
            Manifest.permission.FOREGROUND_SERVICE
    };


    /**
     * 检查权限
     *
     * @param activity
     */
    public static void verifyStoragePermissions(Activity activity) {
        try {
            //检测需要的全部的权限
            for (String permission : PERMISSIONS_STORAGE) {  // 判断是否所有的权限都已经授予了
                int permissionCode = ActivityCompat.checkSelfPermission(activity, permission);
                if (permissionCode != PackageManager.PERMISSION_GRANTED) {
                    // 没有写的权限，去申请写的权限，会弹出对话框
                    ActivityCompat.requestPermissions(activity, PERMISSIONS_STORAGE, REQUEST_EXTERNAL_STORAGE);
                    break;
                }
            }

            //弹窗显示
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                if (!Settings.canDrawOverlays(activity)) {
                    Intent intent = new Intent(Settings.ACTION_MANAGE_OVERLAY_PERMISSION,
                            Uri.parse("package:" + activity.getPackageName()));
                    activity.startActivityForResult(intent, REQUEST_EXTERNAL_STORAGE);
                }
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    /**
     * 再次申请权限
     *
     * @param activity
     */
    public static void confirmPermissions(Activity activity, int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (requestCode == REQUEST_EXTERNAL_STORAGE) {
            boolean isAllGranted = true;
            // 判断是否所有的权限都已经授予了
            int nowPerm = 0;
            for (int grant : grantResults) {
                LogInfo.e(permissions[nowPerm] + "申请权限结果====" + grant);
                if (grant != PackageManager.PERMISSION_GRANTED) {
                    isAllGranted = false;
                    break;
                }
                nowPerm += 1;
            }
            //拒绝了权限
            if (!isAllGranted) {
                AlertDialog.Builder builder = new AlertDialog.Builder(activity);
                builder.setMessage("软件功能需要获取" + permissions[nowPerm] + "权限, 是否继续并且选择允许?")
                        .setTitle("提示")
                        .setPositiveButton("确认", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.dismiss();
                                ActivityCompat.requestPermissions(activity, permissions, REQUEST_EXTERNAL_STORAGE);
                            }
                        })
                        .setNegativeButton("取消", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.dismiss();
                                activity.finish();
                            }
                        });
                builder.create().show();
            }
        }
    }

}
