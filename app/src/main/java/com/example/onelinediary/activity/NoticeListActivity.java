package com.example.onelinediary.activity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.example.onelinediary.adapter.NoticeAdapter;
import com.example.onelinediary.constant.Const;
import com.example.onelinediary.databinding.ActivityNoticeListBinding;
import com.example.onelinediary.dto.ItemNotice;
import com.example.onelinediary.utiliy.DatabaseUtility;
import com.example.onelinediary.utiliy.Utility;

import java.util.ArrayList;

public class NoticeListActivity extends AppCompatActivity {
    private ActivityNoticeListBinding noticeListBinding;

    NoticeAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        noticeListBinding = ActivityNoticeListBinding.inflate(getLayoutInflater());
        setContentView(noticeListBinding.getRoot());

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
}