package com.zhuli.mail.mail;
import java.util.Date;
import java.util.Properties;

import javax.activation.DataHandler;
import javax.activation.FileDataSource;
import javax.mail.Address;
import javax.mail.Authenticator;
import javax.mail.BodyPart;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.Multipart;
import javax.mail.PasswordAuthentication;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeBodyPart;
import javax.mail.internet.MimeMessage;
import javax.mail.internet.MimeMultipart;
import javax.mail.internet.MimeUtility;


/**
 * Copyright (C) 王字旁的理
 * Date: 2021/12/12
 * Description: 邮件发送器
 * Author: zl
 */
public class MailSender {

    /**
     * 以HTML格式发送邮件
     *
     * @param mailInfo 待发送的邮件信息
     */
    public static void sendHtmlMail(MailInfo mailInfo) {
        // 判断是否需要身份认证
        MyAuthenticator authenticator = null;
        Properties pro = mailInfo.getProperties();
        // 如果需要身份认证，则创建一个密码验证器
        if (mailInfo.isValidate()) {
            authenticator = new MyAuthenticator(mailInfo.getUserName(), mailInfo.getPassword());
        }
        // 根据邮件会话属性和密码验证器构造一个发送邮件的session
        Session sendMailSession = Session.getDefaultInstance(pro, authenticator);
        try {
            // 根据session创建一个邮件消息
            Message mailMessage = new MimeMessage(sendMailSession);
            // 创建邮件发送者地址
            Address from = new InternetAddress(mailInfo.getFromAddress());
            // 设置邮件消息的发送者
            mailMessage.setFrom(from);
            // 创建邮件的接收者地址，并设置到邮件消息中
            Address to = new InternetAddress(mailInfo.getToAddress());
            // Message.RecipientType.TO属性表示接收者的类型为TO
            mailMessage.setRecipient(Message.RecipientType.TO, to);
            // 设置邮件消息的主题
            mailMessage.setSubject(mailInfo.getSubject());
            // 设置邮件消息发送的时间
            mailMessage.setSentDate(new Date());
            // MiniMultipart类是一个容器类，包含MimeBodyPart类型的对象
            Multipart mainPart = new MimeMultipart();
            // 创建一个包含HTML内容的MimeBodyPart
            BodyPart html = new MimeBodyPart();
            // 设置HTML内容
            html.setContent(mailInfo.getContent(), "text/html; charset=utf-8");
            mainPart.addBodyPart(html);
            // 将MiniMultipart对象设置为邮件内容
            mailMessage.setContent(mainPart);
            // 发送邮件
            Transport.send(mailMessage);

        } catch (MessagingException ex) {
            ex.printStackTrace();
        }
    }


    /**
     * 以文本格式发送邮件
     *
     * @param mailInfo 待发送的邮件的信息
     */
    public void sendTextMail(final MailInfo mailInfo) {

        // 判断是否需要身份认证
        MyAuthenticator authenticator = null;
        Properties pro = mailInfo.getProperties();
        if (mailInfo.isValidate()) {
            // 如果需要身份认证，则创建一个密码验证器
            authenticator = new MyAuthenticator(mailInfo.getUserName(), mailInfo.getPassword());
        }
        // 根据邮件会话属性和密码验证器构造一个发送邮件的session
        Session sendMailSession = Session.getDefaultInstance(pro, authenticator);

        try {
            // 根据session创建一个邮件消息
            Message mailMessage = new MimeMessage(sendMailSession);
            // 创建邮件发送者地址
            Address from = new InternetAddress(mailInfo.getFromAddress());
            // 设置邮件消息的发送者
            mailMessage.setFrom(from);
            // 创建邮件的接收者地址，并设置到邮件消息中
            Address to = new InternetAddress(mailInfo.getToAddress());
            // 设置收件人
            mailMessage.setRecipient(Message.RecipientType.TO, to);
            // 设置邮件消息的主题
            mailMessage.setSubject(mailInfo.getSubject());
            // 设置邮件消息发送的时间
            mailMessage.setSentDate(new Date());
            // 设置邮件消息的主要内容
            mailMessage.setText(mailInfo.getContent());
            // 发送邮件
            Transport.send(mailMessage);
        } catch (MessagingException ex) {
            ex.printStackTrace();
        }
    }


    /**
     * 发送带附件的邮件
     *
     * @param info
     * @return
     */
    public void sendFileMail(MailInfo info) {
        Message attachmentMail = createAttachmentMail(info);
        try {
            Transport.send(attachmentMail);
        } catch (MessagingException ignored) {
            LogInfo.e("发送带附件的邮件失败");
        }
    }


    /**
     * 创建带有附件的邮件
     *
     * @return
     */
    private Message createAttachmentMail(final MailInfo info) {
        //创建邮件
        MimeMessage message = null;
        Properties pro = info.getProperties();
        try {
            Session sendMailSession = Session.getInstance(pro, new Authenticator() {
                @Override
                protected PasswordAuthentication getPasswordAuthentication() {
                    return new PasswordAuthentication(info.getUserName(), info.getPassword());
                }
            });
            message = new MimeMessage(sendMailSession);
            // 设置邮件的基本信息
            //创建邮件发送者地址
            Address from = new InternetAddress(info.getFromAddress());
            //设置邮件消息的发送者
            message.setFrom(from);
            //创建邮件的接受者地址，并设置到邮件消息中
            Address to = new InternetAddress(info.getToAddress());
            //设置邮件消息的接受者, Message.RecipientType.TO属性表示接收者的类型为TO
            message.setRecipient(Message.RecipientType.TO, to);
            //邮件标题
            message.setSubject(info.getSubject());
            // 创建邮件正文，为了避免邮件正文中文乱码问题，需要使用CharSet=UTF-8指明字符编码
            MimeBodyPart text = new MimeBodyPart();
            text.setContent(info.getContent(), "text/html;charset=UTF-8");
            // 创建容器描述数据关系
            MimeMultipart mp = new MimeMultipart();
            mp.addBodyPart(text);

            for (int i = 0; i < info.getAttachFiles().size(); i++) {
                // 创建邮件附件
                MimeBodyPart attach = new MimeBodyPart();
                FileDataSource ds = new FileDataSource(info.getAttachFiles().get(i));
                DataHandler dh = new DataHandler(ds);
                attach.setDataHandler(dh);
                attach.setFileName(MimeUtility.encodeText(dh.getName()));
                mp.addBodyPart(attach);
            }

            mp.setSubType("mixed");
            message.setContent(mp);
            message.saveChanges();

        } catch (Exception ignored) {
            LogInfo.e("创建带附件的邮件失败");
        }
        // 返回生成的邮件
        return message;
    }

}
