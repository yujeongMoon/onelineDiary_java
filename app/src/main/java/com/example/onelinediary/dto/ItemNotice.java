package com.example.onelinediary.dto;

import android.os.Parcel;
import android.os.Parcelable;

public class ItemNotice implements Item, Parcelable {
    private String key; // 작성 일자
    private Notice notice;
    private boolean isClicked = false;

    public ItemNotice() {}

    public ItemNotice(Notice notice, boolean isClicked) {
        this.notice = notice;
        this.isClicked = isClicked;
    }

    public ItemNotice(String key, Notice notice, boolean isClicked) {
        this.key = key;
        this.notice = notice;
        this.isClicked = isClicked;
    }

    protected ItemNotice(Parcel in) {
        key = in.readString();
        notice = in.readParcelable(Notice.class.getClassLoader());
        isClicked = in.readByte() != 0;
    }

    public static final Creator<ItemNotice> CREATOR = new Creator<ItemNotice>() {
        @Override
        public ItemNotice createFromParcel(Parcel in) {
            return new ItemNotice(in);
        }

        @Override
        public ItemNotice[] newArray(int size) {
            return new ItemNotice[size];
        }
    };

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public boolean isClicked() {
        return isClicked;
    }

    public void setClicked(boolean clicked) {
        isClicked = clicked;
    }

    public Notice getNotice() {
        return notice;
    }

    public void setNotice(Notice notice) {
        this.notice = notice;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(key);
        dest.writeParcelable(notice, flags);
        dest.writeByte((byte) (isClicked ? 1 : 0));
    }
}
