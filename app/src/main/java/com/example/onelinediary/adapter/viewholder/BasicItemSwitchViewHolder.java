package com.example.onelinediary.adapter.viewholder;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.onelinediary.databinding.ViewholderItemBasicSwitchBinding;
import com.example.onelinediary.dto.BasicItemSwitch;

public class BasicItemSwitchViewHolder extends RecyclerView.ViewHolder {
    private final ViewholderItemBasicSwitchBinding basicSwitchBinding;

    public BasicItemSwitchViewHolder(@NonNull ViewholderItemBasicSwitchBinding basicSwitchBinding) {
        super(basicSwitchBinding.getRoot());
        this.basicSwitchBinding = basicSwitchBinding;
    }

    public void onBind(BasicItemSwitch item) {
        basicSwitchBinding.icon.setImageResource(item.getIcon());
        basicSwitchBinding.title.setText(item.getTitle());

        // switch 초기화
        basicSwitchBinding.basicSwitch.setChecked(item.isEnabled());

        basicSwitchBinding.basicSwitch.setOnCheckedChangeListener(item.getListener());
    }
}
