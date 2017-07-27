package com.janino.jsportstep.step;

import android.util.Log;

import java.util.ArrayList;


public class StepDisplayer implements StepListener {
	private static final String TAG = "StepDisplayer";
    private int mCount = 0;
    private int mCount1, mCount2;
    PedometerSettings mSettings;

    public StepDisplayer(PedometerSettings settings) {
        mSettings = settings;
        notifyListener();
    }


    public void setSteps(int steps) {
        mCount = steps;
        mCount1 = steps;
        notifyListener();
    }

    @Override
    public void onStep(int Algorithm) {
    	if(Algorithm == 0) {
    		mCount ++;
    		notifyListener();
    		Log.e(TAG, "Algorithm benchmark get: " + mCount);
//            notifyListener();
    	} else if(Algorithm == 1) {
            mCount1 ++;
            
            Log.e(TAG, "Algorithm 1 get: " + mCount1);
    	} else if(Algorithm == 2) {
            mCount2 ++;
            Log.e(TAG, "Algorithm 2 get: " + mCount2);
    	}
//        mCount ++;

    }
    public void reloadSettings() {
        notifyListener();
    }
    public void passValue() {
    }
    
    

    //-----------------------------------------------------
    // Listener
    
    public interface Listener {
        public void stepsChanged();
        public void passValue();
    }
 
    private ArrayList<Listener> mListeners = new ArrayList<Listener>();

    public void addListener(Listener l) {
        mListeners.add(l);
    }
    public void notifyListener() {
        for (Listener listener : mListeners) {
            listener.stepsChanged();
        }
    }
    
}
