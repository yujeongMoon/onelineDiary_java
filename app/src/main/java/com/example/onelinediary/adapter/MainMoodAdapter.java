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

import com.example.onelinediary.R;
import com.example.onelinediary.activity.DiaryDetailActivity;
import com.example.onelinediary.activity.MainActivity;
import com.example.onelinediary.activity.NewDiaryActivity;
import com.example.onelinediary.constant.Const;
import com.example.onelinediary.dialog.YesNoDialog;
import com.example.onelinediary.dto.Diary;
import com.example.onelinediary.utiliy.DatabaseUtility;
import com.example.onelinediary.utiliy.Utility;

import java.util.ArrayList;

public class MainMoodAdapter extends BaseAdapter {
    private Context context;
    String month;
    String year;
    ArrayList<Diary> diaryList = new ArrayList<>();

    // 해당 날짜에 일기가 있는지 구분해주는 플래그
    boolean isExist = false;

    // 해당 날짜에 일기가 있다고 판단되면 일기가 있는 인덱스를 저장해서 사용한다.
    int index = 0;

    // 해당 달의 1일의 요일(1:일 ~ 7:토)
    int startDayInMonth = 0;
    // 해당 달마다의 마지막 날짜
    int lastDayInMonth = 0;

    public MainMoodAdapter() {}

    public MainMoodAdapter(Context context) {
        this.context = context;
    }

    public void addDiaryList(String year, String month, ArrayList<Diary> diaryList) {
        this.year = year;
        this.month = month;
        this.diaryList = diaryList;
    }

    @Override
    public int getCount() {
        // 해당 달의 1일의 요일(1:일 ~ 7:토)
        // 예를 들어 1일의 요일이 화요일이면 일,월 2개의 더미가 쌓인 후 일기를 순서대로 보여준다.
        // 더미가 쌓이는 만큼 늘어나야 하기때문에 반환된 요일 값에서 -1을 한 값을 마지막 날짜에 더해서 어뎁터가 가진 데이터의 크기를 만들어준다.
        startDayInMonth = Utility.getDayOfWeek(Integer.parseInt(Const.currentYear), Integer.parseInt(month), 1) - 1;

        // 해당 달마다의 마지막 날짜
        lastDayInMonth = Utility.getLastDayNumberOfMonth(Integer.parseInt(Const.currentYear), Integer.parseInt(month));

        return lastDayInMonth + startDayInMonth;
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
        ViewHolder viewholder;
        LayoutInflater inflater = (LayoutInflater) parent.getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        if (convertView == null) {
            convertView = inflater.inflate(R.layout.viewholder_mood_item, parent, false);

            viewholder = new ViewHolder(convertView);
            convertView.setTag(viewholder);
        } else {
            viewholder = (ViewHolder) convertView.getTag();
        }

        if (position < startDayInMonth) {
            viewholder.tvDay.setVisibility(View.GONE);
            viewholder.emoji.setVisibility(View.GONE);
            viewholder.convertView.setOnClickListener(null);
        } else {
            viewholder.tvDay.setVisibility(View.VISIBLE);
            viewholder.emoji.setVisibility(View.VISIBLE);

            viewholder.day = (position - startDayInMonth) + 1 + "";
            viewholder.tvDay.setText(viewholder.day);

            if (Utility.getDayOfWeek(Integer.parseInt(year), Integer.parseInt(month), Integer.parseInt(viewholder.day)) == 7) {
                viewholder.tvDay.setTextColor(context.getColor(R.color.blue));
            } else if (Utility.getDayOfWeek(Integer.parseInt(year), Integer.parseInt(month), Integer.parseInt(viewholder.day)) == 1) {
                viewholder.tvDay.setTextColor(context.getColor(R.color.red));
            }

            if (Utility.getMonth().equals(month) && Utility.getDayToInt() == Integer.parseInt(viewholder.tvDay.getText().toString())) {
                viewholder.line.setVisibility(View.VISIBLE);
            } else {
                viewholder.line.setVisibility(View.INVISIBLE);
            }

            for(int i = 0; i < diaryList.size(); i++) {
                if (!isExist && Integer.parseInt(diaryList.get(i).getDay()) == ((position - startDayInMonth) + 1)) {
                    index = i;
                    isExist = true;
                }
            }

            if (isExist) {
                viewholder.emoji.setImageResource(Utility.migrationMoodToEmoji(context, diaryList.get(index)));
                viewholder.index = index;
                viewholder.convertView.setOnClickListener(v -> {
                    Intent detailIntent = new Intent(context, DiaryDetailActivity.class);
                    detailIntent.putExtra(Const.INTENT_KEY_MONTH, month);
                    detailIntent.putExtra(Const.INTENT_KEY_DIARY, diaryList.get(viewholder.index));
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
                String message = context.getString(R.string.dialog_message_delete_diary, month, diaryList.get(index).getDay());
                viewholder.convertView.setOnLongClickListener(v -> {
                    new YesNoDialog(message, v1 -> DatabaseUtility.deleteDiary(context, Utility.getYear(), month, diaryList.get(viewholder.index).getDay(), isSuccess -> {
                        if (isSuccess) {
                            Toast.makeText(context, context.getString(R.string.message_delete_diary), Toast.LENGTH_LONG).show();

                            // 일기가 삭제된 것을 pagerAdapter에게 notify 해준다.
                            Const.todayDiary = null;
                            Const.deleteDiary = true;
                            Utility.putString(context, Const.SP_KEY_TODAY_DIARY, null);

                            ((Activity)context).runOnUiThread(() -> ((MainActivity)context).notifyToPager());
                        } else {
                            Toast.makeText(context, context.getString(R.string.error_message_delete_diary), Toast.LENGTH_LONG).show();
                        }
                    })).show(context);

                    return true;
                });
                isExist = false;
            } else {
                Intent addBeforeDiaryIntent = new Intent(context, NewDiaryActivity.class);
                addBeforeDiaryIntent.putExtra("year", year);
                addBeforeDiaryIntent.putExtra("month", month);
                addBeforeDiaryIntent.putExtra("day", viewholder.day);

                if (Utility.getMonth().equals(month)) { // 현재 달
                    if (Utility.getDayToInt() >= Integer.parseInt(viewholder.tvDay.getText().toString())) {
                        viewholder.convertView.setOnClickListener(v -> {
                            addBeforeDiaryIntent.putExtra("beforeDay", !Utility.isSameDateWithToday(month, viewholder.day));
                            context.startActivity(addBeforeDiaryIntent);
                        });
                    } else { // 오늘 날짜 이후
                        viewholder.convertView.setOnClickListener(null);
                    }
                } else { // 이전 달
                    viewholder.convertView.setOnClickListener(v -> {
                        addBeforeDiaryIntent.putExtra("beforeDay", true);
                        context.startActivity(addBeforeDiaryIntent);
                    });
                }
            }
        }

        return convertView;
    }

    public static class ViewHolder {
        View convertView;
        TextView tvDay;
        ImageView emoji;
        View line;

        int index;
        String day;

        public ViewHolder(View convertView) {
            this.convertView = convertView;
            this.tvDay = convertView.findViewById(R.id.day);
            this.emoji = convertView.findViewById(R.id.emoji);
            this.line = convertView.findViewById(R.id.day_line);
        }
    }
}
