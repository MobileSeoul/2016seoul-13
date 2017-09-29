package com.jjh.parkinseoul.service;

import com.jjh.parkinseoul.vo.response.ParkByNameResponse;
import com.jjh.parkinseoul.vo.response.ParkResponse;
import com.jjh.parkinseoul.vo.response.ProgramByNameResponse;
import com.jjh.parkinseoul.vo.response.ProgramByParkResponse;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Path;

/**
 * Created by JJH on 2016-08-21.
 * 프로그램 정보 조회 API
 */
public interface ProgramAPIService {

    /**
     * 프로그램명으로 프로그램 조회
     * @param programName 프로그램명
     * @return
     */
    @GET("SearchParkProgramByNameService/1/300/{programname}")
    public Call<ProgramByNameResponse> getProgramListByName(@Path("programname") String programName);

    /**
     * 공원명으로 프로그램 조회
     * @param parkNum 공원번호
     * @return
     */
    @GET("SearchProgramsByParkIDService/1/300/{parknum}")
    public Call<ProgramByParkResponse> getProgramListByPark(@Path("parknum") int parkNum);

}
