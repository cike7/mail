package com.zhuli.mail.file;

/**
 * Copyright (C) 王字旁的理
 * Date: 2021/12/30
 * Description: 处理完成回调事件
 * Author: zl
 */
public interface CallbackProcessingListener<T> {
    void onComplete(T t);
}
