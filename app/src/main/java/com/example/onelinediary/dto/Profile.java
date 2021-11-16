package com.example.onelinediary.dto;

import android.widget.LinearLayout;

public class Profile {
    private String imageResName;
    private boolean isChecked;
    private LinearLayout profileLayout;

    public Profile() {}

    public Profile(String imageResName, boolean isChecked, LinearLayout profileLayout) {
        this.imageResName = imageResName;
        this.isChecked = isChecked;
        this.profileLayout = profileLayout;
    }

    public String getImageResName() {
        return imageResName;
    }

    public void setImageResName(String imageResName) {
        this.imageResName = imageResName;
    }

    public boolean isChecked() {
        return isChecked;
    }

    public void setChecked(boolean checked) {
        isChecked = checked;
    }

    public LinearLayout getProfileLayout() {
        return profileLayout;
    }

    public void setProfileLayout(LinearLayout profileLayout) {
        this.profileLayout = profileLayout;
    }
}
