package com.example.onelinediary.adapter.viewholder;

import android.content.Context;
import android.view.View;

import androidx.recyclerview.widget.RecyclerView;

import com.example.onelinediary.R;
import com.example.onelinediary.constant.Const;
import com.example.onelinediary.databinding.ViewholderMessageLeftBinding;
import com.example.onelinediary.dto.Feedback;
import com.example.onelinediary.utiliy.Utility;

public class MessageItemLeftViewHolder extends RecyclerView.ViewHolder {
    private final ViewholderMessageLeftBinding messageLeftBinding;

    Context context;

    public MessageItemLeftViewHolder(ViewholderMessageLeftBinding messageLeftBinding) {
        super(messageLeftBinding.getRoot());

        this.messageLeftBinding = messageLeftBinding;
        this.context = messageLeftBinding.getRoot().getContext();
    }

    public void onBind(boolean isSameDate, String profileImage, Feedback feedback) {
        int profileImageResId = Utility.getResourceImage(context, profileImage);

        if (feedback.getAndroidId().equals(Const.ADMIN_ANDROID_ID)) {
            messageLeftBinding.profileImage.setImageResource(R.drawable.profile_admin);
        } else {
            if (profileImageResId != 0) {
                messageLeftBinding.profileImage.setImageResource(profileImageResId);
            }
        }

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
