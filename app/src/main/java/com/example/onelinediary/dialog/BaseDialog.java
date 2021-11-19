package com.example.onelinediary.dialog;

import android.content.Context;

import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.FragmentActivity;

public class BaseDialog extends DialogFragment {

    public void show(Context context) {
        super.show(((FragmentActivity)context).getSupportFragmentManager(), "");
    }
}