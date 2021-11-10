package com.example.onelinediary.activity;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.Toast;

import com.example.onelinediary.R;
import com.example.onelinediary.adapter.ListAdapter;
import com.example.onelinediary.databinding.ActivitySecurityBinding;
import com.example.onelinediary.dialog.ConfirmDialog;
import com.example.onelinediary.dto.BasicItemBtn;
import com.example.onelinediary.dto.BasicItemSwitch;
import com.example.onelinediary.dto.PinInfo;
import com.example.onelinediary.utiliy.DatabaseUtility;
import com.example.onelinediary.utiliy.Utility;

public class SecurityActivity extends AppCompatActivity {
    private ActivitySecurityBinding securityBinding;

    private ListAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        securityBinding = ActivitySecurityBinding.inflate(getLayoutInflater());
        setContentView(securityBinding.getRoot());

        securityBinding.securityRecyclerview.setLayoutManager(new LinearLayoutManager(this));
        adapter = new ListAdapter();

        boolean isEnabled = false;
        if (Utility.getString(getApplicationContext(), "setPinNumber").equals("Y")) {
            isEnabled = true;
        }

        adapter.addItem(new BasicItemSwitch(R.drawable.pin_number_black_24, "pin 번호", isEnabled, setPinNumber));
        securityBinding.securityRecyclerview.setAdapter(adapter);
    }

    CompoundButton.OnCheckedChangeListener setPinNumber = new CompoundButton.OnCheckedChangeListener() {
        @Override
        public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
            if (isChecked) {
                Intent pinIntent = new Intent(SecurityActivity.this, PinActivity.class);
                pinIntent.putExtra("isLogin", false);
                startActivityForResult(pinIntent, 100);
            } else {
                if (Utility.getString(SecurityActivity.this, "setPinNumber").equals("Y")) {
                    DatabaseUtility.setPinNumber(SecurityActivity.this, new PinInfo("N", ""), new DatabaseUtility.onCompleteCallback() {
                        @Override
                        public void onComplete(boolean isSuccess) {
                            new ConfirmDialog("암호 설정을 해제합니다.", v -> {
                                adapter.updateItem(0, new BasicItemSwitch(R.drawable.pin_number_black_24, "pin 번호", false, setPinNumber));

                                Utility.putString(getApplicationContext(), "setPinNumber", "N");
                                Utility.putString(getApplicationContext(), "pinNumber", "");
                            }).show(getSupportFragmentManager(), "resetSecurityPin");
                        }
                    });
                }
            }
        }
    };

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if(resultCode == RESULT_CANCELED) {
            adapter.updateItem(0, new BasicItemSwitch(R.drawable.pin_number_black_24, "pin 번호", false, setPinNumber));
        }
    }
}