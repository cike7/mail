package com.zhuli.mail;

import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentPagerAdapter;
import androidx.viewpager.widget.ViewPager;

import com.zhuli.mail.adapter.FragmentsAdapter;
import com.zhuli.mail.fragment.MailReceiveFragment;
import com.zhuli.mail.fragment.MailSendFragment;
import com.zhuli.mail.util.NetworkDiagnosisUtil;
import com.zhuli.mail.util.PermissionUtil;

import java.util.ArrayList;
import java.util.List;


public class MailActivity extends AppCompatActivity {

    private ViewPager viewPager;
    private FragmentsAdapter adapter;

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_mail);

        NetworkDiagnosisUtil.init(this);
        PermissionUtil.verifyStoragePermissions(this);

        viewPager = findViewById(R.id.page_mail_view);

        List<Fragment> fragments = new ArrayList<>();
//        fragments.add(new MailReceiveFragment());
        fragments.add(new MailSendFragment());

        adapter = new FragmentsAdapter(getSupportFragmentManager(), FragmentPagerAdapter.BEHAVIOR_RESUME_ONLY_CURRENT_FRAGMENT, fragments);
        viewPager.setAdapter(adapter);
        viewPager.setCurrentItem(0);

    }


    /**
     * 申请权限结果返回处理
     */
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        PermissionUtil.confirmPermissions(this, requestCode, permissions, grantResults);
    }

}
