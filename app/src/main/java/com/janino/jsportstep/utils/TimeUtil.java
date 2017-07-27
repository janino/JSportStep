
/**   
 * @Title: TimeUtil.java 
 * @Package: com.pajk.pedometer.utils 
 * @Description: TODO
 * @author xiezhidong@pajk.cn  
 * @date 2014-12-5 下午7:37:48 
 */


package com.janino.jsportstep.utils;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;
import java.util.TimeZone;

import android.text.format.Time;


public class TimeUtil {
	
	public static long lastClickTime;
    /**
     * @return true快速点击，FALSE 非快速点击
     */
    public static boolean isFastDoubleClick() {
        long time = System.currentTimeMillis();
        long timeDuration = time - lastClickTime;

        if ( 0 < timeDuration && timeDuration < 500) {
            return true;
        }
        lastClickTime = time;
        return false;
    }
	

    public static long getNowHourTime(){
		Time a = new Time();
		a.setToNow();
		a.minute=0;
		a.second=0;
		return a.toMillis(true);
    }
    
    public static long getHourTime(long time){
		Time a = new Time();
		a.set(time);
		a.minute=0;
		a.second=0;
		return a.toMillis(true);
    }
    
    public static long getDayTime(long time){
		Time a = new Time();
		a.set(time);
		a.minute=0;
		a.hour=0;
		a.second=0;
		return a.toMillis(true);
    }
    
    public static long getNowTime(){
		Time a = new Time();
		a.setToNow();
		return a.toMillis(true);
    }
    
     
    /** 
     * @Description yyyyMMdd 转 long
     * @author xiezhidong@pajk.cn
     * @param str
     * @return  
     */
      	
    public static long getTimefromStr(String str){
    	SimpleDateFormat sdf=new SimpleDateFormat("yyyyMMdd",  Locale.getDefault());
    	Date date = new Date();
    	try {
    		date = sdf.parse(str);
		} catch (ParseException e) {
			e.printStackTrace();
		}
    	return date.getTime();
    }
    
    public static Date getDatefromStr(String str){
    	SimpleDateFormat sdf=new SimpleDateFormat("yyyyMMdd",  Locale.getDefault());
    	Date date = new Date();
    	try {
    		date = sdf.parse(str);
		} catch (ParseException e) {
			e.printStackTrace();
		}
    	return date;
    }
    
    public static String getstrfromDate(long time){
    	SimpleDateFormat sdf=new SimpleDateFormat("yyyyMMdd",  Locale.getDefault());
    	Date date = new Date(time);
    	return sdf.format(date);
    }
    
	/** 
	 * @Description 获得30天时间点
	 * @author xiezhidong@pajk.cn
	 * @param nowtime
	 * @return  
	 */
	  	
    public static long getThirtyDayTime() {
		int time = Time.getJulianDay(System.currentTimeMillis(), TimeZone
				.getDefault().getRawOffset()/1000);
		time -= 30;
		Time a = new Time();
		a.setJulianDay(time);
		return a.toMillis(true);
	}
    
    //for birthday setting
    public static long getBirthTime(int year, int month, int day){
    	Calendar a = Calendar.getInstance();
    	a.set(year, month - 1, day);
    	return a.getTimeInMillis();
    }
    
    public static int birthYear(long birthDay){
    	Calendar a = Calendar.getInstance();
		a.setTimeInMillis(birthDay);
		return a.get(Calendar.YEAR);
    }
    
    public static int birthMonth(long birthDay){
    	Calendar a = Calendar.getInstance();
		a.setTimeInMillis(birthDay);
		return a.get(Calendar.MONTH) + 1;
    }
    
    public static int birthDay(long birthDay){
    	Calendar a = Calendar.getInstance();
		a.setTimeInMillis(birthDay);
		return a.get(Calendar.DATE);
    }
    
    public static int birthDayToAge(long birthDay){
    	Calendar a = Calendar.getInstance();
		a.setTimeInMillis(new Date().getTime());
    	return  a.get(Calendar.YEAR) - birthYear(birthDay);
    }
}
