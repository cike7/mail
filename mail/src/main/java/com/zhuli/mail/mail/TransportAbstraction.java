package com.zhuli.mail.mail;

import com.zhuli.mail.file.CallbackProcessingListener;

/**
 * Copyright (C) 王字旁的理
 * Date: 2021/12/30
 * Description: 消息传输抽象功能
 * Author: zl
 */
public interface TransportAbstraction {

    <T> void send(String toAdd,T data);

    void receive(CallbackProcessingListener callback);

}
