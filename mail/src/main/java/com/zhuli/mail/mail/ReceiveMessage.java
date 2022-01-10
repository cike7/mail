package com.zhuli.mail.mail;

import com.zhuli.mail.file.CallbackProcessingListener;

/**
 * Copyright (C) 王字旁的理
 * Date: 2021/12/30
 * Description: 消息接收接口
 * Author: zl
 */
public interface ReceiveMessage<T> {
    void receive(CallbackProcessingListener<T> listener);
}
