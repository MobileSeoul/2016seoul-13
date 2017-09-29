package com.jjh.parkinseoul.utils;

import java.io.File;
import java.io.FileOutputStream;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.List;

import android.app.Activity;
import android.content.Context;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.InputMethodManager;

import com.jjh.parkinseoul.common.Config;

public class CommonUtil {
	
	public static void logE(String tag, String msg){
		if(Config.IS_DEBUG){
			Log.e(tag, msg);
		}
	}
	
	public static void logI(String tag, String msg){
		if(Config.IS_DEBUG){
			Log.i(tag, msg);
		}
	}
	
	 /**
     * SoftKeyboard 숨김
     * 
     */
	 public static void hideSoftKeyboard(Activity activity){
		 if (activity != null && activity.getCurrentFocus() != null) {
			 ((InputMethodManager) activity.getSystemService(Context.INPUT_METHOD_SERVICE)).hideSoftInputFromWindow(activity.getCurrentFocus().getWindowToken(), 0);
		 }
	 }

    /**
     * SoftKeyboard 보임
     * @param context
     * @param view
     */
    public static void showSoftkeyboard(Context context, View view) {
        if (view != null) {
            InputMethodManager imm = (InputMethodManager) context.getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.showSoftInput(view, 0);
        }
    }

    public static void writeLogFile(String str){
    	if(!Config.IS_DEBUG) return;
		
    	doWriteLogFile(str);
		
	}
    
    public static void doWriteLogFile(String str){
		if(!Config.IS_DEBUG){
			return;
		}
		File file = new File("/sdcard/temp.txt");
		try{
			logE("FILE", "WRITE_START");
			if(!file.exists()){
				file.createNewFile();
			}
			FileOutputStream stream = new FileOutputStream(file, true);
			stream.write(str.getBytes());
			stream.close();
			logE("FILE", "WRITE_END");
			
		}catch(Exception e){
			logE("FILE_WRITE_ERROR", e.getMessage());
		}
		
		
	}
    
    public static String getStackTraceStr(final Throwable throwable) {
        final StringWriter sw = new StringWriter();
        final PrintWriter pw = new PrintWriter(sw, true);
        throwable.printStackTrace(pw);
        return sw.getBuffer().toString();
   }
}
