package com.jjh.parkinseoul.fragments;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;

import com.jjh.parkinseoul.MenuActivity;
import com.jjh.parkinseoul.R;
import com.jjh.parkinseoul.adapter.ProgramSearchAdapter;
import com.jjh.parkinseoul.utils.CommonUtil;
import com.jjh.parkinseoul.utils.DisplayUtil;
import com.jjh.parkinseoul.utils.NetworkConnector;
import com.jjh.parkinseoul.vo.ProgramVO;
import com.jjh.parkinseoul.vo.response.ProgramByNameResponse;
import com.jjh.parkinseoul.vo.response.ProgramInfoResponse;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * 프로그램 검색 Fragment
 */
public class ProgramSearchFragment extends Fragment {

    private View fragmentView;
    private EditText etProgramSearch;
    private ListView listView;

    private Call<ProgramInfoResponse> call;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        fragmentView = inflater.inflate(R.layout.fragment_program_search, container, false);

        etProgramSearch = (EditText) fragmentView.findViewById(R.id.etProgramSearch);
        etProgramSearch.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                switch (actionId) {
                    case EditorInfo.IME_ACTION_SEARCH:
                        loadProgramByName(v.getText().toString());
                        break;
                }
                return true;
            }
        });
        listView = (ListView) fragmentView.findViewById(R.id.lvProgram);

        fragmentView.findViewById(R.id.btnProgramSearch).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                loadProgramByName(etProgramSearch.getText().toString());
            }
        });

        loadProgramByName("");

        return fragmentView;
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        ((MenuActivity)getActivity()).setActivityTitle("프로그램 찾기");
    }

    @Override
    public void onDetach() {
        if(call != null && call.isExecuted()) {
            call.cancel();
        }
        super.onDetach();
    }

    /**
     * 프로그램 조회 API 호출
     */
    private void loadProgramByName(String programName){
        if(call != null && call.isExecuted()) {
            call.cancel();
        }
        listView.setAdapter(null);

        ((MenuActivity)getActivity()).showMainProgress();
        call = new NetworkConnector().getProgramInfoService().getProgramInfoByName(programName);
        call.enqueue(new Callback<ProgramInfoResponse>() {
            @Override
            public void onResponse(Call<ProgramInfoResponse> call, Response<ProgramInfoResponse> response) {
                if(response.body().getProgramSearchList() != null) {
                    List<ProgramVO> list = response.body().getProgramSearchList();
                    final ProgramSearchAdapter programAdapter = new ProgramSearchAdapter(getActivity());
                    programAdapter.setData(list);

                    listView.setAdapter(programAdapter);
                    listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                        @Override
                        public void onItemClick(AdapterView<?> adapterView, View view, int position, long id) {
                            startProgramDetailFragment(programAdapter.getItem(position));
                        }
                    });
                }
                ((MenuActivity)getActivity()).hideMainProgress();
            }

            @Override
            public void onFailure(Call<ProgramInfoResponse> call, Throwable t) {
                if(!call.isCanceled()) {
                    ((MenuActivity) getActivity()).hideMainProgress();
                }
            }
        });

    }

    /**
     * 프로그램 조회 API 호출
     *//*
    private void loadProgramByName(String programName){
        if(call != null && call.isExecuted()) {
            call.cancel();
        }
        listView.setAdapter(null);
        ((MenuActivity)getActivity()).showMainProgress();
        call = new NetworkConnector().getProgramAPIService().getProgramListByName(programName);
        call.enqueue(new Callback<ProgramByNameResponse>() {
            @Override
            public void onResponse(Call<ProgramByNameResponse> call, Response<ProgramByNameResponse> response) {
                if(response.body().getProgramResponseData() != null) {
                    List<ProgramVO> list = response.body().getProgramResponseData().getProgramVOList();
                    final ProgramSearchAdapter programAdapter = new ProgramSearchAdapter(getActivity());
                    programAdapter.setData(list);

                    listView.setAdapter(programAdapter);
                    listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                        @Override
                        public void onItemClick(AdapterView<?> adapterView, View view, int position, long id) {
                            startProgramDetailFragment(programAdapter.getItem(position));
                        }
                    });
                }
                ((MenuActivity)getActivity()).hideMainProgress();
            }

            @Override
            public void onFailure(Call<ProgramByNameResponse> call, Throwable t) {
                if(!call.isCanceled()) {
                    ((MenuActivity) getActivity()).hideMainProgress();
                }
            }
        });

    }*/

    /**
     * 프로그램 상세 Fragment 호출
     */
    protected void startProgramDetailFragment(ProgramVO vo) {
        CommonUtil.hideSoftKeyboard(getActivity());
        FragmentManager fm = getFragmentManager();
        Fragment fragment = new ProgramDetailFragment();
        Bundle args = new Bundle();
        args.putSerializable(ProgramDetailFragment.PARAM_PROGRAM_VO, vo); //프로그램 VO
        args.putString(ProgramDetailFragment.PARAM_PARENT_MENU_TITLE, "프로그램 찾기");
        fragment.setArguments(args);
        getActivity().getSupportFragmentManager().beginTransaction().setCustomAnimations(R.anim.transition_enter_from_right, R.anim.transition_exit_to_right).add(R.id.main_fragment, fragment).addToBackStack(null).commit();
    }

}
