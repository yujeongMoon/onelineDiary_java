package com.example.onelinediary.adapter;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.fragment.app.FragmentActivity;

import com.example.onelinediary.R;
import com.example.onelinediary.activity.DiaryDetailActivity;
import com.example.onelinediary.activity.MainActivity;
import com.example.onelinediary.constant.Const;
import com.example.onelinediary.dialog.SelectDialog;
import com.example.onelinediary.dto.Diary;
import com.example.onelinediary.utiliy.DatabaseUtility;
import com.example.onelinediary.utiliy.Utility;

import java.util.ArrayList;

public class MoodAdapter extends BaseAdapter {
    private Context context;
    String month;
    ArrayList<Diary> diaryList = new ArrayList<>();

    public MoodAdapter() {}

    public MoodAdapter(Context context) {
        this.context = context;
    }

    public void addDiaryList(String month, ArrayList<Diary> diaryList) {
        this.month = month;
        this.diaryList = diaryList;
    }

    @Override
    public int getCount() {
        return diaryList.size();
    }

    @Override
    public Object getItem(int position) {
        return diaryList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @SuppressLint("SetTextI18n")
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        int mood = diaryList.get(position).getMood();

        if (convertView == null) {
            LayoutInflater inflater = (LayoutInflater) parent.getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = inflater.inflate(R.layout.viewholder_mood_item, parent, false);

            TextView day = convertView.findViewById(R.id.day);
            day.setText(diaryList.get(position).getDay() + context.getString(R.string.day));

            ImageView emoji = convertView.findViewById(R.id.emoji);

            switch (mood) {
                case 1:
                    emoji.setImageResource(R.drawable.emoji_happy_icon);
                    break;
                case 2:
                    emoji.setImageResource(R.drawable.emoji_blushing_icon);
                    break;
                case 3:
                    emoji.setImageResource(R.drawable.emoji_blank_icon);
                    break;
                case 4:
                    emoji.setImageResource(R.drawable.emoji_consoling_icon);
                    break;
                case 5:
                    emoji.setImageResource(R.drawable.emoji_nervous_icon);
                    break;
            }
        } else {
            View view = new View(parent.getContext());
            view = (View) convertView;
        }

        convertView.setOnClickListener(v -> {
            Intent detailIntent = new Intent(context, DiaryDetailActivity.class);
            detailIntent.putExtra(Const.INTENT_KEY_MONTH, month);
            detailIntent.putExtra(Const.INTENT_KEY_DIARY, diaryList.get(position));
            context.startActivity(detailIntent);
        });

        /*
        * onClick과 onLongClick이 동시에 실행되면 문제가 발생할 수 있다.
        * onLongClick은 onClick과 다르게 반환값이 있다.
        * 기본값은 false, 다음 이벤트가 진행되어 롱클릭 이 후 클릭 이벤트가 진행된다.
        * true를 반환하면 동시에 실행되지않고 이벤트가 종료된다.
        *
        * 이모지를 롱클릭하면 일기 삭제를 진행한다.
        * 사용자에게 다이얼로그를 띄워 삭제 여부를 물어본다.
        */
        String message = context.getString(R.string.dialog_message_delete_diary, month, diaryList.get(position).getDay());
        convertView.setOnLongClickListener(v -> {
            new SelectDialog(message, v1 -> DatabaseUtility.deleteDiary(context, Utility.getYear(), month, diaryList.get(position).getDay(), isSuccess -> {
                if (isSuccess) {
                    Toast.makeText(context, context.getString(R.string.message_delete_diary), Toast.LENGTH_LONG).show();
//                    View view = ((Activity)context).findViewById(R.id.main_layout);
//                    Snackbar snackbar = Snackbar.make(v, R.string.message_delete_diary, Snackbar.LENGTH_LONG);
//                    Snackbar.SnackbarLayout snackbarView = (Snackbar.SnackbarLayout) snackbar.getView();
//                    ViewGroup.LayoutParams params = new ViewGroup.LayoutParams(30, 30);
//                    snackbarView.setLayoutParams(params);
//                    snackbarView.setPadding(0,0,0,0);
//                    TextView snackbarText = snackbar.getView().findViewById(com.google.android.material.R.id.snackbar_text);
//                    snackbarText.setTextAlignment(View.TEXT_ALIGNMENT_CENTER);
//                    snackbar.getView().setBackground(ContextCompat.getDrawable(context, R.drawable.snackbar));
//                    snackbar.show();

                    // 일기가 삭제되었다는 것을 알려준다?
                    // 연결된 그리드 뷰에게 알려준다?
//                    notifyDataSetChanged();

                    // 일기가 삭제된 것을 pagerAdapter에게 notify 해준다.
                    ((Activity)context).runOnUiThread(() -> ((MainActivity)context).notifyToPager());
                } else {
                    Toast.makeText(context, context.getString(R.string.error_message_delete_diary), Toast.LENGTH_LONG).show();
                }
            })).show(((FragmentActivity)context).getSupportFragmentManager(), "deleteDiarySuccess");

            return true;
        });

        return convertView;
    }
}
