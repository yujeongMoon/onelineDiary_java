package com.example.onelinediary.activity;

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
import com.example.onelinediary.utiliy.LocationUtility;
import com.example.onelinediary.utiliy.WeatherUtility;

public class SplashActivity extends AppCompatActivity {
    private ActivitySplashBinding splashBinding;

    Location location = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        splashBinding = ActivitySplashBinding.inflate(getLayoutInflater());
        setContentView(splashBinding.getRoot());

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
                        splashBinding.splashTitle.setVisibility(View.VISIBLE);
                        splashBinding.splashWeather.setVisibility(View.VISIBLE);
                        splashBinding.splashWeather.setImageResource(resId);
                    });

                    new Handler(Looper.getMainLooper()).postDelayed(() -> {
                        Intent mainIntent = new Intent(this, MainActivity.class);
                        startActivity(mainIntent);
                        finish();
                    }, 2000);
                }
            });
        } else {
            runOnUiThread(() -> {
                splashBinding.splashTitle.setVisibility(View.VISIBLE);
            });

            new Handler(Looper.getMainLooper()).postDelayed(() -> {
                Intent mainIntent = new Intent(this, MainActivity.class);
                startActivity(mainIntent);
                finish();
            }, 2000);
        }
    }
}