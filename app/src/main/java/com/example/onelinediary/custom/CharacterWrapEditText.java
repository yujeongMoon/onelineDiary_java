package com.example.onelinediary.custom;

import android.annotation.SuppressLint;
import android.content.Context;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.AttributeSet;
import android.widget.EditText;

@SuppressLint("AppCompatCustomView")
public class CharacterWrapEditText extends EditText {

    private int cursorIndex = 0;

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
                // 커서의 위치가 맨 처음에 있거나 마지막을 포함해서 중간에 위치할 때
                // 커서의 위치를 저장한다.(cursorIndex)
                if(editText.getSelectionStart() > 0 && editText.getSelectionStart() <= editText.getText().toString().length())
                    cursorIndex = editText.getSelectionStart();

                // 스페이스를 입력했을 때, 스페이스를 "\u00A0"로 바꿔주고 setText()를 호출한다.
                // setText()를 호출하면 커서가 맨 처음으로 이동하기 때문에 저장된 커서의 위치로 옮겨주기위해 setSelection()을 호출하여 커서를 아동시킨다.
                if (s.toString().contains(" ")) {
                    editText.setText(editText.getText().toString().replace(" ", "\u00A0"));
                    // 커서의 위치가 초기값(0)일 때 커서의 위치를 마지막으로 설정해준다.
                    if(cursorIndex == 0)
                        cursorIndex = editText.getText().toString().length();

                    editText.setSelection(cursorIndex);
                }

                if (count == 0)
                    cursorIndex = 0;

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
