package com.example.onelinediary.activity;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;

import com.example.onelinediary.R;
import com.example.onelinediary.constant.Const;
import com.example.onelinediary.databinding.ActivityDiaryDetailBinding;
import com.example.onelinediary.dialog.ConfirmDialog;
import com.example.onelinediary.dto.Diary;
import com.example.onelinediary.utiliy.DatabaseUtility;
import com.example.onelinediary.utiliy.Utility;

public class DiaryDetailActivity extends AppCompatActivity {
    private ActivityDiaryDetailBinding detailBinding;

    String month;
    String day;
    Diary diary;

    boolean isEnabled = false;
    
    int currentMood = Const.Mood.NONE.value;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        detailBinding = ActivityDiaryDetailBinding.inflate(getLayoutInflater());
        setContentView(detailBinding.getRoot());

        Intent items = getIntent();
        month = items.getStringExtra("month");
        day = items.getStringExtra("day");
        diary = items.getParcelableExtra("diary");

        // 일기를 작성한 날짜
        detailBinding.detailTodayDate.setText(diary.getReportingDate());

        // 일기를 작성할 때 선택한 기분
        setCurrentMood(diary.getMood());

        // 일기 내용
        detailBinding.detailDiaryContents.setText(diary.getContents());

        // 일기와 같이 선택한 사진
        if (diary.getPhoto().equals("")) {
            detailBinding.detailPhoto.setImageResource(R.drawable.default_placeholder_image);
        } else {
            Uri imageUri = Uri.parse(diary.getPhoto());
            detailBinding.detailPhoto.setImageURI(imageUri);
        }

        detailBinding.switchEditMode.setOnCheckedChangeListener((buttonView, isChecked) -> {
            isEnabled = isChecked;

            if (isEnabled) {
                detailBinding.detailEmoji.setVisibility(View.GONE);

                detailBinding.detailEmojiLayout.setVisibility(View.VISIBLE);

                detailBinding.detailDiaryContents.setEnabled(true);

                detailBinding.detailButtonLayout.setVisibility(View.VISIBLE);

                detailBinding.detailPhoto.isClickEnabled(true);
            } else {
                setCurrentMood(diary.getMood());
                detailBinding.detailEmoji.setVisibility(View.VISIBLE);

                detailBinding.detailEmojiLayout.setVisibility(View.GONE);

                detailBinding.detailDiaryContents.setText(diary.getContents());
                detailBinding.detailDiaryContents.setEnabled(false);

                detailBinding.detailButtonLayout.setVisibility(View.GONE);

                detailBinding.detailPhoto.isClickEnabled(false);
            }
        });

        detailBinding.emojiHappyLayout.setOnClickListener(onClickListener);
        detailBinding.emojiSmileLayout.setOnClickListener(onClickListener);
        detailBinding.emojiBlankLayout.setOnClickListener(onClickListener);
        detailBinding.emojiSadLayout.setOnClickListener(onClickListener);
        detailBinding.emojiNervousLayout.setOnClickListener(onClickListener);

        // TODO 사진 변경하는 기능 추가하기
        detailBinding.detailPhoto.setOnClickListener(v -> {

        });
    }

    View.OnClickListener onClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            if (v.getId() == R.id.emoji_happy_layout) {
                if (currentMood != Const.Mood.HAPPY.value) {
                    resetSelectedState();
                }

                if (detailBinding.emojiHappyCheck.getVisibility() == View.INVISIBLE) {
                    detailBinding.emojiHappyCheck.setVisibility(View.VISIBLE);
                    currentMood = Const.Mood.HAPPY.value;
                } else {
                    detailBinding.emojiHappyCheck.setVisibility(View.INVISIBLE);
                    currentMood = Const.Mood.NONE.value;
                }
            } if (v.getId() == R.id.emoji_smile_layout) {
                if (currentMood != Const.Mood.SMILE.value) {
                    resetSelectedState();
                }
                if (detailBinding.emojiSmileCheck.getVisibility() == View.INVISIBLE) {
                    detailBinding.emojiSmileCheck.setVisibility(View.VISIBLE);
                    currentMood = Const.Mood.SMILE.value;
                } else {
                    detailBinding.emojiSmileCheck.setVisibility(View.INVISIBLE);
                    currentMood = Const.Mood.NONE.value;
                }
            } if (v.getId() == R.id.emoji_blank_layout) {
                if (currentMood != Const.Mood.BLANK.value) {
                    resetSelectedState();
                }
                if (detailBinding.emojiBlankCheck.getVisibility() == View.INVISIBLE) {
                    detailBinding.emojiBlankCheck.setVisibility(View.VISIBLE);
                    currentMood = Const.Mood.BLANK.value;
                } else {
                    detailBinding.emojiBlankCheck.setVisibility(View.INVISIBLE);
                    currentMood = Const.Mood.NONE.value;
                }
            } if (v.getId() == R.id.emoji_sad_layout) {
                if (currentMood != Const.Mood.SAD.value) {
                    resetSelectedState();
                }
                if (detailBinding.emojiSadCheck.getVisibility() == View.INVISIBLE) {
                    detailBinding.emojiSadCheck.setVisibility(View.VISIBLE);
                    currentMood = Const.Mood.SAD.value;
                } else {
                    detailBinding.emojiSadCheck.setVisibility(View.INVISIBLE);
                    currentMood = Const.Mood.NONE.value;
                }
            } if (v.getId() == R.id.emoji_nervous_layout) {
                if (currentMood != Const.Mood.NERVOUS.value) {
                    resetSelectedState();
                }
                if (detailBinding.emojiNervousCheck.getVisibility() == View.INVISIBLE) {
                    detailBinding.emojiNervousCheck.setVisibility(View.VISIBLE);
                    currentMood = Const.Mood.NERVOUS.value;
                } else {
                    detailBinding.emojiNervousCheck.setVisibility(View.INVISIBLE);
                    currentMood = Const.Mood.NONE.value;
                }
            }

            diary.setMood(currentMood);
        }
    };

    private void resetSelectedState() {
        detailBinding.emojiHappyCheck.setVisibility(View.INVISIBLE);
        detailBinding.emojiSmileCheck.setVisibility(View.INVISIBLE);
        detailBinding.emojiBlankCheck.setVisibility(View.INVISIBLE);
        detailBinding.emojiSadCheck.setVisibility(View.INVISIBLE);
        detailBinding.emojiNervousCheck.setVisibility(View.INVISIBLE);

        currentMood = Const.Mood.NONE.value;
        diary.setMood(currentMood);
    }

    private void setCurrentMood(int mood) {
        switch (mood) {
            case 1:
                detailBinding.detailEmoji.setImageResource(R.drawable.emoji_happy_icon);
                break;
            case 2:
                detailBinding.detailEmoji.setImageResource(R.drawable.emoji_blushing_icon);
                break;
            case 3:
                detailBinding.detailEmoji.setImageResource(R.drawable.emoji_blank_icon);
                break;
            case 4:
                detailBinding.detailEmoji.setImageResource(R.drawable.emoji_consoling_icon);
                break;
            case 5:
                detailBinding.detailEmoji.setImageResource(R.drawable.emoji_nervous_icon);
                break;
        }
    }

    public void editDiary(View view) {
        // 작성한 일기 컨텐츠 저장
        String contents = detailBinding.detailDiaryContents.getText().toString();
        diary.setContents(contents);

        // 오늘의 기분 선택
        if (currentMood == Const.Mood.NONE.value) {
            new ConfirmDialog("오늘의 기분을 입력해주세요!", null).show(getSupportFragmentManager(), "mood");
        } else {
            DatabaseUtility.updateDiary(this, Utility.getYear(), month, day, diary, isSuccess -> {
                if (isSuccess) {
                    new ConfirmDialog("일기가 수정되었습니다.", null).show(getSupportFragmentManager(), "updateDiarySuccess");
                    detailBinding.switchEditMode.setChecked(false);
                } else {
                    new ConfirmDialog("일기를 수정하는데 문제가 발생하였습니다. 다시 시도해주세요!", null).show(getSupportFragmentManager(), "updateDiaryFailure");
                }
            });
        }
    }
}