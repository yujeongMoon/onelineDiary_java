package com.example.onelinediary.dialog;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;

import com.example.onelinediary.constant.Const;
import com.example.onelinediary.databinding.DialogInputBinding;

public class InputDialog extends DialogFragment {
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

        inputBinding.ok.setOnClickListener(v -> {
            if (listener != null) {
                Const.nickname = inputBinding.input.getText().toString();
                listener.onClick(inputBinding.ok);
            }

            dismiss();
        });

        inputBinding.cancel.setOnClickListener(v -> dismiss());

        return inputBinding.getRoot();
    }
}
