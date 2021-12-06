package com.example.onelinediary.utiliy;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.net.Uri;
import android.os.Environment;
import android.os.Handler;
import android.os.Looper;
import android.os.Parcelable;
import android.provider.MediaStore;
import android.provider.Settings;
import android.text.TextUtils;

import androidx.exifinterface.media.ExifInterface;

import com.example.onelinediary.R;
import com.example.onelinediary.constant.Const;
import com.example.onelinediary.dto.Diary;
import com.example.onelinediary.dto.PhotoInfo;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;

public class Utility {
    public static boolean isStringNullOrEmpty(String text) {
        return text == null || text.isEmpty();
    }

    /**
     * millisTime 시간만큼 지연을 시킨 후 해당 액티비티로 이동하는 메소드
     *
     * @param context 컨텍스트
     * @param activityClass 이동할 액티비티
     * @param millisTime 지연 시간
     */
    public static void startActivity(Context context, Class activityClass, int millisTime) {
        new Handler(Looper.getMainLooper()).postDelayed(() -> {
            Intent mainIntent = new Intent(context, activityClass);
            context.startActivity(mainIntent);
            ((Activity)context).finish();
        }, millisTime);
    }

    /**
     * 입력한 달의 마지막 날짜를 알려준다.
     *
     * @param year 연도
     * @param month 달
     * @return 해당 달의 마지막 날짜
     */
    public static int getLastDayNumberOfMonth(int year, int month) {
        Calendar calendar = Calendar.getInstance();
        calendar.set(year, month - 1, 1);
        return calendar.getActualMaximum(Calendar.DAY_OF_MONTH);
    }

    /**
     * 원하는 날짜의 요일(1:일요일, ... 7:토요일)을 알려준다.
     *
     * @param year 연도
     * @param month 달
     * @return 원하는 날짜의 요일
     */
    public static int getDayOfWeek(int year, int month, int day) {
        Calendar calendar = Calendar.getInstance();
        calendar.set(year, month - 1, day);
        return calendar.get(Calendar.DAY_OF_WEEK);
    }

    /**
     * 현재 날짜의 요일(1:일요일, ... 7:토요일)을 알려준다.
     *
     * @return 원하는 날짜의 요일
     */
    public static int getTodayOfWeek() {
        Calendar calendar = Calendar.getInstance();
        calendar.set(getYearToInt(), getMonthToInt() - 1, getDayToInt());
        return calendar.get(Calendar.DAY_OF_WEEK);
    }

    /**
     * 몇 일인지 입력하면 요일로 바꿔주는 메소드
     * @param day 원하는 날짜의 일
     * @return 요일
     */
    public static String changeDayOfWeekToString(int day) {
        String dayOfWeek = "";
        switch (day) {
            case 1:
                dayOfWeek = "일";
                break;
            case 2:
                dayOfWeek = "월";
                break;
            case 3:
                dayOfWeek = "화";
                break;
            case 4:
                dayOfWeek = "수";
                break;
            case 5:
                dayOfWeek = "목";
                break;
            case 6:
                dayOfWeek = "금";
                break;
            case 7:
                dayOfWeek = "토";
                break;
        }

        return dayOfWeek;
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
     * 관리자의 아이디와 같은 지 비교해주는 메소드
     * 관리자의 아이디와 같을 경우, true 아니면 false
     *
     * @param androidId 비교할 안드로이드 아이디
     * @return true or false
     */
    public static boolean isAdmin(String androidId) {
        return Const.ADMIN_ANDROID_ID.equals(androidId);
    }

    /**
     * 입력받은 달과 일이 오늘 날짜와 같은 지 비교해주는 메소드
     *
     * @param month 달
     * @param day 일
     * @return 오늘 날짜와 같으면 true, 다르면 false
     */
    public static boolean isSameDateWithToday(String month, String day) {
        return Utility.getMonth().equals(month) && (Utility.getDayToInt() == Integer.parseInt(day));
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
     * @return integer 현재 연도
     */
    public static int getYearToInt() {
        return Calendar.getInstance().get(Calendar.YEAR);
    }

    /**
     * 인덱스가 0부터 시작하기 때문에 1을 더해준다.
     * 0이 1월
     * @return integer 현재 달
     */
    public static int getMonthToInt() {
        return Calendar.getInstance().get(Calendar.MONTH) + 1;
    }

    /**
     * @return integer 현재 일
     */
    public static int getDayToInt() {
        return Calendar.getInstance().get(Calendar.DAY_OF_MONTH);
    }

    /**
     * hour in am/pm : h
     * hour in day(24) : k
     * @return 현재 시간(시)
     */
    @SuppressLint("SimpleDateFormat")
    public static String getTime_kk() {
        return new SimpleDateFormat("kk").format(new Date());
    }

    @SuppressLint("SimpleDateFormat")
    public static String getTime_a_hh_mm() {
        // AM, PM이 아니라 오전, 오후를 반환해준다.
        String time = new SimpleDateFormat("a").format(new Date());

        return time + " " + new SimpleDateFormat("h:mm").format(new Date());
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

    /**
     * 원하는 포맷을 입력하면 오늘의 날짜를 해당 포맷으로 만들어준다.
     * 요일을 추가해서 보여준다.
     * @param format 입력한 포맷 ex) "yyyy-MM-dd"
     * @return 포맷에 맞게 변환된 오늘 날짜
     */
    @SuppressLint("SimpleDateFormat")
    public static String getDateWithDayOfWeek(String format) {
        String day = changeDayOfWeekToString(getTodayOfWeek());
        return new SimpleDateFormat(format).format(new Date()) + " (" + day + ")";
    }

    // TODO 두 날짜가 같은 날짜인지 비교해주는 메소드

    public static Uri selectPhoto(Context context, int requestCode) {
        // 카메라를 사용하여 사진을 찍게 해주는 인텐트
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

        /*
            contentProvider에 데이터를 삽입하기 위해서 contentValues 객체를 생성한다.
            각각의 컬럼과 값을 저장하고 contentResolver 객체에 insert 한다.
            그리고 반환되는 uri가 카메라로 찍은 사진의 경로가 된다.

            contentResolver 객체의 query() 메소드 통해 커서 안에서 절대 경로를 알아낸다.
         */

        // 공유 저장소(SDCard)에 이미지 저장하는 방법
        ContentValues values = new ContentValues();
        values.put(MediaStore.MediaColumns.DISPLAY_NAME, newImageFileName());
        values.put(MediaStore.MediaColumns.MIME_TYPE, "image/jpeg");
//        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
//            values.put(MediaStore.MediaColumns.RELATIVE_PATH, "Pictures/onelineDiary");
//        }

        Uri photoUri = context.getContentResolver().insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, values);
        takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoUri); // 카메라 관련 인텐트

        // 사용자에게 선택 사항을 제공해주는 인텐트
        Intent pickIntent = new Intent(Intent.ACTION_PICK);
        // 이미지 타입 관련 앱 보여주기
        // 아래의 설정이 없으면 갤러리나 구글 포토 등의 이미지 관련 앱이 보이지 않는다.
        pickIntent.setDataAndType(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, MediaStore.Images.Media.CONTENT_TYPE);

        Intent chooseIntent = Intent.createChooser(pickIntent, context.getString(R.string.message_picker));
        chooseIntent.putExtra(Intent.EXTRA_INITIAL_INTENTS, new Parcelable[]{takePictureIntent}); // 위의 피커에 카메라도 추가

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
     * @throws IOException 에러
     *
     * @return 외부 저장소의 경로로 연결된 이미지 파일(이름은 그 날의 날짜와 시간으로 구성)
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
     * @param cr ContentResolver
     * @param uri 사진의 경로
     * @return 비트맵 이미지
     * @throws FileNotFoundException => openInputStream()
     * @throws IOException => close()
     *
     * 구글 포토를 사용한 경우는 제외
     * permission denial, openInputStream()에서 오류가 남.
     * google photo authority => "com.google.android.apps.photos.contentprovider"
     */
    public static Bitmap getBitmap(ContentResolver cr, Uri uri) throws FileNotFoundException, IOException {
        Bitmap bitmap;

        InputStream input = cr.openInputStream(uri);
        bitmap = BitmapFactory.decodeStream(input);
        input.close();

        return bitmap;
    }

    // 1024 바이트 버퍼를 두고 파일에 쓰는 메소드
    public static void copyInputStreamToFile(InputStream input, File file) {
        try {
            FileOutputStream outputStream = new FileOutputStream(file);
            int read;
            byte[] bytes = new byte[1024];

            while ((read = input.read(bytes)) != -1) {
                outputStream.write(bytes, 0, read);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * 입력된 uri의 절대 경로 명을 찾아주는 메소드
     *
     * @param context 컨텍스트
     * @param photoUri 이미지의 uri
     * @return 이미지의 절대 경로
     */
    public static String getRealPathFromURI(Context context, Uri photoUri) {
        int index = 0;
        String[] projections = {MediaStore.Images.Media.DATA};
        Cursor cursor = context.getContentResolver().query(photoUri, projections, null, null, null);

        if (cursor == null) {
            return "";
        }

        if (cursor.moveToFirst()) {
            index = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
        }

        String realPath = cursor.getString(index);
        cursor.close();

        return realPath;
    }

    /**
     * 입력된 경로에 있는 사진이 회전이 된 상태인지 파악하는 메소드
     * ExifInterface.TAG_ORIENTATION => 회전된 각도
     * 0이나 null이 반환된다면 회전이 되지 않은 상태를 의미한다.
     * @param path 사진의 절대 경로
     * @return 사진의 회전 상태
     */
    public static int getOrientation(String path) {
        ExifInterface exif = null;

        try {
            exif = new ExifInterface(path);
        } catch (IOException e) {
            e.printStackTrace();
        }

        if (exif != null) {
            return exif.getAttributeInt(ExifInterface.TAG_ORIENTATION, ExifInterface.ORIENTATION_UNDEFINED);
        } else {
            return -1; // 경로에 접근하지 못하였거나 오류가 발생하면 -1을 반환한다.
        }
    }

    /**
     * 입력된 비트맵 이미지를 회전 상태에 따라 맞게 원래대로 돌려주는 메소드
     * 회전되지 않은 상태라면 그대로 입력받은 비트맵 이미지를 반환하고
     * 회전이 필요한 상태라면 회전 한 후의 비트맵 이미지를 반환한다.
     *
     * 카메라로 사진을 찍거나 구글 포토에서 절대 경로로 사진을 불러와 이미지 뷰에 띄울 때
     * 이미지가 회전되어 보이는 현상을 해결하기 위해 추가되었다.
     *
     * @param bitmap 비트맵 이미지
     * @param orientation 이미지의 회전 상태
     * @return 원래 상태의 비트맵 이미지
     */
    public static Bitmap rotateBitmap(Bitmap bitmap, int orientation) {
        Matrix matrix = new Matrix();
        switch (orientation) {
            case ExifInterface.ORIENTATION_NORMAL:
                return bitmap;
            case ExifInterface.ORIENTATION_FLIP_HORIZONTAL:
                matrix.setScale(-1, 1);
                break;
            case ExifInterface.ORIENTATION_ROTATE_180:
                matrix.setRotate(180);
                break;
            case ExifInterface.ORIENTATION_FLIP_VERTICAL:
                matrix.setRotate(180);
                matrix.postScale(-1, 1);
                break;
            case ExifInterface.ORIENTATION_TRANSPOSE:
                matrix.setRotate(90);
                matrix.postScale(-1, 1);
                break;
            case ExifInterface.ORIENTATION_ROTATE_90:
                matrix.setRotate(90);
                break;
            case ExifInterface.ORIENTATION_TRANSVERSE:
                matrix.setRotate(-90);
                matrix.postScale(-1, 1);
                break;
            case ExifInterface.ORIENTATION_ROTATE_270:
                matrix.setRotate(-90);
                break;
            default:
                return bitmap;
        }
        try {
            Bitmap bmRotated = Bitmap.createBitmap(bitmap, 0, 0, bitmap.getWidth(), bitmap.getHeight(), matrix, true);
            bitmap.recycle();
            return bmRotated;
        }
        catch (OutOfMemoryError e) {
            e.printStackTrace();
            return null;
        }
    }

    /**
     * 사진의 절대 경로를 입력하면 파일을 비트맵 이미지를 생성하고 원래 회전 상태로 만들어주는 메소드
     *
     * 반복적으로 사용되는 코드여서 한번에 메소드로 만들었다.
     *
     * @param path 사진의 절대 경로
     * @return 일기 작성화면이나 상세화면에서 보여질 비트맵 이미지
     */
    public static Bitmap getRotatedBitmap(String path) {
        Bitmap photo = BitmapFactory.decodeFile(path); // 실제 경로를 통해 파일을 비트맵 이미지로 변경
        int orientation = Utility.getOrientation(path); // 이미지가 회전되어 있는 지 확인

        return Utility.rotateBitmap(photo, orientation); // 이미지가 회전되어 있을 경우, 원래대로 회전 시킨 후 비트맵 이미지를 반환
    }

    public static ArrayList<PhotoInfo> createPhotoInfoList(ArrayList<String> pathList) {
        ArrayList<PhotoInfo> bitmapList = new ArrayList<>();

        for (String path : pathList) {
            Bitmap image = Utility.getRotatedBitmap(path);
            bitmapList.add(new PhotoInfo(path, image));
        }

        return bitmapList;
    }

    public static String SPFileKey = "__oneLineDiary__";

    public static SharedPreferences getSharedPreference(Context context) {
        return context.getSharedPreferences(SPFileKey, Context.MODE_PRIVATE);
    }

    public static void putString(Context context, String key, String value) {
        SharedPreferences.Editor editor = getSharedPreference(context).edit();
        editor.putString(key, value);
        editor.apply();
    }

    public static void putBoolean(Context context, String key, Boolean value) {
        SharedPreferences.Editor editor = getSharedPreference(context).edit();
        editor.putBoolean(key, value);
        editor.apply();
    }

    public static void putInt(Context context, String key, int value) {
        SharedPreferences.Editor editor = getSharedPreference(context).edit();
        editor.putInt(key, value);
        editor.apply();
    }

    public static int getInt(Context context, String key) {
        return getSharedPreference(context).getInt(key, 0);
    }

    public static String getString(Context context, String key) {
        return getSharedPreference(context).getString(key, "");
    }

    public static Boolean getBoolean(Context context, String key, boolean defValue) {
        return getSharedPreference(context).getBoolean(key, defValue);
    }

    public static void putWidgetStatus(Context context, HashMap<Integer, Boolean> sMap) {

        Iterator<Integer> iterator = sMap.keySet().iterator();
        JSONArray jsonArray = new JSONArray();

        while (iterator.hasNext()) {
            int sKey = iterator.next();
            boolean status = sMap.get(sKey);
            try {
                JSONObject jsonObject = new JSONObject();
                jsonObject.put("statusKey", sKey);
                jsonObject.put("status", status);
                jsonArray.put(jsonObject);
                putString(context, "WidgetStatus", jsonArray.toString());
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }

    public static HashMap<Integer, Boolean> getWidgetStatusMap(Context context) {
        HashMap<Integer, Boolean> sMap = new HashMap<>();

        String jsonString = getString(context, "WidgetStatus");

        try {
            JSONArray jsonArray = new JSONArray(jsonString);
            for(int i = 0; i < jsonArray.length(); i++) {

                JSONObject object = jsonArray.getJSONObject(i);
                int sKey = object.getInt("statusKey");
                boolean status = object.getBoolean("status");
                sMap.put(sKey, status);

            }
        } catch (JSONException e) {
            e.printStackTrace();
        }

        return sMap;
    }

    public static void clearWidgetStatusMap(Context context) {
        SharedPreferences.Editor editor = getSharedPreference(context).edit();
        editor.remove("WidgetStatus");
        editor.commit();
    }

    /***
     * <pre>리소스 명을 입력하면 drawable id로 변환해줌.</pre>
     * @param res 리소스명
     * @return id로 변환된 리소스
     */
    public static int getResourceImage(Context context, String res){
        return context.getResources().getIdentifier(res,"drawable",context.getPackageName());
    }

    /**
     * 2021.11.12
     * 이모지를 전체적으로 변경하고 추가하면서 저장 방식을 변경(mood type(int) -> emoji imageFile name(String))
     * 이 전의 일기를 유지하기 위해 이모티콘을 마이그레이션 하여 비슷한 이모지로 보일 수 있도록 하기위해 사용한다.
     *
     * mood type(diary.getMood) 값이 있을 경우, 값에 따라 맞는 비슷한 이모지의 리소스 아이디를 반환해준다.
     * 없을 경우, 이모지 이름 값(diary.getIconName)으로 리소스 아이디 값을 가져와서 반환해준다.
     *
     * @param context 컨텍스트
     * @param diary 마이그레이션 할 일기
     * @return 이모지의 리소스 아이디
     */
    public static int migrationMoodToEmoji(Context context, Diary diary) {
        if (TextUtils.isEmpty(diary.getIconName())) {
            switch (diary.getMood()) {
                case 1:
                    return R.drawable.happy;
                case 2:
                    return R.drawable.shy;
                case 3:
                default:
                    return R.drawable.dd;
                case 4:
                    return R.drawable.sad;
                case 5:
                    return R.drawable.nervous;
            }
        } else {
            return getResourceImage(context, diary.getIconName());
        }
    }

    /**
     * Emoticon 클래스에 이미지 파일 명을 저장하기 때문에 리소스 아이디 대신 이미지 파일명을 반횐하는 기능이 필요
     *
     * @param diary 마이그레이션 할 일기
     * @return 이모지 파일의 이름
     */
    public static String migrationMoodToEmojiName(Diary diary) {
        if (TextUtils.isEmpty(diary.getIconName())) {
            switch (diary.getMood()) {
                case 1:
                    return "happy";
                case 2:
                    return "shy";
                case 3:
                default:
                    return "dd";
                case 4:
                    return "sad";
                case 5:
                    return "nervous";
            }
        } else {
            return diary.getIconName();
        }
    }
}
