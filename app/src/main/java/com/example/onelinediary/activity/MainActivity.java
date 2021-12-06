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
import android.text.TextUtils;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
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
import com.google.gson.Gson;

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
    private final int REQUEST_ADD_NEW_DIARY = 200;

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

        DatabaseUtility.readDiaryList(this, (isSuccess, result) -> {
            if (isSuccess) {
                Const.yearList = result;
            }
        });

        initDiaryList(Utility.getYear());

        mainBinding.btnAddNewDiary.setOnClickListener(v -> addNewDiary());
        mainBinding.btnSetting.setOnClickListener(v -> gotoSettingActivity());
        mainBinding.btnNotice.setOnClickListener(v -> gotoNoticeActivity());

        if (Utility.getString(getApplicationContext(), Const.SP_KEY_PROFILE).equals("")) {
            DatabaseUtility.getProfileImage(Utility.getAndroidId(this), (isSuccess, result) -> {
                if (isSuccess) {
                    if (result != null && !result.equals("")) {
                        Utility.putString(getApplicationContext(), Const.SP_KEY_PROFILE, result);
                    } else { // 저장된 프로필 이미지가 없을 때
                        Utility.putString(getApplicationContext(), Const.SP_KEY_PROFILE, "");
                    }
                }
            });
        }
    }

    private void initDiaryList(String year) {
        DatabaseUtility.readYearDiaryList(this, year, isSuccess -> {
            if (progressDialog != null) {
                progressDialog.dismiss();
            }

            pagerAdapter = new MainPagerAdapter();
            pagerAdapter.setDiaryInterface(new MainPagerAdapter.onDiaryInterface() {
                @Override
                public void initDiaryCompleted() {
                    // 오늘의 일기가 있는지 확인하고 메인의 아이콘을 추가버튼 또는 오늘의 기분으로 변경해준다.
                    checkExistDiary();
                    moveSelectedActivity();
                }

                @Override
                public void onPagerScroll(boolean prev) {
                    // 페이저의 현재 위치
                    int position = mainBinding.pager.getCurrentItem();

                    if (prev) { // true이면 이전화면, false이면 다음화면
                        if (position > 0) // 0일 경우, 첫 화면이기 때문에 이동 못함.
                            mainBinding.pager.setCurrentItem(position - 1);
                    } else {
                        if (position < pagerAdapter.getItemCount() - 1) // 마지막 위치일 경우, 마지막 화면이기 때문에 이동 못함.
                            mainBinding.pager.setCurrentItem(position + 1);
                    }
                }

                @Override
                public void onYearSelected(String year) {
                    if(!TextUtils.isEmpty(year))
                        initDiaryList(year);
                }
            });
            mainBinding.pager.setAdapter(pagerAdapter);

            // 현재 달의 페이지를 보여준다.
            // 데이터가 순서대로 들어가기 때문에 diaryList의 마지막이 현재 달!
            if (!Const.monthKeyList.isEmpty()) {
                // onResume() 다음에 setCurrentItem()이 동작하지 않는다는 오류가 있다.
                // 그걸 해결하기 위해 post()를 사용해서 현재 아이템의 위치를 설정해준다.
                // smoothScroll = false : 애니메이션 없음
                mainBinding.pager.post(() -> mainBinding.pager.setCurrentItem(Const.monthKeyList.size() - 1, false));
            }

            loading = false;
        });
    }

    private void moveSelectedActivity() {
        if(getIntent() != null) {
            Intent moveIntent = getIntent();
            if(getIntent().hasExtra("moveActivity")) {
                try {
                    if(getIntent().getStringExtra("moveActivity").equals(Const.ACTIVITY_TYPE_DETAIL)
                    || getIntent().getStringExtra("moveActivity").equals(Const.ACTIVITY_TYPE_New)) {
                        addNewDiary();
                    } else {
                        moveIntent.setClass(this, Class.forName(getPackageName() + ".activity." + getIntent().getStringExtra("moveActivity")));
                        startActivity(moveIntent);
                    }
                    getIntent().removeExtra("moveActivity");
                } catch (ClassNotFoundException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    private void checkExistDiary() {
        mainBinding.btnAddNewDiary.setImageResource(R.drawable.add_circle_outline_black);

        // 현재 달의 일기 리스트
        ArrayList<Diary> currentMonthDiaryList = Const.diaryList.get(Utility.getMonth());
        // 오늘의 일기(이번 달 일기의 리스트의 마지막이 오늘의 일기)
        if (currentMonthDiaryList != null && currentMonthDiaryList.size() > 0) {
            Diary todayDiary = currentMonthDiaryList.get(currentMonthDiaryList.size() - 1);

            if (Const.diaryList != null && Const.diaryList.containsKey(Utility.getMonth())) {
                if (todayDiary.getDay().equals(Utility.getDay())) {
                    Const.todayDiary = todayDiary;

                    Gson gson = new Gson();
                    Utility.putString(this, "todayDiary", gson.toJson(todayDiary));
                    mainBinding.btnAddNewDiary.setImageResource(Utility.migrationMoodToEmoji(this, todayDiary));
                }
            }
        }
    }

    @Override
    protected void onResume() {
        super.onResume();

        // 일기가 삭제되었거나 화면 이동 후 어뎁터에게 알린다.
        notifyToPager();

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
        Toast.makeText(getApplicationContext(), getString(R.string.message_close_app), Toast.LENGTH_SHORT).show();

        // 플래그 초기화
        new Handler(Looper.getMainLooper()).postDelayed(() -> pressedBackOnce = false, 2000);
    }

    @SuppressLint("NotifyDataSetChanged")
    public void notifyToPager() {
        if (pagerAdapter != null) {
            pagerAdapter.notifyDataSetChanged();

            // 일기를 삭제하면 현재 날짜가 포함된 제일 마지막 페이지로 이동하게 하거나 바로 전 달의 페이지로 이동한다.
            if (Const.deleteDiary) {
                Const.deleteDiary = false;
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

                    Utility.putInt(this, "todayWeather", resId);
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

    public void addNewDiary() {
//        overridePendingTransition(R.anim.slide_in_up, R.anim.slide_out_up);

        if (Const.todayDiary != null) {
            Intent detailIntent = new Intent(this, DiaryDetailActivity.class);
            detailIntent.putExtra(Const.INTENT_KEY_MONTH, Utility.getMonth());
            detailIntent.putExtra(Const.INTENT_KEY_DIARY, Const.todayDiary);
            startActivity(detailIntent);
        } else {
            Intent newDiaryIntent = new Intent(this, NewDiaryActivity.class);
            startActivityForResult(newDiaryIntent, REQUEST_ADD_NEW_DIARY);
        }
    }

    public void gotoSettingActivity() {
        Intent settingIntent = new Intent(this, SettingActivity.class);
        startActivity(settingIntent);
    }

    public void gotoNoticeActivity() {
        Intent noticeIntent = new Intent(this, NoticeListActivity.class);
        startActivity(noticeIntent);
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
                // TODO 설정화면으로 이동하기
                finish();
            }
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == REQUEST_ADD_NEW_DIARY && resultCode == RESULT_OK) {
            // 새로 일기를 쓴 경우에는 어느 페이지에 있어도 현재 날짜가 포함된 제일 마지막 페이지로 이동하게 한다.
            // 시간차를 주면서 페이저의 포지션 바꾸기
            mainBinding.pager.postDelayed(() -> mainBinding.pager.setCurrentItem(Const.monthKeyList.size() - 1, false), 100);
        }
    }
}