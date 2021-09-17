package com.example.onelinediary.dto;

public class MoodItem {
    private int mood;

    public MoodItem() {}

    public MoodItem(int mood) {
        this.mood = mood;
    }

    public int getMood() {
        return mood;
    }

    public void setMood(int mood) {
        this.mood = mood;
    }
}
