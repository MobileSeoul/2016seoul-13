package com.jjh.parkinseoul.vo.response;

import com.jjh.parkinseoul.vo.BoardVO;

import java.util.List;

/**
 * Created by JJH on 2016-09-18.
 * 게시글 목록 Response
 */
public class BoardResponse {
    private List<BoardVO> boardListArray;

    public List<BoardVO> getBoardListArray() {
        return boardListArray;
    }

    public void setBoardListArray(List<BoardVO> boardListArray) {
        this.boardListArray = boardListArray;
    }
}
