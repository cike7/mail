package com.zhuli.mail.fragment;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.zhuli.mail.MailEntrustManage;
import com.zhuli.mail.R;
import com.zhuli.mail.mail.LogInfo;
import com.zhuli.mail.model.FragmentSendViewModel;
import com.zhuli.mail.util.IntentUtil;
import com.zhuli.mail.util.PathUtil;

import java.util.ArrayList;
import java.util.List;


public class MailSendFragment extends Fragment implements View.OnClickListener {

    private EditText editRecipient;
    private Spinner spinnerContact;
    private RecyclerView recyclerView;

    private ActivityResultLauncher<Intent> resultLauncher;

    private ArrayAdapter<String> spinnerAdapter;

    private FragmentSendViewModel viewModel;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        viewModel = new ViewModelProvider(this).get(FragmentSendViewModel.class);

        resultLauncher = registerForActivityResult(new ActivityResultContracts.StartActivityForResult(), new ActivityResultCallback<ActivityResult>() {
            @Override
            public void onActivityResult(ActivityResult result) {
                if (result.getResultCode() == Activity.RESULT_OK) {
                    if (result.getData() != null) {
                        viewModel.getAdapter().updateData(PathUtil.getPaths(getContext(), result.getData()));
                    }
                }
            }
        });

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View root = inflater.inflate(R.layout.fragment_mail_send, container, false);

        editRecipient = root.findViewById(R.id.edit_receive_recipient_view);
        spinnerContact = root.findViewById(R.id.match_parent_contact_view);

        //发送
        root.findViewById(R.id.but_send_mail).setOnClickListener(this);

        //添加文件
        root.findViewById(R.id.but_get_file).setOnClickListener(this);

        //压缩文件
        root.findViewById(R.id.but_compression_file).setOnClickListener(this);

        String[] items = new String[]{"aaaaaaaa", "bbb", "ccc", "zhuli@toprand.com"};
        spinnerAdapter = new ArrayAdapter<>(root.getContext(), android.R.layout.simple_spinner_item, items);

        spinnerContact.setAdapter(spinnerAdapter);
        spinnerContact.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                editRecipient.setText(parent.getAdapter().getItem(position).toString());
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }

        });

        recyclerView = root.findViewById(R.id.recycler_files_layout);

        recyclerView.setLayoutManager(new GridLayoutManager(getContext(), 4));

        LogInfo.e("widthPixels : " + root.getResources().getDisplayMetrics().widthPixels / 210);

        recyclerView.setAdapter(viewModel.getAdapter());

        return root;
    }


    @Override
    public void onClick(View v) {

        if (v.getId() == R.id.but_send_mail) { //发送

            MailEntrustManage.sendFileMail(getContext(), editRecipient.getText().toString(), viewModel.getAdapter().getFilePaths());
            viewModel.getAdapter().clear();

        } else if (v.getId() == R.id.but_get_file) { //打开文件选择器

            resultLauncher.launch(Intent.createChooser(IntentUtil.getFileIntent(), "请选择文件："));

        } else if (v.getId() == R.id.but_compression_file) {

            compressionFile();

        }

    }


    /**
     * 压缩文件
     */
    private void compressionFile() {

        final Button butSend = getView().findViewById(R.id.but_send_mail);
        if (butSend != null) {
            butSend.setClickable(false);
            butSend.setAlpha(0.4f);
        }

        MailEntrustManage.compressionFile(getContext(), viewModel.getAdapter().getFilePaths(), zipPath -> {
            if (zipPath != null) {
                List<String> paths = new ArrayList<>();
                paths.add(zipPath);
                viewModel.getAdapter().clear();
                viewModel.getAdapter().updateData(paths);
                Toast.makeText(getContext(), "压缩完成！", Toast.LENGTH_SHORT).show();
                if (butSend != null) {
                    butSend.setClickable(true);
                    butSend.setAlpha(1f);
                }
            } else {
                Toast.makeText(getContext(), "压缩失败，请检查文件路径是否正确！", Toast.LENGTH_SHORT).show();
            }
        });
    }

}