package com.example.onelinediary.activity;

import android.annotation.SuppressLint;
import android.content.ContentValues;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.Parcelable;
import android.provider.MediaStore;
import android.view.View;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.example.onelinediary.R;
import com.example.onelinediary.databinding.ActivityNewDiaryBinding;
import com.example.onelinediary.dialog.ConfirmDialog;
import com.example.onelinediary.dto.Diary;
import com.example.onelinediary.utiliy.DatabaseUtility;
import com.example.onelinediary.utiliy.Utility;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class NewDiaryActivity extends AppCompatActivity{
    private ActivityNewDiaryBinding newDiaryBinding;

    // 기분 정의
    public enum Mood {
        NONE(0), HAPPY(1), SMILE(2), BLANK(3), SAD(4), NERVOUS(5);

        private final int value;
        Mood(int value) { this.value = value; }
        public int getValue() { return value; }
    }

    private final int PICKER_IMAGE_REQUEST = 100;

    private int currentMood = Mood.NONE.value;

    String currentPhotoPath;
    Uri photoUri;

    Diary diary = new Diary();

    FirebaseDatabase database = FirebaseDatabase.getInstance();
    DatabaseReference reference = database.getReference(); // default

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
    }

    View.OnClickListener onClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            if (v.getId() == R.id.emoji_happy_layout) {
                if (currentMood != Mood.HAPPY.value) {
                    resetSelectedState();
                }

                if (newDiaryBinding.emojiHappyCheck.getVisibility() == View.INVISIBLE) {
                    newDiaryBinding.emojiHappyCheck.setVisibility(View.VISIBLE);
                    currentMood = Mood.HAPPY.value;
                } else {
                    newDiaryBinding.emojiHappyCheck.setVisibility(View.INVISIBLE);
                    currentMood = Mood.NONE.value;
                }
            } if (v.getId() == R.id.emoji_smile_layout) {
                if (currentMood != Mood.SMILE.value) {
                    resetSelectedState();
                }
                if (newDiaryBinding.emojiSmileCheck.getVisibility() == View.INVISIBLE) {
                    newDiaryBinding.emojiSmileCheck.setVisibility(View.VISIBLE);
                    currentMood = Mood.SMILE.value;
                } else {
                    newDiaryBinding.emojiSmileCheck.setVisibility(View.INVISIBLE);
                    currentMood = Mood.NONE.value;
                }
            } if (v.getId() == R.id.emoji_blank_layout) {
                if (currentMood != Mood.BLANK.value) {
                    resetSelectedState();
                }
                if (newDiaryBinding.emojiBlankCheck.getVisibility() == View.INVISIBLE) {
                    newDiaryBinding.emojiBlankCheck.setVisibility(View.VISIBLE);
                    currentMood = Mood.BLANK.value;
                } else {
                    newDiaryBinding.emojiBlankCheck.setVisibility(View.INVISIBLE);
                    currentMood = Mood.NONE.value;
                }
            } if (v.getId() == R.id.emoji_sad_layout) {
                if (currentMood != Mood.SAD.value) {
                    resetSelectedState();
                }
                if (newDiaryBinding.emojiSadCheck.getVisibility() == View.INVISIBLE) {
                    newDiaryBinding.emojiSadCheck.setVisibility(View.VISIBLE);
                    currentMood = Mood.SAD.value;
                } else {
                    newDiaryBinding.emojiSadCheck.setVisibility(View.INVISIBLE);
                    currentMood = Mood.NONE.value;
                }
            } if (v.getId() == R.id.emoji_nervous_layout) {
                if (currentMood != Mood.NERVOUS.value) {
                    resetSelectedState();
                }
                if (newDiaryBinding.emojiNervousCheck.getVisibility() == View.INVISIBLE) {
                    newDiaryBinding.emojiNervousCheck.setVisibility(View.VISIBLE);
                    currentMood = Mood.NERVOUS.value;
                } else {
                    newDiaryBinding.emojiNervousCheck.setVisibility(View.INVISIBLE);
                    currentMood = Mood.NONE.value;
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

        currentMood = Mood.NONE.value;
        diary.setMood(currentMood);
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();

        overridePendingTransition(R.anim.slide_in_down, R.anim.slide_out_down);
        finish();
    }

    /**
     * 이미지 뷰를 클릭했을 때, 카메라와 갤러리 선택 팝업이 띄워준다.
     * TODO 이미지 뷰 더블 클릭을 막아야 함.
     * @param view 클릭한 버튼
     */
    @SuppressLint("QueryPermissionsNeeded")
    public void selectPhoto(View view) {
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);

        // 외부 저장소에 이미지 저장하는 방법
        // takePictureIntent.resolveActivity(getPackageManager()) != null
        // 위의 조건문은 기기에 카메라 앱이 존재하는지 확인한다.
        // 앱이 존재하지 않는 상태에서 startActivityForResult()를 호출하면 프로그램이 종료되기 때문에
        // 이를 방지하기위해 체크한다.
//        if (takePictureIntent.resolveActivity(getPackageManager()) != null) {
//            File photoFile = null;
//            try {
//                photoFile = createImageFile();
//            } catch (IOException e) {
//                e.printStackTrace();
//            }
//
//            String packageName = getApplicationContext().getPackageName();
//            if (photoFile != null) {
//                photoUri = FileProvider.getUriForFile(
//                        this,
//                        packageName + ".fileprovider",
//                        photoFile
//                );
//                takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoUri);
//            }
//        }

        // 공유 저장소(SDCard)에 이미지 저장하는 방법
        ContentValues values = new ContentValues();
        values.put(MediaStore.MediaColumns.DISPLAY_NAME, newImageFileName());
        values.put(MediaStore.MediaColumns.MIME_TYPE, "image/jpeg");
//        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
//            values.put(MediaStore.MediaColumns.RELATIVE_PATH, "Pictures/onelineDiary");
//        }

        photoUri = getContentResolver().insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, values);
        takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoUri);

        Intent pickIntent = new Intent(Intent.ACTION_PICK);
        pickIntent.setDataAndType(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, MediaStore.Images.Media.CONTENT_TYPE);

        String pickTitle = "사진 가져올 방법을 선택하세요.";
        Intent chooseIntent = Intent.createChooser(pickIntent, pickTitle);
        chooseIntent.putExtra(Intent.EXTRA_INITIAL_INTENTS, new Parcelable[]{takePictureIntent});

        // TODO startActivityForResult() deprecated 대체 메소드로 수정 필요.
        startActivityForResult(chooseIntent, PICKER_IMAGE_REQUEST);
//        registerForActivityResult(ActivityResultContract, new ActivityResultCallback<Object>() {
//            @Override
//            public void onActivityResult(Object result) {
//
//            }
//        });
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
                        imageBitmap = MediaStore.Images.Media.getBitmap(this.getContentResolver(), photoUri);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }

                    if (imageBitmap != null) {
                        newDiaryBinding.photo.setImageBitmap(imageBitmap);
                        diary.setPhoto(photoUri.toString());
                    } else {
                        newDiaryBinding.photo.setImageResource(R.drawable.default_placeholder_image);
                        //TODO 빈문자열이면 디폴트 이미지를 보여주거나 이미지 항목을 gone 처리함.
                        diary.setPhoto("");
                    }
                }
            }
        }
    }

    /**
     * 새로운 이미지 파일을 생성해주는 메소드
     * @return 외부 저장소의 경로로 연결된 이미지 파일(이름은 그 날의 날짜와 시간으로 구성)
     * @throws IOException
     */
    private File createImageFile() throws IOException {
        // 외부 저장소의 pictures 폴더 아래의 image 폴더에 이미지를 저장
        File storageDir = new File(getExternalFilesDir(Environment.DIRECTORY_PICTURES), "image");
        if (!storageDir.exists()) {
            // mkdir() : 한 번에 하나의 디렉토리 생성
            // mkdirs() : 한 번에 여러 디렉토리 생성
            storageDir.mkdir();
        }

        File image = File.createTempFile(
                newImageFileName(), // prefix
                ".jpg", // suffix
                storageDir // directory
        );

        // 파일 저장
        currentPhotoPath = image.getAbsolutePath();
        return image;
    }

    /**
     * 새로 생성되는 이미지 파일의 이름을 그 날의 날짜와 시간으로 만들어 반환하는 메소드
     * @return 현재 날짜와 시간으로 만들어진 이미지 파일의 이름
     */
    private String newImageFileName() {
        @SuppressLint("SimpleDateFormat")
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());

        return "JPEG_" + timeStamp + "_";
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
        diary.setReportingDate(new SimpleDateFormat("yyyy-MM-dd").format(new Date()));

        // 작성한 일기 컨텐츠 저장
        String contents = newDiaryBinding.diaryContents.getText().toString();
        diary.setContents(contents);

        // 오늘의 기분 선택
        if (currentMood == Mood.NONE.value) {
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