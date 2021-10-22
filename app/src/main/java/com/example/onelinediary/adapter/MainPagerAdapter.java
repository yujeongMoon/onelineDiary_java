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

    int weather = -1;

    onDiaryInterface diaryInterface;

    public interface onDiaryInterface {
        void initDiaryCompleted();
    }

    @NonNull
    @Override
    public MainPageViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
//        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
//        View itemView = inflater.inflate(R.layout.viewholder_main_diary, parent, false);
//        return new MainPageViewHolder(itemView);

        binding = ViewholderMainDiaryBinding.inflate(LayoutInflater.from(parent.getContext()), parent, false);
        return new MainPageViewHolder(binding);
    }

    public void setDiaryInterface(onDiaryInterface diaryInterface) {
        this.diaryInterface = diaryInterface;
    }

    // 달마다 해당하는 arrayList를 넘겨줘야함.
    @Override
    public void onBindViewHolder(@NonNull MainPageViewHolder holder, int position) {
        String month = Const.monthKeyList.get(position);
        ArrayList<Diary> diaryListByMonth = Const.diaryList.get(month);
        if(diaryInterface != null) {
            diaryInterface.initDiaryCompleted();
        }
        if (diaryListByMonth != null && !diaryListByMonth.isEmpty()) {
            holder.onBind(month, diaryListByMonth);
        }

        if (weather != -1) {
            holder.setWeather(weather);
        }
    }

    @Override
    public int getItemCount() {
        return Const.monthKeyList.size();
    }

    public void setWeather(int weather) {
        this.weather = weather;
    }
}
