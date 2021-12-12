package com.zhuli.mail.unit;

import android.content.ClipData;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Build;
import android.provider.MediaStore;

import com.zhuli.mail.mail.LogInfo;

import java.util.ArrayList;
import java.util.List;


/**
 * Copyright (C) 王字旁的理
 * Date: 2021/12/12
 * Description: 从Url返回结果中获取路径
 * Author: zl
 */
public class PathUnit {

    /**
     * 解析Intent返回结果，返回正式路径
     *
     * @param context
     * @param data
     * @return
     */
    public static List<String> getPaths(Context context, Intent data) {
        final List<String> paths = new ArrayList<>();
        if (data.getData() != null) {
            Uri uri = data.getData();
            String path = Build.VERSION.SDK_INT > Build.VERSION_CODES.KITKAT ? getPath(context, uri) : getRealPathFromURI(context, uri);
            paths.add(path);
        } else {
            //长按使用多选的情况
            ClipData clipData = data.getClipData();
            if (clipData != null) {
                for (int i = 0; i < clipData.getItemCount(); i++) {
                    ClipData.Item item = clipData.getItemAt(i);
                    Uri uri = item.getUri();
                    String path = Build.VERSION.SDK_INT > Build.VERSION_CODES.KITKAT ? getPath(context, uri) : getRealPathFromURI(context, uri);
                    paths.add(path);
                }
            }
        }
        return paths;
    }


    /**
     * 获取真实路径
     *
     * @param context
     * @param uri
     * @return
     */
    private static String getPath(Context context, Uri uri) {
        // 通过ContentProvider查询文件路径
        ContentResolver resolver = context.getContentResolver();
        Cursor cursor = resolver.query(uri, null, null, null, null);
        String path = null;
        if (cursor == null) {
            // 未查询到，说明为普通文件，可直接通过URI获取文件路径
            path = uri.getPath();
        } else {
            if (cursor.moveToFirst()) {
                // 多媒体文件，从数据库中获取文件的真实路径
                path = cursor.getString(cursor.getColumnIndex("_data"));
                LogInfo.e("多媒体文件，从数据库中获取文件的真实路径" + path);
            }
            if (path == null) {
                if (cursor.moveToFirst()) {
                    for (int i = 0; i < cursor.getColumnNames().length; i++) {
                        int nameColumnIndex = cursor.getColumnIndex(cursor.getColumnNames()[i]);
                        String name = cursor.getString(nameColumnIndex);
                        LogInfo.e(nameColumnIndex + "获取文件真实路径" + name);
                    }
                }
            }
            cursor.close();
        }

        return path;
    }

    /**
     * 从 URI 获取真实路径
     *
     * @param context
     * @param contentUri
     * @return
     */
    public static String getRealPathFromURI(Context context, Uri contentUri) {
        String res = null;
        String[] proj = {MediaStore.Images.Media.DATA};
        Cursor cursor = context.getContentResolver().query(contentUri, proj, null, null, null);
        if (null != cursor && cursor.moveToFirst()) {
            int column_index = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
            res = cursor.getString(column_index);
            cursor.close();
        }
        return res;
    }

}
