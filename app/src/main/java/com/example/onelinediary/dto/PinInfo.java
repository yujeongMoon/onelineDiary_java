package com.example.onelinediary.dto;

public class PinInfo {
    private String setPinNumber;
    private String number;

    public PinInfo() {}

    public PinInfo(String setPinNumber, String number) {
        this.setPinNumber = setPinNumber;
        this.number = number;
    }

    public String getSetPinNumber() {
        return setPinNumber;
    }

    public void setSetPinNumber(String setPinNumber) {
        this.setPinNumber = setPinNumber;
    }

    public String getNumber() {
        return number;
    }

    public void setNumber(String number) {
        this.number = number;
    }
}
