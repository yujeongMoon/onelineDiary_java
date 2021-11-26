package com.example.onelinediary.adapter;

import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.onelinediary.adapter.viewholder.MainPageViewHolder;
import com.example.onelinediary.constant.Const;
import com.example.onelinediary.databinding.ViewholderMainDiaryBinding;
import com.example.onelinediary.dto.Diary;

import java.util.ArrayList;

public class MainPagerAdapter extends RecyclerView.Adapter<MainPageViewHolder> {
    private ViewholderMainDiaryBinding binding;

    onDiaryInterface diaryInterface;

    public interface onDiaryInterface {
        void initDiaryCompleted();
        void onPagerScroll(boolean prev);
        void onYearSelected(String year);
    }

    @NonNull
    @Override
    public MainPageViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        binding = ViewholderMainDiaryBinding.inflate(LayoutInflater.from(parent.getContext()), parent, false);
        return new MainPageViewHolder(binding, diaryInterface);
    }

    public void setDiaryInterface(onDiaryInterface diaryInterface) {
        this.diaryInterface = diaryInterface;
    }

    // 달마다 해당하는 arrayList를 넘겨줘야함.
    @Override
    public void onBindViewHolder(@NonNull MainPageViewHolder holder, int position) {
        String month = Const.monthKeyList.get(position);
        ArrayList<Diary> diaryListByMonth = Const.diaryList.get(month);

        // 오늘 일기를 쓴 지 확인하고 일기를 썼다면 일기 추가 버튼을 오늘의 기분 이모지로 바꿔준다.
        if(diaryInterface != null) {
            diaryInterface.initDiaryCompleted();
        }
        if (diaryListByMonth != null && !diaryListByMonth.isEmpty()) {
            holder.onBind(month, diaryListByMonth);
        }
    }

    @Override
    public int getItemCount() {
        return Const.monthKeyList.size();
    }
}
