package com.zhuli.mail.mail;

import com.zhuli.mail.file.CallbackProcessingListener;

import java.util.Properties;

import jakarta.mail.Folder;
import jakarta.mail.Message;
import jakarta.mail.Session;
import jakarta.mail.Store;


/**
 * Copyright (C) 王字旁的理
 * Date: 2021/12/30
 * Description: 接收邮箱具体实现
 * Author: zl
 */
public class JakartaReceiveMailImp implements ReceiveMessage<Message[]> {

    private final MailInfo info;

    public JakartaReceiveMailImp(MailInfo info) {
        this.info = info;
    }

    @Override
    public void receive(CallbackProcessingListener<Message[]> listener) {
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
//            Message[] msg = mbox.getMessages();
            listener.onComplete(mbox.getMessages());

        } catch (Exception e) {
            LogInfo.e("接收邮件错误！");
            e.printStackTrace();
        }

    }


}
