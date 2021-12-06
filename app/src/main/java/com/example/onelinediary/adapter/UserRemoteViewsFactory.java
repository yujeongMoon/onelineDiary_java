package com.example.onelinediary.adapter;

import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.widget.RemoteViews;
import android.widget.RemoteViewsService;

import com.example.onelinediary.R;
import com.example.onelinediary.dto.Feedback;
import com.example.onelinediary.dto.WidgetUserList;
import com.example.onelinediary.utiliy.Utility;
import com.google.gson.Gson;

import java.util.ArrayList;

public class UserRemoteViewsFactory implements RemoteViewsService.RemoteViewsFactory {

    //context 설정하기
    public Context context = null;
    public ArrayList<String> userList;
    public ArrayList<Feedback> userLastFeedbackList;

    public UserRemoteViewsFactory(Context context) {
        Log.e("++++++++++++++++++++++++++++++++", "++++++++++++++++MyRemoteViewsFactory++++++++++++++++");
        this.context = context;
        userList = new ArrayList<>();
        userLastFeedbackList = new ArrayList<>();
        setData();
    }

    //DB를 대신하여 arrayList에 데이터를 추가하는 함수ㅋㅋ
    public void setData() {
        userList.clear();
        userLastFeedbackList.clear();

        Gson gson = new Gson();
        WidgetUserList widgetUserList = gson.fromJson(Utility.getString(context, "WidgetUserList"), WidgetUserList.class);
        userList.addAll(widgetUserList.userList);
        userLastFeedbackList.addAll(widgetUserList.userLastFeedbackList);
        Log.e("++++++++++++++++++++++++++++++++", "++++++++++++++++MyRemoteViewsFactory++++++++++++++++" + userList.size());
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
    public void onDestroy() {

    }

    // 항목 개수를 반환하는 함수
    @Override
    public int getCount() {
        return userList.size();
    }

    //각 항목을 구현하기 위해 호출, 매개변수 값을 참조하여 각 항목을 구성하기위한 로직이 담긴다.
    // 항목 선택 이벤트 발생 시 인텐트에 담겨야 할 항목 데이터를 추가해주어야 하는 함수
    @Override
    public RemoteViews getViewAt(int position) {
        RemoteViews listviewWidget = new RemoteViews(context.getPackageName(), R.layout.widget_item_user_list);

        Log.e("++++++++++++++++++++++++++++++++", "++++++++++++++++getViewAt++++++++++++++++" + userLastFeedbackList.get(position).getContents());

        listviewWidget.setTextViewText(R.id.feedback_last_contents, userLastFeedbackList.get(position).getContents());
        listviewWidget.setTextViewText(R.id.date, userLastFeedbackList.get(position).getReportingDate());
        listviewWidget.setTextViewText(R.id.nickname, userLastFeedbackList.get(position).getUserNickname());
        listviewWidget.setImageViewResource(R.id.profile_image, Utility.getResourceImage(context, userLastFeedbackList.get(position).getProfileImageName()));

        String[] reportingDate = userLastFeedbackList.get(position).getReportingDate().split("\\s");
        listviewWidget.setTextViewText(R.id.date, reportingDate[1] + " " + reportingDate[2]);

        // 항목 선택 이벤트 발생 시 인텐트에 담겨야 할 항목 데이터를 추가해주는 코드
        Intent fillInIntent = new Intent();
        fillInIntent.putExtra("androidId", userList.get(position));
        listviewWidget.setOnClickFillInIntent(R.id.user_layout, fillInIntent);

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
        return 1;
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
