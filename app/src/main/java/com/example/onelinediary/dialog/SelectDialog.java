package com.example.onelinediary.dialog;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.example.onelinediary.databinding.DialogSelectBinding;

public class SelectDialog extends BaseDialog {
    private DialogSelectBinding selectBinding;

    String message;
    String buttonText1;
    String buttonText2;
    View.OnClickListener listener1;
    View.OnClickListener listener2;

    public SelectDialog() {}

    public SelectDialog(String message, String buttonText1, View.OnClickListener listener1, String buttonText2, View.OnClickListener listener2) {
        this.message = message;
        this.listener1 = listener1;
        this.listener2 = listener2;
        this.buttonText1 = buttonText1;
        this.buttonText2 = buttonText2;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        selectBinding = DialogSelectBinding.inflate(inflater, container, false);

        selectBinding.message.setText(this.message);

        selectBinding.click1.setText(buttonText1);
        selectBinding.click1.setOnClickListener(v -> {
            if (listener1 != null) {
                listener1.onClick(selectBinding.click1);
            }

            dismiss();
        });

        selectBinding.click2.setText(buttonText2);
        selectBinding.click2.setOnClickListener(v -> {
            if (listener2 != null) {
                listener2.onClick(selectBinding.click2);
            }

            dismiss();
        });

        return selectBinding.getRoot();
    }
}
