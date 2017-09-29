package com.jjh.parkinseoul.fragments;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;

import com.jjh.parkinseoul.MainActivity;
import com.jjh.parkinseoul.MenuActivity;
import com.jjh.parkinseoul.R;
import com.jjh.parkinseoul.adapter.FavoriteParkAdapter;
import com.jjh.parkinseoul.utils.BusProvider;
import com.jjh.parkinseoul.utils.DisplayUtil;
import com.jjh.parkinseoul.vo.ActivityResultEvent;
import com.jjh.parkinseoul.vo.FavoriteVO;
import com.jjh.parkinseoul.vo.ParkVO;
import com.squareup.otto.Subscribe;

import java.util.List;


/**
 * 즐겨찾기 Fragment
 */
public class FavoriteParkFragment extends Fragment {

    private View fragmentView;
    private ListView lvFavoriteList;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        fragmentView = inflater.inflate(R.layout.fragment_favorite_park, container, false);

        lvFavoriteList = (ListView) fragmentView.findViewById(R.id.lvFavoriteList);

        initFavoriteList();
        return fragmentView;
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);

        BusProvider.getInstance().register(this);

        ((MenuActivity)getActivity()).setActivityTitle("즐겨찾는 공원");
    }

    @Override
    public void onDetach() {
        BusProvider.getInstance().unregister(this);
        super.onDetach();
    }

    @Subscribe
    public void onBusEvent(Boolean isChanged){
        if(isChanged) {
            initFavoriteList();
        }
    }

    private void initFavoriteList(){
        List<FavoriteVO> list = ((MenuActivity)getActivity()).getDatabaseHelper().selectFavoriteParkList();

        if(list.size() > 0) {
            final FavoriteParkAdapter adapter = new FavoriteParkAdapter(getActivity());
            adapter.setData(list);

            lvFavoriteList.setAdapter(adapter);

            lvFavoriteList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                    ParkVO vo = adapter.getItem(position).getParkVO();
                    startParkDetailFragment(vo.getP_idx(), vo.getP_park());
                }
            });
            lvFavoriteList.setVisibility(View.VISIBLE);
            fragmentView.findViewById(R.id.tvNoFavoritePark).setVisibility(View.GONE);
        }else{
            lvFavoriteList.setVisibility(View.GONE);
            fragmentView.findViewById(R.id.tvNoFavoritePark).setVisibility(View.VISIBLE);
        }
    }

    /**
     * 공원상세 Fragment 호출
     */
    protected void startParkDetailFragment(int parkNum, String parkName) {
        FragmentManager fm = getFragmentManager();
        Fragment fragment = new ParkDetailFragment();
        Bundle args = new Bundle();
        args.putInt(ParkDetailFragment.PARAM_PARK_NUM, parkNum); //공원번호
        args.putString(ParkDetailFragment.PARAM_PARK_NAME, parkName); //공원명
        args.putString(ParkDetailFragment.PARAM_PARENT_MENU_TITLE,"즐겨찾는 공원"); //Return시 표시할 상단 Title명
        fragment.setArguments(args);
        getActivity().getSupportFragmentManager().beginTransaction().setCustomAnimations(R.anim.transition_enter_from_right, R.anim.transition_exit_to_right).add(R.id.main_fragment, fragment).addToBackStack(null).commit();
    }

}
