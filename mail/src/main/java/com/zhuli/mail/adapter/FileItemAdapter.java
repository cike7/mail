package com.zhuli.mail.adapter;

import android.graphics.Bitmap;
import android.os.AsyncTask;
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
import com.zhuli.mail.util.FileMime;
import com.zhuli.mail.util.LoadBitmapAsyncTask;
import com.zhuli.mail.util.PathUtil;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;


/**
 * Copyright (C) 王字旁的理
 * Date: 2021/12/21
 * Description:
 * Author: zl
 */
public class FileItemAdapter extends RecyclerView.Adapter<FileItemViewHolder> {

    private List<String> filePaths = new ArrayList<>();
    private List<String> names;
    private List<Bitmap> bitmaps;

    public FileItemAdapter(List<String> names, List<Bitmap> bitmaps) {
        this.names = names;
        this.bitmaps = bitmaps;
    }

    @NonNull
    @Override
    public FileItemViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        final int parentWidth = parent.getMeasuredWidth() / 4;
        final int parentHeight = parentWidth + (int) (parent.getResources().getDisplayMetrics().density * 18);
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_file, parent, false);
        view.setLayoutParams(new ViewGroup.LayoutParams(parentWidth, parentHeight));
        return new FileItemViewHolder(view);
    }

    @Override
    public void onBindViewHolder(FileItemViewHolder holder, int position) {
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

        for (int k = 0; k < filePaths.size(); k++) {

            String[] filePath = filePaths.get(k).split("/");
            String fileName = filePath[filePath.length - 1];
            names.add(fileName);
            if (fileName.endsWith(".jpg") || fileName.endsWith(".jpeg") || fileName.endsWith(".png")) {
                try {
                    //异步加载略缩图任务
                    LoadBitmapAsyncTask task = new LoadBitmapAsyncTask();
                    task.execute(filePaths.get(k));
                    bitmaps.add(task.get());
                } catch (ExecutionException | InterruptedException e) {
                    e.printStackTrace();
                }
            } else {
                bitmaps.add(null);
            }
        }

        notifyDataSetChanged();

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

}
