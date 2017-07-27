package com.janino.jsportstep.step;

import android.annotation.SuppressLint;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.util.Log;

import com.janino.jsportstep.Constants;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

public class NewStepDetector implements IStepAlgorithm {
	private final static String TAG = "NewStepDetector";

	private float mSampleNew, mSampleOld;
	private float mLimit = 0.1f;

	private long mLastSamplingTime = 0;
	private long mPrevStepTime = 0;

	private float mValues[] = new float[1200];

	private ArrayList<IStepListener> mStepListeners = new ArrayList<IStepListener>();

	public NewStepDetector() {
	}

	public void setSensitivity(float sensitivity) {
		mLimit = sensitivity; // 1.97 2.96 4.44 6.66 10.00 15.00 22.50 33.75
								// 50.62
	}

	public void addStepListener(IStepListener sl) {
		mStepListeners.add(sl);
	}

	private int sampleIndex;

	public void onSensorChanged(SensorEvent event) {
		Sensor sensor = event.sensor;
		if (sensor.getType() == Sensor.TYPE_ACCELEROMETER) {
			setSensitivity(0.1f);
			long currentUpdateTime = System.currentTimeMillis();
			long timeInterval = currentUpdateTime - mLastSamplingTime;
			if (timeInterval < Constants.UPTATE_INTERVAL_TIME) {
				return;
			}

			mLastSamplingTime = currentUpdateTime;
			sampleIndex++;

			/*
			 * Acquire raw acceleration data
			 */
			float vSum = 0;
			for (int i = 0; i < 3; i++) {
				vSum += event.values[i];
				;
			}
			float v = vSum / 3;
			writeToFile(v);

			v = smooth(v);
			updateDynamicThreshold(v);
			updateRegister(v);

			boolean isCandidate = mSampleNew < mDynamicThreshold
					&& mDynamicThreshold < mSampleOld;
			boolean isInTimeSpan = ((currentUpdateTime - mPrevStepTime > 200) && (currentUpdateTime
					- mPrevStepTime < 2000))
					|| mPrevStepTime == 0;
			if (currentUpdateTime - mPrevStepTime > 2000) {
				mPrevStepTime = 0;
			}
			Log.d(TAG, "Decision:" + " isCandidate" + isCandidate
					+ " isInTimeSpan:" + isInTimeSpan);
			if (isCandidate && isInTimeSpan) {
				Log.e(TAG, "Step Detected");
				for (IStepListener stepListener : mStepListeners) {
					stepListener.onStep(1);
				}
				mPrevStepTime = currentUpdateTime;
			}
		}
	}

	private float mLastFewValues[] = new float[Constants.SMOOTH_LENGTH];

	private float smooth(float v) {
		mLastFewValues[sampleIndex % Constants.SMOOTH_LENGTH] = v;
		float avgSum = 0;
		for (int i = 0; i < Constants.SMOOTH_LENGTH; i++) {
			avgSum += mLastFewValues[i];
		}
		return v = avgSum / Constants.SMOOTH_LENGTH;
	}

	private float mMin = Integer.MAX_VALUE;
	private float mMax = Integer.MIN_VALUE;
	private float mDynamicThreshold = Integer.MAX_VALUE;

	private void updateDynamicThreshold(float v) {
		if (v > mMax) {
			mMax = v;
		}
		if (v < mMin) {
			mMin = v;
		}
		if (sampleIndex % Constants.SAMPLES_IN_SECONDE == 0) {
			mDynamicThreshold = (mMax + mMin) / 2;
			Log.d(TAG, "updateDynamicThreshold" + " mMax:" + mMax + " mMin:"
					+ mMin + " mDynamicThreshold:" + mDynamicThreshold);
			mMax = Integer.MIN_VALUE;
			mMin = Integer.MAX_VALUE;
		}
	}

	private void updateRegister(float v) {
		mSampleOld = mSampleNew;
		Log.d(TAG, "updateRegister" + " mSampleOld:" + mSampleOld
				+ " mSampleResult:" + v + " Diff:" + Math.abs(v - mSampleOld));
		mLimit = ((mMax - mMin) / 4f) < 0.3f ? 0.3f : (mMax - mMin) / 4;
		if (Math.abs(v - mSampleOld) > mLimit) {
			mSampleNew = v;
			Log.i(TAG, "updateRegister Get in");
		}
	}

	@SuppressLint("SimpleDateFormat")
	private void writeToFile(float v) {
		mValues[sampleIndex % 1200] = v;
		Log.d(TAG, "mLastSamplingTime:" + mLastSamplingTime + " sampleIndex:"
				+ sampleIndex + " sampleValue:" + v);
		if (sampleIndex % 1200 == 0 && sampleIndex != 0) {
			SimpleDateFormat dateformat = new SimpleDateFormat(
					"yyyy-MM-dd HH:mm:ss E");
			String d = dateformat.format(new Date());
			StringBuilder sb = new StringBuilder(d.toString()).append(":\r\n");
			for (int i = 0; i < 1200; i++) {
				sb.append(String.valueOf(mValues[i])).append(",");
			}
			// Log.writeToFile(TAG, sb.toString());
		}
	}

	public void onAccuracyChanged(Sensor sensor, int accuracy) {
	}

	@Override
	public int detectStep(long timeStamp, float[] indata) {
		long currentUpdateTime = System.currentTimeMillis();
		long timeInterval = currentUpdateTime - mLastSamplingTime;
		if (timeInterval < Constants.UPTATE_INTERVAL_TIME) {
			return -1;
		}

		mLastSamplingTime = currentUpdateTime;
		sampleIndex++;

		/*
		 * Acquire raw acceleration data
		 */
		float vSum = 0;
		for (int i = 0; i < 3; i++) {
			vSum += indata[i];
			;
		}
		float v = vSum / 3;
		writeToFile(v);

		v = smooth(v);
		updateDynamicThreshold(v);
		updateRegister(v);

		boolean isCandidate = mSampleNew < mDynamicThreshold
				&& mDynamicThreshold < mSampleOld;
		boolean isInTimeSpan = ((currentUpdateTime - mPrevStepTime > 200) && (currentUpdateTime
				- mPrevStepTime < 2000))
				|| mPrevStepTime == 0;
		if (currentUpdateTime - mPrevStepTime > 2000) {
			mPrevStepTime = 0;
		}
		Log.d(TAG, "Decision:" + " isCandidate" + isCandidate
				+ " isInTimeSpan:" + isInTimeSpan);
		if (isCandidate && isInTimeSpan) {
			Log.e(TAG, "Step Detected");
			mPrevStepTime = currentUpdateTime;
			return 1;
		}
		return -1;
	}

	@Override
	public void setParameter(double... args) {
		// TODO Auto-generated method stub

	}

}