package com.example.onelinediary.dto;

public class Weather {
    private double latitude; // 위도
    private double longitude; // 경도

    private String fcstDate; // 예보 날짜
    private String fcstTime; // 예보 시간
    /**
     * 0 ~ 5 : 맑음
     * 6 ~ 8 : 구름 많음
     * 9 ~ 10 : 흐림
     */
    private String SKY; // 하늘상태
    /**
     * 0 : 없음
     * 1 : 비
     * 2 : 비/눈
     * 3 : 눈
     * 4 : 소나기
     */
    private String PTY; // 강수형태
    private String POP; // 강수확률(%)
    private String TMN; // 일 최저기온
    private String TMX; // 일 최고기온

    public double getLatitude() {
        return latitude;
    }

    public void setLatitude(double latitude) {
        this.latitude = latitude;
    }

    public double getLongitude() {
        return longitude;
    }

    public void setLongitude(double longitude) {
        this.longitude = longitude;
    }

    public String getFcstDate() {
        return fcstDate;
    }

    public void setFcstDate(String fcstDate) {
        this.fcstDate = fcstDate;
    }

    public String getFcstTime() {
        return fcstTime;
    }

    public void setFcstTime(String fcstTime) {
        this.fcstTime = fcstTime;
    }

    public String getSKY() {
        return SKY;
    }

    public void setSKY(String SKY) {
        this.SKY = SKY;
    }

    public String getPTY() {
        return PTY;
    }

    public void setPTY(String PTY) {
        this.PTY = PTY;
    }

    public String getPOP() {
        return POP;
    }

    public void setPOP(String POP) {
        this.POP = POP;
    }

    public String getTMN() {
        return TMN;
    }

    public void setTMN(String TMN) {
        this.TMN = TMN;
    }

    public String getTMX() {
        return TMX;
    }

    public void setTMX(String TMX) {
        this.TMX = TMX;
    }
}
