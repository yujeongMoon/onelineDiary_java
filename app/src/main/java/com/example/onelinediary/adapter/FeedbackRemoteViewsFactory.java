package com.example.onelinediary.adapter;

import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.widget.RemoteViews;
import android.widget.RemoteViewsService;

import com.example.onelinediary.R;
import com.example.onelinediary.constant.Const;
import com.example.onelinediary.dto.Feedback;
import com.example.onelinediary.dto.WidgetFeedList;
import com.example.onelinediary.utiliy.Utility;
import com.google.gson.Gson;

import java.util.ArrayList;

public class FeedbackRemoteViewsFactory implements RemoteViewsService.RemoteViewsFactory {

    //context 설정하기
    public Context context = null;
    public ArrayList<Feedback> arrayList;

    public FeedbackRemoteViewsFactory(Context context) {
        this.context = context;
        arrayList = new ArrayList<>();
        setData();
    }

    public void setData() {
        arrayList.clear();
        Gson gson = new Gson();
        WidgetFeedList widgetFeedList = gson.fromJson(Utility.getString(context, "WidgetFeedbackList"), WidgetFeedList.class);
        arrayList.addAll(widgetFeedList.feedbackArrayList);
    }

    //이 모든게 필수 오버라이드 메소드

    //실행 최초로 호출되는 함수
    @Override
    public void onCreate() {
        setData();
    }

    //항목 추가 및 제거 등 데이터 변경이 발생했을 때 호출되는 함수
    //브로드캐스트 리시버에서 notifyAppWidgetViewDataChanged()가 호출 될 때 자동 호출
    @Override
    public void onDataSetChanged() {
        setData();
    }

    //마지막에 호출되는 함수
    @Override
    public void onDestroy() { }

    // 항목 개수를 반환하는 함수
    @Override
    public int getCount() {
        return arrayList.size();
    }

    //각 항목을 구현하기 위해 호출, 매개변수 값을 참조하여 각 항목을 구성하기위한 로직이 담긴다.
    // 항목 선택 이벤트 발생 시 인텐트에 담겨야 할 항목 데이터를 추가해주어야 하는 함수
    @Override
    public RemoteViews getViewAt(int position) {
        RemoteViews listviewWidget;

        int layoutId = 0;
        if (arrayList.get(position).getAndroidId().equals(Utility.getAndroidId(context))) {
            listviewWidget = new RemoteViews(context.getPackageName(), R.layout.widget_message_right);
            layoutId = R.id.container_right;
        } else {
            listviewWidget = new RemoteViews(context.getPackageName(), R.layout.widget_message_left);
            layoutId = R.id.container_left;

            if (Utility.getAndroidId(context).equals(Const.ADMIN_ANDROID_ID)) { // 관리자
                listviewWidget.setImageViewResource(R.id.profile_image, Utility.getResourceImage(context, arrayList.get(position).getProfileImageName()));
                listviewWidget.setTextViewText(R.id.nickname, arrayList.get(position).getUserNickname());
            } else { // 사용자
                listviewWidget.setImageViewResource(R.id.profile_image, R.drawable.profile_admin);
                listviewWidget.setTextViewText(R.id.nickname, "관리자");
            }
        }

        listviewWidget.setTextViewText(R.id.message, arrayList.get(position).getContents());
        listviewWidget.setTextViewText(R.id.time, arrayList.get(position).getReportingTime());

        // 항목 선택 이벤트 발생 시 인텐트에 담겨야 할 항목 데이터를 추가해주는 코드
        Intent fillInIntent = new Intent();
        fillInIntent.putExtra(Const.INTENT_KEY_ANDROID_ID, arrayList.get(arrayList.size() - 1).getUserAndroidId());
        listviewWidget.setOnClickFillInIntent(layoutId, fillInIntent);
        //setOnClickFillInIntent 브로드캐스트 리시버에서 항목 선택 이벤트가 발생할 때 실행을 의뢰한 인텐트에 각 항목의 데이터를 추가해주는 함수
        //브로드캐스트 리시버의 인텐트와 Extra 데이터가 담긴 인텐트를 함치는 역할을 한다.

        return listviewWidget;
    }

    //로딩 뷰를 표현하기 위해 호출, 없으면 null
    @Override
    public RemoteViews getLoadingView() {
        return null;
    }

    //항목의 타입 갯수를 판단하기 위해 호출, 모든 항목이 같은 뷰 타입이라면 1을 반환하면 된다.
    @Override
    public int getViewTypeCount() {
        return 2;
    }

    //각 항목의 식별자 값을 얻기 위해 호출
    @Override
    public long getItemId(int position) {
        return 0;
    }

    // 같은 ID가 항상 같은 개체를 참조하면 true 반환하는 함수
    @Override
    public boolean hasStableIds() {
        return false;
    }
}
