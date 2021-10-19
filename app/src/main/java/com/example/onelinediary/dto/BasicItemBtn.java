package com.example.onelinediary.dto;

import android.view.View;

public class BasicItemBtn implements Item {
    private int icon; // 아이콘
    private String title; // 메뉴 타이틀
    private String buttonText; // 버튼 텍스트
    private View.OnClickListener listener; // 버튼 클릭 동작

    public BasicItemBtn() { }

    public BasicItemBtn(int icon, String title, String buttonText, View.OnClickListener listener) {
        this.icon = icon;
        this.title = title;
        this.buttonText = buttonText;
        this.listener = listener;
    }

    public int getIcon() {
        return icon;
    }

    public void setIcon(int icon) {
        this.icon = icon;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getButtonText() {
        return buttonText;
    }

    public void setButtonText(String buttonText) {
        this.buttonText = buttonText;
    }

    public View.OnClickListener getListener() {
        return listener;
    }

    public void setListener(View.OnClickListener listener) {
        this.listener = listener;
    }
}
