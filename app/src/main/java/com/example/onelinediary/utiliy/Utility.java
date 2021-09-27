package com.example.onelinediary.utiliy;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Parcelable;
import android.provider.MediaStore;
import android.provider.Settings;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

public class Utility {
    public static boolean isStringNullOrEmpty(String text) {
        return text == null || text.isEmpty();
    }

    /**
     * dp 값을 px 값으로 바꿔준다.
     * @param context 컨텍스트
     * @param dp px로 변환될 dp
     * @return px
     */
    public static int dpToPx(Context context, int dp) {
        return (int)(dp * context.getResources().getDisplayMetrics().density + 0.5f);
    }

    /**
     * 기기의 안드로이드 아이디를 가져온다.
     * 안드로이드 아이디를 가져오기 위해서는 manifest에 아래의 권한을 추가해야한다.
     * <uses-permission android:name="android.permission.READ_PHONE_STATE"/>
     *
     * @param context 컨텍스트
     * @return 안드로이드 아이디
     */
    @SuppressLint("HardwareIds")
    public static String getAndroidId(Context context) {
        return Settings.Secure.getString(context.getContentResolver(), Settings.Secure.ANDROID_ID);
    }

    /**
     * @return 현재 연도
     */
    @SuppressLint("SimpleDateFormat")
    public static String getYear() {
        return new SimpleDateFormat("yyyy").format(new Date());
    }

    /**
     * @return 현재 달
     */
    @SuppressLint("SimpleDateFormat")
    public static String getMonth() {
        return new SimpleDateFormat("MM").format(new Date());
    }

    /**
     * @return 현재 일
     */
    @SuppressLint("SimpleDateFormat")
    public static String getDay() {
        return new SimpleDateFormat("dd").format(new Date());
    }

    /**
     * 원하는 포맷을 입력하면 오늘의 날짜르 해당 포맷으로 만들어준다.
     * @param format 입력한 포맷 ex) "yyyy-MM-dd"
     * @return 포맷에 맞게 변환된 오늘 날짜
     */
    @SuppressLint("SimpleDateFormat")
    public static String getDate(String format) {
        return new SimpleDateFormat(format).format(new Date());
    }

//    public static Uri selectPhoto(Context context) {
//        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
//
//        // 외부 저장소에 이미지 저장하는 방법
//        // takePictureIntent.resolveActivity(getPackageManager()) != null
//        // 위의 조건문은 기기에 카메라 앱이 존재하는지 확인한다.
//        // 앱이 존재하지 않는 상태에서 startActivityForResult()를 호출하면 프로그램이 종료되기 때문에
//        // 이를 방지하기위해 체크한다.
////        if (takePictureIntent.resolveActivity(getPackageManager()) != null) {
////            File photoFile = null;
////            try {
////                photoFile = createImageFile();
////            } catch (IOException e) {
////                e.printStackTrace();
////            }
////
////            String packageName = getApplicationContext().getPackageName();
////            if (photoFile != null) {
////                photoUri = FileProvider.getUriForFile(
////                        this,
////                        packageName + ".fileprovider",
////                        photoFile
////                );
////                takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoUri);
////            }
////        }
//
//        // 공유 저장소(SDCard)에 이미지 저장하는 방법
//        ContentValues values = new ContentValues();
//        values.put(MediaStore.MediaColumns.DISPLAY_NAME, newImageFileName());
//        values.put(MediaStore.MediaColumns.MIME_TYPE, "image/jpeg");
////        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
////            values.put(MediaStore.MediaColumns.RELATIVE_PATH, "Pictures/onelineDiary");
////        }
//
//        Uri photoUri = context.getContentResolver().insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, values);
//        takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoUri);
//
//        Intent pickIntent = new Intent(Intent.ACTION_PICK);
//        pickIntent.setDataAndType(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, MediaStore.Images.Media.CONTENT_TYPE);
//
//        String pickTitle = "사진 가져올 방법을 선택하세요.";
//        Intent chooseIntent = Intent.createChooser(pickIntent, pickTitle);
//        chooseIntent.putExtra(Intent.EXTRA_INITIAL_INTENTS, new Parcelable[]{takePictureIntent});
//
//        // TODO startActivityForResult() deprecated 대체 메소드로 수정 필요.
//        ((Activity)context).startActivityForResult(chooseIntent, PICKER_IMAGE_REQUEST);
////        registerForActivityResult(ActivityResultContract, new ActivityResultCallback<Object>() {
////            @Override
////            public void onActivityResult(Object result) {
////
////            }
////        });
//    }

    /**
     * 새로 생성되는 이미지 파일의 이름을 그 날의 날짜와 시간으로 만들어 반환하는 메소드
     * @return 현재 날짜와 시간으로 만들어진 이미지 파일의 이름
     */
    public static String newImageFileName() {
        @SuppressLint("SimpleDateFormat")
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());

        return "JPEG_" + timeStamp + "_";
    }
}
