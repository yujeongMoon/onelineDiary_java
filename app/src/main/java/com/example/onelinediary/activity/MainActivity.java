package com.example.onelinediary.activity;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.FragmentActivity;
import androidx.recyclerview.widget.RecyclerView;

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

    private final String[] permissions = {
            Manifest.permission.CAMERA,
            Manifest.permission.WRITE_EXTERNAL_STORAGE,
            Manifest.permission.READ_EXTERNAL_STORAGE
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
            progressDialog.setMessage("잠시만 기다려주세요!");
            progressDialog.show();
        }

        DatabaseUtility.readYearDiaryList(this, Utility.getYear(), isSuccess -> {
            if (progressDialog != null) {
                progressDialog.dismiss();
            }

            // TODO pageAdapter setting
            pagerAdapter = new MainPagerAdapter();
            mainBinding.pager.setAdapter(pagerAdapter);

            loading = false;
        });

        mainBinding.btnAddNewDiary.setOnClickListener(v -> addNewDiary());
    }

    @SuppressLint("NotifyDataSetChanged")
    @Override
    protected void onResume() {
        super.onResume();

//        pagerAdapter.notifyDataSetChanged();
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
        startActivity(new Intent(this, NewDiaryActivity.class));

        overridePendingTransition(R.anim.slide_in_up, R.anim.slide_out_up);
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
                Toast.makeText(getApplicationContext(), "권한 승인 부탁드립니다.", Toast.LENGTH_LONG).show();
                finish();
            }
        }
    }
}