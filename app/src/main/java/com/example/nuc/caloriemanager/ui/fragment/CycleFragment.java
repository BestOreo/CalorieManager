package com.example.nuc.caloriemanager.ui.fragment;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.nuc.caloriemanager.R;
import com.example.nuc.caloriemanager.base.BaseBackFragment;

/**
 * Created by Ge Shuaiqi on 17/9/12.
 */
public class CycleFragment extends BaseBackFragment {
    private static final String ARG_NUMBER = "arg_number";
    private static final String ARG_FORM= "arg_form";

    private Toolbar mToolbar;
    private TextView mTvName;
    private Button mBtnLastWithFinish, mBtnNextWithFinish;

    private ImageView mImage;
    private TextView mText;

    final int mTotal = 13; // 为方便暂时这样写，意思是总数13个

    private int mNumber;
    private int mForm;

    public static CycleFragment newInstance(int form, int number) {
        CycleFragment fragment = new CycleFragment();
        Bundle args = new Bundle();
        args.putInt(ARG_NUMBER, number);
        args.putInt(ARG_FORM, form);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Bundle args = getArguments();
        if (args != null) {
            mNumber = args.getInt(ARG_NUMBER);
            mForm = args.getInt(ARG_FORM);
        }
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_cycle, container, false);
        initView(view);
        return view;
    }


    // 发现栏内单元标题

    private void initView(View view) {
        mToolbar = (Toolbar) view.findViewById(R.id.toolbar);
        mTvName = (TextView) view.findViewById(R.id.tv_name);
        mBtnLastWithFinish = (Button) view.findViewById(R.id.btn_last_with_finish);
        mBtnNextWithFinish = (Button) view.findViewById(R.id.btn_next_with_finish);
        mText = (TextView)view.findViewById(R.id.contexthandle);
        mImage = (ImageView)view.findViewById(R.id.imagehandle);

        String[] sportsTitle = getResources().getStringArray(R.array.sports_rank_title);
        String[] sportsContent = getResources().getStringArray(R.array.sports_rank_content);
        String[] foodTitle = getResources().getStringArray(R.array.food_rank_title);
        String[] foodContent = getResources().getStringArray(R.array.food_rank_content);
        String[] nearbyTitle = getResources().getStringArray(R.array.nearby_rank_title);
        String[] nearbyContent = getResources().getStringArray(R.array.nearby_rank_content);

        String title;
        if(mForm == 0){
            title = sportsTitle[mNumber];
            mText.setText(sportsContent[mNumber]);

            mImage.setImageResource(R.drawable.image_dir);
            mImage.setImageLevel(100+mNumber);
        }
        else if(mForm == 1){
            title = foodTitle[mNumber];
            mText.setText(foodContent[mNumber]);

            mImage.setImageResource(R.drawable.image_dir);
            mImage.setImageLevel(200+mNumber);

        }
        else{
            mText.setText(nearbyContent[mNumber]);
            title = nearbyTitle[mNumber];

            mImage.setImageResource(R.drawable.image_dir);
            mImage.setImageLevel(300+mNumber);
        }





        mToolbar.setTitle(title);
        initToolbarNav(mToolbar);

        mTvName.setText(title);

        mBtnLastWithFinish.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startWithPop(CycleFragment.newInstance(mForm,(mNumber - 1+mTotal)%mTotal));
            }
        });

        mBtnNextWithFinish.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startWithPop(CycleFragment.newInstance(mForm,(mNumber + 1)%mTotal));
            }
        });
    }
}
