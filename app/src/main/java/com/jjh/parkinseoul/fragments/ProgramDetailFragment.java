package com.jjh.parkinseoul.fragments;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.jjh.parkinseoul.MenuActivity;
import com.jjh.parkinseoul.R;
import com.jjh.parkinseoul.utils.CommonUtil;
import com.jjh.parkinseoul.utils.DisplayUtil;
import com.jjh.parkinseoul.utils.NetworkConnector;
import com.jjh.parkinseoul.vo.ParkVO;
import com.jjh.parkinseoul.vo.ProgramVO;
import com.jjh.parkinseoul.vo.response.BoardAddResponse;
import com.jjh.parkinseoul.vo.response.ProgramInfoResponse;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * 프로그램 상세 Fragment
 */
public class ProgramDetailFragment extends Fragment {

    public static final String PARAM_PROGRAM_VO = "param_program_vo";
    public static final String PARAM_PARENT_MENU_TITLE = "param_parent_menu_title";
    public static final String PARAM_IS_FROM_PARK_DETAIL = "param_is_from_park_detail";

    private View fragmentView;

    private ProgramVO programVo;

    private boolean isFromParkDetail = false; //공원 상세에서 온경우 true

    private Call<ProgramInfoResponse> programInfoCall;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        fragmentView = inflater.inflate(R.layout.fragment_program_detail, container, false);
        initView();
        return fragmentView;
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if(getArguments().getSerializable(PARAM_PROGRAM_VO) != null){
            programVo = (ProgramVO)getArguments().getSerializable(PARAM_PROGRAM_VO);
            isFromParkDetail = getArguments().getBoolean(PARAM_IS_FROM_PARK_DETAIL,false);
            ((MenuActivity)getActivity()).setActivityTitle("프로그램 정보"); //상단 타이틀 표시
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        //이전메뉴로 상단 타이틀 표시
        ((MenuActivity)getActivity()).setActivityTitle(getArguments().getString(PARAM_PARENT_MENU_TITLE));
    }

    private void initView(){
        if(programVo == null){
            return;
        }

        //프로그램 정보
        if(DisplayUtil.isEmptyStr(programVo.getP_content())){
            fragmentView.findViewById(R.id.tvNoProgramInfo).setVisibility(View.VISIBLE);
        }else {
            ((TextView) fragmentView.findViewById(R.id.tvProgramContent)).setText(programVo.getP_content());
        }

        ((TextView)fragmentView.findViewById(R.id.tvProgramName)).setText(programVo.getP_name());
        ((TextView)fragmentView.findViewById(R.id.tvProgramDate)).setText(programVo.getP_eduday_s() + " ~ " + programVo.getP_eduday_e());
        ((TextView)fragmentView.findViewById(R.id.tvProgramDay)).setText(programVo.getP_proday());
        ((TextView)fragmentView.findViewById(R.id.tvProgramTime)).setText(programVo.getP_edutime());
        ((TextView)fragmentView.findViewById(R.id.tvProgramPeople)).setText("최대 " + programVo.getP_eamax() + "명");
        ((TextView)fragmentView.findViewById(R.id.tvProgramTarget)).setText(programVo.getP_eduperson());

        loadParkInfo();
    }

    private void loadParkInfo(){
        try {
            ParkVO parkVo = ((MenuActivity)getActivity()).getDatabaseHelper().selectParkDetail(Integer.parseInt(programVo.getP_idx()));

            Glide.with(getActivity()).load(parkVo.getP_img()).into((ImageView) fragmentView.findViewById(R.id.ivParkImg)); //공원 이미지
            ((TextView)fragmentView.findViewById(R.id.tvProgramParkName)).setText(parkVo.getP_park());
            ((TextView)fragmentView.findViewById(R.id.tvProgramParkAddr)).setText(parkVo.getP_addr());

            final String parmName = parkVo.getP_park();
            if(isFromParkDetail){
                fragmentView.findViewById(R.id.btnParkInfo).setVisibility(View.GONE);
            }else{
                fragmentView.findViewById(R.id.btnParkInfo).setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        startParkDetailFragment(Integer.parseInt(programVo.getP_idx()), parmName);
                    }
                });
            }

        }catch(Exception e){
            e.printStackTrace();
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
        args.putString(ParkDetailFragment.PARAM_PARENT_MENU_TITLE, "프로그램 정보"); //Return시 표시할 상단 Title명
        fragment.setArguments(args);
        getActivity().getSupportFragmentManager().beginTransaction().setCustomAnimations(R.anim.transition_enter_from_right, R.anim.transition_exit_to_right).add(R.id.main_fragment, fragment).addToBackStack(null).commit();
    }
}
