package com.example.onelinediary.service;

import android.content.Intent;
import android.widget.RemoteViewsService;

import com.example.onelinediary.adapter.FeedbackRemoteViewsFactory;
import com.example.onelinediary.adapter.UserRemoteViewsFactory;

public class UserRemoteViewsService extends RemoteViewsService {

    //필수 오버라이드 함수 : RemoteViewsFactory를 반환한다.
    @Override
    public RemoteViewsFactory onGetViewFactory(Intent intent) {
        return new UserRemoteViewsFactory(this.getApplicationContext());
    }
}
