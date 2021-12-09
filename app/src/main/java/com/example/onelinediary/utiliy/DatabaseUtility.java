package com.example.onelinediary.utiliy;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.text.TextUtils;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;
import androidx.core.app.RemoteInput;

import com.example.onelinediary.R;
import com.example.onelinediary.constant.Const;
import com.example.onelinediary.custom.AppWidget;
import com.example.onelinediary.dto.Diary;
import com.example.onelinediary.dto.Feedback;
import com.example.onelinediary.dto.ItemNotice;
import com.example.onelinediary.dto.Notice;
import com.example.onelinediary.dto.PinInfo;
import com.example.onelinediary.dto.WidgetFeedList;
import com.example.onelinediary.dto.WidgetUserList;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.google.gson.Gson;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

/**
 * firebase Realtime database
 */
public class DatabaseUtility {

    private static final HashMap<String, ArrayList<Diary>> yearDiaryList = new HashMap<>(); // 해당 연도의 일기를 담을 해시맵

    private static final HashMap<String, ArrayList<ItemNotice>> allNoticeList = new HashMap<>();
    private static final ArrayList<ItemNotice> yearNoticeList = new ArrayList<>();

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

    public static void writeNewDiaryWithDate(Context context, String year, String month, String day, Diary newDiary, onCompleteCallback callback) {
        getReference() // 데이터 베이스 참조(default)
                .child(Utility.getAndroidId(context))
                .child(Const.DATABASE_CHILD_DIARY)
                .child(year)
                .child(month)
                .child(day)
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

    public static void readDiaryList(Context context, onCompleteResultCallback<ArrayList<String>> callback) {
        ArrayList<String> yearList = new ArrayList<>();
        Query query = getReference().child(Utility.getAndroidId(context)).child(Const.DATABASE_CHILD_DIARY);

        query.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                yearList.clear();
                for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                   yearList.add(dataSnapshot.getKey());
                }

                if (yearList.size() > 0)
                    callback.onComplete(true, yearList);
                else
                    callback.onComplete(false, null);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Log.e("DB__readYearDiaryList", "code : " + error.getCode() + ", message : " + error.getMessage());
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
        yearDiaryList.clear();

        query.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                // 데이터가 누적되기 때문에 초기화 시켜준다.
                Const.monthKeyList.clear();

                if (snapshot.getValue() == null || snapshot.getChildrenCount() == 0) {
                    callback.onComplete(false);
                }

                for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                    Const.monthKeyList.add(dataSnapshot.getKey());

                    readMonthDiaryList(context, year, dataSnapshot.getKey(), snapshot.getChildrenCount(), isSuccess -> {
                        if (isSuccess) {
                            Const.diaryList = yearDiaryList;
                            if (callback != null) {
                                if (Const.diaryList.size() == 0) {
                                    callback.onComplete(false);
                                } else {
                                    callback.onComplete(true);
                                }
                            }
                        } else {
                            callback.onComplete(false);
                        }
                    });
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Log.e("DB__readYearDiaryList", "code : " + error.getCode() + ", message : " + error.getMessage());
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
    public static void readMonthDiaryList(Context context, String year, String month, long keyCount, onCompleteCallback callback) {
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

                    if (diary != null) {
                        diary.setYear(year);
                        diary.setDay(dataSnapshot.getKey());
                    }
                    dList.add(diary);
                }
                yearDiaryList.put(month, dList); // 해당 월을 key로, 일기 리스트를 value로 저장한다.

                if (yearDiaryList.size() == keyCount) {
                    callback.onComplete(true);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Log.e("DB__readMonthDiaryList", "code : " + error.getCode() + ", message : " + error.getMessage());
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
                Log.e("DB__getNickname", "code : " + error.getCode() + ", message : " + error.getMessage());
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
    public static void readFeedback(Context context, String androidId, onCompleteCallback callback) {
        Const.feedbackList = new ArrayList<>();
        Query query = getReference().child(androidId).child(Const.DATABASE_CHILD_FEEDBACK);

        query.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                Const.feedbackList.clear();
                for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                    // 불러온 피드백 데이터를 리스트에 저장한다.
                    Feedback feedback = dataSnapshot.getValue(Feedback.class);
                    feedback.setFeedbackKey(dataSnapshot.getKey());
                    Const.feedbackList.add(feedback);
                }

                if (callback != null) {
                    if (Const.feedbackList.size() > 0) {
                        setWidgetFeedbackData(context, androidId);
                        callback.onComplete(true);
                    } else {
                        callback.onComplete(false);
                    }
                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Log.e("DB__readFeedback", "code : " + error.getCode() + ", message : " + error.getMessage());
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
    public static void readFeedbackListWithUser(Context context, onCompleteCallback callback) {
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

                if (callback != null) {
                    if (Const.userList.size() > 0 && Const.userLastFeedbackList.size() > 0) {
                        WidgetUserList widgetUserList = new WidgetUserList();
                        widgetUserList.userList = Const.userList;
                        widgetUserList.userLastFeedbackList = Const.userLastFeedbackList;

                        Gson gson = new Gson();
                        Utility.putString(context, "WidgetUserList", gson.toJson(widgetUserList));

                        callback.onComplete(true);
                    } else {
                        callback.onComplete(false);
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Log.e("DB__readFeedbackListWithUser", "code : " + error.getCode() + ", message : " + error.getMessage());
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
                Log.e("DB__getProfileImage", "code : " + error.getCode() + ", message : " + error.getMessage());
            }
        });
    }

    /**
     * 새로운 공지사항을 작성하거나 수정할 때 사용되는 메소드
     *
     * @param notice 공지사항
     * @param callback 콜백 메소드
     */
    public static void writeNotice(Notice notice, onCompleteCallback callback) {
        String reportingDate = String.valueOf(System.currentTimeMillis());

        getReference() // 데이터 베이스 참조(default)
                .child(Const.DATABASE_CHILD_ADMIN)
                .child(Const.DATABASE_CHILD_NOTICE)
                .child(Utility.getYear())
                .child(reportingDate)
                .setValue(notice)
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
     * admin/notice.. 경로에 저장된 공지사항 리스트를 가져오는 메소드
     * notice/연도/작성일자(currentMilis) 경로에 저장되어 있기 때문에 getNoticeListYear() 메소드에 키(연도)와 키 개수(연도의 개수)를 넘기면
     * 연도 아래의 작성일자 별 공지사항을 가져오고 키의 개수만큼 리스트가 채워지면 콜백 메소드로 결과를 반환한다.
     *
     * @param callback 콜백 메소드
     */
    public static void getNoticeListAll(onCompleteResultCallback<HashMap<String, ArrayList<ItemNotice>>> callback) {
        Query query = getReference().child(Const.DATABASE_CHILD_ADMIN).child(Const.DATABASE_CHILD_NOTICE);

        query.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                    getNoticeListYear(snapshot.getChildrenCount(), dataSnapshot.getKey(), isSuccess -> {

                        callback.onComplete(true, allNoticeList);
                    });
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Log.e("DB__getNoticeList", "code : " + error.getCode() + ", message : " + error.getMessage());
            }
        });
    }

    /**
     * admin/notice/연도.. 경로에 저장된 공지사항 리스트를 가져오는 메소드
     * 키(연도)와 키의 개수가 넘어오면 연도 아래 작성 일자별 공지사항을 가져와 리스트를 만들고 연도를 key, 리스트를 value로 하는 해시뱁을 만들어준다.
     *
     * @param keyCount 키(연도)의 개수
     * @param year 키
     * @param callback 콜백 메소드
     */
    public static void getNoticeListYear(long keyCount, String year, onCompleteCallback callback) {
        Query query = getReference().child(Const.DATABASE_CHILD_ADMIN).child(Const.DATABASE_CHILD_NOTICE).child(year);

        query.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                yearNoticeList.clear();

                ItemNotice itemNotice;
                for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                    Notice notice = dataSnapshot.getValue(Notice.class);
                    itemNotice = new ItemNotice(dataSnapshot.getKey(), notice ,false);
                    yearNoticeList.add(itemNotice);
                }

                allNoticeList.put(year, yearNoticeList);

                if (allNoticeList.size() == keyCount) {
                    callback.onComplete(true);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Log.e("DB__getNoticeList", "code : " + error.getCode() + ", message : " + error.getMessage());
            }
        });
    }

    /**
     * admin/notice/연도/작성일자..
     * 선택한 공지사항의 키를 넘겨받아 해당 위치의 공지사항을 삭제하는 메소드
     *
     * @param year 연도
     * @param reportingDate 작성 일자
     * @param callback 콜백 메소드
     */
    public static void deleteNotice(String year, String reportingDate, onCompleteCallback callback) {
        getReference()
                .child(Const.DATABASE_CHILD_ADMIN)
                .child(Const.DATABASE_CHILD_NOTICE)
                .child(year)
                .child(reportingDate)
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

    @RequiresApi(api = Build.VERSION_CODES.O)
    public static void noti(Context context, WidgetFeedList newWidgetFeedList) {
        String replyLabel = "채팅 메세지를 입력하세요!";
        RemoteInput remoteInput = new RemoteInput.Builder(Const.KEY_TEXT_REPLY)
                .setLabel(replyLabel)
                .build();

        Feedback feedback = newWidgetFeedList.feedbackArrayList.get(newWidgetFeedList.feedbackArrayList.size() - 1);
        Intent replyIntent = new Intent(context, AppWidget.class);
        replyIntent.setAction(AppWidget.ACTION_NOTIFICATION_REPLY);
        replyIntent.putExtra(Const.INTENT_KEY_USER_NICKNAME, feedback.getUserNickname());
        replyIntent.putExtra(Const.INTENT_KEY_USER_ANDROID_ID, feedback.getUserAndroidId());
        replyIntent.putExtra(Const.INTENT_KEY_USER_PROFILE_IMAGE_NAME, feedback.getProfileImageName());

        // Build a PendingIntent for the reply action to trigger.
        PendingIntent replyPendingIntent =
                PendingIntent.getBroadcast(context.getApplicationContext(),
                        0,
                        replyIntent,
                        PendingIntent.FLAG_UPDATE_CURRENT);

        // Create the reply action and add the remote input.
        NotificationCompat.Action action =
                new NotificationCompat.Action.Builder(R.drawable.dd,
                        "입력", replyPendingIntent)
                        .addRemoteInput(remoteInput)
                        .build();

        // Build the notification and add the action.
        Notification newMessageNotification = new NotificationCompat.Builder(context, "channelId")
                .setSmallIcon(R.drawable.face_black_24)
                .setContentTitle(newWidgetFeedList.feedbackArrayList.get(newWidgetFeedList.feedbackArrayList.size() - 1).getNickname())
                .setContentText(newWidgetFeedList.feedbackArrayList.get(newWidgetFeedList.feedbackArrayList.size() - 1).getContents())
                .setPriority(Notification.PRIORITY_HIGH)
                .addAction(action)
                .build();

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            int importance = NotificationManager.IMPORTANCE_HIGH;
            NotificationChannel channel = new NotificationChannel("channelId", "피드백 채널", importance);
            NotificationManager notificationManager = context.getSystemService(NotificationManager.class);
            notificationManager.createNotificationChannel(channel);
        }

        // Issue the notification.
        NotificationManagerCompat notificationManager = NotificationManagerCompat.from(context);
        notificationManager.notify(0, newMessageNotification);
    }

    public static void setWidgetFeedbackData(Context context, String androidId) {
        WidgetFeedList newWidgetFeedList = new WidgetFeedList();
        newWidgetFeedList.feedbackArrayList = new ArrayList<>();
        int maxCount = 10;
        if(Const.feedbackList.size() < maxCount) {
            maxCount = Const.feedbackList.size();
        }
        for (int i = Const.feedbackList.size() - maxCount; i <= Const.feedbackList.size() - 1; i++) {
            newWidgetFeedList.feedbackArrayList.add(Const.feedbackList.get(i));
        }

        Gson gson = new Gson();
        ArrayList<Feedback> prefFeedbackList = new ArrayList<>();
        WidgetFeedList oldWidgetFeedList = gson.fromJson(Utility.getString(context, "WidgetFeedbackList"), WidgetFeedList.class);

        String lastChat = Utility.getString(context, "feedbackLastChat_" + androidId);
        boolean isNeedNotification = true;
        if(!TextUtils.isEmpty(lastChat) && lastChat.equals(newWidgetFeedList.feedbackArrayList.get(newWidgetFeedList.feedbackArrayList.size() - 1).getFeedbackKey())) {
            isNeedNotification = false;
        }

        if(oldWidgetFeedList != null) {
            prefFeedbackList.addAll(oldWidgetFeedList.feedbackArrayList);
            if (!newWidgetFeedList.feedbackArrayList.get(newWidgetFeedList.feedbackArrayList.size() - 1).getFeedbackKey().equals(oldWidgetFeedList.feedbackArrayList.get(oldWidgetFeedList.feedbackArrayList.size() - 1).getFeedbackKey()) &&
                    !newWidgetFeedList.feedbackArrayList.get(newWidgetFeedList.feedbackArrayList.size() - 1).getAndroidId().equals(Utility.getAndroidId(context))) {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                    if(isNeedNotification) {
                        Utility.putString(context, "feedbackLastChat_" + androidId, newWidgetFeedList.feedbackArrayList.get(newWidgetFeedList.feedbackArrayList.size() - 1).getFeedbackKey());
                        noti(context, newWidgetFeedList);
                    }
                }
            }
        }
        Utility.putString(context, "WidgetFeedbackList", gson.toJson(newWidgetFeedList));
    }
}
