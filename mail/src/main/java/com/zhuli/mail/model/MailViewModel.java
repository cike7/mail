package com.zhuli.mail.model;
import androidx.lifecycle.ViewModel;

import com.zhuli.mail.adapter.FileItemAdapter;

import java.util.ArrayList;

/**
 * Copyright (C), 2003-2021, 深圳市图派科技有限公司
 * Date: 2022/1/6
 * Description:
 * Author: zl
 */
public class MailViewModel extends ViewModel {

    private FileItemAdapter adapter;

    public MailViewModel() {
        adapter = new FileItemAdapter(new ArrayList<>(), new ArrayList<>());
    }

    public FileItemAdapter getAdapter() {
        return adapter;
    }
}
