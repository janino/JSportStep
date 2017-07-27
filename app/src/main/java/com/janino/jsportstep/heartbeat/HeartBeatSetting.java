package com.janino.jsportstep.heartbeat;

import android.content.Context;

public class HeartBeatSetting {
	private static HeartBeatSetting instance_;
	private HeartBeatSetter heartBeatSetter_;

	private HeartBeatSetting(Context context) {

		this.heartBeatSetter_ = new HeartBeatSetterDefault(context);
	}

	public static HeartBeatSetting getInstance(Context context) {
		if (instance_ == null)
			instance_ = new HeartBeatSetting(context);
		return instance_;
	}

	public void cancelAlarm(Context context) {
		this.heartBeatSetter_.cancelAlarm(context);
	}

	public void checkWakeupOnTime() {
		this.heartBeatSetter_.checkWakeupOnTime();
	}

	public void setAlarm(Context context) {
		this.heartBeatSetter_.setAlarm(context);
	}
}
