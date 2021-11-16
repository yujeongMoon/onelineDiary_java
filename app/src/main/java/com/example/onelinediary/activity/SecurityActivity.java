package com.example.onelinediary.activity;

import android.content.Intent;
import android.os.Bundle;
import android.widget.CompoundButton;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.example.onelinediary.R;
import com.example.onelinediary.adapter.ListAdapter;
import com.example.onelinediary.constant.Const;
import com.example.onelinediary.databinding.ActivitySecurityBinding;
import com.example.onelinediary.dialog.ConfirmDialog;
import com.example.onelinediary.dto.BasicItemSwitch;
import com.example.onelinediary.dto.PinInfo;
import com.example.onelinediary.utiliy.DatabaseUtility;
import com.example.onelinediary.utiliy.Utility;

public class SecurityActivity extends AppCompatActivity {
    private ActivitySecurityBinding securityBinding;

    private final int PIN_REQUEST = 100;
    private ListAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        securityBinding = ActivitySecurityBinding.inflate(getLayoutInflater());
        setContentView(securityBinding.getRoot());

        securityBinding.securityRecyclerview.setLayoutManager(new LinearLayoutManager(this));
        adapter = new ListAdapter();

        boolean isEnabled = false;
        if (Utility.getString(getApplicationContext(), Const.SP_KEY_SET_PIN_NUMBER).equals("Y")) {
            isEnabled = true;
        }

        adapter.addItem(new BasicItemSwitch(R.drawable.pin_number_black_24, getString(R.string.title_security_pin), isEnabled, setPinNumber));
        securityBinding.securityRecyclerview.setAdapter(adapter);
    }

    CompoundButton.OnCheckedChangeListener setPinNumber = new CompoundButton.OnCheckedChangeListener() {
        @Override
        public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
            if (isChecked) {
                Intent pinIntent = new Intent(SecurityActivity.this, PinActivity.class);
                pinIntent.putExtra(Const.INTENT_KEY_IS_LOGIN, false);
                startActivityForResult(pinIntent, PIN_REQUEST);
            } else {
                if (Utility.getString(getApplicationContext(), Const.SP_KEY_SET_PIN_NUMBER).equals("Y")) {
                    DatabaseUtility.setPinNumber(SecurityActivity.this, new PinInfo("N", ""),
                            isSuccess -> new ConfirmDialog(getString(R.string.dialog_message_unlock_pin), v -> {
                        adapter.updateItem(0, new BasicItemSwitch(R.drawable.pin_number_black_24, getString(R.string.title_security_pin), false, setPinNumber));

                        Utility.putString(getApplicationContext(), Const.SP_KEY_SET_PIN_NUMBER, "N");
                        Utility.putString(getApplicationContext(), Const.SP_KEY_PIN_NUMBER, "");
                    }).show(getSupportFragmentManager(), "resetSecurityPin"));
                }
            }
        }
    };

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        // 암호 설정을 하는 중에 취소를 하는 경우
        // RESULT_CANCELED을 넘겨 알린다.
        if(resultCode == RESULT_CANCELED) {
            adapter.updateItem(0, new BasicItemSwitch(R.drawable.pin_number_black_24, getString(R.string.title_security_pin), false, setPinNumber));
        }
    }
}