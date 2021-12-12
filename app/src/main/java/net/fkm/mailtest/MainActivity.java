package net.fkm.mailtest;

import android.content.Intent;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.MutableLiveData;

import com.zhuli.mail.MailActivity;

import java.util.List;


public class MainActivity extends AppCompatActivity {

    private MutableLiveData<List<String>> paths = new MutableLiveData<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        startActivity(new Intent(this, MailActivity.class));

    }


}
