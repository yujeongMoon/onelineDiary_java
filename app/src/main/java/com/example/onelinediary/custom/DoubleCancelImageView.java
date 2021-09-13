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

    private boolean isGuard = false;
    private GestureDetector detector;

    @SuppressLint("ClickableViewAccessibility")
    public DoubleCancelImageView(@NonNull Context context) {
        super(context);
        this.setOnTouchListener(onTouchListener);
        detector = new GestureDetector(context, new SimpleGesture());
    }

    @SuppressLint("ClickableViewAccessibility")
    public DoubleCancelImageView(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        this.setOnTouchListener(onTouchListener);
        detector = new GestureDetector(context, new SimpleGesture());
    }

    @SuppressLint("ClickableViewAccessibility")
    public DoubleCancelImageView(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        this.setOnTouchListener(onTouchListener);
        detector = new GestureDetector(context, new SimpleGesture());
    }

    OnTouchListener onTouchListener = new OnTouchListener() {
        @SuppressLint("ClickableViewAccessibility")
        @Override
        public boolean onTouch(View v, MotionEvent event) {
            if (event.getAction() == MotionEvent.ACTION_UP) {
                if (isGuard) { // 두번째 클릭일 경우
                    isGuard = false;
                    return true;
                }
            }

            // false
            // onTouchListener 이 후에도 다른 listener들이 동작하게 함.
            return detector.onTouchEvent(event);
        }
    };

    class SimpleGesture extends GestureDetector.SimpleOnGestureListener {
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
    }
}
