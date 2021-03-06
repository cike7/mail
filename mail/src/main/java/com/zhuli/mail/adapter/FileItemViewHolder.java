package com.zhuli.mail.adapter;

import android.graphics.Bitmap;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.zhuli.mail.R;
import com.zhuli.mail.mail.LogInfo;

/**
 * Copyright (C) 王字旁的理
 * Date: 2021/12/21
 * Description:
 * Author: zl
 */
public class FileItemViewHolder extends RecyclerView.ViewHolder {

    private TextView textView;
    private ImageView imageView;

    public FileItemViewHolder(@NonNull View itemView) {
        super(itemView);
        textView = itemView.findViewById(R.id.text_file_item_view);
        imageView = itemView.findViewById(R.id.img_file_item_view);
    }

    public void bindData(String fileName, Bitmap bitmap) {
        textView.setText(fileName);
        if (bitmap != null) {
            imageView.setImageBitmap(bitmap);
        } else {
            imageView.setImageResource(R.mipmap.ic_drive_file);
        }
    }

}
