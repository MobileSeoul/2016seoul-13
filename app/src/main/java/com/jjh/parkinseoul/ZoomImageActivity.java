package com.jjh.parkinseoul;

import android.content.Context;
import android.os.AsyncTask;
import android.os.Bundle;
import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;

import com.bumptech.glide.Glide;

import pl.polidea.view.ZoomView;

public class ZoomImageActivity extends Activity {

    public static final String PARAM_IMG_URL = "param_img_url";
    public static final String PARAM_IMG_BYTE_ARR = "param_img_byte_arr";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_zoom_image);
        LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);

        ImageView imageView = new ImageView(this);
        imageView.setScaleType(ImageView.ScaleType.FIT_XY);
        imageView.setLayoutParams(layoutParams);

        ZoomView zoomView = new ZoomView(this);
        zoomView.addView(imageView);
        zoomView.setLayoutParams(layoutParams);
        zoomView.setMiniMapEnabled(false); // 좌측 상단 검은색 미니맵 설정
        zoomView.setMaxZoom(4f); // 줌 Max 배율 설정

        LinearLayout container = (LinearLayout) findViewById(R.id.llZoomView);
        container.addView(zoomView);

        String imgUrl = getIntent().getStringExtra(PARAM_IMG_URL);
        if(imgUrl != null) {
            Glide.with(this).load(imgUrl).into(imageView);
        }else{
            byte[] imgBytes = getIntent().getByteArrayExtra(PARAM_IMG_BYTE_ARR);
            Glide.with(this).load(imgBytes).into(imageView);
        }
        Animation animation = new AnimationUtils().loadAnimation(this, android.R.anim.fade_out);
        animation.setDuration(500);
        animation.setStartOffset(1000);
        animation.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {}
            @Override
            public void onAnimationRepeat(Animation animation) {}
            @Override
            public void onAnimationEnd(Animation animation) {
                findViewById(R.id.llZoomTip).setVisibility(View.GONE);
            }
        });

        findViewById(R.id.llZoomTip).startAnimation(animation);

        findViewById(R.id.ivClose).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

    }

}
