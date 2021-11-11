package com.example.onelinediary.activity;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.viewpager2.widget.ViewPager2;

import com.example.onelinediary.R;
import com.example.onelinediary.adapter.ImagePagerAdapter;
import com.example.onelinediary.constant.Const;
import com.example.onelinediary.databinding.ActivityDiaryDetailBinding;
import com.example.onelinediary.dialog.ConfirmDialog;
import com.example.onelinediary.dialog.SelectDialog;
import com.example.onelinediary.dto.Diary;
import com.example.onelinediary.dto.PhotoInfo;
import com.example.onelinediary.dialog.CustomProgressDialog;
import com.example.onelinediary.utiliy.DatabaseUtility;
import com.example.onelinediary.utiliy.Utility;
import com.google.android.material.tabs.TabLayoutMediator;

import java.util.ArrayList;

import static com.example.onelinediary.constant.Const.PICKER_IMAGE_REQUEST;

public class DiaryDetailActivity extends AppCompatActivity {
    private ActivityDiaryDetailBinding detailBinding;

    String month;
    String day;
    Diary diary;

    boolean isEnabled = false;
    boolean isUpdate = false;
    boolean isChanged = false;

    // 상세 화면에서 보여줄 리스트
    public ArrayList<PhotoInfo> oldPhotoList = new ArrayList<>();

    // 수정 화면에서 보여줄 리스트
   public ArrayList<PhotoInfo> newPhotoList = new ArrayList<>();
    
    int currentMood = Const.Mood.NONE.value;

    ImagePagerAdapter imagePagerAdapter;

    int currentIndex = 0;

    @SuppressLint("NotifyDataSetChanged")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        detailBinding = ActivityDiaryDetailBinding.inflate(getLayoutInflater());
        setContentView(detailBinding.getRoot());

        Intent items = getIntent();
        diary = items.getParcelableExtra(Const.INTENT_KEY_DIARY);
        month = items.getStringExtra(Const.INTENT_KEY_MONTH);
        day = diary.getDay();

        // 일기를 작성한 날짜
        detailBinding.detailTodayDate.setText(diary.getReportingDate());

        // 일기를 작성한 위치
        if (diary.getLocation().equals("")) {
            detailBinding.detailCurrentLocation.setVisibility(View.GONE);
        } else {
            detailBinding.detailCurrentLocation.setVisibility(View.VISIBLE);
            detailBinding.detailCurrentLocation.setText(diary.getLocation());
        }

        // 일기를 작성한 날의 날씨
        if (diary.getWeather().equals("")) {
            detailBinding.detailCurrentWeather.setVisibility(View.GONE);
        } else {
            detailBinding.detailCurrentWeather.setVisibility(View.VISIBLE);
            setCurrentWeather(diary.getWeather());
        }

        // 위치랑 날씨가 없다면 레이아웃을 gone 처리한다.
        if (detailBinding.detailCurrentLocation.getVisibility() == View.GONE && detailBinding.detailCurrentWeather.getVisibility() == View.GONE)
            detailBinding.detailInfoLayout.setVisibility(View.GONE);
        else
            detailBinding.detailInfoLayout.setVisibility(View.VISIBLE);

        // 일기를 작성할 때 선택한 기분
        setCurrentMood(diary.getMood());

        // 일기 내용
        detailBinding.textDetailDiaryContents.setText(diary.getContents());

        // 일기와 같이 선택한 사진
//        if (diary.getPhoto().equals("")) {
//            detailBinding.detailPhoto.setImageResource(R.drawable.default_placeholder_image);
//            detailBinding.detailPhoto.setBackground(ContextCompat.getDrawable(this, R.color.white));
//        } else {
            /*
                구글 포토에 있는 사진을 사용한 경우, uri에 "com.google.android.apps.photos.contentprovider"이 포함되어있다.
                구글 포토에서 사진을 선택하여 onActivityResult()에서 uri를 사용하는 경우에는 authority가 "com.google.android.apps.photos.contentprovider"로 지정되어 있기 때문에
                permission Denial이 발생하지 않는다.
                DB에 저장했다가 불러온 uri는 authority는 null이기 때문에 구글 포토에 있는 사진에 접근할 수가 없다.
                새로운 이미지 파일을 생성해서 선택한 이미지를 저장한 다음에 그 파일 경로를 통해 사진을 불러오는 등의 방법을 사용해야한다.(일단은)

                갤러리를 통해서 저장된 uri도 DB에서 불러올 때는 authority가 null이다.
                결론적으로 authority가 원인은 아닐 수 있다.

                새로운 파일을 생성해서 저장하는 방법은 비효율적이기 때문에 DB에 uri가 아닌 절대 경로를 저장하도록 바꿔서 진행했다.
                getContentResolver().query()로 cursor를 생성하고 입력한 uri에서 절대 경로를 알아낸다.
                절대 경로를 이용하면 구글 포토 권한이 필요 없기 때문에 잘 진행된다.
             */
//            Bitmap photo = Utility.getRotatedBitmap(diary.getPhoto());
//
//            if (photo != null) {
//                detailBinding.detailPhoto.setImageBitmap(photo);
//                detailBinding.detailPhoto.setBackground(ContextCompat.getDrawable(this, R.color.black));
//            } else {
//                Toast.makeText(getApplicationContext(), getString(R.string.error_message_get_photo), Toast.LENGTH_LONG).show();
//                detailBinding.detailPhoto.setImageResource(R.drawable.default_placeholder_image);
//                detailBinding.detailPhoto.setBackground(ContextCompat.getDrawable(this, R.color.white));
//            }
//        }

        detailBinding.switchEditMode.setOnCheckedChangeListener((buttonView, isChecked) -> {
            isEnabled = isChecked;

            if (isEnabled) {
                // editmode on
                // 이미지뷰에 클릭 이벤트를 추가하기 위해 알린다.
                imagePagerAdapter.setEditable(true);

                // 어뎁터의 리스트에 빈자리가 있다면 null로 채워주며 초기화한다.
                if (imagePagerAdapter.photoList.size() < 3) {
                    int size = imagePagerAdapter.photoList.size();
                    for (int i = 0; i < 3 - size; i++) {
                        newPhotoList.add(new PhotoInfo(null, null));
                    }
                } // 총 3개 보여주기

                imagePagerAdapter.addPhotoList(newPhotoList);

                detailBinding.indicator.setVisibility(View.VISIBLE);

                detailBinding.detailEmoji.setVisibility(View.GONE);

                detailBinding.detailEmojiLayout.setVisibility(View.VISIBLE);

                detailBinding.textDetailDiaryContentsLayout.setVisibility(View.GONE);
                detailBinding.editDetailDiaryContents.setVisibility(View.VISIBLE);

                detailBinding.editDetailDiaryContents.setText(diary.getContents());
                detailBinding.editDetailDiaryContents.addTextChangedListener(watcher);

                detailBinding.detailButtonLayout.setVisibility(View.VISIBLE);

                // 기존에 선택한 기분을 확인해서 표시해준다.
                currentMood = diary.getMood();
                initMood();

                detailBinding.emojiHappyLayout.setOnClickListener(onClickListener);
                detailBinding.emojiSmileLayout.setOnClickListener(onClickListener);
                detailBinding.emojiBlankLayout.setOnClickListener(onClickListener);
                detailBinding.emojiSadLayout.setOnClickListener(onClickListener);
                detailBinding.emojiNervousLayout.setOnClickListener(onClickListener);
            } else {
                // editmode off
                // 이미지뷰 클릭이 되지 않도록 설정한다.
                imagePagerAdapter.setEditable(false);

                detailBinding.detailPhoto.setCurrentItem(0);

                InputMethodManager im = (InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);
                im.hideSoftInputFromWindow(detailBinding.editDetailDiaryContents.getWindowToken(), 0);

                if (isUpdate) {
                    isUpdate = false;
                    setCurrentMood(diary.getMood());

                    if (newPhotoList.isEmpty()) {
                        newPhotoList.add(new PhotoInfo(null, null));
                    }

                    oldPhotoList.clear();
                    oldPhotoList.addAll(newPhotoList);
                    imagePagerAdapter.addPhotoList(oldPhotoList);
                } else {
                    newPhotoList.clear();
                    newPhotoList.addAll(oldPhotoList);
                    imagePagerAdapter.addPhotoList(oldPhotoList);
                }

                // 이미지가 1개 이하일 경우, indicator를 보여주지 않는다.
                if (newPhotoList.size() < 2) {
                    detailBinding.indicator.setVisibility(View.GONE);
                }

                detailBinding.detailEmoji.setVisibility(View.VISIBLE);

                // editmode를 해제하면 선택된 기분을 초기화해준다.
                resetSelectedState();
                detailBinding.detailEmojiLayout.setVisibility(View.GONE);

                detailBinding.textDetailDiaryContentsLayout.setVisibility(View.VISIBLE);
                detailBinding.editDetailDiaryContents.setVisibility(View.GONE);

                detailBinding.textDetailDiaryContents.setText(diary.getContents());

                detailBinding.detailButtonLayout.setVisibility(View.GONE);
            }
        });

        CustomProgressDialog pDialog = new CustomProgressDialog(this);
        pDialog.show();

        new Handler().postDelayed(() -> {
            imagePagerAdapter = new ImagePagerAdapter();

            detailBinding.detailPhoto.setAdapter(imagePagerAdapter);

            if (diary.getPhotoList() != null) { // 사진 여러장
                newPhotoList = Utility.createPhotoInfoList(diary.getPhotoList());
                imagePagerAdapter.addPhotoList(newPhotoList);
            } else {
                PhotoInfo photoInfo;
                if (!diary.getPhoto().equals("")) { // 사진 한장
                    photoInfo = new PhotoInfo(diary.getPhoto(), Utility.getRotatedBitmap(diary.getPhoto()));
                } else {
                    photoInfo = new PhotoInfo(null, null);
                }
                newPhotoList.add(photoInfo);
                imagePagerAdapter.addPhoto(newPhotoList.get(0));
                imagePagerAdapter.notifyDataSetChanged();
            }

            pDialog.dismiss();
            // 기존의 데이터 복사
            oldPhotoList.addAll(newPhotoList);

            TabLayoutMediator mediator = new TabLayoutMediator(detailBinding.indicator, detailBinding.detailPhoto, (tab, position) -> { });

            // 뷰페이저와 탭레이아웃을 연결한다.
            // 뷰페이저2에서는 이 방법을 통해서 연결한다.
            mediator.attach();

            // 이미지가 없거나 한개일 경우, indicator는 보여주지 않는다.
            if (imagePagerAdapter.photoList.size() < 2) {
                detailBinding.indicator.setVisibility(View.GONE);
            } else {
                detailBinding.indicator.setVisibility(View.VISIBLE);
            }

            detailBinding.detailPhoto.registerOnPageChangeCallback(new ViewPager2.OnPageChangeCallback() {
                @Override
                public void onPageSelected(int position) {
                    super.onPageSelected(position);

                    currentIndex = position;
                }
            });
        }, 500);
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

            if (diary.getContents().equals(s.toString())) {
                isChanged = false;
            }
        }
    };

    @Override
    public void onBackPressed() {
        if (isEnabled) {
            if (isChanged) {
                new SelectDialog(getString(R.string.dialog_message_exit_editmode), v -> {
                    detailBinding.switchEditMode.setChecked(false);
                    isChanged = false;
                }).show(getSupportFragmentManager(), "finishUpdateDiary");
            } else {
                detailBinding.switchEditMode.setChecked(false);
            }
        } else {
            finish();
        }
    }

    // newDiaryActivity와 같은 기능
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        imagePagerAdapter.initPreTime();

        if (requestCode == PICKER_IMAGE_REQUEST) { // 카메라와 갤러리 선택 팝업을 선택한 경우
            if (data != null && data.getData() != null) { // 갤러리를 선택했을 경우(구글 포토 포함)
                Uri selectedImageUri = data.getData();

                Bitmap imageBitmap;

                String path = Utility.getRealPathFromURI(this, selectedImageUri);
                imageBitmap = Utility.getRotatedBitmap(path);

                if (imagePagerAdapter != null && imagePagerAdapter.photoList.size() <= 3) {
                    newPhotoList.set(currentIndex, new PhotoInfo(path, imageBitmap));
                    imagePagerAdapter.addPhotoList(newPhotoList);
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
                            newPhotoList.set(currentIndex, new PhotoInfo(path, imageBitmap));
                            imagePagerAdapter.addPhotoList(newPhotoList);
                            imagePagerAdapter.isChanged = true;
                        }

                        isChanged = true;
                    }
                }
            }
        }
    }

    View.OnClickListener onClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            if (v.getId() == R.id.emoji_happy_layout) {
                if (currentMood != Const.Mood.HAPPY.value) {
                    resetSelectedState();
                }

                if (detailBinding.emojiHappyCheck.getVisibility() == View.INVISIBLE) {
                    detailBinding.emojiHappyCheck.setVisibility(View.VISIBLE);
                    currentMood = Const.Mood.HAPPY.value;
                } else {
                    detailBinding.emojiHappyCheck.setVisibility(View.INVISIBLE);
                    currentMood = Const.Mood.NONE.value;
                }
            } if (v.getId() == R.id.emoji_smile_layout) {
                if (currentMood != Const.Mood.SMILE.value) {
                    resetSelectedState();
                }
                if (detailBinding.emojiSmileCheck.getVisibility() == View.INVISIBLE) {
                    detailBinding.emojiSmileCheck.setVisibility(View.VISIBLE);
                    currentMood = Const.Mood.SMILE.value;
                } else {
                    detailBinding.emojiSmileCheck.setVisibility(View.INVISIBLE);
                    currentMood = Const.Mood.NONE.value;
                }
            } if (v.getId() == R.id.emoji_blank_layout) {
                if (currentMood != Const.Mood.BLANK.value) {
                    resetSelectedState();
                }
                if (detailBinding.emojiBlankCheck.getVisibility() == View.INVISIBLE) {
                    detailBinding.emojiBlankCheck.setVisibility(View.VISIBLE);
                    currentMood = Const.Mood.BLANK.value;
                } else {
                    detailBinding.emojiBlankCheck.setVisibility(View.INVISIBLE);
                    currentMood = Const.Mood.NONE.value;
                }
            } if (v.getId() == R.id.emoji_sad_layout) {
                if (currentMood != Const.Mood.SAD.value) {
                    resetSelectedState();
                }
                if (detailBinding.emojiSadCheck.getVisibility() == View.INVISIBLE) {
                    detailBinding.emojiSadCheck.setVisibility(View.VISIBLE);
                    currentMood = Const.Mood.SAD.value;
                } else {
                    detailBinding.emojiSadCheck.setVisibility(View.INVISIBLE);
                    currentMood = Const.Mood.NONE.value;
                }
            } if (v.getId() == R.id.emoji_nervous_layout) {
                if (currentMood != Const.Mood.NERVOUS.value) {
                    resetSelectedState();
                }
                if (detailBinding.emojiNervousCheck.getVisibility() == View.INVISIBLE) {
                    detailBinding.emojiNervousCheck.setVisibility(View.VISIBLE);
                    currentMood = Const.Mood.NERVOUS.value;
                } else {
                    detailBinding.emojiNervousCheck.setVisibility(View.INVISIBLE);
                    currentMood = Const.Mood.NONE.value;
                }
            }

            if (currentMood == Const.Mood.NONE.value || currentMood == diary.getMood()) {
                isChanged = false;
            } else {
                isChanged = true;
            }
        }
    };

    private void setCurrentWeather(String weather) {
        switch (weather) {
            case "1":
                detailBinding.detailCurrentWeather.setImageResource(R.drawable.weather_rain);
                break;
            case "3":
                detailBinding.detailCurrentWeather.setImageResource(R.drawable.weather_snow);
                break;
            case "4":
                detailBinding.detailCurrentWeather.setImageResource(R.drawable.weather_shower);
                break;
            case "01":
                detailBinding.detailCurrentWeather.setImageResource(R.drawable.weather_sunny);
                break;
            case "03":
                detailBinding.detailCurrentWeather.setImageResource(R.drawable.weather_cloud);
                break;
            case "04":
                detailBinding.detailCurrentWeather.setImageResource(R.drawable.weather_blur);
                break;
        }
    }

    private void resetSelectedState() {
        detailBinding.emojiHappyCheck.setVisibility(View.INVISIBLE);
        detailBinding.emojiSmileCheck.setVisibility(View.INVISIBLE);
        detailBinding.emojiBlankCheck.setVisibility(View.INVISIBLE);
        detailBinding.emojiSadCheck.setVisibility(View.INVISIBLE);
        detailBinding.emojiNervousCheck.setVisibility(View.INVISIBLE);

        currentMood = Const.Mood.NONE.value;
        isChanged = false;
    }

    private void initMood() {
        switch (currentMood) {
            case 1:
                detailBinding.emojiHappyCheck.setVisibility(View.VISIBLE);
                break;
            case 2:
                detailBinding.emojiSmileCheck.setVisibility(View.VISIBLE);
                break;
            case 3:
                detailBinding.emojiBlankCheck.setVisibility(View.VISIBLE);
                break;
            case 4:
                detailBinding.emojiSadCheck.setVisibility(View.VISIBLE);
                break;
            case 5:
                detailBinding.emojiNervousCheck.setVisibility(View.VISIBLE);
                break;
        }
    }

    private void setCurrentMood(int mood) {
        switch (mood) {
            case 1:
                detailBinding.detailEmoji.setImageResource(R.drawable.emoji_happy_icon);
                break;
            case 2:
                detailBinding.detailEmoji.setImageResource(R.drawable.emoji_blushing_icon);
                break;
            case 3:
                detailBinding.detailEmoji.setImageResource(R.drawable.emoji_blank_icon);
                break;
            case 4:
                detailBinding.detailEmoji.setImageResource(R.drawable.emoji_consoling_icon);
                break;
            case 5:
                detailBinding.detailEmoji.setImageResource(R.drawable.emoji_nervous_icon);
                break;
        }
    }

    public void editDiary(View view) {
        // 작성한 일기 컨텐츠 저장
        String contents = detailBinding.editDetailDiaryContents.getText().toString().trim();
        diary.setContents(contents);

        diary.setMood(currentMood);

        // 사진 저장
        if (newPhotoList.size() != 0) {
            newPhotoList.clear();
        }

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
        if (currentMood == Const.Mood.NONE.value) {
            new ConfirmDialog(getString(R.string.dialog_message_confirm_mood), null).show(getSupportFragmentManager(), "mood");
        } else {
            DatabaseUtility.updateDiary(this, Utility.getYear(), month, day, diary, isSuccess -> {
                if (isSuccess) {
                    isUpdate = true;
                    isChanged = false;

                    detailBinding.switchEditMode.setChecked(false);
                    Toast.makeText(getApplicationContext(), getString(R.string.message_update_diary), Toast.LENGTH_LONG).show();
                } else {
                    Toast.makeText(getApplicationContext(), getString(R.string.error_message_update_diary), Toast.LENGTH_LONG).show();
                }
            });
        }
    }
}