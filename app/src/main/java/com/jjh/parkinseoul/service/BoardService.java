package com.jjh.parkinseoul.service;

import com.jjh.parkinseoul.vo.response.BoardAddResponse;
import com.jjh.parkinseoul.vo.response.BoardReplyResponse;
import com.jjh.parkinseoul.vo.response.BoardResponse;
import com.jjh.parkinseoul.vo.response.ParkByNameResponse;
import com.jjh.parkinseoul.vo.response.ParkResponse;

import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Path;
import retrofit2.http.Query;

/**
 * Created by JJH on 2016-08-21.
 * 게시판 관련 Service
 */
public interface BoardService {

    /**
     * 실시간 게시판 목록 조회(공원 상세 화면)
     * @param startBNo 기준 게시글번호
     * @param p_idx 공원번호
     * @return
     */
    @GET("boardSelect.do?select_type=C")
    public Call<BoardResponse> getCurrentBoardList(@Query("startBNo") String startBNo, @Query("p_idx") int p_idx);

    /**
     * 이전 게시판 목록 조회(공원 상세 화면)
     * @param startBNo 기준 게시글번호
     * @param p_idx 공원번호
     * @return
     */
    @GET("boardSelect.do?select_type=P")
    public Call<BoardResponse> getPreviousBoardList(@Query("startBNo") String startBNo, @Query("p_idx") int p_idx);

    /**
     * 다음 게시판 목록 조회(공원 상세 화면)
     * @param startBNo 기준 게시글번호
     * @param p_idx 공원번호
     * @return
     */
    @GET("boardSelect.do?select_type=N")
    public Call<BoardResponse> getNextBoardList(@Query("startBNo") String startBNo, @Query("p_idx") int p_idx);

    //게시판 글 등록
    @POST("boardInsert.do")
    public Call<BoardAddResponse> addBoardList(@Body RequestBody updatedBody);

    //게시글 댓글 조회
    @GET("boardReplySelect.do")
    public Call<BoardReplyResponse> getBoardReplyList(@Query("b_no") String b_no);

    @POST("boardReplyInsert.do")
    public Call<BoardAddResponse> addBoardReplyList(@Query("b_no") String b_no, @Query("b_contents") String b_contents);

}
