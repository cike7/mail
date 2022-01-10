package com.zhuli.mail.mail;

/**
 * Copyright (C) 王字旁的理
 * Date: 2021/12/31
 * Description:
 * Author: zl
 */
public interface ICallback<T> {
    void onCall(T t);
}
