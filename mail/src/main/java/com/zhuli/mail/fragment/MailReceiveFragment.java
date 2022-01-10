package com.zhuli.mail.fragment;

import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.zhuli.mail.R;
import com.zhuli.mail.file.CallbackProcessingListener;
import com.zhuli.mail.mail.LogInfo;
import com.zhuli.mail.mail.MailUtil;
import com.zhuli.mail.model.FragmentReceiveViewModel;
import com.zhuli.mail.util.StringUtil;
import com.zhuli.mail.view.SwipeRefreshLayout;
import com.zhuli.mail.view.WebDownloadView;

import java.io.IOException;

import jakarta.mail.Address;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMultipart;


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

        MailUtil.receive(new CallbackProcessingListener<jakarta.mail.Message[]>() {
            @Override
            public void onComplete(jakarta.mail.Message[] msg) {
                try {

                    LogInfo.e("刷新完成！");

                    for (int i = 0; i < msg.length; i++) {
                        LogInfo.e("--------------" + i + "--------------");

                        LogInfo.e("getFolderv：" + msg[i].getFolder());
                        LogInfo.e("getSubject：" + msg[i].getSubject());
                        LogInfo.e("getContentType：" + msg[i].getContentType());
                        LogInfo.e("getFolder：" + msg[i].getFolder().getName());
                        for (Address add : msg[i].getFrom()) {
                            LogInfo.e("getFrom：" + add);
                        }
                        LogInfo.e("getDescription：" + msg[i].getDescription());
                        LogInfo.e("getDisposition：" + msg[i].getDisposition());
                        LogInfo.e("getFileName：" + msg[i].getFileName());

                        for (Address add : msg[i].getAllRecipients()) {
                            LogInfo.e("getAllRecipients：" + add);
                        }
                        LogInfo.e("getMessageNumber：" + msg[i].getMessageNumber());
                        LogInfo.e("getReceivedDate：" + msg[i].getReceivedDate());
                        LogInfo.e("getSentDate：" + msg[i].getSentDate());
                        LogInfo.e("getSession：" + msg[i].getSession().toString());
                        LogInfo.e("getLineCount：" + msg[i].getLineCount());
                        LogInfo.e("getSize：" + msg[i].getSize());
                        LogInfo.e("收件人：" + msg[i].getRecipients(jakarta.mail.Message.RecipientType.TO));
                        LogInfo.e("抄送人：" + msg[i].getRecipients(jakarta.mail.Message.RecipientType.CC));
                        LogInfo.e("密送人：" + msg[i].getRecipients(jakarta.mail.Message.RecipientType.BCC));

                        MimeMultipart mimeMultipart = (MimeMultipart) msg[i].getContent();

                        LogInfo.e("1内容：" + mimeMultipart.getBodyPart(0).getContent());

                        LogInfo.e("url : " + StringUtil.getUrl(mimeMultipart.getBodyPart(0).getContent().toString()));

//                for (int k = 0; k < mimeMultipart.getCount(); k++) {
//                    BodyPart part = mimeMultipart.getBodyPart(k);
//                    LogInfo.e("2内容：" + part.getContent());
//                }

                        LogInfo.e(">>>>>" + StringUtil.getSegment(mimeMultipart.getBodyPart(0).getContent().toString()));
                        LogInfo.e(">>>>>" + StringUtil.getFileName(mimeMultipart.getBodyPart(0).getContent().toString()));
                        LogInfo.e(">>>>>" + StringUtil.getUrl(mimeMultipart.getBodyPart(0).getContent().toString()));
                    }

                } catch (MessagingException | IOException e) {
                    Toast.makeText(getContext(), "接收邮箱错误:" + e.getMessage(), Toast.LENGTH_SHORT).show();
                }

                handler.sendEmptyMessageAtTime(200, 500);

            }
        });

    }

}