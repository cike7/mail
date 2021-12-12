package com.zhuli.mail.annotation;

import android.content.Context;
import android.text.TextUtils;

import java.io.IOException;
import java.lang.reflect.Field;
import java.util.Properties;

/**
 * Copyright (C), 2003-2021, 深圳市图派科技有限公司
 * Date: 2021/9/7
 * Description: 实现注解绑定Properties字段
 * Author: zl
 */
public class AutoProperties {

    /**
     * 实现注解绑定Properties文件的字段
     * @param context 当前Activity
     * @param fileName Properties配置文件的文件名
     */
    public static void bind(Context context, String fileName){

        Properties properties = new Properties();
        try {
            //加载配置文件
            properties.load(context.getAssets().open(fileName));
        } catch (IOException e) {
            e.printStackTrace();
        }

        // 获取类信息
        Class<? extends Context> aClass = context.getClass();
        // 获取所有属性
        Field[] declaredFields = aClass.getDeclaredFields();
        if (declaredFields.length == 0) {
            return;
        }

        for (Field field : declaredFields) {
            // 获取属性上的注解
            BindString annotation = field.getAnnotation(BindString.class);
            if (annotation != null) {
                //获取注解的值
                String key = annotation.value();
                if (TextUtils.isEmpty(key)) {
                    // 如果注解没有设置key，则设置字段的名称为key
                    key = field.getName();
                }
                try {
                    // 获取到传递过来的值  这种获取到的数据不用判断类型，后面直接赋值给属性即可
                    String value = properties.getProperty(key);
                    if (TextUtils.isEmpty(key)) {
                        return;
                    }
                    // 设置属性可改变（私有属性才需要进行设置，共有属性设置了也没关系）
                    field.setAccessible(true);
                    // 设置值到当前页面的属性上面
                    field.set(context, value);

                } catch (IllegalAccessException e) {
                    e.printStackTrace();
                }
            }
        }
    }

}
