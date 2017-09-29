package com.jjh.parkinseoul.adapter;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.drawable.GradientDrawable;
import android.graphics.drawable.ShapeDrawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.jjh.parkinseoul.R;
import com.jjh.parkinseoul.vo.ParkVO;
import com.jjh.parkinseoul.vo.ProgramVO;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import de.halfbit.pinnedsection.PinnedSectionListView;

/**
 * Created by JJH on 2016-08-22.
 * 프로그램 검색 List Adpater
 */
public class ProgramSearchAdapter extends BaseAdapter {
    private Context mContext = null;
    private LayoutInflater mInflater = null;
    private List<ProgramVO> data = new ArrayList<ProgramVO>();

    public ProgramSearchAdapter(Context context){
        mContext = context;
        mInflater = LayoutInflater.from(mContext);
    }
    public void setData(List<ProgramVO> data){
        this.data = data;
    }

    public void addData(List<ProgramVO> data){
        this.data.addAll(data);
    }

    public void add(ProgramVO item){
        data.add(item);
    }
    @Override
    public int getCount() {
        return data.size();
    }

    @Override
    public ProgramVO getItem(int position) {
        return data.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    public void remove(ProgramVO item){ data.remove(item); }

    public void clear(){
        data = new ArrayList<ProgramVO>();
    }


    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ProgramVO item = getItem(position);

        ProgramViewHolder programViewHolder = null;
        if(convertView == null) {
            programViewHolder = new ProgramViewHolder();
            convertView = mInflater.inflate(R.layout.layout_item_program_search, null);
            programViewHolder.tvProgramName = (TextView) convertView.findViewById(R.id.tvProgramName);
            programViewHolder.tvParkName = (TextView) convertView.findViewById(R.id.tvParkName);
            programViewHolder.tvProgramDate = (TextView) convertView.findViewById(R.id.tvProgramDate);
            programViewHolder.tvProgramTime = (TextView) convertView.findViewById(R.id.tvProgramTime);
            programViewHolder.tvTargetAge = (TextView) convertView.findViewById(R.id.tvTargetAge);

            convertView.setTag(programViewHolder);
        }else{
            programViewHolder = (ProgramViewHolder) convertView.getTag();
        }

        programViewHolder.tvProgramName.setText(item.getP_name());
        programViewHolder.tvParkName.setText(item.getP_park());
        programViewHolder.tvProgramDate.setText(item.getP_eduday_s() + " ~ " + item.getP_eduday_e());
        programViewHolder.tvProgramTime.setText(item.getP_proday() + " | " + item.getP_edutime());

        GradientDrawable drawble = (GradientDrawable) programViewHolder.tvTargetAge.getBackground();

        if(item.getP_eduperson() != null) {
            if (item.getP_eduperson().contains("유아")) {
                programViewHolder.tvTargetAge.setCompoundDrawablesWithIntrinsicBounds(R.drawable.age_icon_baby, 0, 0, 0);
                programViewHolder.tvTargetAge.setText("유아");
                drawble.setColor(Color.parseColor("#FFE400"));
            } else if (item.getP_eduperson().contains("어린이")) {
                programViewHolder.tvTargetAge.setCompoundDrawablesWithIntrinsicBounds(R.drawable.age_icon_child, 0, 0, 0);
                programViewHolder.tvTargetAge.setText("어린이");
                drawble.setColor(Color.parseColor("#47C83E"));
            }else if (item.getP_eduperson().contains("청소년")) {
                programViewHolder.tvTargetAge.setCompoundDrawablesWithIntrinsicBounds(R.drawable.age_icon_teenager, 0, 0, 0);
                programViewHolder.tvTargetAge.setText("청소년");
                drawble.setColor(Color.parseColor("#4374D9"));
            } else if (item.getP_eduperson().contains("성인")) {
                programViewHolder.tvTargetAge.setCompoundDrawablesWithIntrinsicBounds(R.drawable.age_icon_adult, 0, 0, 0);
                programViewHolder.tvTargetAge.setText("성인");
                drawble.setColor(Color.parseColor("#FF5A5A"));
            } else{
                programViewHolder.tvTargetAge.setCompoundDrawablesWithIntrinsicBounds(R.drawable.age_icon_else, 0, 0, 0);
                programViewHolder.tvTargetAge.setText(item.getP_eduperson());
                drawble.setColor(Color.parseColor("#8041D9"));
            }
        }
        return convertView;
    }

    public class ProgramViewHolder{
        TextView tvProgramName;
        TextView tvParkName;
        TextView tvProgramDate;
        TextView tvProgramTime;
        TextView tvTargetAge;
    }
}
