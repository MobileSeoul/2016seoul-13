package com.jjh.parkinseoul.service;

import com.jjh.parkinseoul.vo.response.ParkByNameResponse;
import com.jjh.parkinseoul.vo.response.ParkResponse;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Path;

/**
 * Created by JJH on 2016-08-21.
 * 공원정보조회 API
 */
public interface ParkAPIService {
    /**
     * 공원명으로 조회
     * @param parkname 공원명
     * @return
     */
    @GET("SearchInfoByParkNameService/1/100/{parkname}")
    public Call<ParkByNameResponse> getParkListByName(@Path("parkname") String parkname);

    /**
     * 공원번호로 조회
     * @param parkNum 공원번호
     * @return
     */
    @GET("SearchParkInfoService/1/5/{parkNum}")
    public Call<ParkResponse> getParkDetail(@Path("parkNum") int parkNum);

}
