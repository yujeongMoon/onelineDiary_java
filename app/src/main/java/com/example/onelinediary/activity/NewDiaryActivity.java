package com.example.onelinediary.activity;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.example.onelinediary.R;
import com.example.onelinediary.constant.Const;
import com.example.onelinediary.databinding.ActivityNewDiaryBinding;
import com.example.onelinediary.dialog.ConfirmDialog;
import com.example.onelinediary.dto.Diary;
import com.example.onelinediary.utiliy.DatabaseUtility;
import com.example.onelinediary.utiliy.Utility;

import java.text.SimpleDateFormat;
import java.util.Date;

public class NewDiaryActivity extends AppCompatActivity{
    private ActivityNewDiaryBinding newDiaryBinding;

    private final int PICKER_IMAGE_REQUEST = 100;

    private int currentMood = Const.Mood.NONE.value;

    String currentPhotoPath;
    Uri photoUri;

    Diary diary = new Diary();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        newDiaryBinding = ActivityNewDiaryBinding.inflate(getLayoutInflater());
        setContentView(newDiaryBinding.getRoot());

        newDiaryBinding.todayDate.setText(Utility.getDate(Const.REPORTING_DATE_FORMAT));

        newDiaryBinding.emojiHappyLayout.setOnClickListener(onClickListener);
        newDiaryBinding.emojiSmileLayout.setOnClickListener(onClickListener);
        newDiaryBinding.emojiBlankLayout.setOnClickListener(onClickListener);
        newDiaryBinding.emojiSadLayout.setOnClickListener(onClickListener);
        newDiaryBinding.emojiNervousLayout.setOnClickListener(onClickListener);

        newDiaryBinding.photo.setOnClickListener(v -> photoUri = Utility.selectPhoto(NewDiaryActivity.this, PICKER_IMAGE_REQUEST));
    }

    View.OnClickListener onClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            if (v.getId() == R.id.emoji_happy_layout) {
                if (currentMood != Const.Mood.HAPPY.value) {
                    resetSelectedState();
                }

                if (newDiaryBinding.emojiHappyCheck.getVisibility() == View.INVISIBLE) {
                    newDiaryBinding.emojiHappyCheck.setVisibility(View.VISIBLE);
                    currentMood = Const.Mood.HAPPY.value;
                } else {
                    newDiaryBinding.emojiHappyCheck.setVisibility(View.INVISIBLE);
                    currentMood = Const.Mood.NONE.value;
                }
            } if (v.getId() == R.id.emoji_smile_layout) {
                if (currentMood != Const.Mood.SMILE.value) {
                    resetSelectedState();
                }
                if (newDiaryBinding.emojiSmileCheck.getVisibility() == View.INVISIBLE) {
                    newDiaryBinding.emojiSmileCheck.setVisibility(View.VISIBLE);
                    currentMood = Const.Mood.SMILE.value;
                } else {
                    newDiaryBinding.emojiSmileCheck.setVisibility(View.INVISIBLE);
                    currentMood = Const.Mood.NONE.value;
                }
            } if (v.getId() == R.id.emoji_blank_layout) {
                if (currentMood != Const.Mood.BLANK.value) {
                    resetSelectedState();
                }
                if (newDiaryBinding.emojiBlankCheck.getVisibility() == View.INVISIBLE) {
                    newDiaryBinding.emojiBlankCheck.setVisibility(View.VISIBLE);
                    currentMood = Const.Mood.BLANK.value;
                } else {
                    newDiaryBinding.emojiBlankCheck.setVisibility(View.INVISIBLE);
                    currentMood = Const.Mood.NONE.value;
                }
            } if (v.getId() == R.id.emoji_sad_layout) {
                if (currentMood != Const.Mood.SAD.value) {
                    resetSelectedState();
                }
                if (newDiaryBinding.emojiSadCheck.getVisibility() == View.INVISIBLE) {
                    newDiaryBinding.emojiSadCheck.setVisibility(View.VISIBLE);
                    currentMood = Const.Mood.SAD.value;
                } else {
                    newDiaryBinding.emojiSadCheck.setVisibility(View.INVISIBLE);
                    currentMood = Const.Mood.NONE.value;
                }
            } if (v.getId() == R.id.emoji_nervous_layout) {
                if (currentMood != Const.Mood.NERVOUS.value) {
                    resetSelectedState();
                }
                if (newDiaryBinding.emojiNervousCheck.getVisibility() == View.INVISIBLE) {
                    newDiaryBinding.emojiNervousCheck.setVisibility(View.VISIBLE);
                    currentMood = Const.Mood.NERVOUS.value;
                } else {
                    newDiaryBinding.emojiNervousCheck.setVisibility(View.INVISIBLE);
                    currentMood = Const.Mood.NONE.value;
                }
            }

            diary.setMood(currentMood);
        }
    };

    private void resetSelectedState() {
        newDiaryBinding.emojiHappyCheck.setVisibility(View.INVISIBLE);
        newDiaryBinding.emojiSmileCheck.setVisibility(View.INVISIBLE);
        newDiaryBinding.emojiBlankCheck.setVisibility(View.INVISIBLE);
        newDiaryBinding.emojiSadCheck.setVisibility(View.INVISIBLE);
        newDiaryBinding.emojiNervousCheck.setVisibility(View.INVISIBLE);

        currentMood = Const.Mood.NONE.value;
        diary.setMood(currentMood);
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();

//        overridePendingTransition(R.anim.slide_in_down, R.anim.slide_out_down);
        finish();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        // 카메라를 실행해서 외부의 저장하지 않고 바로 썸네일로 보여주는 경우
//                Bundle extras = data.getExtras();
//                if (extras != null) { // 카메라로 사진을 찍은 경우
//                // MediaStore.ACTION_IMAGE_CAPTURE
//                // 사진을 찍은 후 "data" 키 값으로 사진이 넘어온다.
//                    Bitmap imageBitmap = (Bitmap) extras.get("data"); // thumbnail
//                    newDiaryBinding.photo.setImageBitmap(imageBitmap);
//                }


        // 선택 팝업이 뜨고 선택을 하거나 하지 않아도 무조건 거치는 메소드이기 때문에
        // 팝업이 떴다고 가정하고 팝업이 내려갈 때 여기서 첫번째 클릭의 시간을 초기화 시켜준다.
        // 초기화를 시키지 않으면 팝업이 내려가는 동시에 이미지뷰를 클릭을 했을 때 시간이 짧아서 팝업이 뜨지 않는다.
        // 그렇기때문에 첫번째 클릭 시간을 초기화 시켜줘야한다.
        newDiaryBinding.photo.initPreTime();

        if (requestCode == PICKER_IMAGE_REQUEST) { // 카메라와 갤러리 선택 팝업을 선택한 경우
            if (data != null && data.getData() != null) { // 갤러리를 선택했을 경우
                Uri selectedImageUri = data.getData();

                String path = Utility.getRealPathFromURI(this, selectedImageUri);
                diary.setPhoto(path);
            } else { // 카메라를 선택했을 경우
                if (photoUri != null) {
                    // uri로부터 Bitmap 이미지를 생성
                    Bitmap imageBitmap = null;

                    String path = Utility.getRealPathFromURI(this, photoUri);
                    imageBitmap = Utility.getRotatedBitmap(path);

                    // photoUri는 무조건 생겨서 넘어오기 때문에 비트맵 이미지가 생성되는지 따로 체크한다.
                    if (imageBitmap != null) {
                        diary.setPhoto(path);
                    }
                }
            }

            // 피커 중에 아무것도 선택하지 않은 경우도 있기 때문에 아무것도 선택하지 않은 경우에는 기존의 사진을 보여줘야한다.
            // 기존에 이미지가 없는 경우에는 디폴트 사진을 보여준다.
            // diray의 photo 필드는 디폴트 값으로 ""로 설정되어있다.
            if (diary.getPhoto().equals("")) {
                newDiaryBinding.photo.setImageResource(R.drawable.default_placeholder_image);
            } else {
                Bitmap photo = Utility.getRotatedBitmap(diary.getPhoto());
                newDiaryBinding.photo.setImageBitmap(photo);
            }

            // photoUri는 무조건 생성되기 때문에 피커 중 아무것도 선택하지 않았거나 갤러리를 통해 이미지를 선택한 경우
            // 더미 이미지가 생기기 때문에 photoUri 경로로 연결된 이미지를 지워주기 제공자에 있는 이미지르 삭제해야한다.
            if (photoUri != null) {
                getContentResolver().delete(photoUri, null, null);
            }
        }
    }

    /**
     * 사진 등록이나 일기를 작성하고 기분을 선택한 후 저장 버튼을 눌러 DB에 저장을 한다.
     * 사진 등록이나 일기 작성은 선택 사항이지만 기분 선택은 필수로 정한다.
     * 선택하지 않은 상태로 저장 버튼을 누르면 기본 기분 아이콘아 선택 되도록 설정?
     *
     * @param view 저장 버튼 레이아웃
     */
    @SuppressLint("SimpleDateFormat")
    public void saveNewDiary(View view) {
        // 일기 작성한 시간 저장
        diary.setReportingDate(new SimpleDateFormat(Const.REPORTING_DATE_FORMAT).format(new Date()));

        // 작성한 일기 컨텐츠 저장
        String contents = newDiaryBinding.diaryContents.getText().toString();
        diary.setContents(contents);

        // 오늘의 기분 선택
        if (currentMood == Const.Mood.NONE.value) {
            new ConfirmDialog(getString(R.string.dialog_message_confirm_mood), null).show(getSupportFragmentManager(), "mood");
        } else {
            DatabaseUtility.writeNewDiary(this, diary, isSuccess -> {
                if (isSuccess) {
                    new ConfirmDialog(getString(R.string.message_save_diary),
                            v -> {
                                Const.addNewDiary = true;
                                NewDiaryActivity.this.finish();
                            }).show(getSupportFragmentManager(), "newDiarySuccess");
                } else {
                    new ConfirmDialog(getString(R.string.error_message_save_diary), null).show(getSupportFragmentManager(), "newDiaryFailure");
                }
            });
        }
    }
}