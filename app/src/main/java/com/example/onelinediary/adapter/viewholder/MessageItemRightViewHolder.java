package com.example.onelinediary.adapter.viewholder;

import android.view.View;

import androidx.recyclerview.widget.RecyclerView;

import com.example.onelinediary.databinding.ViewholderMessageRightBinding;
import com.example.onelinediary.dto.Feedback;

public class MessageItemRightViewHolder extends RecyclerView.ViewHolder {
    private ViewholderMessageRightBinding messageRightBinding;

    public MessageItemRightViewHolder(ViewholderMessageRightBinding messageRightBinding) {
        super(messageRightBinding.getRoot());

        this.messageRightBinding = messageRightBinding;
    }

    public void onBind(boolean isSameDate, Feedback feedback) {
        if (!isSameDate) {
            messageRightBinding.date.setVisibility(View.VISIBLE);
            messageRightBinding.date.setText(feedback.getReportingDate());
        } else {
            messageRightBinding.date.setVisibility(View.GONE);
        }

        messageRightBinding.message.setText(feedback.getContents());
        messageRightBinding.time.setText(feedback.getReportingTime());
    }
}
