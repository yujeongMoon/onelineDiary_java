package com.example.onelinediary.activity;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.viewpager2.adapter.FragmentStateAdapter;

import com.example.onelinediary.fragment.MainDiaryFragment;
import com.example.onelinediary.R;
import com.example.onelinediary.databinding.ActivityMainBinding;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class MainActivity extends FragmentActivity {
    private ActivityMainBinding mainBinding;

    private static final int NUM_PAGES = 5;

    private final String[] permissions = {
            Manifest.permission.CAMERA,
            Manifest.permission.WRITE_EXTERNAL_STORAGE,
            Manifest.permission.READ_EXTERNAL_STORAGE
    };

    private final int PERMISSION_REQUEST = 100;

    private FragmentStateAdapter pagerAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mainBinding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(mainBinding.getRoot());

        // 필요한 권한 체크
        // 카메라나 저장소 접근 등 관련 권한을 사용자에게 승인 받는다.
        checkPermission(permissions, PERMISSION_REQUEST);

        pagerAdapter = new MainPagerAdapter(this);
        mainBinding.pager.setAdapter(pagerAdapter);

        mainBinding.btnAddNewDiary.setOnClickListener(v -> addNewDiary());
    }

    private static class MainPagerAdapter extends FragmentStateAdapter {

        public MainPagerAdapter(@NonNull FragmentActivity fragmentActivity) {
            super(fragmentActivity);
        }

        @NonNull
        @Override
        public Fragment createFragment(int position) {
            return new MainDiaryFragment();
        }

        @Override
        public int getItemCount() {
            return NUM_PAGES;
        }
    }

    public void addNewDiary() {
        startActivity(new Intent(this, NewDiaryActivity.class));

        overridePendingTransition(R.anim.slide_in_up, R.anim.slide_out_up);
    }

    private boolean checkPermission(String[] permissions, int permissionRequest) {
        ArrayList<String> permissionList = new ArrayList<>();

        for (String permission : permissions) {
            int result = ContextCompat.checkSelfPermission(this, permission);
            if (result != PackageManager.PERMISSION_GRANTED) {
                permissionList.add(permission);
            }
        }

        if (!permissionList.isEmpty()) {
            ActivityCompat.requestPermissions(this, permissionList.toArray(new String[0]), PERMISSION_REQUEST);
            return false;
        }
        return true;
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