package com.example.onelinediary.adapter.viewholder;

import android.annotation.SuppressLint;
import android.content.Context;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.onelinediary.R;
import com.example.onelinediary.adapter.MoodAdapter;
import com.example.onelinediary.constant.Const;
import com.example.onelinediary.databinding.ViewholderMainDiaryBinding;
import com.example.onelinediary.dto.Diary;

import java.util.ArrayList;

public class MainPageViewHolder extends RecyclerView.ViewHolder {
    private ViewholderMainDiaryBinding binding;

    Context context;

    public MainPageViewHolder(@NonNull ViewholderMainDiaryBinding binding) {
        super(binding.getRoot());
        this.binding = binding;

        context = binding.getRoot().getContext();
    }

    @SuppressLint("SetTextI18n")
    public void onBind(String month, ArrayList<Diary> diaryList) {
        binding.year.setText(Const.currentYear);
        binding.month.setText(month + context.getString(R.string.month));

        MoodAdapter adapter = new MoodAdapter(context);
        adapter.addDiaryList(month, diaryList);
        binding.moodLayout.setAdapter(adapter);
    }
}
