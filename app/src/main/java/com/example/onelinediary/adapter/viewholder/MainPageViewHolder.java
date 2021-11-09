package com.example.onelinediary.adapter.viewholder;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.onelinediary.R;
import com.example.onelinediary.adapter.MoodAdapter;
import com.example.onelinediary.constant.Const;
import com.example.onelinediary.databinding.ViewholderMainDiaryBinding;
import com.example.onelinediary.dto.Diary;
import com.example.onelinediary.utiliy.Utility;

import java.util.ArrayList;

public class MainPageViewHolder extends RecyclerView.ViewHolder {
    private final ViewholderMainDiaryBinding mainDiaryBinding;

    Context context;

    public MainPageViewHolder(@NonNull ViewholderMainDiaryBinding mainDiaryBinding) {
        super(mainDiaryBinding.getRoot());
        this.mainDiaryBinding = mainDiaryBinding;

        context = mainDiaryBinding.getRoot().getContext();
    }

    @SuppressLint("SetTextI18n")
    public void onBind(String month, ArrayList<Diary> diaryList) {
        mainDiaryBinding.year.setText(Const.currentYear);
        mainDiaryBinding.month.setText(month + context.getString(R.string.month));

        if (Utility.isStringNullOrEmpty(Utility.getString(context, Const.SP_KEY_NICKNAME))) {
            mainDiaryBinding.nickname.setVisibility(View.GONE);
        } else {
            mainDiaryBinding.nickname.setVisibility(View.VISIBLE);
            mainDiaryBinding.nickname.setText(Const.nickname + "님의");
        }

        // 그리드 뷰 데이터와 어뎁터 설정
        MoodAdapter adapter = new MoodAdapter(context);
        adapter.addDiaryList(month, diaryList);
        mainDiaryBinding.moodLayout.setAdapter(adapter);
    }
}
