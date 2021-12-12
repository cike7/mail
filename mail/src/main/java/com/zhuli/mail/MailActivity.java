package com.zhuli.mail;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.zhuli.mail.annotation.AutoProperties;
import com.zhuli.mail.annotation.BindString;
import com.zhuli.mail.mail.SendMailUtil;
import com.zhuli.mail.unit.PathUnit;
import com.zhuli.mail.unit.PermissionUnit;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class MailActivity extends AppCompatActivity {

    @BindString("HOST")
    private String HOST;

    @BindString("PORT")
    private String PORT;

    @BindString("FROM_ADD")
    private String FROM_ADD;

    @BindString("FROM_PSW")
    private String FROM_PSW;

    private EditText toAddEt;

    private LinearLayout linearLayout;

    private List<String> paths;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_mail);

        toAddEt = findViewById(R.id.toAddEt);
        linearLayout = findViewById(R.id.linear_files_layout);

        AutoProperties.bind(this, "config.properties");

        SendMailUtil.init(HOST, PORT, FROM_ADD, FROM_PSW);

        PermissionUnit.verifyStoragePermissions(this);

    }


    /**
     * 申请权限结果返回处理
     */
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        PermissionUnit.confirmPermissions(this, requestCode, permissions, grantResults);
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == Activity.RESULT_OK) {
            if (paths == null) {
                paths = new ArrayList<>();
            }
            List<String> getPaths = PathUnit.getPaths(this, data);
            for (String path : getPaths) {
                //判断是否存在重复的文件
                if (paths.contains(path)) {
                    getPaths.remove(path);
                } else {
                    //判断路径是否有效
                    if (path == null || path.equals("") || path.length() < 1) {
                        getPaths.remove(path);
                    }
                }
            }
            if (getPaths.size() > 0) {
                paths.addAll(getPaths);
                linearLayout.removeAllViews();
                for (int i = 0; i < paths.size(); i++) {
                    TextView textView = new TextView(MailActivity.this);
                    textView.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));
                    String[] filePath = paths.get(i).split("/");
                    textView.setText(filePath[filePath.length - 1]);
                    textView.setTextSize(14);
                    textView.setTextColor(Color.BLACK);
                    linearLayout.addView(textView);
                }
            }
        }
    }


    public void sendFileMail(View view) {
        linearLayout.removeAllViews();
        List<File> files = new ArrayList<>();
        for (int i = 0; i < paths.size(); i++) {
            if (paths.get(i) != null && paths.get(i).length() > 0) {
                files.add(new File(paths.get(i)));
            }
        }
        if (files.size() > 0) {
            SendMailUtil.send(files, toAddEt.getText().toString());
            paths.clear();
            Toast.makeText(MailActivity.this, "邮件发送成功", Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(MailActivity.this, "选择邮件为空", Toast.LENGTH_SHORT).show();
        }
    }


    public void getFile(View view) {

        Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
//        //选择图片
//        intent.setType("image/*");
//        //选择音频
//        intent.setType("audio/*");
//        //选择视频 （mp4 3gp 是android支持的视频格式）
//        intent.setType("video/*");
//        //同时选择视频和图片
//        intent.setType("video/*;image/*");
        //无类型限制
        intent.setType("*/*");
        //额外允许多个
        intent.putExtra(Intent.EXTRA_ALLOW_MULTIPLE, true);
        intent.addCategory(Intent.CATEGORY_OPENABLE);
        startActivityForResult(intent, 1);
    }


}
