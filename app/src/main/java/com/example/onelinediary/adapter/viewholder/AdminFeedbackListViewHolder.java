package com.example.onelinediary.adapter.viewholder;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.onelinediary.activity.FeedbackActivity;
import com.example.onelinediary.constant.Const;
import com.example.onelinediary.databinding.ViewholderItemFeedbackListBinding;
import com.example.onelinediary.dto.Feedback;

public class AdminFeedbackListViewHolder extends RecyclerView.ViewHolder {
    private final ViewholderItemFeedbackListBinding feedbackListBinding;

    public AdminFeedbackListViewHolder(@NonNull ViewholderItemFeedbackListBinding feedbackListBinding) {
        super(feedbackListBinding.getRoot());

        this.feedbackListBinding = feedbackListBinding;
    }

    @SuppressLint("SetTextI18n")
    public void onBind(String androidId, Feedback feedback) {
        feedbackListBinding.nickname.setText(feedback.getNickname());

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
