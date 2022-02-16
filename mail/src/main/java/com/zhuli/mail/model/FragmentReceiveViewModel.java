package com.zhuli.mail.model;

import android.graphics.Color;
import android.widget.TextView;

import androidx.lifecycle.ViewModel;

import com.zhuli.mail.adapter.MsgItemAdapter;
import com.zhuli.mail.mail.ICallback;

import java.util.ArrayList;
import java.util.List;

/**
 * Copyright (C), 2003-2021, 深圳市图派科技有限公司
 * Date: 2022/1/6
 * Description:
 * Author: zl
 */
public class FragmentReceiveViewModel extends ViewModel {

    private MsgItemAdapter<String> adapter;

    public FragmentReceiveViewModel() {

        List<String> items = new ArrayList<>();

        adapter = new MsgItemAdapter<String>(android.R.layout.test_list_item, items, new ICallback<MsgItemAdapter<String>.BindingView>() {
            @Override
            public void onCall(MsgItemAdapter<String>.BindingView bind) {
                TextView view = (TextView) bind.getView();
                view.setTextSize(22);
                view.setTextColor(Color.BLACK);
                view.setPadding(50, 10, 0, 10);
                view.setText(bind.getData());
            }
        });

    }


    public MsgItemAdapter<String> getAdapter() {
        return adapter;
    }

}
