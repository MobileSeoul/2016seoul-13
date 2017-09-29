package com.jjh.parkinseoul;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.provider.MediaStore;
import android.support.v4.widget.SwipeRefreshLayout;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.jjh.parkinseoul.adapter.BoardAdapter;
import com.jjh.parkinseoul.utils.CommonUtil;
import com.jjh.parkinseoul.utils.DisplayUtil;
import com.jjh.parkinseoul.utils.ImageUtil;
import com.jjh.parkinseoul.utils.NetworkConnector;
import com.jjh.parkinseoul.vo.BoardVO;
import com.jjh.parkinseoul.vo.response.BoardAddResponse;
import com.jjh.parkinseoul.vo.response.BoardResponse;
import com.oguzdev.circularfloatingactionmenu.library.FloatingActionMenu;
import com.oguzdev.circularfloatingactionmenu.library.SubActionButton;

import java.util.Arrays;
import java.util.List;

import cn.pedant.sweetalert.SweetAlertDialog;
import okhttp3.FormBody;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class BoardActivity extends Activity implements FloatingActionMenu.MenuStateChangeListener, View.OnLayoutChangeListener, View.OnClickListener, SwipeRefreshLayout.OnRefreshListener, AdapterView.OnItemClickListener {

    public static final String PARAM_PARK_INDEX = "param_park_index";

    private final int REQUEST_CODE_CAMERA = 0;
    private final int REQUEST_CODE_GALLERY = 1;

    private FloatingActionMenu bottomMenu;

    private SwipeRefreshLayout swipeRefreshLayout;
    private ListView lvBoardList;
    private ProgressHelper progressHelper;
    private View loadingView;

    private String attachBase64Image = null;

    private BoardAdapter boardAdapter;
    private Call<BoardAddResponse> boardAddCall;

    private Call<BoardResponse> boardPrevCall; //게시판 이전 글 조회
    private Call<BoardResponse> boardNextCall; //게시판 다음 글 조회

    private boolean isBoardPrevRequesting = false;
    private boolean isBoardCurrRequesting = false;

    private int parkIdx;
    private String currBNo;
    private String lastBNo;

    private boolean lastItemVisibleFlag = false;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_board);

        initView();
        initCircleMenu();
        initData();
    }

    private void initView(){
        lvBoardList = (ListView) findViewById(R.id.lvBoardList);

        swipeRefreshLayout = (SwipeRefreshLayout)findViewById(R.id.swipeRefreshLayout);
        swipeRefreshLayout.setEnabled(false);
        swipeRefreshLayout.setOnRefreshListener(this);
        swipeRefreshLayout.setColorSchemeResources(R.color.blue, R.color.red, R.color.yellow,  R.color.black);

        findViewById(R.id.btnSend).setOnClickListener(this);
        findViewById(R.id.btnBack).setOnClickListener(this);
        findViewById(R.id.etBoardContent).clearFocus();

        progressHelper = new ProgressHelper((ProgressBar)findViewById(R.id.progressTree), (TextView) findViewById(R.id.tvLoading));

        ((EditText)findViewById(R.id.etBoardContent)).addTextChangedListener(new TextWatcher() {
            String previousString = "";
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                previousString= s.toString();
            }
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {}
            @Override
            public void afterTextChanged(Editable s) {
                EditText et = ((EditText)findViewById(R.id.etBoardContent));
                if (et.getLineCount() > 20)
                {
                    Toast.makeText(BoardActivity.this,"한마디 입력은 최대 20줄까지 가능합니다.",Toast.LENGTH_SHORT).show();
                    et.setText(previousString);
                    et.setSelection(et.length());
                }

            }
        });

    }

    private void initData(){
        parkIdx = getIntent().getIntExtra(PARAM_PARK_INDEX, -1);
        boardAdapter = new BoardAdapter(this);
        lvBoardList.setAdapter(boardAdapter);

        lvBoardList.setOnItemClickListener(this);

        loadPreviousBoardList();
    }

    private void initCircleMenu(){
        lvBoardList.addOnLayoutChangeListener(this);
        // Attach a menu to the button_on in the bottom bar, just to prove that it works.
        View flAttachPhoto = findViewById(R.id.flAttachPhoto);
        SubActionButton.Builder rLSubBuilder = new SubActionButton.Builder(this);
        ImageView rlIcon1 = new ImageView(this);
        ImageView rlIcon2 = new ImageView(this);
        ImageView rlIcon3 = new ImageView(this);

        rlIcon1.setImageDrawable(getResources().getDrawable(R.drawable.ic_action_cancel_light));
        rlIcon2.setImageDrawable(getResources().getDrawable(R.drawable.ic_action_picture_light));
        rlIcon3.setImageDrawable(getResources().getDrawable(R.drawable.ic_action_camera_light));

        SubActionButton sab1 = rLSubBuilder.setContentView(rlIcon1).build();
        SubActionButton sab2 = rLSubBuilder.setContentView(rlIcon2).build();
        SubActionButton sab3 = rLSubBuilder.setContentView(rlIcon3).build();

        sab1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                bottomMenu.close(true);

            }
        });

        sab2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                bottomMenu.close(true);
                Uri uri = Uri.parse("content://media/external/images/media");
                Intent intent = new Intent(Intent.ACTION_VIEW, uri);
                intent.setAction(Intent.ACTION_GET_CONTENT);
                intent.setType("image/*");
                startActivityForResult(intent, REQUEST_CODE_GALLERY);
            }
        });

        sab3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                bottomMenu.close(true);
                Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                startActivityForResult(intent, REQUEST_CODE_CAMERA);

            }
        });

        bottomMenu = new FloatingActionMenu.Builder(this)
                .addSubActionView(sab1)
                .addSubActionView(sab2)
                .addSubActionView(sab3)
                .setStartAngle(-35)
                .setEndAngle(-90)
                .setRadius(getResources().getDimensionPixelSize(R.dimen.radius_medium))
                .attachTo(flAttachPhoto)
                .build();
    }

    private void addBoard(){
        String content = ((EditText)findViewById(R.id.etBoardContent)).getText().toString();
        if(DisplayUtil.isEmptyStr(content)){
            new SweetAlertDialog(this, SweetAlertDialog.WARNING_TYPE)
                    .setTitleText(null)
                    .setContentText("한마디를 입력 해 주세요.")
                    .setConfirmClickListener(new SweetAlertDialog.OnSweetClickListener() {
                        @Override
                        public void onClick(SweetAlertDialog sDialog) {
                            sDialog.dismiss();
                            ((EditText)findViewById(R.id.etBoardContent)).requestFocus();
                            CommonUtil.showSoftkeyboard(BoardActivity.this,findViewById(R.id.etBoardContent));
                        }
                    }).show();
            //Toast.makeText(this,"한마디를 입력 해 주세요.",Toast.LENGTH_SHORT).show();

            return;
        }
        showHideAddBoardProgress(true);
        FormBody.Builder bodyBuilder = new FormBody.Builder();
        bodyBuilder.add("p_idx", parkIdx + "");
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
                        ((EditText)findViewById(R.id.etBoardContent)).setText("");
                        delAttachPhoto();
                        Toast.makeText(BoardActivity.this, "한마디가 등록 되었습니다.", Toast.LENGTH_SHORT).show();
                        loadCurrentBoardList();
                        setResult(RESULT_OK);
                    }else{
                        Toast.makeText(BoardActivity.this, "한마디 등록에 실패 하였습니다.\n잠시 후 다시 시도해 주시기 바랍니다.", Toast.LENGTH_SHORT).show();
                    }
                }

            }

            @Override
            public void onFailure(Call<BoardAddResponse> call, Throwable t) {
                showHideAddBoardProgress(false);
                Toast.makeText(BoardActivity.this, "한마디 등록에 실패 하였습니다.\n잠시 후 다시 시도해 주시기 바랍니다.", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void loadCurrentBoardList(){

        if(isBoardCurrRequesting){
            return;
        }

        isBoardCurrRequesting = true;
        boardNextCall = new NetworkConnector().getBoardService().getNextBoardList(currBNo, parkIdx);
        boardNextCall.enqueue(new Callback<BoardResponse>() {
            @Override
            public void onResponse(Call<BoardResponse> call, Response<BoardResponse> response) {
                isBoardCurrRequesting = false;
                swipeRefreshLayout.setRefreshing(false);
                if(response.body().getBoardListArray() != null) {
                    final List<BoardVO> list = response.body().getBoardListArray();
                    if(list.size() > 0) {
                        currBNo = list.get(0).getB_no();
                        String str = "";
                        for(BoardVO vo : list){
                            str += vo.getB_no() + ",";
                        }
                        boardAdapter.addDataFirst(list);
                    }
                    if(list.size() >= 10){
                        loadCurrentBoardList();
                    }
                }
                setVisibleNoBoardText();

            }
            @Override
            public void onFailure(Call<BoardResponse> call, Throwable t) {
                swipeRefreshLayout.setRefreshing(false);
                isBoardCurrRequesting = false;
                setVisibleNoBoardText();
            }
        });
    }

    private void loadPreviousBoardList(){

        if(isBoardPrevRequesting){
            return;
        }
        if(lastBNo == null) {
            progressHelper.showProgress();
        }else{
            loadingView = LayoutInflater.from(this).inflate(R.layout.layout_board_loading,null);
            lvBoardList.addFooterView(loadingView);
            lvBoardList.setSelection(boardAdapter.getCount() - 1);
        }
        isBoardPrevRequesting = true;
        boardPrevCall = new NetworkConnector().getBoardService().getPreviousBoardList(lastBNo, parkIdx);
        boardPrevCall.enqueue(new Callback<BoardResponse>() {
            @Override
            public void onResponse(Call<BoardResponse> call, Response<BoardResponse> response) {
                swipeRefreshLayout.setEnabled(true);
                isBoardPrevRequesting = false;
                if(lastBNo == null) {
                    progressHelper.hideProgress();
                }else{
                    lvBoardList.removeFooterView(loadingView);
                }
                if(response.body().getBoardListArray() != null) {
                    final List<BoardVO> list = response.body().getBoardListArray();
                    if(list.size() > 0) {
                        if(currBNo == null) {
                            currBNo = list.get(0).getB_no();
                        }
                        lastBNo = list.get(list.size()-1).getB_no();
                        boardAdapter.addData(list);
                    }
                    if(list.size() >= 10){
                        lvBoardList.setOnScrollListener(new AbsListView.OnScrollListener() {
                            @Override
                            public void onScrollStateChanged(AbsListView view, int scrollState) {
                                if (scrollState == AbsListView.OnScrollListener.SCROLL_STATE_IDLE && lastItemVisibleFlag) {
                                    loadPreviousBoardList();
                                }
                            }
                            @Override
                            public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
                                lastItemVisibleFlag = (totalItemCount > 0) && (firstVisibleItem + visibleItemCount >= totalItemCount);
                            }
                        });
                    }else{
                        lvBoardList.setOnScrollListener(null);
                    }
                }
                setVisibleNoBoardText();
            }
            @Override
            public void onFailure(Call<BoardResponse> call, Throwable t) {
                swipeRefreshLayout.setEnabled(true);
                isBoardPrevRequesting = false;
                if(lastBNo == null) {
                    progressHelper.hideProgress();
                }else{
                    lvBoardList.removeFooterView(loadingView);
                }
                setVisibleNoBoardText();
            }
        });
    }

    private void setVisibleNoBoardText(){
        if(boardAdapter.getCount() > 0){
            findViewById(R.id.tvNoBoard).setVisibility(View.GONE);
            lvBoardList.setVisibility(View.VISIBLE);
        }else{
            findViewById(R.id.tvNoBoard).setVisibility(View.VISIBLE);
            lvBoardList.setVisibility(View.GONE);
        }
    }

    /**
     * 첨부된 이미지 삭제
     */
    public void delAttachPhoto(){
        attachBase64Image = null;
        ((ImageView)findViewById(R.id.ivAttachedPhoto)).setImageBitmap(null);
        findViewById(R.id.flAttachPhoto).setVisibility(View.VISIBLE);
        findViewById(R.id.rlAttachedPhoto).setVisibility(View.GONE);
    }

    /**
     * 글을 등록중입니다 Progress Visible/Gone
     */
    private void showHideAddBoardProgress(boolean isShow){
        View view = findViewById(R.id.llBoardAddProgress);
        if(isShow) {
            ViewGroup.LayoutParams params = (ViewGroup.LayoutParams) view.getLayoutParams();
            params.height = ((RelativeLayout) view.getParent()).getHeight();
            view.setLayoutParams(params);
        }
        view.setVisibility(isShow? View.VISIBLE : View.GONE);

    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.btnSend : //글저장
                addBoard();
                break;
            case R.id.btnBack: //종료
                finish();
                break;
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent intent) {
        super.onActivityResult(requestCode, resultCode, intent);
        if(resultCode == RESULT_OK && intent.getData() != null) {
            switch (requestCode){
                case REQUEST_CODE_GALLERY:
                    setImageDataFromUri(intent.getData());
                    break;
                case REQUEST_CODE_CAMERA:
                    setImageDataFromUri(intent.getData());
                break;
                }
        }
    }

    public void setImageDataFromUri(Uri imageUri){
        try {
            final Bitmap src = ImageUtil.getBitmapFromUri(this, imageUri);
            attachBase64Image = ImageUtil.convertBase64FromBitmap(src);

            findViewById(R.id.flAttachPhoto).setVisibility(View.GONE);
            findViewById(R.id.rlAttachedPhoto).setVisibility(View.VISIBLE);
            ((ImageView)findViewById(R.id.ivAttachedPhoto)).setImageBitmap(Bitmap.createScaledBitmap(src, DisplayUtil.dpToPx(100), DisplayUtil.dpToPx(100), false));
            findViewById(R.id.llDeletePhoto).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    attachBase64Image = null;
                    ((ImageView)findViewById(R.id.ivAttachedPhoto)).setImageBitmap(null);
                    findViewById(R.id.flAttachPhoto).setVisibility(View.VISIBLE);
                    findViewById(R.id.rlAttachedPhoto).setVisibility(View.GONE);
                }
            });

        }catch(Exception e){
            e.printStackTrace();
        }
    }

    @Override
    public void onRefresh() {
        loadCurrentBoardList();
    }

    @Override
    public void onMenuOpened(FloatingActionMenu menu) {
    }

    @Override
    public void onMenuClosed(FloatingActionMenu menu) {
    }

    @Override
    public void onLayoutChange(View v, int left, int top, int right, int bottom, int oldLeft, int oldTop, int oldRight, int oldBottom) {
        if(right - left != 0 && bottom - top != 0 &&
                (oldLeft != left || oldTop != top || oldRight != right || oldBottom != bottom) && bottomMenu != null) {
            bottomMenu.updateItemPositions();
        }
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        if(boardAdapter != null){
            BoardVO vo = boardAdapter.getItem(position);
            Intent intent = new Intent(this, BoardDetailActivity.class);
            intent.putExtra(BoardDetailActivity.PARAM_BOARD_VO, vo);
            overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
        }
    }
}
