package com.jjh.parkinseoul.service;

import com.jjh.parkinseoul.vo.response.BoardAddResponse;
import com.jjh.parkinseoul.vo.response.BoardResponse;
import com.jjh.parkinseoul.vo.response.ProgramInfoResponse;

import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Query;

/**
 * Created by JJH on 2016-08-21.
 * 게시판 관련 Service
 */
public interface ProgramInfoService {

    /**
     * 프로그램 조회(이름)
     * @param search_word 프로그램 번호
     * @return
     */
    @GET("programView.do")
    public Call<ProgramInfoResponse> getProgramInfoByName(@Query("search_word") String search_word);

    /**
     * 프로그램 조회(공원번호)
     * @param p_idx 프로그램 번호
     * @return
     */
    @GET("programView.do")
    public Call<ProgramInfoResponse> getProgramInfoByParkIdx(@Query("p_idx") int p_idx);
}
