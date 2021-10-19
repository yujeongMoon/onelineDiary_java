package com.example.onelinediary.dto;

public class BasicItemSwitch implements Item {
    private int icon; // 아이콘
    private String title; // 메뉴 타이틀
    private boolean isEnabled; // on, off

    public BasicItemSwitch() { }

    public BasicItemSwitch(int icon, String title, boolean isEnabled) {
        this.icon = icon;
        this.title = title;
        this.isEnabled = isEnabled;
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
}
