package com.jjh.parkinseoul.vo.response;

import com.google.gson.annotations.SerializedName;
import com.jjh.parkinseoul.vo.ParkVO;
import com.jjh.parkinseoul.vo.SubwayVO;

import java.util.List;

/**
 * Created by JJH on 2016-08-21.
 * 주변 지하철 Response
 */
public class NearSubwayResponse {
    @SerializedName("stationList")
    private List<SubwayVO> subwayVoList;

    public List<SubwayVO> getSubwayVoList() {
        return subwayVoList;
    }

    public void setSubwayVoList(List<SubwayVO> subwayVoList) {
        this.subwayVoList = subwayVoList;
    }
}
