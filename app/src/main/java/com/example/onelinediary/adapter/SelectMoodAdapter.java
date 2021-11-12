package com.example.onelinediary.adapter;

import android.annotation.SuppressLint;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.onelinediary.adapter.viewholder.SelectMoodViewHolder;
import com.example.onelinediary.databinding.ViewholderSelectMoodItemBinding;
import com.example.onelinediary.dto.Emoji;

import java.util.ArrayList;

public class SelectMoodAdapter extends RecyclerView.Adapter<SelectMoodViewHolder> {
    private ViewholderSelectMoodItemBinding selectMoodItemBinding;

    private ArrayList<Emoji> emojiList;

    public interface OnSelectEmoticon {
        void onClickEmoji(Emoji selEmoji);
    }

    private OnSelectEmoticon onSelectEmoticon;

    @NonNull
    @Override
    public SelectMoodViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        selectMoodItemBinding = ViewholderSelectMoodItemBinding.inflate(LayoutInflater.from(parent.getContext()), parent, false);
        return new SelectMoodViewHolder(selectMoodItemBinding);
    }

    @Override
    public void onBindViewHolder(@NonNull SelectMoodViewHolder holder, int position) {
        holder.onBind(emojiList.get(position), onSelectEmoticon);
    }

    @Override
    public int getItemCount() {
        return emojiList.size();
    }

    public void setList(ArrayList<Emoji> list, OnSelectEmoticon listener) {
        this.onSelectEmoticon = listener;
        emojiList = list;
    }

    /**
     * 선택된 이모지 외의 값은 체크를 false로 만들어 준다.
     * 한 번 선택한 이모지를 다시 클릭할 경우에도 호출되며 전체 다 false로 바꿔준다.
     * @param emoji 선택한 이모지의 정보
     */
    @SuppressLint("NotifyDataSetChanged")
    public void setSelectedEmoji(Emoji emoji) {
        for(Emoji item : emojiList) {
            if(item.res.equals(emoji.res)) {
                item.checked = !emoji.checked;
            } else {
                item.checked = false;
            }
        }
        notifyDataSetChanged();
    }

    /**
     * 상세화면에서 수정화면으로 넘어갈 때나 일기가 변경되었지만 수정 버튼을 누르지 않아 변경하지 않았을 때
     * 처음 일기의 기분으로 체크 여부를 초기화 해준다.
     *
     * @param emojiName 이모지 파일명
     */
    @SuppressLint("NotifyDataSetChanged")
    public void initEmoji(String emojiName) {
        for(Emoji item : emojiList) {
            item.checked = item.res.equals(emojiName);
        }
        notifyDataSetChanged();
    }

    /**
     * 현재 선택된 이모지의 포지션을 반환해준다.
     *
     * @return 선택된 이모지의 포지션
     */
    public int getPosition() {
        for(Emoji item : emojiList) {
            if (item.checked)
                return item.position;
        }
        return 0;
    }
}
