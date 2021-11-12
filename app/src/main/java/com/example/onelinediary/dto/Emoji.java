package com.example.onelinediary.dto;

public class Emoji {
    public String res;   // 이모티콘 리소스 명
    public boolean checked;  // 체크 여부
    public int position; // 이모지 인덱스

    public Emoji() { }

    public Emoji(String res, boolean checked) {
        this.res = res;
        this.checked = checked;
    }

    public Emoji(String res, boolean checked, int position) {
        this.res = res;
        this.checked = checked;
        this.position = position;
    }
}
