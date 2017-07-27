package com.janino.jsportstep.utils;

import java.text.SimpleDateFormat;
import java.util.Date;

public class TimeUtils {
    public static String getMDDescriptionString(long t) {
        SimpleDateFormat sdf = new SimpleDateFormat("MM月dd日");
        return sdf.format(new Date(t));
    }

    public static boolean isToday(long t) {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        String today = sdf.format(new Date());
        String tStr = sdf.format(t);
        if (today.equals(tStr))
            return true;
        else
            return false;
    }
}
