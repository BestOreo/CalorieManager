package com.example.nuc.caloriemanager.base;

import android.support.v7.widget.Toolbar;
import android.view.View;

import com.example.nuc.caloriemanager.R;

/**
 * Created by YoKeyword on 16/2/7.
 */
public class BaseBackFragment extends MySupportFragment {

    protected void initToolbarNav(Toolbar toolbar) {
        toolbar.setNavigationIcon(R.drawable.ic_arrow_back_white_24dp);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                pop();
            }
        });
    }
}
