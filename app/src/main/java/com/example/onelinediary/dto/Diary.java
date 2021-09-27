package com.example.onelinediary.dto;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.HashMap;
import java.util.Map;

/**
 * Firebase database에 저장될 필드들
 * <중요>데이터 베이스에 Uri를 저장하려고 하면 에러가 난다.</중요>
 */
public class Diary implements Parcelable {
    private String reportingDate;
    private String photo = ""; // String -> uri => setImageUri
    private String contents = "";
    private int mood;
    private String day;

    public Diary() { }

    public Diary(String reportingDate, String photo, String contents, int mood) {
        this.reportingDate = reportingDate;
        this.photo = photo;
        this.contents = contents;
        this.mood = mood;
    }

    protected Diary(Parcel in) {
        reportingDate = in.readString();
        photo = in.readString();
        contents = in.readString();
        mood = in.readInt();
        day = in.readString();
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(reportingDate);
        dest.writeString(photo);
        dest.writeString(contents);
        dest.writeInt(mood);
        dest.writeString(day);
    }

    @Override
    public int describeContents() {
        return 0;
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

    public Map<String, Object> toMap() {
        Map<String, Object> result = new HashMap<>();
        result.put("contents", contents);
        result.put("photo", photo);
        result.put("mood", mood);
        result.put("reportingDate", reportingDate);

        return result;
    }

}
