package com.example.onelinediary.utiliy;

import android.util.Log;

import com.example.onelinediary.dto.ConnError;
import com.example.onelinediary.dto.Weather;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;

/**
 * 기상청 open api - 단기예보 조회 서비스
 *
 * open api serviceKey
 * encoding => 웹 브라우저에서 Call할 때
 * decoding => 프로그램을 돌리고 싶을 때, decoding키를 사용하면 알아서 encoding키로 데이터를 불러온다.
 */
public class WeatherUtility {
    public interface OnCompleteCallback<T, K> {
        void onComplete(boolean isSuccess, T result, K error);
    }

    public static double latitude;
    public static double longitude;

    public static double X;
    public static double Y;

    public static void getGridCoordinate(double _latitude, double _longitude) {
        double RE = 6371.00877; // 지구 반경(km)
        double GRID = 5.0; // 격자 간격(km)
        double SLAT1 = 30.0; // 투영위도 1(degree)
        double SLAT2 = 60.0; // 투영위도 2(degree)
        double OLON = 126.0; // 기준점 경도(degree)
        double OLAT = 38.0; // 기준점 위도(degree)
        double XO = 210 / GRID; // 기준점 x좌표(GRID)
        double YO = 675 / GRID; // 기준점 y좌표(GRID)

        double DEGRAD = Math.PI / 180.0;
        double RADDEG = 180.0 / Math.PI;

        double re = RE / GRID;
        double slat1 = SLAT1 * DEGRAD;
        double slat2 = SLAT2 * DEGRAD;
        double olon = OLON * DEGRAD;
        double olat = OLAT * DEGRAD;

        double sn = Math.tan(Math.PI * 0.25 + slat2 * 0.5) / Math.tan(Math.PI * 0.25 + slat1 * 0.5);
        sn = Math.log(Math.cos(slat1) / Math.cos(slat2)) / Math.log(sn);
        double sf = Math.tan(Math.PI * 0.25 + slat1 * 0.5);
        sf = Math.pow(sf, sn) * Math.cos(slat1) / sn;
        double ro = Math.tan(Math.PI * 0.25 + olat * 0.5);
        ro = re * sf / Math.pow(ro, sn);

        double ra = Math.tan(Math.PI * 0.25 + _latitude * DEGRAD * 0.5);
        ra = re * sf / Math.pow(ra, sn);
        double theta = _longitude * DEGRAD - olon;
        if (theta > Math.PI) theta -= 2.0 * Math.PI;
        if (theta < -Math.PI) theta += 2.0 * Math.PI;
        theta *= sn;

        latitude = _latitude;
        longitude = _longitude;

        X = (Math.floor(ra * Math.sin(theta)) + XO + 0.5);
        Y = (Math.floor(ro - ra * Math.cos(theta)) + YO + 0.5);
    }

    private static final String baseUrl = "http://apis.data.go.kr/1360000/VilageFcstInfoService_2.0/getVilageFcst";

    // decoding serviceKey
    private static final String serviceKey = "8%2BvD0jzwYbBYETK0fbOyrcpDWGeiRmi%2Bgbm0Jxx80E1iLbqMTY4VvRehBKd5o7yxblIFN%2BbSfnowRL3fZCOofA%3D%3D";

    private static final String[] baseTimeList = {"02", "05", "08", "11", "14", "17", "20", "23"};

    private static final String dataType = "JSON";
    private static String baseDate = Utility.getDate("yyyyMMdd");
    private static String baseTime = ""; // 3시간 간격
    private static String numOfRows = "36"; // 1시간에 12개의 카테고리, 3시간의 양으로 36개

    static Weather weather = new Weather();

    public static String getRequest(double latitude, double longitude) throws UnsupportedEncodingException {
        // 경도와 위도를 격자 좌표로 변환하기
        getGridCoordinate(latitude, longitude);

        // 현재 시간에 따라 baseTime 변경하기
        setBaseDate();

        return baseUrl +
                "?" + URLEncoder.encode("ServiceKey", "UTF-8") + "=" + serviceKey +
                "&" + URLEncoder.encode("nx", "UTF-8") + "=" + URLEncoder.encode(String.format("%.0f", X), "UTF-8") +
                "&" + URLEncoder.encode("ny", "UTF-8") + "=" + URLEncoder.encode(String.format("%.0f", Y), "UTF-8") +
                "&" + URLEncoder.encode("numOfRows", "UTF-8") + "=" + URLEncoder.encode(numOfRows, "UTF-8") +
                "&" + URLEncoder.encode("dataType", "UTF-8") + "=" + URLEncoder.encode(dataType, "UTF-8") +
                "&" + URLEncoder.encode("base_date", "UTF-8") + "=" + URLEncoder.encode(baseDate, "UTF-8") +
                "&" + URLEncoder.encode("base_time", "UTF-8") + "=" + URLEncoder.encode(baseTime, "UTF-8");
    }

    public static void getWeather(double latitude, double longitude, OnCompleteCallback<Weather, ConnError> callback) {
        new Thread(() -> {
            JSONObject response = null;
            BufferedReader br;

            try {
                URL url = new URL(getRequest(latitude, longitude));

                HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                conn.setRequestMethod("GET");
                conn.setRequestProperty("Content-type", "application/json");
                conn.setRequestProperty("Accept", "application/json");

//                // 출력(POST)
                // get 방식일 경우에 설정해두면 영향이 있음(하지 않기)
//                conn.setDoOutput(true);
                // 입력(default true)
//                conn.setDoInput(true);
                int responseCode = conn.getResponseCode();

                Log.d("weatherUtility", "getResponse");

                if (responseCode == 400 || responseCode == 401 || responseCode == 500) { // 기상 정보를 가져오지 못했을 때
                    ConnError error = new ConnError(responseCode, conn.getResponseMessage());
                    callback.onComplete(false, null, error);
                } else { // 기상 정보를 성공적으로 가져왔을 때
                    // 스트림에서 결과값 String으로 변환
                    br = new BufferedReader(new InputStreamReader(conn.getInputStream()));
                    Log.d("weatherUtility", "br");

                    StringBuilder sb = new StringBuilder();
                    String line = "";
                    while ((line = br.readLine()) != null) {
                        sb.append(line); // 스트림에서 결과값 String으로 변환
                    }

                    Log.d("weatherUtility", "sb & res");
                    // 결과값 String을 json 객체로 변환
                    response = new JSONObject(sb.toString());
                    JSONObject items = response.getJSONObject("response").getJSONObject("body").getJSONObject("items");
                    JSONArray itemArray = items.getJSONArray("item");

                    weather.setLatitude(latitude);
                    weather.setLongitude(longitude);

                    String currentTime = Utility.getTime_kk() + "00";
                    for (int i = 0; i < itemArray.length(); i++) {
                        Log.d("weatherUtility", i + "");
                        JSONObject item = new JSONObject(itemArray.get(i).toString());

                        if (item.get("fcstTime").toString().equals(currentTime)) { // 12번
                            if (weather.getFcstDate() == null) {
                                weather.setFcstDate(item.get("fcstDate").toString());
                            }

                            if (weather.getFcstTime() == null) {
                                weather.setFcstTime(item.get("fcstTime").toString());
                            }

                            // 필요한 기상 정보 관련 필드 값 가져오기
                            setWeatherInstance(item.get("category").toString(), item.get("fcstValue").toString());
                        }
                    }

                    callback.onComplete(true, weather, null);
                    br.close();
                }

                conn.disconnect();
            } catch (IOException e) {
                e.printStackTrace();
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }).start();
    }

    /**
     * 기상 정보를 가져오는데 필요한 baseTime을 설정하는 메소드
     * 현재 시간을 구해서 조건 중 현재 시간이 속하는 baseTime을 찾아 설정해준다.
     * 한 번에 많이 가져와서 저장하는 것은ㅇ 효율적이지 못하기 때문에 baseTime이 기본적으로 3시간 차이로 정해져있기 때문에
     * 그 기준을 따라서 현재 시간에 따른 baseTime을 지정한다.
     */
    public static void setBaseDate() {
        int currentTime = Integer.parseInt(Utility.getTime_kk()); // 현재 시간(시)

        if (currentTime > 2 && currentTime <= 5) {
            baseTime = "0200";
        } else if (currentTime > 5 && currentTime <= 8) {
            baseTime = "0500";
        } else if (currentTime > 8 && currentTime <= 11) {
            baseTime = "0800";
        } else if (currentTime > 11 && currentTime <= 14) {
            baseTime = "1100";
        } else if (currentTime > 14 && currentTime <= 17) {
            baseTime = "1400";
        } else if (currentTime > 17 && currentTime <= 20) {
            baseTime = "1700";
        } else if (currentTime > 20 && currentTime <= 23) {
            baseTime = "2000";
        } else {
            baseTime = "2300";
        }
    }


    /**
     * 기상 정보를 가져오는데 성공하면 weather 객체에 필요한 값들을 넣어준다.
     * 카테고리 별로 값이 다르기 때문에 카테고리를 구분하여 값을 저장한다.
     *
     * @param category response 값 중 필요한 필드 이름
     * @param value category 값
     */
    public static void setWeatherInstance(String category, String value) {
        switch (category) {
            case "PTY": // 강수 형태
                weather.setPTY(value);
                break;

            case "POP": // 강수 확률
                weather.setPOP(value);
                break;

            case "SKY": // 하늘 상태
                weather.setSKY(value);
                break;

            case "TMN": // 일 최저 기온(단기에서 내려오지 않음)
                weather.setTMN(value);
                break;

            case "TMX": // 일 최고 기온(단기에서 내려오지 않음)
                weather.setTMX(value);
                break;
        }
    }

    public static String transPTYToWeather(String code) {
        switch (code) {
            case "1": // 비
                return "비";

            case "2": // 비/눈
                return "비 또는 눈";

            case "3": // 눈
                return "눈";

            case "4": // 소나기
                return "소나기";

            default:
                return "";
        }
    }
}
