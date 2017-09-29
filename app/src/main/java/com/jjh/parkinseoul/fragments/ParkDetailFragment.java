package com.jjh.parkinseoul.fragments;

import android.app.Activity;
import android.app.Instrumentation;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.GradientDrawable;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.provider.MediaStore;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.text.Editable;
import android.text.Html;
import android.text.TextWatcher;
import android.util.Log;
import android.view.Display;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import com.achep.header2actionbar.HeaderFragment;
import com.bumptech.glide.Glide;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.MapsInitializer;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.jjh.parkinseoul.BoardActivity;
import com.jjh.parkinseoul.BoardDetailActivity;
import com.jjh.parkinseoul.MenuActivity;
import com.jjh.parkinseoul.ParkInfoActivity;
import com.jjh.parkinseoul.ProgressHelper;
import com.jjh.parkinseoul.R;
import com.jjh.parkinseoul.ZoomImageActivity;
import com.jjh.parkinseoul.utils.BusProvider;
import com.jjh.parkinseoul.utils.CommonUtil;
import com.jjh.parkinseoul.utils.DisplayUtil;
import com.jjh.parkinseoul.utils.ImageUtil;
import com.jjh.parkinseoul.utils.NetworkConnector;
import com.jjh.parkinseoul.vo.ActivityResultEvent;
import com.jjh.parkinseoul.vo.BoardVO;
import com.jjh.parkinseoul.vo.FavoriteVO;
import com.jjh.parkinseoul.vo.ParkImageVO;
import com.jjh.parkinseoul.vo.ParkVO;
import com.jjh.parkinseoul.vo.ProgramVO;
import com.jjh.parkinseoul.vo.response.BoardAddResponse;
import com.jjh.parkinseoul.vo.response.BoardResponse;
import com.jjh.parkinseoul.vo.response.NearSubwayResponse;
import com.jjh.parkinseoul.vo.response.ParkImageResponse;
import com.jjh.parkinseoul.vo.response.ProgramByParkResponse;
import com.jjh.parkinseoul.vo.response.ProgramInfoResponse;
import com.jjh.parkinseoul.widget.CTextView;
import com.like.LikeButton;
import com.like.OnLikeListener;

import com.oguzdev.circularfloatingactionmenu.library.FloatingActionMenu;
import com.oguzdev.circularfloatingactionmenu.library.SubActionButton;
import com.squareup.otto.Subscribe;

import java.util.List;

import cn.pedant.sweetalert.SweetAlertDialog;
import okhttp3.FormBody;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * 공원 상세 Fragment
 */
public class ParkDetailFragment extends HeaderFragment  implements FloatingActionMenu.MenuStateChangeListener, ViewTreeObserver.OnScrollChangedListener, View.OnLayoutChangeListener, View.OnClickListener {

    private final int REQUEST_CODE_CAMERA = 0; //Camera 호출
    private final int REQUEST_CODE_GALLERY = 1; //Gallery 호출
    private final int REQUEST_CODE_BOARD_ACTIVITY = 2; //게시판 리스트Activity 호출

    public static final String PARAM_PARK_NUM = "param_park_num";
    public static final String PARAM_PARK_NAME = "param_park_name";
    public static final String PARAM_PARENT_MENU_TITLE = "param_parent_menu_title";

    private static final String TAG_KEY_BOARD_DATE = "tag_key_board_date";


    private ScrollView mScrollView;
    private String[] mListViewTitles;
    private boolean mLoaded;

    private FrameLayout mContentOverlay;
    private View headerView;

    private LinearLayout boardListContainer; //공원 목록 Container

    private int parkNum; //공원 번호
    private String parkName; //공원명

    private Call<ProgramInfoResponse> programCall; //프로그램 검색 API
    private Call<ParkImageResponse> nImageCall; //네이버 이미지 검색 API
    private Call<BoardResponse> boardCall; //게시판
    private Call<BoardAddResponse> boardAddCall;  //게시판 글등록
    private ParkVO parkVo;

    private Bundle mSavedInstanceState;

    private FloatingActionMenu boardAttachMenu; //이미지 첨부 Circle메뉴

    private String attachBase64Image; //이미지 base64 값
    private String currBNo;//가장 최근에 조회한 Board No값

    /**********************************************************************
     * Header Library 관련
     **********************************************************************/
    @Override
    public View onCreateContentOverlayView(LayoutInflater inflater, ViewGroup container) {
        mContentOverlay = new FrameLayout(getActivity());
        return mContentOverlay;
    }

    private void setListViewTitles(String[] titles) {
        mLoaded = true;
        mListViewTitles = titles;
        if (mScrollView == null) return;
        mScrollView.setVisibility(View.VISIBLE);
    }


    /**********************************************************************
     * Fragment Override
     **********************************************************************/
    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        setHeaderBackgroundScrollMode(HEADER_BACKGROUND_SCROLL_PARALLAX);
        setOnHeaderScrollChangedListener(new OnHeaderScrollChangedListener() {
            @Override
            public void onHeaderScrollChanged(float progress, int height, int scroll) {

                progress = (float) scroll / height;
                if (progress > 1f) progress = 1f;

                progress = (1 - (float) Math.cos(progress * Math.PI)) * 0.5f;

            }
        });

        //공원찾기 Fragment에서 넘어온 공원명, 공원번호 값 셋팅
        parkName = getArguments().getString(PARAM_PARK_NAME);
        parkNum = getArguments().getInt(PARAM_PARK_NUM);

        ((MenuActivity)getActivity()).setActivityTitle(parkName); //상단 타이틀 공원명으로 변경

        //이벤트 버스 등록
        BusProvider.getInstance().register(ParkDetailFragment.this);
    }

    @Override
    public void onDetach() {
        //프로그램 Request 취소
        if(programCall != null && programCall.isExecuted()){
            programCall.cancel();
        }
        //네이버 이미지 API Request 취소
        if(nImageCall != null && nImageCall.isExecuted()){
            nImageCall.cancel();
        }
        //게시판 Request 취소
        if(boardCall != null && boardCall.isExecuted()){
            boardCall.cancel();
        }
        //게시판 글 등록 Request 취소
        if(boardAddCall != null && boardAddCall.isExecuted()){
            boardAddCall.cancel();
        }
        //게시판 이미지 첨부 Circle메뉴 닫기
        if(boardAttachMenu != null && boardAttachMenu.isOpen()){
            boardAttachMenu.close(true);
        }

        //이벤트 버스 해제
        BusProvider.getInstance().unregister(ParkDetailFragment.this);

        //이전 Fragment 메뉴명으로 상단 타이틀 변경
        ((MenuActivity)getActivity()).setActivityTitle(getArguments().getString(PARAM_PARENT_MENU_TITLE));

        super.onDetach();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        mSavedInstanceState = savedInstanceState;
        return super.onCreateView(inflater, container, savedInstanceState);
    }

    @Override
    public View onCreateHeaderView(LayoutInflater inflater, ViewGroup container) {
        headerView = inflater.inflate(R.layout.fragment_park_detail_header, container, false);

        LikeButton btnStar = (LikeButton)headerView.findViewById(R.id.btnStar);

        boolean isExistsFavorite = ((MenuActivity)getActivity()).getDatabaseHelper().isExistsFavorite(parkNum);
        if(isExistsFavorite){
            btnStar.setLiked(true);
        }

        //즐겨찾기 버튼 이벤트
        btnStar.setOnLikeListener(new OnLikeListener() {
            @Override
            public void liked(LikeButton likeButton) {
                FavoriteVO vo = new FavoriteVO();
                vo.setParkVO_id(parkNum);
                boolean isSuccess = ((MenuActivity)getActivity()).getDatabaseHelper().insertFavoritePark(vo);
                if(isSuccess) {
                    Toast.makeText(getActivity(), "즐겨찾기에 추가 되었습니다.", Toast.LENGTH_SHORT).show();
                    BusProvider.getInstance().post(new Boolean(true));
                }
            }

            @Override
            public void unLiked(LikeButton likeButton) {
                boolean isSuccess = ((MenuActivity)getActivity()).getDatabaseHelper().deleteFavoritePark(parkNum);
                if(isSuccess) {
                    Toast.makeText(getActivity(), "즐겨찾기가 해제 되었습니다.", Toast.LENGTH_SHORT).show();
                    BusProvider.getInstance().post(new Boolean(true));
                }
            }
        });

        //공원상세정보 버튼 이벤트
        headerView.findViewById(R.id.btnParkInfo).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(), ParkInfoActivity.class);
                intent.putExtra(ParkInfoActivity.PARAM_PARK_VO, parkVo);
                startActivity(intent);
            }
        });

       return headerView;
    }

    @Override
    public View onCreateContentView(LayoutInflater inflater, ViewGroup container) {
        mScrollView = (ScrollView) inflater.inflate(R.layout.fragment_park_detail_content, container, false);
        mScrollView.getViewTreeObserver().addOnScrollChangedListener(this);
        if (mLoaded) setListViewTitles(mListViewTitles);
        initView();
        initCircleMenus();
        loadData();
        return mScrollView;
    }


    /**
     * View 초기화
     */
    private void initView(){

        boardListContainer = (LinearLayout)mScrollView.findViewById(R.id.llBoardList); //게시판목록 Container

        mScrollView.findViewById(R.id.btnAllBoard).setOnClickListener(this);

        ((EditText)mScrollView.findViewById(R.id.etBoardContent)).addTextChangedListener(new TextWatcher() {
            String previousString = "";
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                previousString= s.toString();
            }
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {}
            @Override
            public void afterTextChanged(Editable s) {
                EditText et = ((EditText)mScrollView.findViewById(R.id.etBoardContent));
                if (et.getLineCount() > 20)
                {
                    Toast.makeText(getActivity(),"한마디 입력은 최대 20줄까지 가능합니다.",Toast.LENGTH_SHORT).show();
                    et.setText(previousString);
                    et.setSelection(et.length());
                }

            }
        });

    }

    /**
     * Location Map 초기화
     */
    private void initLocationMap(String lat, String lang){

        final MapView mapView = (MapView)mScrollView.findViewById(R.id.mvLocationMap);
        mapView.onCreate(mSavedInstanceState);

        /*try{
            MapsInitializer.initialize(getActivity().getApplicationContext());
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }*/
        final LatLng latLng = new LatLng(Double.parseDouble(lat), Double.parseDouble(lang));
        mapView.getMapAsync(new OnMapReadyCallback() {
            @Override
            public void onMapReady(GoogleMap googleMap) {
                CameraPosition cp = new CameraPosition.Builder().target((latLng)).zoom(13f).build();
                googleMap.moveCamera(CameraUpdateFactory.newCameraPosition(cp));
                MarkerOptions mo = new MarkerOptions();
                mo.position(latLng);
                mo.title(parkVo.getP_park()); // 제목 미리보기
//                mo.snippet(parkVo.getP_addr());
//                mo.icon(BitmapDescriptorFactory
//                        .fromResource(R.drawable.progress_tree));
                googleMap.addMarker(mo).showInfoWindow();
                mapView.onResume();
            }
        });

        mScrollView.findViewById(R.id.ibZoomLocationMap).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startLocationMapFragment();
            }
        });


    }

    /**
     * 게시판 이미지 등록 Circle 메뉴 초기화
     */
    private void initCircleMenus(){
        View boardAttachButton = mScrollView.findViewById(R.id.flAttachPhoto);//게시판 글등록 + 버튼

        SubActionButton.Builder rLSubBuilder = new SubActionButton.Builder(getActivity());
        ImageView icClose = new ImageView(getActivity());
        ImageView icGallery = new ImageView(getActivity());
        ImageView icCamera = new ImageView(getActivity());

        icClose.setImageDrawable(getResources().getDrawable(R.drawable.ic_action_cancel_light));
        icGallery.setImageDrawable(getResources().getDrawable(R.drawable.ic_action_picture_light));
        icCamera.setImageDrawable(getResources().getDrawable(R.drawable.ic_action_camera_light));

        final SubActionButton saClose = rLSubBuilder.setContentView(icClose).build();
        final SubActionButton saGallery = rLSubBuilder.setContentView(icGallery).build();
        final SubActionButton saCamera = rLSubBuilder.setContentView(icCamera).build();

        View.OnClickListener circleMenuClickListener = new View.OnClickListener(){

            @Override
            public void onClick(View v) {
                if(v == saClose){ //Circle 메뉴 닫기
                    boardAttachMenu.close(true);
                }else if(v == saGallery){ //갤러리 호출
                    boardAttachMenu.close(true);
                    Uri uri = Uri.parse("content://media/external/images/media");
                    Intent intent = new Intent(Intent.ACTION_VIEW, uri);
                    intent.setAction(Intent.ACTION_GET_CONTENT);
                    intent.setType("image/*");
                    getActivity().startActivityForResult(intent, REQUEST_CODE_GALLERY);
                }else if(v == saCamera){ //카메라 호출
                    boardAttachMenu.close(true);
                    Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                    getActivity().startActivityForResult(intent, REQUEST_CODE_CAMERA);
                }
            }
        };
        saClose.setOnClickListener(circleMenuClickListener);
        saGallery.setOnClickListener(circleMenuClickListener);
        saCamera.setOnClickListener(circleMenuClickListener);

        boardAttachMenu = new FloatingActionMenu.Builder(getActivity())
                .addSubActionView(saClose)
                .addSubActionView(saGallery)
                .addSubActionView(saCamera)
                .setStartAngle(90)
                .setEndAngle(-90)
                .setRadius(getResources().getDimensionPixelSize(R.dimen.radius_small))
                .attachTo(boardAttachButton)
                .build();

        //스크롤 시 Circle메뉴 자동으로 위치 이동하도록 리스너 등록
        mScrollView.findViewById(R.id.btnSendBoard).setOnClickListener(this);
    }

    /****************************************************************************
     * 데이터 조회
     ***************************************************************************/

    /*
    * 최초 로드시 모든 데이터 초기화
    */
    private void loadData(){

        initParkInfoFromDB();
        //initExtraImages();
        initParkNearSubwayAndBusInfo();
        initParkProgramInfo();
        initBoardList();

    }

    /**
     * 공원 정보 조회 From DB
     */
    private void initParkInfoFromDB(){
        parkVo = ((MenuActivity)getActivity()).getDatabaseHelper().selectParkDetail(parkNum);
        Glide.with(getActivity()).load(parkVo.getP_img()).into((ImageView)getHeaderBackgroundView()); //공원 이미지
        ((TextView)headerView.findViewById(R.id.title)).setText(parkVo.getP_park()); //공원명
        ((TextView)headerView.findViewById(R.id.subtitle)).setText(parkVo.getP_addr()); //주소
        ((CTextView)mScrollView.findViewById(R.id.tvParkInfo)).setText(" " + parkVo.getP_list_content()); //공원개요

  /*      //관리부서
        if(DisplayUtil.isEmptyStr(parkVo.getP_division())
                && DisplayUtil.isEmptyStr(parkVo.getP_admintel())){
            //관리부서와 전화번호 둘다 없는 경우 레이아웃 표시 안함
            ((LinearLayout)mScrollView.findViewById(R.id.tvParkManageDept).getParent().getParent()).setVisibility(View.GONE);
        }else{
            //부서정보 표시
            if(DisplayUtil.isEmptyStr(parkVo.getP_division())){
                mScrollView.findViewById(R.id.tvParkManageDept).setVisibility(View.GONE);
            }else {
                ((TextView) mScrollView.findViewById(R.id.tvParkManageDept)).setText(parkVo.getP_division());
            }
            //전화번호 표시
            if(DisplayUtil.isEmptyStr(parkVo.getP_admintel())){
                mScrollView.findViewById(R.id.tvParkDeptPhone).setVisibility(View.GONE);
            }else {
                ((TextView)mScrollView.findViewById(R.id.tvParkDeptPhone)).setText(parkVo.getP_admintel());
            }
        }

        //홈페이지
        if(DisplayUtil.isEmptyStr(parkVo.getP_homepage())){
            ((LinearLayout)mScrollView.findViewById(R.id.tvParkHomePage).getParent()).setVisibility(View.GONE);
        }else {
            ((TextView) mScrollView.findViewById(R.id.tvParkHomePage)).setText(parkVo.getP_homepage());
        }*/

     /*   setParkInfoView((TextView)mScrollView.findViewById(R.id.tvParkOpenDate), parkVo.getP_open()); //개원
        setParkInfoView((TextView)mScrollView.findViewById(R.id.tvParkSize), parkVo.getP_size());//면적
        setParkInfoView((TextView)mScrollView.findViewById(R.id.tvParkFacilities), parkVo.getP_facilities());//주요시설
        setParkInfoView((TextView)mScrollView.findViewById(R.id.tvParkPlants), parkVo.getP_plants());//주요식물*/
        setParkInfoView((ImageView)mScrollView.findViewById(R.id.ivParkMap), parkVo.getP_locationmap()); //안내도

        initLocationMap(parkVo.getLatitude(), parkVo.getLongitude());

    }

    /**
     * 네이버 이미지 API 호출
     */
  /*  private void initExtraImages(){
        nImageCall = new NetworkConnector().getNaverImageAPIService().getImagesFromNaver("1",parkName);
        nImageCall.enqueue(new Callback<ParkImageResponse>() {
            @Override
            public void onResponse(Call<ParkImageResponse> call, Response<ParkImageResponse> response) {
                List<ParkImageVO> list = response.body().getParkImageResponseData().getItem();
                //이미지 HorizontalScrollView에 추가
                for(int i=0; i<list.size(); i++) {
                    ImageView iv = new ImageView(getActivity());
                    iv.setScaleType(ImageView.ScaleType.FIT_XY);
                    LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(DisplayUtil.dpToPx(230), ViewGroup.LayoutParams.MATCH_PARENT);
                    params.setMargins(20,0,20,0);
                    iv.setLayoutParams(params);
                    Glide.with(getActivity()).load(list.get(i).getThumbnail()).into(iv);
                    ((LinearLayout) mScrollView.findViewById(R.id.llNaverImages)).addView(iv);
                }

            }

            @Override
            public void onFailure(Call<ParkImageResponse> call, Throwable t) {
            }
        });
    }*/


    /**
     * 공원 프로그램 조회 API 호출
     */
    private void initParkProgramInfo(){
        final ProgressHelper progressHelper = new ProgressHelper((ProgressBar)mScrollView.findViewById(R.id.programProgressTree), (TextView)mScrollView.findViewById(R.id.tvProgramLoading));
        progressHelper.showProgress();

        programCall = new NetworkConnector().getProgramInfoService().getProgramInfoByParkIdx(parkNum);
        programCall.enqueue(new Callback<ProgramInfoResponse>() {
            @Override
            public void onResponse(Call<ProgramInfoResponse> call, Response<ProgramInfoResponse> response) {
                progressHelper.hideProgress();
                final List<ProgramVO> list = response.body().getProgramSearchList();
                if(list != null && list.size() > 0) {
                    final LinearLayout containerView = ((LinearLayout)mScrollView.findViewById(R.id.llParkProgram));
                    for(int i=0; i<list.size(); i++){
                        ProgramVO vo = list.get(i);
                        addProgramView(vo);
                        if(i == 4 && list.size() > 5){
                            //5개 이상인 경우는 더보기 버튼 으로 대체
                            final View moreView = LayoutInflater.from(getActivity()).inflate(R.layout.layout_see_more, null);
                            ((LinearLayout)mScrollView.findViewById(R.id.llParkProgram)).addView(moreView);
                             moreView.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    //더보기 버튼 클릭 시 레이아웃에 나머지 프로그램 추가
                                    addLineView(containerView);
                                    ((LinearLayout)moreView.getParent()).removeView(moreView);
                                    for(int j=5; j<list.size();j++){
                                        addProgramView(list.get(j));
                                        addLineView(containerView);
                                    }
                                }
                            });

                            break;
                        }else{
                            addLineView(containerView);
                        }
                    }
                }else{
                    //공원정보없음 표시
                    mScrollView.findViewById(R.id.tvNoParkProgram).setVisibility(View.VISIBLE);
                }
            }

            @Override
            public void onFailure(Call<ProgramInfoResponse> call, Throwable t) {
        }
        });
    }

    /**
     * 공원 주변 대중교통 조회
     */
    private void initParkNearSubwayAndBusInfo(){
        if(parkVo == null){
            return;
        }
        if(DisplayUtil.isEmptyStr(parkVo.getP_near_subway())) {
            ((TextView) mScrollView.findViewById(R.id.tvParkNearSubway)).setText("주변 지하철 정보가 없습니다.");
        }else{
            ((TextView) mScrollView.findViewById(R.id.tvParkNearSubway)).setText(Html.fromHtml(parkVo.getP_near_subway()));
        }

        if(DisplayUtil.isEmptyStr(parkVo.getP_near_bus())){
            ((TextView) mScrollView.findViewById(R.id.tvParkNearBus)).setText("주변 버스 정보가 없습니다.");
        }else {
            ((TextView) mScrollView.findViewById(R.id.tvParkNearBus)).setText(Html.fromHtml(parkVo.getP_near_bus()));
        }

    }

    /**
     * 최초 게시판 목록 조회
     */
    private void initBoardList(){

        final ProgressHelper progressHelper = new ProgressHelper((ProgressBar) mScrollView.findViewById(R.id.boardProgressTree), (TextView) mScrollView.findViewById(R.id.tvBoardLoading));
        progressHelper.showProgress();

        boardCall = new NetworkConnector().getBoardService().getCurrentBoardList(currBNo, parkVo.getP_idx());
        boardCall.enqueue(new Callback<BoardResponse>() {
            @Override
            public void onResponse(Call<BoardResponse> call, Response<BoardResponse> response) {
                progressHelper.hideProgress();
                if(response.body().getBoardListArray() != null) {
                    final List<BoardVO> list = response.body().getBoardListArray();
                    if(list.size() > 0) {
                        for (int i = 0; i < list.size() && i < 5; i++) {
                            BoardVO vo = list.get(i);
                            if (i == 0) {
                                currBNo = vo.getB_no();
                            }
                            View boardView = getBoardView(vo);
                            boardListContainer.addView(boardView);
                        }
                        if(mScrollView.findViewById(R.id.tvNoBoard).getVisibility() == View.VISIBLE){
                            //한마디가 없습니다 없애기
                            mScrollView.findViewById(R.id.tvNoBoard).setVisibility(View.GONE);
                        }
                    }else {
                        //한마디가 없습니다 표시
                        mScrollView.findViewById(R.id.tvNoBoard).setVisibility(View.VISIBLE);
                    }
                }else{
                    //한마디가 없습니다 표시
                    mScrollView.findViewById(R.id.tvNoBoard).setVisibility(View.VISIBLE);
                }
            }
            @Override
            public void onFailure(Call<BoardResponse> call, Throwable t) {
            }
        });
    }

    /**
     * 실시간 게시판 조회(글 등록시, 주기적으로 호출)
     */
    private void loadRealBoardList(){
        boardCall = new NetworkConnector().getBoardService().getCurrentBoardList(currBNo, parkVo.getP_idx());
        boardCall.enqueue(new Callback<BoardResponse>() {
            @Override
            public void onResponse(Call<BoardResponse> call, Response<BoardResponse> response) {
                final List<BoardVO> list = response.body().getBoardListArray();
                if(list != null && list.size() > 0) {
                    int boardListCount = boardListContainer.getChildCount(); //이미 조회된 글 갯수
                    int newCount = list.size(); //새글 갯수
                    int removeCount = boardListCount + newCount - 5; //지울 조회된 글 갯수

                    if(removeCount > 0) {
                        //맨 하단의 글 부터 지울 갯수만큼 Layout에서 삭제
                        for (int i = 0; i < removeCount; i++) {
                            boardListContainer.removeViewAt((boardListCount)-- -1);
                        }
                        //존재하는 글 작성일 업데이트
                        for(int i=boardListContainer.getChildCount()-1; i >= 0; i--){
                            View boardView = boardListContainer.getChildAt(i);
                            Object tag = boardView.getTag();
                            if(tag != null){
                                String date = DisplayUtil.formatDateTimeStr(tag.toString());
                                ((TextView)boardView.findViewById(R.id.tvBoardDate)).setText(date);

                            }
                        }
                    }

                    //새로운 글 Layout에 추가
                    for(int i=list.size()-1; i>=0; i--) {
                        BoardVO vo = list.get(i);
                        View boardView = getBoardView(vo);
                        boardListContainer.addView(boardView,0);
                        Animation anim = AnimationUtils.loadAnimation
                                (getActivity(),
                                        R.anim.alpha_anim);
                        boardView.startAnimation(anim);
                        if(i==0){
                            currBNo = vo.getB_no();
                        }
                    }

                    if(mScrollView.findViewById(R.id.tvNoBoard).getVisibility() == View.VISIBLE){
                        //한마디가 없습니다 없애기
                        mScrollView.findViewById(R.id.tvNoBoard).setVisibility(View.GONE);
                    }
                }
            }

            @Override
            public void onFailure(Call<BoardResponse> call, Throwable t) {
            }
        });
    }

    /**
     * 게시판 글등록 Request
     */
    private void addBoard(){
        String content = ((EditText)mScrollView.findViewById(R.id.etBoardContent)).getText().toString();
        if(DisplayUtil.isEmptyStr(content)){
            new SweetAlertDialog(getActivity(), SweetAlertDialog.WARNING_TYPE)
                    .setTitleText(null)
                    .setContentText("한마디를 입력 해 주세요.")
                    .setConfirmClickListener(new SweetAlertDialog.OnSweetClickListener() {
                        @Override
                        public void onClick(SweetAlertDialog sDialog) {
                            sDialog.dismissWithAnimation();
                            ((EditText)mScrollView.findViewById(R.id.etBoardContent)).requestFocus();
                            CommonUtil.showSoftkeyboard(getActivity(),mScrollView.findViewById(R.id.etBoardContent));
                        }
                    }).show();
            return;
        }
        showHideAddBoardProgress(true);
        FormBody.Builder bodyBuilder = new FormBody.Builder();
        bodyBuilder.add("p_idx", parkVo.getP_idx() + "");
        bodyBuilder.add("b_contents", content);
        if(attachBase64Image != null) {
            bodyBuilder.add("b_image", attachBase64Image);
        }
        RequestBody requestBody = bodyBuilder.build();
        boardAddCall = new NetworkConnector().getBoardService().addBoardList(requestBody);
        boardAddCall.enqueue(new Callback<BoardAddResponse>() {
            @Override
            public void onResponse(Call<BoardAddResponse> call, Response<BoardAddResponse> response) {
                showHideAddBoardProgress(false);
                if(response.body() != null){
                    if("Y".equalsIgnoreCase(response.body().getSuccess())){
                        ((EditText)mScrollView.findViewById(R.id.etBoardContent)).setText("");
                        delAttachPhoto(); //첨부된 이미지 삭제
                        loadRealBoardList(); //추가된 게시글 조회
                        Toast.makeText(getActivity(), "한마디가 등록 되었습니다.", Toast.LENGTH_SHORT).show();
                    }else{
                        Toast.makeText(getActivity(), "한마디 등록에 실패 하였습니다.\n잠시 후 다시 시도해 주시기 바랍니다.", Toast.LENGTH_SHORT).show();
                    }
                }
            }

            @Override
            public void onFailure(Call<BoardAddResponse> call, Throwable t) {
                Toast.makeText(getActivity(), "한마디 등록에 실패 하였습니다.\n잠시 후 다시 시도해 주시기 바랍니다.", Toast.LENGTH_SHORT).show();
                showHideAddBoardProgress(false);
            }
        });
    }

    /***********************************************************
     * View 관련
     ***********************************************************/

    /*
     * 공원정보 View 셋팅
     */
    private void setParkInfoView(View view, Object info){
        if(info == null){
            ((ViewGroup) view.getParent()).setVisibility(View.GONE);
        }else{
            if(view instanceof TextView) { //TextView인 경우
                String infoStr = (String)info;
                if(infoStr.trim().isEmpty() || "()".equals(infoStr.trim())){
                    ((LinearLayout) view.getParent()).setVisibility(View.GONE);
                }else{
                    ((TextView)view).setText(infoStr);
                }
            }else if(view instanceof ImageView){ //ImageView 인경우
                Glide.with(getActivity()).load((byte[])info).into((ImageView)view);
                if(view.getId() == R.id.ivParkMap) {
                    mScrollView.findViewById(R.id.ibZoomMap).setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            Intent intent = new Intent(getActivity(), ZoomImageActivity.class);
                            if (DisplayUtil.isEmptyStr(parkVo.getP_locationmap_url())) {
                                //안내도 이미지 URL 없는 경우 이미지 표시
                                intent.putExtra(ZoomImageActivity.PARAM_IMG_BYTE_ARR, parkVo.getP_locationmap());
                            } else {
                                //안내도 이미지 URL존재시
                                intent.putExtra(ZoomImageActivity.PARAM_IMG_URL, parkVo.getP_locationmap_url());
                            }
                            startActivity(intent);
                            getActivity().overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
                        }
                    });
                }
            }
        }
    }

    /*
     * 프로그램 리스트 추가
     */
    private void addProgramView(final ProgramVO vo){
        View programView = LayoutInflater.from(getActivity()).inflate(R.layout.layout_item_program_search, null);
        ((TextView) programView.findViewById(R.id.tvProgramName)).setText(vo.getP_name());
        ((TextView) programView.findViewById(R.id.tvParkName)).setText(vo.getP_park());
        ((TextView) programView.findViewById(R.id.tvProgramDate)).setText(vo.getP_eduday_s() + " ~ " + vo.getP_eduday_e());
        ((TextView) programView.findViewById(R.id.tvProgramTime)).setText(vo.getP_proday() + " | " + vo.getP_edutime());
        ((TextView) programView.findViewById(R.id.tvTargetAge)).setText(vo.getP_name());
        GradientDrawable drawble = (GradientDrawable) programView.findViewById(R.id.tvTargetAge).getBackground();

        if(vo.getP_eduperson() != null) {
            if (vo.getP_eduperson().contains("유아")) {
                ((TextView)programView.findViewById(R.id.tvTargetAge)).setCompoundDrawablesWithIntrinsicBounds(R.drawable.age_icon_baby, 0, 0, 0);
                ((TextView)programView.findViewById(R.id.tvTargetAge)).setText("유아");
                drawble.setColor(Color.parseColor("#FFE400"));
            } else if (vo.getP_eduperson().contains("어린이")) {
                ((TextView)programView.findViewById(R.id.tvTargetAge)).setCompoundDrawablesWithIntrinsicBounds(R.drawable.age_icon_child, 0, 0, 0);
                ((TextView)programView.findViewById(R.id.tvTargetAge)).setText("어린이");
                drawble.setColor(Color.parseColor("#47C83E"));
            }else if (vo.getP_eduperson().contains("청소년")) {
                ((TextView)programView.findViewById(R.id.tvTargetAge)).setCompoundDrawablesWithIntrinsicBounds(R.drawable.age_icon_teenager, 0, 0, 0);
                ((TextView)programView.findViewById(R.id.tvTargetAge)).setText("청소년");
                drawble.setColor(Color.parseColor("#4374D9"));
            } else if (vo.getP_eduperson().contains("성인")) {
                ((TextView)programView.findViewById(R.id.tvTargetAge)).setCompoundDrawablesWithIntrinsicBounds(R.drawable.age_icon_adult, 0, 0, 0);
                ((TextView)programView.findViewById(R.id.tvTargetAge)).setText("성인");
                drawble.setColor(Color.parseColor("#FF5A5A"));
            } else{
                ((TextView)programView.findViewById(R.id.tvTargetAge)).setCompoundDrawablesWithIntrinsicBounds(R.drawable.age_icon_else, 0, 0, 0);
                ((TextView)programView.findViewById(R.id.tvTargetAge)).setText(vo.getP_eduperson());
                drawble.setColor(Color.parseColor("#8041D9"));
            }
        }
        programView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startProgramDetailFragment(vo);
            }
        });
        ((LinearLayout)mScrollView.findViewById(R.id.llParkProgram)).addView(programView);
    }

    /**
     * 라인 추가
     */
    private View addLineView(ViewGroup parent){
        View lineView = new View(getActivity());
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT,2);
        lineView.setBackgroundColor(getResources().getColor(android.R.color.black));
        lineView.setLayoutParams(params);
        parent.addView(lineView);
        return lineView;
    }

    /**
     * 게시판 리스트 Item View
     */
    private View getBoardView(final BoardVO vo){
        String date = DisplayUtil.formatDateTimeStr(vo.getB_date());
        View boardView = LayoutInflater.from(getActivity()).inflate(R.layout.layout_item_board_list, null);
        ((TextView)boardView.findViewById(R.id.tvBoardText)).setText(vo.getB_contents());
        ((TextView)boardView.findViewById(R.id.tvBoardDate)).setText(date);
        ImageUtil.setBase64ToImageView((ImageView)boardView.findViewById(R.id.ivBoardPhoto), vo.getB_image());
        ((TextView)boardView.findViewById(R.id.tvReplyCnt)).setText(vo.getReply_cnt() + " 개");
        boardView.setTag(vo.getB_date());

        boardView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(), BoardDetailActivity.class);
                intent.putExtra(BoardDetailActivity.PARAM_BOARD_VO, vo);
                getActivity().startActivity(intent);
                getActivity().overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
            }
        });

        return boardView;
    }

    /**
     * 첨부된 이미지 삭제
     */
    public void delAttachPhoto(){
        attachBase64Image = null;
        ((ImageView)mScrollView.findViewById(R.id.ivAttachedPhoto)).setImageBitmap(null);
        mScrollView.findViewById(R.id.flAttachPhoto).setVisibility(View.VISIBLE);
        mScrollView.findViewById(R.id.rlAttachedPhoto).setVisibility(View.GONE);
    }

    /**
     * 첨부된 이미지 표시
     */
    public void setImageDataFromUri(Uri imageUri){
        try {
            final Bitmap src = ImageUtil.getBitmapFromUri(getActivity(), imageUri);
            new Runnable() {
                @Override
                public void run() {
                    attachBase64Image = ImageUtil.convertBase64FromBitmap(src);
                }
            }.run();
            mScrollView.findViewById(R.id.flAttachPhoto).setVisibility(View.GONE);
            mScrollView.findViewById(R.id.rlAttachedPhoto).setVisibility(View.VISIBLE);
            ((ImageView)mScrollView.findViewById(R.id.ivAttachedPhoto)).setImageBitmap(Bitmap.createScaledBitmap(src, DisplayUtil.dpToPx(100), DisplayUtil.dpToPx(100), false));
            mScrollView.findViewById(R.id.llDeletePhoto).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                   delAttachPhoto();
                }
            });

        }catch(Exception e){
            e.printStackTrace();
        }
    }

    /**
     * 글을 등록중입니다 Progress Visible/Gone
     */
    private void showHideAddBoardProgress(boolean isShow){
        CommonUtil.hideSoftKeyboard(getActivity());
        View view = mScrollView.findViewById(R.id.llBoardAddProgress);
        if(isShow) {
            ViewGroup.LayoutParams params = (ViewGroup.LayoutParams) view.getLayoutParams();
            params.height = ((RelativeLayout) view.getParent()).getHeight();
            view.setLayoutParams(params);
        }
        view.setVisibility(isShow? View.VISIBLE : View.GONE);
    }


    /*****************************************************
     *  이벤트
     *****************************************************/

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.btnSendBoard: //전송 버튼
                addBoard();
                break;
            case R.id.btnAllBoard : //게시판 전체 목록 보기
                Intent intent = new Intent(getActivity(), BoardActivity.class);
                intent.putExtra(BoardActivity.PARAM_PARK_INDEX, parkVo.getP_idx());
                startActivityForResult(intent,REQUEST_CODE_BOARD_ACTIVITY);
                getActivity().overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
                break;
        }

    }
    @Override
    public void onMenuOpened(FloatingActionMenu menu) {}

    @Override
    public void onMenuClosed(FloatingActionMenu menu) {}

    @Override
    public void onScrollChanged() {
        if(boardAttachMenu != null) {
            //스크롤시 Circle메뉴 위치 이동
            boardAttachMenu.updateItemPositions();
        }
    }

    @Override
    public void onLayoutChange(View v, int left, int top, int right, int bottom, int oldLeft, int oldTop, int oldRight, int oldBottom) {
        if(right - left != 0 && bottom - top != 0 &&
                (oldLeft != left || oldTop != top || oldRight != right || oldBottom != bottom) && boardAttachMenu != null) {
            //레이아웃 변경시 Circle 메뉴 위치 이동
            boardAttachMenu.updateItemPositions();
        }
    }

    /*
     *Event Otto Bus용 메소드
     */
    @Subscribe
    public void onBusEvent(ActivityResultEvent activityResultEvent){
        if(activityResultEvent != null) {
            onActivityResult(activityResultEvent.getRequestCode(), activityResultEvent.getResultCode(), activityResultEvent.getData());
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent intent) {
        super.onActivityResult(requestCode, resultCode, intent);
        if(resultCode == Activity.RESULT_OK) {
            switch (requestCode){
                case REQUEST_CODE_GALLERY: //갤러리에서 사진선택
                case REQUEST_CODE_CAMERA: //카메라
                    if(intent.getData() != null) {
                        setImageDataFromUri(intent.getData());
                    }
                    break;
                case REQUEST_CODE_BOARD_ACTIVITY: //게시판 목록 Activity
                    loadRealBoardList();
                    break;
            }
        }
    }

    /**
     * Location Map Fragment 호출
     */
    protected void startLocationMapFragment() {
        FragmentManager fm = getFragmentManager();
        Fragment fragment = new ParkLocationMapFragment();
        Bundle args = new Bundle();
        args.putSerializable(ParkLocationMapFragment.PARAM_PARK_VO, parkVo);
        fragment.setArguments(args);
        getActivity().getSupportFragmentManager().beginTransaction().setCustomAnimations(R.anim.transition_enter_from_right, R.anim.transition_exit_to_right).add(R.id.main_fragment, fragment).addToBackStack(null).commit();
    }

    /**
     * 프로그램 상세 Fragment 호출
     */
    protected void startProgramDetailFragment(ProgramVO vo) {
        FragmentManager fm = getFragmentManager();
        Fragment fragment = new ProgramDetailFragment();
        Bundle args = new Bundle();
        args.putSerializable(ProgramDetailFragment.PARAM_PROGRAM_VO, vo);
        args.putBoolean(ProgramDetailFragment.PARAM_IS_FROM_PARK_DETAIL, true);
        args.putString(ProgramDetailFragment.PARAM_PARENT_MENU_TITLE, getArguments().getString(PARAM_PARK_NAME));
        fragment.setArguments(args);
        getActivity().getSupportFragmentManager().beginTransaction().setCustomAnimations(R.anim.transition_enter_from_right, R.anim.transition_exit_to_right).add(R.id.main_fragment, fragment).addToBackStack(null).commit();
    }

}
