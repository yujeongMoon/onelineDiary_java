package com.example.onelinediary.adapter.viewholder;

import android.content.Intent;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.onelinediary.activity.FeedbackActivity;
import com.example.onelinediary.constant.Const;
import com.example.onelinediary.databinding.ViewholderItemFeedbackListBinding;
import com.example.onelinediary.dto.Feedback;

public class AdminFeedbackListViewHolder extends RecyclerView.ViewHolder {
    private ViewholderItemFeedbackListBinding feedbackListBinding;

    public AdminFeedbackListViewHolder(@NonNull ViewholderItemFeedbackListBinding feedbackListBinding) {
        super(feedbackListBinding.getRoot());

        this.feedbackListBinding = feedbackListBinding;
    }

    public void onBind(String androidId, Feedback feedback) {
        if (feedback.getNickname().equals("")) {
            feedbackListBinding.nickname.setText("사용자");
        } else {
            feedbackListBinding.nickname.setText(feedback.getNickname());
        }

        feedbackListBinding.feedbackLastContents.setText(feedback.getContents());
        feedbackListBinding.date.setText("11월 5일");

        feedbackListBinding.userLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent feedbackIntent = new Intent(feedbackListBinding.getRoot().getContext(), FeedbackActivity.class);
                feedbackIntent.putExtra("androidId", androidId);
                feedbackListBinding.getRoot().getContext().startActivity(feedbackIntent);
            }
        });
    }
}
