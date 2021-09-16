package com.example.onelinediary.utiliy;

import android.annotation.SuppressLint;
import android.content.Context;
import android.provider.Settings;

import java.text.SimpleDateFormat;
import java.util.Date;

public class Utility {
    public static boolean isStringNullOrEmpty(String text) {
        return text == null || text.isEmpty();
    }

    /**
     * dp 값을 px 값으로 바꿔준다.
     * @param context 컨텍스트
     * @param dp px로 변환될 dp
     * @return px
     */
    public static int dpToPx(Context context, int dp) {
        return (int)(dp * context.getResources().getDisplayMetrics().density + 0.5f);
    }

    /**
     * 기기의 안드로이드 아이디를 가져온다.
     * 안드로이드 아이디를 가져오기 위해서는 manifest에 아래의 권한을 추가해야한다.
     * <uses-permission android:name="android.permission.READ_PHONE_STATE"/>
     *
     * @param context 컨텍스트
     * @return 안드로이드 아이디
     */
    @SuppressLint("HardwareIds")
    public static String getAndroidId(Context context) {
        return Settings.Secure.getString(context.getContentResolver(), Settings.Secure.ANDROID_ID);
    }

    /**
     * @return 현재 연도
     */
    @SuppressLint("SimpleDateFormat")
    public static String getYear() {
        return new SimpleDateFormat("yyyy").format(new Date());
    }

    /**
     * @return 현재 달
     */
    @SuppressLint("SimpleDateFormat")
    public static String getMonth() {
        return new SimpleDateFormat("MM").format(new Date());
    }

    /**
     * @return 현재 일
     */
    @SuppressLint("SimpleDateFormat")
    public static String getDay() {
        return new SimpleDateFormat("dd").format(new Date());
    }

    /**
     * 원하는 포맷을 입력하면 오늘의 날짜르 해당 포맷으로 만들어준다.
     * @param format 입력한 포맷 ex) "yyyy-MM-dd"
     * @return 포맷에 맞게 변환된 오늘 날짜
     */
    @SuppressLint("SimpleDateFormat")
    public static String getDate(String format) {
        return new SimpleDateFormat(format).format(new Date());
    }
}
