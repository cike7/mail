package com.zhuli.mail;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.Observer;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.zhuli.mail.adapter.FileItemAdapter;
import com.zhuli.mail.file.CallbackProcessingListener;
import com.zhuli.mail.file.DocumentProcessingFactory;
import com.zhuli.mail.mail.LogInfo;
import com.zhuli.mail.util.PathUtil;

import java.util.ArrayList;
import java.util.List;


/**
 * 解压文件
 */
public class UnzipActivity extends AppCompatActivity {

    public final static String UNZIP_ACTION = "android.intent.action.com.unzip";
    public final String FILE_PATH = "FILE_PATH";

    private TextView textView;
    private MutableLiveData<String> unzipPath;
    private RecyclerView recyclerView;
    private FileItemAdapter adapter;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_unzip);

        recyclerView = findViewById(R.id.recycler_files_layout);
        textView = findViewById(R.id.text_zip_file_path);

        unzipPath = new MutableLiveData<>();
        unzipPath.observe(this, new Observer<String>() {
            @Override
            public void onChanged(String s) {
                String[] paths = s.split("/");
                textView.setText(paths[paths.length - 1]);
            }
        });

        recyclerView.setLayoutManager(new GridLayoutManager(this, 5));
        adapter = new FileItemAdapter(new ArrayList<>(), new ArrayList<>());
        recyclerView.setAdapter(adapter);

        DocumentProcessingFactory.init(this, (CallbackProcessingListener<List<String>>) t -> {
            adapter.updateData(t);
            Toast.makeText(UnzipActivity.this, "文件处理完毕！", Toast.LENGTH_SHORT).show();
        });

        initIntent(getIntent());

    }


    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        initIntent(intent);
    }


    /**
     * 初始化Intent
     *
     * @param intent
     */
    private void initIntent(Intent intent) {

        switch (intent.getAction()) {
            case Intent.ACTION_VIEW:
                List<String> str = PathUtil.getPaths(UnzipActivity.this, intent);//Uri.decode(uri.getEncodedPath());
                unzipPath.setValue(str.get(0));
                break;

            case Intent.ACTION_SEND:
                List<String> strs = PathUtil.getPaths(UnzipActivity.this, intent);//Uri.decode(uri.getEncodedPath());
                for (String itemPath : strs) {
                    LogInfo.e(itemPath);
                }
                break;

            case UnzipActivity.UNZIP_ACTION:
                //处理其他程序传来的文件地址
                final String path = getIntent().getStringExtra(FILE_PATH);
                if (path != null && !path.equals("")) {
                    unzipPath.setValue(path);
                    DocumentProcessingFactory.setFilePath(path);
                }
                break;

            default:
                break;
        }
    }


    public void onUnzip(View view) {
        DocumentProcessingFactory.setFilePath(unzipPath.getValue());
    }


}
