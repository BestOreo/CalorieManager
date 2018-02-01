package com.example.nuc.caloriemanager.adapter;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.support.v7.widget.PopupMenu;
import android.support.v7.widget.RecyclerView;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.ImageView;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Random;

import com.example.nuc.caloriemanager.Database;
import com.example.nuc.caloriemanager.MainActivity;
import com.example.nuc.caloriemanager.R;
import com.example.nuc.caloriemanager.Statistic;
import com.example.nuc.caloriemanager.UnitHelper;
import com.example.nuc.caloriemanager.listener.OnItemClickListener;
import com.example.nuc.caloriemanager.entity.Article;
import com.example.nuc.caloriemanager.models.ActivitySummary;
import com.github.mikephil.charting.animation.Easing;
import com.github.mikephil.charting.charts.PieChart;
import com.github.mikephil.charting.components.Legend;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.PieData;
import com.github.mikephil.charting.data.PieDataSet;
import com.github.mikephil.charting.utils.ColorTemplate;
import com.github.mikephil.charting.utils.PercentFormatter;

/**
 * 主页HomeFragment  Adapter
 */
public class HomeAdapter extends RecyclerView.Adapter<HomeAdapter.ViewHolder> {

    static float Total_calory = 4000;
    static float have_eat = 1200;

    private static final int CALORIE_ITEM = 0;
    private static final int NORMAL_ITEM = 1;
    private static final int SUMMARY_ITEM = 2;

    private List<Article> mItems = new ArrayList<>();
    private List<Object> summaryItems = new ArrayList<>();
    private LayoutInflater mInflater;

    private OnItemClickListener mClickListener;


    public HomeAdapter(Context context) {
        this.mInflater = LayoutInflater.from(context);
    }

    static PieChart mChart;

    public static void setCalorie()
    {
        mChart.setCenterText(String.valueOf(Database.db.Select_today()));
    }


    public void setDatas(List<Article> items) {
        mItems.clear();
        mItems.addAll(items);
    }
    public void setSummary(List<Object> items) {
        summaryItems.clear();
        summaryItems.addAll(items);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        if (position == 1)
            position = 0;
        else if (position >= 2)
            position -= 2;
        Article item = mItems.get(position);
        if (holder instanceof MyCalorieViewHolder) {
            //((MyCalorieViewHolder) holder).Calorie.setText("1024");
        } else if (holder instanceof MyViewHolder) {
            MyViewHolder myvh = (MyViewHolder) holder;
            myvh.tvTitle.setText(item.getTitle());
            myvh.tvContent.setText(item.getContent());
        } else if (holder instanceof SummaryViewHolder) {
            ActivitySummary summaryData = (ActivitySummary) summaryItems.get(position);
            SummaryViewHolder summaryViewHolder = (SummaryViewHolder) holder;
            UnitHelper.FormattedUnitPair distance = UnitHelper.formatKilometers(UnitHelper.metersToKilometers(summaryData.getDistance()), summaryViewHolder.itemView.getContext());
            UnitHelper.FormattedUnitPair calories = UnitHelper.formatCalories(summaryData.getCalories(), summaryViewHolder.itemView.getContext());
            summaryViewHolder.mTitleTextView.setText(summaryData.getTitle());
            summaryViewHolder.mStepsTextView.setText(String.valueOf(summaryData.getSteps()));
            summaryViewHolder.mDistanceTextView.setText(distance.getValue());
            summaryViewHolder.mDistanceTitleTextView.setText(distance.getUnit());
            summaryViewHolder.mCaloriesTextView.setText(calories.getValue());
            summaryViewHolder.mCaloriesTitleTextView.setText(calories.getUnit());
            summaryViewHolder.mNextButton.setVisibility(summaryData.isHasSuccessor() ? View.VISIBLE : View.INVISIBLE);
            summaryViewHolder.mPrevButton.setVisibility(summaryData.isHasPredecessor() ? View.VISIBLE : View.INVISIBLE);
            if (summaryData.getCurrentSpeed() != null) {
                summaryViewHolder.mVelocityContainer.setVisibility(View.VISIBLE);
                summaryViewHolder.mVelocityTextView.setText(String.valueOf(UnitHelper.formatKilometersPerHour(UnitHelper.metersPerSecondToKilometersPerHour(summaryData.getCurrentSpeed()), summaryViewHolder.context)));
            } else {
                summaryViewHolder.mVelocityContainer.setVisibility(View.GONE);
            }
        }
    }


    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        if (viewType == CALORIE_ITEM) {
            View view = mInflater.inflate(R.layout.item_homecalorie, parent, false);
            final MyCalorieViewHolder holder = new MyCalorieViewHolder(view);
            holder.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    int position = holder.getAdapterPosition();
                    if (mClickListener != null) {
                        mClickListener.onItemClick(position, v);
                    }
                }
            });
            return holder;
        }
    else
        if (viewType == NORMAL_ITEM){
            View view = mInflater.inflate(R.layout.item_home, parent, false);
            final MyViewHolder holder = new MyViewHolder(view);
            holder.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    int position = holder.getAdapterPosition();
                    if (mClickListener != null) {
                        mClickListener.onItemClick(position, v);
                    }
                }
            });
            return holder;
        }
        else
            {
                View view = mInflater.inflate(R.layout.item_stepsummary, parent, false);
                final SummaryViewHolder sumvh = new SummaryViewHolder(view);
                return sumvh;
            }

    }

    public class SummaryViewHolder extends ViewHolder {

        public TextView mTitleTextView;
        public TextView mStepsTextView;
        public TextView mDistanceTextView;
        public TextView mCaloriesTextView;
        public TextView mVelocityTextView;
        public TextView mDistanceTitleTextView;
        public TextView mCaloriesTitleTextView;
        public RelativeLayout mVelocityContainer;
        public ImageButton mPrevButton;
        public ImageButton mNextButton;
        public ImageButton mMenuButton;

        public SummaryViewHolder(View itemView) {
            super(itemView);
            mTitleTextView = (TextView) itemView.findViewById(R.id.period);
            mStepsTextView = (TextView) itemView.findViewById(R.id.stepCount);
            mDistanceTextView = (TextView) itemView.findViewById(R.id.distanceCount);
            mCaloriesTextView = (TextView) itemView.findViewById(R.id.calorieCount);
            mVelocityTextView = (TextView) itemView.findViewById(R.id.speed);
            mVelocityContainer = (RelativeLayout) itemView.findViewById(R.id.speedContainer);
            mDistanceTitleTextView = (TextView) itemView.findViewById(R.id.distanceTitle);
            mCaloriesTitleTextView = (TextView) itemView.findViewById(R.id.calorieTitle);
            mPrevButton = (ImageButton) itemView.findViewById(R.id.prev_btn);
            mNextButton = (ImageButton) itemView.findViewById(R.id.next_btn);
            mMenuButton = (ImageButton) itemView.findViewById(R.id.periodMoreButton);
/*
            mMenuButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    showPopup(mMenuButton, context);
                }
            });
*/
            mPrevButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (mClickListener != null) {
                        mClickListener.onPrevClicked();
                    }
                }
            });
            mNextButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (mClickListener != null) {
                        mClickListener.onNextClicked();
                    }
                }
            });
            mTitleTextView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (mClickListener != null) {
                        mClickListener.onTitleClicked();
                    }
                }
            });
        }
/*
        @Override
        public boolean onMenuItemClick(MenuItem item) {
            item.setChecked(!item.isChecked());
            if (mClickListener != null) {
                mClickListener.onWalkingModeClicked(item.getItemId());
                return true;
            } else {
                return false;
            }
        }*/
    }

    public interface OnItemClickListener {

        void onPrevClicked();

        void onNextClicked();

        void onTitleClicked();

        void onItemClick(int position, View view);
    }
        public class ViewHolder extends RecyclerView.ViewHolder {
            public Context context;

            public ViewHolder(View itemView) {
                super(itemView);
                context = itemView.getContext();
            }
        }


    @Override
    public int getItemCount() {
        return mItems.size();
    }

    public Article getItem(int position) {
        return mItems.get(position);
    }

    class MyViewHolder extends ViewHolder {
        private TextView tvTitle, tvContent;

        public MyViewHolder(View itemView) {
            super(itemView);
            tvTitle = (TextView) itemView.findViewById(R.id.tt_title);
            tvContent = (TextView) itemView.findViewById(R.id.tv_content);
        }
    }

    public class MyCalorieViewHolder extends MyViewHolder {
        //TextView Calorie;
        //ImageView MainCircle;;

        public MyCalorieViewHolder(View itemView) {
            super(itemView);
            //MainCircle = (ImageView) itemView.findViewById(R.id.Maincircle);
            //Calorie = (TextView) itemView.findViewById(R.id.CurrentCalorie);

            mChart = (PieChart) itemView.findViewById(R.id.calory_count);
            PieData mPieData = getPieData(2, 100);
            showChart(mChart, mPieData);

        }
    }

    // 展示图表
    private void showChart(PieChart pieChart, PieData pieData) {
        pieChart.setHoleColorTransparent(true);

        pieChart.setHoleRadius(70f);  //半径
        pieChart.setTransparentCircleRadius(64f); // 半透明圈
        //pieChart.setHoleRadius(0)  //实心圆

        pieChart.setDescription("");

        // mChart.setDrawYValues(true);
        pieChart.setDrawCenterText(true);  //饼状图中间可以添加文字

        pieChart.setDrawHoleEnabled(true);

        pieChart.setRotationAngle(-90); // 初始旋转角度

        // draws the corresponding description value into the slice
        // mChart.setDrawXValues(true);

        // enable rotation of the chart by touch
        pieChart.setRotationEnabled(true); // 可以手动旋转

        // display percentage values
        pieChart.setUsePercentValues(true);  //显示成百分比
        // mChart.setUnit(" €");
        // mChart.setDrawUnitsInChart(true);

        // add a selection listener
//      mChart.setOnChartValueSelectedListener(this);
        // mChart.setTouchEnabled(false);

//      mChart.setOnAnimationListener(this);

        pieChart.setCenterText(""+ (Database.db.Select_today() + have_eat));  //饼状图中间的文字
        pieChart.setCenterTextSize(30);

        //设置数据
        pieChart.setData(pieData);

        pieChart.setDrawSliceText(false);   // 不显示数据上的文字
        pieChart.highlightValues(null);

        //pieChart.setDescriptionTextSize(20);

        // undo all highlights
//      pieChart.highlightValues(null);
//      pieChart.invalidate();

        Legend mLegend = pieChart.getLegend();  //设置比例图
        //mLegend.setEnabled(false);
        mLegend.setPosition(Legend.LegendPosition.RIGHT_OF_CHART_CENTER);  //最右边显示
//      mLegend.setForm(LegendForm.LINE);  //设置比例图的形状，默认是方形
        mLegend.setXEntrySpace(7f);
        mLegend.setYEntrySpace(5f);

        mLegend.setTextSize(12);

        pieChart.animateXY(1000, 1000);  //设置动画
        // mChart.spin(2000, 0, 360);
    }

    /**
     *
     * @param count 分成几部分
     * @param range 没什么用
     */
    private PieData getPieData(int count, float range) {

        ArrayList<String> xValues = new ArrayList<String>();  //xVals用来表示每个饼块上的内容

//        for (int i = 0; i < count; i++) {
//            xValues.add("Quarterly" + (i + 1));  //饼块上显示成Quarterly1, Quarterly2, Quarterly3, Quarterly4
//        }
        xValues.add("计划内剩余");
        xValues.add("已经摄入" );

        ArrayList<Entry> yValues = new ArrayList<Entry>();  //yVals用来表示封装每个饼块的实际数据

        // 饼图数据
        /**
         * 将一个饼形图分成四部分， 四部分的数值比例为14:14:34:38
         * 所以 14代表的百分比就是14%
         *
         *
         * 这里到时候改一下，直接从数据库取
         */

        float not_eat = Total_calory - have_eat;

        yValues.add(new Entry(not_eat, 1));
        yValues.add(new Entry(have_eat, 0));

        // 进度



        //y轴的集合
        PieDataSet pieDataSet = new PieDataSet(yValues, "卡路里摄入情况"/*显示在比例图上*/);
        pieDataSet.setSliceSpace(0f); //设置个饼状图之间的距离

        ArrayList<Integer> colors = new ArrayList<Integer>();

        // 饼图颜色
        colors.add(Color.rgb(205, 205, 205));
        colors.add(Color.rgb(114, 188, 223));
//        colors.add(Color.rgb(255, 123, 124));
//        colors.add(Color.rgb(57, 135, 200));

        pieDataSet.setColors(colors);


        PieData pieData = new PieData(xValues, pieDataSet);
        pieData.setDrawValues(false);

        return pieData;
    }



    @Override
    public int getItemViewType(int position) {
        if (position == 0)
            return CALORIE_ITEM;
        else
        if (position == 1)
            return SUMMARY_ITEM;
        else
            return NORMAL_ITEM;
    }

    public void setOnItemClickListener(OnItemClickListener itemClickListener) {
        this.mClickListener = itemClickListener;
    }
}
