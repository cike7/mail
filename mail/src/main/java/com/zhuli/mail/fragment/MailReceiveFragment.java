package com.zhuli.mail.fragment;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ObjectAnimator;
import android.animation.ValueAnimator;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.FrameLayout;
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
    private WebDownloadView downloadLayout;

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

        View view = inflater.inflate(R.layout.fragment_mail_receive, container, false);

        refreshableView = view.findViewById(R.id.swipe_refresh_receive_layout);
        listView = view.findViewById(R.id.list_receive_layout);
        downloadLayout = view.findViewById(R.id.frame_download_layout);
        refreshableView.setOnRefreshListener(this);
        listView.setAdapter(viewModel.getAdapter());
        listView.setOnItemClickListener(this);
        return view;
    }


    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        //        String url = "https://www.icloud.com/attachment/?u=https%3A%2F%2Fcvws.icloud-content.com%2FB%2FAXEh4BE-iF5vcfnarAVOVYqawFJbAbMhbSkX-QlQnI5_3RNbuhqfH__W%2F%24%7Bf%7D%3Fo%3DAn0TMsKeH-colLDMedGE7m1tW9IbBBUK1lhOuu3exJPB%26v%3D1%26x%3D3%26a%3DCAogCGac-_mYoh4qsMjBVyRGS0VlG8HhxyR_GBBieN6EdlQSehCJ_oLX4y8YiY7-qu0vIgEAKgkC6AMA_wj_lI1SBJrAUltaBJ8f_9ZqJwBflRqI5OmKiLVSDlPLD7kYfIBsbQtsIn5H0boh-sqGqdpBP5HE7nInJRJvLTpu7kz-AyLAmm92qBRdQEYOm52IKJzGP5ESetg1pmCxv1De%26e%3D1644257314%26fl%3D%26r%3D54EE7538-5C59-4A70-9607-A0ED6886D5C1-1%26k%3D%24%7Buk%7D%26ckc%3Dcom.apple.largeattachment%26ckz%3D61315739-26D5-4268-B154-0C05337B07E3%26p%3D52%26s%3DSbnprN4aOkxJN7fWAkggRcOPv8o&uk=DbCf7HWscHzCy5P9S0RfyA&f=OPPO-GOlf-EN-APK-1.0-202201071407.apk&sz=25405521";
        String url = "https://qiye.aliyun.com/alimail/openLinks/downloadMimeMetaDiskBigAttach?id=netdiskid%3Av001%3Afile%3ADzzzzzzNqYC%3BOnqg0AtYuYaXRvNTRJ5F2ho3XRdl6eIIZdro5pRpVnX4YDQizm%2BbN65EXLWK5jJfJ9wqVGjl21r%2Fzxp6zyXztV3Plpx6%2FykcCJB81hXuSMJdpGXGNd46Mg%3D%3D ";

        downloadLayout.open(url);
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