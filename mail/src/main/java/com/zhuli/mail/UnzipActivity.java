package com.zhuli.mail;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.Observer;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.zhuli.mail.adapter.FileItemAdapter;
import com.zhuli.mail.file.DocumentProcessingFactory;
import com.zhuli.mail.file.FileProcessingCompleteListener;
import com.zhuli.mail.unit.PathUnit;

import java.util.ArrayList;
import java.util.List;


/**
 * 解压文件
 */
public class UnzipActivity extends AppCompatActivity {

    public final String FILE_PATH = "FILE_PATH";

    private TextView textView;
    private MutableLiveData<String> unzipPath;
    private RecyclerView linearLayout;
    private FileItemAdapter adapter;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_unzip);

        linearLayout = findViewById(R.id.recycler_files_layout);
        textView = findViewById(R.id.text_zip_file_path);

        unzipPath = new MutableLiveData<>();
        unzipPath.observe(this, new Observer<String>() {
            @Override
            public void onChanged(String s) {
                String[] paths = s.split("/");
                textView.setText(paths[paths.length - 1]);
            }
        });

        linearLayout.setLayoutManager(new GridLayoutManager(this, 4));
        adapter = new FileItemAdapter(new ArrayList<>(), new ArrayList<>());
        linearLayout.setAdapter(adapter);

        Intent intent = getIntent();
        String action = intent.getAction();
        if (Intent.ACTION_VIEW.equals(action)) {
            List<String> str = PathUnit.getPaths(UnzipActivity.this, intent);//Uri.decode(uri.getEncodedPath());
            unzipPath.setValue(str.get(0));
        }

        DocumentProcessingFactory.init(this, new FileProcessingCompleteListener() {
            @Override
            public <T> void onComplete(T t) {
                if (t instanceof List) {
                    adapter.updateData((List<String>) t);
                    Toast.makeText(UnzipActivity.this, "文件处理完毕！", Toast.LENGTH_SHORT).show();
                }
            }
        });

        //处理其他程序传来的文件地址
        final String path = getIntent().getStringExtra(FILE_PATH);
        if (path != null && !path.equals("")) {
            unzipPath.setValue(path);
            DocumentProcessingFactory.setFilePath(path);
        }
    }


    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
    }

    public void onUnzip(View view) {
        DocumentProcessingFactory.setFilePath(unzipPath.getValue());
    }


}
