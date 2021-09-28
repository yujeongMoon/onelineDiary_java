package com.example.onelinediary.activity;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
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

import java.io.IOException;
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

        newDiaryBinding.todayDate.setText(Utility.getDate("yyyy년 MM월 dd일"));

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
                // 갤러리에서 선택한 이미지의 uri가 넘어온다.
                newDiaryBinding.photo.setImageURI(selectedImageUri);

                /*
                   TODO
                    위에서 이미지 선택 팝업 설정을 하는 과정에서 getContentResolver().insert()가 무조건 실행되면서
                    더미 이미지가 추가된다. 이미지를 선택해서 다음 단계로 넘어가면 더미 이미자가 사라진다.
                    나중에 수정 필요.
                */
                if (photoUri != null) {
                    getContentResolver().delete(photoUri, null, null);
                }

                diary.setPhoto(selectedImageUri.toString());
            } else { // 카메라를 선택했을 경우
                if (photoUri != null) {
                    // uri로부터 Bitmap 이미지를 생성
                    Bitmap imageBitmap = null;
                    /*
                        TODO
                         사진을 찍고 돌아왔을 때 이미지가 회전되어있는 이슈 수정 필요
                         사용자에게 회전을 할 수 있는 기능을 주거나 회전 방향이 일정한 경우, 개발자가 수정해줌.
                     */
                    try {
                        // 주어진 uri를 bitmap image로 만들어준다
                        // api 29에서 deprecated될 예정
                        imageBitmap = MediaStore.Images.Media.getBitmap(this.getContentResolver(), photoUri);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }

                    if (imageBitmap != null) {
                        newDiaryBinding.photo.setImageBitmap(imageBitmap);
                        diary.setPhoto(photoUri.toString());
                    } else {
                        newDiaryBinding.photo.setImageResource(R.drawable.default_placeholder_image);
                        diary.setPhoto("");
                    }
                }
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
        diary.setReportingDate(new SimpleDateFormat("yyyy년 MM월 dd일").format(new Date()));

        // 작성한 일기 컨텐츠 저장
        String contents = newDiaryBinding.diaryContents.getText().toString();
        diary.setContents(contents);

        // 오늘의 기분 선택
        if (currentMood == Const.Mood.NONE.value) {
            new ConfirmDialog("오늘의 기분을 입력해주세요!", null).show(getSupportFragmentManager(), "mood");
        } else {
            DatabaseUtility.writeNewDiary(this, diary, isSuccess -> {
                if (isSuccess) {
                    new ConfirmDialog("일기가 저장되었습니다.", v -> finish()).show(getSupportFragmentManager(), "newDiarySuccess");
                } else {
                    new ConfirmDialog("일기를 저장하는데 문제가 발생하였습니다. 다시 시도해주세요!", null).show(getSupportFragmentManager(), "newDiaryFailure");
                }
            });
        }
    }
}