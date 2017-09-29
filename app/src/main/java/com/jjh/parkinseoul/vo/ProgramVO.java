package com.jjh.parkinseoul.vo;

import java.io.Serializable;

/**
 * Created by JJH on 2016-08-31.
 */
public class ProgramVO implements Serializable{
    private String p_idx;				//공원번호
    private String g_idx;			//프로그램번호
    private String p_name;			//프로그램명
    private String p_eduperson;		//행사대상
    private String p_eamax;			//인원
    private String p_content;		//프로그램내용
    private String p_eduday_s;		//프로그램시작일
    private String p_eduday_e;		//프로그램종료일
    private String p_edutime;		//프로그램시간
    private String p_proday;			//요일
    private String p_park;          //공원명

    public String getP_idx() {
        return p_idx;
    }

    public void setP_idx(String p_idx) {
        this.p_idx = p_idx;
    }

    public String getG_idx() {
        return g_idx;
    }

    public void setG_idx(String g_idx) {
        this.g_idx = g_idx;
    }

    public String getP_name() {
        return p_name;
    }

    public void setP_name(String p_name) {
        this.p_name = p_name;
    }

    public String getP_eduperson() {
        return p_eduperson;
    }

    public void setP_eduperson(String p_eduperson) {
        this.p_eduperson = p_eduperson;
    }

    public String getP_eamax() {
        return p_eamax;
    }

    public void setP_eamax(String p_eamax) {
        this.p_eamax = p_eamax;
    }

    public String getP_content() {
        return p_content;
    }

    public void setP_content(String p_content) {
        this.p_content = p_content;
    }

    public String getP_eduday_s() {
        return p_eduday_s;
    }

    public void setP_eduday_s(String p_eduday_s) {
        this.p_eduday_s = p_eduday_s;
    }

    public String getP_eduday_e() {
        return p_eduday_e;
    }

    public void setP_eduday_e(String p_eduday_e) {
        this.p_eduday_e = p_eduday_e;
    }

    public String getP_edutime() {
        return p_edutime;
    }

    public void setP_edutime(String p_edutime) {
        this.p_edutime = p_edutime;
    }

    public String getP_proday() {
        return p_proday;
    }

    public void setP_proday(String p_proday) {
        this.p_proday = p_proday;
    }

    public String getP_park() {
        return p_park;
    }

    public void setP_park(String p_park) {
        this.p_park = p_park;
    }
}
