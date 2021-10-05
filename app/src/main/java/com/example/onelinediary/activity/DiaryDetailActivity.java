package com.example.onelinediary.activity;

import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import com.example.onelinediary.R;
import com.example.onelinediary.constant.Const;
import com.example.onelinediary.databinding.ActivityDiaryDetailBinding;
import com.example.onelinediary.dialog.ConfirmDialog;
import com.example.onelinediary.dto.Diary;
import com.example.onelinediary.utiliy.DatabaseUtility;
import com.example.onelinediary.utiliy.Utility;

public class DiaryDetailActivity extends AppCompatActivity {
    private ActivityDiaryDetailBinding detailBinding;

    private final int UPDATE_PICKER_IMAGE_REQUEST = 200;

    String month;
    String day;
    Diary diary;

    boolean isEnabled = false;
    boolean isUpdate = false;
    
    int currentMood = Const.Mood.NONE.value;

    Uri photoUri;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        detailBinding = ActivityDiaryDetailBinding.inflate(getLayoutInflater());
        setContentView(detailBinding.getRoot());

        Intent items = getIntent();
        diary = items.getParcelableExtra(Const.INTENT_KEY_DIARY);
        month = items.getStringExtra(Const.INTENT_KEY_MONTH);
        day = diary.getDay();

        // 일기를 작성한 날짜
        detailBinding.detailTodayDate.setText(diary.getReportingDate());

        // 일기를 작성한 위치
        if (diary.getLocation().equals("")) {
            detailBinding.detailCurrentLocation.setVisibility(View.GONE);
        } else {
            detailBinding.detailCurrentLocation.setVisibility(View.VISIBLE);
            detailBinding.detailCurrentLocation.setText(diary.getLocation());
        }

        // 일기를 작성할 때 선택한 기분
        setCurrentMood(diary.getMood());

        // 일기 내용
        detailBinding.textDetailDiaryContents.setText(diary.getContents());

        // 일기와 같이 선택한 사진
        if (diary.getPhoto().equals("")) {
            detailBinding.detailPhoto.setImageResource(R.drawable.default_placeholder_image);
            detailBinding.detailPhoto.setBackground(ContextCompat.getDrawable(this, R.color.white));
        } else {
            /*
                구글 포토에 있는 사진을 사용한 경우, uri에 "com.google.android.apps.photos.contentprovider"이 포함되어있다.
                구글 포토에서 사진을 선택하여 onActivityResult()에서 uri를 사용하는 경우에는 authority가 "com.google.android.apps.photos.contentprovider"로 지정되어 있기 때문에
                permission Denial이 발생하지 않는다.
                DB에 저장했다가 불러온 uri는 authority는 null이기 때문에 구글 포토에 있는 사진에 접근할 수가 없다.
                새로운 이미지 파일을 생성해서 선택한 이미지를 저장한 다음에 그 파일 경로를 통해 사진을 불러오는 등의 방법을 사용해야한다.(일단은)

                갤러리를 통해서 저장된 uri도 DB에서 불러올 때는 authority가 null이다.
                결론적으로 authority가 원인은 아닐 수 있다.

                새로운 파일을 생성해서 저장하는 방법은 비효율적이기 때문에 DB에 uri가 아닌 절대 경로를 저장하도록 바꿔서 진행했다.
                getContentResolver().query()로 cursor를 생성하고 입력한 uri에서 절대 경로를 알아낸다.
                절대 경로를 이용하면 구글 포토 권한이 필요 없기 때문에 잘 진행된다.
             */
            Bitmap photo = Utility.getRotatedBitmap(diary.getPhoto());

            if (photo != null) {
                detailBinding.detailPhoto.setImageBitmap(photo);
                detailBinding.detailPhoto.setBackground(ContextCompat.getDrawable(this, R.color.black));
            } else {
                Toast.makeText(getApplicationContext(), getString(R.string.error_message_get_photo), Toast.LENGTH_LONG).show();
                detailBinding.detailPhoto.setImageResource(R.drawable.default_placeholder_image);
                detailBinding.detailPhoto.setBackground(ContextCompat.getDrawable(this, R.color.white));
            }
        }

        detailBinding.switchEditMode.setOnCheckedChangeListener((buttonView, isChecked) -> {
            isEnabled = isChecked;

            if (isEnabled) {
                detailBinding.detailEmoji.setVisibility(View.GONE);

                detailBinding.detailEmojiLayout.setVisibility(View.VISIBLE);

                detailBinding.textDetailDiaryContentsLayout.setVisibility(View.GONE);
                detailBinding.editDetailDiaryContents.setVisibility(View.VISIBLE);

                detailBinding.editDetailDiaryContents.setText(diary.getContents());

                detailBinding.detailButtonLayout.setVisibility(View.VISIBLE);

                detailBinding.detailPhoto.isClickEnabled(true);

                // 기존에 선택한 기분을 확인해서 표시해준다.
                currentMood = diary.getMood();
                initMood();

                detailBinding.emojiHappyLayout.setOnClickListener(onClickListener);
                detailBinding.emojiSmileLayout.setOnClickListener(onClickListener);
                detailBinding.emojiBlankLayout.setOnClickListener(onClickListener);
                detailBinding.emojiSadLayout.setOnClickListener(onClickListener);
                detailBinding.emojiNervousLayout.setOnClickListener(onClickListener);

                detailBinding.detailPhoto.setOnClickListener(v -> photoUri = Utility.selectPhoto(DiaryDetailActivity.this, UPDATE_PICKER_IMAGE_REQUEST));
            } else {
                InputMethodManager im = (InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);
                im.hideSoftInputFromWindow(detailBinding.editDetailDiaryContents.getWindowToken(), 0);

                if (isUpdate) {
                    isUpdate = false;
                    setCurrentMood(diary.getMood());
                }

                detailBinding.detailEmoji.setVisibility(View.VISIBLE);

                // editmode를 해제하면 선택된 기분을 초기화해준다.
                resetSelectedState();
                detailBinding.detailEmojiLayout.setVisibility(View.GONE);

                detailBinding.textDetailDiaryContentsLayout.setVisibility(View.VISIBLE);
                detailBinding.editDetailDiaryContents.setVisibility(View.GONE);

                detailBinding.textDetailDiaryContents.setText(diary.getContents());

                detailBinding.detailButtonLayout.setVisibility(View.GONE);

                detailBinding.detailPhoto.isClickEnabled(false);
            }
        });
    }

    // newDiaryActivity와 같은 기능
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        detailBinding.detailPhoto.initPreTime();

        if (requestCode == UPDATE_PICKER_IMAGE_REQUEST) {
            if (data != null && data.getData() != null) { // 갤러리를 선택한 경우
                Uri selectedImageUri = data.getData();

                String path = Utility.getRealPathFromURI(this, selectedImageUri);
                diary.setPhoto(path);

                if (photoUri != null) {
                    getContentResolver().delete(photoUri, null, null);
                }
            } else { // 카메라를 선택한 경우
                if (photoUri != null) {
                    Bitmap imageBitmap = null;

                    String path = Utility.getRealPathFromURI(this, photoUri);
                    imageBitmap = Utility.getRotatedBitmap(path);

                    if (imageBitmap != null) {
                        diary.setPhoto(path);
                    }
                }
            }

            // 피커 중에 아무것도 선택하지 않은 경우도 있기 때문에 아무것도 선택하지 않은 경우에는 기존의 사진을 보여줘야한다.
            // 기존에 이미지가 없는 경우에는 디폴트 사진을 보여준다.
            if (diary.getPhoto().equals("")) {
                detailBinding.detailPhoto.setImageResource(R.drawable.default_placeholder_image);
                detailBinding.detailPhoto.setBackground(ContextCompat.getDrawable(this, R.color.white));
            } else {
                Bitmap photo = Utility.getRotatedBitmap(diary.getPhoto());
                detailBinding.detailPhoto.setImageBitmap(photo);
                detailBinding.detailPhoto.setBackground(ContextCompat.getDrawable(this, R.color.black));
            }
        }
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

//            diary.setMood(currentMood);
        }
    };

    private void resetSelectedState() {
        detailBinding.emojiHappyCheck.setVisibility(View.INVISIBLE);
        detailBinding.emojiSmileCheck.setVisibility(View.INVISIBLE);
        detailBinding.emojiBlankCheck.setVisibility(View.INVISIBLE);
        detailBinding.emojiSadCheck.setVisibility(View.INVISIBLE);
        detailBinding.emojiNervousCheck.setVisibility(View.INVISIBLE);

        currentMood = Const.Mood.NONE.value;
//        diary.setMood(currentMood);
    }

    private void initMood() {
        switch (currentMood) {
            case 1:
                detailBinding.emojiHappyCheck.setVisibility(View.VISIBLE);
                break;
            case 2:
                detailBinding.emojiSmileCheck.setVisibility(View.VISIBLE);
                break;
            case 3:
                detailBinding.emojiBlankCheck.setVisibility(View.VISIBLE);
                break;
            case 4:
                detailBinding.emojiSadCheck.setVisibility(View.VISIBLE);
                break;
            case 5:
                detailBinding.emojiNervousCheck.setVisibility(View.VISIBLE);
                break;
        }
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
        String contents = detailBinding.editDetailDiaryContents.getText().toString().trim();
        diary.setContents(contents);

        diary.setMood(currentMood);

        // 오늘의 기분 선택
        if (currentMood == Const.Mood.NONE.value) {
            new ConfirmDialog(getString(R.string.dialog_message_confirm_mood), null).show(getSupportFragmentManager(), "mood");
        } else {
            DatabaseUtility.updateDiary(this, Utility.getYear(), month, day, diary, isSuccess -> {
                if (isSuccess) {
                    isUpdate = true;

                    detailBinding.switchEditMode.setChecked(false);
                    Toast.makeText(getApplicationContext(), getString(R.string.message_update_diary), Toast.LENGTH_LONG).show();
                } else {
                    Toast.makeText(getApplicationContext(), getString(R.string.error_message_update_diary), Toast.LENGTH_LONG).show();
                }
            });
        }
    }
}