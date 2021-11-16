package com.example.onelinediary.adapter.viewholder;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.onelinediary.activity.FeedbackActivity;
import com.example.onelinediary.constant.Const;
import com.example.onelinediary.databinding.ViewholderItemFeedbackListBinding;
import com.example.onelinediary.dto.Feedback;
import com.example.onelinediary.utiliy.DatabaseUtility;
import com.example.onelinediary.utiliy.Utility;

public class AdminFeedbackListViewHolder extends RecyclerView.ViewHolder {
    private final ViewholderItemFeedbackListBinding feedbackListBinding;

    Context context;

    public AdminFeedbackListViewHolder(@NonNull ViewholderItemFeedbackListBinding feedbackListBinding) {
        super(feedbackListBinding.getRoot());

        this.feedbackListBinding = feedbackListBinding;
        this.context = feedbackListBinding.getRoot().getContext();
    }

    @SuppressLint("SetTextI18n")
    public void onBind(String androidId, Feedback feedback) {
        // 사용자가 닉네임을 바꿀때마다 변경되도록 설정했지만
        // 처음에 가져올 때 가져오는 시간이 필요하다.
        DatabaseUtility.getNickname(androidId, (isSuccess, result) -> {
            if (isSuccess) {
                feedbackListBinding.nickname.setText(result);
            }
        });

        DatabaseUtility.getProfileImage(androidId, (isSuccess, result) -> {
            if (isSuccess) {
                feedbackListBinding.profileImage.setImageResource(Utility.getResourceImage(context, result));
            }
        });

        feedbackListBinding.feedbackLastContents.setText(feedback.getContents());
        String[] reportingDate = feedback.getReportingDate().split("\\s");
        feedbackListBinding.date.setText(reportingDate[1] + " " + reportingDate[2]);

        feedbackListBinding.userLayout.setOnClickListener(v -> {
            Intent feedbackIntent = new Intent(feedbackListBinding.getRoot().getContext(), FeedbackActivity.class);
            feedbackIntent.putExtra(Const.INTENT_KEY_ANDROID_ID, androidId);
            feedbackListBinding.getRoot().getContext().startActivity(feedbackIntent);
        });
    }
}
