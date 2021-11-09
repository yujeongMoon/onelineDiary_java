package com.example.onelinediary.dto;

import android.widget.CompoundButton;

public class BasicItemSwitch implements Item {
    private int icon; // 아이콘
    private String title; // 메뉴 타이틀
    private boolean isEnabled; // on, off
    private CompoundButton.OnCheckedChangeListener listener;

    public BasicItemSwitch() { }

    public BasicItemSwitch(int icon, String title, boolean isEnabled, CompoundButton.OnCheckedChangeListener listener) {
        this.icon = icon;
        this.title = title;
        this.isEnabled = isEnabled;
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

    public boolean isEnabled() {
        return isEnabled;
    }

    public void setEnabled(boolean enabled) {
        isEnabled = enabled;
    }

    public CompoundButton.OnCheckedChangeListener getListener() {
        return listener;
    }

    public void setListener(CompoundButton.OnCheckedChangeListener listener) {
        this.listener = listener;
    }
}
