package com.example.onelinediary.dto;

import android.view.View;

public class ImageTextItem implements Item {
    private int profileImage; // 프로필 이미지
    private String nickname; // 닉네임
    private View.OnClickListener imageClickListener;
    private View.OnClickListener textClickListener;

    public ImageTextItem() {}

    public ImageTextItem(int profileImage, String nickname, View.OnClickListener imageClickListener, View.OnClickListener textClickListener) {
        this.profileImage = profileImage;
        this.nickname = nickname;
        this.imageClickListener = imageClickListener;
        this.textClickListener = textClickListener;
    }

    public int getProfileImage() {
        return profileImage;
    }

    public void setProfileImage(int profileImage) {
        this.profileImage = profileImage;
    }

    public String getNickname() {
        return nickname;
    }

    public void setNickname(String nickname) {
        this.nickname = nickname;
    }

    public View.OnClickListener getImageClickListener() {
        return imageClickListener;
    }

    public void setImageClickListener(View.OnClickListener imageClickListener) {
        this.imageClickListener = imageClickListener;
    }

    public View.OnClickListener getTextClickListener() {
        return textClickListener;
    }

    public void setTextClickListener(View.OnClickListener textClickListener) {
        this.textClickListener = textClickListener;
    }
}
