package com.zhuli.mail.model;

import androidx.lifecycle.MediatorLiveData;
import androidx.lifecycle.ViewModel;

import com.zhuli.mail.adapter.FileItemAdapter;

import java.util.ArrayList;

/**
 * Copyright (C), 2003-2021, 深圳市图派科技有限公司
 * Date: 2022/1/6
 * Description:
 * Author: zl
 */
public class FragmentSendViewModel extends ViewModel {

    /**
     * 文件图片
     */
    private FileItemAdapter adapter;

    /**
     * 自动删除压缩文件
     */
    private MediatorLiveData<Boolean> autoDeleteZip;

    public FragmentSendViewModel() {
        adapter = new FileItemAdapter(new ArrayList<>(), new ArrayList<>());
        autoDeleteZip = new MediatorLiveData<>();
    }

    public FileItemAdapter getAdapter() {
        return adapter;
    }

    public MediatorLiveData<Boolean> getAutoDeleteZip() {
        return autoDeleteZip;
    }

    public void setAutoDeleteZip(boolean autoDeleteZip) {
        this.autoDeleteZip.setValue(autoDeleteZip);
    }
}
