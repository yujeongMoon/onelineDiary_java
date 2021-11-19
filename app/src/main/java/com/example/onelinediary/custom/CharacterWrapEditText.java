package com.example.onelinediary.custom;

import android.annotation.SuppressLint;
import android.content.Context;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.AttributeSet;
import android.widget.EditText;

@SuppressLint("AppCompatCustomView")
public class CharacterWrapEditText extends EditText {

    public interface onTextChangeListener {
        void beforeTextChanged(CharSequence s, int start, int count, int after);
        void onTextChanged(CharSequence s, int start, int before, int count);
        void afterTextChanged (Editable s);
    }

    public CharacterWrapEditText(Context context) {
        super(context);
        // addTextChangedListenerWithChar를 설정하지 않아도
        // 기본적으로 작성할 때 word wrap -> character wrap
        this.addTextChangedListenerWithChar(this, null);
    }

    public CharacterWrapEditText(Context context, AttributeSet attrs) {
        super(context, attrs);
        this.addTextChangedListenerWithChar(this, null);
    }

    public CharacterWrapEditText(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        this.addTextChangedListenerWithChar(this, null);
    }

    public CharacterWrapEditText(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        this.addTextChangedListenerWithChar(this, null);
    }

    @Override
    public void setText(CharSequence text, BufferType type) {
        super.setText(text.toString().replace(" ", "\u00A0"), type);
    }

    public void addTextChangedListenerWithChar(EditText editText, onTextChangeListener listener) {
        super.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                if (listener != null)
                    listener.beforeTextChanged(s, start, count, after);
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (s.toString().contains(" ")) {
                    editText.setText(editText.getText().toString().replace(" ", "\u00A0"));
                    editText.setSelection(editText.getText().length());
                }

                if (listener != null)
                    listener.onTextChanged(s, start, before, count);
            }

            @Override
            public void afterTextChanged (Editable s){
                if (listener != null)
                    listener.afterTextChanged(s);
            }
        });
    }
}
