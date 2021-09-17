package com.example.onelinediary.adapter.viewholder;

import android.annotation.SuppressLint;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.onelinediary.adapter.MoodAdapter;
import com.example.onelinediary.constant.Const;
import com.example.onelinediary.databinding.ViewholderMainDiaryBinding;
import com.example.onelinediary.dto.Diary;

import java.util.ArrayList;

public class MainPageViewHolder extends RecyclerView.ViewHolder {
    private ViewholderMainDiaryBinding binding;

    public MainPageViewHolder(@NonNull ViewholderMainDiaryBinding binding) {
        super(binding.getRoot());
        this.binding = binding;
    }

    @SuppressLint("SetTextI18n")
    public void onBind(String month, ArrayList<Diary> diaryList) {
        binding.year.setText(Const.currentYear);
        binding.month.setText(month + "월");

        MoodAdapter adapter = new MoodAdapter();
        adapter.addDiaryList(diaryList);
        binding.moodLayout.setAdapter(adapter);
    }
}
