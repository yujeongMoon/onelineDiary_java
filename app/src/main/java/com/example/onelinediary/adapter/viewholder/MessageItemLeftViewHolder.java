package com.example.onelinediary.adapter.viewholder;

import android.view.View;

import androidx.recyclerview.widget.RecyclerView;

import com.example.onelinediary.databinding.ViewholderMessageLeftBinding;
import com.example.onelinediary.dto.Feedback;

public class MessageItemLeftViewHolder extends RecyclerView.ViewHolder {
    private final ViewholderMessageLeftBinding messageLeftBinding;

    public MessageItemLeftViewHolder(ViewholderMessageLeftBinding messageLeftBinding) {
        super(messageLeftBinding.getRoot());

        this.messageLeftBinding = messageLeftBinding;
    }

    public void onBind(boolean isSameDate, Feedback feedback) {
        if (!isSameDate) {
            messageLeftBinding.date.setVisibility(View.VISIBLE);
            messageLeftBinding.date.setText(feedback.getReportingDate());
        } else {
            messageLeftBinding.date.setVisibility(View.GONE);
        }

        messageLeftBinding.nickname.setText(feedback.getNickname());
        messageLeftBinding.message.setText(feedback.getContents());
        messageLeftBinding.time.setText(feedback.getReportingTime());
    }
}
