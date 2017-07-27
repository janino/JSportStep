package com.janino.jsportstep.heartbeat;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import com.lidroid.xutils.util.LogUtils;

public class HeartBeatReceiver extends BroadcastReceiver
{
  private HeartBeatDetector mDetector;

  public HeartBeatReceiver() {
//    if (EnvironmentDetector.getInstance().isZteStarII())
//    {
//      this.mDetector = new HeartBeatDetectorZteStarII();
//      return;
//    }
    this.mDetector = new HeartBeatDetectorDefault();
  }

  public void onReceive(Context paramContext, Intent paramIntent)  {
    LogUtils.i("Received heartbeat Alarm!");
    this.mDetector.detect(paramContext.getApplicationContext());
  }
}
