package com.example.onelinediary.custom;

import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.view.View;
import android.widget.RemoteViews;

import com.example.onelinediary.R;
import com.example.onelinediary.constant.Const;
import com.example.onelinediary.dto.Diary;
import com.example.onelinediary.service.FeedbackRemoteViewsService;
import com.example.onelinediary.service.UserRemoteViewsService;
import com.example.onelinediary.utiliy.DatabaseUtility;
import com.example.onelinediary.utiliy.Utility;
import com.google.gson.Gson;

/**
 * Implementation of App Widget functionality.
 */
public class AppWidget extends AppWidgetProvider {

    private RemoteViews views;
    private AppWidgetManager widgetManager;

    private final String ACTION_REFRESH_USER_LIST = "action.refresh.userList";
    private final String ACTION_REFRESH_FEEDBACK_LIST = "action.refresh.feedbackList";

    private Context context;

    // 위젯이 설치될 때마다 호출
    @Override
    public void onUpdate(Context context, AppWidgetManager appWidgetManager, int[] appWidgetIds) {
        // There may be multiple widgets active, so update all of them
        for (int appWidgetId : appWidgetIds) {
            updateAppWidget(context, appWidgetManager, appWidgetId);
        }

        Log.e("++++++++++++++++++++++++++++++++", "+++++++++++++++onUpdate+++++++++++++++++");

        super.onUpdate(context, appWidgetManager, appWidgetIds);
    }

    // 위젯의 크기 및 변경될 때마다 호출
    private void updateAppWidget(Context context, AppWidgetManager appWidgetManager,
                                 int appWidgetId) {

        this.context = context;
        widgetManager = appWidgetManager;

        Log.e("++++++++++++++++++++++++++++++++", "+++++++++++++++updateAppWidget+++++++++++++++++");

        // Construct the RemoteViews object
        views = new RemoteViews(context.getPackageName(), R.layout.app_widget);
        views.setTextViewText(R.id.tv_date, Utility.getDate(Const.REPORTING_DATE_FORMAT));
        views.setImageViewResource(R.id.profile_image, Utility.getResourceImage(context, Utility.getString(context, Const.SP_KEY_PROFILE)));
        views.setTextViewText(R.id.nickname, Utility.getString(context, Const.SP_KEY_NICKNAME));

        Gson gson = new Gson();
        Diary todayDiary = gson.fromJson(Utility.getString(context, "todayDiary"), Diary.class);
        if (todayDiary != null) {
            views.setImageViewResource(R.id.iv_mood, Utility.getResourceImage(context, todayDiary.getIconName()));
        } else {
            views.setImageViewResource(R.id.iv_mood, R.drawable.circle_gray);
        }
        views.setImageViewResource(R.id.iv_weather, Utility.getInt(context, "todayWeather"));

        if (Utility.getAndroidId(context).equals(Const.ADMIN_ANDROID_ID) && Utility.getBoolean(context, "isShowUserList", true)) { // 관리자
            getUserList(context, appWidgetId);
            Utility.putBoolean(context, "isShowUserList", true);
            views.setViewVisibility(R.id.user_list_feedback, View.VISIBLE);
            views.setViewVisibility(R.id.list_feedback, View.GONE);
        } else { // 사용자
            getFeedback(context, appWidgetId);
            Utility.putBoolean(context, "isShowUserList", false);
            views.setViewVisibility(R.id.user_list_feedback, View.GONE);
            views.setViewVisibility(R.id.list_feedback, View.VISIBLE);
        }

        Intent intent = new Intent(context, getClass());
        if (Utility.getBoolean(context, "isShowUserList", true)) {
            intent.setAction(ACTION_REFRESH_USER_LIST);
        } else {
            intent.setAction(ACTION_REFRESH_FEEDBACK_LIST);
        }
        views.setOnClickPendingIntent(R.id.iv_refresh, PendingIntent.getBroadcast(context, 0, intent, 0));

        // Instruct the widget manager to update the widget
        appWidgetManager.notifyAppWidgetViewDataChanged(appWidgetId, R.id.tv_date);
        appWidgetManager.updateAppWidget(appWidgetId, views);
    }

    // 위젯이 처음 생성될때 호출되며, 동일한 위젯의 경우 처음 호출
    @Override
    public void onEnabled(Context context) {
        // Enter relevant functionality for when the first widget is created
    }

    // 위젯의 마지막 인스턴스가 제거될때 호출
    @Override
    public void onDisabled(Context context) {
        Utility.putBoolean(context, "isShowUserList", true);
        // Enter relevant functionality for when the last widget is disabled
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        final String action = intent.getAction();
        Log.e("++++++++++++++++++++++++++++++++", "++++++++++++++onReceive+++++++++++++++++");

//        if (action.equals(AppWidgetManager.ACTION_APPWIDGET_UPDATE)) {
//            // refresh all your widgets
//            AppWidgetManager mgr = AppWidgetManager.getInstance(context);
//            ComponentName cn = new ComponentName(context, AppWidget.class);
//            if (Utility.getAndroidId(context).equals(Const.ADMIN_ANDROID_ID) && Utility.getBoolean(context, "isShowUserList", true)) {
//                mgr.notifyAppWidgetViewDataChanged(mgr.getAppWidgetIds(cn), R.id.user_list_feedback);
//            } else {
//                mgr.notifyAppWidgetViewDataChanged(mgr.getAppWidgetIds(cn), R.id.list_feedback);
//            }
//            Log.e("++++++++++++++++++++++++++++++++", "++++++++++++++notifyAppWidgetViewDataChanged+++++++++++++++++");
//        } else

        if (action.equals(ACTION_REFRESH_USER_LIST) || action.equals(ACTION_REFRESH_FEEDBACK_LIST)) {
            AppWidgetManager mgr = AppWidgetManager.getInstance(context);
            ComponentName cn = new ComponentName(context, AppWidget.class);
            onUpdate(context, mgr, mgr.getAppWidgetIds(cn));
            Log.e("++++++++++++++++++++++++++++++++", "++++++++++++++ACTION_REFRESH+++++++++++++++++");
        }

        super.onReceive(context, intent);
    }


    private void getUserList(Context context, int appWidgetId) {
//        Intent serviceIntent = new Intent(context, UserRemoteViewsService.class);
//        RemoteViews widget = new RemoteViews(context.getPackageName(), R.layout.app_widget);
//        widget.setRemoteAdapter(R.id.user_list_feedback, serviceIntent);
//        widgetManager.updateAppWidget(appWidgetId, widget);
//        widgetManager.notifyAppWidgetViewDataChanged(appWidgetId, R.id.user_list_feedback);

        DatabaseUtility.readFeedbackListWithUser(context, new DatabaseUtility.onCompleteCallback() {
            @Override
            public void onComplete(boolean isSuccess) {
                if(isSuccess) {
//                    views.set
                    Log.e("++++++++++++++++++++++++++++++++", "++++++++++++++++getFeedback++++++++++++++++" +widgetManager);
                    // RemoteViewsService 실행 등록시키는 함수
                    Intent serviceIntent = new Intent(context, UserRemoteViewsService.class);
                    RemoteViews widget = new RemoteViews(context.getPackageName(), R.layout.app_widget);
                    widget.setRemoteAdapter(R.id.user_list_feedback, serviceIntent);

                    widget.setTextViewText(R.id.tv_date, Utility.getDate(Const.REPORTING_DATE_FORMAT));

                    widgetManager.updateAppWidget(appWidgetId, widget);
                    widgetManager.notifyAppWidgetViewDataChanged(appWidgetId, R.id.user_list_feedback);
                } else {
                    Log.e("++++++++++++++++++++++++++++++++", "++++++++++++++++false getFeedback++++++++++++++++");
                }
            }
        });
    }

    private void getFeedback(Context context, int appWidgetId) {
        DatabaseUtility.readFeedback(context, Const.VVIP_ANDROID_ID, new DatabaseUtility.onCompleteCallback() {
            @Override
            public void onComplete(boolean isSuccess) {
                if(isSuccess) {
//                    views.set
                    Log.e("++++++++++++++++++++++++++++++++", "++++++++++++++++getFeedback++++++++++++++++" +widgetManager);
                    // RemoteViewsService 실행 등록시키는 함수
                    Intent serviceIntent = new Intent(context, FeedbackRemoteViewsService.class);
                    RemoteViews widget = new RemoteViews(context.getPackageName(), R.layout.app_widget);
                    widget.setRemoteAdapter(R.id.list_feedback, serviceIntent);

                    widget.setTextViewText(R.id.tv_date, Utility.getDate(Const.REPORTING_DATE_FORMAT));

//                    for (int appWidgetId : appWidgetIds) {
                    widgetManager.updateAppWidget(appWidgetId, widget);
                    widgetManager.notifyAppWidgetViewDataChanged(appWidgetId, R.id.list_feedback);
                    widget.setScrollPosition(R.id.list_feedback, Const.feedbackList.size() - 1);
                    new Handler(Looper.getMainLooper()).postDelayed(() -> {
                        widgetManager.updateAppWidget(appWidgetId, widget);
                    }, 1000);
//                    }
                } else {
                    Log.e("++++++++++++++++++++++++++++++++", "++++++++++++++++false getFeedback++++++++++++++++");
                }
            }
        });
    }


}