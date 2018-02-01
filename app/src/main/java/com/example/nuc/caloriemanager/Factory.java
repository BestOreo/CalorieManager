package com.example.nuc.caloriemanager;

/**
 * Created by NUC on 2017/11/29.
 */

import android.content.pm.PackageManager;

import com.example.nuc.caloriemanager.services.AbstractStepDetectorService;
import com.example.nuc.caloriemanager.services.AccelerometerStepDetectorService;
import com.example.nuc.caloriemanager.services.HardwareStepDetectorService;

import com.example.nuc.caloriemanager.AndroidVersionHelper;

/**
 * Factory class
 *
 * @author Tobias Neidig
 * @version 20160610
 */

public class Factory {

    /**
     * Returns the class of the step detector service which should be used
     * <p>
     * The used step detector service depends on different soft- and hardware preferences.
     *
     * @param pm An instance of the android PackageManager
     * @return The class of step detector
     */
    public static Class<? extends AbstractStepDetectorService> getStepDetectorServiceClass(PackageManager pm) {
        if (pm != null && AndroidVersionHelper.supportsStepDetector(pm)) {
            return HardwareStepDetectorService.class;
        } else {
            return AccelerometerStepDetectorService.class;
        }
    }
}