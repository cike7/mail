package com.zhuli.mail.mail;

import android.annotation.SuppressLint;

import androidx.annotation.NonNull;

import com.zhuli.mail.file.CallbackProcessingListener;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class MailManage implements TransportAbstraction {

    private final String SEND_HOST;
    private final String SEND_PORT;

    private String RECEIVE_HOST;
    private String RECEIVE_PORT;

    private final String FROM_ADD;
    private final String FROM_PSW;

    //发送完成回调
    private ICallback<String> callback;
    //单个核线的fixed
    private final ExecutorService executor;

    /**
     * @param host     发送方的邮箱服务器 示例：smtp.qq.com
     * @param port     发送方的邮箱端口号 示例：587
     * @param from_add 发送方邮箱的地址 示例：1234...@qq.com
     * @param from_psw 发送方邮箱的授权码 示例：abcdxxxxxxxxxxx
     */
    public MailManage(String host, String port, String from_add, String from_psw) {

        this.SEND_HOST = host;
        this.SEND_PORT = port;
        this.FROM_ADD = from_add;
        this.FROM_PSW = from_psw;

        executor = Executors.newSingleThreadExecutor();

        if (SEND_HOST == null || SEND_HOST.equals("")) {
            new NullPointerException("邮箱未初始化").printStackTrace();
        } else if (SEND_PORT == null || SEND_PORT.equals("")) {
            new NullPointerException("邮箱未初始化").printStackTrace();
        } else if (FROM_ADD == null || FROM_ADD.equals("")) {
            new NullPointerException("邮箱未初始化").printStackTrace();
        } else if (FROM_PSW == null || FROM_PSW.equals("")) {
            new NullPointerException("邮箱未初始化").printStackTrace();
        }
    }

    /**
     * 设置接收邮箱主机
     *
     * @param host imap.qq.com
     * @param port 993
     */
    public void setReceiveHost(String host, String port) {
        this.RECEIVE_HOST = host;
        this.RECEIVE_PORT = port;
    }

    public <T> void send(String toAdd, T data, ICallback<String> callback) {
        send(toAdd, data);
        this.callback = callback;
    }

    @Override
    public <T> void send(String toAdd, T data) {

        if (data instanceof Iterable) {
            MailInfo mailInfo = createSendMail(toAdd, (List<File>) data);
            executor.execute(() -> {
                SendMessage<MailInfo> mail = new JakartaSendMailImp(callback);
//                MailSend mail = new JavaxMailImp();
                mail.send(mailInfo);
            });
        }

    }


    @Override
    public void receive(CallbackProcessingListener callback) {

        MailInfo mailInfo = createReceiveMail();
        executor.execute(() -> {
            ReceiveMessage data = new JakartaReceiveMailImp(mailInfo);
            data.receive(callback);
        });

    }

    @SuppressLint("SimpleDateFormat")
    @NonNull
    private MailInfo createSendMail(String toAdd, List<File> files) {
        return createSendMail(toAdd, new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date()), "", files);
    }

    @NonNull
    private MailInfo createSendMail(String toAdd, String subject, String content) {
        return createSendMail(toAdd, subject, content, new ArrayList<>());
    }

    @NonNull
    private MailInfo createSendMail(String toAdd, String subject, String content, List<File> files) {
        final MailInfo mailInfo = new MailInfo();
        mailInfo.setMailServerSendHost(SEND_HOST);//发送方邮箱服务器
        mailInfo.setMailServerSendPort(SEND_PORT);//发送方邮箱端口号
        mailInfo.setUserName(FROM_ADD); // 发送者邮箱地址
        mailInfo.setPassword(FROM_PSW);// 发送者邮箱授权码
        mailInfo.setFromAddress(FROM_ADD); // 发送者邮箱
        mailInfo.setTransportProtocol("smtp");//指定邮件发送协议
        mailInfo.setValidate(true);// 开启验证
        mailInfo.setToAddress(toAdd); // 接收者邮箱
        mailInfo.setSubject(subject); // 邮件主题
        mailInfo.setContent(content); // 邮件文本
        mailInfo.setAttachFiles(files); //附加文件
        return mailInfo;
    }

    @NonNull
    private MailInfo createReceiveMail() {
        final MailInfo mailInfo = new MailInfo();
        mailInfo.setMailServerReceiveHost(RECEIVE_HOST);//发送方邮箱服务器
        mailInfo.setMailServerReceivePort(RECEIVE_PORT);//发送方邮箱端口号
        mailInfo.setUserName(FROM_ADD); // 发送者邮箱地址
        mailInfo.setPassword(FROM_PSW);// 发送者邮箱授权码
        mailInfo.setFromAddress(FROM_ADD); // 发送者邮箱
        mailInfo.setStoreProtocol("imap");//指定邮件接收协议
        mailInfo.setValidate(true);// 开启验证
        return mailInfo;
    }

}
