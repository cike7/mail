package com.zhuli.mail.adapter;

import android.graphics.Bitmap;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.zhuli.mail.R;
import com.zhuli.mail.mail.LogInfo;
import com.zhuli.mail.util.PathUtil;
import com.zhuli.mail.widget.FileImgView;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;


/**
 * Copyright (C) 王字旁的理
 * Date: 2021/12/21
 * Description:
 * Author: zl
 */
public class FileItemAdapter extends RecyclerView.Adapter<FileItemViewViewHolder> {

    private final ExecutorService executors = Executors.newCachedThreadPool();

    private List<String> filePaths = new ArrayList<>();
    private List<String> names;
    private List<Bitmap> bitmaps;

    public FileItemAdapter(List<String> names, List<Bitmap> bitmaps) {
        this.names = names;
        this.bitmaps = bitmaps;
    }

    @NonNull
    @Override
    public FileItemViewViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        FileImgView view = new FileImgView(parent.getContext());
        return new FileItemViewViewHolder(view);
    }

    @Override
    public void onBindViewHolder(FileItemViewViewHolder holder, int position) {
        if (names != null && names.size() > 0) {
            if (bitmaps != null && bitmaps.size() > 0) {
                holder.bindData(names.get(position), bitmaps.get(position));
            }
        }
    }

    @Override
    public int getItemCount() {
        return names.size();
    }


    /**
     * 更新文件
     *
     * @param paths
     */
    public void updateData(List<String> paths) {
        if (names != null) {
            names.clear();
        }
        if (bitmaps != null) {
            bitmaps.clear();
        }
        if (filePaths == null) {
            filePaths = new ArrayList<>();
        }
        if (paths != null) {
            for (int i = 0; i < paths.size(); i++) {
                if (!filePaths.contains(paths.get(i))) {
                    if (paths.get(i) != null && !paths.get(i).equals("") && paths.get(i).length() > 0) {
                        LogInfo.e("文件路径：" + paths.get(i));
                        filePaths.add(paths.get(i));
                    }
                } else {
                    LogInfo.e("重复选择文件" + paths.get(i));
                }
            }
        }

        executors.execute(new Runnable() {
            @Override
            public void run() {
                names = new ArrayList<>();
                bitmaps = new ArrayList<>();
                for (int i = 0; i < filePaths.size(); i++) {
                    String[] filePath = filePaths.get(i).split("/");
                    names.add(filePath[filePath.length - 1]);
                    bitmaps.add(PathUtil.getImageThumbnail(filePaths.get(i), 100, 100));
                }
                handler.sendEmptyMessage(0);
            }
        });

    }


    public List<String> getFilePaths() {
        return filePaths;
    }

    public void clear() {
        if (filePaths != null && filePaths.size() > 0) {
            filePaths.clear();
            names.clear();
            bitmaps.clear();
            notifyDataSetChanged();
        }
    }

    private final Handler handler = new Handler(Looper.getMainLooper()) {
        @Override
        public void handleMessage(@NonNull Message msg) {
            super.handleMessage(msg);
            notifyDataSetChanged();
        }
    };


}
