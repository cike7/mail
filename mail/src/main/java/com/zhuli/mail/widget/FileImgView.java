package com.zhuli.mail.widget;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Rect;
import android.media.ThumbnailUtils;
import android.util.AttributeSet;
import android.view.Gravity;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.DrawableRes;
import androidx.annotation.Nullable;

/**
 * Copyright (C), 2003-2021, 深圳市图派科技有限公司
 * Date: 2022/1/4
 * Description: 文件view
 * Author: zl
 */
public class FileImgView extends LinearLayout {

    private ImageView imageView;
    private TextView textView;

    public FileImgView(Context context) {
        this(context, null, 0);
    }

    public FileImgView(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public FileImgView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);

        setOrientation(LinearLayout.VERTICAL);
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(120, 120);
        params.gravity = Gravity.CENTER;
        imageView = new ImageView(getContext());
        imageView.setLayoutParams(params);
        addView(imageView);

        textView = new TextView(getContext());
        textView.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));
        textView.setGravity(Gravity.CENTER);
        textView.setTextColor(Color.BLACK);
        textView.setTextSize(10);
        textView.setMaxLines(1);
        addView(textView);
    }

    public ImageView getImageView() {
        return imageView;
    }

    public TextView getTextView() {
        return textView;
    }

    public void setImageAndSuffixName(@DrawableRes int gResId, String gText) {
        Resources resources = getResources();
        Bitmap bitmap = BitmapFactory.decodeResource(resources, gResId);
        bitmap = ThumbnailUtils.extractThumbnail(bitmap, 100, 100, ThumbnailUtils.OPTIONS_RECYCLE_INPUT);//bitmap.copy(Bitmap.Config.ARGB_8888, true);
        Canvas canvas = new Canvas(bitmap);
        textView.getPaint().setDither(true); //获取跟清晰的图像采样
        textView.getPaint().setFilterBitmap(true);//过滤一些
        Rect bounds = new Rect();
        textView.getPaint().getTextBounds(gText, 0, gText.length(), bounds);
        canvas.drawText(gText, (bitmap.getWidth() - bounds.width()) * 0.5f, bitmap.getHeight() * 0.6f, textView.getPaint());
        imageView.setImageBitmap(bitmap);
        requestLayout();
    }

}
