package com.jjh.parkinseoul.vo.response;

import com.google.gson.annotations.SerializedName;
import com.jjh.parkinseoul.vo.ParkVO;
import com.jjh.parkinseoul.vo.ProgramVO;

import java.util.List;

/**
 * Created by JJH on 2016-08-21.
 */
public class ProgramByNameResponse {
    @SerializedName("SearchParkProgramByNameService")
    private ProgramResponseData programResponseData;

    public ProgramResponseData getProgramResponseData() {
        return programResponseData;
    }

    public void setProgramResponseData(ProgramResponseData programResponseData) {
        this.programResponseData = programResponseData;
    }


    public static class ProgramResponseData{
        @SerializedName("list_total_count")
        private int list_total_count;

        @SerializedName("row")
        private List<ProgramVO> programVoList;

        public int getList_total_count() {
            return list_total_count;
        }

        public void setList_total_count(int list_total_count) {
            this.list_total_count = list_total_count;
        }

        public List<ProgramVO> getProgramVOList() {
            return programVoList;
        }

        public void setProgramVoList(List<ProgramVO> programVoList) {
            this.programVoList = programVoList;
        }
    }
}
