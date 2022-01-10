package com.zhuli.mail.mail;

import com.zhuli.mail.file.CallbackProcessingListener;

import java.io.File;
import java.util.List;


/**
 * Copyright (C) 王字旁的理
 * Date: 2021/12/12
 * Description: 邮件发送工具
 * Author: zl
 */
public class MailUtil {

    private static MailManage mailManage;

    /**
     * @param host     发送方的邮箱服务器 示例：smtp.qq.com
     * @param port     发送方的邮箱端口号 示例：587
     * @param from_add 发送方邮箱的地址 示例：1234...@qq.com
     * @param from_psw 发送方邮箱的授权码 示例：abcdxxxxxxxxxxx
     */
    public static TransportAbstraction init(String host, String port, String from_add, String from_psw) {
        if (mailManage == null) {
            mailManage = new MailManage(host, port, from_add, from_psw);
        }
        return mailManage;
    }

    public static void send(String toAdd, final List<File> files) {
        if (mailManage == null) return;
        mailManage.send(toAdd, files);
    }

    public static void receive(CallbackProcessingListener callback) {
        if (mailManage == null) return;
        mailManage.receive(callback);
    }

}
