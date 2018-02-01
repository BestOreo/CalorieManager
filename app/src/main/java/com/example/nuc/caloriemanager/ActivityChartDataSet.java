package com.example.nuc.caloriemanager;

/**
 * Created by NUC on 2017/12/5.
 */
/**
 * @author Tobias Neidig
 * @version 20160716
 */

public class ActivityChartDataSet {
    public double value;
    public StepCount stepCount;

    public ActivityChartDataSet(double value, StepCount stepCount) {
        this.value = value;
        this.stepCount = stepCount;
    }

    public double getValue() {
        return value;
    }

    public void setValue(double value) {
        this.value = value;
    }

    public StepCount getStepCount() {
        return stepCount;
    }

    public void setStepCount(StepCount stepCount) {
        this.stepCount = stepCount;
    }
}
