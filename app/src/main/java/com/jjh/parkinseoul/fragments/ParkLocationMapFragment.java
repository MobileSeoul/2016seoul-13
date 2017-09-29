package com.jjh.parkinseoul.fragments;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.MapsInitializer;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.jjh.parkinseoul.MenuActivity;
import com.jjh.parkinseoul.R;
import com.jjh.parkinseoul.utils.DisplayUtil;
import com.jjh.parkinseoul.utils.ImageUtil;
import com.jjh.parkinseoul.vo.ParkVO;

/**
 * 공원 위치 상세 Fragment
 */
public class ParkLocationMapFragment extends Fragment implements OnMapReadyCallback {

    public static final String PARAM_PARK_VO = "param_park_vo";

    private View fragmentView;

    private MapView mapView;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        fragmentView = inflater.inflate(R.layout.fragment_near_park, container, false);
        fragmentView.findViewById(R.id.flSettingButton).setVisibility(View.GONE);
        mapView = (MapView) fragmentView.findViewById(R.id.mapView);
        mapView.onCreate(savedInstanceState);
        mapView.onResume();

        try {
            MapsInitializer.initialize(getActivity().getApplicationContext());
        } catch (Exception e) {
            e.printStackTrace();
        }
        mapView.getMapAsync(this);


        return fragmentView;
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {

        final ParkVO vo = (ParkVO) getArguments().getSerializable(PARAM_PARK_VO);
        LatLng latLng = new LatLng(Double.parseDouble(vo.getLatitude()), Double.parseDouble(vo.getLongitude()));
        CameraPosition cp = new CameraPosition.Builder().target((latLng)).zoom(11f).build();
        googleMap.moveCamera(CameraUpdateFactory.newCameraPosition(cp));
        googleMap.setMyLocationEnabled(true);

        String addr = vo.getP_addr();
        int middle = addr.length() /2;
        addr = addr.substring(0, middle) + "\n" +addr.substring(middle);

        googleMap.setInfoWindowAdapter(new GoogleMap.InfoWindowAdapter() {
            @Override
            public View getInfoWindow(Marker marker) {
                return null;
            }

            @Override
            public View getInfoContents(Marker marker) {
                View view = LayoutInflater.from(getActivity()).inflate(R.layout.layout_marker_window,null);
                Bitmap bm = BitmapFactory.decodeByteArray(vo.getP_img(), 0, vo.getP_img().length);
                ((ImageView)view.findViewById(R.id.ivMarkerWindow)).setImageBitmap(bm);
                ((TextView)view.findViewById(R.id.tvMarkerWindowTitle)).setText(vo.getP_park());
                ((TextView)view.findViewById(R.id.tvMarkerWindowAddr)).setText(vo.getP_addr());
                return view;
            }
        });

        MarkerOptions mo = new MarkerOptions();
        mo.position(latLng);

//                mo.icon(BitmapDescriptorFactory
//                        .fromResource(R.drawable.progress_tree));
        googleMap.addMarker(mo).showInfoWindow();
    }
}
