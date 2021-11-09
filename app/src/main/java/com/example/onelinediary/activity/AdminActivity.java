package com.example.onelinediary.activity;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;

import android.os.Bundle;

import com.example.onelinediary.adapter.AdminAdapter;
import com.example.onelinediary.constant.Const;
import com.example.onelinediary.databinding.ActivityAdminBinding;
import com.example.onelinediary.utiliy.DatabaseUtility;

public class AdminActivity extends AppCompatActivity {
    private ActivityAdminBinding adminBinding;

    AdminAdapter adminAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        adminBinding = ActivityAdminBinding.inflate(getLayoutInflater());
        setContentView(adminBinding.getRoot());

        adminBinding.feedbackListRecyclerview.setLayoutManager(new LinearLayoutManager(this));
        adminAdapter = new AdminAdapter();
        adminBinding.feedbackListRecyclerview.setAdapter(adminAdapter);

        // admin/feedbackList에 저장된 마지막 피드백 리스트를 가져온다.
        // 해당 위치의 값들이 바뀔때마다 호출된다.
        DatabaseUtility.readFeedbackListWithUser(isSuccess -> {
            adminAdapter.addUserList(Const.userList);
            adminAdapter.adduserLastFeedbackList(Const.userLastFeedbackList);
        });
    }
}