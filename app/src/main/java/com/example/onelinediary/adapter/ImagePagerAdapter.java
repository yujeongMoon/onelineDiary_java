package com.example.onelinediary.adapter;

import android.annotation.SuppressLint;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.onelinediary.adapter.viewholder.ImagePagerViewHolder;
import com.example.onelinediary.databinding.ViewholderImageBinding;
import com.example.onelinediary.dto.PhotoInfo;

import java.util.ArrayList;

public class ImagePagerAdapter extends RecyclerView.Adapter<ImagePagerViewHolder> {
    private ViewholderImageBinding imageBinding;

    public ArrayList<PhotoInfo> photoList = new ArrayList<>();

    public boolean isChanged = false;
    public boolean isEditable = false;

    public ImagePagerViewHolder holder;

    @NonNull
    @Override
    public ImagePagerViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        imageBinding = ViewholderImageBinding.inflate(LayoutInflater.from(parent.getContext()), parent, false);
        return new ImagePagerViewHolder(this, imageBinding);
    }

    @Override
    public void onBindViewHolder(@NonNull ImagePagerViewHolder holder, int position) {
        this.holder = holder;
        holder.onBind(isEditable, position, photoList.get(position).getBitmapImage());
    }

    @Override
    public int getItemCount() {
        return photoList.size();
    }

    public void setEditable(boolean isEditable) {
        this.isEditable = isEditable;
    }

    @SuppressLint("NotifyDataSetChanged")
    public void addPhotoList(ArrayList<PhotoInfo> photoInfoList) {
        resetPhotoList();
        this.photoList.addAll(photoInfoList);
        this.notifyDataSetChanged();
    }

    public void addPhoto(PhotoInfo path) {
        photoList.add(path);
    }

    @SuppressLint("NotifyDataSetChanged")
    public void addPhotoWithIndex(int index, PhotoInfo photoInfo) {
        photoList.set(index, photoInfo);
        this.notifyDataSetChanged();
    }

    public void resetPhotoList() {
        this.photoList.clear();
    }

    @SuppressLint("NotifyDataSetChanged")
    public void deletePhotoList(int position) {
        photoList.set(position, new PhotoInfo(null, null));
        this.notifyDataSetChanged();
    }
    public void initPreTime() {
        holder.initPreTime();
    }
}
