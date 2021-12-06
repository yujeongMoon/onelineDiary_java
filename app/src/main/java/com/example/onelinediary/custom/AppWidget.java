package com.example.onelinediary.custom;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.RemoteViews;

import androidx.core.app.RemoteInput;

import com.example.onelinediary.R;
import com.example.onelinediary.activity.SplashActivity;
import com.example.onelinediary.constant.Const;
import com.example.onelinediary.dto.Diary;
import com.example.onelinediary.dto.Feedback;
import com.example.onelinediary.service.FeedbackRemoteViewsService;
import com.example.onelinediary.service.UserRemoteViewsService;
import com.example.onelinediary.utiliy.DatabaseUtility;
import com.example.onelinediary.utiliy.Utility;
import com.google.gson.Gson;

import java.util.HashMap;

/**
 * Implementation of App Widget functionality.
 */
public class AppWidget extends AppWidgetProvider {
    private final String ACTION_REFRESH_USER_LIST = "userList";
    private final String ACTION_REFRESH_FEEDBACK_LIST = "feedbackList";

    Intent selectedItem;

    private RemoteViews widget;

    // 위젯이 설치될 때마다 호출
    @Override
    public void onUpdate(Context context, AppWidgetManager appWidgetManager, int[] appWidgetIds) {
        // There may be multiple widgets active, so update all of them
        HashMap<Integer, Boolean> statusMap = Utility.getWidgetStatusMap(context);
        for (int appWidgetId : appWidgetIds) {

            Log.e("++++++++++++++++++++++++++++++++", "+++++++++++++++statusMap.size()+++++++++++++++++" + statusMap.size());
            Log.e("++++++++++++++++++++++++++++++++", "+++++++++++++++statusMap.containsKey(appWidgetId)+++++++++++++++++" + statusMap.containsKey(appWidgetId));
            /**
             * 프리퍼런스에 저장된 위젯 아이디가 존재하는지 체크한다.
             */
            if(statusMap.size() == 0 || !statusMap.containsKey(appWidgetId)) {

                Log.e("++++++++++++++++++++++++++++++++", "+++++++++++++++statusMap create!!!+++++++++++++++++" + appWidgetId);

                /**
                 * 초기값을 프리퍼런스에 저장.
                 */
                if (Utility.getAndroidId(context).equals(Const.ADMIN_ANDROID_ID)) {
                    statusMap.put(appWidgetId, true);
                } else {
                    statusMap.put(appWidgetId, false);
                }
                Utility.putWidgetStatus(context, statusMap);
            }

            updateAppWidget(context, appWidgetManager, appWidgetId, statusMap, null);
        }

        Log.e("++++++++++++++++++++++++++++++++", "+++++++++++++++onUpdate+++++++++++++++++");

        super.onUpdate(context, appWidgetManager, appWidgetIds);
    }

    // 위젯의 크기 및 변경될 때마다 호출
    private void updateAppWidget(Context context, AppWidgetManager appWidgetManager,
                                 int appWidgetId, HashMap<Integer, Boolean> statusMap, String androidId) {

        Log.e("++++++++++++++++++++++++++++++++", "+++++++++++++++updateAppWidget+++++++++++++++++" + statusMap.size());

        widget = new RemoteViews(context.getPackageName(), R.layout.app_widget);

        if (Utility.getAndroidId(context).equals(Const.ADMIN_ANDROID_ID) && statusMap.get(appWidgetId)) { // 관리자
            putWidgetStatus(context, appWidgetId, true, statusMap);
            getUserList(context, appWidgetId, appWidgetManager, statusMap);
        } else { // 사용자
            putWidgetStatus(context, appWidgetId, false, statusMap);

            String andId = Utility.getAndroidId(context);
            Log.e("++++++++++++++4d9546db1392bca5++++++++++++++++++", "+++++++++++++++androidId11+++++++++++++++++" +andId);
            if (!TextUtils.isEmpty(androidId)) {
                andId = androidId;
                Log.e("++++++++++++++4d9546db1392bca5++++++++++++++++++", "+++++++++++++++androidId22+++++++++++++++++" +andId);
            }

            getFeedback(context, andId, appWidgetId, appWidgetManager, statusMap);
        }
    }



    private void putWidgetStatus(Context context, int appWidgetId, boolean isShowUserList, HashMap<Integer, Boolean> statusMap) {
        statusMap.put(appWidgetId, isShowUserList);
        Utility.putWidgetStatus(context, statusMap);
    }

    // 위젯이 처음 생성될때 호출되며, 동일한 위젯의 경우 처음 호출
    @Override
    public void onEnabled(Context context) {
        // Enter relevant functionality for when the first widget is created
    }

    // 위젯의 마지막 인스턴스가 제거될때 호출
    @Override
    public void onDisabled(Context context) {
        Utility.clearWidgetStatusMap(context);
        // Enter relevant functionality for when the last widget is disabled
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        final String action = intent.getAction();
        AppWidgetManager mgr = AppWidgetManager.getInstance(context);
        ComponentName cn = new ComponentName(context, AppWidget.class);
        Log.e("++++++++++++++++++++++++++++++++", "++++++++++++++onReceive+++++++++++++++++" + action);

        HashMap<Integer, Boolean> statusMap = Utility.getWidgetStatusMap(context);

        if (action.equals(AppWidgetManager.ACTION_APPWIDGET_UPDATE)) {
            // refresh all your widgets
//            AppWidgetManager mgr = AppWidgetManager.getInstance(context);
//            ComponentName cn = new ComponentName(context, AppWidget.class);
//            if (Utility.getAndroidId(context).equals(Const.ADMIN_ANDROID_ID) && Utility.getBoolean(context, "isShowUserList", true)) {
//                mgr.notifyAppWidgetViewDataChanged(mgr.getAppWidgetIds(cn), R.id.user_list_feedback);
//            } else {
//                mgr.notifyAppWidgetViewDataChanged(mgr.getAppWidgetIds(cn), R.id.list_feedback);
//            }
//            Log.e("++++++++++++++++++++++++++++++++", "++++++++++++++notifyAppWidgetViewDataChanged+++++++++++++++++");
        } else if (action.contains(ACTION_REFRESH_FEEDBACK_LIST)) {
            String[] actionSplit = action.split("\\.");
            String androidId = actionSplit[1];
            Log.e("++++++++++++++++++++++++++++++++", "++++++++++++++ACTION_REFRESH_FEEDBACK_LIST androidId +++++++++++++++++" + androidId);
            int appWidgetId = Integer.parseInt(actionSplit[2]);
            Log.e("++++++++++++++++++++++++++++++++", "++++++++++++++ACTION_REFRESH_FEEDBACK_LIST appWidgetId+++++++++++++++++" + appWidgetId);
            selectedItem = intent;
//            onUpdate(context, mgr, mgr.getAppWidgetIds(cn));
            updateAppWidget(context, mgr, appWidgetId, statusMap, androidId);
        } else if (action.contains(ACTION_REFRESH_USER_LIST)) {
            String[] actionSplit = action.split("\\.");
            String androidId = actionSplit[1];
            Log.e("++++++++++++++++++++++++++++++++", "++++++++++++++ACTION_REFRESH_FEEDBACK_LIST androidId +++++++++++++++++" + androidId);
            int appWidgetId = Integer.parseInt(actionSplit[2]);
            Log.e("++++++++++++++++++++++++++++++++", "++++++++++++++ACTION_REFRESH_FEEDBACK_LIST appWidgetId+++++++++++++++++" + appWidgetId);
            selectedItem = null;
//            onUpdate(context, mgr, mgr.getAppWidgetIds(cn));
            updateAppWidget(context, mgr, appWidgetId, statusMap, androidId);
            Log.e("++++++++++++++++++++++++++++++++", "++++++++++++++ACTION_REFRESH_USER_LIST+++++++++++++++++");
        } else if (action.contains("action.move")) {
            int appWidgetId = Integer.parseInt(action.substring(action.lastIndexOf("_") + 1));
            Log.e("++++++++++++++++++++++++++++++++", "++++++++++++++getAppWidgetIds+++++++++++++++++" + appWidgetId);
            putWidgetStatus(context, appWidgetId, false, statusMap);
            updateAppWidget(context, mgr, appWidgetId, statusMap, intent.getStringExtra("androidId"));
        } else if (action.contains("action.back")) {
            String[] actionSplit = action.split("\\.");
            int appWidgetId = Integer.parseInt(actionSplit[2]);
            putWidgetStatus(context, appWidgetId, true, statusMap);
            Log.e("++++++++++++++++++++++++++++++++", "++++++++++++++action.back+++++++++++++++++" + appWidgetId);

            new Handler(Looper.getMainLooper()).postDelayed(() -> updateAppWidget(context, mgr, appWidgetId, statusMap, null), 500);
        } else if (action.contains("action.activity")) {
            Intent intent1 = new Intent(context, SplashActivity.class);
            intent1.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);

            if (action.equals("action.activity.setting")) {
                intent1.putExtra("moveActivity", Const.ACTIVITY_TYPE_SETTING);
            } else if (action.equals("action.activity.feedback")) {
                intent1.putExtra("moveActivity", Const.ACTIVITY_TYPE_FEEDBACK);
                intent1.putExtra("androidId", intent.getStringExtra("androidId"));
            } else if (action.equals("action.activity.detailDiary")) {
                intent1.putExtra("moveActivity", Const.ACTIVITY_TYPE_DETAIL);
            } else if (action.equals("action.activity.NewDiary")) {
                intent1.putExtra("moveActivity", Const.ACTIVITY_TYPE_New);
            }

            context.startActivity(intent1);
        } else if (action.equals("action.notification.reply")) {
            Bundle remoteInput = RemoteInput.getResultsFromIntent(intent);
            String message = "";
            if (remoteInput != null) {
                message = remoteInput.getCharSequence("key_text_reply").toString();
            }

            if (!TextUtils.isEmpty(message)) {
                Feedback feedback = new Feedback();
                feedback.setNickname(Utility.getString(context.getApplicationContext(), Const.SP_KEY_NICKNAME));
                feedback.setAndroidId(Utility.getAndroidId(context));
                feedback.setReportingDate(Utility.getDateWithDayOfWeek("yyyy년 MM월 d일"));
                feedback.setReportingTime(Utility.getTime_a_hh_mm());
                feedback.setContents(message);
                feedback.setAdmin(Utility.getAndroidId(context).equals(Const.ADMIN_ANDROID_ID));
                feedback.setUserAndroidId(intent.getStringExtra("userAndroidId"));
                feedback.setProfileImageName(intent.getStringExtra("userProfileImageName"));
                feedback.setUserNickname(intent.getStringExtra("userNickname"));

                DatabaseUtility.writeFeedback(intent.getStringExtra("userAndroidId"), feedback, isSuccess -> {
                    if (isSuccess) {
                        // 관리자 화면에서 보여주기 위한 마지막 피드백 리스트에 추가
                        // 마지막에 작성한 사람이 관리자일 경우에 관리자의 메세지 저장
                        if (!feedback.getContents().equals("")) {
                            DatabaseUtility.addFeedbackListWithUser(intent.getStringExtra("userAndroidId"), feedback, null);
                        }
                    } else { // 피드백 작성이 실패했을 경우

                    }
                });
            }

            NotificationManager manager = (NotificationManager)context.getSystemService(Context.NOTIFICATION_SERVICE);
            manager.cancel(0);
            Log.e("++++++++++++++++++++++++++++++++", "++++++++++++++action.notification.reply+++++++++++++++++" + message);
        }

        super.onReceive(context, intent);
    }


    private void getUserList(Context context, int appWidgetId, AppWidgetManager appWidgetManager, HashMap<Integer, Boolean> statusMap) {
        DatabaseUtility.readFeedbackListWithUser(context, isSuccess -> {
            if(isSuccess) {
                Log.e("++++++++++++++++++++++++++++++++", "++++++++++++++++getUserList++++++++++++++++" +appWidgetManager);

            } else {
                Log.e("++++++++++++++++++++++++++++++++", "++++++++++++++++false getUserList++++++++++++++++");
            }
            if(Utility.getWidgetStatusMap(context).get(appWidgetId)) {
                // RemoteViewsService 실행 등록시키는 함수
                Intent serviceIntent = new Intent(context, UserRemoteViewsService.class);
                widget.setRemoteAdapter(R.id.user_list_feedback, serviceIntent);

                widgetViewInit(context, widget, appWidgetId, null, statusMap);

                Intent intent = new Intent(context, getClass());
                intent.setAction("action.move_" + appWidgetId );
                PendingIntent pendingIntent = PendingIntent.getBroadcast(context, 0, intent, 0);
                widget.setPendingIntentTemplate(R.id.user_list_feedback, pendingIntent);

                appWidgetManager.updateAppWidget(appWidgetId, widget);
                appWidgetManager.notifyAppWidgetViewDataChanged(appWidgetId, R.id.user_list_feedback);
            }

        });
    }

    private void getFeedback(Context context, String androidId, int appWidgetId, AppWidgetManager appWidgetManager, HashMap<Integer, Boolean> statusMap) {
        DatabaseUtility.readFeedback(context, androidId, isSuccess -> {
            Log.e("++++++++++++++++++++++++++++++++", "++++++++++++++++getFeedback androidId++++++++++++++++" + androidId);
            /*if(isSuccess) {

            } else {
                Log.e("++++++++++++++++++++++++++++++++", "++++++++++++++++false getFeedback++++++++++++++++");
            }*/
            if(!Utility.getWidgetStatusMap(context).get(appWidgetId)) {
                // RemoteViewsService 실행 등록시키는 함수
                Intent serviceIntent = new Intent(context, FeedbackRemoteViewsService.class);
                widget.setRemoteAdapter(R.id.list_feedback, serviceIntent);
                widgetViewInit(context, widget, appWidgetId, androidId, statusMap);

                Intent intent = new Intent(context, getClass());
                intent.setAction("action.activity.feedback");
                PendingIntent pendingIntent = PendingIntent.getBroadcast(context, 0, intent, 0);
                widget.setPendingIntentTemplate(R.id.list_feedback, pendingIntent);

                appWidgetManager.updateAppWidget(appWidgetId, widget);
                appWidgetManager.notifyAppWidgetViewDataChanged(appWidgetId, R.id.list_feedback);
                // 제일 마지막 위치로 스크롤
                widget.setScrollPosition(R.id.list_feedback, Const.feedbackList.size() - 1);
                new Handler(Looper.getMainLooper()).postDelayed(() -> appWidgetManager.updateAppWidget(appWidgetId, widget), 1000);
            }

        });
    }

    private void widgetViewInit(Context context, RemoteViews view, int appWidgetId, String androidId, HashMap<Integer, Boolean> statusMap) {
        // 오늘 날짜
        view.setTextViewText(R.id.tv_date, Utility.getDate(Const.REPORTING_DATE_FORMAT));
        // 프로필 이미지
        view.setImageViewResource(R.id.profile_image, Utility.getResourceImage(context, Utility.getString(context, Const.SP_KEY_PROFILE)));
        // 닉네임
        view.setTextViewText(R.id.nickname, Utility.getString(context, Const.SP_KEY_NICKNAME));

        setPendingIntent(context, "action.activity.setting", view, R.id.layout_profile, null, 0);

        Gson gson = new Gson();
        Diary todayDiary = gson.fromJson(Utility.getString(context, "todayDiary"), Diary.class);
        String action = "";
        // 오늘의 기분
        if (todayDiary != null && todayDiary.getReportingDate().equals(Utility.getDate(Const.REPORTING_DATE_FORMAT))) { // detail
            view.setImageViewResource(R.id.iv_mood, Utility.getResourceImage(context, todayDiary.getIconName()));
            action = "action.activity.detailDiary";
        } else { // add
            view.setImageViewResource(R.id.iv_mood, R.drawable.circle_gray);
            action = "action.activity.NewDiary";
        }

        setPendingIntent(context, action, view, R.id.layout_diary, null, 0);

        // 오늘의 날씨
        if (Utility.getInt(context, "todayWeather") == 0) {
            view.setImageViewResource(R.id.iv_weather, R.drawable.circle_gray);
        } else {
            view.setImageViewResource(R.id.iv_weather, Utility.getInt(context, "todayWeather"));
        }

        setPendingIntent(context, "action.activity.main", view, R.id.layout_weather, null, 0);
        Log.e("++++++++++++++++++++++++++++++++", "++++++++++++++intent.putExtra+++++++++++++++++" + appWidgetId);

        StringBuilder builder = new StringBuilder();
        if (statusMap.get(appWidgetId)) {
            builder.append(ACTION_REFRESH_USER_LIST);
        } else {
            builder.append(ACTION_REFRESH_FEEDBACK_LIST);
        }
        builder.append(".");
        builder.append(androidId);
        builder.append(".");
        builder.append(appWidgetId);

        // 새로고침 버튼 동작
        setPendingIntent(context, builder.toString(), view, R.id.iv_refresh, null, 0);

        Log.e("++++++++++++++++++++++++++++++++", "++++++++++++++view change+++++++++++++++++" + statusMap.get(appWidgetId));

        if (Utility.getAndroidId(context).equals(Const.ADMIN_ANDROID_ID) && statusMap.get(appWidgetId)) {
            view.setViewVisibility(R.id.iv_back, View.GONE);
            view.setViewVisibility(R.id.user_list_feedback, View.VISIBLE);
            view.setViewVisibility(R.id.list_feedback, View.GONE);
        } else {
            if (Utility.getAndroidId(context).equals(Const.ADMIN_ANDROID_ID)) {
                view.setViewVisibility(R.id.iv_back, View.VISIBLE);

                setPendingIntent(context, "action.back" + "." + appWidgetId, view, R.id.feedback_title_bar, null, 0);
            } else {
                view.setViewVisibility(R.id.iv_back, View.GONE);
                view.setOnClickPendingIntent(R.id.feedback_title_bar, null);
            }
            view.setViewVisibility(R.id.user_list_feedback, View.GONE);
            view.setViewVisibility(R.id.list_feedback, View.VISIBLE);
        }
    }

    private void setPendingIntent(Context context, String action, RemoteViews view, int viewId, String androidId, int appWidgetId) {
        Intent intent = new Intent(context, getClass());
        intent.setAction(action);

        if (!TextUtils.isEmpty(androidId)) {
            intent.putExtra("androidId", androidId);
        }

        if (appWidgetId != 0) {
            intent.putExtra("appWidgetId", appWidgetId);
        }

        view.setOnClickPendingIntent(viewId, PendingIntent.getBroadcast(context, 0, intent, 0));
    }
}