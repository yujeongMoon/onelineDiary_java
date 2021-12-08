package com.example.onelinediary.activity;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.onelinediary.R;
import com.example.onelinediary.constant.Const;
import com.example.onelinediary.custom.CharacterWrapEditText;
import com.example.onelinediary.databinding.ActivityWriteNoticeBinding;
import com.example.onelinediary.dialog.ConfirmDialog;
import com.example.onelinediary.dialog.YesNoDialog;
import com.example.onelinediary.dto.ItemNotice;
import com.example.onelinediary.dto.Notice;
import com.example.onelinediary.utiliy.DatabaseUtility;
import com.example.onelinediary.utiliy.Utility;
import com.google.gson.Gson;

public class WriteNoticeActivity extends AppCompatActivity {
    private ActivityWriteNoticeBinding writeNoticeBinding;

    boolean addNewNotice;
    boolean isChanged;

    ItemNotice itemNotice;
    String title;
    String contents;
    String action;

    Notice notice = new Notice();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        writeNoticeBinding = ActivityWriteNoticeBinding.inflate(getLayoutInflater());
        setContentView(writeNoticeBinding.getRoot());

        addNewNotice = getIntent().getBooleanExtra(Const.INTENT_KEY_ADD_NEW_NOTICE, false);

        if (addNewNotice) { // 공지사항을 새로 작성하는 경우
            action = getString(R.string.save);
            writeNoticeBinding.writeNoticeButton.setText(action);
            writeNoticeBinding.writeNoticeButtonLayout.setOnClickListener(v -> addNotice());
        } else { // 작성한 공지사항을 수정하는 경우
            itemNotice = getIntent().getParcelableExtra(Const.INTENT_KEY_SELECTED_ITEM_NOTICE);

            if (itemNotice != null) {
                title = itemNotice.getNotice().getTitle();
                contents = itemNotice.getNotice().getContents();

                if (title != null) {
                    writeNoticeBinding.inputTitle.setText(itemNotice.getNotice().getTitle());
                } else {
                    writeNoticeBinding.inputTitle.setText("");
                }

                if (contents != null) {
                    writeNoticeBinding.inputContents.setText(itemNotice.getNotice().getContents());
                } else {
                    writeNoticeBinding.inputContents.setText("");
                }
            }

            action = getString(R.string.edit);
            writeNoticeBinding.writeNoticeButton.setText(action);
            writeNoticeBinding.writeNoticeButtonLayout.setOnClickListener(v -> updateNotice());
        }

        writeNoticeBinding.inputTitle.addTextChangedListenerWithChar(writeNoticeBinding.inputTitle,
                new CharacterWrapEditText.onTextChangeListener() {
                    @Override
                    public void beforeTextChanged(CharSequence s, int start, int count, int after) {

                    }

                    @Override
                    public void onTextChanged(CharSequence s, int start, int before, int count) {

                    }

                    @Override
                    public void afterTextChanged(Editable s) {
                        isChanged = !s.toString().equals("");

                        if (itemNotice != null && itemNotice.getNotice().getTitle().equals(s.toString())) {
                            isChanged = false;
                        }
                    }
                });

        writeNoticeBinding.inputContents.addTextChangedListenerWithChar(writeNoticeBinding.inputContents,
                new CharacterWrapEditText.onTextChangeListener() {
                    @Override
                    public void beforeTextChanged(CharSequence s, int start, int count, int after) {

                    }

                    @Override
                    public void onTextChanged(CharSequence s, int start, int before, int count) {

                    }

                    @Override
                    public void afterTextChanged(Editable s) {
                        isChanged = !s.toString().equals("");

                        if (itemNotice != null && itemNotice.getNotice().getContents().equals(s.toString())) {
                            isChanged = false;
                        }
                    }
                });
    }

    @Override
    public void onBackPressed() {
        String message;
        if (isChanged) {
            if (addNewNotice) {
                message = getString(R.string.dialog_message_exit_add_notice);
            } else {
                message = getString(R.string.dialog_message_exit_update_notice);
            }
            new YesNoDialog(message, v -> finish()).show(this);
        } else {
            super.onBackPressed();
        }
    }

    public boolean validate() {
        boolean valid = true;
        String message = "";

        if (TextUtils.isEmpty(writeNoticeBinding.inputTitle.getText().toString())) {
            message = getString(R.string.dialog_message_input_title);
            valid = false;
        } else if (TextUtils.isEmpty(writeNoticeBinding.inputContents.getText().toString())) {
            message = getString(R.string.dialog_message_input_contents);
            valid = false;
        }

        if (!valid) {
            new ConfirmDialog(message, null).show(WriteNoticeActivity.this);
        }

        return valid;
    }

    public void addNotice() {
        if (validate()) {
            notice.setYear(Utility.getYear());
            notice.setReportingDate(Utility.getDate(Const.REPORTING_DATE_FORMAT));
            notice.setTitle(writeNoticeBinding.inputTitle.getText().toString());
            notice.setContents(writeNoticeBinding.inputContents.getText().toString());

            Utility.putBoolean(getApplicationContext(), Const.SP_KEY_NOTIFICATION_IN, true);
            DatabaseUtility.writeNotice(notice, isSuccess -> {
                if (isSuccess) {
                    new ConfirmDialog(getString(R.string.dialog_message_confirm_action_notice, action), v -> finish()).show(WriteNoticeActivity.this);
                } else {
                    Toast.makeText(getApplicationContext(), getString(R.string.error_message_action_notice, action), Toast.LENGTH_SHORT).show();
                }
            });
        }
    }

    public void deleteNotice() {
        DatabaseUtility.deleteNotice(itemNotice.getNotice().getYear(), itemNotice.getKey(), null);
    }

    public void updateNotice() {
        // 수정하면서 작성일자가 바뀌기 때문에 기존의 공지사항을 삭제하고 새로 추가한다.
        // 수정한 공지사항을 제일 위로 올라간다.
        deleteNotice();
        addNotice();
    }

    @Override
    protected void onDestroy() {
        Utility.putBoolean(getApplicationContext(), Const.SP_KEY_NOTIFICATION_IN, true);
        super.onDestroy();
    }
}