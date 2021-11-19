package com.example.onelinediary.dialog;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.example.onelinediary.constant.Const;
import com.example.onelinediary.databinding.DialogInputBinding;

public class InputDialog extends BaseDialog {
    private DialogInputBinding inputBinding;

    String message;
    View.OnClickListener listener;

    public InputDialog() {}

    public InputDialog(String message, View.OnClickListener listener) {
        this.message = message;
        this.listener = listener;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setCancelable(false);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        inputBinding = DialogInputBinding.inflate(inflater, container, false);

        inputBinding.input.setHint(this.message);

        inputBinding.click1.setOnClickListener(v -> {
            if (listener != null) {
                Const.nickname = inputBinding.input.getText().toString();
                listener.onClick(inputBinding.click1);
            }

            dismiss();
        });

        inputBinding.click2.setOnClickListener(v -> dismiss());

        return inputBinding.getRoot();
    }
}
