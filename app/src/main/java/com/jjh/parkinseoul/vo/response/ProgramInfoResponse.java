package com.jjh.parkinseoul.vo.response;

import com.jjh.parkinseoul.vo.ProgramInfoVO;
import com.jjh.parkinseoul.vo.ProgramVO;

import java.util.List;

/**
 * 프로그램 상세정보 Response
 */
public class ProgramInfoResponse {
    private List<ProgramVO> programSearchList;

    public List<ProgramVO> getProgramSearchList() {
        return programSearchList;
    }

    public void setProgramSearchList(List<ProgramVO> programSearchList) {
        this.programSearchList = programSearchList;
    }
}
