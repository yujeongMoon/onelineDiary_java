package com.example.onelinediary.activity;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.FragmentActivity;

import com.example.onelinediary.R;
import com.example.onelinediary.adapter.MainPagerAdapter;
import com.example.onelinediary.constant.Const;
import com.example.onelinediary.databinding.ActivityMainBinding;
import com.example.onelinediary.utiliy.DatabaseUtility;
import com.example.onelinediary.utiliy.Utility;

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
    private ProgressDialog progressDialog = null;

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
            progressDialog = new ProgressDialog(this);
            progressDialog.setMessage(getString(R.string.message_progress_dialog));
            progressDialog.show();
        }

        DatabaseUtility.readYearDiaryList(this, Utility.getYear(), isSuccess -> {
            if (progressDialog != null) {
                progressDialog.dismiss();
            }

            if (pagerAdapter == null) {
                pagerAdapter = new MainPagerAdapter();
                mainBinding.pager.setAdapter(pagerAdapter);
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
        mainBinding.btnSetting.setOnClickListener(v -> Toast.makeText(getApplicationContext(), "서비스 준비중입니다.", Toast.LENGTH_LONG).show());
    }

    @Override
    protected void onResume() {
        super.onResume();

        // 일기가 삭제되었거나 화면 이동 후 어뎁터에게 알린다.
        notifyToPager();
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