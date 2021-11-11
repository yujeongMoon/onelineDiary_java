package com.example.onelinediary.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.example.onelinediary.R;

public class PinAdapter extends BaseAdapter {
    private final String[] numbers = {"1", "2", "3", "4", "5", "6", "7", "8", "9", "x", "0", "<"};
    pinClickListener pinClickListener;

    public interface pinClickListener {
        void onPinClick(String tag);
    }

    public PinAdapter() { }

    public PinAdapter(pinClickListener listener) {
        this.pinClickListener = listener;
    }

    @Override
    public int getCount() {
        return numbers.length;
    }

    @Override
    public Object getItem(int position) {
        return numbers[position];
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if (convertView == null) {
            LayoutInflater inflater = (LayoutInflater) parent.getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = inflater.inflate(R.layout.viewholder_pin_item, parent, false);

            TextView number = convertView.findViewById(R.id.number);

            number.setText(numbers[position]);

            convertView.setTag(numbers[position]);

            convertView.setOnClickListener(v -> {
                if (pinClickListener != null)
                    pinClickListener.onPinClick((String)v.getTag());
            });
        } else {
            View view = new View(parent.getContext());
            view = (View) convertView;
        }

        return convertView;
    }
}
