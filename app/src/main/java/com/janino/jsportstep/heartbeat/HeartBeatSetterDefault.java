package com.janino.jsportstep.heartbeat;

import com.lidroid.xutils.util.LogUtils;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;

public class HeartBeatSetterDefault implements HeartBeatSetter {
	private static final long TIME_INTERVAL = 10000L;
	private boolean isRTCWakeLock_;
	PendingIntent pendingIntent_ = null;
	private int rtcTimes_ = 0;
	private int rtcWakeupNotOnTimeTimes_ = 0;
	long startTime_ = 0L;

	public HeartBeatSetterDefault(Context context) {
		Context localContext = context;
		Intent localIntent = new Intent(localContext, HeartBeatReceiver.class);
		this.startTime_ = System.currentTimeMillis();
		this.pendingIntent_ = PendingIntent.getBroadcast(localContext, 0,
				localIntent, PendingIntent.FLAG_UPDATE_CURRENT);
		this.isRTCWakeLock_ = true;
	}

	private long getFirstAlarmComingTime() {
		return 10000L + this.startTime_;
	}

	public void cancelAlarm(Context context) {
		((AlarmManager) context.getSystemService("alarm"))
				.cancel(this.pendingIntent_);
	}

	public void checkWakeupOnTime() {
		long l = Math.abs(System.currentTimeMillis() - getFirstAlarmComingTime());
		if ((this.isRTCWakeLock_) && (l > 5000L))
			this.rtcWakeupNotOnTimeTimes_ = (1 + this.rtcWakeupNotOnTimeTimes_);
	}

	public void setAlarm(Context context) {
		int i = 0;
		if (this.rtcTimes_ >= 16000) {
	        this.rtcTimes_ = 0;
	        i = 1;
	      } else {
	        i = 0;
	      }
		if (this.isRTCWakeLock_)
			if (this.rtcWakeupNotOnTimeTimes_ >= 1) {
				this.rtcWakeupNotOnTimeTimes_ = 0;
				i = 0;
			}
		AlarmManager localAlarmManager;
		this.startTime_ = System.currentTimeMillis();
		localAlarmManager = (AlarmManager) context.getSystemService("alarm");
		if(i != 0) {
			this.isRTCWakeLock_ = false;
		} else {
			this.isRTCWakeLock_ = true;

		}
		
		LogUtils.i("Heartbeat isRTCWakeupNow:" + this.isRTCWakeLock_ +" rtcTimes:" + this.rtcTimes_
				+ " rtcWakeupNotOnTimeTimes:" + this.rtcWakeupNotOnTimeTimes_);
		localAlarmManager.set(AlarmManager.RTC_WAKEUP, TIME_INTERVAL + this.startTime_, this.pendingIntent_);
	}

}
