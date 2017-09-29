package com.jjh.parkinseoul.vo;

import com.j256.ormlite.field.DataType;
import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;

import java.io.Serializable;

/**
 * Created by JJH on 2016-08-21.
 */
@DatabaseTable(tableName = "park")
public class ParkVO implements Serializable{
    @DatabaseField(generatedId = true)
    private int p_idx;
    @DatabaseField private String p_park;
    @DatabaseField private String p_list_content;
    @DatabaseField private String p_addr;
    @DatabaseField private String p_zone;
    @DatabaseField private String p_division;
    @DatabaseField(dataType= DataType.BYTE_ARRAY) private byte[] p_img;
    @DatabaseField private String p_admintel;
    @DatabaseField private String longitude;
    @DatabaseField private String latitude;
    @DatabaseField private String g_longitude;
    @DatabaseField private String g_latitude;
    @DatabaseField private String p_size;
    @DatabaseField private String p_open;
    @DatabaseField private String p_facilities;
    @DatabaseField private String p_plants;
    @DatabaseField(dataType= DataType.BYTE_ARRAY) private byte[] p_locationmap;
    @DatabaseField private String p_locationmap_url;
    @DatabaseField private String p_homepage;
    @DatabaseField private String p_near_subway;
    @DatabaseField private String p_near_bus;

    public int getP_idx() {
        return p_idx;
    }

    public void setP_idx(int p_idx) {
        this.p_idx = p_idx;
    }

    public String getP_park() {
        return p_park;
    }

    public void setP_park(String p_park) {
        this.p_park = p_park;
    }

    public String getP_list_content() {
        return p_list_content;
    }

    public void setP_list_content(String p_list_content) {
        this.p_list_content = p_list_content;
    }

    public String getP_addr() {
        return p_addr;
    }

    public void setP_addr(String p_addr) {
        this.p_addr = p_addr;
    }

    public String getP_zone() {
        return p_zone;
    }

    public void setP_zone(String p_zone) {
        this.p_zone = p_zone;
    }

    public String getP_division() {
        return p_division;
    }

    public void setP_division(String p_division) {
        this.p_division = p_division;
    }

    public byte[] getP_img() {
        return p_img;
    }

    public void setP_img(byte[] p_img) {
        this.p_img = p_img;
    }

    public String getP_admintel() {
        return p_admintel;
    }

    public void setP_admintel(String p_admintel) {
        this.p_admintel = p_admintel;
    }

    public String getLongitude() {
        return longitude;
    }

    public void setLongitude(String longitude) {
        this.longitude = longitude;
    }

    public String getLatitude() {
        return latitude;
    }

    public void setLatitude(String latitude) {
        this.latitude = latitude;
    }

    public String getG_longitude() {
        return g_longitude;
    }

    public void setG_longitude(String g_longitude) {
        this.g_longitude = g_longitude;
    }

    public String getG_latitude() {
        return g_latitude;
    }

    public void setG_latitude(String g_latitude) {
        this.g_latitude = g_latitude;
    }

    public String getP_size() {
        return p_size;
    }

    public void setP_size(String p_size) {
        this.p_size = p_size;
    }

    public String getP_open() {
        return p_open;
    }

    public void setP_open(String p_open) {
        this.p_open = p_open;
    }

    public String getP_facilities() {
        return p_facilities;
    }

    public void setP_facilities(String p_facilities) {
        this.p_facilities = p_facilities;
    }

    public String getP_plants() {
        return p_plants;
    }

    public void setP_plants(String p_plants) {
        this.p_plants = p_plants;
    }

    public byte[] getP_locationmap() {
        return p_locationmap;
    }

    public void setP_locationmap(byte[] p_locationmap) {
        this.p_locationmap = p_locationmap;
    }

    public String getP_locationmap_url() {
        return p_locationmap_url;
    }

    public void setP_locationmap_url(String p_locationmap_url) {
        this.p_locationmap_url = p_locationmap_url;
    }

    public String getP_homepage() {
        return p_homepage;
    }

    public void setP_homepage(String p_homepage) {
        this.p_homepage = p_homepage;
    }

    public String getP_near_subway() {
        return p_near_subway;
    }

    public void setP_near_subway(String p_near_subway) {
        this.p_near_subway = p_near_subway;
    }

    public String getP_near_bus() {
        return p_near_bus;
    }

    public void setP_near_bus(String p_near_bus) {
        this.p_near_bus = p_near_bus;
    }
}
