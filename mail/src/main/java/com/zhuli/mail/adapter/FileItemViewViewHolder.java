package com.zhuli.mail.adapter;

import android.graphics.Bitmap;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.zhuli.mail.R;
import com.zhuli.mail.widget.FileImgView;

/**
 * Copyright (C) 王字旁的理
 * Date: 2021/12/21
 * Description:
 * Author: zl
 */
public class FileItemViewViewHolder extends RecyclerView.ViewHolder {

    private FileImgView view;

    public FileItemViewViewHolder(@NonNull FileImgView itemView) {
        super(itemView);
        view = itemView;
    }

    public void bindData(String fileName, Bitmap bitmap) {
        view.getTextView().setText(fileName);
        if (bitmap != null) {
            view.getImageView().setImageBitmap(bitmap);
        } else {
            view.setImageAndSuffixName(R.mipmap.ic_drive_file, fileName.split("\\.")[1]);
        }
    }

}
