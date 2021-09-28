package com.example.onelinediary.utiliy;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Environment;
import android.os.Parcelable;
import android.provider.MediaStore;
import android.provider.Settings;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.util.Date;

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

    public static Uri selectPhoto(Context context, int requestCode) {
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

        Uri photoUri = context.getContentResolver().insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, values);
        takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoUri);

        Intent pickIntent = new Intent(Intent.ACTION_PICK);
        pickIntent.setDataAndType(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, MediaStore.Images.Media.CONTENT_TYPE);

        String pickTitle = "사진 가져올 방법을 선택하세요.";
        Intent chooseIntent = Intent.createChooser(pickIntent, pickTitle);
        chooseIntent.putExtra(Intent.EXTRA_INITIAL_INTENTS, new Parcelable[]{takePictureIntent});

        // TODO startActivityForResult() deprecated 대체 메소드로 수정 필요.
        ((Activity)context).startActivityForResult(chooseIntent, requestCode);
//        registerForActivityResult(ActivityResultContract, new ActivityResultCallback<Object>() {
//            @Override
//            public void onActivityResult(Object result) {
//
//            }
//        });

        return photoUri;
    }

    /**
     * 새로운 이미지 파일을 생성해주는 메소드
     * @return 외부 저장소의 경로로 연결된 이미지 파일(이름은 그 날의 날짜와 시간으로 구성)
     * @throws IOException
     *
     * @return 특정 경로로 연결된 이미지 파일
     */
    public static File createImageFile(Context context) throws IOException {
        // 외부 저장소의 pictures 폴더 아래의 image 폴더에 이미지를 저장
        File storageDir = new File(context.getExternalFilesDir(Environment.DIRECTORY_PICTURES), "image");
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
        String currentPhotoPath = image.getAbsolutePath();
        return image;
    }

    /**
     * 새로 생성되는 이미지 파일의 이름을 그 날의 날짜와 시간으로 만들어 반환하는 메소드
     * @return 현재 날짜와 시간으로 만들어진 이미지 파일의 이름
     */
    public static String newImageFileName() {
        @SuppressLint("SimpleDateFormat")
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());

        return "JPEG_" + timeStamp + "_";
    }

    /**
     * uri를 bitmap image로 바꿔준다.
     * @param cr ContentResolver // TODO 정리 필요
     * @param uri 사진의 경로
     * @return 비트맵 이미지
     * @throws FileNotFoundException => openInputStream()
     * @throws IOException => close()
     */
    public static Bitmap getBitmap(ContentResolver cr, Uri uri) throws FileNotFoundException, IOException {
        InputStream input = cr.openInputStream(uri);
        Bitmap bitmap = BitmapFactory.decodeStream(input);
        input.close();
        return bitmap;
    }
}
