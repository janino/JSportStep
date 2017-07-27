package com.janino.jsportstep.utils;

import android.util.Log;

import com.janino.jsportstep.AppDebug;
import com.janino.jsportstep.DirConstants;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;


public class HarwkinLogUtils {
	public static final String DEFAULT_TAG = "Jsport";
	public static final boolean DEBUG = AppDebug.LOG_DEGUG;
	public static final boolean IS_RECORD_LOCAL = AppDebug.WRITE_DEBUG;
	public static HarwkinLogUtils mInstance = null;
	
	StringBuffer mLogBuffer = null;
	
	public HarwkinLogUtils() {
		super();
		if(mLogBuffer == null){
			mLogBuffer = new StringBuffer();
		}
		
		mLogBuffer.append(0);
	}

	public static HarwkinLogUtils getInstance(){
		if(mInstance == null){
			mInstance = new HarwkinLogUtils();
		}
		return mInstance;
	}
	
	private void appendLog(String info){
		if(mLogBuffer == null){
			return ;
		}
		if(!IS_RECORD_LOCAL){
			return;
		}
		mLogBuffer.append(DateUtil.getDisplayDate(System.currentTimeMillis(), "yy-MM-dd hh-mm-ss"));
		mLogBuffer.append("-------");
		mLogBuffer.append(info);
		mLogBuffer.append("\n");
	}
	
	public void info(String tag,String info){
		if(DEBUG){
			Log.i(tag, info);
		}
		appendLog(info);
	}
	
	public void info(String info){
		if(DEBUG){
			Log.i(DEFAULT_TAG, info);
		}
		appendLog(info);
	}
	
	public void warnning(String tag,String info){
		if(DEBUG){
			Log.w(tag, info);
		}
		appendLog(info);
	}
	
	public void warnning(String info){
		if(DEBUG){
			Log.w(DEFAULT_TAG, info);
		}
		appendLog(info);
	}
	
	public void error(String tag,String info){
		if(DEBUG){
			Log.e(tag, info);
		}
		appendLog(info);
	}
	
	public void error(String info){
		if(DEBUG){
			Log.e(DEFAULT_TAG, info);
		}
		appendLog(info);
	}
	
	public void flush(){
		writeLog();
		resetLog();
	}
	
	public void resetLog(){
		if(mLogBuffer == null){
			return ;
		}
		mLogBuffer.append(0);
	}
	
	public void writeLog(){
		String fileName = DirConstants.DIR_LOGS + DateUtil.getDisplayDate(System.currentTimeMillis(), "yy-MM-dd hh-mm-ss") + ".log";
		writeContent(fileName,mLogBuffer.toString());
	}
	
	public static boolean writeContent(String filePath,String content){
		try{
			FileOutputStream fos = new FileOutputStream(new File(filePath));
			fos.write(content.getBytes());
			fos.flush();
			fos.close();
			return true;
		}catch (Exception e) {
			e.printStackTrace();
		}
		return false;
	}
	
	public static String readContent(String filePath){
		try{
			byte[] buffer = new byte[(int) new File(filePath).length()];
			FileInputStream fis = new FileInputStream(new File(filePath));
			fis.read(buffer);
			fis.close();
			return new String(buffer);
		}catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}
}
