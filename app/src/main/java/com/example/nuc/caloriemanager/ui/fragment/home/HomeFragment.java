package com.example.nuc.caloriemanager.ui.fragment.home;

import com.example.nuc.caloriemanager.ActivityChartDataSet;
import com.example.nuc.caloriemanager.StepCount;
import com.example.nuc.caloriemanager.listener.OnItemClickListener;
import android.app.DatePickerDialog;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.IBinder;
import android.preference.PreferenceManager;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v4.view.GravityCompat;
import android.support.v7.view.menu.MenuBuilder;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.PopupMenu;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.DatePicker;
import android.widget.Toast;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.lang.Object;
import java.util.TimeZone;

import me.yokeyword.fragmentation.ISupportActivity;
import me.yokeyword.fragmentation.anim.DefaultHorizontalAnimator;
import me.yokeyword.fragmentation.anim.DefaultNoAnimator;
import me.yokeyword.fragmentation.anim.DefaultVerticalAnimator;

import com.example.nuc.caloriemanager.Factory;
import com.example.nuc.caloriemanager.MainActivity;
import com.example.nuc.caloriemanager.R;
import com.example.nuc.caloriemanager.StepDetectionServiceHelper;
import com.example.nuc.caloriemanager.adapter.HomeAdapter;
import com.example.nuc.caloriemanager.base.BaseMainFragment;
import com.example.nuc.caloriemanager.entity.Article;
import com.example.nuc.caloriemanager.listener.OnItemClickListener;
import com.example.nuc.caloriemanager.models.ActivitySummary;
import com.example.nuc.caloriemanager.persistence.StepCountPersistenceHelper;
import com.example.nuc.caloriemanager.persistence.WalkingModePersistenceHelper;
import com.example.nuc.caloriemanager.services.AbstractStepDetectorService;

import static com.example.nuc.caloriemanager.R.id.stepCount;
import static com.example.nuc.caloriemanager.R.string.calories;
import static com.github.mikephil.charting.charts.Chart.LOG_TAG;


public class HomeFragment extends BaseMainFragment implements Toolbar.OnMenuItemClickListener, SharedPreferences.OnSharedPreferenceChangeListener,
 HomeAdapter.OnItemClickListener{
    private static final String TAG = "Fragmentation";
    private final BroadcastReceiver broadcastReceiver = new BroadcastReceiver();
    private String[] mTitles;
    private RecyclerView mRecyclerView;
    private String[] mContents;
    private Calendar day;
    private List<Object> reports = new ArrayList<>();
    private ActivitySummary activitySummary;
    private Toolbar mToolbar;
    private RecyclerView mRecy;
    private boolean generatingReports;
    private AbstractStepDetectorService.StepDetectorBinder myBinder;
    private HomeAdapter mAdapter;
    private ServiceConnection mServiceConnection = new ServiceConnection() {

        @Override
        public void onServiceDisconnected(ComponentName name) {
            myBinder = null;
        }

        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            myBinder = (AbstractStepDetectorService.StepDetectorBinder) service;
            generateReports(true);
        }
    };

    public static HomeFragment newInstance() {
        return new HomeFragment();
    }

    @Override
    public void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(getActivity().getApplicationContext());
        sharedPref.registerOnSharedPreferenceChangeListener(this);
        day = Calendar.getInstance();
        registerReceivers();
        // Bind to stepDetector if today is shown
        if (isTodayShown() && StepDetectionServiceHelper.isStepDetectionEnabled(getContext())) {
            bindService();
        }
    }
    private void registerReceivers(){
        // subscribe to onStepsSaved and onStepsDetected broadcasts and onSpeedChanged
        IntentFilter filterRefreshUpdate = new IntentFilter();
        filterRefreshUpdate.addAction(StepCountPersistenceHelper.BROADCAST_ACTION_STEPS_SAVED);
        filterRefreshUpdate.addAction(AbstractStepDetectorService.BROADCAST_ACTION_STEPS_DETECTED);
        filterRefreshUpdate.addAction(StepCountPersistenceHelper.BROADCAST_ACTION_STEPS_INSERTED);
        filterRefreshUpdate.addAction(StepCountPersistenceHelper.BROADCAST_ACTION_STEPS_UPDATED);
        LocalBroadcastManager.getInstance(getContext()).registerReceiver(broadcastReceiver, filterRefreshUpdate);
        SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(getContext());
        sharedPref.registerOnSharedPreferenceChangeListener(this);
    }

    private void unregisterReceivers(){
        LocalBroadcastManager.getInstance(getContext()).unregisterReceiver(broadcastReceiver);
        SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(getContext());
        sharedPref.unregisterOnSharedPreferenceChangeListener(this);
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if(day == null){
            day = Calendar.getInstance();
        }
        if(!day.getTimeZone().equals(TimeZone.getDefault())) {
            day = Calendar.getInstance();
            generateReports(true);
        }
        if(isTodayShown() && StepDetectionServiceHelper.isStepDetectionEnabled(getContext())){
            bindService();
        }
        registerReceivers();
    }

    @Override
    public void onResume(){
        super.onResume();
        if(day == null){
            day = Calendar.getInstance();
        }
        if(!day.getTimeZone().equals(TimeZone.getDefault())) {
            day = Calendar.getInstance();
            generateReports(true);
        }
        if(isTodayShown() && StepDetectionServiceHelper.isStepDetectionEnabled(getContext())){
            bindService();
        }
        registerReceivers();
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_home, container, false);
        // Generate the reports
        generateReports(false);
        initView(view);

//        动态改动 当前Fragment的动画
//        setFragmentAnimator(fragmentAnimator);

        return view;
    }

    @Override
    public boolean onMenuItemClick(MenuItem item) {
        SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(getContext());
        SharedPreferences.Editor editor = sharedPref.edit();
        switch(item.getItemId()){
            case R.id.menu_pause_step_detection:
                editor.putBoolean(getString(R.string.pref_step_counter_enabled), false);
                editor.apply();
                StepDetectionServiceHelper.stopAllIfNotRequired(getActivity().getApplicationContext());
                return true;
            case R.id.menu_continue_step_detection:
                editor.putBoolean(getString(R.string.pref_step_counter_enabled), true);
                editor.apply();
                StepDetectionServiceHelper.startAllIfEnabled(true, getActivity().getApplicationContext());
                return true;
            default:
                return false;
        }
    }

    /**
     * Sets the visibility of pause and continue buttons in given menu
     * @param menu
     */
    private void setPauseContinueMenuItemVisibility(Menu menu){
        SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(this.getContext());
        boolean isStepCounterEnabled = sharedPref.getBoolean(getString(R.string.pref_step_counter_enabled), true);
        MenuItem continueStepDetectionMenuItem = menu.findItem(R.id.menu_continue_step_detection);
        MenuItem pauseStepDetectionMenuItem = menu.findItem(R.id.menu_pause_step_detection);
        if(isStepCounterEnabled){
            continueStepDetectionMenuItem.setVisible(false);
            pauseStepDetectionMenuItem.setVisible(true);
        }else {
            continueStepDetectionMenuItem.setVisible(true);
            pauseStepDetectionMenuItem.setVisible(false);
        }
    }

    @Override
        public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        inflater.inflate(R.menu.menu_options_overview, menu);
    }

    @Override
    public void onPrepareOptionsMenu(Menu menu) {
        super.onPrepareOptionsMenu(menu);
        setPauseContinueMenuItemVisibility(menu);
    }


    private void initView(View view) {

        mToolbar = (Toolbar) view.findViewById(R.id.toolbar);
        mRecy = (RecyclerView) view.findViewById(R.id.recy);

        mTitles = getResources().getStringArray(R.array.array_title);
        mContents = getResources().getStringArray(R.array.array_content);
        mToolbar.setTitle(R.string.home);
        ((MainActivity) getActivity()).setSupportActionBar(mToolbar);       // 将toolbar设置成当前actionbar
        setHasOptionsMenu(true);
        initToolbarNav(mToolbar, true);

        mToolbar.setOnMenuItemClickListener(this);
 //       setPauseContinueMenuItemVisibility(menu);

        mAdapter = new HomeAdapter(_mActivity);
        LinearLayoutManager manager = new LinearLayoutManager(_mActivity);
        mRecy.setLayoutManager(manager);
        mRecy.setAdapter(mAdapter);

        mAdapter.setOnItemClickListener(this);

        // Init Datas
        List<Article> articleList = new ArrayList<>();
        for (int i = 0; i < 5; i++) { // 主页卡数

            //int index = (int) (Math.random() * 3);
            int index = i;
            Article article = new Article(mTitles[index], mContents[index]);
            articleList.add(article);
        }
        mAdapter.setDatas(articleList);
        mAdapter.setSummary(reports);
    }

    /**
     * 类似于 Activity的 onNewIntent()
     */
    @Override
    public void onNewBundle(Bundle args) {
        super.onNewBundle(args);

        Toast.makeText(_mActivity, args.getString("from"), Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {    //每次sharedpreference更改后 调用这个回调函数
        if(key.equals(getString(R.string.pref_step_counter_enabled))){
            if(!StepDetectionServiceHelper.isStepDetectionEnabled(getContext())){
                unbindService();
            }else if(this.isTodayShown()){
                bindService();
            }
            this.getActivity().invalidateOptionsMenu(); // 重新渲染activity
        }
    }

    private void bindService(){
        if(myBinder == null) {
            Intent serviceIntent = new Intent(getContext(), Factory.getStepDetectorServiceClass(getContext().getPackageManager()));
            getActivity().getApplicationContext().bindService(serviceIntent, mServiceConnection, Context.BIND_AUTO_CREATE);
        }
    }

    private void unbindService(){
        if (this.isTodayShown() && mServiceConnection != null && myBinder != null && myBinder.getService() != null) {
            getActivity().getApplicationContext().unbindService(mServiceConnection);
            myBinder = null;
        }
    }
    @Override
    public void onDetach() {
        unbindService();
        unregisterReceivers();
        super.onDetach();
    }

    @Override
    public void onPause(){
        unbindService();
        unregisterReceivers();
        super.onPause();
    }

    @Override
    public void onDestroy() {
        unbindService();
        unregisterReceivers();
        super.onDestroy();
    }


    @Override
    public void onItemClick(int position, View view) {
        if (position >= 1)
            position--;
        start(DetailFragment.newInstance(mAdapter.getItem(position).getTitle(), position));
        Log.d("ASSSS", "" + position);
    }

    @Override
    public void onPrevClicked() {
        this.day.add(Calendar.DAY_OF_MONTH, -1);
        this.generateReports(false);
        if (isTodayShown() && StepDetectionServiceHelper.isStepDetectionEnabled(getContext())) {
            bindService();
        }
    }

    @Override
    public void onNextClicked() {
        this.day.add(Calendar.DAY_OF_MONTH, 1);
        this.generateReports(false);
        if (isTodayShown() && StepDetectionServiceHelper.isStepDetectionEnabled(getContext())) {
            bindService();
        }
    }

    @Override
    public void onTitleClicked() {
        int year = this.day.get(Calendar.YEAR);
        int month = this.day.get(Calendar.MONTH);
        int day = this.day.get(Calendar.DAY_OF_MONTH);

        // Create a new instance of DatePickerDialog and return it
        DatePickerDialog dialog = new DatePickerDialog(getContext(), R.style.AppTheme_DatePickerDialog, new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                HomeFragment.this.day.set(Calendar.DAY_OF_MONTH, dayOfMonth);
                HomeFragment.this.day.set(Calendar.MONTH, monthOfYear);
                HomeFragment.this.day.set(Calendar.YEAR, year);
                HomeFragment.this.generateReports(false);
                if (isTodayShown() && StepDetectionServiceHelper.isStepDetectionEnabled(getContext())) {
                    bindService();
                }
            }
        }, year, month, day);
        dialog.getDatePicker().setMaxDate(new Date().getTime()); // Max date is today
        dialog.getDatePicker().setMinDate(StepCountPersistenceHelper.getDateOfFirstEntry(getContext()).getTime());
        dialog.show();
    }


    /**
     * @return is the day which is currently shown today?
     */
    private boolean isTodayShown() {
        return (Calendar.getInstance().get(Calendar.YEAR) == day.get(Calendar.YEAR) &&
                Calendar.getInstance().get(Calendar.MONTH) == day.get(Calendar.MONTH) &&
                Calendar.getInstance().get(Calendar.DAY_OF_MONTH) == day.get(Calendar.DAY_OF_MONTH));
    }


    /**
     * Generates the report objects and adds them to the recycler view adapter.
     * The following reports will be generated:
     * * ActivitySummary
     * * ActivityChart
     * If one of these reports does not exist it will be created and added at the end of view.
     *
     * @param updated determines if the method is called because of an update of current steps.
     *                If set to true and another day than today is shown the call will be ignored.
     */
    private void generateReports(boolean updated) {
        Log.i(LOG_TAG, "Generating reports");
        if (!this.isTodayShown() && updated || isDetached() || getContext() == null || generatingReports) {
            // the day shown is not today or is detached
            return;
        }
        generatingReports = true;
        // Get all step counts for this day.
        final Context context = getActivity().getApplicationContext();
        final Locale locale = context.getResources().getConfiguration().locale;
        final SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(context);
        List<StepCount> stepCounts = new ArrayList<>();
        SimpleDateFormat formatHourMinute = new SimpleDateFormat("HH:mm", locale);
        Calendar m = day;
        m.set(Calendar.HOUR_OF_DAY, 0);
        m.set(Calendar.MINUTE, 0);
        m.set(Calendar.SECOND, 0);

        StepCount s = new StepCount();
        s.setStartTime(m.getTimeInMillis());
        s.setEndTime(m.getTimeInMillis()); // one hour more
        s.setStepCount(0);
        s.setWalkingMode(null);
        stepCounts.add(s);
        StepCount previousStepCount = s;
        for (int h = 0; h < 24; h++) {
            m.set(Calendar.HOUR_OF_DAY, h);
            s = new StepCount();
            s.setStartTime(m.getTimeInMillis() + 1000);
            if(h != 23) {
                s.setEndTime(m.getTimeInMillis() + 3600000); // one hour more
            }else{
                s.setEndTime(m.getTimeInMillis() + 3599000); // one hour more - 1sec
            }
            s.setWalkingMode(previousStepCount.getWalkingMode());
            previousStepCount = s;
            // load step counts in interval [s.getStartTime(), s.getEndTime()] from database
            List<StepCount> stepCountsFromStorage = StepCountPersistenceHelper.getStepCountsForInterval(s.getStartTime(), s.getEndTime(), context);
            // add non-saved steps if today
            if(isTodayShown() && s.getStartTime() < Calendar.getInstance().getTimeInMillis() && s.getEndTime() >= Calendar.getInstance().getTimeInMillis() && myBinder != null){
                // Today is shown. Add the steps which are not in database.
                StepCount s1 = new StepCount();
                if (stepCountsFromStorage.size() > 0) {
                    s1.setStartTime(stepCountsFromStorage.get(stepCountsFromStorage.size() - 1).getEndTime());
                } else {
                    s1.setStartTime(s.getStartTime());
                }
                s1.setEndTime(Calendar.getInstance().getTimeInMillis()); // now
                s1.setStepCount(myBinder.stepsSinceLastSave());
                s1.setWalkingMode(WalkingModePersistenceHelper.getActiveMode(context)); // add current walking mode
                stepCounts.add(s1);
            }
            // iterate over stepcounts in interval to sum up steps and detect changes in walkingmode
            for(StepCount stepCountFromStorage : stepCountsFromStorage){
                if(previousStepCount.getWalkingMode() == null || !previousStepCount.getWalkingMode().equals(stepCountFromStorage.getWalkingMode())){
                    // we have to create a new stepcount entry.
                    long oldEndTime = s.getEndTime();
                    s.setEndTime(stepCountFromStorage.getStartTime() - 1000);
                    stepCounts.add(s);
                    previousStepCount = s;
                    if(previousStepCount.getWalkingMode() == null){
                        for (StepCount s1: stepCounts) {
                            s1.setWalkingMode(stepCountFromStorage.getWalkingMode());
                        }
                        previousStepCount.setWalkingMode(stepCountFromStorage.getWalkingMode());
                    }
                    // create new stepcount.
                    s = new StepCount();
                    s.setStartTime(stepCountFromStorage.getStartTime());
                    s.setEndTime(oldEndTime);
                    s.setStepCount(stepCountFromStorage.getStepCount());
                    s.setWalkingMode(stepCountFromStorage.getWalkingMode());
                }else{
                    s.setStepCount(s.getStepCount() + stepCountFromStorage.getStepCount());
                }
            }
            stepCounts.add(s);
        }
        // fill chart data arrays
        int stepCount = 0;
        double distance = 0;
        int calories = 0;
        Map<String, ActivityChartDataSet> stepData = new LinkedHashMap<>();
        Map<String, ActivityChartDataSet> distanceData = new LinkedHashMap<>();
        Map<String, ActivityChartDataSet> caloriesData = new LinkedHashMap<>();
        for (StepCount s1: stepCounts) {
            stepCount += s1.getStepCount();
            distance += s1.getDistance();
            calories += s1.getCalories(context);
            if (!stepData.containsKey(formatHourMinute.format(s1.getEndTime())) ||
                    stepData.get(formatHourMinute.format(s1.getEndTime())).getStepCount().getStepCount() < stepCount) {
                if (s1.getEndTime() > Calendar.getInstance().getTime().getTime()) {
                    stepData.put(formatHourMinute.format(s1.getEndTime()), null);
                    distanceData.put(formatHourMinute.format(s1.getEndTime()), null);
                    caloriesData.put(formatHourMinute.format(s1.getEndTime()), null);
                } else {
                    stepData.put(formatHourMinute.format(s1.getEndTime()), new ActivityChartDataSet(stepCount, s1));
                    distanceData.put(formatHourMinute.format(s1.getEndTime()), new ActivityChartDataSet(distance, s1));
                    caloriesData.put(formatHourMinute.format(s1.getEndTime()), new ActivityChartDataSet(calories, s1));
                }
            }else{
                Log.i(LOG_TAG, "Skipping put operation");
            }
        }
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("dd. MMMM", locale);

        // create view models
        if (activitySummary == null) {
            activitySummary = new ActivitySummary(stepCount, distance, calories, simpleDateFormat.format(day.getTime()));
            reports.add(activitySummary);
        } else {
            activitySummary.setSteps(stepCount);
            activitySummary.setDistance(distance);
            activitySummary.setCalories(calories);
            activitySummary.setTitle(simpleDateFormat.format(day.getTime()));
            activitySummary.setHasSuccessor(!this.isTodayShown());
            activitySummary.setHasPredecessor(StepCountPersistenceHelper.getDateOfFirstEntry(getContext()).before(day.getTime()));
            boolean isVelocityEnabled = sharedPref.getBoolean(getString(R.string.pref_show_velocity), false);
                activitySummary.setCurrentSpeed(null);
        }
        // notify ui
        if (mAdapter != null && mRecy != null && getActivity() != null) {
            getActivity().runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    if(!mRecy.isComputingLayout()) {
                        mAdapter.notifyDataSetChanged();
                    }else{
                        Log.w(LOG_TAG, "Cannot inform adapter for changes - RecyclerView is computing layout.");
                    }
                }
            });
        } else {
            Log.w(LOG_TAG, "Cannot inform adapter for changes.");
        }
        generatingReports = false;
    }
    public class BroadcastReceiver extends android.content.BroadcastReceiver {

        @Override
        public void onReceive(Context context, Intent intent) {
            if (intent == null) {
                Log.w(LOG_TAG, "Received intent which is null.");
                return;
            }
            switch (intent.getAction()) {
                case AbstractStepDetectorService.BROADCAST_ACTION_STEPS_DETECTED:
                case StepCountPersistenceHelper.BROADCAST_ACTION_STEPS_SAVED:
                case WalkingModePersistenceHelper.BROADCAST_ACTION_WALKING_MODE_CHANGED:
//                case MovementSpeedService.BROADCAST_ACTION_SPEED_CHANGED:
//                    // Steps were saved, reload step count from database
                    generateReports(true);
                    break;
                case StepCountPersistenceHelper.BROADCAST_ACTION_STEPS_INSERTED:
                case StepCountPersistenceHelper.BROADCAST_ACTION_STEPS_UPDATED:
                    generateReports(false);
                    break;
                default:
            }
        }
    }
}
