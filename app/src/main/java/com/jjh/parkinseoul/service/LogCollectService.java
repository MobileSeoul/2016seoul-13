package com.jjh.parkinseoul.service;

import com.jjh.parkinseoul.vo.response.BoardAddResponse;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;

/**
 * Created by JJH on 2016-08-21.
 * Runtime Exception Log수집 Service
 */
public interface LogCollectService {

    @GET("dataInsert.do")
    public Call<BoardAddResponse> addExceptionLogInfo(@Query("log_desc") String log_desc, @Query("os_version") String os_version,
                    @Query("device") String device, @Query("app_version") String app_version, @Query("debug_yn") String debug_yn);

}
