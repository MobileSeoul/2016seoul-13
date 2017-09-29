package com.jjh.parkinseoul;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import com.jjh.parkinseoul.utils.DataBaseHelper;

/**
 * Created by JJH on 2016-08-21.
 */
public class MainActivity extends Activity implements View.OnClickListener{
    private Toast toast;
    private long backKeyPressedTime =  0;

    private DataBaseHelper dbHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        findViewById(R.id.btnFavoritePark).setOnClickListener(this);
        findViewById(R.id.btnParkSearch).setOnClickListener(this);
        findViewById(R.id.btnProgramSearch).setOnClickListener(this);
        findViewById(R.id.btnNearPark).setOnClickListener(this);

        findViewById(R.id.btnAppInfo).setOnClickListener(this);


    }

    @Override
    public void onClick(View v) {
        Intent intent = new Intent(this, MenuActivity.class);

        switch (v.getId()){
            case R.id.btnFavoritePark: //즐겨찾는 공원
                intent.putExtra(MenuActivity.PARAM_INIT_MENU, MenuActivity.MENU_FAVORITE);
                break;
            case R.id.btnParkSearch: //공원 찾기
                intent.putExtra(MenuActivity.PARAM_INIT_MENU, MenuActivity.MENU_PARK_SEARCH);
                break;
            case R.id.btnProgramSearch: //프로그램 찾기
                intent.putExtra(MenuActivity.PARAM_INIT_MENU, MenuActivity.MENU_PROGRAM_SEARCH);
                break;
            case R.id.btnNearPark: //내 주변 공원
                intent.putExtra(MenuActivity.PARAM_INIT_MENU, MenuActivity.MENU_NEAR_PARK);
                break;
            case R.id.btnAppInfo: //앱정보
                Intent appInfoIntent = new Intent(this, AppInfoActivity.class);
                startActivity(appInfoIntent);
                return;
        }
        startActivity(intent);

    }

    @Override
    public void onBackPressed() {
        if (System.currentTimeMillis() > backKeyPressedTime + 2000) {
            backKeyPressedTime = System.currentTimeMillis();
            toast = Toast.makeText(this,
                    "\'뒤로\'버튼을 한번 더 누르시면 종료됩니다.", Toast.LENGTH_SHORT);
            toast.show();
            return;
        }
        if (System.currentTimeMillis() <= backKeyPressedTime + 2000) {
            toast.cancel();
            super.onBackPressed(); //default
        }
    }
}
