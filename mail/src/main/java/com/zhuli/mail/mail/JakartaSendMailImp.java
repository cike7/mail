package com.zhuli.mail.mail;

import android.os.Handler;
import android.os.Looper;

import androidx.annotation.NonNull;

import com.zhuli.mail.util.FileMime;

import java.io.File;
import java.io.IOException;
import java.util.Date;
import java.util.Properties;

import jakarta.mail.Address;
import jakarta.mail.Authenticator;
import jakarta.mail.Message;
import jakarta.mail.MessagingException;
import jakarta.mail.PasswordAuthentication;
import jakarta.mail.Session;
import jakarta.mail.Transport;
import jakarta.mail.internet.InternetAddress;
import jakarta.mail.internet.MimeBodyPart;
import jakarta.mail.internet.MimeMessage;
import jakarta.mail.internet.MimeMultipart;


/**
 * Copyright (C) 王字旁的理
 * Date: 2021/12/30
 * Description:
 * Author: zl
 */
public class JakartaSendMailImp implements SendMessage<MailInfo> {

    private Handler handler;
    private ICallback<String> callback;

    public JakartaSendMailImp() {

    }

    public JakartaSendMailImp(ICallback<String> callback) {
        if (callback == null) return;
        this.callback = callback;
        handler = new Handler(Looper.getMainLooper()) {
            @Override
            public void handleMessage(android.os.Message msg) {
                super.handleMessage(msg);
                callback.onCall((String) msg.obj);
            }
        };
    }

    @Override
    public void send(MailInfo info) {

        android.os.Message handleMessage = handler.obtainMessage();

        try {
            Properties props = info.getSendProperties();
            // 创建会话
            Session session = Session.getInstance(props, new Authenticator() {
                @Override
                protected PasswordAuthentication getPasswordAuthentication() {
                    return new PasswordAuthentication(info.getUserName(), info.getPassword());
                }
            });
            // -------------- 设置邮件的基本信息 --------------
            MimeMessage msg = new MimeMessage(session);
            // 设置发送者
            msg.setFrom(new InternetAddress(info.getFromAddress()));
            // 设置接收者
            Address[] address = new Address[info.getToAddress().size()];
            for (int i = 0; i < address.length; i++) {
                address[i] = new InternetAddress(info.getToAddress().get(i));
            }
            // 可以用msg.setRecipients方法增加多个接收人，指定接收人类型
            // msg.RecipientType.CC 抄送
            // msg.RecipientType.BCC 密送
            // msg.RecipientType.TO 接收
            msg.setRecipients(Message.RecipientType.TO, address);
            // 邮件标题
            msg.setSubject(info.getSubject());
            // 创建邮件正文
            MimeBodyPart text = new MimeBodyPart();
            // 为了避免邮件正文中文乱码问题，需要使用CharSet=UTF-8指明字符编码
            text.setContent(info.getContent(), "text/html;charset=UTF-8");
            // 创建容器描述数据关系
            MimeMultipart mp = new MimeMultipart();
            // 设置邮件正文
            mp.addBodyPart(text);
            mp.setSubType("mixed");
            // 附件
            if (info.getAttachFiles().size() > 0) {
                MimeBodyPart attach = new MimeBodyPart();
                // 创建邮件附件
                for (File file : info.getAttachFiles()) {
                    attach.attachFile(file);
                }
                mp.addBodyPart(attach);
            }
            msg.setContent(mp);
            msg.setSentDate(new Date());
            msg.saveChanges();
            Transport.send(msg);
            LogInfo.e("邮件发送成功");
            if (callback != null) {
                handleMessage.obj = "邮件发送成功!";
            }

        } catch (MessagingException | IOException e) {
            LogInfo.e("发送邮件错误！" + e.getMessage());
            if (callback != null) {
                handleMessage.obj = "邮件发送成功!" + e.getMessage();
            }

        }
        if (callback != null) {
            handler.sendMessage(handleMessage);
        }
    }

}
