package com.example.onelinediary.activity;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.os.VibrationEffect;
import android.os.Vibrator;
import android.text.TextUtils;
import android.view.View;
import android.widget.ImageView;

import com.example.onelinediary.R;
import com.example.onelinediary.adapter.PinAdapter;
import com.example.onelinediary.constant.Const;
import com.example.onelinediary.databinding.ActivityPinBinding;
import com.example.onelinediary.dialog.ConfirmDialog;
import com.example.onelinediary.dialog.YesNoDialog;
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
    Intent mainIntent;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        pinBinding = ActivityPinBinding.inflate(getLayoutInflater());
        setContentView(pinBinding.getRoot());

        mainIntent = new Intent();
        if(getIntent() != null) {
            mainIntent = getIntent();
        }
        mainIntent.setClass(this, MainActivity.class);

        isLogin = getIntent().getBooleanExtra(Const.INTENT_KEY_IS_LOGIN, false);

        if (isLogin) {
            pinBinding.message.setText(R.string.message_login_pin);
        }

        vibrator = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);

        adapter = new PinAdapter(new PinAdapter.pinClickListener() {
            @Override
            public void onPinClick(String tag) {
                // 버튼을 클릭할 때마다 미세하게 진동을 준다.(햅틱)
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                    vibrator.vibrate(VibrationEffect.createOneShot(100, 50));
                } else {
                    vibrator.vibrate(100);
                }

                if (tag.equals("x")) { // 리셋하는 경우
                    resetPinNumber();
                } else if (tag.equals("<")) { // 한 자리씩 지우는 경우
                    // 하나도 입력하지 않았을 경우에는 지우기 버튼이 동작하지 않는다.
                    // 하나라도 입력했다면 stringBuilder에 마지막에 저장된 값을 지우고
                    // stringBuilder의 length를 넘겨서 해당 위치의 pin 이미지를 회색 원으로 바꿔준다.
                    if (stringBuilder.length() > 0) {
                        // 마지막 인덱스의 번호를 지운다.
                        stringBuilder.deleteCharAt(stringBuilder.length() - 1);
                        // 위에서 마지막 인덱스를 지웠기 때문에 stringBuilder의 length를 넘겨준다.
                        setPinImage(stringBuilder.length(), false, R.drawable.circle_gray);
                    }
                } else { // 숫자
                    // 4개의 숫자가 채워지기 전까지는 stringBuilder에 pin 번호를 저장한다.
                    if (stringBuilder.length() < 4) {
                        // 넘겨받은 태그 값을 저장한다.
                        stringBuilder.append(tag);
                        // 추가된 값의 인덱스를 넘겨 분홍색 원으로 바꿔준다.
                        setPinImage(stringBuilder.length() - 1, false, R.drawable.circle_pink);
                    }

                    if (stringBuilder.length() == 4) {
                        if (isLogin) {
                            // 로그인의 경우, 설정한 pin 번호와 일치하면 메인 화면으로 이동 시켜준다.
                            if (Utility.getString(getApplicationContext(), Const.SP_KEY_PIN_NUMBER).equals(stringBuilder.toString())) {
                                startActivity(mainIntent);
                                finish();
                            } else {
                                // 일치하지 않을 경우, 암호가 틀렸다고 다이얼로그를 띄워 알려준다.
                                new ConfirmDialog(getString(R.string.dialog_message_no_match_password), new View.OnClickListener() {
                                    @Override
                                    public void onClick(View v) {
                                        resetPinNumber();
                                    }
                                }).show(PinActivity.this);
                            }
                        } else {
                            // 암호 설정일 경우, 2번의 입력을 받아 설정한다.
                            // 처음 입력한 pin 번호는 firstPassword 변수에 저장하고 확인 차 다시 한 번 입력을 받는다.
                            // 두번째 입력한 pin 번호는 firstPassword와 비교하여 일치할 경우, 암호 설정을 완료하고 틀릴 경우 다시 입력을 받는다.
                            if (TextUtils.isEmpty(firstPassword)) {
                                firstPassword = stringBuilder.toString();
                                pinBinding.message.setText(R.string.message_input_one_more);
                                resetPinNumber();
                            } else {
                                if (firstPassword.equals(stringBuilder.toString())) {
                                    DatabaseUtility.setPinNumber(PinActivity.this, new PinInfo("Y", stringBuilder.toString()), new DatabaseUtility.onCompleteCallback() {
                                        @Override
                                        public void onComplete(boolean isSuccess) {
                                            new ConfirmDialog(getString(R.string.dialog_message_lock_pin), new View.OnClickListener() {
                                                @Override
                                                public void onClick(View v) {
                                                    Utility.putString(getApplicationContext(), Const.SP_KEY_SET_PIN_NUMBER, "Y");
                                                    Utility.putString(getApplicationContext(), Const.SP_KEY_PIN_NUMBER, stringBuilder.toString());


                                                    // setResult로 ok값을 설정하면 이 화면이 종료되고
                                                    // 이 화면을 호출한 전 화면으로 돌아갔을 때 onActivityResult가 호출되고
                                                    // 그 메소드에서 resultCode로 받아서 사용할 수 있다.
                                                    // ok는 암호 설정이 성공적으로 완료되었다는 의미로 사용된다.
                                                    setResult(RESULT_OK);
                                                    finish();
                                                }
                                            }).show(PinActivity.this);
                                        }
                                    });
                                } else {
                                    // 다시 한 번 입력 받은 pin 번호가 틀렸을 경우, 다이얼로그를 띄워 알려주고 다시 입력 받는다.
                                    new ConfirmDialog(getString(R.string.dialog_message_no_match_password), new View.OnClickListener() {
                                        @Override
                                        public void onClick(View v) {
                                            resetPinNumber();
                                        }
                                    }).show(PinActivity.this);
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
            message = getString(R.string.dialog_message_close_app);
        } else {
            message = getString(R.string.dialog_message_stop_setting_pin);
        }

        setResult(RESULT_CANCELED);

        new YesNoDialog(message, v -> finish()).show(this);
    }

    // 입력한 핀 번호를 초기화하고 이미지도 회색으로 초기화한다.
    private void resetPinNumber() {
        stringBuilder = new StringBuilder();
        setPinImage(0, true, R.drawable.circle_gray);
    }

    /**
     * stringBuilder에 저장된 핀 번호에 따라 Pin 이미지를 바꿔주는 메소드
     *
     * 4개의 원 이미지마다 태그를 달아주고 태그와 인덱스를 비교하여 해당 원의 이미지를 바꿔준다.
     *
     * pin 번호를 하나 씩 입력할 때마다 해당 인덱스 값의 원을 분홍색 원으로 바꿔준다.
     *
     * 전체를 리셋하는 경우에는 리셋 인덱스를 0으로 넘기고 allreset을 true로 넘기고 전체 이미지가 회색 원으로 바뀐다.
     * 지우기 버튼을 클릭하여 하나 씩 지우는 경우에는 뒤에부터 하나씩 바꿔줘야하기 때문에 stringBuilder의 사이즈나 인덱스를 넘겨받아 회색 원으로 바꿔준다.
     *
     * @param index 변화할 이미지의 인덱스
     * @param allReset 전체를 리셋할지에 대한 여부
     * @param drawable 분홍색 또는 회색 원의 드로어블
     */
    private void setPinImage(int index, boolean allReset, int drawable) {
        // pinBinding.layoutPinImage 레이아웃 안에 있는 뷰의 개수를 알 수 있다. (현재 4개)
        int childCount = pinBinding.layoutPinImage.getChildCount();
        if(allReset) {
            for(int i = 0; i < childCount; i++) {
                if(pinBinding.layoutPinImage.getChildAt(i) instanceof ImageView) {
                    ImageView resetPinImg = (ImageView)pinBinding.layoutPinImage.getChildAt(i);
                    resetPinImg.setImageResource(drawable);
                }
            }
        } else  {
            if(pinBinding.layoutPinImage.getChildAt(index) instanceof ImageView) {
                ImageView resetPinImg = (ImageView)pinBinding.layoutPinImage.getChildAt(index);
                if(String.valueOf(index).equals((String)resetPinImg.getTag())) {
                    resetPinImg.setImageResource(drawable);
                }
            }
        }
    }
}