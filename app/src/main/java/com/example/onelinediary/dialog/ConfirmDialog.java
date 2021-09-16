package com.example.onelinediary.dialog;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.DialogFragment;

import com.example.onelinediary.R;
import com.example.onelinediary.utiliy.Utility;

public class ConfirmDialog extends DialogFragment {
    String message;
    View.OnClickListener listener = null;

    public ConfirmDialog() {}

    public ConfirmDialog(String message, View.OnClickListener listener) {
        this.message = message;
        this.listener = listener;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setCancelable(false);
    }

    @Override
    public void onResume() {
        super.onResume();

        int width = Utility.dpToPx(requireContext(), 300);
        int height = Utility.dpToPx(requireContext(), 200);

        requireDialog().getWindow().setLayout(width, height);
    }

    @SuppressLint("InflateParams")
    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(requireActivity());

        LayoutInflater inflater = requireActivity().getLayoutInflater();
        View view = inflater.inflate(R.layout.dialog_custom, null);

        TextView message = view.findViewById(R.id.confirm_message);
        message.setText(this.message);

        View ok = view.findViewById(R.id.ok_layout);
        if (listener != null)
            ok.setOnClickListener(listener);
        else
            ok.setOnClickListener(v -> dismiss());

        builder.setView(view);

        return builder.create();
    }
}
