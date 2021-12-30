package com.zhuli.mail;

import android.app.Activity;
import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.FileProvider;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.zhuli.mail.adapter.FileItemAdapter;
import com.zhuli.mail.mail.LogInfo;
import com.zhuli.mail.mail.SendMailUtil;
import com.zhuli.mail.util.NetworkDiagnosisUnit;
import com.zhuli.mail.util.PathUtil;
import com.zhuli.mail.util.PermissionUtil;
import com.zhuli.mail.util.WifiContentUtil;
import com.zhuli.mail.util.ZipUtils;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class MailActivity extends AppCompatActivity {

    private EditText toAddEt;

    private RecyclerView linearLayout;

    private FileItemAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_mail);
        WifiContentUtil.registerReceiverWifi(this);

        toAddEt = findViewById(R.id.toAddEt);

        linearLayout = findViewById(R.id.recycler_files_layout);

        SendMailUtil.init("smtp.qq.com", "456", "2053095395@qq.com", "xadftgekqyktfdif");
        NetworkDiagnosisUnit.init(this);

        PermissionUtil.verifyStoragePermissions(this);

        linearLayout.setLayoutManager(new GridLayoutManager(this, 4));
        adapter = new FileItemAdapter(new ArrayList<>(), new ArrayList<>());
        linearLayout.setAdapter(adapter);

    }


    /**
     * 申请权限结果返回处理
     */
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        PermissionUtil.confirmPermissions(this, requestCode, permissions, grantResults);
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 1 && resultCode == Activity.RESULT_OK) {
            if (data != null) {
                adapter.updateData(PathUtil.getPaths(this, data));
            }
        }
    }


    /**
     * 发送邮件
     *
     * @param view
     */
    public void sendFileMail(View view) {

        if (NetworkDiagnosisUnit.getNetworkState()) {
            if (adapter.getFilePaths().size() > 0) {
                linearLayout.removeAllViews();
                List<File> files = new ArrayList<>();
                for (int i = 0; i < adapter.getFilePaths().size(); i++) {
                    if (adapter.getFilePaths().get(i) != null && adapter.getFilePaths().get(i).length() > 0) {
                        files.add(new File(adapter.getFilePaths().get(i)));
                    }
                }
                if (files.size() > 0) {
                    SendMailUtil.send(files, toAddEt.getText().toString());
                    adapter.getFilePaths().clear();
                    LogInfo.e("邮件发送成功");
                } else {
                    LogInfo.e("选择邮件为空");
                }
            }
        } else {
            NetworkDiagnosisUnit.notNetworkConnect();
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
            startActivityForResult(Intent.createChooser(intent, "请选择文件"), 1);
        } catch (ActivityNotFoundException e) {
            e.printStackTrace();
            Toast.makeText(MailActivity.this, "请安装文件管理器", Toast.LENGTH_SHORT).show();
        }

    }


    //压缩文件
    public void compressionFile(View view) {

        SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd");//设置日期格式
        // 将本地的目标文件夹的文件，统一打包成test.zip,
        // 查找本地缓存的图片目录文件夹路径
//        String filepath = getApplication().getExternalFilesDir() + File.separator + df.format(new Date()) + "-" + System.currentTimeMillis() + ".zip";
        String filepath = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS).getPath() + File.separator + df.format(new Date()) + "-" + System.currentTimeMillis() + ".zip";
        LogInfo.e(filepath);
        //批量压缩到指定文件夹下并命名为test.zip
        try {
            ZipUtils.zipFiles(adapter.getFilePaths(), filepath);
        } catch (IOException e) {
            e.printStackTrace();
        }

    }


}
