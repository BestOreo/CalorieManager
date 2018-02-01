package com.example.nuc.caloriemanager;

import android.graphics.Color;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;

import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.charts.PieChart;
import com.github.mikephil.charting.components.Legend;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.data.PieData;
import com.github.mikephil.charting.data.PieDataSet;
import com.github.mikephil.charting.utils.ColorTemplate;
import com.github.mikephil.charting.utils.ValueFormatter;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class Statistic extends ActionBarActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_statistic);

        // 后退的toolbar
        Toolbar toolbar = (Toolbar) findViewById(R.id.statis_toolbar);
        setSupportActionBar(toolbar);
        //关键下面两句话，设置了回退按钮，及点击事件的效果
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        getSupportActionBar().setTitle("统计");

        /*下面是绘图函数*/
        /*折线图*/
        LineChart chart = (LineChart) findViewById(R.id.chart);
        // 制作7个数据点（沿x坐标轴）
//        LineData mLineData = makeLineData(7);       // 这里设置放多少个数据
        //返回的数据
        ArrayList<String> lastweek=Database.db.Select_Week();
        ArrayList<String> xVal = new ArrayList<>();
        ArrayList<Entry> yVals = new ArrayList<>();
        for(int i=0;i<7;i++){
            //合起来的地方
            int calorie = (int)(Float.parseFloat(lastweek.get(i)));
            yVals.add(new Entry(calorie,i));
            xVal.add("前" + (7-i) + "天");
        }
        LineDataSet d= new LineDataSet(yVals, "卡路里值");
        d.setAxisDependency(YAxis.AxisDependency.LEFT);
        d.setHighLightColor(Color.RED); // 设置点击某个点时，横竖两条线的颜色
        d.setDrawValues(true);//在点上显示数值 默认true
        d.setValueTextSize(12f);//数值字体大小，同样可以设置字体颜色、自定义字体等
        LineData mLinedata = new LineData(xVal,d);
        chart.setData(mLinedata);

        /*饼图*/
        PieChart chart_pie = (PieChart) findViewById(R.id.chart_pie);

        Random random;
        PieData data;
        PieDataSet dataSet;

        List<Entry> entries = new ArrayList<>();//显示条目
        ArrayList<String> xVals = new ArrayList<String>();//横坐标标签
        random=new Random();//随机数
        ArrayList<String> monthcalorie=Database.db.Select_Month4();
        for(int i=0;i<4;i++){
            float profit= Float.parseFloat(monthcalorie.get(i));
            //entries.add(BarEntry(float val,int positon);
            entries.add(new BarEntry(profit,i));
            xVals.add("前"+(3-i)+"月");
        }
        dataSet = new PieDataSet(entries, "营养分布");
        dataSet.setColors(ColorTemplate.COLORFUL_COLORS);
        data = new PieData(xVals, dataSet);
        chart_pie.setData(data);
        //设置Y方向上动画animateY(int time);
        chart_pie.animateY(3000);
        //图表描述
        chart_pie.setDescription("营养分布");

        /*条形图-步数*/
        BarChart chart_walk = (BarChart)findViewById(R.id.chart_walk);
        BarData walk_data ;
        BarDataSet walk_data_set;
        ArrayList<BarEntry> walk_entries = new ArrayList<>();
        ArrayList<String> xVals_walk = new ArrayList<>();
        for(int i=0;i<7;i++){
            //合起来的地方
            int walk = (int)(Float.parseFloat(lastweek.get(i)));
            walk_entries.add(new BarEntry(walk,i));
            xVals_walk.add("前" + (7-i) + "天");
        }
        walk_data_set = new BarDataSet(walk_entries,"步数");
        walk_data_set.setColors(ColorTemplate.COLORFUL_COLORS);
        walk_data = new BarData(xVals_walk, walk_data_set);
        chart_walk.setData(walk_data);
        //设置Y方向上动画animateY(int time);
        chart_walk.animateY(3000);
        //图表描述
        chart_walk.setDescription("运动情况");



    }

    // 设置chart显示的样式
    private void setChartStyle(LineChart mLineChart, LineData lineData,
                               int color) {
        // 是否在折线图上添加边框
        mLineChart.setDrawBorders(true);

        mLineChart.setDescription("测试");// 数据描述

        // 如果没有数据的时候，会显示这个，类似listview的emtpyview
        mLineChart
                .setNoDataTextDescription("如果传给MPAndroidChart的数据为空，那么你将看到这段文字。");

        // 是否绘制背景颜色。
        // 如果mLineChart.setDrawGridBackground(false)，
        // 那么mLineChart.setGridBackgroundColor(Color.CYAN)将失效;
        mLineChart.setDrawGridBackground(false);
        mLineChart.setGridBackgroundColor(Color.CYAN);

        // 触摸
        mLineChart.setTouchEnabled(true);

        // 拖拽
        mLineChart.setDragEnabled(true);

        // 缩放
        mLineChart.setScaleEnabled(true);

        mLineChart.setPinchZoom(false);

        // 设置背景
        mLineChart.setBackgroundColor(color);

        // 设置x,y轴的数据
        mLineChart.setData(lineData);

        // 设置比例图标示，就是那个一组y的value的
        Legend mLegend = mLineChart.getLegend();

        mLegend.setPosition(Legend.LegendPosition.BELOW_CHART_CENTER);
        mLegend.setForm(Legend.LegendForm.CIRCLE);// 样式
        mLegend.setFormSize(15.0f);// 字体
        mLegend.setTextColor(Color.BLUE);// 颜色

        // 沿x轴动画，时间2000毫秒。
        mLineChart.animateX(2000);
    }

    /**
     * @param count
     *            数据点的数量。
     * @return
     */
    private LineData makeLineData(int count) {
        ArrayList<String> x = new ArrayList<String>();
        for (int i = 0; i < count; i++) {
            // x轴显示的数据
            x.add("前"  +(count - i) + "天");
        }

        // y轴的数据
        ArrayList<Entry> y = new ArrayList<Entry>();
        for (int i = 0; i < count; i++) {
            float val = (float) (Math.random() * 100);
            Entry entry = new Entry(val, i);
            y.add(entry);   // 这里传入数据val
        }

        // y轴数据集
        LineDataSet mLineDataSet = new LineDataSet(y, "");

        // 用y轴的集合来设置参数
        // 线宽
        mLineDataSet.setLineWidth(3.0f);

        // 显示的圆形大小
        mLineDataSet.setCircleSize(5.0f);

        // 折线的颜色
        mLineDataSet.setColor(Color.DKGRAY);

        // 圆球的颜色
        mLineDataSet.setCircleColor(Color.RED);

        // 设置mLineDataSet.setDrawHighlightIndicators(false)后，
        // Highlight的十字交叉的纵横线将不会显示，
        // 同时，mLineDataSet.setHighLightColor(Color.CYAN)失效。
        mLineDataSet.setDrawHighlightIndicators(true);

        // 按击后，十字交叉线的颜色
        mLineDataSet.setHighLightColor(Color.CYAN);

        // 设置这项上显示的数据点的字体大小。
        mLineDataSet.setValueTextSize(10.0f);

        // mLineDataSet.setDrawCircleHole(true);

        // 改变折线样式，用曲线。
        mLineDataSet.setDrawCubic(true);
        // 默认是直线
        // 曲线的平滑度，值越大越平滑。
        mLineDataSet.setCubicIntensity(0.2f);

        // 填充曲线下方的区域，红色，半透明。
        mLineDataSet.setDrawFilled(true);
        mLineDataSet.setFillAlpha(128);
        mLineDataSet.setFillColor(Color.RED);

        // 填充折线上数据点、圆球里面包裹的中心空白处的颜色。
        mLineDataSet.setCircleColorHole(Color.YELLOW);

        // 设置折线上显示数据的格式。如果不设置，将默认显示float数据格式。
        mLineDataSet.setValueFormatter(new ValueFormatter() {

            @Override
            public String getFormattedValue(float value) {
                int n = (int) value;
                String s = "y:" + n;
                return s;
            }
        });

        ArrayList<LineDataSet> mLineDataSets = new ArrayList<LineDataSet>();
        mLineDataSets.add(mLineDataSet);

        LineData mLineData = new LineData(x, mLineDataSets);

        return mLineData;
    }
}