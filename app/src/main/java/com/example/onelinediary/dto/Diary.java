package com.example.onelinediary.dto;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

/**
 * Firebase database에 저장될 필드들
 * <중요>데이터 베이스에 Uri를 저장하려고 하면 에러가 난다.</중요>
 */
public class Diary implements Parcelable {
    private String reportingDate; // 일기를 작성한 날짜
    private String photo = ""; // 사진이 하나인 경우
    private ArrayList<String> photoList = null; // 사진이 여러장인경우
    private String contents = ""; // 일기 내용
    private int mood; // 오늘의 기분
    private String day; // 일기를 작성한 날짜(일)
    private String location = ""; // 일기를 작성한 위치
    /**
     * 0 : 없음
     * - 01 : 맑음
     * - 03 : 구름 많음
     * - 04 : 흐림
     *
     * 1 : 비
     * 2 : 비/눈
     * 3 : 눈
     * 4 : 소나기
     */
    private String weather = ""; // 일기를 작성한 날의 날씨

    public Diary() { }

    protected Diary(Parcel in) {
        reportingDate = in.readString();
        photo = in.readString();
        photoList = in.createStringArrayList();
        contents = in.readString();
        mood = in.readInt();
        day = in.readString();
        location = in.readString();
        weather = in.readString();
    }

    public static final Creator<Diary> CREATOR = new Creator<Diary>() {
        @Override
        public Diary createFromParcel(Parcel in) {
            return new Diary(in);
        }

        @Override
        public Diary[] newArray(int size) {
            return new Diary[size];
        }
    };

    public String getReportingDate() {
        return reportingDate;
    }

    public void setReportingDate(String reportingDate) {
        this.reportingDate = reportingDate;
    }

    public String getPhoto() {
        return photo;
    }

    public void setPhoto(String photo) {
        this.photo = photo;
    }

    public ArrayList<String> getPhotoList() {
        return photoList;
    }

    public void setPhotoList(ArrayList<String> photoList) {
        this.photoList = photoList;
    }

    public String getContents() {
        return contents;
    }

    public void setContents(String contents) {
        this.contents = contents;
    }

    public int getMood() {
        return mood;
    }

    public void setMood(int mood) {
        this.mood = mood;
    }

    public String getDay() {
        return day;
    }

    public void setDay(String day) {
        this.day = day;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public String getWeather() {
        return weather;
    }

    public void setWeather(String weather) {
        this.weather = weather;
    }

    public Map<String, Object> toMap() {
        Map<String, Object> result = new HashMap<>();
        result.put("contents", contents);
        result.put("photo", photo);
        result.put("photoList", photoList);
        result.put("mood", mood);
        result.put("reportingDate", reportingDate);
        result.put("day", day);
        result.put("location", location);
        result.put("weather", weather);

        return result;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(reportingDate);
        dest.writeString(photo);
        dest.writeStringList(photoList);
        dest.writeString(contents);
        dest.writeInt(mood);
        dest.writeString(day);
        dest.writeString(location);
        dest.writeString(weather);
    }
}
