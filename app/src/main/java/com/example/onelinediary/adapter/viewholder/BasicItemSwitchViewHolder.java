package com.example.onelinediary.adapter.viewholder;

import android.widget.CompoundButton;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.onelinediary.databinding.ViewholderItemBasicBtnBinding;
import com.example.onelinediary.databinding.ViewholderItemBasicSwitchBinding;
import com.example.onelinediary.dto.BasicItemBtn;
import com.example.onelinediary.dto.BasicItemSwitch;

public class BasicItemSwitchViewHolder extends RecyclerView.ViewHolder {
    private ViewholderItemBasicSwitchBinding basicSwitchBinding;

    public BasicItemSwitchViewHolder(@NonNull ViewholderItemBasicSwitchBinding binding) {
        super(binding.getRoot());
        basicSwitchBinding = binding;
    }

    public void onBind(BasicItemSwitch item) {
        basicSwitchBinding.icon.setImageResource(item.getIcon());
        basicSwitchBinding.title.setText(item.getTitle());

        // switch 초기화
        if (item.isEnabled()) {
            basicSwitchBinding.basicSwitch.setChecked(true);
        } else {
            basicSwitchBinding.basicSwitch.setChecked(false);
        }

        basicSwitchBinding.basicSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {

            }
        });
    }
}
