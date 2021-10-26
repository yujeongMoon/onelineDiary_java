package com.example.onelinediary.custom;

import android.annotation.SuppressLint;
import android.content.Context;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.widget.LinearLayout;

import androidx.annotation.Nullable;

@SuppressLint("AppCompatCustomView")
public class DoubleCancelLinearLayout extends LinearLayout {

    private static long preTime = 0;
    private static long tempTime = 0;

    @SuppressLint("ClickableViewAccessibility")
    public DoubleCancelLinearLayout(Context context) {
        super(context);
        this.setOnTouchListener(onTouchListener);
    }

    @SuppressLint("ClickableViewAccessibility")
    public DoubleCancelLinearLayout(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        this.setOnTouchListener(onTouchListener);
    }

    @SuppressLint("ClickableViewAccessibility")
    public DoubleCancelLinearLayout(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        this.setOnTouchListener(onTouchListener);
    }

    @SuppressLint("ClickableViewAccessibility")
    public DoubleCancelLinearLayout(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        this.setOnTouchListener(onTouchListener);
    }

    OnTouchListener onTouchListener = new OnTouchListener() {
        @SuppressLint("ClickableViewAccessibility")
        @Override
        public boolean onTouch(View v, MotionEvent event) {
            if (event.getAction() == MotionEvent.ACTION_UP) {
                /* 더블 탭의 경우, 같은 자리를 두번 연속 터치할 경우는 체크하지만
                 * 같은 시간동안 서로 다른 자리를 터치하는 경우에는 팝업을 두개 띄워준다.
                 * 같은 자리를 두번 연속 클릭하는 경우는 해결했지만 다른 위치를 연속적으로 터치하는 경우를 위해
                 * 터치를 한번 하고 다음 터치까지의 시간이 2초 안쪽이면 두번째 터치는 무시한다.
                 */
                tempTime = System.currentTimeMillis(); // 현재 시간 구하기
                // 이전에 저정한 시간과 현재 시간의 차이를 구한다.(초)
                long interval = tempTime - preTime;

                if (preTime <= 0) { // 1번째 클릭
                    preTime = tempTime;
                    return false;
                } else { // n번째 클릭(n>1)
                    // 첫번째 클릭과 3초이상 차이가 날 경우
                    // 현재 클릭 시간을 첫번째 클릭 시간으로 지정한다.
                    if(interval >= 3000) {
                        preTime = tempTime;
                        return false;
                    }
                    return true;
                }
            }

            // false
            // onTouchListener 이 후에도 다른 listener들이 동작하게 함.
            return false;
        }
    };
}
