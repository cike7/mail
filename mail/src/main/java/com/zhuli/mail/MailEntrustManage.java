package com.zhuli.mail;

import android.annotation.SuppressLint;
import android.content.Context;
import android.widget.Toast;

import com.zhuli.mail.file.CallbackProcessingListener;
import com.zhuli.mail.mail.ICallback;
import com.zhuli.mail.mail.LogInfo;
import com.zhuli.mail.mail.MailUtil;
import com.zhuli.mail.util.NetworkDiagnosisUtil;
import com.zhuli.mail.util.ZipUtils;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * 邮箱委托管理中心
 */
public class MailEntrustManage {

    /**
     * 发送邮件
     *
     * @param toAdd     收件人
     * @param filesPath 附件地址
     */
    public static void sendFileMail(String toAdd, List<String> filesPath, ICallback<String> callback) {

        if (NetworkDiagnosisUtil.getNetworkState()) {
            if (filesPath.size() > 0) {
                List<File> files = new ArrayList<>();
                for (int i = 0; i < filesPath.size(); i++) {
                    if (filesPath.get(i) != null && filesPath.get(i).length() > 0) {
                        files.add(new File(filesPath.get(i)));
                    }
                }
                if (files.size() > 0) {
                    MailUtil.send(toAdd, files, callback);

                } else {
                    LogInfo.e("选择邮件为空");
                }
            }
        } else {
            NetworkDiagnosisUtil.notNetworkConnect();
        }
    }


    /**
     * 压缩文件
     */
    public static void compressionFile(Context context, List<String> filesPath, CallbackProcessingListener<String> callback) {

        @SuppressLint("SimpleDateFormat")
        SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd");//设置日期格式
        // 将本地的目标文件夹的文件，统一打包成test.zip,
        // 查找本地缓存的图片目录文件夹路径
        String filepath = context.getExternalFilesDir("") + File.separator + df.format(new Date()) + "-" + System.currentTimeMillis() + ".zip";
//        String filepath = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS).getPath() + File.separator + df.format(new Date()) + "-" + System.currentTimeMillis() + ".zip";

        //批量压缩到指定文件夹下并命名为test.zip
        ZipUtils.zipFiles(filesPath, filepath).setListener(o -> {
            if ((Integer) o == 200) {
                callback.onComplete(filepath);
            } else if ((Integer) o == 400) {
                callback.onComplete(null);
            }
        });

    }

}
