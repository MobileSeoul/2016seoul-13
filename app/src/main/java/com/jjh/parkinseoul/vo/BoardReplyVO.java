package com.jjh.parkinseoul.vo;

import java.io.Serializable;

/**
 * Created by JJH on 2016-09-18.
 */
public class BoardReplyVO implements Serializable{
    private String b_contents;
    private String b_date;
    private String b_no;

    public String getB_contents() {
        return b_contents;
    }

    public void setB_contents(String b_contents) {
        this.b_contents = b_contents;
    }

    public String getB_date() {
        return b_date;
    }

    public void setB_date(String b_date) {
        this.b_date = b_date;
    }

    public String getB_no() {
        return b_no;
    }

    public void setB_no(String b_no) {
        this.b_no = b_no;
    }

}
