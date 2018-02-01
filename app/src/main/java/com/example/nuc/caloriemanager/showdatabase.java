package com.example.nuc.caloriemanager;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.method.ScrollingMovementMethod;
import android.widget.TextView;

import java.util.ArrayList;

/**
 * Created by zf on 2017/11/20.
 */

public class showdatabase extends AppCompatActivity {
    TextView mInfo;
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.showdatabase);

        mInfo=(TextView)findViewById(R.id.show);
        mInfo.setMovementMethod(ScrollingMovementMethod.getInstance());
        ArrayList<String> a=Database.db.show();
        String s="";
        s+=Database.db.Select_today()+"\n";
        for(int i=0;i<a.size();i++)
            s+=a.get(i)+"\n";
        mInfo.setText(s);
    }
}
