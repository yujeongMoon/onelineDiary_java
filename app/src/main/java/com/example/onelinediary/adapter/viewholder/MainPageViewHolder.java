package com.example.onelinediary.adapter.viewholder;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.onelinediary.R;
import com.example.onelinediary.adapter.MainMoodAdapter;
import com.example.onelinediary.adapter.MainPagerAdapter;
import com.example.onelinediary.constant.Const;
import com.example.onelinediary.databinding.ViewholderMainDiaryBinding;
import com.example.onelinediary.dto.Diary;
import com.example.onelinediary.utiliy.Utility;

import java.util.ArrayList;

public class MainPageViewHolder extends RecyclerView.ViewHolder {
    private final ViewholderMainDiaryBinding mainDiaryBinding;

    Context context;

    GestureDetector gestureDetector = null;

    MainPagerAdapter.onDiaryInterface onDiaryInterface;

    boolean isSelectedYear;

    ArrayAdapter<String> spinnerAdapter;

    public MainPageViewHolder(@NonNull ViewholderMainDiaryBinding mainDiaryBinding, MainPagerAdapter.onDiaryInterface onDiaryInterface) {
        super(mainDiaryBinding.getRoot());
        this.mainDiaryBinding = mainDiaryBinding;

        context = mainDiaryBinding.getRoot().getContext();
        this.onDiaryInterface = onDiaryInterface;

        Glide.with(context).load(R.drawable.bg_blossom).into(mainDiaryBinding.spring);
        Glide.with(context).load(R.drawable.bg_summer).into(mainDiaryBinding.summer);
        Glide.with(context).load(R.drawable.bg_autumn).into(mainDiaryBinding.autumn);
//        Glide.with(context).load(R.drawable.snow).into(mainDiaryBinding.winter);

        isSelectedYear = false;
        if(spinnerAdapter == null) {
            spinnerAdapter = new ArrayAdapter<String>(context, R.layout.simple_spinner_dropdown_item, Const.yearList);
            mainDiaryBinding.yearSpinner.setAdapter(spinnerAdapter);
            mainDiaryBinding.yearSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                @Override
                public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                    if(isSelectedYear) {
                        onDiaryInterface.onYearSelected(Const.yearList.get(position));
                        isSelectedYear = false;
                    }
                }

                @Override
                public void onNothingSelected(AdapterView<?> parent) {

                }
            });
        }
    }

    @SuppressLint({"SetTextI18n", "ClickableViewAccessibility"})
    public void onBind(String month, ArrayList<Diary> diaryList) {
        setSeason(month);

        String year = "";
        if (diaryList.size() > 0) {
            year = diaryList.get(diaryList.size() - 1).getReportingDate().substring(0, 4);
        }

        mainDiaryBinding.year.setText(year);
        mainDiaryBinding.year.setOnClickListener(v -> {
            isSelectedYear = true;
            mainDiaryBinding.yearSpinner.performClick();
        });
        mainDiaryBinding.month.setText(month + context.getString(R.string.month));

        if (Utility.isStringNullOrEmpty(Utility.getString(context, Const.SP_KEY_NICKNAME))) {
            mainDiaryBinding.nickname.setVisibility(View.GONE);
        } else {
            mainDiaryBinding.nickname.setVisibility(View.VISIBLE);
            mainDiaryBinding.nickname.setText(Const.nickname + "님의");
        }

        // 그리드 뷰 데이터와 어뎁터 설정
        MainMoodAdapter adapter = new MainMoodAdapter(context);
        adapter.addDiaryList(year, month, diaryList);
        mainDiaryBinding.moodLayout.setAdapter(adapter);

        // 뷰페이저 안에 다른 아이템들로 인해 스크롤이 잘 안되는 문제를 해결하기 위해서 추가한 코드
        // GestureDetector는 일반적인 동작으로 감지한다.
        gestureDetector = new GestureDetector(context, new SingleTapGestureListener());

        // 그리드뷰에 터치리스너를 연결한다.
        mainDiaryBinding.moodLayout.setOnTouchListener((v, event) -> {
            gestureDetector.onTouchEvent(event);
            // true : 그 다음 리스너까지 이벤트를 전달하지않고 터치만 하고 끝낸다.
            // false : 그 다음 리스너까지 이벤트를 전달한다.
            // onTouch -> onClick -> onLongClick 순서로 진행된다.

            // gestureDetector에게 이벤트를 전달하기 위해 false를 반환한다.
            return false;
        });
    }

    // 몇 가지 동작만 처리하기위해 GestureDetector.SimpleOnGestureListener를 사용한다.
    public class SingleTapGestureListener extends GestureDetector.SimpleOnGestureListener {
        @Override
        public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX, float velocityY) {
            if (velocityY > 0) {
                // 이전 달로 이동
                if (onDiaryInterface != null)
                    onDiaryInterface.onPagerScroll(true);
            } else {
                // 다음 달로 이동
                if (onDiaryInterface != null)
                    onDiaryInterface.onPagerScroll(false);
            }

            // 이 후에 이벤트를 전달하지 않아도 되기 때문에 true를 반환한다.
            return true;
        }
    }

    public void setSeason(String month) {
        resetSeason();

        switch (month) {
            case "12":
            case "01":
            case "02":
                mainDiaryBinding.winter.setVisibility(View.VISIBLE);
                break;
            case "03":
            case "04":
            case "05":
                mainDiaryBinding.spring.setVisibility(View.VISIBLE);
                break;
            case "06":
            case "07":
            case "08":
                mainDiaryBinding.summer.setVisibility(View.VISIBLE);
                break;
            case "09":
            case "10":
            case "11":
                mainDiaryBinding.autumn.setVisibility(View.VISIBLE);
                break;
        }
    }

    public void resetSeason() {
        mainDiaryBinding.summer.setVisibility(View.GONE);
        mainDiaryBinding.autumn.setVisibility(View.GONE);
        mainDiaryBinding.spring.setVisibility(View.GONE);
        mainDiaryBinding.winter.setVisibility(View.GONE);
    }
}
