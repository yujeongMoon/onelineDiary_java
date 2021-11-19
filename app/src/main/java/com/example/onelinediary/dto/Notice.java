package com.example.onelinediary.dto;

import android.os.Parcel;
import android.os.Parcelable;

public class Notice implements Parcelable {
    private String title; // 공지사항의 제목
    private String writer; // 작성자
    private String year; // 삭제를 위한 키
    private String reportingDate; // 공지사항 작성일자
    private String contents; // 공지사항 내용

    public Notice() {}

    public Notice(String title, String reportingDate, String contents) {
        this.title = title;
        this.reportingDate = reportingDate;
        this.contents = contents;
    }

    public Notice(String title, String year, String reportingDate, String contents) {
        this.title = title;
        this.year = year;
        this.reportingDate = reportingDate;
        this.contents = contents;
    }

    protected Notice(Parcel in) {
        title = in.readString();
        writer = in.readString();
        year = in.readString();
        reportingDate = in.readString();
        contents = in.readString();
    }

    public static final Creator<Notice> CREATOR = new Creator<Notice>() {
        @Override
        public Notice createFromParcel(Parcel in) {
            return new Notice(in);
        }

        @Override
        public Notice[] newArray(int size) {
            return new Notice[size];
        }
    };

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getWriter() {
        return writer;
    }

    public void setWriter(String writer) {
        this.writer = writer;
    }

    public String getYear() {
        return year;
    }

    public void setYear(String year) {
        this.year = year;
    }

    public String getReportingDate() {
        return reportingDate;
    }

    public void setReportingDate(String reportingDate) {
        this.reportingDate = reportingDate;
    }

    public String getContents() {
        return contents;
    }

    public void setContents(String contents) {
        this.contents = contents;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(title);
        dest.writeString(writer);
        dest.writeString(year);
        dest.writeString(reportingDate);
        dest.writeString(contents);
    }
}
