package com.zhuli.mail.mail;

import javax.mail.Authenticator;
import javax.mail.PasswordAuthentication;

/**
 * Copyright (C) 王字旁的理
 * Date: 2021/12/12
 * Description: 身份验证器
 * Author: zl
 */
public class MyAuthenticator extends Authenticator {
    String userName;
    String password;

    public MyAuthenticator(String username, String password) {
        this.userName = username;
        this.password = password;
    }

    protected PasswordAuthentication getPasswordAuthentication() {
        return new PasswordAuthentication(userName, password);
    }
}
