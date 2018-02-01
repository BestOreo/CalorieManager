package com.example.nuc.caloriemanager.ui.fragment.discover;

import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;
import java.util.List;

import com.example.nuc.caloriemanager.R;
import com.example.nuc.caloriemanager.adapter.PagerAdapter;
import com.example.nuc.caloriemanager.base.MySupportFragment;
import com.example.nuc.caloriemanager.listener.OnItemClickListener;
import com.example.nuc.caloriemanager.ui.fragment.CycleFragment;


public class PagerChildFragment extends MySupportFragment {
    private static final String ARG_FROM = "arg_from";

    private int mFrom;

    private RecyclerView mRecy;
    private PagerAdapter mAdapter;

    public static PagerChildFragment newInstance(int from) {
        Bundle args = new Bundle();
        args.putInt(ARG_FROM, from);

        PagerChildFragment fragment = new PagerChildFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Bundle args = getArguments();
        if (args != null) {
            mFrom = args.getInt(ARG_FROM);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_pager, container, false);

        initView(view);

        return view;
    }

    private void initView(View view) {
        mRecy = (RecyclerView) view.findViewById(R.id.recy);

        mAdapter = new PagerAdapter(_mActivity);
        LinearLayoutManager manager = new LinearLayoutManager(_mActivity);
        mRecy.setLayoutManager(manager);
        mRecy.setAdapter(mAdapter);

        mAdapter.setOnItemClickListener(new OnItemClickListener() {
            @Override
            public void onItemClick(int position, View view) {
                Log.d("ITEMCLICK66666666",String.valueOf(mFrom));
                Log.d("Position",String.valueOf(position));
                if (getParentFragment() instanceof DiscoverFragment) {
                    ((DiscoverFragment) getParentFragment()).start(CycleFragment.newInstance(mFrom,position));
                }
            }
        });

        mRecy.post(new Runnable() {
            @Override
            public void run() {

                String[] sportsTitle = getResources().getStringArray(R.array.sports_rank_title);
                String[] sportsContent = getResources().getStringArray(R.array.sports_rank_content);
                String[] foodTitle = getResources().getStringArray(R.array.food_rank_title);
                String[] foodContent = getResources().getStringArray(R.array.food_rank_content);
                String[] nearbyTitle = getResources().getStringArray(R.array.nearby_rank_title);
                String[] nearbyContent = getResources().getStringArray(R.array.nearby_rank_content);

                // Init Datas
                List<String> items = new ArrayList<>();
                for (int i = 0; i < 13; i++) {
                    String item;
                    if (mFrom == 0) {
                        item = sportsTitle[i];
                    } else if (mFrom == 1) {
                        item = foodTitle[i];
                    } else {
                        item = nearbyTitle[i];
                    }
                    items.add(item);
                }
                mAdapter.setDatas(items);
            }
        });
    }
}
