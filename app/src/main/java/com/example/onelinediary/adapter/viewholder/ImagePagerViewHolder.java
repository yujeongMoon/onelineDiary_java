package com.example.onelinediary.adapter.viewholder;

import android.content.Context;
import android.graphics.Bitmap;

import androidx.annotation.NonNull;
import androidx.fragment.app.FragmentActivity;
import androidx.recyclerview.widget.RecyclerView;

import com.example.onelinediary.R;
import com.example.onelinediary.adapter.ImagePagerAdapter;
import com.example.onelinediary.constant.Const;
import com.example.onelinediary.databinding.ViewholderImageBinding;
import com.example.onelinediary.dialog.SelectDialog;
import com.example.onelinediary.utiliy.Utility;

import static com.example.onelinediary.constant.Const.PICKER_IMAGE_REQUEST;

public class ImagePagerViewHolder extends RecyclerView.ViewHolder {
    private final ViewholderImageBinding imageBinding;
    private final ImagePagerAdapter adapter;

    public ImagePagerViewHolder(ImagePagerAdapter adapter, @NonNull ViewholderImageBinding imageBinding) {
        super(imageBinding.getRoot());

        this.imageBinding = imageBinding;
        this.adapter = adapter;
    }

    public void onBind(boolean isEditable, int position, Bitmap bitmapImage) {
        Context context = imageBinding.getRoot().getContext();

        if (isEditable) { // 일기 작성과 수정 가능
            imageBinding.photo.setDoubleClickable(false);
            imageBinding.photo.setOnClickListener(v -> Const.photoUri = Utility.selectPhoto(context, PICKER_IMAGE_REQUEST));

            if (bitmapImage != null) {
                imageBinding.photo.setImageBitmap(bitmapImage);

                imageBinding.photo.setOnLongClickListener(v -> {
                    new SelectDialog("선택한 사진을 삭제하시겠습니까?", v1 -> {
                        adapter.deletePhotoList(position);
                        imageBinding.photo.setImageResource(R.drawable.default_placeholder_image);
                    }).show(((FragmentActivity)context).getSupportFragmentManager(), "deletePhoto");

                    return true;
                });
            } else {
                imageBinding.photo.setImageResource(R.drawable.default_placeholder_image);
                imageBinding.photo.setOnLongClickListener(null);
            }
        } else { // 일기 상세
            imageBinding.photo.setDoubleClickable(true);
            imageBinding.photo.setOnLongClickListener(null);
            if (bitmapImage != null) {
                imageBinding.photo.setImageBitmap(bitmapImage);
                imageBinding.photo.setOnClickListener(adapter.getImageViewer());
            } else {
                imageBinding.photo.setImageResource(R.drawable.default_placeholder_image);
                imageBinding.photo.setOnClickListener(null);
            }
        }
    }

    public void initPreTime() {
        imageBinding.photo.initPreTime();
    }
}
