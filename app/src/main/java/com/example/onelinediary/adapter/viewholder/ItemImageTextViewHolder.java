package com.example.onelinediary.adapter.viewholder;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.onelinediary.databinding.ViewholderItemImageTextBinding;
import com.example.onelinediary.dto.ImageTextItem;

public class ItemImageTextViewHolder extends RecyclerView.ViewHolder {
    private final ViewholderItemImageTextBinding imageTextBinding;

    public ItemImageTextViewHolder(@NonNull ViewholderItemImageTextBinding imageTextBinding) {
        super(imageTextBinding.getRoot());
        this.imageTextBinding = imageTextBinding;
    }

    public void onBind(ImageTextItem item) {
        imageTextBinding.profileImage.setImageResource(item.getProfileImage());
        imageTextBinding.profileImage.setOnClickListener(item.getImageClickListener());

        imageTextBinding.nickname.setText(item.getNickname());
        imageTextBinding.nickname.setOnClickListener(item.getTextClickListener());
    }
}
