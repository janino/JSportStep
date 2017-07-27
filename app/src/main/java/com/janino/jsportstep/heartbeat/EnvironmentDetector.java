package com.janino.jsportstep.heartbeat;

import java.util.List;

import android.annotation.SuppressLint;
import android.app.ActivityManager;
import android.app.ActivityManager.RunningTaskInfo;
import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Build;
import android.os.PowerManager;

import com.lidroid.xutils.util.LogUtils;

public class EnvironmentDetector {
	private static EnvironmentDetector instance_;
	private static boolean isScreenOn = true;
	private static float sGravity  = 9.812345F;
	PartialWakeLock pwlForGravity_ = new PartialWakeLock("startDetectGravity");
	private GravityDetectorListener gravityDetectorListener_;
	private SensorManager sensorManager_;
	private Sensor sensor_;
	private static String BrandType = Build.BRAND + Build.MANUFACTURER;
	private EnvironmentDetector(Context context) {
		if(sGravity == 9.812345F) {
			startDetectGravity(context);
		}
		String strBrand = Build.BRAND;
		
		LogUtils.d(strBrand + "|Mani:" + Build.MANUFACTURER + "|Board:" +Build.BOARD +
				"|Device:" + Build.DEVICE + "|Display:" + Build.DISPLAY + "|Hardware:" + Build.HARDWARE +
				"|Model: " + Build.MODEL + "|Product:" + Build.PRODUCT);
	}
	public static EnvironmentDetector getInstance(Context context) {
		if (instance_ == null)
			instance_ = new EnvironmentDetector(context.getApplicationContext());
		EnvironmentDetector localEnvironmentDetector = instance_;
		return localEnvironmentDetector;
	}
	
	@SuppressLint("DefaultLocale")
	public static boolean isXiaoMi() {
	    if ((BrandType == null))
	        return false;
	      if (BrandType.toLowerCase().contains("xiaomi")) {
	    	LogUtils.i("jsport is: " +  true);
	        return true;
	      } else {
	    	  return false;
	      }
	}
	
	public static boolean isNoMovement(float[] paramArrayOfFloat) {
		
		boolean bool = Math.abs(Math.sqrt(paramArrayOfFloat[0]
				* paramArrayOfFloat[0] + paramArrayOfFloat[1]
				* paramArrayOfFloat[1] + paramArrayOfFloat[2]
				* paramArrayOfFloat[2])
				- sGravity) < 1D;
		double x = Math.abs(Math.sqrt(paramArrayOfFloat[0]
				* paramArrayOfFloat[0] + paramArrayOfFloat[1]
				* paramArrayOfFloat[1] + paramArrayOfFloat[2]
				* paramArrayOfFloat[2])
				- sGravity);
	
//		LogUtils.d("heartbeat detector environment, para is: " + x + " isNoMovement is: " + bool);
		return bool;
	}
	
	public static boolean isBigMotion(float[] paramArrayOfFloat) {
		return !isNoMovement(paramArrayOfFloat);
	}
	
	public static boolean isScreenOn(Context context) {
		 PowerManager powerManager_ = (PowerManager)context.getSystemService("power");
//		 LogUtils.d("heartbeat detector environment, screen is " + powerManager_.isScreenOn());
		 return powerManager_.isScreenOn();
	}
	
	public class GravityDetectorListener implements SensorEventListener {
		GravityDetector gravityDetector_ = new GravityDetector();

		public GravityDetectorListener() {
		}

		public void onAccuracyChanged(Sensor paramSensor, int paramInt) {
		}

		public void onSensorChanged(SensorEvent paramSensorEvent) {
			if (sGravity == 9.812345F) {
				this.gravityDetector_.pushData(paramSensorEvent);
				if (this.gravityDetector_.gravity() != 9.812344551086426D) {
					EnvironmentDetector.this.setGravity((float) this.gravityDetector_.gravity());
					EnvironmentDetector.this.stopDetectGravity();
				}
			}
		}
	}

	public void setGravity(float paramFloat) {
		LogUtils.e("heartbeat detector environment, gravity is= " + paramFloat);
		sGravity = paramFloat;
	}

	private void startDetectGravity(Context context) {
		this.pwlForGravity_.acquireWakeLock(context);
		this.gravityDetectorListener_ = new GravityDetectorListener();
		this.sensorManager_ = (SensorManager)context.getSystemService("sensor");
		this.sensor_ = this.sensorManager_.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
		this.sensorManager_.registerListener(this.gravityDetectorListener_,
				this.sensor_, 50000);
	}
	private void stopDetectGravity() {
		this.sensorManager_.unregisterListener(this.gravityDetectorListener_);
		this.sensor_  = null;
		this.sensorManager_ = null;
		this.pwlForGravity_.releaseWakeLock();
	}
	
	public static boolean isLedongliRunning(Context context) {
		String packageName = "ledongli";
        boolean isAppRunning = false;
        ActivityManager am = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
        List<RunningTaskInfo> list = am.getRunningTasks(100);
        for (RunningTaskInfo info : list) {
            if (info.topActivity.getPackageName().contains(packageName) || info.baseActivity.getPackageName().contains(packageName)) {
                isAppRunning = true;
                // find it, break
                break;
            }
        }
        LogUtils.i("Is ledongli running is: " +  isAppRunning);
        return isAppRunning;
	}
}
