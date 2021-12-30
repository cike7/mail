//package com.zhuli.mail.mail;
//
//import java.io.File;
//import java.util.Date;
//import java.util.Properties;
//
//import javax.mail.Address;
//import javax.mail.Authenticator;
//import javax.mail.Message;
//import javax.mail.PasswordAuthentication;
//import javax.mail.Session;
//import javax.mail.Transport;
//import javax.mail.internet.InternetAddress;
//import javax.mail.internet.MimeBodyPart;
//import javax.mail.internet.MimeMessage;
//import javax.mail.internet.MimeMultipart;
//
//
///**
// * Copyright (C) 王字旁的理
// * Date: 2021/12/12
// * Description: 邮件发送器
// * Author: zl
// */
//public class JavaxMailImp implements MailSend {
//
//    @Override
//    public void send(MailInfo info) {
//        try {
//            Properties pro = info.getProperties();
//            Session sendMailSession = Session.getInstance(pro, new Authenticator() {
//                @Override
//                protected PasswordAuthentication getPasswordAuthentication() {
//                    return new PasswordAuthentication(info.getUserName(), info.getPassword());
//                }
//            });
//            // -------------- 设置邮件的基本信息 --------------
//            MimeMessage msg = new MimeMessage(sendMailSession);
//            //设置邮件消息的发送者
//            msg.setFrom(new InternetAddress(info.getFromAddress()));
//            //创建邮件的接收者地址，并设置到邮件消息中
//            Address[] address = new Address[info.getToAddress().size()];
//            for (int i = 0; i < address.length; i++) {
//                address[i] = new InternetAddress(info.getToAddress().get(i));
//            }
//            // 设置邮件消息的接收者,
//            // Message.RecipientType.TO属性表示接收者的类型为TO
//            msg.setRecipients(Message.RecipientType.TO, address);
//            //邮件标题
//            msg.setSubject(info.getSubject());
//            // 创建邮件正文
//            MimeBodyPart text = new MimeBodyPart();
//            // 为了避免邮件正文中文乱码问题，需要使用CharSet=UTF-8指明字符编码
//            text.setContent(info.getContent(), "text/html;charset=UTF-8");
//            // 创建容器描述数据关系
//            MimeMultipart mp = new MimeMultipart();
//            // 设置邮件正文
//            mp.addBodyPart(text);
//            // 创建邮件附件
//            if (info.getAttachFiles().size() > 0) {
//                for (File file : info.getAttachFiles()) {
//                    MimeBodyPart attach = new MimeBodyPart();
//                    attach.attachFile(file);
//                    mp.addBodyPart(attach);
//                }
//                mp.setSubType("mixed");
//            }
//            msg.setContent(mp);
//            msg.saveChanges();
//            msg.setSentDate(new Date());
//            Transport.send(msg);
//            LogInfo.e("邮件发送成功");
//
//        } catch (Exception e) {
//            LogInfo.e("创建带附件的邮件失败");
//            e.printStackTrace();
//        }
//
//    }
//
//}
