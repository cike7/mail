package com.zhuli.mail.file;

/**
 * Copyright (C) 王字旁的理
 * Date: 2021/12/30
 * Description: 文件处理完成时
 * Author: zl
 */
public interface FileProcessingCompleteListener {
    <T> void onComplete(T t);
}
