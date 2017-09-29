package com.jjh.parkinseoul.service;

import com.jjh.parkinseoul.vo.response.ParkByNameResponse;
import com.jjh.parkinseoul.vo.response.ParkImageResponse;
import com.jjh.parkinseoul.vo.response.ParkResponse;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Path;
import retrofit2.http.Query;

/**
 * Created by JJH on 2016-08-21.
 * 네이버 이미지 API
 */
public interface NaverImageAPIService {

    /**
     * @param start 시작index
     * @param query 검색어
     * @return
     */
    @GET("image.xml?display=5&sort=sim")
    public Call<ParkImageResponse> getImagesFromNaver(@Query("start") String start, @Query("query") String query);

}
