package com.janino.jsportstep.ui;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.PowerManager;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.janino.jsportstep.R;
import com.lidroid.xutils.util.LogUtils;

public class CheckFragment extends Fragment {
	private SensorManager mSeneorManager;
	private boolean mSupport = false;
	private boolean mShakeOk = false;
	private Handler mHandler;

	public static final String SP_CHECK_NAME = "sp_check_flag";
	public static final String SP_CHECK_ITEM_KEY = "sp_check_item_key";
	public static final int SP_CHECK_ITEM_VALUE_UNKNOWN = 0;
	public static final int SP_CHECK_ITEM_VALUE_SUPPORT = 1;
	public static final int SP_CHECK_ITEM_VALUE_UNSUPPORT = 2;

	private TextView tv_result;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		View view = inflater.inflate(R.layout.fragment_check, container, false);
		tv_result = (TextView) view.findViewById(R.id.tv_result);
		mHandler = new Handler();
		return view;
	}

	boolean isSupportStepDetector = false;

	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onActivityCreated(savedInstanceState);
		mSeneorManager = (SensorManager) getActivity().getSystemService(
				Context.SENSOR_SERVICE);
		final IntentFilter filter = new IntentFilter();
		filter.addAction(Intent.ACTION_SCREEN_OFF);
		filter.addAction(Intent.ACTION_SCREEN_ON);
		getActivity().registerReceiver(mBatInfoReceiver, filter);
	}

	private final BroadcastReceiver mBatInfoReceiver = new BroadcastReceiver() {

		@Override
		public void onReceive(final Context context, final Intent intent) {
			final String action = intent.getAction();
			if (Intent.ACTION_SCREEN_ON.equals(action)) {
				LogUtils.d("-----------------screen is on...");
				mSeneorManager.unregisterListener(sensorEventListener);
				SharedPreferences spf = getActivity().getSharedPreferences(
						CheckFragment.SP_CHECK_NAME, Context.MODE_PRIVATE);
				int flag = spf.getInt(CheckFragment.SP_CHECK_ITEM_KEY, -1);
				if (flag == -1) {
					if (!mSupport) {
						tv_result.setText("不支持");
					} else if (mShakeOk) {
						tv_result.setText("支持");

					}
				}
			} else if (Intent.ACTION_SCREEN_OFF.equals(action)) {
				LogUtils.d("----------------- screen is off...");
				Sensor aSensor;
				isSupportStepDetector = getActivity().getPackageManager()
						.hasSystemFeature(
								PackageManager.FEATURE_SENSOR_STEP_DETECTOR);
				if (isSupportStepDetector) {
					aSensor = mSeneorManager
							.getDefaultSensor(Sensor.TYPE_STEP_DETECTOR);
				} else {
					aSensor = mSeneorManager
							.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
				}

				if (aSensor != null) {
					mSeneorManager.registerListener(sensorEventListener,
							aSensor, SensorManager.SENSOR_DELAY_NORMAL);
				}
				mHandler.postDelayed(mUnSupportDetectRunnable, 5 * 1000);
			}
		}
	};

	private Runnable mUnSupportDetectRunnable = new Runnable() {

		@Override
		public void run() {
			// TODO Auto-generated method stub
			if (!mSupport) {
				mSeneorManager.unregisterListener(sensorEventListener);
				wakeupScreen();
			}
		}
	};

	private SensorEventListener sensorEventListener = new SensorEventListener() {

		@Override
		public void onSensorChanged(SensorEvent event) {
			if (isSupportStepDetector) {
				if (event.values[0] == 1.0f) {
					mShakeOk = true;
					mSupport = true;
					LogUtils.d("---shake ok");
					mSeneorManager.unregisterListener(sensorEventListener);
					wakeupScreen();
				}
			} else {
				// 传感器信息改变时执行该方法
				float[] values = event.values;
				float x = values[0]; // x轴方向的重力加速度，向右为正
				float y = values[1]; // y轴方向的重力加速度，向前为正
				float z = values[2]; // z轴方向的重力加速度，向上为正
				// 一般在这三个方向的重力加速度达到40就达到了摇晃手机的状态。
				if (x != 0 || y != 0 || z != 0) {
					mSupport = true;
				}
				int medumValue = 1;// 三星 i9250怎么晃都不会超过20，没办法，只设置19了
				if (Math.abs(x) > medumValue || Math.abs(y) > medumValue
						|| Math.abs(z) > medumValue) {
					mShakeOk = true;
					LogUtils.d("---shake ok");
					mSeneorManager.unregisterListener(sensorEventListener);
					wakeupScreen();
				}
			}
		}

		@Override
		public void onAccuracyChanged(Sensor sensor, int accuracy) {
			Log.d("zcc", "onAccuracyChanged()");
		}
	};

	private void wakeupScreen() {
		if (getActivity() == null)
			return;
		PowerManager pm = (PowerManager) getActivity().getSystemService(
				Context.POWER_SERVICE);
		PowerManager.WakeLock wl = pm.newWakeLock(
				PowerManager.SCREEN_BRIGHT_WAKE_LOCK
						| PowerManager.ACQUIRE_CAUSES_WAKEUP
						| PowerManager.ON_AFTER_RELEASE, "bright");
		wl.acquire(10000);
	}

	@Override
	public void onDestroy() {
		// TODO Auto-generated method stub
		super.onDestroy();
		if (mHandler != null && mUnSupportDetectRunnable != null)
			mHandler.removeCallbacks(mUnSupportDetectRunnable);
		if (getActivity() != null)
			getActivity().unregisterReceiver(mBatInfoReceiver);
	}
}
