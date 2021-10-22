package com.example.onelinediary.activity;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.FragmentActivity;

import com.example.onelinediary.R;
import com.example.onelinediary.adapter.MainPagerAdapter;
import com.example.onelinediary.constant.Const;
import com.example.onelinediary.databinding.ActivityMainBinding;
import com.example.onelinediary.dialog.CustomProgressDialog;
import com.example.onelinediary.dto.Diary;
import com.example.onelinediary.utiliy.DatabaseUtility;
import com.example.onelinediary.utiliy.LocationUtility;
import com.example.onelinediary.utiliy.Utility;
import com.example.onelinediary.utiliy.WeatherUtility;

import java.util.ArrayList;

public class MainActivity extends FragmentActivity {
    private ActivityMainBinding mainBinding;

    boolean loading = false;

    @SuppressLint("InlinedApi")
    private final String[] permissions = {
            Manifest.permission.CAMERA,
            Manifest.permission.WRITE_EXTERNAL_STORAGE,
            Manifest.permission.READ_EXTERNAL_STORAGE,
            Manifest.permission.ACCESS_COARSE_LOCATION,
            Manifest.permission.ACCESS_FINE_LOCATION
//            Manifest.permission.ACCESS_MEDIA_LOCATION
    };

    private final int PERMISSION_REQUEST = 100;

    private MainPagerAdapter pagerAdapter;
    private CustomProgressDialog progressDialog = null;

    boolean pressedBackOnce = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mainBinding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(mainBinding.getRoot());

        // 필요한 권한 체크
        // 카메라나 저장소 접근 등 관련 권한을 사용자에게 승인 받는다.
        checkPermission(permissions, PERMISSION_REQUEST);

        if (Const.diaryList == null || Const.diaryList.size() == 0)
            loading = true;

        if (loading) {
            progressDialog = new CustomProgressDialog(this);
            progressDialog.show();
        }

        LocationUtility.requestLocationUpdate(this, listener);

        if (Const.currentLocation == null) {
            Const.currentLocation = LocationUtility.getLastKnownLocation(this);
        }

        DatabaseUtility.readYearDiaryList(this, Utility.getYear(), isSuccess -> {
            if (progressDialog != null) {
                progressDialog.dismiss();
            }

            if (pagerAdapter == null) {
                pagerAdapter = new MainPagerAdapter();
                pagerAdapter.setDiaryInterface(this::checkExistDiary);
                mainBinding.pager.setAdapter(pagerAdapter);

                // 배경화면을 현재 날씨 아이콘으로 바꿔준다.
//                if (Const.weather != -1) {
//                    pagerAdapter.setWeather(Const.weather);
//                    pagerAdapter.notifyDataSetChanged();
//                }
                // 현재 달의 페이지를 보여준다.
                // 데이터가 순서대로 들어가기 때문에 diaryList의 마지막이 현재 달!
                if (!Const.monthKeyList.isEmpty()) {
                    // onResume() 다음에 setCurrentItem()이 동작하지 않는다는 오류가 있다.
                    // 그걸 해결하기 위해 post()를 사용해서 현재 아이템의 위치를 설정해준다.
                    // smoothScroll = false : 애니메이션 없음
                    mainBinding.pager.post(() -> mainBinding.pager.setCurrentItem(Const.monthKeyList.size() - 1, false));
                }
            }
            loading = false;
        });

        mainBinding.btnAddNewDiary.setOnClickListener(v -> addNewDiary());
        mainBinding.btnSetting.setOnClickListener(v -> gotoSettingActivity());
        mainBinding.btnNotice.setOnClickListener(v -> Toast.makeText(getApplicationContext(), "서비스 준비중 입니다.", Toast.LENGTH_SHORT).show());
    }

    private void checkExistDiary() {
        mainBinding.btnAddNewDiary.setImageResource(R.drawable.add_circle_outline_black);

        // 현재 달의 일기 리스트
        ArrayList<Diary> currentMonthDiaryList = Const.diaryList.get(Utility.getMonth());
        // 오늘의 일기(이번 달 일기의 리스트의 마지막이 오늘의 일기)
        Diary todayDiary = currentMonthDiaryList.get(currentMonthDiaryList.size() - 1);

        if (Const.diaryList != null && Const.diaryList.containsKey(Utility.getMonth())) {
            if (todayDiary.getDay().equals(Utility.getDay())) {
                switch (todayDiary.getMood()) {
                    case 1:
                        mainBinding.btnAddNewDiary.setImageResource(R.drawable.emoji_happy_icon);
                        break;
                    case 2:
                        mainBinding.btnAddNewDiary.setImageResource(R.drawable.emoji_blushing_icon);
                        break;
                    case 3:
                        mainBinding.btnAddNewDiary.setImageResource(R.drawable.emoji_blank_icon);
                        break;
                    case 4:
                        mainBinding.btnAddNewDiary.setImageResource(R.drawable.emoji_consoling_icon);
                        break;
                    case 5:
                        mainBinding.btnAddNewDiary.setImageResource(R.drawable.emoji_nervous_icon);
                        break;
                }
            }
        }
    }

    @Override
    protected void onResume() {
        super.onResume();

        // 일기가 삭제되었거나 화면 이동 후 어뎁터에게 알린다.
        notifyToPager();

        // TODO 일기장 화면에서 사용하기 위해서 현재 위치의 날씨 정보를 얻어 둔다.
        getCurrentWeather(Const.currentLocation);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        LocationUtility.getLocationManager(this).removeUpdates(listener);
    }

    public LocationListener listener = location -> {
        Const.currentLocation = location;
        getCurrentWeather(Const.currentLocation);
    };

    /*
        back키를 눌러서 앱을 종료할 때 두번 연속으로 클릭할 시 앱을 종료한다.
        한 번 누른 후 2초안에 눌러야 연속으로 보고 앱을 종료한다. 2초가 넘으면 플래그를 초기화 시킨다.
     */
    @Override
    public void onBackPressed() {
        if (pressedBackOnce) {
            super.onBackPressed();
        }

        pressedBackOnce = true;
        Toast.makeText(getApplicationContext(), "앱을 종료하려면 한번 더 눌러주세요", Toast.LENGTH_SHORT).show();

        // 플래그 초기화
        new Handler(Looper.getMainLooper()).postDelayed(() -> pressedBackOnce = false, 2000);
    }

    @SuppressLint("NotifyDataSetChanged")
    public void notifyToPager() {
        if (pagerAdapter != null) {
            pagerAdapter.notifyDataSetChanged();

            // 새로 일기를 쓴 경우에는 어느 페이지에 있어도 현재 날짜가 포함된 제일 마지막 페이지로 이동하게 한다.
            if (Const.addNewDiary) {
                Const.addNewDiary = false;
                // 시간차를 주면서 페이저의 포지션 바꾸기
                mainBinding.pager.post(() -> mainBinding.pager.setCurrentItem(Const.monthKeyList.size() - 1, false));
            }
        }
    }

    private void getCurrentWeather(Location location) {
        if (location != null) {
            WeatherUtility.getWeather(location.getLatitude(), location.getLongitude(), (isSuccess, result, error) -> {
                if (isSuccess) {
                    int resId;
                    String w;
                    switch (result.getPTY()) {
                        case "1":
                            resId = R.drawable.weather_rain;
                            w = "1";
                            break;

                        case "3":
                            resId = R.drawable.weather_snow;
                            w = "3";
                            break;

                        case "4":
                            resId = R.drawable.weather_shower;
                            w = "4";
                            break;

                        case "0":
                            if (result.getSKY().equals("1")) {
                                resId = R.drawable.weather_sunny;
                                w = "01";
                            } else if (result.getSKY().equals("3")) {
                                resId = R.drawable.weather_cloud;
                                w = "03";
                            } else {
                                resId = R.drawable.weather_blur;
                                w = "04";
                            }
                            break;

                        default:
                            resId = -1;
                            w = "";
                    }

                    Const.weatherResId = resId;
                    Const.weather = w;
                } else {
                    Log.d("MainActivity", error);
//                        Toast.makeText(getApplicationContext(), "현재 날씨를 가져오는 과정에서 문제가 발생하였습니다.", Toast.LENGTH_LONG).show();
                }
            });
        } else {
            Log.d("MainActivity", "위치 정보가 없습니다.");
//            Toast.makeText(getApplicationContext(), "위치 정보가 없습니다.", Toast.LENGTH_LONG).show();
        }
    }

    //    @Override
//    public void onComplete(boolean isSuccess, HashMap<String, ArrayList<Diary>> result) {
//        if (isSuccess) {
//            Iterator<String> iterator = result.keySet().iterator();
//            while (iterator.hasNext()) {
//                String key = iterator.next();
//                Log.d(MainActivity.class.getSimpleName(),"month : " + key);
//                for(Diary diary : result.get(key)) {
//                    Log.d(MainActivity.class.getSimpleName(),"modd : " + diary.getMood());
//                }
//            }
//        }
//    }

    public void addNewDiary() {
        Intent newDiaryIntent = new Intent(this, NewDiaryActivity.class);
        startActivity(newDiaryIntent);

//        overridePendingTransition(R.anim.slide_in_up, R.anim.slide_out_up);
    }

    public void gotoSettingActivity() {
        Intent settingIntent = new Intent(this, SettingActivity.class);
        startActivity(settingIntent);
    }

    private void checkPermission(String[] permissions, int permissionRequest) {
        ArrayList<String> permissionList = new ArrayList<>();

        for (String permission : permissions) {
            int result = ContextCompat.checkSelfPermission(this, permission);
            if (result != PackageManager.PERMISSION_GRANTED) {
                permissionList.add(permission);
            }
        }

        if (!permissionList.isEmpty()) {
            ActivityCompat.requestPermissions(this, permissionList.toArray(new String[0]), permissionRequest);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        for (int result : grantResults) {
            if (result != PackageManager.PERMISSION_GRANTED) {
                Toast.makeText(getApplicationContext(), getString(R.string.message_permission_check), Toast.LENGTH_LONG).show();
                finish();
            }
        }
    }
}