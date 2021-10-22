package com.example.onelinediary.dialog;

import android.app.ProgressDialog;
import android.content.Context;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.Window;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.example.onelinediary.R;

public class CustomProgressDialog extends ProgressDialog {
    private Context mContext;

    public CustomProgressDialog(Context context) {
        super(context);
        mContext = context;

        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
        setCanceledOnTouchOutside(false);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.progress_dialog);
        ImageView loadImage = findViewById(R.id.progressBar);
        Glide.with(mContext).load(R.drawable.jd).into(loadImage);
        setCancelable(false);
        setCanceledOnTouchOutside(false);
    }
}