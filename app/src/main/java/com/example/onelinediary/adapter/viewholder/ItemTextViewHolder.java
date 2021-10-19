package com.example.onelinediary.adapter.viewholder;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.onelinediary.databinding.ViewholderItemBasicBtnBinding;
import com.example.onelinediary.databinding.ViewholderItemTextBinding;
import com.example.onelinediary.dto.BasicItemBtn;
import com.example.onelinediary.dto.TextItem;

public class ItemTextViewHolder extends RecyclerView.ViewHolder {
    private ViewholderItemTextBinding textBinding;

    public ItemTextViewHolder(@NonNull ViewholderItemTextBinding binding) {
        super(binding.getRoot());
        textBinding = binding;
    }

    public void onBind(TextItem item) {
        textBinding.noticeContents.setText(item.getContents());
    }
}
