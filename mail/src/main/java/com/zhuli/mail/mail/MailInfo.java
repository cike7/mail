package com.zhuli.mail.mail;

import java.io.File;
import java.util.List;
import java.util.Properties;

/**
 * Copyright (C) 王字旁的理
 * Date: 2021/12/12
 * Description: 邮箱消息类
 * Author: zl
 */
public class MailInfo {

    private String mailServerHost;// 发送邮件的服务器的IP
    private String mailServerPort;// 发送邮件的服务器的端口
    private String fromAddress;// 邮件发送者的地址
    private String toAddress;    // 邮件接收者的地址
    private String userName;// 登陆邮件发送服务器的用户名
    private String password;// 登陆邮件发送服务器的密码
    private boolean validate = true;// 是否需要身份验证
    private String subject;// 邮件主题
    private String content;// 邮件的文本内容
    private List<File> attachFiles;// 邮件附件的文件名
    
    public String getMailServerHost() {
        return mailServerHost;
    }

    public void setMailServerHost(String mailServerHost) {
        this.mailServerHost = mailServerHost;
    }

    public String getMailServerPort() {
        return mailServerPort;
    }

    public void setMailServerPort(String mailServerPort) {
        this.mailServerPort = mailServerPort;
    }

    public boolean isValidate() {
        return validate;
    }

    public void setValidate(boolean validate) {
        this.validate = validate;
    }

    public List<File> getAttachFiles() {
        return attachFiles;
    }

    public void setAttachFiles(List<File> files) {
        this.attachFiles = files;
    }

    public String getFromAddress() {
        return fromAddress;
    }

    public void setFromAddress(String fromAddress) {
        this.fromAddress = fromAddress;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getToAddress() {
        return toAddress;
    }

    public void setToAddress(String toAddress) {
        this.toAddress = toAddress;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getSubject() {
        return subject;
    }

    public void setSubject(String subject) {
        this.subject = subject;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String textContent) {
        this.content = textContent;
    }

    /**
     * 获得邮件会话属性
     */
    public Properties getProperties() {
        Properties p = new Properties();
        p.put("mail.smtp.host", getMailServerHost());
        p.put("mail.smtp.port", getMailServerPort());
        p.put("mail.smtp.auth", isValidate() ? "true" : "false");
        return p;
    }

}