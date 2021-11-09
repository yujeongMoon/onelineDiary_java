package com.example.onelinediary.adapter.viewholder;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.onelinediary.databinding.ViewholderItemTextBinding;
import com.example.onelinediary.dto.TextItem;

public class ItemTextViewHolder extends RecyclerView.ViewHolder {
    private final ViewholderItemTextBinding textBinding;

    public ItemTextViewHolder(@NonNull ViewholderItemTextBinding textBinding) {
        super(textBinding.getRoot());
        this.textBinding = textBinding;
    }

    public void onBind(TextItem item) {
        textBinding.noticeContents.setText(item.getContents());
    }
}
