package com.example.nuc.caloriemanager;

import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.ThumbnailUtils;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.preference.PreferenceManager;
import android.provider.MediaStore;
import android.support.design.widget.NavigationView;
import android.support.v4.content.FileProvider;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import me.yokeyword.fragmentation.ISupportFragment;
import me.yokeyword.fragmentation.SupportFragment;
import me.yokeyword.fragmentation.anim.FragmentAnimator;
import com.example.nuc.caloriemanager.R;
import com.example.nuc.caloriemanager.base.BaseMainFragment;
import com.example.nuc.caloriemanager.base.MySupportActivity;
import com.example.nuc.caloriemanager.base.MySupportFragment;
import com.example.nuc.caloriemanager.ui.fragment.account.LoginFragment;
import com.example.nuc.caloriemanager.ui.fragment.discover.DiscoverFragment;
import com.example.nuc.caloriemanager.ui.fragment.home.HomeFragment;
import com.example.nuc.caloriemanager.ui.fragment.shop.ShopFragment;

import org.json.JSONException;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * 流程式demo  tip: 多使用右上角的"查看栈视图"
 * Created by YoKeyword on 16/1/29.
 */
public class MainActivity extends MySupportActivity
        implements NavigationView.OnNavigationItemSelectedListener, BaseMainFragment.OnFragmentOpenDrawerListener
        , LoginFragment.OnLoginSuccessListener{
    public static final String TAG = MainActivity.class.getSimpleName();

    // 再点一次退出程序时间设置
    private static final long WAIT_TIME = 2000L;
    private long TOUCH_TIME = 0;
    static final int REQUEST_IMAGE_CAPTURE = 1;

    public static Bitmap mBitmap;
    private String mFilePath;
    private DrawerLayout mDrawer;
    private NavigationView mNavigationView;
    public String []data={"please waiting choice1","please waiting choice2","please waiting choice3"};
    private TextView mTvName;   // NavigationView上的名字
    private ImageView mImgNav;  // NavigationView上的头像
    public Button mbtn;
    public String response;

    public MenuItem msyscamera;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        MySupportFragment fragment = findFragment(HomeFragment.class);
        if (fragment == null) {
            loadRootFragment(R.id.fl_container, HomeFragment.newInstance());
        }

        Database.db=new Database(this,"CalorieManager",null,1);
        Database.db.getWritableDatabase();
        Database.db.delete();
        Database.db.insert_demo();

        initView();
    }


    /**
     * 设置动画，也可以使用setFragmentAnimator()设置
     */
    @Override
    public FragmentAnimator onCreateFragmentAnimator() {
        // 设置默认Fragment动画  默认竖向(和安卓5.0以上的动画相同)
        return super.onCreateFragmentAnimator();
        // 设置横向(和安卓4.x动画相同)
//        return new DefaultHorizontalAnimator();
        // 设置自定义动画
//        return new FragmentAnimator(enter,exit,popEnter,popExit);
    }

    private void initView() {
        mDrawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, mDrawer, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
//        mDrawer.setDrawerListener(toggle);
        toggle.syncState();

        mNavigationView = (NavigationView) findViewById(R.id.nav_view);
        mNavigationView.setNavigationItemSelectedListener(this);
        mNavigationView.setCheckedItem(R.id.nav_home);

        LinearLayout llNavHeader = (LinearLayout) mNavigationView.getHeaderView(0);
        mTvName = (TextView) llNavHeader.findViewById(R.id.tv_name);
        mImgNav = (ImageView) llNavHeader.findViewById(R.id.img_nav);




        llNavHeader.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mDrawer.closeDrawer(GravityCompat.START);
                mDrawer.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        goLogin();
                    }
                }, 250);
            }
        });
        mFilePath= Environment.getExternalStorageDirectory().getPath();
        mFilePath=mFilePath+"/temp.jpeg";
    }




    @Override
    public void onBackPressedSupport() {
        if (mDrawer.isDrawerOpen(GravityCompat.START)) {
            mDrawer.closeDrawer(GravityCompat.START);
        } else {
            ISupportFragment topFragment = getTopFragment();

            // 主页的Fragment
            if (topFragment instanceof BaseMainFragment) {
                mNavigationView.setCheckedItem(R.id.nav_home);
            }

            if (getSupportFragmentManager().getBackStackEntryCount() > 1) {
                pop();
            } else {
                if (System.currentTimeMillis() - TOUCH_TIME < WAIT_TIME) {
                    finish();
                } else {
                    TOUCH_TIME = System.currentTimeMillis();
                    Toast.makeText(this, R.string.press_again_exit, Toast.LENGTH_SHORT).show();
                }
            }
        }
    }

    /**
     * 打开抽屉
     */
    @Override
    public void onOpenDrawer() {
        if (!mDrawer.isDrawerOpen(GravityCompat.START)) {
            mDrawer.openDrawer(GravityCompat.START);
        }
    }

    @Override
    public boolean onNavigationItemSelected(final MenuItem item) {
        mDrawer.closeDrawer(GravityCompat.START);

        mDrawer.postDelayed(new Runnable() {
            @Override
            public void run() {
                int id = item.getItemId();

                final ISupportFragment topFragment = getTopFragment();

                if (id == R.id.nav_home) {

                    HomeFragment fragment = findFragment(HomeFragment.class);
                    Bundle newBundle = new Bundle();
                    newBundle.putString("from", "From:" + topFragment.getClass().getSimpleName());
                    fragment.putNewBundle(newBundle);

                    start(fragment, SupportFragment.SINGLETASK);
                } else if (id == R.id.nav_discover) {
                    DiscoverFragment fragment = findFragment(DiscoverFragment.class);
                    if (fragment == null) {
                        popTo(HomeFragment.class, false, new Runnable() {
                            @Override
                            public void run() {
                                start(DiscoverFragment.newInstance());
                            }
                        });
                    } else {
                        // 如果已经在栈内,则以SingleTask模式start
                        start(fragment, SupportFragment.SINGLETASK);
                    }
                } else if (id == R.id.nav_statistic) { // 统计结果
                    Intent i= new Intent(MainActivity.this,Statistic.class);
                    startActivity(i);

                } else if (id == R.id.nav_login) {
                    goLogin();
                } else if (id == R.id.nav_takepic) {
                    //startActivity(new Intent(MainActivity.this, SwipeBackSampleActivity.class));
                    openCamera();
                }
                else if (id == R.id.nav_seeresult) {
                    Intent i= new Intent(MainActivity.this,detail.class);
                    i.putExtra("info",data);
                    i.putExtra("mfilepath",mFilePath);
                    startActivity(i);
                }
                else if(id == R.id.nav_set){
                    Toast.makeText(MainActivity.this, "SET", Toast.LENGTH_LONG).show();
                    startActivity(new Intent(MainActivity.this, MysetActivity.class));
                }
//                else if (id == R.id.nav_showdatabase){
//                    Intent i=new Intent(MainActivity.this,showdatabase.class);
//                    startActivity(i);
//                }
            }
        }, 300);




        return true;
    }

    private void goLogin() {
        start(LoginFragment.newInstance());
    }



    @Override
    public void onLoginSuccess(String account) {
        mTvName.setText(account);
        mImgNav.setImageResource(R.drawable.ic_account_circle_white_48dp);
        Toast.makeText(this, R.string.sign_in_success, Toast.LENGTH_SHORT).show();
    }


    public void openCamera() {
        /*
        Intent i = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);// 启动系统相机
        Uri photoUri = Uri.fromFile(new File(mFilePath)); // 传递路径
        i.putExtra(MediaStore.EXTRA_OUTPUT, photoUri);// 更改系统默认存储路径
        startActivityForResult(i,1);
        */

        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        if (takePictureIntent.resolveActivity(getPackageManager()) != null) {
            File photoFile = null;
            try {
                photoFile = createImageFile();
            } catch (IOException ex) {
                // Error occurred while creating the File
                Log.e("FoodRecognitionExample", ex.getMessage(), ex);
                // TODO: return and toast
            }
            // Continue only if the File was successfully created
            if (photoFile != null) {
//                Uri photoUri = Uri.fromFile(new File(mFilePath));
                Uri photoURI = FileProvider.getUriForFile(this, "com.example.nuc.caloriemanager.fileprovider", photoFile);
                takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoURI);
                startActivityForResult(takePictureIntent, REQUEST_IMAGE_CAPTURE);
            }
        }

    }

    private File createImageFile() throws IOException {
        // Create an image file name
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        String imageFileName = "JPEG_" + timeStamp + "_";
        File storageDir = getExternalFilesDir(Environment.DIRECTORY_PICTURES);
        File image = File.createTempFile(
                imageFileName,  /* prefix */
                ".jpg",         /* suffix */
                storageDir      /* directory */
        );

        // Save a file: path for use with ACTION_VIEW intents
        mFilePath = image.getAbsolutePath();
        return image;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data)
    {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK) { // 如果返回数据
            if (requestCode == 1) {
                FileInputStream fis = null;
                try {
                    fis = new FileInputStream(mFilePath); // 根据路径获取数据
                    Bitmap bitmap = BitmapFactory.decodeStream(fis);
                    Bitmap cropped = ThumbnailUtils.extractThumbnail(bitmap, 544, 544);
                    mBitmap=cropped;
                    FoodTask f= new FoodTask("7601dc4ce3192d433ba40e7492069677",cropped);

                    new FoodRecognitionTask().execute(f);
                }
                catch (FileNotFoundException e) {
                    e.printStackTrace();
                } finally {
                    try {
                        fis.close();// 关闭流
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }
        }
    }


    class Trans extends AsyncTask<String,Void,String>{

        private ProgressDialog progressDialog;
        @Override
        protected void onPreExecute(){
            super.onPreExecute();
            progressDialog = ProgressDialog.show(MainActivity.this,"请等待", "食物识别完毕，正在翻译中");
        }
        @Override
        protected String doInBackground(String...params) {
            MyObject v=new MyObject();
            try {
                data=v.calorieCoubt(response);
//                mjson.setText(data[0]+"\n"+data[1]+"\n"+data[2]);
//
            } catch (JSONException e) {
//                mjson.setText(e.toString());
            }
            return null;
        }

        @Override
        protected void onPostExecute(String result){
            super.onPostExecute(result);
            progressDialog.dismiss();
            Intent i= new Intent(MainActivity.this,detail.class);
            i.putExtra("info",data);
            i.putExtra("mfilepath",mFilePath);
            startActivity(i);
        }

    }
    class FoodRecognitionTask extends AsyncTask<FoodTask,Void,String> {

        private ProgressDialog progressDialog;

        @Override
        protected void onPreExecute(){
            super.onPreExecute();
            progressDialog = ProgressDialog.show(MainActivity.this,"请等待", "食物正在识别中");
//            progressDialog.setCancelable(true);
        }

        @Override
        protected String doInBackground(FoodTask...params) {
            try {
                CalorieCount foodClient = new CalorieCount(params[0]);
                response = foodClient.send();
                Log.d("reponse",response);
                data[0]=response.substring(0,20);
                data[1]=response.substring(21,40);
                data[2]=response.substring(41,60);
                return response;

            } catch (IOException e) {
                Log.e("FoodRecognitionExample", e.getMessage(), e);
            }
            return null;
        }

        @Override
        protected void onPostExecute(String result){
            super.onPostExecute(result);
//            Intent i= new Intent(MainActivity.this,detail.class);
//            i.putExtra("info",data);
//            i.putExtra("mfilepath",mFilePath);
//            startActivity(i);
            progressDialog.dismiss();
            new Trans().execute(response);
        }
    }
}
