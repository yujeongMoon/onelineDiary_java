package com.example.onelinediary.activity;

import android.content.Intent;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;

import com.example.onelinediary.databinding.ActivityInputBinding;

/**
 * 다이얼로그 대신 액티비티로 만든 입력창
 * startActivityForResult()로 호출하여 결과값을 받아서 사용한다.
 * onActivityResult()에서 "inputText"에 저장된 값을 사용한다.
 */
public class InputActivity extends AppCompatActivity {
    private ActivityInputBinding inputBinding;

    public static final String INTENT_KEY_INPUT_TEXT = "inputText";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        inputBinding = ActivityInputBinding.inflate(getLayoutInflater());
        setContentView(inputBinding.getRoot());

        inputBinding.click1.setOnClickListener(v -> {
            String text = inputBinding.input.getText().toString();
            Intent intent = new Intent();
            intent.putExtra(INTENT_KEY_INPUT_TEXT, text);
            setResult(RESULT_OK, intent);
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