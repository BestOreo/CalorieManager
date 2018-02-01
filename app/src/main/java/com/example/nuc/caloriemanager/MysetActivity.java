package com.example.nuc.caloriemanager;

import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

public class MysetActivity extends AppCompatActivity {

    static public int mheight = 0;
    static public int mweight = 0;
    static public int mtarget_calorie = 0;

    Button mSetbutton;
    private EditText mEtHeight, mEtWeight, mEtTargetCalorie;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_myset);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        initView();


        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });
    }

    private void initView() {
        mSetbutton = (Button)findViewById(R.id.btn_set);
        mEtHeight = (EditText)findViewById(R.id.nheight);
        mEtWeight = (EditText)findViewById(R.id.nweight);
        mEtTargetCalorie = (EditText)findViewById(R.id.target_calorie);

        mEtHeight.setText(String.valueOf(mheight));
        mEtWeight.setText(String.valueOf(mweight));
        mEtTargetCalorie.setText(String.valueOf(mtarget_calorie));


        mSetbutton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mheight = Integer.parseInt(mEtHeight.getText().toString(),10);
                mweight = Integer.parseInt(mEtWeight.getText().toString(),10);
                mtarget_calorie = Integer.parseInt(mEtTargetCalorie.getText().toString(),10);
            }
        });
    }

}
