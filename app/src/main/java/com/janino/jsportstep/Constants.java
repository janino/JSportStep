package com.janino.jsportstep;

public class Constants {
	public static final String DB_NAME = "jsport_steps.db";
	
	public static final int MIL_SECONDS_DAY = 86400000;

	// Sampling Rate = 1000/UPTATE_INTERVAL_TIME
	public static final int UPTATE_INTERVAL_TIME = 20;

	//Order of low pass smoother
    public static final int SMOOTH_LENGTH = 5;

    // 10s largest interval between two steps, larger than this will be considered as new start
    public static final long THRESHOLD = 10000;

    //Dither resistance, continuous steps larger than this will be counted
    public static final int STEP_THRESHOLD = 8;
    
    public static final String ACTION_UPLOAD_STEP = "action_upload_step_data";
    public static final String ACTION_SAVE_STEP_PER_HOUR = "action_save_step_per_hour";
    public static final String ACTION_CLEAR_DATA = "action_clear_data";
    public static final String ACTION_UPLOAD_STEP_DATA_OK = "action_upload_step_data_ok";
    public static final String ACTION_UPLOAD_STEP_DATA_KO = "action_upload_step_data_ko";

	public static final int SAMPLES_IN_SECONDE = 1000 / UPTATE_INTERVAL_TIME;
    
	public static final String SP_SETTING_NAME = "sp_setting_flag";
    public static final String SP_SETTING_ITEM_KEY = "sp_setting_item_key";
    public static final String SP_SETTING_TARGET = "sp_setting_pedometer_target";
}