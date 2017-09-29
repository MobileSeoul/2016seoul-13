package com.jjh.parkinseoul.vo.response;

import com.google.gson.annotations.SerializedName;
import com.jjh.parkinseoul.vo.ParkImageVO;

import org.simpleframework.xml.Attribute;
import org.simpleframework.xml.Element;
import org.simpleframework.xml.ElementList;
import org.simpleframework.xml.Root;

import java.util.ArrayList;

/**
 * Created by JJH on 2016-08-28.
 */
@Root(name="rss")
public class ParkImageResponse {
    @Attribute(name="version")
    private String version;
    @Element(name="channel")
    private ParkImageResponseData parkImageResponseData;

    public String getVersion() {
        return version;
    }

    public void setVersion(String version) {
        this.version = version;
    }

    public ParkImageResponseData getParkImageResponseData() {
        return parkImageResponseData;
    }

    public void setParkImageResponseData(ParkImageResponseData parkImageResponseData) {
        this.parkImageResponseData = parkImageResponseData;
    }
}
