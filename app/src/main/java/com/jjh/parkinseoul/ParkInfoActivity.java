package com.jjh.parkinseoul;

import android.app.Activity;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.jjh.parkinseoul.utils.DisplayUtil;
import com.jjh.parkinseoul.vo.ParkVO;

/**
 * Created by JJH on 2016-09-24.
 */
public class ParkInfoActivity extends Activity{

    public static final String PARAM_PARK_VO = "param_park_vo";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        DisplayMetrics metrics = getResources().getDisplayMetrics();
        int screenWidth = (int) (metrics.widthPixels * 0.9);
        //int screenHeight = (int)(metrics.heightPixels * 0.8);

        setContentView(R.layout.activity_park_info);

        getWindow().setLayout(screenWidth, ViewGroup.LayoutParams.WRAP_CONTENT); //set below the setContentview

        setFinishOnTouchOutside(true);

        initView();
        initData();
    }

    private void initView(){
        findViewById(R.id.parkInfoCloseBtn).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }

    private void initData(){
        initParkInfoFromDB((ParkVO)getIntent().getSerializableExtra(PARAM_PARK_VO));
    }

    /**
     * 공원 정보 조회 From DB
     */
    private void initParkInfoFromDB(ParkVO parkVo){
        //관리부서
        if(DisplayUtil.isEmptyStr(parkVo.getP_division())
                && DisplayUtil.isEmptyStr(parkVo.getP_admintel())){
            //관리부서와 전화번호 둘다 없는 경우 레이아웃 표시 안함
            ((LinearLayout)findViewById(R.id.tvParkManageDept).getParent().getParent()).setVisibility(View.GONE);
        }else{
            //부서정보 표시
            if(DisplayUtil.isEmptyStr(parkVo.getP_division())){
                findViewById(R.id.tvParkManageDept).setVisibility(View.GONE);
            }else {
                ((TextView) findViewById(R.id.tvParkManageDept)).setText(parkVo.getP_division());
            }
            //전화번호 표시
            if(DisplayUtil.isEmptyStr(parkVo.getP_admintel())){
                findViewById(R.id.tvParkDeptPhone).setVisibility(View.GONE);
            }else {
                ((TextView)findViewById(R.id.tvParkDeptPhone)).setText(parkVo.getP_admintel());
            }
        }

        setParkInfoView((TextView)findViewById(R.id.tvParkHomePage), parkVo.getP_homepage()); //개원
        setParkInfoView((TextView)findViewById(R.id.tvParkOpenDate), parkVo.getP_open()); //개원
        setParkInfoView((TextView)findViewById(R.id.tvParkSize), parkVo.getP_size());//면적
        setParkInfoView((TextView)findViewById(R.id.tvParkFacilities), parkVo.getP_facilities());//주요시설
        setParkInfoView((TextView)findViewById(R.id.tvParkPlants), parkVo.getP_plants());//주요식물
    }

    /*
     * 공원정보 View 셋팅
     */
    private void setParkInfoView(TextView view, Object info){
        if(info == null){
            view.setText("해당 정보가 없습니다.");
        }else{

            String infoStr = (String)info;
            if(infoStr.trim().isEmpty() || "()".equals(infoStr.trim())){
                view.setText("해당 정보가 없습니다.");
            }else{
                ((TextView)view).setText(infoStr);
            }

        }
    }

}
