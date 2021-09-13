package com.example.onelinediary.activity;

import android.annotation.SuppressLint;
import android.content.ContentValues;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.ImageDecoder;
import android.graphics.Picture;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.Parcelable;
import android.provider.MediaStore;
import android.view.View;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.FileProvider;

import com.example.onelinediary.R;
import com.example.onelinediary.databinding.ActivityNewDiaryBinding;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class NewDiaryActivity extends AppCompatActivity {
    private ActivityNewDiaryBinding newDiaryBinding;

    private final int PICKER_IMAGE_REQUEST = 100;

    String currentPhotoPath;
    Uri photoUri;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        newDiaryBinding = ActivityNewDiaryBinding.inflate(getLayoutInflater());
        setContentView(newDiaryBinding.getRoot());
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();

        overridePendingTransition(R.anim.slide_in_down, R.anim.slide_out_down);
        finish();
    }

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

        if (requestCode == PICKER_IMAGE_REQUEST) {
            if (data != null) { // 갤러리를 선택했을 경우
                Uri selectedImageUri = data.getData();
                if (selectedImageUri != null) { // 갤러리에서 사진을 선택한 경우
                    // 갤러리에서 선택한 이미지의 uri가 넘어온다.
                    newDiaryBinding.photo.setImageURI(selectedImageUri);
                }

                /*
                   TODO
                    위에서 이미지 선택 팝업 설정을 하는 과정에서 getContentResolver().insert()가 무조건 실행되면서
                    더미 이미지가 추가된다. 이미지를 선택해서 다음 단계로 넘어가면 더미 이미자가 사라진다.
                    나중에 수정 필요.
                */
                if (photoUri != null) {
                    getContentResolver().delete(photoUri, null, null);
                }
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
                    newDiaryBinding.photo.setImageBitmap(imageBitmap);
                }
            }
        }
    }

    private File createImageFile() throws IOException {
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

    private String newImageFileName() {
        @SuppressLint("SimpleDateFormat")
        String timeStamp = new SimpleDateFormat("yyyMMdd_HHmmss").format(new Date());

        return "JPEG_" + timeStamp + "_";
    }
}