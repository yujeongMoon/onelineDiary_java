package com.example.onelinediary.dto;

/**
 * 설정의 피드백 데이터를 저장하기 위한 클래스
 */
public class Feedback {
    private String androidId; // 작성자의 안드로이드 아이디
    private String nickname = ""; // 작성자의 닉네임
    private String reportingDate; // 작성한 날짜 ex) 2021년 11월 4일 (목)
    private String reportingTime; // 작성한 시간 ex) 오전 10:22
    private String contents = ""; // 작성 내용
    private String feedbackKey = ""; // 피드백 키값

    private boolean isAdmin = false;
    private String profileImageName = "";
    private String UserNickname = "";
    private String UserAndroidId = "";

    public Feedback() {}

    public Feedback(String androidId, String nickname, String reportingDate, String reportingTime, String contents) {
        this.androidId = androidId;
        this.nickname = nickname;
        this.reportingDate = reportingDate;
        this.reportingTime = reportingTime;
        this.contents = contents;
    }

    public String getAndroidId() {
        return androidId;
    }

    public void setAndroidId(String androidId) {
        this.androidId = androidId;
    }

    public String getNickname() {
        return nickname;
    }

    public void setNickname(String nickname) {
        this.nickname = nickname;
    }

    public String getReportingDate() {
        return reportingDate;
    }

    public void setReportingDate(String reportingDate) {
        this.reportingDate = reportingDate;
    }

    public String getReportingTime() {
        return reportingTime;
    }

    public void setReportingTime(String reportingTime) {
        this.reportingTime = reportingTime;
    }

    public String getContents() {
        return contents;
    }

    public void setContents(String contents) {
        this.contents = contents;
    }

    public boolean isAdmin() {
        return isAdmin;
    }

    public void setAdmin(boolean admin) {
        isAdmin = admin;
    }

    public String getProfileImageName() {
        return profileImageName;
    }

    public void setProfileImageName(String profileImageName) {
        this.profileImageName = profileImageName;
    }

    public String getUserNickname() {
        return UserNickname;
    }

    public void setUserNickname(String userNickname) {
        UserNickname = userNickname;
    }

    public String getUserAndroidId() {
        return UserAndroidId;
    }

    public void setUserAndroidId(String userAndroidId) {
        UserAndroidId = userAndroidId;
    }

    public String getFeedbackKey() {
        return feedbackKey;
    }

    public void setFeedbackKey(String feedbackKey) {
        this.feedbackKey = feedbackKey;
    }
}
