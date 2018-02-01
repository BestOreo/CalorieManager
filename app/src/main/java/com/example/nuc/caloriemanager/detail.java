package com.example.nuc.caloriemanager;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.media.ThumbnailUtils;
import android.os.Bundle;
import android.os.Environment;
import android.os.StrictMode;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.nuc.caloriemanager.adapter.HomeAdapter;

import java.io.FileInputStream;
import java.io.FileNotFoundException;

/**
 * Created by zf on 2017/11/3.
 */

public class detail extends AppCompatActivity {
    protected static String[] info;
    public static Bitmap mbitmap;

    private TextView mChoice1,mChoice2,mChoice3;
    private ImageView mpic ;

    private  ListView mListView;

    public detail(){};
//    public detail(Bitmap picture,String[] information)
//    {
//        pic=picture;
//        info=information;
//    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.detail);
        if (android.os.Build.VERSION.SDK_INT > 9) {
            StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
            StrictMode.setThreadPolicy(policy);
        }
        info=getIntent().getStringArrayExtra("info");//这是原来的数据，现在除了问题，你就按照这个合起来就好了，
        String mfilepath=getIntent().getStringExtra("mfilepath");
        FileInputStream fis = null; // 根据路径获取数据
        try {
            fis = new FileInputStream(mfilepath);
            Bitmap bitmap = BitmapFactory.decodeStream(fis);
            Matrix m=new Matrix();
            m.postRotate(90);
            WindowManager wm = this.getWindowManager();
            int width = wm.getDefaultDisplay().getWidth();
            int height = wm.getDefaultDisplay().getHeight();
            int picwidth= (int) (width);
            int picheight= (int) (height*0.4);
            Bitmap xbitmap=Bitmap.createBitmap(bitmap,0,0,bitmap.getWidth(),bitmap.getHeight(),m,true);
            Bitmap cropped = ThumbnailUtils.extractThumbnail(xbitmap, picwidth, picheight);
            mpic=(ImageView)findViewById(R.id.mpic);
            mpic.setImageBitmap(cropped);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }

//        mChoice1=(TextView)findViewById(R.id.mchoice1);
//        mChoice2=(TextView)findViewById(R.id.mchoice2);
//        mChoice3=(TextView)findViewById(R.id.mchoice3);
//        mChoice1.setText(info[0]);
//        mChoice2.setText(info[1]);
//        mChoice3.setText(info[2]);


        mListView = (ListView)findViewById(R.id.listView_food);
        mListView.setAdapter(new ArrayAdapter<String>(this,
                android.R.layout.simple_list_item_1,
                info));
        mListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                String []s=info[i].split("\t");
                if(s.length==4)
                {
                    Database.db.insert(s[0], s[3]);
                    Toast.makeText(detail.this, "成功选择，已经添加到您的卡路里账单", Toast.LENGTH_SHORT).show();
                    HomeAdapter.setCalorie();
                }
                else
                    Toast.makeText(detail.this, "请等待" + i, Toast.LENGTH_SHORT).show();
            }
        });
    }



}

