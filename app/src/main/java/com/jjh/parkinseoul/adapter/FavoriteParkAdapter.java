package com.jjh.parkinseoul.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.jjh.parkinseoul.R;
import com.jjh.parkinseoul.vo.FavoriteVO;
import com.jjh.parkinseoul.vo.ParkVO;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.TreeSet;

import de.halfbit.pinnedsection.PinnedSectionListView;

/**
 * Created by JJH on 2016-08-22.
 * 즐겨찾는 공원 List Adpater
 */
public class FavoriteParkAdapter extends BaseAdapter{
    private Context mContext = null;
    private LayoutInflater mInflater = null;
    private List<FavoriteVO> data = new ArrayList<FavoriteVO>();

    public FavoriteParkAdapter(Context context){
        mContext = context;
        mInflater = LayoutInflater.from(mContext);
    }
    public void setData(List<FavoriteVO> data){
        this.data = data;
    }

    @Override
    public int getCount() {
        return data.size();
    }

    @Override
    public FavoriteVO getItem(int position) {
        return data.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    public void add(FavoriteVO item){
        data.add(item);
    }

    public void remove(FavoriteVO item){ data.remove(item); }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        FavoriteVO item = getItem(position);

        ParkViewHolder parkViewHolder = null;
        if(convertView == null){
            parkViewHolder = new ParkViewHolder();
            convertView = mInflater.inflate(R.layout.layout_item_park_search, null);
            parkViewHolder.ivParking = (ImageView) convertView.findViewById(R.id.ivParkImg);
            parkViewHolder.tvParkName = (TextView) convertView.findViewById(R.id.tvParkName);
            parkViewHolder.tvParkAddr = (TextView) convertView.findViewById(R.id.tvParkAddr);
            convertView.setTag(parkViewHolder);
        }else{
            parkViewHolder = (ParkViewHolder)convertView.getTag();
        }

        parkViewHolder.tvParkName.setText(item.getParkVO().getP_park());
        parkViewHolder.tvParkAddr.setText(item.getParkVO().getP_addr());
        Glide.with(mContext).load(item.getParkVO().getP_img()).into(parkViewHolder.ivParking);


        return convertView;
    }

    //공원 View Holder
    public class ParkViewHolder{
        ImageView ivParking;
        TextView tvParkName;
        TextView tvParkAddr;
    }

}
