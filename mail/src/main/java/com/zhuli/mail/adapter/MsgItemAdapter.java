package com.zhuli.mail.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;

import androidx.annotation.LayoutRes;

import com.zhuli.mail.mail.ICallback;

import java.util.List;


/**
 * Copyright (C) 王字旁的理
 * Date: 2021/12/21
 * Description: 消息
 * Author: zl
 */
public class MsgItemAdapter<T> extends BaseAdapter {

    private final @LayoutRes
    int layoutId;

    private List<T> message;

    private ICallback<BindingView> callback;

    public MsgItemAdapter(@LayoutRes int layoutId, List<T> msg, ICallback<BindingView> callback) {
        this.layoutId = layoutId;
        this.message = msg;
        this.callback = callback;
    }

    @Override
    public int getCount() {
        return message.size();
    }

    @Override
    public Object getItem(int position) {
        return message.get(position);
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if (convertView == null) {
            convertView = LayoutInflater.from(parent.getContext()).inflate(layoutId, parent, false);
        }
        callback.onCall(new BindingView(message.get(position), convertView));
        return convertView;
    }


    public class BindingView {

        private T data;

        private View view;

        public BindingView(T data, View view) {
            this.data = data;
            this.view = view;
        }

        public View getView() {
            return view;
        }

        public T getData() {
            return data;
        }
    }


}
