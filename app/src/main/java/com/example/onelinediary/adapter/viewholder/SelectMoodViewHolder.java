package com.example.onelinediary.adapter.viewholder;

import android.view.View;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.onelinediary.adapter.SelectMoodAdapter;
import com.example.onelinediary.databinding.ViewholderSelectMoodItemBinding;
import com.example.onelinediary.dto.Emoji;
import com.example.onelinediary.utiliy.Utility;

public class SelectMoodViewHolder extends RecyclerView.ViewHolder {
    private final ViewholderSelectMoodItemBinding selectMoodItemBinding;

    public SelectMoodViewHolder(@NonNull ViewholderSelectMoodItemBinding selectMoodItemBinding) {
        super(selectMoodItemBinding.getRoot());

        this.selectMoodItemBinding = selectMoodItemBinding;
    }

    public void onBind(Emoji emoji, SelectMoodAdapter.OnSelectEmoticon onSelectEmoticon) {
        selectMoodItemBinding.emoji.setImageResource(Utility.getResourceImage(selectMoodItemBinding.getRoot().getContext(), emoji.res));

        if(emoji.checked) {
            selectMoodItemBinding.emojiSelect.setVisibility(View.VISIBLE);
        } else {
            selectMoodItemBinding.emojiSelect.setVisibility(View.INVISIBLE);
        }

        selectMoodItemBinding.emojiLayout.setOnClickListener(v -> {
            if(onSelectEmoticon != null) {
                onSelectEmoticon.onClickEmoji(emoji);
            }
        });
    }
}
