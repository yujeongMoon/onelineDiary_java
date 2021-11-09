package com.example.onelinediary.custom;

import android.app.Activity;
import android.content.Context;
import android.graphics.Point;
import android.graphics.Rect;
import android.util.AttributeSet;
import android.view.View;

import androidx.annotation.Nullable;

public class SoftKeyBoardDetectorView extends View {
    private boolean mShowKeyboard;
    private OnShowKeyboardListener mOnShowSoftKeyboard;
    private OnHiddenKeyboardListener mOnHiddenSoftKeyboard;

    public SoftKeyBoardDetectorView(Context context) {
        super(context, null);
    }

    public SoftKeyBoardDetectorView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        Activity activity = (Activity) getContext();
        Rect rect = new Rect();
        activity.getWindow().getDecorView().getWindowVisibleDisplayFrame(rect);

        int statusBarHeight = rect.top;

        // 화면 전체 크기를 구하는 방법
//      1. int screenHeight = activity.getWindowManager().getDefaultDisplay().getHeight() deprecated

//      2. int screenHeight = activity.getResources().getDisplayMetrics().heightPixels;

        // 3
        Point point = new Point();
        activity.getWindowManager().getDefaultDisplay().getSize(point);
        int screenHeight = point.y;

        int diffHeight = (screenHeight - statusBarHeight) - h;

        if (diffHeight > 100 && !mShowKeyboard) {
            mShowKeyboard = true;
            onShowSoftKeyboard();
        } else if (diffHeight < 100 && mShowKeyboard) {
            mShowKeyboard = false;
            onHiddenSoftKeyboard();
        }
        super.onSizeChanged(w, h, oldw, oldh);
    }

    public void onHiddenSoftKeyboard() {
        if (mOnHiddenSoftKeyboard != null)
            mOnHiddenSoftKeyboard.onHiddenSoftKeyboard();
    }

    public void onShowSoftKeyboard() {
        if (mOnShowSoftKeyboard != null)
            mOnShowSoftKeyboard.onShowSoftKeyboard();
    }

    public void setOnShowKeyboard(OnShowKeyboardListener listener) {
        mOnShowSoftKeyboard = listener;
    }

    public void setOnHiddenKeyboard(OnHiddenKeyboardListener listener) {
        mOnHiddenSoftKeyboard = listener;
    }

    public interface OnShowKeyboardListener {
        void onShowSoftKeyboard();
    }

    public interface OnHiddenKeyboardListener {
        void onHiddenSoftKeyboard();
    }
}

//        SoftKeyBoardDetectorView softKeyBoardDetectorView = new SoftKeyBoardDetectorView(this);
//        addContentView(softKeyBoardDetectorView, new FrameLayout.LayoutParams(-1, -1));
//
//        softKeyBoardDetectorView.setOnShowKeyboard(new SoftKeyBoardDetectorView.OnShowKeyboardListener() {
//            @Override
//            public void onShowSoftKeyboard() {
//                feedbackBinding.feedbackRecyclerview.scrollToPosition(adapter.getItemCount() - 1);
////                new Handler(Looper.getMainLooper()).postDelayed(new Runnable() {
////                    @Override
////                    public void run() {
////                        feedbackBinding.feedbackRecyclerview.scrollToPosition(adapter.getItemCount() - 1);
////                    }
////                }, 100);
//            }
//        });
//
//        softKeyBoardDetectorView.setOnHiddenKeyboard(new SoftKeyBoardDetectorView.OnHiddenKeyboardListener() {
//            @Override
//            public void onHiddenSoftKeyboard() {
//                Toast.makeText(getApplicationContext(), "키보드가 없습니다.", Toast.LENGTH_SHORT).show();
//            }
//        });
//
//        feedbackBinding.rootView.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
//            @Override
//            public void onGlobalLayout() {
//                softKeyBoardDetectorView.onShowSoftKeyboard();
//            }
//        });