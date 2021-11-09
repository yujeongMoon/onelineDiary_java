package com.example.onelinediary.activity;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;

import android.os.Bundle;
import android.view.View;
import android.view.ViewTreeObserver;

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

    int recyclerviewHeight = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        feedbackBinding = ActivityFeedbackBinding.inflate(getLayoutInflater());
        setContentView(feedbackBinding.getRoot());

        recyclerviewHeight = feedbackBinding.feedbackRecyclerview.getHeight();

        // 피드백을 읽어올 안드로이드 아이디를 전달 받는다.
        androidId = getIntent().getStringExtra(Const.INTENT_KEY_ANDROID_ID);

        feedbackBinding.feedbackRecyclerview.setLayoutManager(new LinearLayoutManager(this));

        adapter = new FeedbackAdapter(this);
        feedbackBinding.feedbackRecyclerview.setAdapter(adapter);

        DatabaseUtility.readFeedback(androidId, isSuccess -> {
            if (isSuccess) {
                adapter.addFeedbackList(Const.feedbackList);
                feedbackBinding.feedbackRecyclerview.scrollToPosition(adapter.getItemCount() - 1);
            }
        });

        feedbackBinding.btnSendFeedback.setOnClickListener(sendFeedback);

        // 리사이클러뷰의 layout이나 visivility가 바뀔때마다 호출된다.
        // 리사이클러뷰의 높이가 바뀔때마다 높이를 저장해두고 저장해둔 높이와 지금 높이가 같지 않고 작을 경우, 리사이클러뷰의 포지션을 제일 마지막으로 이동시킨다.
        feedbackBinding.feedbackRecyclerview.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {
                if (feedbackBinding.feedbackRecyclerview.getHeight() != recyclerviewHeight) {
                    if (recyclerviewHeight > feedbackBinding.feedbackRecyclerview.getHeight()) {
                        feedbackBinding.feedbackRecyclerview.scrollToPosition(adapter.getItemCount() - 1);
                    }
                    recyclerviewHeight = feedbackBinding.feedbackRecyclerview.getHeight();
                }
            }
        });
    }

    View.OnClickListener sendFeedback = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            String contents = feedbackBinding.feedbackContents.getText().toString();

            if (!contents.equals("")) { // 입력한 값이 있을 경우
                feedback.setNickname(Utility.getString(FeedbackActivity.this, Const.SP_KEY_NICKNAME));
                feedback.setAndroidId(Utility.getAndroidId(FeedbackActivity.this));
                feedback.setReportingDate(Utility.getDateWithDayOfWeek("yyyy년 MM월 d일"));
                feedback.setReportingTime(Utility.getTime_a_hh_mm());
                feedback.setContents(contents);

                // 입력창 초기화
                feedbackBinding.feedbackContents.setText("");

                // 전송 버튼을 클릭하면 DB에 저장한다.
                // androidId/feedback/currentTime.. 해당 경로에 저장된다.
                DatabaseUtility.writeFeedback(FeedbackActivity.this, androidId, feedback, new DatabaseUtility.onCompleteCallback() {
                    @Override
                    public void onComplete(boolean isSuccess) {
                        if (isSuccess) {
                            if (!feedback.getContents().equals("") && !androidId.equals(Const.ADMIN_ANDROID_ID)) {
                                DatabaseUtility.addFeedbackListWithUser(androidId, feedback, new DatabaseUtility.onCompleteCallback() {
                                    @Override
                                    public void onComplete(boolean isSuccess) {

                                    }
                                });
                            }
                        } else { // 피드백 작성이 실패했을 경우

                        }
                    }
                });
            } else { // 입력한 값이 없을 경우

            }
        }
    };
}