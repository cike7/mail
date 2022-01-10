package com.zhuli.mail.fragment;

import android.app.AlertDialog;
import android.app.DownloadManager;
import android.content.IntentFilter;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.PopupWindow;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.zhuli.mail.R;
import com.zhuli.mail.mail.LogInfo;
import com.zhuli.mail.mail.MailUtil;
import com.zhuli.mail.model.FragmentReceiveViewModel;
import com.zhuli.mail.util.StringUtil;
import com.zhuli.mail.view.SwipeRefreshLayout;
import com.zhuli.mail.view.WebDownloadView;


public class MailReceiveFragment extends Fragment implements SwipeRefreshLayout.OnRefreshListener, AdapterView.OnItemClickListener {

    private Handler handler;
    private SwipeRefreshLayout refreshableView;
    private ListView listView;
    private FragmentReceiveViewModel viewModel;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        viewModel = new ViewModelProvider(this).get(FragmentReceiveViewModel.class);

        handler = new Handler(Looper.getMainLooper()) {
            @Override
            public void handleMessage(@NonNull Message msg) {
                super.handleMessage(msg);
                if (refreshableView != null) {
                    refreshableView.setRefreshing(false);
                }
            }
        };

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_mail_receive, container, false);
        refreshableView = root.findViewById(R.id.swipe_refresh_receive_layout);
        listView = root.findViewById(R.id.list_receive_layout);

        refreshableView.setOnRefreshListener(this);

        listView.setAdapter(viewModel.getAdapter());
        listView.setOnItemClickListener(this);
        return root;
    }


    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        WebDownloadView downloadView = new WebDownloadView(getContext());
        //相对某个控件的位置，有偏移;xoff表示x轴的偏移，正值表示向左，负值表示向右；yoff表示相对y轴的偏移，正值是向下，负值是向上；
        downloadView.showAsDropDown(refreshableView, 0, 500);
        downloadView.showAtLocation(refreshableView, Gravity.BOTTOM, 0, 200);
        downloadView.setAnimationStyle(R.style.PictureThemeWindowStyle);

        //TODO 弹窗上移
    }


    @Override
    public void onRefresh() {

        MailUtil.receive(t -> {
            LogInfo.e("刷新完成！");
            LogInfo.e(">>>>>" + StringUtil.getSegment(t));
            LogInfo.e(">>>>>" + StringUtil.getFileName(t));
            LogInfo.e(">>>>>" + StringUtil.getUrl(t));
            handler.sendEmptyMessageAtTime(200, 500);
        });

    }

}