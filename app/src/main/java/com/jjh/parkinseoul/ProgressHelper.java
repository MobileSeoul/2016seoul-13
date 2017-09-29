package com.jjh.parkinseoul;

import android.app.Activity;
import android.os.Handler;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;


/**
 * Created by kurt on 08 06 2015 .
 */
public class ProgressHelper {

    private Handler handle=new Handler();
    private final TextView tvLoading;
    private final ProgressBar pbLoading;

    public ProgressHelper(ProgressBar pbLoading, TextView tvLoading) {
        this.pbLoading = pbLoading;
        this.tvLoading = tvLoading;
    }

    public void showProgress() {
        if(tvLoading != null) {
            tvLoading.setVisibility(View.VISIBLE);
        }
        if(pbLoading != null) {
            pbLoading.setVisibility(View.VISIBLE);
        }
    }

    public void hideProgress(){
        if(tvLoading != null) {
            tvLoading.setVisibility(View.GONE);
        }
        if(pbLoading != null) {
            pbLoading.setVisibility(View.GONE);
        }
    }

}
