package com.jjh.parkinseoul.vo;

import java.io.Serializable;

/**
 * Created by JJH on 2016-09-18.
 */
public class BoardVO implements Serializable{
    private String b_contents;
    private String b_date;
    private String b_image;
    private String b_no;
    private String p_idx;
    private String rownum;
    private String select_type;
    private String startBNo;
    private String reply_cnt;

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

    public String getB_image() {
        return b_image;
    }

    public void setB_image(String b_image) {
        this.b_image = b_image;
    }

    public String getB_no() {
        return b_no;
    }

    public void setB_no(String b_no) {
        this.b_no = b_no;
    }

    public String getP_idx() {
        return p_idx;
    }

    public void setP_idx(String p_idx) {
        this.p_idx = p_idx;
    }

    public String getRownum() {
        return rownum;
    }

    public void setRownum(String rownum) {
        this.rownum = rownum;
    }

    public String getSelect_type() {
        return select_type;
    }

    public void setSelect_type(String select_type) {
        this.select_type = select_type;
    }

    public String getStartBNo() {
        return startBNo;
    }

    public void setStartBNo(String startBNo) {
        this.startBNo = startBNo;
    }

    public String getReply_cnt() {
        return reply_cnt;
    }

    public void setReply_cnt(String reply_cnt) {
        this.reply_cnt = reply_cnt;
    }
}
