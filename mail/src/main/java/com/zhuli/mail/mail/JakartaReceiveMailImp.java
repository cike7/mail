package com.zhuli.mail.mail;

import com.zhuli.mail.file.CallbackProcessingListener;
import com.zhuli.mail.util.StringUtil;

import java.util.Properties;

import jakarta.mail.Address;
import jakarta.mail.Folder;
import jakarta.mail.Message;
import jakarta.mail.Session;
import jakarta.mail.Store;
import jakarta.mail.internet.MimeMultipart;


/**
 * Copyright (C) 王字旁的理
 * Date: 2021/12/30
 * Description: 接收邮箱具体实现
 * Author: zl
 */
public class JakartaReceiveMailImp implements ReceiveMessage<String> {

    private final MailInfo info;

    public JakartaReceiveMailImp(MailInfo info) {
        this.info = info;
    }

    @Override
    public void receive(CallbackProcessingListener<String> listener) {
        try {
            Properties props = info.getReceiveProperties();
            // 创建会话
            Session session = Session.getDefaultInstance(props);
            //创建存储库
            Store store = session.getStore(info.getStoreProtocol());
            //建立连接
            store.connect(info.getMailServerReceiveHost(), Integer.parseInt(info.getMailServerReceivePort()), info.getUserName(), info.getPassword());

            // -------------- 设置邮件的基本信息 --------------
            // 获取收件箱 store.getDefaultForlder
            Folder mbox = store.getFolder("INBOX");
            // INBOX
            mbox.open(Folder.READ_ONLY);
            //邮件总数
            int msgCount = mbox.getMessageCount();
            LogInfo.e("邮件总数：" + msgCount);
            // 取最新的邮件
            Message[] msg = mbox.getMessages();

            for (int i = 0; i < msg.length; i++) {
                LogInfo.e("--------------" + i + "--------------");

                LogInfo.e("getFolderv：" + msg[i].getFolder());
                LogInfo.e("getSubject：" + msg[i].getSubject());
                LogInfo.e("getContentType：" + msg[i].getContentType());
                LogInfo.e("getFolder：" + msg[i].getFolder().getName());
                for (Address add : msg[i].getFrom()) {
                    LogInfo.e("getFrom：" + add);
                }
                LogInfo.e("getDescription：" + msg[i].getDescription());
                LogInfo.e("getDisposition：" + msg[i].getDisposition());
                LogInfo.e("getFileName：" + msg[i].getFileName());

                for (Address add : msg[i].getAllRecipients()) {
                    LogInfo.e("getAllRecipients：" + add);
                }
                LogInfo.e("getMessageNumber：" + msg[i].getMessageNumber());
                LogInfo.e("getReceivedDate：" + msg[i].getReceivedDate());
                LogInfo.e("getSentDate：" + msg[i].getSentDate());
                LogInfo.e("getSession：" + msg[i].getSession().toString());
                LogInfo.e("getLineCount：" + msg[i].getLineCount());
                LogInfo.e("getSize：" + msg[i].getSize());
                LogInfo.e("收件人：" + msg[i].getRecipients(Message.RecipientType.TO));
                LogInfo.e("抄送人：" + msg[i].getRecipients(Message.RecipientType.CC));
                LogInfo.e("密送人：" + msg[i].getRecipients(Message.RecipientType.BCC));

                MimeMultipart mimeMultipart = (MimeMultipart) msg[i].getContent();

                LogInfo.e("1内容：" + mimeMultipart.getBodyPart(0).getContent());

                LogInfo.e("url : " + StringUtil.getUrl(mimeMultipart.getBodyPart(0).getContent().toString()));

//                for (int k = 0; k < mimeMultipart.getCount(); k++) {
//                    BodyPart part = mimeMultipart.getBodyPart(k);
//                    LogInfo.e("2内容：" + part.getContent());
//                }

//                listener.onComplete("" + (MimeMultipart) ((MimeMultipart) msg[i].getContent()).getBodyPart(1).getContent());

            }

        } catch (Exception e) {
            LogInfo.e("接收邮件错误！");
            e.printStackTrace();
        }

    }


}
