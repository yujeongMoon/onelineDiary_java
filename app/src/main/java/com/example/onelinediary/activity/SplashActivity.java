package com.example.onelinediary.activity;

import android.app.Activity;
import android.content.Intent;
import android.location.Location;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;

import com.example.onelinediary.R;
import com.example.onelinediary.constant.Const;
import com.example.onelinediary.databinding.ActivitySplashBinding;
import com.example.onelinediary.utiliy.DatabaseUtility;
import com.example.onelinediary.utiliy.LocationUtility;
import com.example.onelinediary.utiliy.Utility;
import com.example.onelinediary.utiliy.WeatherUtility;

public class SplashActivity extends AppCompatActivity {
    private ActivitySplashBinding splashBinding;

    Location location = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        splashBinding = ActivitySplashBinding.inflate(getLayoutInflater());
        setContentView(splashBinding.getRoot());

        // preference에 안드로이드 아이디를 저장한다.
        Utility.putString(this, Const.SP_KEY_ANDROID_ID, Utility.getAndroidId(this));

        // 앱을 처음 설치하거나 재설치 했을 경우, DB에 닉네임이 있는 지 확인한 후 닉네임 값을 가져온다.
        // 기기에 닉네임이 저장되어있다면 닉네임을 사용하고 없다면 보여주지 않는다.
        Boolean isInstalled = Utility.getBoolean(SplashActivity.this, Const.SP_KEY_INSTALLED);
        String nickname = Utility.getString(this, Const.SP_KEY_NICKNAME);

        if (!isInstalled) {
            DatabaseUtility.getNickname(this, (isSuccess, result) -> {
                Const.nickname = result;
                Utility.putString(SplashActivity.this, Const.SP_KEY_NICKNAME, result);
                Utility.putBoolean(SplashActivity.this, Const.SP_KEY_INSTALLED, true);
            });
        } else {
            if (!nickname.equals("")) {
                Const.nickname = nickname;
                splashBinding.nicknameLayout.setVisibility(View.VISIBLE);
                splashBinding.nickname.setText(nickname);
            } else {
                Const.nickname = "";
                splashBinding.nicknameLayout.setVisibility(View.GONE);
            }
        }

        if (!LocationUtility.checkPermission(this)) {
            location = LocationUtility.getLastKnownLocation(this);
        }

        if (location != null) {
            WeatherUtility.getWeather(location.getLatitude(), location.getLongitude(), (isSuccess, result, error) -> {
                if (isSuccess) {
                    int resId;
                    switch (result.getPTY()) {
                        case "1":
                            resId = R.drawable.weather_rain;
                            break;

                        case "3":
                            resId = R.drawable.weather_snow;
                            break;

                        case "4":
                            resId = R.drawable.weather_shower;
                            break;

                        case "0":
                            if (result.getSKY().equals("1")) {
                                resId = R.drawable.weather_sunny;
                            } else if (result.getSKY().equals("3")) {
                                resId = R.drawable.weather_cloud;
                            } else {
                                resId = R.drawable.weather_blur;
                            }
                            break;

                        default:
                            resId = -1;
                    }

                    Const.weatherResId = resId;

                    runOnUiThread(() -> {
                        splashBinding.splashWeather.setVisibility(View.VISIBLE);
                        splashBinding.splashWeather.setImageResource(resId);
                    });

                    gotoNextActivity();
                } else {
                    gotoNextActivity();
                }
            });
        } else { // location이 null일 때
            gotoNextActivity();
        }
    }

    private void gotoNextActivity() {
        if (Utility.getString(getApplicationContext(), "setPinNumber").equals("Y")) {
            Intent pinLoginIntent = new Intent(this, PinActivity.class);
            pinLoginIntent.putExtra("isLogin", true);
            startActivity(pinLoginIntent);

//            new Handler(Looper.getMainLooper()).postDelayed(() -> {
//                Intent mainIntent = new Intent(context, activityClass);
//                context.startActivity(mainIntent);
//                ((Activity)context).finish();
//            }, millisTime);

            finish();
        } else {
            Utility.startActivity(this, MainActivity.class, 2000);
        }
    }
}