package com.example.onelinediary.activity;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.view.View;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.example.onelinediary.R;
import com.example.onelinediary.adapter.NoticeAdapter;
import com.example.onelinediary.constant.Const;
import com.example.onelinediary.databinding.ActivityNoticeListBinding;
import com.example.onelinediary.dto.ItemNotice;
import com.example.onelinediary.dto.Notice;
import com.example.onelinediary.utiliy.DatabaseUtility;
import com.example.onelinediary.utiliy.Utility;
import com.google.gson.Gson;

import java.util.ArrayList;

public class NoticeListActivity extends AppCompatActivity {
    private ActivityNoticeListBinding noticeListBinding;

    NoticeAdapter adapter;

    static String noticeAction = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        noticeListBinding = ActivityNoticeListBinding.inflate(getLayoutInflater());
        setContentView(noticeListBinding.getRoot());

        Utility.putBoolean(getApplicationContext(), Const.SP_KEY_NOTIFICATION_IN, true);

        noticeListBinding.noticeRecyclerview.setLayoutManager(new LinearLayoutManager(this));

        adapter = new NoticeAdapter();
        noticeListBinding.noticeRecyclerview.setAdapter(adapter);

        if (adapter != null) {
            DatabaseUtility.getNoticeListAll((isSuccess, result) -> { // result => HashMap<String, ArrayList<ItemNotice>>
                if (isSuccess) {
                    adapter.resetNoticeList();
                    for (String key : result.keySet()) { // key : 연도
                        ArrayList<ItemNotice> noticeList = result.get(key);
                        if (noticeList != null) {
                            adapter.addItemNoticeList(noticeList);

                            if (Utility.getYear().equals(key)) {
                                if(!Utility.getBoolean(getApplicationContext(), Const.SP_KEY_NOTIFICATION_IN, true)) {
                                    noticeNotify(noticeList.get(noticeList.size() - 1).getNotice());
                                }
                            }
                        }
                    }
                    adapter.notifyNoticeListChanged();
                }
            });
        }

        if (Utility.getAndroidId(this).equals(Const.ADMIN_ANDROID_ID)) {
            noticeListBinding.noticeAddLayout.setVisibility(View.VISIBLE);
            noticeListBinding.noticeAddLayout.setOnClickListener(addNotice);
        } else {
            noticeListBinding.noticeAddLayout.setVisibility(View.GONE);
            noticeListBinding.noticeAddLayout.setOnClickListener(null);
        }
    }

    View.OnClickListener addNotice = v -> {
        Intent addNoticeIntent = new Intent(NoticeListActivity.this, WriteNoticeActivity.class);
        addNoticeIntent.putExtra(Const.INTENT_KEY_ADD_NEW_NOTICE, true);
        startActivity(addNoticeIntent);
    };


    public void noticeNotify(Notice notice) {
        Intent intent = new Intent(this, SplashActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP|Intent.FLAG_ACTIVITY_SINGLE_TOP);
        intent.putExtra(Const.INTENT_KEY_MOVE_ACTIVITY, Const.ACTIVITY_TYPE_NOTICE_LIST);

        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, intent, PendingIntent.FLAG_ONE_SHOT);

        NotificationCompat.Builder builder = new NotificationCompat.Builder(this, "channelId1")
                .setSmallIcon(R.drawable.notice_black_24)
                .setContentTitle(notice.getTitle())
                .setContentText(notice.getContents())
                .setContentIntent(pendingIntent)
                .setAutoCancel(true)
                .setPriority(NotificationCompat.PRIORITY_HIGH);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            int importance = NotificationManager.IMPORTANCE_HIGH;
            NotificationChannel channel = new NotificationChannel("channelId1", "공지사항 채널", importance);
            NotificationManager notificationManager = getSystemService(NotificationManager.class);
            notificationManager.createNotificationChannel(channel);
        }

        NotificationManagerCompat notificationManager = NotificationManagerCompat.from(this);

        notificationManager.notify(1, builder.build());
    }

    @Override
    protected void onResume() {
        Utility.putBoolean(getApplicationContext(), Const.SP_KEY_NOTIFICATION_IN, true);
        super.onResume();
    }

    @Override
    protected void onPause() {
        Utility.putBoolean(getApplicationContext(), Const.SP_KEY_NOTIFICATION_IN, false);
        super.onPause();
    }

    @Override
    protected void onDestroy() {
        Utility.putBoolean(getApplicationContext(), Const.SP_KEY_NOTIFICATION_IN, false);
        super.onDestroy();
    }
}