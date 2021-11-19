package com.example.onelinediary.adapter;

import android.annotation.SuppressLint;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.onelinediary.adapter.viewholder.NoticeViewHolder;
import com.example.onelinediary.databinding.ViewholderNoticeBinding;
import com.example.onelinediary.dto.ItemNotice;
import com.example.onelinediary.dto.Notice;

import java.util.ArrayList;
import java.util.Collections;

public class NoticeAdapter extends RecyclerView.Adapter<NoticeViewHolder> {
    private ViewholderNoticeBinding binding;
    
    ArrayList<ItemNotice> noticeList = new ArrayList<>();

    public ListAdapter.onItemClickListenerWithPosition<Integer> listener;

    public interface onItemClickListener {
        void onItemClick();
    }

    public interface onItemClickListenerWithPosition<T> {
        void onItemClick(T position);
    }
    
    @NonNull
    @Override
    public NoticeViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        binding = ViewholderNoticeBinding.inflate(LayoutInflater.from(parent.getContext()), parent, false);
        return new NoticeViewHolder(this, binding);
    }

    @Override
    public void onBindViewHolder(@NonNull NoticeViewHolder holder, int position) {
        holder.onBind(position, noticeList.get(position));
    }

    @Override
    public int getItemCount() {
        return noticeList.size();
    }

    @SuppressLint("NotifyDataSetChanged")
    public void addItem(Notice notice) {
        noticeList.add(new ItemNotice(notice, false));
    }

    public void addItemNoticeList(ArrayList<ItemNotice> noticeList) {
        this.noticeList.addAll(noticeList);
    }

    public void updateItemWithNotify(int position, Notice notice) {
        noticeList.set(position, new ItemNotice(notice, false));
        // 포지션과 객체를 같이 넘기는 notifyItemChanged()를 사용하면 애니메이션 없이 해당 위치의 아이템을 변경할 수 있다.
        this.notifyItemChanged(position, new ItemNotice(notice, false));
    }

    public void updateItem(int position, ItemNotice itemNotice) {
        noticeList.set(position, itemNotice);
    }

    public void resetNoticeList() {
        noticeList.clear();
    }

    @SuppressLint("NotifyDataSetChanged")
    public void notifyNoticeListChanged() {
        Collections.reverse(noticeList);
        this.notifyDataSetChanged();
    }

    public void setListenerWithPosition(ListAdapter.onItemClickListenerWithPosition<Integer> listener) {
        this.listener = listener;
    }
}
