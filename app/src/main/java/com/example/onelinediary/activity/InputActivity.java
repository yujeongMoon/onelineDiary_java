package com.example.onelinediary.activity;

import android.content.Intent;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;

import com.example.onelinediary.constant.Const;
import com.example.onelinediary.databinding.ActivityInputBinding;

public class InputActivity extends AppCompatActivity {
    private ActivityInputBinding inputBinding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        inputBinding = ActivityInputBinding.inflate(getLayoutInflater());
        setContentView(inputBinding.getRoot());

        inputBinding.click1.setOnClickListener(v -> {
            String nickname = inputBinding.input.getText().toString();
            Const.nickname = nickname;
            Intent nicknameIntent = new Intent();
            nicknameIntent.putExtra("nickname", nickname);
            setResult(RESULT_OK, nicknameIntent);
            finish();
        });

        inputBinding.click2.setOnClickListener(v -> {
            setResult(RESULT_CANCELED);
            finish();
        });
    }

    @Override
    public void onBackPressed() { }
}