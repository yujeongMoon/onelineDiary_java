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
import com.example.onelinediary.constant.Const;
import com.example.onelinediary.databinding.ActivitySettingBinding;
import com.example.onelinediary.dialog.InputDialog;
import com.example.onelinediary.dto.BasicItemBtn;
import com.example.onelinediary.dto.BasicItemSwitch;
import com.example.onelinediary.dto.TextItem;
import com.example.onelinediary.utiliy.DatabaseUtility;
import com.example.onelinediary.utiliy.Utility;

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
        String buttonText = "설정";
        if (!Const.nickname.equals("")) {
            buttonText = "변경";
        }
        adapter.addItem(new BasicItemBtn(R.drawable.face_black_24, "닉네임 설정 및 변경", buttonText, setNicknameListener));

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

    View.OnClickListener setNicknameListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            new InputDialog("닉네임을 입력해주세요!", v1 -> {
                if (Utility.isStringNullOrEmpty(Const.nickname)) {
                    Const.nickname = "";
                }

                // 닉네임을 DB에 저장한다.
                DatabaseUtility.setNickname(SettingActivity.this, Const.nickname, isSuccess -> {
                    if (isSuccess) {
                        // 변경된 닉네임을 기기에 저장한다.
                        // 닉네임을 불러올 때 DB에서 가져오는 시간을 줄이기 위해서 저장한다.
                        Utility.putString(SettingActivity.this, Const.SP_KEY_NICKNAME, Const.nickname);

                        if (!Const.nickname.equals("")) {
                            Toast.makeText(getApplicationContext(), "닉네임이 저장되었습니다.", Toast.LENGTH_SHORT).show();
                            adapter.updateItem(1, new BasicItemBtn(R.drawable.face_black_24, "닉네임 설정 및 변경", "변경", setNicknameListener));
                        } else {
                            Toast.makeText(getApplicationContext(), "닉네임이 초기화되었습니다.", Toast.LENGTH_SHORT).show();
                            adapter.updateItem(1, new BasicItemBtn(R.drawable.face_black_24, "닉네임 설정 및 변경", "설정", setNicknameListener));
                        }
                    } else {
                        Utility.putString(SettingActivity.this, Const.SP_KEY_NICKNAME, "");
                        Toast.makeText(getApplicationContext(), "오류가 발생하였습니다. 잠시 후에 다시 시도해주세요!", Toast.LENGTH_SHORT).show();
                    }
                });
            }).show(getSupportFragmentManager(), "setNickname");
        }
    };
}