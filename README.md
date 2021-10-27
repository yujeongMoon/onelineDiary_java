# 한 줄 일기장
 
### 한 줄 일기장의 기본 기능
1. 일기 리스트 조회(메인 화면)
2. 일기 작성
3. 일기 수정
4. 일기 삭제

### 한 줄 일기장의 구성 과정

* 1차 - 화면 구성 및 기본 기능 구현(~ 21/09/30)
  * 화면 구성
    * 메인 화면
      * 연도, 월을 기준으로 일기들을 보여주고 하단에는 새로운 일기를 작성할 수 있는 화면으로 연결해주는 버튼과 설정 버튼으로 이루어져있다.
      * 화면에서 일기는 일과 그날의 기분으로 표시된다.
      * 이모지를 클릭하면 일기의 상세 화면으로 이동한다.

    * 입력 화면
      * 오늘의 날짜는 기본적으로 적용이 되고 사용자는 사진, 한 줄 일기, 그날의 기분으로 일기를 작성할 수 있다.
      * 사진은 카메라, 갤러리 등의 선택 사항이 있고 사진을 찍거나 선택하면 작성 화면에 적용되어 바로 볼 수 있다.
      * 이모지는 필수 선택 조건으로 꼭 선택해야 일기를 저장할 수 있다.
      * 저장 버튼을 누르면 메인 화면으로 이동한다.

    * 상세 화면 및 수정 화면
      * editMode 추가
        * editMode가 off이면 상세화면으로 작성한 일기를 보여준다.
        * on이면 사진, 한 줄 일기, 그날의 기분 등을 수정하여 선택한 날짜의 일기를 수정할 수 있다.

  * 데이터 베이스 연동
    * firebase realtime database
    * 조회, 저장, 수정, 삭제

  * 사진 연동
    * 사진 찍기
    * 앨범에서 선택

* 2차 - 부가 기능 추가(21/10/01 ~)
  * 내 위치 확인 및 날씨 연동(~21/10/13)
    * 날씨에 따라 스플래쉬나 일기 배경 바꿔주기

  * 새로운 피드백 적용!(21/10/13 ~ 21/10/31)
    * 일기를 하루에 여러개 등록(no)
      * 일기는 하루에 한 개로 정한 후 진행하는 것으로 변경
      * 기존의 일기를 유지
    * 사진을 한 번에 여러개 등록(yes) 
      * 기존에는 한 개만 등록할 수 있었는데 최대 3개로 변경
    * 로딩 프로그래스바 변경하기(다른 디자인)
    * 메인 화면을 캘린더 형식으로 변경하기

  * 설정 화면
    * 일기 초기화(예정)
    * 암호 설정
    * 문의하기 및 피드백
    * 버전 관리

  * 팝업 및 알림
    * 푸시나 팝업으로 날씨 알림(예정)
      * 기기의 최상단에 팝업 띄우기(windowManager) 
    * 공지사항이나 업데이트 관련 팝업 띄우기(추가된 기능 등)
    * 버전 비교해서 새로 업데이트 받을 수 있도록 알려주기

  * 위젯 생성
    
  * 일기의 상세 화면에서 전체 일기 보여주기(가로 스크롤 - 뷰페이저)

  * 사용자에게 닉네임을 사용할 수 있도록 제공

  * 좋아하는 일기 북마크 제공

