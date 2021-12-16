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
public class AppWidgetTest extends AppWidgetProvider {
    private final String INTENT_KEY_APP_WIDGET_ID = "appWidgetId";

    private final String ACTION_REFRESH_USER_LIST = "userList";
    private final String ACTION_REFRESH_FEEDBACK_LIST = "feedbackList";

    private final String ACTION_MOVE = "action.move";
    private final String ACTION_BACK = "action.back";

    private final String ACTION_ACTIVITY = "action.activity";
    private final String ACTION_ACTIVITY_MAIN = "action.activity.main";
    private final String ACTION_ACTIVITY_SETTING = "action.activity.setting";
    private final String ACTION_ACTIVITY_FEEDBACK = "action.activity.feedback";
    private final String ACTION_ACTIVITY_DETAIL_DIARY = "action.activity.detailDiary";
    private final String ACTION_ACTIVITY_NEW_DIARY = "action.activity.NewDiary";
    public static final String ACTION_NOTIFICATION_REPLY = "action.notification.reply";

    private RemoteViews widget;

    // 위젯이 설치될 때마다 호출
    @Override
    public void onUpdate(Context context, AppWidgetManager appWidgetManager, int[] appWidgetIds) {
        HashMap<Integer, Boolean> statusMap = Utility.getWidgetStatusMap(context);
        for (int appWidgetId : appWidgetIds) {
            // proference에 위젯 아이디가 존재하는지 체크한다.
            // 위젯을 처음 생성하거나 새로 추가된 위젯을 경우, 초기값을 설정한다.
            if(statusMap.size() == 0 || !statusMap.containsKey(appWidgetId)) {
                // statusMap.put(앱 위젯 아이디, 유저 리스트 화면 노출 여부);
                // 관리자 일 경우, 유저 리스트 화면을 먼저 보여줘야하기 때문에 true로 설정해준다.
                if (Utility.getAndroidId(context).equals(Const.ADMIN_ANDROID_ID)) {
                    statusMap.put(appWidgetId, true);
                } else {
                    statusMap.put(appWidgetId, false);
                }
                // proference에 위젯 아이디와 화면 노출 여부를 저장한다.
                Utility.putWidgetStatus(context, statusMap);
            }

            // 위젯을 새로 업데이트한다.
            updateAppWidget(context, appWidgetManager, appWidgetId, statusMap, null);
        }

        super.onUpdate(context, appWidgetManager, appWidgetIds);
    }

    // 위젯의 크기 및 변경될 때마다 호출
    private void updateAppWidget(Context context, AppWidgetManager appWidgetManager,
                                 int appWidgetId, HashMap<Integer, Boolean> statusMap, String androidId) {
        widget = new RemoteViews(context.getPackageName(), R.layout.app_widget);

        if (Utility.getAndroidId(context).equals(Const.ADMIN_ANDROID_ID) && statusMap.get(appWidgetId)) { // 관리자
            // 위의 조건을 통과했다는 것은 관리자이고 유저 리스트 화면이 보이는 상태이기 때문에
            // 새로 상태 값을 저장한다.
            putWidgetStatus(context, appWidgetId, true, statusMap);
            // 유저 리스트를 불러온다.
            getUserList(context, appWidgetId, appWidgetManager, statusMap);
        } else { // 사용자
            putWidgetStatus(context, appWidgetId, false, statusMap);

            String andId = Utility.getAndroidId(context);
            if (!TextUtils.isEmpty(androidId)) {
                andId = androidId;
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
    }

    // 위젯의 마지막 인스턴스가 제거될때 호출
    @Override
    public void onDisabled(Context context) {
        // 위젯이 전체가 삭제되면 preference에 저장된 상태 값을 삭제한다.
        Utility.clearWidgetStatusMap(context);
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        final String action = intent.getAction();
        AppWidgetManager mgr = AppWidgetManager.getInstance(context);
        ComponentName cn = new ComponentName(context, AppWidgetTest.class);

        HashMap<Integer, Boolean> statusMap = Utility.getWidgetStatusMap(context);

        // 위젯이 업데이트 되었을 때
        if (action.equals(AppWidgetManager.ACTION_APPWIDGET_UPDATE)) {
            // refresh all your widgets
//            AppWidgetManager mgr = AppWidgetManager.getInstance(context);
//            ComponentName cn = new ComponentName(context, AppWidget.class);
//            if (Utility.getAndroidId(context).equals(Const.ADMIN_ANDROID_ID) && Utility.getBoolean(context, "isShowUserList", true)) {
//                mgr.notifyAppWidgetViewDataChanged(mgr.getAppWidgetIds(cn), R.id.user_list_feedback);
//            } else {
//                mgr.notifyAppWidgetViewDataChanged(mgr.getAppWidgetIds(cn), R.id.list_feedback);
//            }
            // 피드백 화면에서 새로고침 버튼을 눌렀을 때
        } else if (action.contains(ACTION_REFRESH_FEEDBACK_LIST)) {
            String androidId = intent.getStringExtra(Const.INTENT_KEY_ANDROID_ID);
            int appWidgetId = intent.getIntExtra(INTENT_KEY_APP_WIDGET_ID, 0);
            updateAppWidget(context, mgr, appWidgetId, statusMap, androidId);
            // 유저 리스트 화면에서 새로고침 버튼을 눌렀을 때
        } else if (action.contains(ACTION_REFRESH_USER_LIST)) {
            String androidId = intent.getStringExtra(Const.INTENT_KEY_ANDROID_ID);
            int appWidgetId = intent.getIntExtra(INTENT_KEY_APP_WIDGET_ID, 0);
            updateAppWidget(context, mgr, appWidgetId, statusMap, androidId);
            // 유저 리스트에서 한 아이템을 클릭했을 때
        } else if (action.contains(ACTION_MOVE)) {
            int appWidgetId = intent.getIntExtra(INTENT_KEY_APP_WIDGET_ID, 0);
            putWidgetStatus(context, appWidgetId, false, statusMap);
            updateAppWidget(context, mgr, appWidgetId, statusMap, intent.getStringExtra(Const.INTENT_KEY_ANDROID_ID));
            // 피드백 화면에서 뒤로가기 버튼을 눌렀을 때
        } else if (action.contains(ACTION_BACK)) {
            int appWidgetId = intent.getIntExtra(INTENT_KEY_APP_WIDGET_ID, 0);
            putWidgetStatus(context, appWidgetId, true, statusMap);
            new Handler(Looper.getMainLooper()).postDelayed(() -> updateAppWidget(context, mgr, appWidgetId, statusMap, null), 500);
            // 앱을 실행시킬 때
            // 액션에 따라 액티비티의 이름을 넘겨준다.
        } else if (action.contains(ACTION_ACTIVITY)) {
            Intent intent1 = new Intent(context, SplashActivity.class);
            intent1.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP|Intent.FLAG_ACTIVITY_NEW_TASK);

            if (action.equals(ACTION_ACTIVITY_SETTING)) {
                intent1.putExtra(Const.INTENT_KEY_MOVE_ACTIVITY, Const.ACTIVITY_TYPE_SETTING);
            } else if (action.equals(ACTION_ACTIVITY_FEEDBACK)) {
                intent1.putExtra(Const.INTENT_KEY_MOVE_ACTIVITY, Const.ACTIVITY_TYPE_FEEDBACK);
                intent1.putExtra(Const.INTENT_KEY_ANDROID_ID, intent.getStringExtra(Const.INTENT_KEY_ANDROID_ID));
            } else if (action.equals(ACTION_ACTIVITY_DETAIL_DIARY)) {
                intent1.putExtra(Const.INTENT_KEY_MOVE_ACTIVITY, Const.ACTIVITY_TYPE_DETAIL);
            } else if (action.equals(ACTION_ACTIVITY_NEW_DIARY)) {
                intent1.putExtra(Const.INTENT_KEY_MOVE_ACTIVITY, Const.ACTIVITY_TYPE_New);
            }

            context.startActivity(intent1);
        } else if (action.equals(ACTION_NOTIFICATION_REPLY)) {
            Bundle remoteInput = RemoteInput.getResultsFromIntent(intent);
            String message = "";
            if (remoteInput != null) {
                message = remoteInput.getCharSequence(Const.KEY_TEXT_REPLY).toString();
            }

            if (!TextUtils.isEmpty(message)) {
                Feedback feedback = new Feedback();
                feedback.setNickname(Utility.getString(context.getApplicationContext(), Const.SP_KEY_NICKNAME));
                feedback.setAndroidId(Utility.getAndroidId(context));
                feedback.setReportingDate(Utility.getDateWithDayOfWeek("yyyy년 MM월 d일"));
                feedback.setReportingTime(Utility.getTime_a_hh_mm());
                feedback.setContents(message);
                feedback.setAdmin(Utility.getAndroidId(context).equals(Const.ADMIN_ANDROID_ID));
                feedback.setUserAndroidId(intent.getStringExtra(Const.INTENT_KEY_USER_ANDROID_ID));
                feedback.setProfileImageName(intent.getStringExtra(Const.INTENT_KEY_USER_PROFILE_IMAGE_NAME));
                feedback.setUserNickname(intent.getStringExtra(Const.INTENT_KEY_USER_NICKNAME));

                DatabaseUtility.writeFeedback(intent.getStringExtra(Const.INTENT_KEY_USER_ANDROID_ID), feedback, isSuccess -> {
                    if (isSuccess) {
                        // 관리자 화면에서 보여주기 위한 마지막 피드백 리스트에 추가
                        // 마지막에 작성한 사람이 관리자일 경우에 관리자의 메세지 저장
                        if (!feedback.getContents().equals("")) {
                            DatabaseUtility.addFeedbackListWithUser(intent.getStringExtra(Const.INTENT_KEY_USER_ANDROID_ID), feedback, null);
                        }
                    }
                });
            }

            // 현재 떠있는 알림을 삭제한다.
            NotificationManager manager = (NotificationManager)context.getSystemService(Context.NOTIFICATION_SERVICE);
            manager.cancel(0);
        }

        super.onReceive(context, intent);
    }


    private void getUserList(Context context, int appWidgetId, AppWidgetManager appWidgetManager, HashMap<Integer, Boolean> statusMap) {
        DatabaseUtility.readFeedbackListWithUser(context, isSuccess -> {
            // 유저 리스트 화면 노출 여부가 true일 때(관리자만 보는 화면)
            if(isSuccess && Utility.getWidgetStatusMap(context).get(appWidgetId)) {
                // RemoteViewsService 실행 등록시키는 함수
                Intent serviceIntent = new Intent(context, UserRemoteViewsService.class);
                widget.setRemoteAdapter(R.id.user_list_feedback, serviceIntent);

                widgetViewInit(context, widget, appWidgetId, null, statusMap);

                Intent intent = new Intent(context, getClass());
                intent.setAction(ACTION_MOVE);
                intent.putExtra(INTENT_KEY_APP_WIDGET_ID, appWidgetId);
                PendingIntent pendingIntent = PendingIntent.getBroadcast(context, 0, intent, PendingIntent.FLAG_CANCEL_CURRENT);
                widget.setPendingIntentTemplate(R.id.user_list_feedback, pendingIntent);

                appWidgetManager.updateAppWidget(appWidgetId, widget);
                appWidgetManager.notifyAppWidgetViewDataChanged(appWidgetId, R.id.user_list_feedback);
            }
        });
    }

    private void getFeedback(Context context, String androidId, int appWidgetId, AppWidgetManager appWidgetManager, HashMap<Integer, Boolean> statusMap) {
        DatabaseUtility.readFeedback(context, androidId, isSuccess -> {
            // 유저 리스트 화면 노출 여부가 false일 때
            if(isSuccess && !Utility.getWidgetStatusMap(context).get(appWidgetId)) {
                // RemoteViewsService 실행 등록시키는 함수
                Intent serviceIntent = new Intent(context, FeedbackRemoteViewsService.class);
                widget.setRemoteAdapter(R.id.list_feedback, serviceIntent);
                widgetViewInit(context, widget, appWidgetId, androidId, statusMap);

                Intent intent = new Intent(context, getClass());
                intent.setAction(ACTION_ACTIVITY_FEEDBACK);
                PendingIntent pendingIntent = PendingIntent.getBroadcast(context, 0, intent, PendingIntent.FLAG_CANCEL_CURRENT);
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

        // 사용자 정보 레이아웃을 클릭했을 때, 실행되는 인텐트
        setPendingIntent(context, ACTION_ACTIVITY_SETTING, view, R.id.layout_profile, null, 0, PendingIntent.FLAG_CANCEL_CURRENT);

        // preference에 저장된 오늘의 일기 불러오기
        Gson gson = new Gson();
        Diary todayDiary = gson.fromJson(Utility.getString(context, Const.SP_KEY_TODAY_DIARY), Diary.class);
        String action = "";
        // 오늘의 기분
        if (todayDiary != null && todayDiary.getReportingDate().equals(Utility.getDate(Const.REPORTING_DATE_FORMAT))) { // detail
            view.setImageViewResource(R.id.iv_mood, Utility.getResourceImage(context, todayDiary.getIconName()));
            action = ACTION_ACTIVITY_DETAIL_DIARY;
        } else { // add
            view.setImageViewResource(R.id.iv_mood, R.drawable.circle_gray);
            action = ACTION_ACTIVITY_NEW_DIARY;
        }

        setPendingIntent(context, action, view, R.id.layout_diary, null, 0, PendingIntent.FLAG_CANCEL_CURRENT);

        // 오늘의 날씨
        if (Utility.getInt(context, Const.SP_KEY_TODAY_WEATHER) == 0) {
            view.setImageViewResource(R.id.iv_weather, R.drawable.circle_gray);
        } else {
            view.setImageViewResource(R.id.iv_weather, Utility.getInt(context, Const.SP_KEY_TODAY_WEATHER));
        }

        setPendingIntent(context, ACTION_ACTIVITY_MAIN, view, R.id.layout_weather, null, 0, PendingIntent.FLAG_CANCEL_CURRENT);

        String refreshAction;
        if (statusMap.get(appWidgetId)) {
            refreshAction = ACTION_REFRESH_USER_LIST;
        } else {
            refreshAction = ACTION_REFRESH_FEEDBACK_LIST;
        }

        // 새로고침 버튼 동작
        setPendingIntent(context, refreshAction, view, R.id.iv_refresh, androidId, appWidgetId, PendingIntent.FLAG_CANCEL_CURRENT);

        if (Utility.getAndroidId(context).equals(Const.ADMIN_ANDROID_ID) && statusMap.get(appWidgetId)) {
            view.setViewVisibility(R.id.iv_back, View.GONE);
            view.setViewVisibility(R.id.user_list_feedback, View.VISIBLE);
            view.setViewVisibility(R.id.list_feedback, View.GONE);
        } else {
            if (Utility.getAndroidId(context).equals(Const.ADMIN_ANDROID_ID)) {
                view.setViewVisibility(R.id.iv_back, View.VISIBLE);

                setPendingIntent(context, ACTION_BACK, view, R.id.feedback_title_bar, null, appWidgetId, PendingIntent.FLAG_CANCEL_CURRENT);
            } else {
                view.setViewVisibility(R.id.iv_back, View.GONE);
                view.setOnClickPendingIntent(R.id.feedback_title_bar, null);
            }
            view.setViewVisibility(R.id.user_list_feedback, View.GONE);
            view.setViewVisibility(R.id.list_feedback, View.VISIBLE);
        }
    }

    private void setPendingIntent(Context context, String action, RemoteViews view, int viewId, String androidId, int appWidgetId, int intentFlag) {
        Intent intent = new Intent(context, getClass());
        intent.setAction(action);

        if (!TextUtils.isEmpty(androidId)) {
            intent.putExtra(Const.INTENT_KEY_ANDROID_ID, androidId);
        }

        if (appWidgetId != 0) {
            intent.putExtra(INTENT_KEY_APP_WIDGET_ID, appWidgetId);
        }

        view.setOnClickPendingIntent(viewId, PendingIntent.getBroadcast(context, 0, intent, intentFlag));
    }
}