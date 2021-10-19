package com.example.onelinediary.activity;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import com.example.onelinediary.R;
import com.example.onelinediary.adapter.SettingAdapter;
import com.example.onelinediary.databinding.ActivitySettingBinding;
import com.example.onelinediary.dto.BasicItemBtn;
import com.example.onelinediary.dto.BasicItemSwitch;
import com.example.onelinediary.dto.TextItem;

public class SettingActivity extends AppCompatActivity {
    private ActivitySettingBinding settingBinding;

    private SettingAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        settingBinding = ActivitySettingBinding.inflate(getLayoutInflater());
        setContentView(settingBinding.getRoot());

        settingBinding.settingRecyclerview.setLayoutManager(new LinearLayoutManager(this));

        adapter = new SettingAdapter();
        adapter.addItem(new TextItem("최신 버전입니다."));
        // 닉네임을 설정했다면 "변경", 닉네임을 설정하지 않았다면 "설정"
        adapter.addItem(new BasicItemBtn(R.drawable.face_black_24, "닉네임 설정 및 변경", "설정", new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        }));
        adapter.addItem(new BasicItemSwitch(R.drawable.lock_black_24, "암호 설정", false));
        adapter.addItem(new BasicItemSwitch(R.drawable.push_notification_black_24, "푸시 알림 설정", false));
        adapter.addItem(new BasicItemBtn(R.drawable.star_24, "피드백 및 응원의 한마디", "보내기", new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                try {
//                    Intent emailIntent = new Intent(Intent.ACTION_SEND);
//                    emailIntent.setType("text/plain"); // 필수
//                    emailIntent.putExtra(Intent.EXTRA_EMAIL, new String[] {"octo2917@gmail.com"});
//                    emailIntent.putExtra(Intent.EXTRA_SUBJECT, "피드백");
//
//                    // startActivity()에서 오류가 날 수 있기 때문에 예외 처리를 해줘여한다.
//                    startActivity(emailIntent);
//                } catch (Exception e) {
//                    e.printStackTrace();
//                }

                Intent emailIntent = new Intent(Intent.ACTION_SENDTO);
                Uri uri = Uri.parse("mailto:" + "octo2917@gmail.com");
                emailIntent.setData(uri);

                try {
                    startActivity(emailIntent);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }));

        settingBinding.settingRecyclerview.setAdapter(adapter);
    }
}