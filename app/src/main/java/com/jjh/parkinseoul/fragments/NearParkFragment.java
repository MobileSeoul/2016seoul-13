package com.jjh.parkinseoul.fragments;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.location.GpsStatus;
import android.location.Location;
import android.location.LocationListener;
import android.os.Bundle;
import android.os.Handler;
import android.provider.Settings;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.MapsInitializer;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.maps.android.clustering.Cluster;
import com.google.maps.android.clustering.ClusterItem;
import com.google.maps.android.clustering.ClusterManager;
import com.google.maps.android.clustering.view.DefaultClusterRenderer;
import com.google.maps.android.ui.IconGenerator;
import com.jjh.parkinseoul.MenuActivity;
import com.jjh.parkinseoul.R;
import com.jjh.parkinseoul.utils.CommonUtil;
import com.jjh.parkinseoul.utils.LocationUtil;
import com.jjh.parkinseoul.vo.ParkMapItemVO;
import com.oguzdev.circularfloatingactionmenu.library.FloatingActionMenu;

import java.util.List;

import cn.pedant.sweetalert.SweetAlertDialog;

/**
 * 가까운 공원 Fragment
 */
public class NearParkFragment extends Fragment implements OnMapReadyCallback, ClusterManager.OnClusterClickListener<ParkMapItemVO>, ClusterManager.OnClusterItemClickListener<ParkMapItemVO> {

    private final LatLng LOCATION_SEOUL = new LatLng( 37.56, 126.97); //default 위치

    private View fragmentView;

    private FloatingActionMenu settingsMenu;

    private MapView mapView;

    private GoogleMap googleMap;
    private LocationUtil locationUtil;
    private ClusterManager<ParkMapItemVO> mClusterManager;

    private List<ParkMapItemVO> parkMapItemList;

    private boolean isGPSOnNow = false;

    private int targetDistance = 5;
    private boolean isShowAll = false;

    private TextView tvSettings1Km;
    private TextView tvSettings3Km;
    private TextView tvSettings5Km;
    private TextView tvSettingsAll;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        fragmentView = inflater.inflate(R.layout.fragment_near_park, container, false);
        mapView = (MapView)fragmentView.findViewById(R.id.mapView);
        mapView.onCreate(savedInstanceState);
        mapView.onResume();

        try {
            MapsInitializer.initialize(getActivity().getApplicationContext());
        }catch (Exception e){
            e.printStackTrace();
        }
        mapView.getMapAsync(this);

        initCircleMenu();

        return fragmentView;
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        ((MenuActivity)getActivity()).setActivityTitle("내 주변 공원");
    }

    @Override
    public void onDetach() {
        //게시판 이미지 첨부 Circle메뉴 닫기
        if(settingsMenu != null && settingsMenu.isOpen()){
            settingsMenu.close(true);
        }
        parkMapItemList = null;
        googleMap.clear();
        mapView.destroyDrawingCache();

        super.onDetach();
    }

    @Override
    public void onResume() {
        super.onResume();
        if(isGPSOnNow){
            Toast.makeText(getActivity().getApplicationContext(), "GPS가 활성화 되었습니다. 현재위치를 찾습니다.", Toast.LENGTH_SHORT).show();
            locationUtil.initLocation();
            //initLocation(targetDistance);
        }
    }

    @Override
    public void onDestroy() {
        if(locationUtil != null) {
            locationUtil.stopLocationListening();
        }
        super.onDestroy();
    }

    private void initCircleMenu(){
        View flAttachPhoto = fragmentView.findViewById(R.id.flSettingButton);

        final int textColorSelected = Color.parseColor("#555555");

        tvSettings1Km = new TextView(getActivity());
        tvSettings3Km = new TextView(getActivity());
        tvSettings5Km = new TextView(getActivity());
        tvSettingsAll = new TextView(getActivity());

        tvSettings1Km.setText("1 Km");
        tvSettings3Km.setText("3 Km");
        tvSettings5Km.setText("5 Km");
        tvSettingsAll.setText("전체");

        tvSettings1Km.setTextColor(Color.WHITE);
        tvSettings3Km.setTextColor(Color.WHITE);
        tvSettings5Km.setTextColor(textColorSelected);
        tvSettingsAll.setTextColor(Color.WHITE);

        tvSettings1Km.setBackgroundResource(R.drawable.bg_settings_button_blue);
        tvSettings3Km.setBackgroundResource(R.drawable.bg_settings_button_blue);
        tvSettings5Km.setBackgroundResource(R.drawable.bg_settings_button_yellow);
        tvSettingsAll.setBackgroundResource(R.drawable.bg_settings_button_blue);

        tvSettings1Km.setGravity(Gravity.CENTER);
        tvSettings3Km.setGravity(Gravity.CENTER);
        tvSettings5Km.setGravity(Gravity.CENTER);
        tvSettingsAll.setGravity(Gravity.CENTER);


        FrameLayout.LayoutParams params = new FrameLayout.LayoutParams(getResources().getDimensionPixelSize(R.dimen.near_park_settings_size),getResources().getDimensionPixelSize(R.dimen.near_park_settings_size));

        tvSettings1Km.setLayoutParams(params);
        tvSettings3Km.setLayoutParams(params);
        tvSettings5Km.setLayoutParams(params);
        tvSettingsAll.setLayoutParams(params);

        tvSettings1Km.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                settingsMenu.close(true);
                clearSettingsButtons();
                ((TextView)v).setTextColor(textColorSelected);
                v.setBackgroundResource(R.drawable.bg_settings_button_yellow);
                initLocation(1);
            }
        });

        tvSettings3Km.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                settingsMenu.close(true);
                clearSettingsButtons();
                ((TextView)v).setTextColor(textColorSelected);
                v.setBackgroundResource(R.drawable.bg_settings_button_yellow);
                initLocation(3);
            }
        });

        tvSettings5Km.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                settingsMenu.close(true);
                clearSettingsButtons();
                ((TextView)v).setTextColor(textColorSelected);
                v.setBackgroundResource(R.drawable.bg_settings_button_yellow);
                initLocation(5);
            }
        });

        tvSettingsAll.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                settingsMenu.close(true);
                clearSettingsButtons();
                ((TextView)v).setTextColor(textColorSelected);
                v.setBackgroundResource(R.drawable.bg_settings_button_yellow);
                initLocation(-1);
            }
        });

        settingsMenu = new FloatingActionMenu.Builder(getActivity())
                .addSubActionView(tvSettings1Km)
                .addSubActionView(tvSettings3Km)
                .addSubActionView(tvSettings5Km)
                .addSubActionView(tvSettingsAll)
                .setStartAngle(-90)
                .setEndAngle(-190)
                .setRadius(getResources().getDimensionPixelSize(R.dimen.radius_large))
                .attachTo(flAttachPhoto)
                .build();
    }

    private void clearSettingsButtons(){
        tvSettings1Km.setTextColor(Color.WHITE);
        tvSettings3Km.setTextColor(Color.WHITE);
        tvSettings5Km.setTextColor(Color.WHITE);
        tvSettingsAll.setTextColor(Color.WHITE);
        tvSettings1Km.setBackgroundResource(R.drawable.bg_settings_button_blue);
        tvSettings3Km.setBackgroundResource(R.drawable.bg_settings_button_blue);
        tvSettings5Km.setBackgroundResource(R.drawable.bg_settings_button_blue);
        tvSettingsAll.setBackgroundResource(R.drawable.bg_settings_button_blue);
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {

        this.googleMap = googleMap;
        //googleMap.setOnMarkerClickListener(this);
        //googleMap.setOnMapClickListener(this);
        mClusterManager = new ClusterManager<ParkMapItemVO>(getActivity(), googleMap);
        mClusterManager.setRenderer(new NearParkRenderer());
        mClusterManager.setOnClusterClickListener(this);
        mClusterManager.setOnClusterItemClickListener(this);

        googleMap.setMyLocationEnabled(true);
        googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom( LOCATION_SEOUL, 11f));
        //googleMap.setInfoWindowAdapter(mClusterManager.getMarkerManager());

        googleMap.setOnCameraIdleListener(mClusterManager);
        googleMap.setOnMarkerClickListener(mClusterManager);
        googleMap.setOnInfoWindowClickListener(mClusterManager);
        googleMap.setOnMapClickListener(new GoogleMap.OnMapClickListener() {
            @Override
            public void onMapClick(LatLng latLng) {
                fragmentView.findViewById(R.id.llInfoWindow).setVisibility(View.GONE);
            }
        });
        locationUtil = new LocationUtil(getActivity(),mLocationListener);
        initLocation(5);

    }

    private void initLocation(int distance){
        fragmentView.findViewById(R.id.llInfoWindow).setVisibility(View.GONE);
        if(distance > 0){
            isShowAll = false;
            targetDistance = distance;
            if(locationUtil.isGPSEnabled()){
                locationUtil.initLocation();
                refreshCurrLocation(false);
            }else{
                showSettingsAlert();
            }
        }else{
            isShowAll = true;
            Toast.makeText(getActivity(), "전체 공원을 조회 합니다.",Toast.LENGTH_SHORT).show();
            googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom( LOCATION_SEOUL, 11f));
            refreshCurrLocation(true);
        }

    }

    /**
     * GPS 정보를 가져오지 못했을때
     * 설정값으로 갈지 물어보는 alert 창
     * */
    public void showSettingsAlert(){

        new SweetAlertDialog(getActivity(), SweetAlertDialog.WARNING_TYPE)
                .setTitleText("GPS가 켜져있지 않습니다.")
                .setContentText("GPS를 설정 하시겠습니까? \n취소를 선택하는 경우 모든 공원이 조회 됩니다.")
                .setConfirmText("설정")
                .setCancelText("취소")
                .setConfirmClickListener(new SweetAlertDialog.OnSweetClickListener() {
                    @Override
                    public void onClick(SweetAlertDialog sDialog) {
                        locationUtil.addGpsStatusListener(new GpsStatus.Listener(){
                            public void onGpsStatusChanged(int event){
                                switch(event){
                                    case GpsStatus.GPS_EVENT_STARTED:
                                        isGPSOnNow = true;
                                        break;
                                    case GpsStatus.GPS_EVENT_STOPPED:
                                        break;
                                }
                            }
                        });
                        Intent intent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                        startActivity(intent);
                        sDialog.cancel();
                    }
                })
                .setCancelClickListener(new SweetAlertDialog.OnSweetClickListener(){
                    @Override
                    public void onClick(SweetAlertDialog sDialog) {
                        sDialog.cancel();
                        tvSettingsAll.performClick();
                    }

                }).show();
    }

    private void refreshCurrLocation(boolean isShowAll){
        mClusterManager.clearItems();
        LatLng currLatLng = new LatLng( locationUtil.getLatitude(), locationUtil.getLongitude());
        if(!isShowAll) {
            googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(currLatLng, 13f));
        }
        if(parkMapItemList == null) {
            parkMapItemList = ((MenuActivity)getActivity()).getDatabaseHelper().selectParkMapItemList();
        }

        int parkCount = 0;
        for(ParkMapItemVO vo : parkMapItemList){
            Location currLocation = new Location("curr");
            currLocation.setLatitude(locationUtil.getLatitude());
            currLocation.setLongitude(locationUtil.getLongitude());

            double parkLatitude = Double.parseDouble(vo.getLatitude());
            double parkLongtitude = Double.parseDouble(vo.getLongitude());

            Location parkLocation = new Location(vo.getP_addr());
            parkLocation.setLatitude(parkLatitude);
            parkLocation.setLongitude(parkLongtitude);

            double distance = currLocation.distanceTo(parkLocation);
            vo.setDistance(isShowAll ? -1 : distance);
            if(isShowAll || distance <= targetDistance * 1000) { //5KM 이내만
                parkCount++;
                mClusterManager.addItem(vo);
            }
        }

        mClusterManager.cluster();

        if(!isShowAll && getActivity() != null){
            if (parkCount > 0) {
                Toast.makeText(getActivity(), "반경 " + targetDistance + "Km 이내에 " + parkCount + "개의 공원이 있습니다.", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(getActivity(), "반경 " + targetDistance + "Km 이내에 공원이 없습니다.", Toast.LENGTH_SHORT).show();
            }
        }


    }

    private LocationListener mLocationListener = new LocationListener() {
        @Override
        public void onStatusChanged(String provider, int status, Bundle extras) {}
        @Override
        public void onProviderEnabled(String provider) {}
        @Override
        public void onProviderDisabled(String provider) {}
        @Override
        public void onLocationChanged(Location location) {
            double latitude = location.getLatitude();
            double longitude = location.getLongitude();
            if(latitude != 0 && longitude != 0 && locationUtil.getLatitude() != latitude && locationUtil.getLongitude() != longitude){
                locationUtil.setLocation(location);
                if(!isShowAll) {
                    refreshCurrLocation(false);
                    new Handler().postDelayed(new Runnable() {
                        @Override
                        public void run() {

                        }
                    },300);

                }
            }
        }
    };

    // View를 Bitmap으로 변환
    private Bitmap createDrawableFromView(Context context, View view) {

        DisplayMetrics displayMetrics = new DisplayMetrics();
        getActivity().getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
        view.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT));
        view.measure(displayMetrics.widthPixels, displayMetrics.heightPixels);
        view.layout(0, 0, displayMetrics.widthPixels, displayMetrics.heightPixels);
        view.buildDrawingCache();
        Bitmap bitmap = Bitmap.createBitmap(view.getMeasuredWidth(), view.getMeasuredHeight(), Bitmap.Config.ARGB_8888);

        Canvas canvas = new Canvas(bitmap);
        view.draw(canvas);

        return bitmap;
    }

    private class NearParkRenderer extends DefaultClusterRenderer<ParkMapItemVO> {
        private final View markerView;
        private final IconGenerator mClusterIconGenerator = new IconGenerator(getActivity());
        public NearParkRenderer() {
            super(getActivity().getApplicationContext(), googleMap, mClusterManager);
            markerView = getActivity().getLayoutInflater().inflate(R.layout.layout_near_park_marker, null);
        }

        @Override
        protected int getColor(int clusterSize) {
            return Color.parseColor("#2F9D27");
            //return super.getColor(clusterSize);
        }

        @Override
        protected void onBeforeClusterItemRendered(ParkMapItemVO vo, MarkerOptions markerOptions) {
            // Draw a single person.
            // Set the info window to show their name.
            TextView tvMarker = (TextView)markerView.findViewById(R.id.tvMarker);
            tvMarker.setText(vo.getP_park());
            tvMarker.setBackgroundResource(R.drawable.bg_marker);
            tvMarker.setTextColor(Color.BLACK);

            markerOptions.icon(BitmapDescriptorFactory.fromBitmap(createDrawableFromView(getActivity(), markerView)));
        }

       /* @Override
        protected void onBeforeClusterRendered(Cluster<ParkMapItemVO> cluster, MarkerOptions markerOptions) {
            // Draw multiple people.
            // Note: this method runs on the UI thread. Don't spend too much time in here (like in this example).
            final Drawable clusterIcon = getResources().getDrawable(R.drawable.shape_custom_cluster_bg);
            //clusterIcon.setColorFilter(getResources().getColor(android.R.color.holo_orange_light), PorterDuff.Mode.SRC_ATOP);


            //modify padding for one or two digit numbers
            *//*if (cluster.getSize() < 10) {
                mClusterIconGenerator.setContentPadding(40, 20, 0, 0);
            }
            else {
                mClusterIconGenerator.setContentPadding(30, 20, 0, 0);
            }*//*

            mClusterIconGenerator.setColor(Color.WHITE);
            mClusterIconGenerator.setBackground(clusterIcon);



            Bitmap icon = mClusterIconGenerator.makeIcon(String.valueOf(cluster.getSize()));
            markerOptions.icon(BitmapDescriptorFactory.fromBitmap(icon));
//            super.onBeforeClusterRendered(cluster,markerOptions);
        }*/

        @Override
        protected boolean shouldRenderAsCluster(Cluster cluster) {
            // Always render clusters.
            return cluster.getSize() > 1;
        }
    }


    @Override
    public boolean onClusterClick(Cluster<ParkMapItemVO> cluster) {
        // Create the builder to collect all essential cluster items for the bounds.
        LatLngBounds.Builder builder = LatLngBounds.builder();
        for (ClusterItem item : cluster.getItems()) {
            builder.include(item.getPosition());
        }
        // Get the LatLngBounds
        final LatLngBounds bounds = builder.build();

        // Animate camera to the bounds
        try {
            googleMap.animateCamera(CameraUpdateFactory.newLatLngBounds(bounds, 100));
        } catch (Exception e) {
            e.printStackTrace();
        }

        return true;
    }

    @Override
    public boolean onClusterItemClick(final ParkMapItemVO parkMapItemVO) {
        Bitmap bm = BitmapFactory.decodeByteArray(parkMapItemVO.getP_img(), 0, parkMapItemVO.getP_img().length);
        fragmentView.findViewById(R.id.llInfoWindow).setVisibility(View.VISIBLE);
        ((ImageView)fragmentView.findViewById(R.id.ivMarkerWindow)).setImageBitmap(bm);
        ((TextView)fragmentView.findViewById(R.id.tvMarkerWindowTitle)).setText(parkMapItemVO.getP_park());
        ((TextView)fragmentView.findViewById(R.id.tvMarkerWindowAddr)).setText(parkMapItemVO.getP_addr());
        if(parkMapItemVO.getDistance() < 0) {
            fragmentView.findViewById(R.id.tvMarkerWindowDistance).setVisibility(View.GONE);
        }else{
            fragmentView.findViewById(R.id.tvMarkerWindowDistance).setVisibility(View.VISIBLE);
            String distanceStr = "";
            if (parkMapItemVO.getDistance() > 1000) {
                distanceStr += (Math.round(parkMapItemVO.getDistance() / 10.0f) / 100.0f) + " Km";
            } else {
                distanceStr += (Math.round(parkMapItemVO.getDistance() * 100.0f) / 100.0f) + " M";
            }
            ((TextView)fragmentView.findViewById(R.id.tvMarkerWindowDistance)).setText(distanceStr);
        }

        fragmentView.findViewById(R.id.llInfoWindow).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startParkDetailFragment(parkMapItemVO.getP_idx(), parkMapItemVO.getP_park());
            }
        });

        return false;
    }

    /**
     * 공원상세 Fragment 호출
     */
    protected void startParkDetailFragment(int parkNum, String parkName) {
        CommonUtil.hideSoftKeyboard(getActivity());
        FragmentManager fm = getFragmentManager();
        Fragment fragment = new ParkDetailFragment();
        Bundle args = new Bundle();
        args.putInt(ParkDetailFragment.PARAM_PARK_NUM, parkNum); //공원번호
        args.putString(ParkDetailFragment.PARAM_PARK_NAME, parkName); //공원명
        args.putString(ParkDetailFragment.PARAM_PARENT_MENU_TITLE,"내 주변 공원"); //Return시 표시할 상단 Title명
        fragment.setArguments(args);
        getActivity().getSupportFragmentManager().beginTransaction().setCustomAnimations(R.anim.transition_enter_from_right, R.anim.transition_exit_to_right).add(R.id.main_fragment, fragment).addToBackStack(null).commit();
    }
}
