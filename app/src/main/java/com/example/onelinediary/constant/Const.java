package com.example.onelinediary.constant;

import android.location.Location;
import android.net.Uri;

import com.example.onelinediary.dto.Diary;
import com.example.onelinediary.dto.Weather;
import com.example.onelinediary.utiliy.Utility;

import java.util.ArrayList;
import java.util.HashMap;

public class Const {
    public static String DATABASE_CHILD_DIARY = "diary";

    public static String INTENT_KEY_MONTH = "month";
    public static String INTENT_KEY_DIARY = "diary";

    public static String REPORTING_DATE_FORMAT = "yyyy년 MM월 dd일";

    public static int PICKER_IMAGE_REQUEST = 100;

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

    // 새로운 다이어리 추가 여부에 관한 플래그
    public static boolean addNewDiary = false;

    // 메인 화면에서 현재 위치를 저장해두는 객체
    public static Location currentLocation = null;

    // 현재 날씨의 이모티콘 리소스 아이디를 저장한다.
    public static int weatherResId = -1; // resid

    public static String weather = null;

    // 카메라로 찍었을 때, 이미지의 uri를 저장한다.
    public static Uri photoUri = null;

    // 오늘 일기를 썼다면 선택한 기분을 + 대신 보여주기 위해서 저장한다.
    public static int currentMoodResId = 0;

    // 오늘 일기를 썼다면 오늘의 일기를 저장할 객체
    public static Diary todayDiary = null;
}
