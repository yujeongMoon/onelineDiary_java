package com.example.onelinediary.activity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.example.onelinediary.R;
import com.example.onelinediary.adapter.ListAdapter;
import com.example.onelinediary.constant.Const;
import com.example.onelinediary.databinding.ActivitySettingBinding;
import com.example.onelinediary.dialog.ConfirmDialog;
import com.example.onelinediary.dialog.InputDialog;
import com.example.onelinediary.dto.BasicItemBtn;
import com.example.onelinediary.dto.BasicItemSwitch;
import com.example.onelinediary.dto.TextItem;
import com.example.onelinediary.utiliy.DatabaseUtility;
import com.example.onelinediary.utiliy.Utility;

public class SettingActivity extends AppCompatActivity {
    private ActivitySettingBinding settingBinding;

    private ListAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        settingBinding = ActivitySettingBinding.inflate(getLayoutInflater());
        setContentView(settingBinding.getRoot());

        settingBinding.settingRecyclerview.setLayoutManager(new LinearLayoutManager(this));

        adapter = new ListAdapter();
        adapter.addItem(new TextItem("최신 버전입니다."));

        // 닉네임을 설정했다면 "변경", 닉네임을 설정하지 않았다면 "설정"
        String buttonText = getString(R.string.setting);
        if (!Const.nickname.equals("")) {
            buttonText = getString(R.string.change);
        }
        adapter.addItem(new BasicItemBtn(R.drawable.star_24, getString(R.string.title_setting_nickname), buttonText, setNicknameListener));

        adapter.addItem(new BasicItemBtn(R.drawable.lock_black_24, getString(R.string.title_setting_security), getString(R.string.setting), setSecurity));
        adapter.addItem(new BasicItemSwitch(R.drawable.push_notification_black_24, getString(R.string.title_setting_push), false, null));
        adapter.addItem(new BasicItemBtn(R.drawable.face_black_24, getString(R.string.title_setting_feedback), getString(R.string.send), moveFeedbackActivityListener));

        settingBinding.settingRecyclerview.setAdapter(adapter);
    }

    View.OnClickListener setNicknameListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            new InputDialog(getString(R.string.dialog_message_input_nickname), v1 -> {
                if (Utility.isStringNullOrEmpty(Const.nickname)) {
                    Const.nickname = "";
                }

                // 닉네임을 DB에 저장한다.
                DatabaseUtility.setNickname(SettingActivity.this, Const.nickname, isSuccess -> {
                    if (isSuccess) {
                        // 변경된 닉네임을 기기에 저장한다.
                        // 닉네임을 불러올 때 DB에서 가져오는 시간을 줄이기 위해서 저장한다.
                        Utility.putString(getApplicationContext(), Const.SP_KEY_NICKNAME, Const.nickname);

                        BasicItemBtn itemBtn = (BasicItemBtn) adapter.items.get(1);
                        if (!Const.nickname.equals("")) {
                            Toast.makeText(getApplicationContext(), getString(R.string.message_save_nickname), Toast.LENGTH_SHORT).show();
                            itemBtn.setButtonText(getString(R.string.change));
                        } else {
                            Toast.makeText(getApplicationContext(), getString(R.string.message_reset_nickname), Toast.LENGTH_SHORT).show();
                            itemBtn.setButtonText(getString(R.string.setting));
                        }

                        adapter.updateItem(1, itemBtn);
                    } else {
                        Utility.putString(getApplicationContext(), Const.SP_KEY_NICKNAME, "");
                        Toast.makeText(getApplicationContext(), "오류가 발생하였습니다. 잠시 후에 다시 시도해주세요!", Toast.LENGTH_SHORT).show();
                    }
                });
            }).show(getSupportFragmentManager(), "setNickname");
        }
    };

    View.OnClickListener moveFeedbackActivityListener = v -> {
//        try {
//            Intent emailIntent = new Intent(Intent.ACTION_SEND);
//            emailIntent.setType("text/plain"); // 필수
//            emailIntent.putExtra(Intent.EXTRA_EMAIL, new String[] {"octo2917@gmail.com"});
//            emailIntent.putExtra(Intent.EXTRA_SUBJECT, "피드백");
//
//            // startActivity()에서 오류가 날 수 있기 때문에 예외 처리를 해줘여한다.
//            startActivity(emailIntent);
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
//
//        Intent emailIntent = new Intent(Intent.ACTION_SENDTO);
//        Uri uri = Uri.parse("mailto:" + "octo2917@gmail.com");
//        emailIntent.setData(uri);
//
//        try {
//            startActivity(emailIntent);
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
        if (Utility.getString(getApplicationContext(), Const.SP_KEY_NICKNAME).equals("")) { // 닉네임이 없을 때
            new ConfirmDialog(getString(R.string.dialog_message_notify_set_nickname), null).show(getSupportFragmentManager(), "setNicknameBeforeFeedback");
        } else {
            // 저장된 안드로이드 아이디와 관리자의 아이디가 같을 경우, 관리자 화면으로 이동한다.
            if (Utility.getString(getApplicationContext(), Const.SP_KEY_ANDROID_ID).equals(Const.ADMIN_ANDROID_ID)) {
                Intent adminIntent = new Intent(SettingActivity.this, AdminActivity.class);
                startActivity(adminIntent);
            } else {
                Intent feedbackIntent = new Intent(SettingActivity.this, FeedbackActivity.class);
                feedbackIntent.putExtra(Const.INTENT_KEY_ANDROID_ID, Utility.getAndroidId(this));
                startActivity(feedbackIntent);
            }
        }
    };

    View.OnClickListener setSecurity = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            Intent securityIntent = new Intent(SettingActivity.this, SecurityActivity.class);
            startActivity(securityIntent);
        }
    };
}