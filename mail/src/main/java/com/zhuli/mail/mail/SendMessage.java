package com.zhuli.mail.mail;

/**
 * Copyright (C) 王字旁的理
 * Date: 2021/12/30
 * Description: 消息发送接口
 * Author: zl
 */
public interface SendMessage<T> {
    void send(T msg);
}
