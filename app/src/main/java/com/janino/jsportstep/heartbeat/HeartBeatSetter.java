package com.janino.jsportstep.heartbeat;

import android.content.Context;

public abstract interface HeartBeatSetter {
  public abstract void cancelAlarm(Context context);

  public abstract void checkWakeupOnTime();

  public abstract void setAlarm(Context context);
}