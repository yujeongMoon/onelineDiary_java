package com.example.onelinediary.adapter;

import android.annotation.SuppressLint;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.onelinediary.adapter.viewholder.AdminFeedbackListViewHolder;
import com.example.onelinediary.databinding.ViewholderItemFeedbackListBinding;
import com.example.onelinediary.dto.Feedback;

import java.util.ArrayList;

public class AdminAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    public ArrayList<String> userList = new ArrayList<>();
    public ArrayList<Feedback> userLastFeedbackList = new ArrayList<>();

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        ViewholderItemFeedbackListBinding feedbackListBinding = ViewholderItemFeedbackListBinding.inflate(LayoutInflater.from(parent.getContext()), parent, false);
        return new AdminFeedbackListViewHolder(feedbackListBinding);
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        ((AdminFeedbackListViewHolder)holder).onBind(userList.get(position), userLastFeedbackList.get(position));
    }

    @Override
    public int getItemCount() {
        return userList.size();
    }

    @SuppressLint("NotifyDataSetChanged")
    public void addUserList(ArrayList<String> userList) {
        this.userList.clear();
        this.userList.addAll(userList);
        notifyDataSetChanged();
    }

    @SuppressLint("NotifyDataSetChanged")
    public void adduserLastFeedbackList(ArrayList<Feedback> userLastFeedbackList) {
        this.userLastFeedbackList.clear();
        this.userLastFeedbackList.addAll(userLastFeedbackList);
        notifyDataSetChanged();
    }
}
