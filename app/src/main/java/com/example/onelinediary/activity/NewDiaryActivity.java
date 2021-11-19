package com.example.onelinediary.activity;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.graphics.Bitmap;
import android.location.Location;
import android.net.Uri;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager2.widget.ViewPager2;

import com.example.onelinediary.R;
import com.example.onelinediary.adapter.ImagePagerAdapter;
import com.example.onelinediary.adapter.SelectMoodAdapter;
import com.example.onelinediary.constant.Const;
import com.example.onelinediary.databinding.ActivityNewDiaryBinding;
import com.example.onelinediary.dialog.ConfirmDialog;
import com.example.onelinediary.dialog.YesNoDialog;
import com.example.onelinediary.dto.Diary;
import com.example.onelinediary.dto.Emoji;
import com.example.onelinediary.dto.PhotoInfo;
import com.example.onelinediary.utiliy.DatabaseUtility;
import com.example.onelinediary.utiliy.LocationUtility;
import com.example.onelinediary.utiliy.Utility;
import com.google.android.material.tabs.TabLayoutMediator;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

public class NewDiaryActivity extends AppCompatActivity{
    private ActivityNewDiaryBinding newDiaryBinding;

    private final int PICKER_IMAGE_REQUEST = 100;

    Uri photoUri;

    ImagePagerAdapter imagePagerAdapter;
    SelectMoodAdapter selectMoodAdapter;

    Diary diary = new Diary();

    public ArrayList<PhotoInfo> newPhotoList = new ArrayList<>();

    int currentIndex = 0;

    boolean isChanged = false;

    private Emoji emoji;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        newDiaryBinding = ActivityNewDiaryBinding.inflate(getLayoutInflater());
        setContentView(newDiaryBinding.getRoot());

        newDiaryBinding.todayDate.setText(Utility.getDate(Const.REPORTING_DATE_FORMAT));

        getCurrentLocation(Const.currentLocation);

        if (Const.weatherResId > 0) {
            newDiaryBinding.currentWeather.setVisibility(View.VISIBLE);
            newDiaryBinding.currentWeather.setImageResource(Const.weatherResId);

            if (Const.weather != null && !Const.weather.equals("")) {
                diary.setWeather(Const.weather);
            } else {
                diary.setWeather("");
            }
        } else {
            newDiaryBinding.currentWeather.setVisibility(View.GONE);
        }

        newDiaryBinding.photo.setOnClickListener(v -> photoUri = Utility.selectPhoto(NewDiaryActivity.this, PICKER_IMAGE_REQUEST));

        newDiaryBinding.diaryContents.addTextChangedListener(watcher);

        imagePagerAdapter = new ImagePagerAdapter();

        // 이미지 초기화
        if (imagePagerAdapter.photoList.size() == 0) {
            for (int i = 0; i < 3; i++) {
                imagePagerAdapter.photoList.add(i, new PhotoInfo(null, null));
            }
        }

        newDiaryBinding.photo.setAdapter(imagePagerAdapter);
        imagePagerAdapter.isEditable = true;

        TabLayoutMediator mediator = new TabLayoutMediator(newDiaryBinding.indicator, newDiaryBinding.photo, (tab, position) -> { });

        // 뷰페이저와 탭레이아웃을 연결한다.
        // 뷰페이저2에서는 이 방법을 통해서 연결한다.
        mediator.attach();

        newDiaryBinding.photo.registerOnPageChangeCallback(new ViewPager2.OnPageChangeCallback() {
            @Override
            public void onPageSelected(int position) {
                super.onPageSelected(position);

                currentIndex = position;
            }
        });

        newDiaryBinding.emojiSelectRecyclerview.setLayoutManager(new LinearLayoutManager(this, RecyclerView.HORIZONTAL, false));
        selectMoodAdapter = new SelectMoodAdapter();

        /*
            arrays.xml에 만들어 둔 string-array를 가져와서 이모지 리스트를 만든다.
            새로 이모지가 추가 될 경우, string-array에 아이템 추가를 해주면 된다.
         */
        String[] arr = getResources().getStringArray(R.array.emoticon_arrays);
        ArrayList<Emoji> emojiList = new ArrayList<>();

        // 이모지 파일의 이름과 체크 여부를 저장한 객체로 이모지 리스트를 만든다.
        for (String emojiName : arr) {
            Emoji eData = new Emoji(emojiName, false);
            emojiList.add(eData);
        }

        // 이모지 레이아웃을 클릭했을 때 선택한 이모지의 이름과 체크 여부가 넘어온다.
        selectMoodAdapter.setList(emojiList, selEmoji -> {
            selectMoodAdapter.setSelectedEmoji(selEmoji);

            isChanged = selEmoji.checked;


            if(isChanged) {
                emoji = selEmoji;
                diary.setIconName(selEmoji.res);
            } else {
                emoji = null;
                diary.setIconName("");
            }
        });

        newDiaryBinding.emojiSelectRecyclerview.setAdapter(selectMoodAdapter);
    }

    private final TextWatcher watcher = new TextWatcher() {
        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {
        }

        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {
        }

        @Override
        public void afterTextChanged(Editable s) {
            isChanged = !s.toString().equals("");
        }
    };

    /**
     * 현재 위치의 위도 경도를 사용하여 도로명 주소로 바꾼 후 사용자에게 보여준다.
     * location == null이거나 도로명 주소로 변경이 되지 않는 경우에는 주소를 보여주지 않는다.
     *
     * @param location 현재 위치의 위도와 경도를 담고 있는 location 객체
     */
    private void getCurrentLocation(Location location) {
        if (location != null) {
            String address = LocationUtility.getAddress(location.getLatitude(), location.getLongitude(), this);
            if (!address.equals("")) {
                // 첫번째 공백 전의 텍스트를 삭제한다. 나라 지우기
                address = address.substring(address.indexOf(" ") + 1);
                newDiaryBinding.currentLocation.setVisibility(View.VISIBLE);
                newDiaryBinding.currentLocation.setText(address);
                diary.setLocation(address);
            } else {
                newDiaryBinding.currentLocation.setVisibility(View.GONE);
                newDiaryBinding.currentLocation.setText("");
            }
        } else {
            newDiaryBinding.currentLocation.setVisibility(View.GONE);
            newDiaryBinding.currentLocation.setText("");
        }
    }

    @Override
    public void onBackPressed() {
//        overridePendingTransition(R.anim.slide_in_down, R.anim.slide_out_down);
        if (isChanged) {
            new YesNoDialog(getString(R.string.dialog_message_notify_exit), v -> finish()).show(this);
        } else {
            finish();
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        // 선택 팝업이 뜨고 선택을 하거나 하지 않아도 무조건 거치는 메소드이기 때문에
        // 팝업이 떴다고 가정하고 팝업이 내려갈 때 여기서 첫번째 클릭의 시간을 초기화 시켜준다.
        // 초기화를 시키지 않으면 팝업이 내려가는 동시에 이미지뷰를 클릭을 했을 때 시간이 짧아서 팝업이 뜨지 않는다.
        // 그렇기때문에 첫번째 클릭 시간을 초기화 시켜줘야한다.
        imagePagerAdapter.initPreTime();

        if (requestCode == PICKER_IMAGE_REQUEST) { // 카메라와 갤러리 선택 팝업을 선택한 경우
            if (data != null && data.getData() != null) { // 갤러리를 선택했을 경우(구글 포토 포함)
                Uri selectedImageUri = data.getData();

                Bitmap imageBitmap;
                String path = Utility.getRealPathFromURI(this, selectedImageUri);
                imageBitmap = Utility.getRotatedBitmap(path);

                if (imagePagerAdapter != null && imagePagerAdapter.photoList.size() <= 3) {
                    imagePagerAdapter.addPhotoWithIndex(currentIndex, new PhotoInfo(path, imageBitmap));
                    imagePagerAdapter.isChanged = true;
                }

                isChanged = true;

                // photoUri는 무조건 생성되기 때문에 피커 중 아무것도 선택하지 않았거나 갤러리를 통해 이미지를 선택한 경우
                // 더미 이미지가 생기기 때문에 photoUri 경로로 연결된 이미지를 지워주기 제공자에 있는 이미지르 삭제해야한다.
                if (Const.photoUri != null) {
                    getContentResolver().delete(Const.photoUri, null, null);
                }
            } else { // 카메라를 선택했을 경우
                if (Const.photoUri != null) {
                    // uri로부터 Bitmap 이미지를 생성
                    Bitmap imageBitmap;

                    String path = Utility.getRealPathFromURI(this, Const.photoUri);
                    imageBitmap = Utility.getRotatedBitmap(path);

                    // photoUri는 무조건 생겨서 넘어오기 때문에 비트맵 이미지가 생성되는지 따로 체크한다.
                    if (imageBitmap != null) {
                        if (imagePagerAdapter != null && imagePagerAdapter.photoList.size() <= 3) {
                            imagePagerAdapter.addPhotoWithIndex(currentIndex, new PhotoInfo(path, imageBitmap));
                            imagePagerAdapter.isChanged = true;
                        }

                        isChanged = true;
                    }
                }
            }
        }
    }

    /**
     * 사진 등록이나 일기를 작성하고 기분을 선택한 후 저장 버튼을 눌러 DB에 저장을 한다.
     * 사진 등록이나 일기 작성은 선택 사항이지만 기분 선택은 필수로 정한다.
     * 선택하지 않은 상태로 저장 버튼을 누르면 기본 기분 아이콘아 선택 되도록 설정?
     *
     * @param view 저장 버튼 레이아웃
     */
    @SuppressLint("SimpleDateFormat")
    public void saveNewDiary(View view) {
        // 일기 작성한 시간 저장
        diary.setReportingDate(new SimpleDateFormat(Const.REPORTING_DATE_FORMAT).format(new Date()));

        // 작성한 일기 컨텐츠 저장
        String contents = newDiaryBinding.diaryContents.getText().toString().trim();
        diary.setContents(contents);

        if (newPhotoList.size() != 0) {
            newPhotoList.clear();
        }

        // 사진 저장
        for(PhotoInfo photoInfo : imagePagerAdapter.photoList) {
            if(photoInfo.getBitmapImage() != null) {
                newPhotoList.add(photoInfo);
            }
        }

        switch (newPhotoList.size()) {
            case 0:
                diary.setPhotoList(null);
                diary.setPhoto("");
                break;

            case 1:
                diary.setPhotoList(null);
                diary.setPhoto(newPhotoList.get(0).getPath());
                break;

            default:
                diary.setPhoto("");
                ArrayList<String> pathList = new ArrayList<>();
                for (int i = 0; i < newPhotoList.size(); i++) {
                    pathList.add(newPhotoList.get(i).getPath());
                }
                diary.setPhotoList(pathList);
                break;
        }

        // 오늘의 기분 선택
        if (emoji == null) {
            new ConfirmDialog(getString(R.string.dialog_message_confirm_mood), null).show(this);
        } else {
            DatabaseUtility.writeNewDiary(this, diary, isSuccess -> {
                if (isSuccess) {
                    new ConfirmDialog(getString(R.string.message_save_diary),
                            v -> {
                                setResult(RESULT_OK);
                                newPhotoList.clear();
                                finish();
                            }).show(this);
                } else {
                    new ConfirmDialog(getString(R.string.error_message_save_diary), null).show(this);
                }
            });
        }
    }
}