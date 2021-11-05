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

        DatabaseUtility.readFeedbackListWithUser(new DatabaseUtility.onCompleteCallback() {
            @Override
            public void onComplete(boolean isSuccess) {
                adminAdapter.addUserList(Const.userList);
                adminAdapter.adduserLastFeedbackList(Const.userLastFeedbackList);
            }
        });
    }
}