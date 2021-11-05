package com.example.onelinediary.activity;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;

import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import com.example.onelinediary.adapter.FeedbackAdapter;
import com.example.onelinediary.constant.Const;
import com.example.onelinediary.databinding.ActivityFeedbackBinding;
import com.example.onelinediary.dto.Feedback;
import com.example.onelinediary.utiliy.DatabaseUtility;
import com.example.onelinediary.utiliy.Utility;

public class FeedbackActivity extends AppCompatActivity {
    private ActivityFeedbackBinding feedbackBinding;

    FeedbackAdapter adapter;

    Feedback feedback = new Feedback();

    String androidId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        feedbackBinding = ActivityFeedbackBinding.inflate(getLayoutInflater());
        setContentView(feedbackBinding.getRoot());

        androidId = getIntent().getStringExtra("androidId");

        feedbackBinding.feedbackRecyclerview.setLayoutManager(new LinearLayoutManager(this));

        adapter = new FeedbackAdapter();
        feedbackBinding.feedbackRecyclerview.setAdapter(adapter);

        DatabaseUtility.readFeedback(androidId, new DatabaseUtility.onCompleteCallback() {
            @Override
            public void onComplete(boolean isSuccess) {
                if (isSuccess) {
                    adapter.addFeedbackList(Const.feedbackList);
                    feedbackBinding.feedbackRecyclerview.scrollToPosition(adapter.getItemCount() - 1);
                }
            }
        });

        feedbackBinding.btnSendFeedback.setOnClickListener(sendFeedback);
    }

    View.OnClickListener sendFeedback = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            // TODO 닉네임이 없는 경우("" 빈 문자열일 경우), 처리 확인하기
            String contents = feedbackBinding.feedbackContents.getText().toString();

            if (!contents.equals("")) { // 입력한 값이 있을 경우
                feedback.setNickname(Const.nickname);
                feedback.setAndroidId(Utility.getAndroidId(FeedbackActivity.this));
                feedback.setReportingDate(Utility.getDateWithDayOfWeek("yyyy년 MM월 d일"));
                feedback.setReportingTime(Utility.getTime_a_hh_mm());
                feedback.setContents(contents);

                // 전송 버튼을 클릭하면 DB에 저장한다.
                // androidId/feedback/currentTime.. 해당 경로에 저장된다.
                DatabaseUtility.writeFeedback(FeedbackActivity.this, androidId, feedback, new DatabaseUtility.onCompleteCallback() {
                    @Override
                    public void onComplete(boolean isSuccess) {
                        if (isSuccess) {
                            // 입력창 초기화
                            feedbackBinding.feedbackContents.setText("");

                            if (!feedback.getContents().equals("") && !Const.androidId.equals(Const.ADMIN_ANDROID_ID)) {
                                DatabaseUtility.addFeedbackListWithUser(Const.androidId, feedback, new DatabaseUtility.onCompleteCallback() {
                                    @Override
                                    public void onComplete(boolean isSuccess) {
//                                        if (isSuccess) {
//                                            Toast.makeText(getApplicationContext(), "피드백 완료!", Toast.LENGTH_SHORT).show();
//                                        } else {
//                                            Toast.makeText(getApplicationContext(), "피드백 실패!", Toast.LENGTH_SHORT).show();
//                                        }
                                    }
                                });
                            }
                        } else {

                        }
                    }
                });
            } else { // 입력한 값이 없을 경우

            }
        }
    };
}