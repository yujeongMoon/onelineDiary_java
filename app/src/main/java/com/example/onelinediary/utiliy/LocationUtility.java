package com.example.onelinediary.utiliy;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.provider.Settings;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;

import com.example.onelinediary.constant.Const;

import java.io.IOException;
import java.util.List;

public class LocationUtility {
    public static LocationManager locationManager;

    public static boolean checkPermission(Context context) {
        return ActivityCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED &&
                ActivityCompat.checkSelfPermission(context, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED;
    }

    public static LocationManager getLocationManager(Context context) {
        return (LocationManager) context.getSystemService(Context.LOCATION_SERVICE);
    }

    public static boolean isGPSEnabled(Context context) {
        return getLocationManager(context).isProviderEnabled(LocationManager.GPS_PROVIDER);
    }

    public static boolean isNetworkEnable(Context context) {
        return getLocationManager(context).isProviderEnabled(LocationManager.NETWORK_PROVIDER);
    }

    public static String getProvider(Context context) {
        if (isGPSEnabled(context)) {
            return LocationManager.GPS_PROVIDER;
        } else {
            return LocationManager.NETWORK_PROVIDER;
        }
    }

    public static Location getCurrentLocation() {
        return Const.currentLocation;
    }

    // 사용자에게 GPS를 활성화할 수 있도록 가이드하는 메소드
    private static void enableLocationSettings(Context context) {
        Intent settingsIntent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
        context.startActivity(settingsIntent);
    }

    public static Location getLastLocation(Context context) {

        if (checkPermission(context)) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return null; // 위치 정보 권한 필요
        }

        locationManager = getLocationManager(context);
        // 오래된 위치의 정보가 리턴될 수도 있고 provider에 대한 저장된 위치 정보가 없을 때는 null이 리턴될 수도 있다.
        Location location = null;

        if (isGPSEnabled(context)) { // GPS 활성화됨
            location = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
        } else { // GPS 활성화 안됨
            enableLocationSettings(context);
        }

        if (location == null) {
            location = locationManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);
        }

        return location;
    }

    public static Location getLastKnownLocation(Context context) {
        if (checkPermission(context)) {
            return null; // 위치 정보 권한 필요
        }

        locationManager = getLocationManager(context);
        List<String> providers = getLocationManager(context).getProviders(true);
        Location bestLocation = null;

        for (String provider : providers) {
            Location l = locationManager.getLastKnownLocation(provider);

            if (l == null)
                continue;

            if (bestLocation == null || l.getAccuracy() < bestLocation.getAccuracy()) {
                bestLocation = l;
            }
        }

        if (bestLocation == null) {
            bestLocation = locationManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);
        }

        return bestLocation;
    }

    public static void requestLocationUpdate(Context context, LocationListener listener) {
        if (checkPermission(context)) {
            return;
        }

        if (isGPSEnabled(context)) { // GPS 활성화됨
            getLocationManager(context).requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 10000, 10.0f, listener);
        } else { // GPS 활성화 안됨
            enableLocationSettings(context);
        }
    }

    public static String getAddress(double latitude, double longitude, Context context) {
        Geocoder geocoder = new Geocoder(context);
        List<Address> addressList = null;
        try {
            addressList = geocoder.getFromLocation(latitude, longitude ,10);
        } catch (IOException e) {
            e.printStackTrace();
        }

        if (addressList != null && addressList.size() != 0) {
           return addressList.get(0).getAddressLine(0);
        } else {
            // 주소 찾기 오류
            return "";
        }
    }
}
