
package com.janino.jsportstep.step;

import android.util.Log;

import com.janino.jsportstep.Constants;

import java.util.ArrayList;


public class PaceNotifier implements IStepListener{

    public interface Listener {
        public void paceChanged(double distance, double cal);
    }
    private ArrayList<Listener> mListeners = new ArrayList<Listener>();
    
    int mCounter = 0;
    
    private long mLastStepTime = 0;
    private long[] mLastStepDeltas = {-1, -1, -1, -1};
    private int mLastStepDeltasIndex = 0;
    private float mPace = 0;
    
    PedometerSettings mSettings;

    /** Desired pace, adjusted by the user */
    int mDesiredPace;
    float mSpeed;
    float mDistance = (float) 0.5;
    float mCal = (float) 0.03;

    public PaceNotifier(PedometerSettings settings) {
        mSettings = settings;
        mDesiredPace = mSettings.getDesiredPace();
    }
    public void setPace(int pace) {
        mPace = pace;
        int avg = (int)(60*1000.0 / mPace);
        for (int i = 0; i < mLastStepDeltas.length; i++) {
            mLastStepDeltas[i] = avg;
        }
        notifyListener(mDistance, mCal);
    }
    
    public void addListener(Listener l) {
        mListeners.add(l);
    }

    public void setDesiredPace(int desiredPace) {
        mDesiredPace = desiredPace;
    }

	@Override
	public void onStep(int value) {
        long thisStepTime = System.currentTimeMillis();
        mCounter ++;
        
        // Calculate pace based on last x steps
        if (mLastStepTime > 0) {
            long delta = thisStepTime - mLastStepTime;
            
            mLastStepDeltas[mLastStepDeltasIndex] = delta;
            mLastStepDeltasIndex = (mLastStepDeltasIndex + 1) % mLastStepDeltas.length;
            
            long sum = 0;
            boolean isMeaningfull = true;
            for (int i = 0; i < mLastStepDeltas.length; i++) {
                if (mLastStepDeltas[i] < 0) {
                    isMeaningfull = false;
                    break;
                }
                sum += mLastStepDeltas[i];
            }
            if (isMeaningfull && sum > 0) {
                long avg = sum / mLastStepDeltas.length;
                mPace = (float) (1000.0 / avg);// How many steps per seconds
            }
            else {
                mPace = -1;
            }
        }
        mLastStepTime = thisStepTime;
        mSpeed = Pedometer.getInstance().getSpeed(mPace);
        
        if(value == Constants.STEP_THRESHOLD) {
	        mDistance = Pedometer.getInstance().getStepLength(mPace) * Constants.STEP_THRESHOLD;
	        mCal = Pedometer.getInstance().getCalories(mSpeed) * Constants.STEP_THRESHOLD;
        } else {
        	mDistance = Pedometer.getInstance().getStepLength(mPace);
        	mCal = Pedometer.getInstance().getCalories(mSpeed);
        }
        notifyListener(mDistance, mCal);
    }
    
    private void notifyListener(double distance, double cal) {
        for (Listener listener : mListeners) {
        	Log.i("PaceNotifier", "mPace is=" + mPace + " mSpeed is=" + mSpeed + " mDistance ="
        			+ mDistance + " mCal=" + mCal);
            listener.paceChanged(distance, cal);
        }
    }
	@Override
	public void onStateChanged(int value) {

	}




}

