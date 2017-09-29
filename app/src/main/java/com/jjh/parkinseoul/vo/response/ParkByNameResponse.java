package com.jjh.parkinseoul.vo.response;

import com.google.gson.annotations.SerializedName;
import com.jjh.parkinseoul.vo.ParkVO;

import java.util.List;

/**
 * Created by JJH on 2016-08-21.
 */
public class ParkByNameResponse {
    @SerializedName("SearchInfoByParkNameService")
    private ParkResponseData parkResponseData;

    public ParkResponseData getParkResponseData() {
        return parkResponseData;
    }

    public void setParkResponseData(ParkResponseData parkResponseData) {
        this.parkResponseData = parkResponseData;
    }


    public static class ParkResponseData{
        @SerializedName("list_total_count")
        private int list_total_count;

        @SerializedName("row")
        private List<ParkVO> parkVoList;

        public int getList_total_count() {
            return list_total_count;
        }

        public void setList_total_count(int list_total_count) {
            this.list_total_count = list_total_count;
        }

        public List<ParkVO> getParkVOList() {
            return parkVoList;
        }

        public void setParkVoList(List<ParkVO> parkVoList) {
            this.parkVoList = parkVoList;
        }
    }
}
