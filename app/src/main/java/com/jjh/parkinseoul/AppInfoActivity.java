package com.jjh.parkinseoul;

import android.app.Activity;
import android.content.pm.PackageInfo;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.jjh.parkinseoul.utils.DataBaseHelper;

public class AppInfoActivity extends Activity implements View.OnClickListener{

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        DisplayMetrics metrics = getResources().getDisplayMetrics();
        int screenWidth = (int) (metrics.widthPixels * 0.9);
        int screenHeight = (int)(metrics.heightPixels * 0.8);

        setContentView(R.layout.activity_app_info);

        getWindow().setLayout(screenWidth, screenHeight);

        initView();

    }

    private void initView(){
        try {
            PackageInfo i = getPackageManager().getPackageInfo(getPackageName(), 0);
            ((TextView)findViewById(R.id.appInfoDBVersion)).setText("V" + i.versionName);
        }catch(Exception e){
            e.printStackTrace();
        }

        ((TextView)findViewById(R.id.appInfoDBVersion)).setText(DataBaseHelper.getDatabaseVersion() + "");

        findViewById(R.id.appInfoCloseBtn).setOnClickListener(this);

    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.appInfoCloseBtn:
                finish();
                break;
        }
    }
}
