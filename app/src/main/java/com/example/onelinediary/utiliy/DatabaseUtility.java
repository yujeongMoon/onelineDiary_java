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
import java.util.Map;

/**
 * firebase Realtime database
 */
public class DatabaseUtility {

    private static final HashMap<String, ArrayList<Diary>> yearDiaryList = new HashMap<>(); // 해당 연도의 일기를 담을 해시맵

    public interface onCompleteCallback {
        void onComplete(boolean isSuccess);
    }

    public interface onCompleteResultCallback<T> {
        void onComplete(boolean isSuccess, T result);
    }
    // 데이터 베이스의 인스턴스를 검색하고 쓰려고 하는 위치를 참조
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
                .addOnSuccessListener(unused -> {
                    if (callback != null) {
                        callback.onComplete(true);
                    }
                })
                .addOnFailureListener(e -> {
                    if (callback != null) {
                        callback.onComplete(false);
                    }
                });
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
        Const.monthKeyList = new ArrayList<>();
        Query query = getReference().child(Utility.getAndroidId(context)).child(Const.DATABASE_CHILD_DIARY).child(year);

        query.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                // 데이터가 누적되기 때문에 초기화 시켜준다.
                Const.monthKeyList.clear();

                for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                    Const.monthKeyList.add(dataSnapshot.getKey());

                    readMonthDiaryList(context, year, dataSnapshot.getKey());
                }
                Const.diaryList = yearDiaryList;

                if (callback != null)
                    callback.onComplete(true);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Log.d("DB__readYearDiaryList", "code : " + error.getCode() + ", message : " + error.getMessage());
            }
        });
    }

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

                    if (diary != null)
                        diary.setDay(dataSnapshot.getKey());
                    dList.add(diary);
                }
                yearDiaryList.put(month, dList); // 해당 월을 key로, 일기 리스트를 value로 저장한다.
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Log.d("DB__readMonthDiaryList", "code : " + error.getCode() + ", message : " + error.getMessage());
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
                .addOnSuccessListener(unused -> {
                    if (callback != null) {
                        callback.onComplete(true);
                    }
                })
                .addOnFailureListener(e -> {
                    if (callback != null) {
                        callback.onComplete(false);
                    }
                });
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
                .addOnSuccessListener(unused -> {
                    if (callback != null) {
                        callback.onComplete(true);
                    }
                })
                .addOnFailureListener(e -> {
                    if (callback != null) {
                        callback.onComplete(false);
                    }
                });
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
                .addOnSuccessListener(unused -> {
                    if (callback != null) {
                        callback.onComplete(true);
                    }
                })
                .addOnFailureListener(e -> {
                    if (callback != null) {
                        callback.onComplete(false);
                    }
                });
    }

    /**
     * DB에서 저장된 닉네임을 가져오는 메소드
     * 설정한 닉네임이 있다면 닉네임 값을 가져오고 없을 경우에는 빈 문자열을 결과로 넘겨준다.
     * 닉네임의 경우, 빈 문자열은 닉네임이 없음을 의미한다.
     *
     * @param androidId 닉네임을 가져올 안드로이드 아이디
     * @param callback 콜백 메소드
     */
    public static void getNickname(String androidId, onCompleteResultCallback<String> callback) {
        Query query = getReference().child(androidId).child(Const.DATABASE_CHILD_MYINFO).child(Const.DATABASE_CHILD_NICKNAME);

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
                Log.d("DB__getNickname", "code : " + error.getCode() + ", message : " + error.getMessage());
            }
        });
    }

    /**
     * 피드백의 내용이나 답변을 DB에 저장하는 메소드
     * 각자의 안드로이드 아이디 테이블 안에 피드백 테이블을 생성해서 저장한다.
     * 사용자와 관리자는 사용자의 피드백 테이블을 공유하면서 1:1 대화를 주고 받는다.
     *
     * @param feedback 작성한 피드백
     * @param callback 콜백 매소드
     */
    public static void writeFeedback(String androidId, Feedback feedback, onCompleteCallback callback) {
        String reportingDate = String.valueOf(System.currentTimeMillis());

        getReference()
                .child(androidId)
                .child(Const.DATABASE_CHILD_FEEDBACK)
                .child(reportingDate)
                .setValue(feedback)
                .addOnSuccessListener(unused -> {
                    if (callback != null) {
                        callback.onComplete(true);
                    }
                })
                .addOnFailureListener(e -> {
                    if (callback != null) {
                        callback.onComplete(false);
                    }
                });
    }

    /**
     * 사용자가 문의한 피드백 내용과 관리자의 답변을 불러오는 메소드
     * 사용자와 관리자의 1:1 대화 방식으로 사용자의 feedback에 내용이 저장된다.
     *
     * @param androidId 사용자의 안드로이드 아이디
     * @param callback 콜백 메소드
     */
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

                if (callback != null)
                    callback.onComplete(true);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Log.d("DB__readFeedback", "code : " + error.getCode() + ", message : " + error.getMessage());
            }
        });
    }

    /**
     * 해당 채팅창에서 마지막으로 남긴 피드백을 admin/feedbackList에 저장한다.
     * 마지막으로 피드백 객체를 저장해둔다.
     *
     * @param androidId 피드백한 사용자의 안드로이드 아이디
     * @param feedback 사용자의 피드백
     * @param callback 콜백 메소드
     */
    public static void addFeedbackListWithUser(String androidId, Feedback feedback, onCompleteCallback callback) {
        getReference()
                .child(Const.DATABASE_CHILD_ADMIN)
                .child(Const.DATABASE_CHILD_FEEDBACK_LIST)
                .child(androidId)
                .setValue(feedback)
                .addOnSuccessListener(unused -> {
                    if (callback != null) {
                        callback.onComplete(true);
                    }
                })
                .addOnFailureListener(e -> {
                    if (callback != null) {
                        callback.onComplete(false);
                    }
                });
    }

    /**
     * admin/feedbackList에 저장된 마지막 피드백 전체를 불러온다.
     * 관리자 화면을 구성하기위한 메소드로 불러온 리스트로 채팅방 입장 전 사용자 목록을 만든다.
     *
     * @param callback 콜백 메소드
     */
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

                if (callback != null)
                    callback.onComplete(true);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Log.d("DB__readFeedbackListWithUser", "code : " + error.getCode() + ", message : " + error.getMessage());
            }
        });
    }

    /**
     * 암호 설정 중 핀 번호를 저장하는 메소드
     * 핀 번호 설정을 on 시키고 번호를 설정할 때 사용된다.
     *
     * @param context 컨텍스트
     * @param pinInfo 핀 번호의 정보
     * @param callback 콜백 메소드
     */
    public static void setPinNumber(Context context, PinInfo pinInfo, onCompleteCallback callback) {
        getReference()
                .child(Utility.getAndroidId(context))
                .child(Const.DATABASE_CHILD_MYINFO)
                .child(Const.DATABASE_CHILD_SECURITY)
                .child(Const.DATABASE_CHILD_PIN)
                .setValue(pinInfo)
                .addOnSuccessListener(unused -> {
                    if (callback != null) {
                        callback.onComplete(true);
                    }
                })
                .addOnFailureListener(e -> {
                    if (callback != null) {
                        callback.onComplete(false);
                    }
                });
    }

    /**
     * DB에 프로필 이미지를 저장하는 메소드
     * 이미지 리소스의 이름을 저장한다.
     *
     * @param context 컨텍스트
     * @param profileImageName 프로필 이미지 리소스의 이름
     * @param callback 콜백 메소드
     */
    public static void setProfileImage(Context context, String profileImageName, onCompleteCallback callback) {
        getReference()
                .child(Utility.getAndroidId(context))
                .child(Const.DATABASE_CHILD_MYINFO)
                .child(Const.DATABASE_CHILD_PROFILE)
                .child(Const.DATABASE_CHILD_PROFILE_IMAGE)
                .setValue(profileImageName)
                .addOnSuccessListener(unused -> {
                    if (callback != null) {
                        callback.onComplete(true);
                    }
                })
                .addOnFailureListener(e -> {
                    if (callback != null) {
                        callback.onComplete(false);
                    }
                });
    }

    /**
     * 설정한 프로필 이미지를 DB에서 가져오는 메소드
     * 저장된 프로필 이미지가 있을 경우, 리소스 이름을 콜백 메소드로 넘겨주고
     * 저장된 프로필 이미지가 없을 경우, 빈 문자열을 콜백 메소드로 넘겨준다.
     *
     * @param androidId 사용자의 안드로이드 아이디
     * @param callback 콜백 메소드
     */
    public static void getProfileImage(String androidId, onCompleteResultCallback<String> callback) {
        Query query = getReference().child(androidId).child(Const.DATABASE_CHILD_MYINFO).child(Const.DATABASE_CHILD_PROFILE).child(Const.DATABASE_CHILD_PROFILE_IMAGE);

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
                Log.d("DB__getProfileImage", "code : " + error.getCode() + ", message : " + error.getMessage());
            }
        });
    }
}
