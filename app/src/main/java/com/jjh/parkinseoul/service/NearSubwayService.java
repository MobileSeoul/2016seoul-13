package com.jjh.parkinseoul.service;

import com.jjh.parkinseoul.vo.response.NearSubwayResponse;
import com.jjh.parkinseoul.vo.response.ParkByNameResponse;
import com.jjh.parkinseoul.vo.response.ParkResponse;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Path;

/**
 * Created by JJH on 2016-08-21.
 * 주변 지하철 조회
 */
public interface NearSubwayService {
    /**
     * @param gpsX GPS X좌표
     * @param gpsY GPS Y좌표
     * @return
     */
    @GET("nearBy/0/15/{gpsX}/{gpsY}")
    public Call<NearSubwayResponse> getNearSubwayList(@Path("gpsX") String gpsX, @Path("gpsY") String gpsY);

}
