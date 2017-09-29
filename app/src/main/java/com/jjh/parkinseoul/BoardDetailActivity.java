package com.jjh.parkinseoul;

import android.os.Bundle;
import android.app.Activity;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import com.jjh.parkinseoul.utils.CommonUtil;
import com.jjh.parkinseoul.utils.DisplayUtil;
import com.jjh.parkinseoul.utils.ImageUtil;
import com.jjh.parkinseoul.utils.NetworkConnector;
import com.jjh.parkinseoul.vo.BoardReplyVO;
import com.jjh.parkinseoul.vo.BoardVO;
import com.jjh.parkinseoul.vo.response.BoardAddResponse;
import com.jjh.parkinseoul.vo.response.BoardReplyResponse;
import com.jjh.parkinseoul.vo.response.BoardResponse;

import java.util.List;

import cn.pedant.sweetalert.SweetAlertDialog;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class BoardDetailActivity extends Activity implements View.OnClickListener{

    public static final String PARAM_BOARD_VO = "param_board_vo";

    private Call<BoardReplyResponse> boardReplyCall; //게시글 댓글 조회
    private Call<BoardAddResponse> boardReplyAddResponse; //게시글 댓글 달기

    private BoardVO boardVo;

    private LinearLayout llBoardReplyList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_board_detail);

        boardVo = (BoardVO)getIntent().getSerializableExtra(PARAM_BOARD_VO);

        initView();
        loadBoardReplyList(false);
    }

    private void initView(){

        findViewById(R.id.btnBack).setOnClickListener(this);
        findViewById(R.id.btnSend).setOnClickListener(this);

        ((TextView)findViewById(R.id.tvBoardDate)).setText(DisplayUtil.convertFormatFormalDate(boardVo.getB_date(),false));
        ((TextView)findViewById(R.id.tvBoardContent)).setText(boardVo.getB_contents());
        llBoardReplyList = (LinearLayout) findViewById(R.id.llBoardReplyList);

        if(DisplayUtil.isEmptyStr(boardVo.getB_image())){
            findViewById(R.id.ivBoardPhoto).setVisibility(View.GONE);
        }else{
            ImageUtil.setBase64ToImageView((ImageView)findViewById(R.id.ivBoardPhoto), boardVo.getB_image());
        }

        ((EditText)findViewById(R.id.etBoardReplyContent)).addTextChangedListener(new TextWatcher() {
            String previousString = "";
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                previousString= s.toString();
            }
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {}
            @Override
            public void afterTextChanged(Editable s) {
                EditText et = ((EditText)findViewById(R.id.etBoardReplyContent));
                if (et.getLineCount() > 5)
                {
                    Toast.makeText(BoardDetailActivity.this,"댓글 입력은 최대 5줄까지 가능합니다.",Toast.LENGTH_SHORT).show();
                    et.setText(previousString);
                    et.setSelection(et.length());
                }

            }
        });

    }

    private void loadBoardReplyList(final boolean isScollDown){
        llBoardReplyList.removeAllViews();
        boardReplyCall = new NetworkConnector().getBoardService().getBoardReplyList(boardVo.getB_no());
        boardReplyCall.enqueue(new Callback<BoardReplyResponse>() {
            @Override
            public void onResponse(Call<BoardReplyResponse> call, Response<BoardReplyResponse> response) {
                if(response.body().getBoardReplyListArray() != null) {
                    List<BoardReplyVO> list = response.body().getBoardReplyListArray();
                    if(list.size() > 0) {
                        findViewById(R.id.tvNoBoardReply).setVisibility(View.GONE);
                        for(BoardReplyVO vo : list){
                            llBoardReplyList.addView(getReplyView(vo));
                        }
                        if(isScollDown){
                            ((ScrollView)findViewById(R.id.svBoardDetail)).post(new Runnable() {
                                @Override
                                public void run() {
                                    ((ScrollView)findViewById(R.id.svBoardDetail)).fullScroll(View.FOCUS_DOWN);
                                }

                            });
                        }
                    }else{
                        findViewById(R.id.tvNoBoardReply).setVisibility(View.VISIBLE);
                    }

                }else{
                    findViewById(R.id.tvNoBoardReply).setVisibility(View.VISIBLE);
                }
            }
            @Override
            public void onFailure(Call<BoardReplyResponse> call, Throwable t) {
                findViewById(R.id.tvNoBoardReply).setVisibility(View.VISIBLE);
            }
        });
    }

    private View getReplyView(BoardReplyVO vo){
        View replyView = LayoutInflater.from(this).inflate(R.layout.layout_item_board_reply_list,null);
        ((TextView)replyView.findViewById(R.id.tvBoardReplyText)).setText(vo.getB_contents());
        ((TextView)replyView.findViewById(R.id.tvBoardReplyDate)).setText(DisplayUtil.convertFormatFormalDate(vo.getB_date(),true));
        return replyView;
    }

    private void addBoardReply(){
        String content = ((EditText)findViewById(R.id.etBoardReplyContent)).getText().toString();
        if(DisplayUtil.isEmptyStr(content)){
            new SweetAlertDialog(this, SweetAlertDialog.WARNING_TYPE)
                    .setTitleText(null)
                    .setContentText("댓글을 입력 해 주세요.")
                    .setConfirmClickListener(new SweetAlertDialog.OnSweetClickListener() {
                        @Override
                        public void onClick(SweetAlertDialog sDialog) {
                            sDialog.dismiss();
                            ((EditText)findViewById(R.id.etBoardReplyContent)).requestFocus();
                        }
                    }).show();

            return;
        }
        showHideAddReplyProgress(true);
        boardReplyAddResponse = new NetworkConnector().getBoardService().addBoardReplyList(boardVo.getB_no(), content);
        boardReplyAddResponse.enqueue(new Callback<BoardAddResponse>() {
            @Override
            public void onResponse(Call<BoardAddResponse> call, Response<BoardAddResponse> response) {
                showHideAddReplyProgress(false);
                if(response.body() != null){
                    if("Y".equalsIgnoreCase(response.body().getSuccess())){
                        ((EditText)findViewById(R.id.etBoardReplyContent)).setText("");
                        Toast.makeText(BoardDetailActivity.this, "댓글이 등록 되었습니다.", Toast.LENGTH_SHORT).show();
                        CommonUtil.hideSoftKeyboard(BoardDetailActivity.this);
                        loadBoardReplyList(true); //댓글 재조회
                    }else{
                        Toast.makeText(BoardDetailActivity.this, "댓글 등록에 실패 하였습니다.\n잠시 후 다시 시도해 주시기 바랍니다.", Toast.LENGTH_SHORT).show();
                    }
                }
            }
            @Override
            public void onFailure(Call<BoardAddResponse> call, Throwable t) {
                showHideAddReplyProgress(false);
                Toast.makeText(BoardDetailActivity.this, "댓글 등록에 실패 하였습니다.\n잠시 후 다시 시도해 주시기 바랍니다.", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void showHideAddReplyProgress(boolean isShow){
        View view = findViewById(R.id.llBoardReplyAddProgress);
        if(isShow) {
            ViewGroup.LayoutParams params = (ViewGroup.LayoutParams) view.getLayoutParams();
            params.height = ((RelativeLayout) view.getParent()).getHeight();
            view.setLayoutParams(params);
        }
        view.findViewById(R.id.llBoardReplyAddProgress).setVisibility(isShow? View.VISIBLE : View.GONE);
    }
    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.btnBack:
                finish();
                break;
            case R.id.btnSend:
                addBoardReply();
                break;
        }
    }
}
