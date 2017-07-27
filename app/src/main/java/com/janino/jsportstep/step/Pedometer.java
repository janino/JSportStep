package com.janino.jsportstep.step;


import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.SharedPreferences;
import android.os.IBinder;
import android.os.RemoteException;
import android.util.Log;

import com.janino.jsportstep.Constants;
import com.lidroid.xutils.util.LogUtils;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class Pedometer {
	
	private static Pedometer mInstance = null;
    
	private StepService mStepService;

    private float mHeight = 170;//cm
    private float mWeight = 50;//kg
    private int mAge = 18;


	int mSteps = 0;
    float mSpeed; // m/s
    float mDistance; // m
	double mCalories;

    
	public static interface OnStepListener {
    	public void onStep(int step, float speed, float calories, long time, float distance);
    }
	
	private List<OnStepListener> mStepListeners = new ArrayList<OnStepListener>();


    public float getWeight() {
		return mWeight;
	}

	public void setWeight(float mWeight) {
		this.mWeight = mWeight;
	}

	public float getHeight() {
		return mHeight;
	}

	public void setHeight(float mHeight) {
		this.mHeight = mHeight;
	}
	
    public void setmAge(int mAge, Context context) {
		this.mAge = mAge;
		updateTargetStep(context);
	}	
    public static int getTargetStep(Context context) {
    	int target = context.getSharedPreferences(Constants.SP_SETTING_NAME, Context.MODE_PRIVATE)
		.getInt(Constants.SP_SETTING_TARGET, 5000);
    	return target;
    }

	private int updateTargetStep(Context context) {
		int target = 7000;
    	float bmi = getBMI();
    	if(bmi <=22) {
    		target *= 0.9;
    	} else {
    		target *= 1.1;
    	}
    	
    	if(mAge < 20) {
    		target *= 1.1;
    	} else if(mAge > 25) {
    		target *= 0.9;
    	}
	    SharedPreferences.Editor editor = context
                .getSharedPreferences(Constants.SP_SETTING_NAME,
                        Context.MODE_PRIVATE).edit();
        editor.putInt(Constants.SP_SETTING_TARGET, target);
        editor.commit();
		return target;
	}
	
	public float getBMI() {
		return (float) (mWeight/ Math.pow(mHeight/100, 2));
	}
    
	public void addListener(OnStepListener listener){
		synchronized(this) {
			if (!mStepListeners.contains(listener)) {
				mStepListeners.add(listener);
			}
		}
	}
    
	public void removeListener(OnStepListener listener) {
		synchronized(this) {
			if (mStepListeners.contains(listener)) {
				mStepListeners.remove(listener);  
			}
		}
	}
	
	public interface IStepCallback {
	    void onStep(int step);
	}

    private IStepCallback mStepCallback = new IStepCallback(){
		@Override
		public void onStep(int step) {
				updateSteps(mStepService.getSteps(), mStepService.getSpeed(), 
						mStepService.getCalories(), mStepService.getTime(), mStepService.getDistance() );
			
		}
	};
	
	public void updateSteps(int steps, float speed, float calories, long time, float distance) {
		mSteps = steps;
		for(int i = 0; i < mStepListeners.size(); i++) {
			mStepListeners.get(i).onStep(steps, speed, calories, time, distance);
			Log.i("Pedometer", "steps:" + steps + " speed" + speed + " cal: " + calories + " time: " + time + " dis:" + distance);
		}
	}
	
	
    public void startStepService(Context ctx) {
        ctx.startService(new Intent(ctx, StepService.class));
     }
	
    public void stopStepService(Context ctx) {
            ctx.stopService(new Intent(ctx,
                  StepService.class));
    }
    
	public synchronized static Pedometer getInstance() {
		if(mInstance == null) {
			mInstance = new Pedometer();
		}
		
		return mInstance;
	}
	
    private ServiceConnection mConnection = new ServiceConnection() {
        public void onServiceConnected(ComponentName className, IBinder binder) {
			try {
	        	mStepService = ((StepService.StepBinder)binder).getService();
				mStepService.registerCallback(mStepCallback);
				LogUtils.e("Service reconnect");
				updateSteps(mStepService.getSteps(), mStepService.getSpeed(), mStepService.getCalories(), 
						mStepService.getTime(), mStepService.getDistance());
			} catch (Exception e) {
				e.printStackTrace();
			}
        }

        public void onServiceDisconnected(ComponentName className) {
        	try {
				mStepService.unregisterCallback(mStepCallback);
			} catch (RemoteException e) {
				e.printStackTrace();
			}
        	mStepService = null;
        }
    };

	public void connect(Context ctx) {
		startStepService(ctx);
		ctx.bindService(new Intent(ctx, 
	                StepService.class), mConnection, Context.BIND_AUTO_CREATE + Context.BIND_DEBUG_UNBIND);
		
	}
	
    public void disconnect(Context ctx) {
        ctx.unbindService(mConnection);

    	try {
            if (mStepService != null)
			    mStepService.unregisterCallback(mStepCallback);
		} catch (RemoteException e) {
			e.printStackTrace();
		}
    }
	
    public int getSteps(Date date) {
    	return mSteps;
    }
    
    public float getSpeed(float pace) {
   		
    	float speed = pace * getStepLength(pace) //meter / seconds
//    			/ 1000f // kilometer/seconds
//    			* 60 * 60// km/hour
    			; 
    	return speed;
    }

    public float getStepLength(float pace) {
    	double stepLength = mHeight / 4 + 10; // Default as average
    	if(pace > 0 && pace < 1) {
    		stepLength = mHeight / 5;
    	} else if(pace < 1.5) {
    		stepLength = mHeight / 4;
    	} else if(pace < 2) {
    		stepLength = mHeight / 3;
    	} else if(pace < 2.5) {
    		stepLength = mHeight / 2;
    	} else if(pace < 3) {
    		stepLength = mHeight / 1.2;
    	} else if(pace < 4) {
    		stepLength = mHeight;
    	} else {
    		stepLength = mHeight * 1.2;
    	}
    	return (float)(stepLength / 100); //meter
    }
 
    public float getCalories(float speed) {
        mCalories = 4.5 * mWeight * speed / 3600; 
    	return (float)mCalories;
    }

    
    public float getFat(float calories) {
    	return calories / 9;
    }

//    public void saveData(PedometerInfo info) {
//    	if(mStepService != null) {
//	    	try {
//				mStepService.saveToDatebase(info);
//			} catch (Exception e) {
//				e.printStackTrace();
//			}
//    	}
//    }
    
    public void unregisterSensor(Context context) {
		try {
            if (mStepService != null) {
			    mStepService.unregisterDetector();
			    setPedoSharedPreferences(context, false);
            }
		} catch (Exception e) {
			e.printStackTrace();
			LogUtils.e(e.toString(), e);
		}
    }
    
	public void registerSensor(Context context) {
		try {
            if (mStepService != null) {
			    mStepService.registerDetector();
			    setPedoSharedPreferences(context, true);
            }
		} catch (Exception e) {
			e.printStackTrace();
			LogUtils.e(e.toString(), e);
		}
	}

	public void setPedoSharedPreferences(Context context, boolean value){
	    SharedPreferences.Editor editor = context
                .getSharedPreferences(Constants.SP_SETTING_NAME,
                        Context.MODE_PRIVATE).edit();
        editor.putBoolean(Constants.SP_SETTING_ITEM_KEY, value);
        editor.commit();
	}
	public boolean isDetectorRegistered(Context context) {
		boolean result = context.getSharedPreferences(Constants.SP_SETTING_NAME, Context.MODE_PRIVATE)
			.getBoolean(Constants.SP_SETTING_ITEM_KEY, true);
		// TODO Auto-generated method stub
		return result;
	}

	public void setSteps(int step, float dis, float cal) {
		try {
            if (mStepService != null)
			    mStepService.setValues(step, dis, cal);
		} catch (Exception e) {
			e.printStackTrace();
			LogUtils.e(e.toString(), e);
		}
	}
	
	
//	public boolean updateRecord(Context ctx){
//		try {
//			if(mStepService != null) {
//				mStepService.uploadData();
//				return true;
//			} else {
//				return false;
//			}
//		} catch (Exception e) {
//			return false;
//		}
//	}
	
}
