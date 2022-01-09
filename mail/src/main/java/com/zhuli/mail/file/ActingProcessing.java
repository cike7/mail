package com.zhuli.mail.file;

/**
 * Copyright (C) 王字旁的理
 * Date: 2021/12/30
 * Description: 文件代理处理器
 * Author: zl
 */
public interface ActingProcessing<T> {

    void startProcessingFile(String path, CallbackProcessingListener<T> listener);

}
