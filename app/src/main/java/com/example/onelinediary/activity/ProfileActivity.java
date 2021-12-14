package com.example.onelinediary.activity;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.widget.LinearLayout;

import com.example.onelinediary.R;
import com.example.onelinediary.adapter.GridViewImageItemAdapter;
import com.example.onelinediary.constant.Const;
import com.example.onelinediary.databinding.ActivityProfileBinding;
import com.example.onelinediary.dialog.ConfirmDialog;
import com.example.onelinediary.dialog.YesNoDialog;
import com.example.onelinediary.dto.Profile;
import com.example.onelinediary.utiliy.DatabaseUtility;
import com.example.onelinediary.utiliy.Utility;

import java.util.ArrayList;

public class ProfileActivity extends AppCompatActivity {
    private ActivityProfileBinding profileBinding;

    private GridViewImageItemAdapter imageItemAdapter;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        profileBinding = ActivityProfileBinding.inflate(getLayoutInflater());
        setContentView(profileBinding.getRoot());

        int arrayId = R.array.profile_arrays;
        switch (Utility.getAndroidId(this)) {
            case Const.ADMIN_ANDROID_ID:
                arrayId = R.array.admin_profile_arrays;
                break;

            case Const.VVIP_ANDROID_ID:
                arrayId = R.array.vvip_profile_arrays;
                break;
        }

        String[] arr = getResources().getStringArray(arrayId);

        ArrayList<Profile> profileList = new ArrayList<>();
        for (String profile : arr) {
            // 레이아웃의 초기값을 null로 설정하여 앱이 죽는 현상이 발생
            // 새로운 레이아웃을 초기값으로 설정
            profileList.add(new Profile(profile, false, new LinearLayout(this)));
        }

        imageItemAdapter = new GridViewImageItemAdapter(profileList);
        profileBinding.profileGridview.setAdapter(imageItemAdapter);
    }

    public void setProfile(View view) {
        if (imageItemAdapter.getSelectedProfile() == null) {
            new ConfirmDialog(getString(R.string.dialog_message_input_profile_image), null).show(this);
        } else {
            if (Utility.getString(getApplicationContext(), Const.SP_KEY_PROFILE).equals(imageItemAdapter.getSelectedProfile().getImageResName())) {
                setResult(RESULT_CANCELED);
                finish();
            } else {
                DatabaseUtility.setProfileImage(this, imageItemAdapter.getSelectedProfile().getImageResName(), isSuccess -> {
                    if (isSuccess) {
                        new ConfirmDialog(getString(R.string.dialog_message_set_profile_image), v -> {
                            Utility.putString(getApplicationContext(), Const.SP_KEY_PROFILE, imageItemAdapter.getSelectedProfile().getImageResName());
                            setResult(RESULT_OK);
                            finish();
                        }).show(this);
                    }
                });
            }
        }
    }

    @Override
    public void onBackPressed() {
        if (imageItemAdapter.getSelectedProfile() == null) {
            super.onBackPressed();
        } else {
            new YesNoDialog(getString(R.string.dialog_message_exit_profile), v -> finish()).show(this);
        }
    }
}