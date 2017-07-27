package com.janino.jsportstep;

import android.os.Environment;

public class DirConstants {
	public static final String DIR_WORK = Environment.getExternalStorageDirectory() + "/jsport/step/";
	public static final String DIR_PICTURE = DIR_WORK + "pic/";
	public static final String DIR_UPDATE_APP = DIR_WORK + "app/";
	public static final String DIR_SHARE_APP = DIR_WORK + "share/";
	public static final String DIR_PIC_THUMB = DIR_PICTURE + "thumb/";
	public static final String DIR_PIC_ORIGIN = DIR_PICTURE + "origin/";
	public static final String DIR_CAPTURE_PIC = DIR_PICTURE + "camera/";
			
	public static final String TMP_DATA_FILE_PATH = DIR_WORK + "tmp.data";
	
	public static final String HEAD_ICON_FILE_PATH = DIR_WORK + "head.jpg";
	
	public static final String TMP_VIEW_CACHE_FILE_PATH = DIR_WORK + "view_cache.jpg";
	
	public static final String DIR_LOGS = DIR_WORK + "logs/";

}
