package com.example.onelinediary.activity;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.os.Bundle;
import android.os.VibrationEffect;
import android.os.Vibrator;
import android.text.TextUtils;
import android.view.View;
import android.widget.ImageView;

import com.example.onelinediary.R;
import com.example.onelinediary.adapter.PinAdapter;
import com.example.onelinediary.databinding.ActivityPinBinding;
import com.example.onelinediary.dialog.ConfirmDialog;
import com.example.onelinediary.dialog.SelectDialog;
import com.example.onelinediary.dto.PinInfo;
import com.example.onelinediary.utiliy.DatabaseUtility;
import com.example.onelinediary.utiliy.Utility;

public class PinActivity extends AppCompatActivity {
    private ActivityPinBinding pinBinding;

    private PinAdapter adapter;
    private boolean isLogin;

    String firstPassword;
    StringBuilder stringBuilder = new StringBuilder();

    Vibrator vibrator;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        pinBinding = ActivityPinBinding.inflate(getLayoutInflater());
        setContentView(pinBinding.getRoot());

        isLogin = getIntent().getBooleanExtra("isLogin", false);

        if (isLogin) {
            pinBinding.message.setText("pin 번호를 입력하세요!");
        }

        vibrator = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);

        adapter = new PinAdapter(new PinAdapter.pinClickListener() {
            @Override
            public void onPinClick(String tag) {
                // 1초 진동
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                    vibrator.vibrate(VibrationEffect.createOneShot(100, 50));
                } else {
                    vibrator.vibrate(100);
                }

                if (tag.equals("reset")) {
                    resetPinNumber();
                } else if (tag.equals("cancel")) {
                    if (stringBuilder.length() > 0) {
                        stringBuilder.deleteCharAt(stringBuilder.length() - 1);
                        setPinImage(stringBuilder.length(), false, R.drawable.circle_gray);
                    }
                } else { // 숫자
                    if (stringBuilder.length() < 4) {
                        stringBuilder.append(tag);
                        setPinImage(stringBuilder.length() - 1, false, R.drawable.circle_pink);
                    }

                    if (stringBuilder.length() == 4) {
                        if (isLogin) {
                            if (Utility.getString(getApplicationContext(), "pinNumber").equals(stringBuilder.toString())) {
                                Intent mainIntent = new Intent(PinActivity.this, MainActivity.class);
                                startActivity(mainIntent);
                                finish();
                            } else {
                                new ConfirmDialog("비밀번호가 일치하지 않습니다.", new View.OnClickListener() {
                                    @Override
                                    public void onClick(View v) {
                                        resetPinNumber();
                                    }
                                }).show(getSupportFragmentManager(), "resetPinNumber");
                            }
                        } else {
                            if (TextUtils.isEmpty(firstPassword)) {
                                firstPassword = stringBuilder.toString();
                                pinBinding.message.setText("다시 한번 입력해주세요!");
                                resetPinNumber();
                            } else {
                                if (firstPassword.equals(stringBuilder.toString())) {
                                    DatabaseUtility.setPinNumber(PinActivity.this, new PinInfo("Y", stringBuilder.toString()), new DatabaseUtility.onCompleteCallback() {
                                        @Override
                                        public void onComplete(boolean isSuccess) {
                                            new ConfirmDialog("암호 설정이 완료되었습니다.", new View.OnClickListener() {
                                                @Override
                                                public void onClick(View v) {
                                                    Utility.putString(getApplicationContext(), "setPinNumber", "Y");
                                                    Utility.putString(getApplicationContext(), "pinNumber", stringBuilder.toString());

                                                    setResult(RESULT_OK);
                                                    finish();
                                                }
                                            }).show(getSupportFragmentManager(), "setPinNumber");
                                        }
                                    });
                                } else {
                                    new ConfirmDialog("비밀번호가 일치하지 않습니다.", new View.OnClickListener() {
                                        @Override
                                        public void onClick(View v) {
                                            resetPinNumber();
                                        }
                                    }).show(getSupportFragmentManager(), "resetPinNumber");
                                }
                            }
                        }
                    }
                }
            }
        });
        pinBinding.pinNumbers.setAdapter(adapter);
    }

    @Override
    public void onBackPressed() {
        String message;
        if (isLogin) {
            message = "앱을 종료하시겠습니까?";
        } else {
            message = "Pin 설정을 종료하시겠습니까?";
        }

        setResult(RESULT_CANCELED);

        new SelectDialog(message, v -> finish()).show(getSupportFragmentManager(), "cancelSetPin");
    }

    private void resetPinNumber() {
        stringBuilder = new StringBuilder();
        setPinImage(0, true, R.drawable.circle_gray);
    }

    private void setPinImage(int resetIndex, boolean allReset, int drawable) {
        int childCount = pinBinding.layoutPinImage.getChildCount();
        if(allReset) {
            for(int i = 0; i < childCount; i++) {
                if(pinBinding.layoutPinImage.getChildAt(i) instanceof ImageView) {
                    ImageView resetPinImg = (ImageView)pinBinding.layoutPinImage.getChildAt(i);
                    resetPinImg.setImageResource(drawable);
                }
            }
        } else  {
            if(pinBinding.layoutPinImage.getChildAt(resetIndex) instanceof ImageView) {
                ImageView resetPinImg = (ImageView)pinBinding.layoutPinImage.getChildAt(resetIndex);
                if(String.valueOf(resetIndex).equals((String)resetPinImg.getTag())) {
                    resetPinImg.setImageResource(drawable);
                }
            }
        }
    }
}