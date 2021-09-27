package com.example.onelinediary.adapter;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.onelinediary.R;
import com.example.onelinediary.activity.DiaryDetailActivity;
import com.example.onelinediary.dto.Diary;

import java.util.ArrayList;

public class MoodAdapter extends BaseAdapter {
    private Context context;
    String month;
    ArrayList<Diary> diaryList = new ArrayList<>();

    public MoodAdapter() {}

    public MoodAdapter(Context context) {
        this.context = context;
    }

    public void addDiaryList(String month, ArrayList<Diary> diaryList) {
        this.month = month;
        this.diaryList = diaryList;
    }

    @Override
    public int getCount() {
        return diaryList.size();
    }

    @Override
    public Object getItem(int position) {
        return diaryList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @SuppressLint("SetTextI18n")
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        int mood = diaryList.get(position).getMood();

        if (convertView == null) {
            LayoutInflater inflater = (LayoutInflater) parent.getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = inflater.inflate(R.layout.viewholder_mood_item, parent, false);

            TextView day = convertView.findViewById(R.id.day);
            day.setText(diaryList.get(position).getDay() + "일");

            ImageView emoji = convertView.findViewById(R.id.emoji);

            switch (mood) {
                case 1:
                    emoji.setImageResource(R.drawable.emoji_happy_icon);
                    break;
                case 2:
                    emoji.setImageResource(R.drawable.emoji_blushing_icon);
                    break;
                case 3:
                    emoji.setImageResource(R.drawable.emoji_blank_icon);
                    break;
                case 4:
                    emoji.setImageResource(R.drawable.emoji_consoling_icon);
                    break;
                case 5:
                    emoji.setImageResource(R.drawable.emoji_nervous_icon);
                    break;
            }
        } else {
            View view = new View(parent.getContext());
            view = (View) convertView;
        }

        convertView.setOnClickListener(v -> {
            Intent detailIntent = new Intent(context, DiaryDetailActivity.class);
//            detailIntent.putExtra("type", "detail");
            detailIntent.putExtra("month", month);
            detailIntent.putExtra("day", diaryList.get(position).getDay());
            detailIntent.putExtra("diary", diaryList.get(position));
            context.startActivity(detailIntent);
        });

        return convertView;
    }
}
