package com.example.onelinediary.constant;

import android.location.Location;
import android.net.Uri;

import com.example.onelinediary.dto.Diary;
import com.example.onelinediary.dto.Feedback;
import com.example.onelinediary.utiliy.Utility;

import java.util.ArrayList;
import java.util.HashMap;

public class Const {
    // 데이터 베이스 키
    public static String DATABASE_CHILD_DIARY = "diary";
    public static String DATABASE_CHILD_MYINFO = "myInfo";
    public static String DATABASE_CHILD_NICKNAME = "nickname";
    public static String DATABASE_CHILD_FEEDBACK = "feedback";
    public static String DATABASE_CHILD_ADMIN = "admin";
    public static String DATABASE_CHILD_FEEDBACK_LIST = "feedbackList";
    public static String DATABASE_CHILD_SECURITY = "security";
    public static String DATABASE_CHILD_PIN = "pin";
    public static String DATABASE_CHILD_PROFILE = "profile";
    public static String DATABASE_CHILD_PROFILE_IMAGE = "pImage";

    public static String SP_KEY_NICKNAME = "com.example.onelinediary.nickname";
    public static String SP_KEY_INSTALLED = "com.example.onelinediary.isInstalled";
    public static String SP_KEY_ANDROID_ID = "com.example.onelinediary.androidId";
    public static String SP_KEY_SET_PIN_NUMBER = "com.example.onelinediary.setPinNumber";
    public static String SP_KEY_PIN_NUMBER = "com.example.onelinediary.pinNumber";
    public static String SP_KEY_PROFILE = "com.example.onelinediary.profile";

    public static String INTENT_KEY_MONTH = "month";
    public static String INTENT_KEY_DIARY = "diary";
    public static String INTENT_KEY_ANDROID_ID = "androidId";
    public static String INTENT_KEY_IS_LOGIN = "isLogin";

    public static String REPORTING_DATE_FORMAT = "yyyy년 MM월 dd일";

    public static int PICKER_IMAGE_REQUEST = 100;

    public final static String ADMIN_ANDROID_ID = "5915737b8962ff5d";
    public final static String VVIP_ANDROID_ID = "4d9546db1392bca5";

    // 기분 정의
    public enum Mood {
        NONE(0), HAPPY(1), SMILE(2), BLANK(3), SAD(4), NERVOUS(5);

        public final int value;
        Mood(int value) { this.value = value; }
        public int getValue() { return value; }
    }

    // page adapter에서 해시맵 데이터를 사용하기 위한 키 리스트
    public static ArrayList<String> monthKeyList;

    // DB에서 일기 데이터를 내려 받아 저장하는 리스트
    public static HashMap<String, ArrayList<Diary>> diaryList;

    // 현재 연도
    public static String currentYear = Utility.getYear();

    // 기존의 다이어리 삭제 여부에 관한 플래그
    public static boolean deleteDiary = false;

    // 메인 화면에서 현재 위치를 저장해두는 객체
    public static Location currentLocation = null;

    // 현재 날씨의 이모티콘 리소스 아이디를 저장한다.
    public static int weatherResId = -1; // resid

    public static String weather = null;

    // 카메라로 찍었을 때, 이미지의 uri를 저장한다.
    public static Uri photoUri = null;

    // 오늘 일기를 썼다면 오늘의 일기를 저장할 객체
    public static Diary todayDiary = null;

    // 사용자 닉네임
    // 처음에 팝업에 입력한 닉네임 값을 사용하기 위해 선언함.
    public static String nickname = "";

    // 안드로이드 아이디
    public static String androidId;

    // DB에서 내려받은 피드백을 저장하는 리스트
    public static ArrayList<Feedback> feedbackList;

    // 관리자 페이지에서 피드백을 보낸 사용자의 리스트
    public static ArrayList<String> userList;

    // 관리자 페이지에서 피드백을 보낸 사용자의 마지막 피드백 내용
    public static ArrayList<Feedback> userLastFeedbackList;
}
