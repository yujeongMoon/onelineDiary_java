package com.example.onelinediary.adapter.viewholder;

import android.content.Context;
import android.content.Intent;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.onelinediary.R;
import com.example.onelinediary.activity.WriteNoticeActivity;
import com.example.onelinediary.adapter.NoticeAdapter;
import com.example.onelinediary.constant.Const;
import com.example.onelinediary.databinding.ViewholderNoticeBinding;
import com.example.onelinediary.dialog.SelectDialog;
import com.example.onelinediary.dto.ItemNotice;
import com.example.onelinediary.utiliy.DatabaseUtility;
import com.example.onelinediary.utiliy.Utility;

public class NoticeViewHolder extends RecyclerView.ViewHolder {
    private final ViewholderNoticeBinding noticeBinding;

    NoticeAdapter adapter;
    Context context;
    public NoticeViewHolder(NoticeAdapter adapter, @NonNull ViewholderNoticeBinding noticeBinding) {
        super(noticeBinding.getRoot());

        this.noticeBinding = noticeBinding;
        this.adapter = adapter;
        this.context = noticeBinding.getRoot().getContext();
    }

    public void onBind(int position, ItemNotice item) {
        noticeBinding.noticeTitle.setText(item.getNotice().getTitle());
        noticeBinding.noticeReportingDate.setText(item.getNotice().getReportingDate());
        noticeBinding.noticeContents.setText(item.getNotice().getContents());

        noticeBinding.container.setOnClickListener(v -> {
            if (item.isClicked()) {
                item.setClicked(false);
                noticeBinding.noticeContents.setMaxLines(4);
            } else {
                item.setClicked(true);
                noticeBinding.noticeContents.setMaxLines(Integer.MAX_VALUE);
            }

            adapter.updateItem(position, item);
        });

        if (Utility.getAndroidId(context).equals(Const.ADMIN_ANDROID_ID)) {
            noticeBinding.container.setOnLongClickListener(v -> {
                new SelectDialog(context.getString(R.string.dialog_message_select_action), context.getString(R.string.edit), v1 -> {
                    Intent updateNoticeIntent = new Intent(context, WriteNoticeActivity.class);
                    updateNoticeIntent.putExtra(Const.INTENT_KEY_ADD_NEW_NOTICE, false);
                    updateNoticeIntent.putExtra(Const.INTENT_KEY_SELECTED_ITEM_NOTICE, item);
                    context.startActivity(updateNoticeIntent);
                }, context.getString(R.string.delete), v2 -> DatabaseUtility.deleteNotice(item.getNotice().getYear(), item.getKey(), isSuccess -> {
                    if (isSuccess) {
                        Toast.makeText(context.getApplicationContext(), context.getString(R.string.dialog_message_delete_notice), Toast.LENGTH_SHORT).show();
                    }
                })).show(context);
                return true;
            });
        } else {
            noticeBinding.container.setOnLongClickListener(null);
        }
    }
}
