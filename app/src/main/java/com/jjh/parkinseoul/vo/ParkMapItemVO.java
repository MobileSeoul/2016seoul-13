package com.jjh.parkinseoul.vo;

import android.location.Location;

import com.google.android.gms.maps.model.LatLng;
import com.google.maps.android.clustering.ClusterItem;
import com.j256.ormlite.table.DatabaseTable;

/**
 * Created by JJH on 2016-09-18.
 */
@DatabaseTable(tableName = "park")
public class ParkMapItemVO extends ParkVO implements ClusterItem{

    private double distance;

    @Override
    public LatLng getPosition() {
        return new LatLng(Double.parseDouble(getLatitude()), Double.parseDouble(getLongitude()));
    }

    public void setDistance(double distance){
        this.distance = distance;
    }

    public double getDistance(){
        return this.distance;
    }
}
