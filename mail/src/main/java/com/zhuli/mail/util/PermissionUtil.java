package com.zhuli.mail.util;

import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.provider.Settings;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;

import com.zhuli.mail.mail.LogInfo;

import java.util.ArrayList;
import java.util.List;


/**
 * Copyright (C) 王字旁的理
 * Date: 2021/12/12
 * Description: 权限工具
 * Author: zl
 */
public class PermissionUtil {

    private static SpecialPermissionsManage manage;
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

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                if (!Settings.canDrawOverlays(activity)) {
                    //弹窗显示
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
                    if (manage == null) {
                        manage = new SpecialPermissionsManage();
                    }
                    //设置当前未通过权限
                    manage.setCurrentPermission(permissions[nowPerm]);
                    break;
                }
                nowPerm += 1;
            }


            //拒绝了权限
            if (!isAllGranted && !manage.isAdvancedPermission(permissions[nowPerm])) {
                AlertDialog.Builder builder = new AlertDialog.Builder(activity);
                builder.setTitle("提示")
                        .setMessage("软件功能需要获取" + permissions[nowPerm].replace("android.permission.", "") + "权限, 是否继续并且选择允许?")
                        .setPositiveButton("确认", (dialog, which) -> {
                            if (manage.getCurrentPermissionState() == 1) {
                                //添加为高级权限，需要到设置里开启
                                manage.addAdvancedPermissions();
                            } else {
                                manage.updateCurrentPermissionState();
                            }
                            dialog.dismiss();
                            ActivityCompat.requestPermissions(activity, permissions, REQUEST_EXTERNAL_STORAGE);
                        })
                        .setNegativeButton("取消", (dialog, which) -> {
                            dialog.dismiss();
                            manage.addRefusePermissions();
                            activity.finish();
                        });
                builder.create().show();

            }
        }
    }


    /**
     * 权限再次申请管理
     */
    public static class SpecialPermissionsManage {
        //拒绝权限
        private List<String> refusePermissions;
        //高级权限
        private List<String> advancedPermissions;
        //当前未通过权限
        private String currentPermission = null;
        //当前未通过权限状态
        private int currentPermissionState;

        private SpecialPermissionsManage() {
            currentPermissionState = 0;
            refusePermissions = new ArrayList<>();
            advancedPermissions = new ArrayList<>();
        }

        public void setCurrentPermission(String permission) {
            currentPermission = permission;
        }

        public void addRefusePermissions() {
            refusePermissions.add(currentPermission);
        }

        public void addAdvancedPermissions() {
            advancedPermissions.add(currentPermission);
            currentPermissionState = 0;
        }

        /**
         * 检查是不是高级权限或者被拒绝到权限
         *
         * @param permission
         * @return
         */
        public boolean isAdvancedPermission(String permission) {
            if (currentPermissionState == 1 && permission.equals(currentPermission)) {
                addAdvancedPermissions();
            }
            for (int i = 0; i < refusePermissions.size(); i++) {
                if (refusePermissions.get(i).equals(permission)) {
                    return true;
                }
            }
            for (int k = 0; k < advancedPermissions.size(); k++) {
                if (advancedPermissions.get(k).equals(permission)) {
                    return true;
                }
            }
            return false;
        }

        /**
         * 更新权限状态
         */
        public void updateCurrentPermissionState() {
            if (currentPermissionState == 0) {
                currentPermissionState = 1;
            }
        }

        public int getCurrentPermissionState() {
            return currentPermissionState;
        }

    }


}
