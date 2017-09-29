package com.jjh.parkinseoul.adapter;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.GradientDrawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.jjh.parkinseoul.BoardDetailActivity;
import com.jjh.parkinseoul.R;
import com.jjh.parkinseoul.utils.DisplayUtil;
import com.jjh.parkinseoul.utils.ImageUtil;
import com.jjh.parkinseoul.vo.BoardVO;
import com.jjh.parkinseoul.vo.BoardVO;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by JJH on 2016-08-22.
 * 게시판 List Adpater
 */
public class BoardAdapter extends BaseAdapter {
    private Context mContext = null;
    private LayoutInflater mInflater = null;
    private List<BoardVO> data = new ArrayList<BoardVO>();
    private int newDataCount = 0;
    public BoardAdapter(Context context){
        mContext = context;
        mInflater = LayoutInflater.from(mContext);
    }
    public void setData(List<BoardVO> data){
        this.data = data;
        notifyDataSetChanged();
    }
    public void addDataFirst(List<BoardVO> data){
        newDataCount = data.size();
        for(int i=0; i<data.size(); i++){
            this.data.add(i,data.get(i));
        }
        notifyDataSetChanged();
    }

    public void addData(List<BoardVO> data){
        this.data.addAll(data);
        notifyDataSetChanged();
    }

    public void add(BoardVO item){
        data.add(item);
        notifyDataSetChanged();
    }
    @Override
    public int getCount() {
        return data.size();
    }

    @Override
    public BoardVO getItem(int position) {
        return data.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    public void remove(BoardVO item){
        data.remove(item);
        notifyDataSetChanged();
    }

    public void clear(){
        data = new ArrayList<BoardVO>();
        notifyDataSetChanged();
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        BoardVO item = getItem(position);

        BoardViewHolder boardViewHolder = null;
        if(convertView == null) {
            boardViewHolder = new BoardViewHolder();
            convertView = mInflater.inflate(R.layout.layout_item_board_list, null);
            boardViewHolder.ivBoardPhoto = (ImageView) convertView.findViewById(R.id.ivBoardPhoto);
            boardViewHolder.tvBoardText = (TextView) convertView.findViewById(R.id.tvBoardText);
            boardViewHolder.tvBoardDate = (TextView) convertView.findViewById(R.id.tvBoardDate);
            boardViewHolder.tvReplyCnt = (TextView) convertView.findViewById(R.id.tvReplyCnt);

            convertView.setTag(boardViewHolder);
        }else{
            boardViewHolder = (BoardViewHolder) convertView.getTag();
        }

        ImageUtil.setBase64ToImageView(boardViewHolder.ivBoardPhoto, item.getB_image());
        boardViewHolder.tvBoardText.setText(item.getB_contents());
        boardViewHolder.tvBoardDate.setText(DisplayUtil.formatDateTimeStr(item.getB_date()));
        boardViewHolder.tvReplyCnt.setText(item.getReply_cnt() + " 개");

        if(position < newDataCount){
            Animation anim = AnimationUtils.loadAnimation(mContext,R.anim.alpha_anim);
            convertView.startAnimation(anim);
        }else{
            newDataCount = 0;
        }

        final int voPosition = position;
        convertView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(mContext, BoardDetailActivity.class);
                intent.putExtra(BoardDetailActivity.PARAM_BOARD_VO, getItem(voPosition));
                mContext.startActivity(intent);
                ((Activity)mContext).overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
            }
        });

        return convertView;
    }

    public class BoardViewHolder{
        ImageView ivBoardPhoto;
        TextView tvBoardText;
        TextView tvBoardDate;
        TextView tvReplyCnt;
    }
}
