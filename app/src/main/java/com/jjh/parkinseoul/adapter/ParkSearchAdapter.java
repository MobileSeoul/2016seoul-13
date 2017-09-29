package com.jjh.parkinseoul.adapter;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.jjh.parkinseoul.R;
import com.jjh.parkinseoul.vo.ParkVO;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
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
 * 공원 검색 List Adpater
 */
public class ParkSearchAdapter extends BaseAdapter implements PinnedSectionListView.PinnedSectionListAdapter  {
    private static final int[] COLORS = new int[] {
            R.color.green_light, R.color.orange_light,
            R.color.blue_light, R.color.red_light };
    public static final int TYPE_ITEM = 0;
    public  static final int TYPE_SECTION = 1;
    String[] region = { "강남구", "강동구", "강북구", "강서구", "관악구", "광진구", "구로구", "금천구", "노원구", "도봉구", "동대문구", "동작구",
                         "마포구", "서대문구", "서초구", "성동구", "성북구", "송파구", "양천구", "영등포구", "용산구", "은평구", "종로구", "중구", "중랑구"};
    private Context mContext = null;
    private LayoutInflater mInflater = null;
    private List<ParkVOItem> data = new ArrayList<ParkVOItem>();

    public ParkSearchAdapter(Context context){
        mContext = context;
        mInflater = LayoutInflater.from(mContext);
    }
    public void setData(List<ParkVO> data){
        generateData(data);
    }

    @Override
    public int getCount() {
        return data.size();
    }

    @Override
    public ParkVOItem getItem(int position) {
        return data.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    public void add(ParkVOItem item){
        data.add(item);
    }

    public void remove(ParkVOItem item){ data.remove(item); }

    private void generateData(List<ParkVO> data){

        Map<String, List<ParkVO>> map = new HashMap<String,List<ParkVO>>();
        List<ParkVO> etcList = new ArrayList<ParkVO>();
        int sectionCount = 0;
        for(ParkVO vo : data){
            String itemGu = vo.getP_addr().replaceAll("\\(?\\d{3}-?\\d{3}\\)?","").replaceAll("서울|([\\S]+(도|시))","").replaceAll(",","").trim().split(" ")[0];
            if(Arrays.asList(region).contains(itemGu)){
                if(map.keySet().contains(itemGu)){
                    map.get(itemGu).add(vo);
                }else{
                    List<ParkVO> itemList = new ArrayList<ParkVO>();
                    itemList.add(vo);
                    map.put(itemGu, itemList);
                }
            }else{
                etcList.add(vo);
            }

        }

        Iterator<String> keyIter = new TreeSet<String>(map.keySet()).iterator();

        while(keyIter.hasNext()){
            String key = keyIter.next();
            List<ParkVO> itemList = map.get(key);
            ParkVOItem sectionItem = new ParkVOItem();
            sectionItem.setType(TYPE_SECTION);
            sectionItem.setSectionName(key);
            sectionItem.setSectionPosition(sectionCount++);
            add(sectionItem);
            for(ParkVO vo : itemList){
                ParkVOItem item = new ParkVOItem();
                item.setType(TYPE_ITEM);
                item.setParkVo(vo);
                add(item);
            }
        }

        if(etcList.size() > 0){
            ParkVOItem sectionItem = new ParkVOItem();
            sectionItem.setType(TYPE_SECTION);
            sectionItem.setSectionName("기타");
            sectionItem.setSectionPosition(sectionCount++);
            add(sectionItem);
            for(ParkVO vo : etcList){
                ParkVOItem item = new ParkVOItem();
                item.setType(TYPE_ITEM);
                item.setParkVo(vo);
                add(item);
            }

        }

    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ParkVOItem item = getItem(position);

        if(item.getType() == TYPE_SECTION){ //구 표시
            SectionViewHolder sectionViewHolder = null;
            if(convertView == null) {
                sectionViewHolder = new SectionViewHolder();
                convertView = mInflater.inflate(R.layout.layout_item_park_search_section, null);
                sectionViewHolder.tvSectionName = (TextView) convertView.findViewById(R.id.tvSectionName);
                convertView.setTag(sectionViewHolder);
            }else{
                sectionViewHolder = (SectionViewHolder) convertView.getTag();
            }
            sectionViewHolder.tvSectionName.setText(item.getSectionName());
            convertView.setBackgroundColor(mContext.getResources().getColor(COLORS[item.getSectionPosition() % COLORS.length]));
        }else { //공원 표시

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
            parkViewHolder.tvParkName.setText(item.getParkVo().getP_park());
            parkViewHolder.tvParkAddr.setText(item.getParkVo().getP_addr());
            Glide.with(mContext).load(item.getParkVo().getP_img()).into(parkViewHolder.ivParking);

        }

        return convertView;
    }

    @Override
    public int getViewTypeCount() {
        return 2;
    }
    @Override
    public int getItemViewType(int position) {
        return getItem(position).getType();
    }
    @Override
    public boolean isItemViewTypePinned(int viewType) {
        return viewType == TYPE_SECTION;
    }

    //구 View Holder
    public class SectionViewHolder{
        TextView tvSectionName;
    }

    //공원 View Holder
    public class ParkViewHolder{
        ImageView ivParking;
        TextView tvParkName;
        TextView tvParkAddr;
    }

    /**
     * Adapter에서 사용할 ParkVO
     */
    public class ParkVOItem{
        private int type;
        private int sectionPosition;
        private String sectionName;

        private ParkVO parkVo;

        public int getType() { return type; }
        public void setType(int type) { this.type = type; }
        public int getSectionPosition() { return sectionPosition; }
        public void setSectionPosition(int sectionPosition) { this.sectionPosition = sectionPosition; }
        public String getSectionName() { return sectionName; }
        public void setSectionName(String sectionName) { this.sectionName = sectionName; }
        public ParkVO getParkVo() { return parkVo; }
        public void setParkVo(ParkVO parkVo) { this.parkVo = parkVo; }
    }
}
