package com.example.onelinediary.utiliy;

import android.content.Context;

import androidx.annotation.NonNull;

import com.example.onelinediary.constant.Const;
import com.example.onelinediary.dto.Diary;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Queue;

/**
 * firebase Realtime database
 */
public class DatabaseUtility {

    private static HashMap<String, ArrayList<Diary>> yearDiaryList = new HashMap<>(); // 해당 연도의 일기를 담을 해시맵
    private static Queue<String> keyListQueue;

    public onCompleteCallback callback;

    public interface onCompleteCallback {
//        void onComplete(boolean isSuccess, HashMap<String, ArrayList<Diary>> result);
        void onComplete(boolean isSuccess);
    }
//    데이터 베이스의 인스턴스를 검색하고 쓰려고 하는 위치를 참조
//    FirebaseDatabase database = FirebaseDatabase.getInstance();
//    DatabaseReference reference = database.getReference(); // default

    // 인스턴스를 생성해서 데이터베이스 참조값을 반환한다.
    public static DatabaseReference getReference() {
        return FirebaseDatabase.getInstance().getReference();
    }

    public static void writeNewDiary(Context context, Diary newDiary, onCompleteCallback onComplete) {
        getReference() // 데이터 베이스 참조(default)
                .child(Utility.getAndroidId(context))
                .child("diary")
                .child(Utility.getYear())
                .child(Utility.getMonth())
                .child(Utility.getDay())
                .setValue(newDiary)
                .addOnSuccessListener(unused -> onComplete.onComplete(true))
                .addOnFailureListener(e -> onComplete.onComplete(false));
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
     * @param context 컨택스트
     * @param year 원하는 연도
     */
    public static void readYearDiaryList(Context context, String year, onCompleteCallback callback) {
        keyListQueue = new LinkedList<>(); // 넣은 순서 그대로 사용하기 위해서 LinkedList를 사용한다.
        Const.monthKeyList = new ArrayList<>();
        Query query = getReference().child(Utility.getAndroidId(context)).child("diary").child(year);

        query.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                    keyListQueue.offer(dataSnapshot.getKey()); // 큐에 key(월)을 넣어 저장한다.
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

    public static void readMonthDiaryList(Context context, String year, String month) {
        Query query = getReference().child(Utility.getAndroidId(context)).child("diary").child(year).child(month);

        ArrayList<Diary> dList = new ArrayList<>(); // 월 별 일기를 담을 리스트
        query.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
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
}
