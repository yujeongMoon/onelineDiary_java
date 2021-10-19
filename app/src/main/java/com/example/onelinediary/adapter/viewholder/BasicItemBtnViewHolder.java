package com.example.onelinediary.adapter.viewholder;

import android.view.View;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.onelinediary.databinding.ViewholderItemBasicBtnBinding;
import com.example.onelinediary.dto.BasicItemBtn;

public class BasicItemBtnViewHolder extends RecyclerView.ViewHolder {
    private ViewholderItemBasicBtnBinding basicBtnBinding;

    public BasicItemBtnViewHolder(@NonNull ViewholderItemBasicBtnBinding binding) {
        super(binding.getRoot());
        basicBtnBinding = binding;
    }

    public void onBind(BasicItemBtn item) {
        basicBtnBinding.icon.setImageResource(item.getIcon());
        basicBtnBinding.title.setText(item.getTitle());
        basicBtnBinding.txtOnClick.setText(item.getButtonText());
        basicBtnBinding.txtOnClick.setOnClickListener(item.getListener());
    }
}
