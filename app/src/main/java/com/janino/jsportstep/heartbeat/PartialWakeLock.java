package com.janino.jsportstep.heartbeat;

import com.lidroid.xutils.util.LogUtils;

import android.content.Context;
import android.os.PowerManager;

public class PartialWakeLock {
	  private static int LOCK_ID = 0;
	  private static boolean ON = true;
	  private static PartialWakeLock pwl = null;
	  private String name_;
	  private PowerManager.WakeLock wakeLock_;
	  
	  public PartialWakeLock(String paramString) {
	    LOCK_ID = 1 + LOCK_ID;
	    this.name_ = paramString;
	    LogUtils.d(paramString + " Create PartialWakeLock");
	  }
	  

	  public static PartialWakeLock getInstance() {
	    try {
	      if (pwl == null) {
	    	  pwl = new PartialWakeLock("Default");
	      }
	      PartialWakeLock localPartialWakeLock = pwl;
	      return localPartialWakeLock;
	    }
	    finally
	    {
	    }
	  }

	public void acquireWakeLock(Context paramContext) {
		try {
			LogUtils.i(this.name_ + " acquireWakeLock");
			if ((this.wakeLock_ == null) && (ON)) {
				this.wakeLock_ = ((PowerManager) paramContext.getSystemService("power"))
						.newWakeLock(PowerManager.PARTIAL_WAKE_LOCK, "Pedometer step keep partial wakelock");
				this.wakeLock_.acquire();
			}
			return;
		} catch (Exception e) {
			LogUtils.e(e.toString(), e);
		}
	}
	
	public boolean isHeld() {
		return this.wakeLock_ != null;
	}

	public void releaseWakeLock() {
		try {
			LogUtils.i(this.name_ + " releaseWakeLock");
			if ((this.wakeLock_ != null) && (ON)) {
				this.wakeLock_.release();
				this.wakeLock_ = null;
			}
			return;
		} catch (Exception e) {
			LogUtils.e(e.toString(), e);
		}
	}
}
