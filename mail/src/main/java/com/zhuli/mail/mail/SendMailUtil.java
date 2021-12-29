package com.zhuli.mail.mail;

import androidx.annotation.NonNull;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;


/**
 * Copyright (C) 王字旁的理
 * Date: 2021/12/12
 * Description: 邮件发送工具
 * Author: zl
 */
public class SendMailUtil {

    private static String HOST;
    private static String PORT;
    private static String FROM_ADD;
    private static String FROM_PSW;

    //单个核线的fixed
    private static final ExecutorService executor = Executors.newSingleThreadExecutor();

    /**
     * @param host     发送方的邮箱服务器 示例：smtp.qq.com
     * @param port     发送方的邮箱端口号 示例：587
     * @param from_add 发送方邮箱的地址 示例：1234...@qq.com
     * @param from_psw 发送方邮箱的授权码 示例：abcdxxxxxxxxxxx
     */
    public static void init(String host, String port, String from_add, String from_psw) {
        HOST = host;
        PORT = port;
        FROM_ADD = from_add;
        FROM_PSW = from_psw;
    }

    public static void send(final List<File> files, String toAdd) {
        final MailInfo mailInfo = createMail(toAdd, files);
        final MailSender sms = new MailSender();
        executor.execute(new Runnable() {
            @Override
            public void run() {
                sms.sendFileMail(mailInfo);
            }
        });
    }

    public static void send(String toAdd, String subject, String content) {
        final MailInfo mailInfo = createMail(toAdd, subject, content);
        final MailSender sms = new MailSender();
        executor.execute(new Runnable() {
            @Override
            public void run() {
                sms.sendTextMail(mailInfo);
            }
        });
    }

    @NonNull
    private static MailInfo createMail(String toAdd, String subject, String content) {
        return createMail(toAdd, subject, content, new ArrayList<>());
    }

    @NonNull
    private static MailInfo createMail(String toAdd, List<File> files) {
        return createMail(toAdd, "", "", files);
    }

    @NonNull
    private static MailInfo createMail(String toAdd, String subject, String content, List<File> files) {
        final MailInfo mailInfo = new MailInfo();
        mailInfo.setMailServerHost(HOST);//发送方邮箱服务器
        mailInfo.setMailServerPort(PORT);//发送方邮箱端口号
        mailInfo.setValidate(true);
        mailInfo.setUserName(FROM_ADD); // 发送者邮箱地址
        mailInfo.setPassword(FROM_PSW);// 发送者邮箱授权码
        mailInfo.setFromAddress(FROM_ADD); // 发送者邮箱
        mailInfo.setToAddress(toAdd); // 接收者邮箱
        mailInfo.setSubject(subject); // 邮件主题
        mailInfo.setContent(content); // 邮件文本
        mailInfo.setAttachFiles(files);
        return mailInfo;
    }


}
