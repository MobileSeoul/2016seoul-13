package com.jjh.parkinseoul.utils;

import android.app.Activity;
import android.content.Context;
import android.content.res.Resources;
import android.view.inputmethod.InputMethodManager;

import java.text.SimpleDateFormat;
import java.util.Calendar;

/**
 * Created by JJH on 2016-09-07.
 */
public class DisplayUtil {

    /**
     * null String => ""
     */
    public static String nullToBlank(String str){
        if(str == null){
            return "";
        }
        return str;
    }

    /**
     * dp => px
     */
    public static int dpToPx(int dp)
    {
        return (int) (dp * Resources.getSystem().getDisplayMetrics().density);
    }

    /**
     * px => dp
     */
    public static int pxToDp(int px)
    {
        return (int) (px / Resources.getSystem().getDisplayMetrics().density);
    }


    /**
     * 게시판 시간 표시
     */
    public static String formatDateTimeStr(String yyyyMMddHHmm){
        if(yyyyMMddHHmm != null && yyyyMMddHHmm.length() == 12) {
            Calendar cal = Calendar.getInstance();
            String currDate = new SimpleDateFormat("yyyyMMdd").format(cal.getTime());

            if(yyyyMMddHHmm.startsWith(currDate)) {
                 String currTime = new SimpleDateFormat("HHmm").format(cal.getTime());
                int currHH = Integer.parseInt(currTime.substring(0,2)); //현재시
                int currMM = Integer.parseInt(currTime.substring(2,4)); //현재분
                int hh = Integer.parseInt(yyyyMMddHHmm.substring(8,10)); //시
                int mm = Integer.parseInt(yyyyMMddHHmm.substring(10,12)); //분
                if(Integer.parseInt(currTime) <= (hh * 100 + mm)){
                    return "방금";
                }else if((currHH * 60 + currMM) -(hh * 60 + mm) < 60){
                    return (currHH * 60 + currMM) -(hh * 60 + mm) + "분전";
                }else{
                    return yyyyMMddHHmm.substring(8,10) + ":" + yyyyMMddHHmm.substring(10,12);
                }
            }else{
                return convertFormatFormalDate(yyyyMMddHHmm,true);
            }
        }else{
            return null;
        }
    }

    public static String convertFormatFormalDate(String yyyyMMddHHmm, boolean isAddNewLine){
        return yyyyMMddHHmm.substring(0, 4) + "-" + yyyyMMddHHmm.substring(4, 6) + "-" + yyyyMMddHHmm.substring(6, 8)
                + (isAddNewLine ? "\n" : " ") + yyyyMMddHHmm.substring(8, 10) + ":" + yyyyMMddHHmm.substring(10, 12);
    }
    /**
     * String null이거나 ""인지 판단
     */
    public static boolean isEmptyStr(String str){
        return str == null || str.trim().isEmpty();
    }

}
