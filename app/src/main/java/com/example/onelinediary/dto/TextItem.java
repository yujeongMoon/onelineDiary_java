package com.example.onelinediary.dto;

public class TextItem implements Item {
    private String contents; // 공지 텍스트

    public TextItem() {}

    public TextItem(String text) {
        this.contents = text;
    }

    public String getContents() {
        return contents;
    }

    public void setContents(String contents) {
        this.contents = contents;
    }
}
