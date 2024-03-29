package com.example.onelinediary.adapter;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.onelinediary.adapter.viewholder.MessageItemLeftViewHolder;
import com.example.onelinediary.adapter.viewholder.MessageItemRightViewHolder;
import com.example.onelinediary.constant.Const;
import com.example.onelinediary.databinding.ViewholderMessageLeftBinding;
import com.example.onelinediary.databinding.ViewholderMessageRightBinding;
import com.example.onelinediary.dto.Feedback;
import com.example.onelinediary.utiliy.Utility;

import java.util.ArrayList;

public class FeedbackAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    public enum itemType {
        MESSAGE_LEFT(0), MESSAGE_RIGHT(1);

        public final int value;
        itemType(int value) { this.value = value; }
        public int getValue() { return value; }
    }

    Context context;
    ArrayList<Feedback> feedbackList = new ArrayList<>();

    String leftProfileImage = "";

    public FeedbackAdapter() {}

    public FeedbackAdapter(Context context) {
        this.context = context;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        if (viewType == itemType.MESSAGE_LEFT.value) {
            ViewholderMessageLeftBinding messageLeftBinding = ViewholderMessageLeftBinding.inflate(LayoutInflater.from(parent.getContext()), parent, false);
            return new MessageItemLeftViewHolder(messageLeftBinding);
        } else {
            ViewholderMessageRightBinding messageRightBinding = ViewholderMessageRightBinding.inflate(LayoutInflater.from(parent.getContext()), parent, false);
            return new MessageItemRightViewHolder(messageRightBinding);
        }
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        boolean isSameDate;

        if (position <= 0) {
            isSameDate = false;
        } else {
            isSameDate = feedbackList.get(position - 1).getReportingDate().equals(feedbackList.get(position).getReportingDate());
        }

        if (feedbackList.get(position).getAndroidId().equals(Utility.getAndroidId(this.context))) {
            ((MessageItemRightViewHolder)holder).onBind(isSameDate, feedbackList.get(position));
        } else {
            ((MessageItemLeftViewHolder)holder).onBind(isSameDate, this.leftProfileImage, feedbackList.get(position));
        }
    }

    @Override
    public int getItemCount() {
        return feedbackList.size();
    }

    @Override
    public int getItemViewType(int position) {
        // 작성자의 안드로이드 아이디와 기기에 저장된 안드로이드 아이디가 같을 경우, 내가 보낸 메세지이기 때문에 오른쪽으로 메세지 박스를 보여준다.
        if (feedbackList.get(position).getAndroidId().equals(Utility.getString(context, Const.SP_KEY_ANDROID_ID))) {
            return itemType.MESSAGE_RIGHT.value;
        } else {
            return itemType.MESSAGE_LEFT.value;
        }
    }

    @SuppressLint("NotifyDataSetChanged")
    public void addFeedback(Feedback feedback) {
        feedbackList.add(feedback);
        notifyItemChanged(feedbackList.size() - 1, feedback);
    }

    @SuppressLint("NotifyDataSetChanged")
    public void addFeedbackList(ArrayList<Feedback> feedbackList) {
        resetFeedbackList();
        this.feedbackList.addAll(feedbackList);
        notifyDataSetChanged();
    }

    public void resetFeedbackList() {
        this.feedbackList.clear();
    }

    @SuppressLint("NotifyDataSetChanged")
    public void setLeftProfileImage(String profileImage) {
        this.leftProfileImage = profileImage;
        notifyDataSetChanged();
    }

    public String getLeftProfileImage() {
        return this.leftProfileImage;
    }
}
