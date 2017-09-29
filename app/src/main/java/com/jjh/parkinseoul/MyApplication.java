package com.jjh.parkinseoul;

import java.lang.Thread.UncaughtExceptionHandler;

import android.app.Application;
import android.content.pm.PackageManager;
import android.net.Network;
import android.os.Build;
import android.util.Log;
import android.widget.EditText;
import android.widget.Toast;

import com.jjh.parkinseoul.common.Config;
import com.jjh.parkinseoul.utils.CommonUtil;
import com.jjh.parkinseoul.utils.NetworkConnector;
import com.jjh.parkinseoul.vo.response.BoardAddResponse;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MyApplication extends Application {
	// uncaught exception handler variable
	private final UncaughtExceptionHandler defaultUEH;

	
	public MyApplication(){
		defaultUEH = Thread.getDefaultUncaughtExceptionHandler();
		
		// setup handler for uncaught exception
		Thread.setDefaultUncaughtExceptionHandler(unCaughtExceptionHandler);
	}
	
	

	// handler listener
	private final UncaughtExceptionHandler unCaughtExceptionHandler = new UncaughtExceptionHandler(){
		@Override
		public void uncaughtException(Thread thread, Throwable ex){
			
			String steStr = CommonUtil.getStackTraceStr(ex);
			steStr += "\n-------------------------------------------------------------------------";
			CommonUtil.doWriteLogFile(steStr);

			String logDesc = CommonUtil.getStackTraceStr(ex);
			String androidOs = Build.VERSION.SDK_INT + "";
			String device = Build.MODEL;
			String appVersion = "";
			try {
				appVersion = getPackageManager().getPackageInfo(getPackageName(), 0).versionName;
			} catch (PackageManager.NameNotFoundException e) {
				e.printStackTrace();
			}
			String debugYn = Config.IS_DEBUG ? "Y" : "N";
			new NetworkConnector().getExceptionLogService()
					.addExceptionLogInfo(logDesc, androidOs, device, appVersion, debugYn)
					.enqueue(new Callback<BoardAddResponse>() {
						@Override
						public void onResponse(Call<BoardAddResponse> call, Response<BoardAddResponse> response) {}
						@Override
						public void onFailure(Call<BoardAddResponse> call, Throwable t) {}
					});

			// re-throw critical exception further to the os (important)
			defaultUEH.uncaughtException(thread, ex);
		}

	};



}
