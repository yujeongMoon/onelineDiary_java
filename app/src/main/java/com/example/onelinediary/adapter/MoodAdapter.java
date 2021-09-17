package com.example.onelinediary.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.textclassifier.ConversationAction;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.onelinediary.R;
import com.example.onelinediary.dto.Diary;

import org.w3c.dom.Text;

import java.util.ArrayList;

public class MoodAdapter extends BaseAdapter {
    ArrayList<Diary> diaryList = new ArrayList<>();

    public void addDiaryList(ArrayList<Diary> diaryList) {
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

        convertView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });

        return convertView;
    }
}
