package com.jjh.parkinseoul;

import android.animation.Animator;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.app.Activity;
import android.os.Handler;
import android.provider.Settings;

import com.jjh.parkinseoul.utils.DataBaseHelper;
import com.romainpiel.shimmer.Shimmer;
import com.romainpiel.shimmer.ShimmerTextView;

public class SplashActivity extends Activity {

    private final int SPLASH_SECONDS = 2000;

    private long startTime;
    private boolean isDBCheckDone = false;
    private boolean isFinished = false;
    private Handler mHandler;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);

        startTime = System.currentTimeMillis();

        Shimmer shimmer = new Shimmer();
        shimmer.setRepeatCount(-1)
                .setDuration(2000)
                .setStartDelay(0)
                .setDirection(Shimmer.ANIMATION_DIRECTION_LTR);
        shimmer.start((ShimmerTextView)findViewById(R.id.tvShimmer));

        mHandler = new Handler();
        mHandler.post(runnableDB);

        new Handler().postDelayed(runnable ,SPLASH_SECONDS);
    }

    @Override
    public void finish() {
        isFinished= true;
        if(mHandler != null){
            mHandler.removeCallbacks(runnableDB);
            mHandler.removeCallbacks(runnable);
        }
        super.finish();
    }

    Runnable runnable = new Runnable() {
        @Override
        public void run() {
            if(isDBCheckDone){
                startMainActivity();
            }
        }
    };

    Runnable runnableDB = new Runnable() {
        @Override
        public void run() {
            new DataBaseHelper(SplashActivity.this).checkDB();
            isDBCheckDone = true;
            if(System.currentTimeMillis() - startTime > SPLASH_SECONDS){
                startMainActivity();
            }
        }
    };

    private synchronized void startMainActivity(){
        if(isFinished){
            return;
        }
        startActivity(new Intent(SplashActivity.this, MainActivity.class));
        overridePendingTransition(android.R.anim.fade_in,android.R.anim.fade_out);
        finish();
    }

}
