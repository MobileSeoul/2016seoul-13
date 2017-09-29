package com.jjh.parkinseoul.utils;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.location.GpsStatus;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.provider.Settings;
import android.widget.Toast;

import cn.pedant.sweetalert.SweetAlertDialog;

public class LocationUtil{
  
    private final Context mContext;
  
    // 현재 GPS 사용유무
    boolean isGPSEnabled = false;
  
    // 네트워크 사용유무 
    boolean isNetworkEnabled = false;

    Location location; 
    double lat; // 위도 
    double lon; // 경도
  
    // 최소 GPS 정보 업데이트 거리 10미터 
    private static final long MIN_DISTANCE_CHANGE_FOR_UPDATES = 10; 
  
    // 최소 GPS 정보 업데이트 시간 3초
    private static final long MIN_TIME_BW_UPDATES = 1000 * 3; 
  
    protected LocationManager locationManager;
  
    private LocationListener locationListener;
    
    public LocationUtil(Context context, LocationListener locationListener) {
        this.mContext = context;
        this.locationListener = locationListener;
        init();
    }

    private void init(){
        try {
            locationManager = (LocationManager) mContext
                    .getSystemService(Service.LOCATION_SERVICE);

            // GPS 정보 가져오기
            isGPSEnabled = locationManager
                    .isProviderEnabled(LocationManager.GPS_PROVIDER);

            // 현재 네트워크 상태 값 알아오기
            isNetworkEnabled = locationManager
                    .isProviderEnabled(LocationManager.NETWORK_PROVIDER);

            if (isGPSEnabled) {
                initLocation();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void initLocation(){
        // 네트워크 정보로 부터 위치값 가져오기
        if(!isNetworkEnabled && !isGPSEnabled){
            init();
        }

        if (isNetworkEnabled) {
            locationManager.requestLocationUpdates(
                    LocationManager.NETWORK_PROVIDER,
                    MIN_TIME_BW_UPDATES,
                    MIN_DISTANCE_CHANGE_FOR_UPDATES, locationListener);

            if (locationManager != null) {
                location = locationManager
                        .getLastKnownLocation(LocationManager.NETWORK_PROVIDER);
                if (location != null) {
                    // 위도 경도 저장
                    lat = location.getLatitude();
                    lon = location.getLongitude();
                }
            }
        }

        if (isGPSEnabled) {
            if (location == null) {
                locationManager.requestLocationUpdates(
                        LocationManager.GPS_PROVIDER,
                        MIN_TIME_BW_UPDATES,
                        MIN_DISTANCE_CHANGE_FOR_UPDATES, locationListener);
                if (locationManager != null) {
                    location = locationManager
                            .getLastKnownLocation(LocationManager.GPS_PROVIDER);
                    if (location != null) {
                        lat = location.getLatitude();
                        lon = location.getLongitude();
                    }
                }
            }
        }
    }

    public boolean isGPSEnabled(){
        return this.isGPSEnabled;
    }

    public void addGpsStatusListener(GpsStatus.Listener listener){
        locationManager.addGpsStatusListener(listener);
    }
      
    /**
     * 위도값을 가져옵니다. 
     * */
    public double getLatitude(){
        if(location != null){
            lat = location.getLatitude();
        }
        return lat;
    }
      
    /**
     * 경도값을 가져옵니다. 
     * */
    public double getLongitude(){
        if(location != null){
            lon = location.getLongitude();
        }
        return lon;
    }

    public void setLocation(Location location){
        if (location != null) {
            // 위도 경도 저장
            this.location = location;
            lat = location.getLatitude();
            lon = location.getLongitude();
        }
    }
    
      

    public void stopLocationListening(){
    	locationManager.removeUpdates(locationListener);
    }
}