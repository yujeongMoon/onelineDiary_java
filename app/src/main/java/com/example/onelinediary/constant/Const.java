package com.example.onelinediary.constant;

import com.example.onelinediary.dto.Diary;
import com.example.onelinediary.utiliy.Utility;

import java.util.ArrayList;
import java.util.HashMap;

public class Const {
    // page adapter에서 해시맵 데이터를 사용하기 위한 키 리스트
    public static ArrayList<String> monthKeyList;

    // DB에서 일기 데이터를 내려 받아 저장하는 리스트
    public static HashMap<String, ArrayList<Diary>> diaryList;

    public static String currentYear = Utility.getYear();
}
