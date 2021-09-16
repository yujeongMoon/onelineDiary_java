package com.example.onelinediary.dto;

import java.util.Date;

/**
 * Firebase database에 저장될 필드들
 * <중요>데이터 베이스에 Uri를 저장하려고 하면 에러가 난다.</중요>
 */
public class Diary {
    private String reportingDate;
    private String photo = ""; // String -> uri => setImageUri
    private String contents = "";
    private int mood;

    public Diary() { }

    public Diary(String reportingDate, String photo, String contents, int mood) {
        this.reportingDate = reportingDate;
        this.photo = photo;
        this.contents = contents;
        this.mood = mood;
    }

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
}
