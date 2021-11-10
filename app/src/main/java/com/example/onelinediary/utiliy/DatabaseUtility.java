package com.example.onelinediary.utiliy;

import android.content.Context;
import android.text.TextUtils;
import android.util.Log;

import androidx.annotation.NonNull;

import com.example.onelinediary.constant.Const;
import com.example.onelinediary.dto.Diary;
import com.example.onelinediary.dto.Feedback;
import com.example.onelinediary.dto.PinInfo;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;
import java.util.Objects;
import java.util.Queue;

import okhttp3.internal.Util;

/**
 * firebase Realtime database
 */
public class DatabaseUtility {

    private static HashMap<String, ArrayList<Diary>> yearDiaryList = new HashMap<>(); // 해당 연도의 일기를 담을 해시맵
//    private static Queue<String> keyListQueue;

    public onCompleteCallback callback;

    public interface onCompleteCallback {
//        void onComplete(boolean isSuccess, HashMap<String, ArrayList<Diary>> result);
        void onComplete(boolean isSuccess);
    }

    public interface onCompleteResultCallback<T> {
        void onComplete(boolean isSuccess, T result);
    }
//    데이터 베이스의 인스턴스를 검색하고 쓰려고 하는 위치를 참조
//    FirebaseDatabase database = FirebaseDatabase.getInstance();
//    DatabaseReference reference = database.getReference(); // default

    // 인스턴스를 생성해서 데이터베이스 참조값을 반환한다.
    public static DatabaseReference getReference() {
        return FirebaseDatabase.getInstance().getReference();
    }

    public static void writeNewDiary(Context context, Diary newDiary, onCompleteCallback callback) {
        getReference() // 데이터 베이스 참조(default)
                .child(Utility.getAndroidId(context))
                .child(Const.DATABASE_CHILD_DIARY)
                .child(Utility.getYear())
                .child(Utility.getMonth())
                .child(Utility.getDay())
                .setValue(newDiary)
                .addOnSuccessListener(unused -> callback.onComplete(true))
                .addOnFailureListener(e -> callback.onComplete(false));
    }

    /**
     * <addValueEventListener의 onDataChange() 안에서 값 가져오는 방법>
     * snapshot.getChildrenCount() : query(year) 아래의 children(month) 개수
     * dataSnapshot : { key = 09, value = {15={mood=1, contents=, photo=, reportingDate=2020-09-15}, 16={mood=3, contents=, photo=, reportingDate=2020-09-16}} } / key : 월, value : {key : 일, value : 일기}
     * dataSnapshot.getKey() : children의 키(month)
     *
     * 원하는 연도를 입력하면 해당 연도의 달을 큐에 순차적으로 저장한다.
     * 달마다 일기를 가져와야하는데 다 가져와서 저장하기 전에 초기화 되는 것을 막기위헤 강제적으로 동기화를 시켜준다(큐 사용)
     * 키를 저장해둔 큐를 사용하여 해당 달의 일기를 불러온다.(requestMonthList 호출)
     *
     * 실패할 경우, 에러처리 필요
     *
     * @param context 컨택스트
     * @param year 원하는 연도
     */
    public static void readYearDiaryList(Context context, String year, onCompleteCallback callback) {
//        keyListQueue = new LinkedList<>(); // 넣은 순서 그대로 사용하기 위해서 LinkedList를 사용한다.
        Const.monthKeyList = new ArrayList<>();
        Query query = getReference().child(Utility.getAndroidId(context)).child(Const.DATABASE_CHILD_DIARY).child(year);

        query.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                // 데이터가 누적되기 때문에 초기화 시켜준다.
                Const.monthKeyList.clear();

                for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
//                    keyListQueue.offer(dataSnapshot.getKey()); // 큐에 key(월)을 넣어 저장한다.
                    Const.monthKeyList.add(dataSnapshot.getKey());

                    readMonthDiaryList(context, year, dataSnapshot.getKey());
                }
//                requestMonthList(context, year, callback); // 큐에 저정한 키값을 사용하여 해당 연도 일기가 저장된 해시맵을 완성한다.
                Const.diaryList = yearDiaryList;
                callback.onComplete(true);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

//    private static void requestMonthList(final Context context, final String year, onCompleteCallback callback) {
//        if(keyListQueue.isEmpty()) { // 큐가 비었다면(해시맵이 완성되었다면)
//            Const.diaryList = yearDiaryList;
//            callback.onComplete(true);
//            return;
//        }
//
//        readMonthDiaryList(context, year, keyListQueue.poll()); // 큐에서 키값 하나씩 빼서 사용한다.
//    }

    /**
     * readYearDiaryList() 안에서 반복적으로 호출되어 달마다 일기를 저장해주는 메소드
     * 연도와 달이 키 값으로 넘어오면 그 달에 있는 일기들을 불러와서 리스트에 저장해준다.
     *
     * 실패할 경우, 에러처리 필요
     *
     * @param context 컨텍스트
     * @param year 연도
     * @param month 월
     */
    public static void readMonthDiaryList(Context context, String year, String month) {
        Query query = getReference().child(Utility.getAndroidId(context)).child(Const.DATABASE_CHILD_DIARY).child(year).child(month);

        ArrayList<Diary> dList = new ArrayList<>(); // 월 별 일기를 담을 리스트
        query.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                // 수정하거나 추가할때마다 읽어오는데 리스트에 쌓여서 배로 개수가 늘어난다.
                // 그래서 리스트 초기화가 필요하다.
                dList.clear();

                for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                    Diary diary = dataSnapshot.getValue(Diary.class); // 하루 일기를 객체로 생성한다.
                    diary.setDay(dataSnapshot.getKey());
                    dList.add(diary);
                }
                yearDiaryList.put(month, dList); // 해당 월을 key로, 일기 리스트를 value로 저장한다.
//                requestMonthList(context, year); // 다음 월의 일기를 저장하기 위해 호출한다.
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    /**
     * 이미 작성한 일기를 수정할 때 호출되는 메소드
     * 연도, 월, 일을 넘겨주면 해당 날짜의 일기를 변경해준다.
     * updateChildren()은 firebase에서 제공하는 메소드로 일기를 한번에 수정할 때 유용하다.
     * setValue를 통해서도 수정이 가능하다. (같은 키값으로 새로운 값을 넣으면 덮어쓰기가 된다.)
     *
     * @param context 컨텍스트
     * @param year 연도
     * @param month 월
     * @param day 일
     * @param diary 수정된 일기 내용
     * @param callback 콜백 메소드
     */
    public static void updateDiary(Context context, String year, String month, String day, Diary diary, onCompleteCallback callback) {
        Map<String, Object> diaryUpdate = new HashMap<>();
        diaryUpdate.put("/" + Utility.getAndroidId(context) + "/" + Const.DATABASE_CHILD_DIARY + "/" + year + "/" + month + "/" + day, diary.toMap());

        getReference().updateChildren(diaryUpdate)
                .addOnSuccessListener(unused -> callback.onComplete(true))
                .addOnFailureListener(e -> callback.onComplete(false));
    }

    /**
     * 선택한 일기를 삭제하는 메소드
     * 연도, 월, 일을 입력받아 일기의 위치를 찾아 삭제한다.
     *
     * @param context 컨텍스트
     * @param year 연도
     * @param month 월
     * @param day 일
     * @param callback 콜백 메소드
     */
    public static void deleteDiary(Context context, String year, String month, String day, onCompleteCallback callback) {
        getReference()
                .child(Utility.getAndroidId(context))
                .child(Const.DATABASE_CHILD_DIARY)
                .child(year)
                .child(month)
                .child(day)
                .removeValue()
                .addOnSuccessListener(unused -> callback.onComplete(true))
                .addOnFailureListener(e -> callback.onComplete(false));
    }

    /**
     * 사용자의 닉네임을 등록하는 메소드
     * 안드로이드 아이디로는 구분하기 어렵기때문에 닉네임을 설정한다.
     * TODO 중복 가능?
     *
     * @param context 컨텍스트
     * @param nickname 닉네임
     * @param callback 콜백 메소드
     */
    public static void setNickname(Context context, String nickname, onCompleteCallback callback) {
        getReference()
                .child(Utility.getAndroidId(context))
                .child(Const.DATABASE_CHILD_MYINFO)
                .child(Const.DATABASE_CHILD_NICKNAME)
                .setValue(nickname)
                .addOnSuccessListener(unused -> callback.onComplete(true))
                .addOnFailureListener(e -> callback.onComplete(false));
    }

    /**
     * DB에서 저장된 닉네임을 가져오는 메소드
     * 설정한 닉네임이 있다면 닉네임 값을 가져오고 없을 경우에는 빈 문자열을 결과로 넘겨준다.
     * 닉네임의 경우, 빈 문자열은 닉네임이 없음을 의미한다.
     *
     * @param context 컨텍스트
     * @param callback 콜백 메소드
     */
    public static void getNickname(Context context, onCompleteResultCallback<String> callback) {
        Query query = getReference().child(Utility.getAndroidId(context)).child(Const.DATABASE_CHILD_MYINFO).child(Const.DATABASE_CHILD_NICKNAME);

        query.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.getValue() != null) {
                    if (TextUtils.isEmpty(snapshot.getValue().toString())) {
                        callback.onComplete(false, "");
                    } else {
                        callback.onComplete(true, snapshot.getValue().toString());
                    }
                } else {
                    callback.onComplete(false, "");
                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    // TODO 피드백 저장하기
    /**
     * 피드백의 내용이나 답변을 DB에 저장하는 메소드
     * 각자의 안드로이드 아이디 테이블 안에 피드백 테이블을 생성해서 저장한다.
     * 사용자와 관리자는 사용자의 피드백 테이블을 공유하면서 1:1 대화를 주고 받는다.
     *
     * @param context 컨텍스트
     * @param feedback 작성한 피드백
     * @param callback 콜백 매소드
     */
    public static void writeFeedback(Context context, String androidId, Feedback feedback, onCompleteCallback callback) {
        String reportingDate = String.valueOf(System.currentTimeMillis());

        getReference()
                .child(androidId)
                .child(Const.DATABASE_CHILD_FEEDBACK)
                .child(reportingDate)
                .setValue(feedback)
                .addOnSuccessListener(unused -> callback.onComplete(true))
                .addOnFailureListener(e -> callback.onComplete(false));
    }

    public static void readFeedback(String androidId, onCompleteCallback callback) {
        Const.feedbackList = new ArrayList<>();
        Query query = getReference().child(androidId).child(Const.DATABASE_CHILD_FEEDBACK);

        query.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                Const.feedbackList.clear();
                for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                    // 불러온 피드백 데이터를 리스트에 저장한다.
                    Const.feedbackList.add(dataSnapshot.getValue(Feedback.class));
                }

                callback.onComplete(true);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    public static void addFeedbackListWithUser(String androidId, Feedback feedback, onCompleteCallback callback) {
        getReference()
                .child(Const.DATABASE_CHILD_ADMIN)
                .child(Const.DATABASE_CHILD_FEEDBACK_LIST)
                .child(androidId)
                .setValue(feedback)
                .addOnSuccessListener(unused -> callback.onComplete(true))
                .addOnFailureListener(e -> callback.onComplete(false));
    }

    public static void readFeedbackListWithUser(onCompleteCallback callback) {
        Const.userList = new ArrayList<>();
        Const.userLastFeedbackList = new ArrayList<>();

        Query query = getReference().child(Const.DATABASE_CHILD_ADMIN).child(Const.DATABASE_CHILD_FEEDBACK_LIST);

        query.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                Const.userList.clear();
                Const.userLastFeedbackList.clear();

                for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                    Const.userList.add(dataSnapshot.getKey());
                    Const.userLastFeedbackList.add(dataSnapshot.getValue(Feedback.class));
                }

                callback.onComplete(true);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    public static void setPinNumber(Context context, PinInfo pinInfo, onCompleteCallback callback) {
        getReference()
                .child(Utility.getAndroidId(context))
                .child(Const.DATABASE_CHILD_MYINFO)
                .child("security")
                .child("pin")
                .setValue(pinInfo)
                .addOnSuccessListener(unused -> callback.onComplete(true))
                .addOnFailureListener(e -> callback.onComplete(false));
    }
}
