package com.jjh.parkinseoul.fragments;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;

import com.jjh.parkinseoul.MenuActivity;
import com.jjh.parkinseoul.R;
import com.jjh.parkinseoul.adapter.ParkSearchAdapter;
import com.jjh.parkinseoul.utils.CommonUtil;
import com.jjh.parkinseoul.utils.DisplayUtil;
import com.jjh.parkinseoul.vo.ParkVO;
import com.jjh.parkinseoul.vo.response.ParkByNameResponse;

import java.util.List;

import de.halfbit.pinnedsection.PinnedSectionListView;
import retrofit2.Call;

/**
 * 공원 찾기 Fragment
 */
public class ParkSearchFragment extends Fragment implements View.OnClickListener{

    private View fragmentView;
    private PinnedSectionListView listView; //공원목록 ListView
    private Button btnToggleParkNm; //공원명
    private Button btnToggleParkAddr;//주소
    private EditText etParkSearch;//검색어 EditText

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        fragmentView = inflater.inflate(R.layout.fragment_park_search, container, false);
        listView   = (PinnedSectionListView) fragmentView.findViewById(R.id.listView);
        btnToggleParkNm = (Button) fragmentView.findViewById(R.id.btnToggleParkNm);
        btnToggleParkAddr = (Button) fragmentView.findViewById(R.id.btnToggleParkAddr);
        etParkSearch = (EditText) fragmentView.findViewById(R.id.etParkSearch);

        btnToggleParkNm.setOnClickListener(this);
        btnToggleParkAddr.setOnClickListener(this);

        btnToggleParkNm.setSelected(true);

        etParkSearch.addTextChangedListener(new TextWatcher() {
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if(s != null){
                    if(btnToggleParkNm.isSelected()) { //공원명으로 검색
                        initParkListByName(s.toString());
                    }else{ //주소로 검색
                        initParkListByAddr(s.toString());
                    }
                }
            }
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
            @Override
            public void afterTextChanged(Editable s) {}
        });
        initParkListByName("");

        return fragmentView;
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        ((MenuActivity)getActivity()).setActivityTitle("공원 찾기"); //상단 타이틀 변경
    }

    /**
     * 공원명으로 조회 From DB
     */
    private void initParkListByName(String parkName){
        ((MenuActivity)getActivity()).showMainProgress();
        List<ParkVO> list = ((MenuActivity)getActivity()).getDatabaseHelper().selectParkListByName(parkName);
        initList(list);
        ((MenuActivity)getActivity()).hideMainProgress();
    }

    /**
     * 주소로 조회 From DB
     */
    private void initParkListByAddr(String parkAddr){
        ((MenuActivity)getActivity()).showMainProgress();
        List<ParkVO> list = ((MenuActivity)getActivity()).getDatabaseHelper().selectParkListByAddr(parkAddr);
        initList(list);
        ((MenuActivity)getActivity()).hideMainProgress();
    }

    /**
     * 공원목록 List 데이터 셋팅
     */
    private void initList(List<ParkVO> list){
        ParkSearchAdapter arrayAdapter = new ParkSearchAdapter(getActivity());
        arrayAdapter.setData(list);
        listView.setAdapter(arrayAdapter);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long id) {
                ParkSearchAdapter.ParkVOItem item = ((ParkSearchAdapter.ParkVOItem)adapterView.getItemAtPosition(position));
                if(item.getType() == ParkSearchAdapter.TYPE_ITEM) {
                    startParkDetailFragment(item.getParkVo().getP_idx(), item.getParkVo().getP_park());
                }
            }
        });
    }


    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.btnToggleParkNm: //공원명 Toggle
            case R.id.btnToggleParkAddr://주소 Toggle
                btnToggleParkNm.setSelected(!btnToggleParkNm.isSelected());
                btnToggleParkAddr.setSelected(!btnToggleParkAddr.isSelected());
                etParkSearch.setText("");
                if(btnToggleParkNm.isSelected()){
                    etParkSearch.setHint("공원명을 입력 해 주세요.");
                }else{
                    etParkSearch.setHint("공원주소를 입력 해 주세요.");
                }
                break;
        }
    }

    /**
     * 공원상세 Fragment 호출
     */
    protected void startParkDetailFragment(int parkNum, String parkName) {
        CommonUtil.hideSoftKeyboard(getActivity());
        FragmentManager fm = getFragmentManager();
        Fragment fragment = new ParkDetailFragment();
        Bundle args = new Bundle();
        args.putInt(ParkDetailFragment.PARAM_PARK_NUM, parkNum); //공원번호
        args.putString(ParkDetailFragment.PARAM_PARK_NAME, parkName); //공원명
        args.putString(ParkDetailFragment.PARAM_PARENT_MENU_TITLE,"공원 찾기"); //Return시 표시할 상단 Title명
        fragment.setArguments(args);
        getActivity().getSupportFragmentManager().beginTransaction().setCustomAnimations(R.anim.transition_enter_from_right, R.anim.transition_exit_to_right).add(R.id.main_fragment, fragment).addToBackStack(null).commit();
    }

}
