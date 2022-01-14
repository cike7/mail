package com.zhuli.mail.util;

import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;

import androidx.core.content.FileProvider;

import com.zhuli.mail.file.CallbackProcessingListener;
import com.zhuli.mail.model.VersionData;

import java.io.File;

/**
 * @Description
 * @Author zhuli
 * @Date 2021/6/6/2:16 PM
 */
public class AppUpdate {


    /**
     * 检测版本更新
     *
     * @param context
     * @param callAction
     */
    public static void appUpdate(Context context, CallbackProcessingListener<VersionData> callAction) {

        try {
            //获取系统版本信息
            PackageInfo packageInfo = context.getApplicationContext()
                    .getPackageManager()
                    .getPackageInfo(context.getPackageName(), 0);

            if (compareVersion(packageInfo.versionName, "1.0.0") < 0) {

            } else {

            }

        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();

        }

    }


    /**
     * 版本号比较
     *
     * @param version1
     * @param version2
     * @return
     */
    public static int compareVersion(String version1, String version2) {
        if (version1.equals(version2)) {
            return 0;
        }
        String[] version1Array = version1.split("\\.");
        String[] version2Array = version2.split("\\.");
        int index = 0;
        // 获取最小长度值
        int minLen = Math.min(version1Array.length, version2Array.length);
        int diff = 0;
        while (index < minLen
                && (diff = Integer.parseInt(version1Array[index])
                - Integer.parseInt(version2Array[index])) == 0) {
            index++;
        }
        if (diff == 0) {
            // 如果位数不一致，比较多余位数
            for (int i = index; i < version1Array.length; i++) {
                if (Integer.parseInt(version1Array[i]) > 0) {
                    return 1;
                }
            }

            for (int i = index; i < version2Array.length; i++) {
                if (Integer.parseInt(version2Array[i]) > 0) {
                    return -1;
                }
            }
            return 0;
        } else {
            return diff > 0 ? 1 : -1;
        }
    }


    /**
     * 安装apk
     *
     * @param context 上下文
     * @param path    文件路劲
     */
    public static void installAPK(Context context, String path) {

        Intent intent = new Intent(Intent.ACTION_VIEW);
        Uri data;
        // 判断版本大于等于7.0
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            // "net.csdn.blog.ruancoder.fileprovider"即是在清单文件中配置的authoritiescom.thinker.member.bull.android_bull_member
            data = FileProvider.getUriForFile(context, "com.yzd.amzy.fileprovider", new File(path.substring(7)));
            // 给目标应用一个临时授权
            intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
        } else {
            data = Uri.fromFile(new File(path.substring(7)));
        }

        intent.setDataAndType(data, "application/vnd.android.package-archive");
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        context.startActivity(intent);
    }

}
