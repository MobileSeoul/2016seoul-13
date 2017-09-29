package com.jjh.parkinseoul.vo.response;

import com.jjh.parkinseoul.vo.BoardReplyVO;
import com.jjh.parkinseoul.vo.BoardVO;

import java.util.List;

/**
 * Created by JJH on 2016-09-18.
 * 게시글 댓글 목록 Response
 */
public class BoardReplyResponse {
    private List<BoardReplyVO> boardReplyListArray;

    public List<BoardReplyVO> getBoardReplyListArray() {
        return boardReplyListArray;
    }

    public void setBoardReplyListArray(List<BoardReplyVO> boardReplyListArray) {
        this.boardReplyListArray = boardReplyListArray;
    }
}
