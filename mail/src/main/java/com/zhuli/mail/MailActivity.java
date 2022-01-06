package com.zhuli.mail;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.os.Bundle;
import android.os.Environment;
import android.os.Message;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.zhuli.mail.file.FileProcessingCompleteListener;
import com.zhuli.mail.mail.LogInfo;
import com.zhuli.mail.mail.SendMailUtil;
import com.zhuli.mail.model.MailViewModel;
import com.zhuli.mail.receiver.ReceiveHandler;
import com.zhuli.mail.util.NetworkDiagnosisUtil;
import com.zhuli.mail.util.PathUtil;
import com.zhuli.mail.util.PermissionUtil;
import com.zhuli.mail.util.ZipUtils;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Copyright (C), 2003-2021, 深圳市图派科技有限公司
 * Date: 2022/1/6
 * Description: 邮箱
 * Author: zl
 */
public class MailActivity extends AppCompatActivity {

    private EditText toAddEt;

    private RecyclerView recyclerView;

    private ActivityResultLauncher<Intent> resultLauncher;

    private MailViewModel viewModel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_mail);

        viewModel = new ViewModelProvider(this).get(MailViewModel.class);

        toAddEt = findViewById(R.id.toAddEt);
        recyclerView = findViewById(R.id.recycler_files_layout);

        NetworkDiagnosisUtil.init(this);
//        WifiContentUtil.registerReceiverWifi(this).setSpareWifi("Tenda_74E7D0", "wifi123456");
//        WifiContentUtil.registerReceiverWifi(this).setSpareWifi("Toprand01", "g00gle.999");
        PermissionUtil.verifyStoragePermissions(this);

        recyclerView.setLayoutManager(new GridLayoutManager(this, 6));
        recyclerView.setAdapter(viewModel.getAdapter());

        resultLauncher = registerForActivityResult(new ActivityResultContracts.StartActivityForResult(), new ActivityResultCallback<ActivityResult>() {
            @Override
            public void onActivityResult(ActivityResult result) {
                if (result.getResultCode() == Activity.RESULT_OK) {
                    if (result.getData() != null) {
                        viewModel.getAdapter().updateData(PathUtil.getPaths(MailActivity.this, result.getData()));
                    }
                }
            }
        });

    }

    @Override
    protected void onStart() {
        super.onStart();
        SendMailUtil.examineInit();
    }

    /**
     * 申请权限结果返回处理
     */
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        PermissionUtil.confirmPermissions(this, requestCode, permissions, grantResults);
    }

    /**
     * 发送邮件
     *
     * @param view
     */
    public void sendFileMail(View view) {

        if (NetworkDiagnosisUtil.getNetworkState()) {
            if (viewModel.getAdapter().getFilePaths().size() > 0) {
                List<File> files = new ArrayList<>();
                for (int i = 0; i < viewModel.getAdapter().getFilePaths().size(); i++) {
                    if (viewModel.getAdapter().getFilePaths().get(i) != null && viewModel.getAdapter().getFilePaths().get(i).length() > 0) {
                        files.add(new File(viewModel.getAdapter().getFilePaths().get(i)));
                    }
                }
                if (files.size() > 0) {
                    SendMailUtil.send(files, toAddEt.getText().toString());
                    Toast.makeText(this, "邮件发送成功!", Toast.LENGTH_SHORT).show();
                    viewModel.getAdapter().clear();
                } else {
                    LogInfo.e("选择邮件为空");
                }
            }
        } else {
            NetworkDiagnosisUtil.notNetworkConnect();
        }
    }


    /**
     * 添加文件
     *
     * @param view
     */
    public void getFile(View view) {

        Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
        // 所有类型
        intent.setType("*/*");
        //默认类别
        intent.addCategory(Intent.CATEGORY_DEFAULT);
        //允许多个
        intent.putExtra(Intent.EXTRA_ALLOW_MULTIPLE, true);
        //标志授予读取 URI 权限
        intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
        try {
            resultLauncher.launch(Intent.createChooser(intent, "请选择文件"));

        } catch (ActivityNotFoundException e) {
            e.printStackTrace();
            Toast.makeText(MailActivity.this, "请安装文件管理器", Toast.LENGTH_SHORT).show();
        }

    }


    //压缩文件
    public void compressionFile(View view) {
        if (viewModel.getAdapter().getFilePaths().size() < 1) {
            ReceiveHandler handler = new ReceiveHandler(this);
            Message message = handler.obtainMessage();
            message.what = 200;
            message.obj = "测试";
            handler.sendMessage(message);
            return;
        }
        @SuppressLint("SimpleDateFormat")
        SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd");//设置日期格式
        // 将本地的目标文件夹的文件，统一打包成test.zip,
        // 查找本地缓存的图片目录文件夹路径
//        String filepath = getApplication().getExternalFilesDir("") + File.separator + df.format(new Date()) + "-" + System.currentTimeMillis() + ".zip";
        String filepath = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS).getPath()
                + File.separator + df.format(new Date()) + "-" + System.currentTimeMillis() + ".zip";

        LogInfo.e(filepath);

        findViewById(R.id.but_mail_send).setClickable(false);
        findViewById(R.id.but_mail_send).setAlpha(0.4f);

        ZipUtils.zipFiles(viewModel.getAdapter().getFilePaths(), filepath).setListener(new FileProcessingCompleteListener() {
            @Override
            public <T> void onComplete(T t) {
                if ((Integer) t == 200) {
                    List<String> paths = new ArrayList<>();
                    paths.add(filepath);
                    viewModel.getAdapter().clear();
                    viewModel.getAdapter().updateData(paths);
                    findViewById(R.id.but_mail_send).setClickable(true);
                    findViewById(R.id.but_mail_send).setAlpha(1f);
                    Toast.makeText(MailActivity.this, "压缩完成！", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }


}
