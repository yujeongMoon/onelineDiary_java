package com.example.onelinediary.adapter.viewholder;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.onelinediary.databinding.ViewholderGridViewImageItemBinding;

public class GridViewImageItemViewHolder extends RecyclerView.ViewHolder {
    private final ViewholderGridViewImageItemBinding imageItemBinding;

    public GridViewImageItemViewHolder(@NonNull ViewholderGridViewImageItemBinding imageItemBinding) {
        super(imageItemBinding.getRoot());

        this.imageItemBinding = imageItemBinding;
    }

    public void onBind() {

    }
}
