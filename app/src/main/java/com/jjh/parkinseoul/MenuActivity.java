package com.jjh.parkinseoul;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.jjh.parkinseoul.fragments.FavoriteParkFragment;
import com.jjh.parkinseoul.fragments.NearParkFragment;
import com.jjh.parkinseoul.fragments.ParkDetailFragment;
import com.jjh.parkinseoul.fragments.ParkLocationMapFragment;
import com.jjh.parkinseoul.fragments.ParkSearchFragment;
import com.jjh.parkinseoul.fragments.ProgramDetailFragment;
import com.jjh.parkinseoul.fragments.ProgramSearchFragment;
import com.jjh.parkinseoul.utils.BusProvider;
import com.jjh.parkinseoul.utils.CommonUtil;
import com.jjh.parkinseoul.utils.DataBaseHelper;
import com.jjh.parkinseoul.vo.ActivityResultEvent;
import com.special.ResideMenu.ResideMenu;
import com.special.ResideMenu.ResideMenuItem;


public class MenuActivity extends FragmentActivity implements View.OnClickListener{

    public static final String PARAM_INIT_MENU = "param_init_menu";

    public static final int MENU_FAVORITE = 0;
    public static final int MENU_PARK_SEARCH = 1;
    public static final int MENU_PROGRAM_SEARCH = 2;
    public static final int MENU_NEAR_PARK = 3;


    private ResideMenu resideMenu;
    private MenuActivity mContext;
    private ResideMenuItem menuItemMain;
    private ResideMenuItem menuItemFavorite;
    private ResideMenuItem menuItemParkSearch;
    private ResideMenuItem menuItemProgramSearch;
    private ResideMenuItem menuItemNearPark;

    private TextView tvActivityTitle;

    private ProgressHelper progressHelper;

    private DataBaseHelper dbHelper;
    /**
     * Called when the activity is first created.
     */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_menu);
        mContext = this;
        tvActivityTitle = (TextView)findViewById(R.id.tvActivityTitle);
        setUpMenu();
        if( savedInstanceState == null ) {
            int initMenu = getIntent().getIntExtra(PARAM_INIT_MENU, MENU_FAVORITE);
            switch (initMenu) {
                case MENU_FAVORITE:
                    changeFragment(new FavoriteParkFragment());
                    break;
                case MENU_PARK_SEARCH:
                    changeFragment(new ParkSearchFragment());
                    break;
                case MENU_PROGRAM_SEARCH:
                    changeFragment(new ProgramSearchFragment());
                    break;
                case MENU_NEAR_PARK:
                    changeFragment(new NearParkFragment());
                    break;
            }
        }
        progressHelper = new ProgressHelper((ProgressBar)findViewById(R.id.progressTree), (TextView)findViewById(R.id.tvLoading));

        initDatabaseHelper();
    }

    private void initDatabaseHelper(){
        dbHelper = new DataBaseHelper(getApplicationContext());
    }

    public DataBaseHelper getDatabaseHelper(){
        if(dbHelper == null){
            initDatabaseHelper();
        }

        return dbHelper;
    }

    private void setUpMenu() {

        // attach to current activity;
        resideMenu = new ResideMenu(this);
        resideMenu.setUse3D(false);
        resideMenu.setBackground(R.drawable.bg_menu);
        resideMenu.setSwipeDirectionDisable(ResideMenu.DIRECTION_LEFT);
        resideMenu.setSwipeDirectionDisable(ResideMenu.DIRECTION_RIGHT);
        resideMenu.setMenuListener(menuListener);
        resideMenu.attachToActivity(this);
        //valid scale factor is between 0.0f and 1.0f. leftmenu'width is 150dip.
        resideMenu.setScaleValue(0.6f);

        // create menu items;
        menuItemMain = new ResideMenuItem(this, R.drawable.menu_icon_main,     "메인");
        menuItemFavorite = new ResideMenuItem(this, R.drawable.menu_icon_star,  "즐겨찾는 공원");
        menuItemParkSearch = new ResideMenuItem(this, R.drawable.menu_icon_search_park, "공원 찾기");
        menuItemProgramSearch = new ResideMenuItem(this, R.drawable.menu_icon_program, "프로그램 찾기");
        menuItemNearPark = new ResideMenuItem(this, R.drawable.menu_icon_near_park, "내 주변 공원");

        menuItemMain.setOnClickListener(this);
        menuItemFavorite.setOnClickListener(this);
        menuItemParkSearch.setOnClickListener(this);
        menuItemProgramSearch.setOnClickListener(this);
        menuItemNearPark.setOnClickListener(this);

        resideMenu.addMenuItem(menuItemMain, ResideMenu.DIRECTION_LEFT);
        resideMenu.addMenuItem(menuItemFavorite, ResideMenu.DIRECTION_LEFT);
        resideMenu.addMenuItem(menuItemParkSearch, ResideMenu.DIRECTION_LEFT);
        resideMenu.addMenuItem(menuItemProgramSearch, ResideMenu.DIRECTION_LEFT);
        resideMenu.addMenuItem(menuItemNearPark, ResideMenu.DIRECTION_LEFT);

        // You can disable a direction by setting ->
        // resideMenu.setSwipeDirectionDisable(ResideMenu.DIRECTION_RIGHT);

        findViewById(R.id.title_bar_left_menu).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                resideMenu.openMenu(ResideMenu.DIRECTION_LEFT);
            }
        });

    }

    @Override
    public boolean dispatchTouchEvent(MotionEvent ev) {
        return resideMenu.dispatchTouchEvent(ev);
    }

    @Override
    public void onClick(View view) {
        CommonUtil.hideSoftKeyboard(this);
        if (view == menuItemMain){
            finish();
        }else if (view == menuItemFavorite){
            changeFragment(new FavoriteParkFragment());
        }else if (view == menuItemParkSearch){
            changeFragment(new ParkSearchFragment());
        }else if (view == menuItemProgramSearch){
            changeFragment(new ProgramSearchFragment());
        }else if (view == menuItemNearPark){
            changeFragment(new NearParkFragment());
        }

        resideMenu.closeMenu();
    }

    private ResideMenu.OnMenuListener menuListener = new ResideMenu.OnMenuListener() {
        @Override
        public void openMenu() {
        }

        @Override
        public void closeMenu() {
        }
    };

    private void changeFragment(Fragment targetFragment){
        resideMenu.clearIgnoredViewList();
        getSupportFragmentManager().popBackStack(null, FragmentManager.POP_BACK_STACK_INCLUSIVE);
        getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.main_fragment, targetFragment, "fragment")
                .setTransitionStyle(FragmentTransaction.TRANSIT_FRAGMENT_FADE)
                .commit();
        hideMainProgress();

    }

    private void clearAllFragments(){

    }

    // What good method is to access resideMenu？
    public ResideMenu getResideMenu(){
        return resideMenu;
    }

    @Override
    public void onBackPressed() {
        if(resideMenu.isOpened()){
            finish();
        }else {
            Fragment currentFragment = getSupportFragmentManager().findFragmentById(R.id.main_fragment);
            if (currentFragment instanceof ParkDetailFragment || currentFragment instanceof ProgramDetailFragment
                    ||  currentFragment instanceof ParkLocationMapFragment) {
                Animation anim = AnimationUtils.loadAnimation(this, R.anim.transition_exit_to_right);
                currentFragment.getView().startAnimation(anim);
                getSupportFragmentManager().popBackStack();
                getSupportFragmentManager().beginTransaction().commit();
            } else {
                resideMenu.openMenu(ResideMenu.DIRECTION_LEFT);
            }
        }
    }

    public void setActivityTitle(String title){
        tvActivityTitle.setText(title);
    }

    public void showMainProgress(){
        if(progressHelper != null) {
            progressHelper.showProgress();
        }
    }

    public void hideMainProgress(){
        if(progressHelper != null) {
            progressHelper.hideProgress();
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        BusProvider.getInstance().post(new ActivityResultEvent(requestCode, resultCode, data));
    }
}
