package com.example.onelinediary.custom;

import android.annotation.SuppressLint;
import android.content.Context;
import android.util.AttributeSet;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

@SuppressLint("AppCompatCustomView")
public class DoubleCancelImageView extends ImageView {

    private static long preTime = 0;
    private static long tempTime = 0;

    private boolean isGuard = false;
    // 연속적인 클릭이 가능하도록 하고싶다면 true로 설정한다.
    private boolean isClickable = false;
    private GestureDetector detector;

    @SuppressLint("ClickableViewAccessibility")
    public DoubleCancelImageView(@NonNull Context context) {
        super(context);
        this.setOnTouchListener(onTouchListener);
//        detector = new GestureDetector(context, new SimpleGesture());
    }

    @SuppressLint("ClickableViewAccessibility")
    public DoubleCancelImageView(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        this.setOnTouchListener(onTouchListener);
//        detector = new GestureDetector(context, new SimpleGesture());
    }

    @SuppressLint("ClickableViewAccessibility")
    public DoubleCancelImageView(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        this.setOnTouchListener(onTouchListener);
//        detector = new GestureDetector(context, new SimpleGesture());
    }

    OnTouchListener onTouchListener = new OnTouchListener() {
        @SuppressLint("ClickableViewAccessibility")
        @Override
        public boolean onTouch(View v, MotionEvent event) {
            if (event.getAction() == MotionEvent.ACTION_UP) {
               /* if (isGuard) { // 두번째 클릭일 경우
                    isGuard = false;
                    return true;
                }*/

                /* 더블 탭의 경우, 같은 자리를 두번 연속 터치할 경우는 체크하지만
                * 같은 시간동안 서로 다른 자리를 터치하는 경우에는 팝업을 두개 띄워준다.
                * 같은 자리를 두번 연속 클릭하는 경우는 해결했지만 다른 위치를 연속적으로 터치하는 경우를 위해
                * 터치를 한번 하고 다음 터치까지의 시간이 2초 안쪽이면 두번째 터치는 무시한다.
                */
                if(!isClickable) {
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
            }

            // false
            // onTouchListener 이 후에도 다른 listener들이 동작하게 함.
            return false;
        }
    };

    /**
     * 첫번째 클릭 시간을 초기화 시켜준다.
     * 팝업이 떴다고 가정하고 그 타이밍에 초기화 시켜준다.
     */
    public void initPreTime() {
        preTime = 0;
    }

    public void isClickEnabled(boolean isEnabled) {
        this.setClickable(isEnabled);
    }

    // gestureDetecter를 사용하여 더블 탭을 막는 기능을 추가하였는데 연속으로 다른 부분을 클릭할 경우 효과가 없다.
    // 클릭간의 간격을 기준으로 변경하여 팝업을 띄운다.
    /*class SimpleGesture extends GestureDetector.SimpleOnGestureListener {
        // 더블 클릭했을 때 발생하는 이벤트
        // 더블 클릭을 하는 상황에서 두번째 클릭의 상황에서 호출된다.
        // 두번째 클릭의 상황에서 isGuard = true가 된다.
        @Override
        public boolean onDoubleTap(MotionEvent e) {
            isGuard = true;
            // false
            // onDoubleTap 이 후에도 다른 listener들이 동작하게 함.
            return super.onDoubleTap(e);
        }
    }*/

    public void setDoubleClickable(boolean clickable) {
        this.isClickable = clickable;
    }
}
