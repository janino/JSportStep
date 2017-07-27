package com.janino.jsportstep.utils;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

import android.app.AlarmManager;

public class DateUtil {
	public static String getCreateAt(Date date) {
		Calendar c = Calendar.getInstance();
		if (c.get(Calendar.YEAR) - (date.getYear() + 1900) > 0) {
			int i = c.get(Calendar.YEAR) - date.getYear();
			return i + "年前";
		} else if (c.get(Calendar.MONTH) - date.getMonth() > 0) {
			int i = c.get(Calendar.MONTH) - date.getMonth();
			return i + "月前";
		} else if (c.get(Calendar.DAY_OF_MONTH) - date.getDate() > 0) {
			int i = c.get(Calendar.DAY_OF_MONTH) - date.getDate();
			return i + "天前";
		} else if (c.get(Calendar.HOUR_OF_DAY) - date.getHours() > 0) {
			int i = c.get(Calendar.HOUR_OF_DAY) - date.getHours();
			return i + "小时前";
		} else if (c.get(Calendar.MINUTE) - date.getMinutes() > 0) {
			int i = c.get(Calendar.MINUTE) - date.getMinutes();
			return i + "分钟前";
		} else {
			return "刚刚";
		}
	}

	public static String getCreateAt(String strDate) {
		if (strDate == null) {
			return "刚刚";
		}
		SimpleDateFormat simpleDateFormat = new SimpleDateFormat(
				"yyyy-MM-dd HH:mm:ss");
		try {
			Date startDate = simpleDateFormat.parse(strDate);
			return getCreateAt(startDate);
		} catch (ParseException e) {
			e.printStackTrace();
		}
		return "刚刚";
	}

	public static String getShorTime(String strDate) {
		SimpleDateFormat simpleDateFormat = new SimpleDateFormat(
				"yyyy-MM-dd HH:mm:ss");
		SimpleDateFormat shortFormat = new SimpleDateFormat("yyyy-MM-dd");
		try {
			Date shortDate = simpleDateFormat.parse(strDate);

			return shortFormat.format(shortDate);
		} catch (ParseException e) {
			e.printStackTrace();
		}

		return "刚刚";
	}

	public static long getPostTimeMiilis(String strDate) {
		if (strDate == null) {
			return System.currentTimeMillis();
		}
		SimpleDateFormat simpleDateFormat = new SimpleDateFormat(
				"yyyy/MM/dd HH:mm:ss");
		try {
			Date startDate = simpleDateFormat.parse(strDate);
			return startDate.getTime();
		} catch (ParseException e) {
			e.printStackTrace();
		}
		return -1;
	}

	public static long getContentTimeMiilis(String strDate) {
		if (strDate == null) {
			return System.currentTimeMillis();
		}
		SimpleDateFormat simpleDateFormat = new SimpleDateFormat(
				"yyyy-MM-dd HH:mm:ss");
		try {
			Date startDate = simpleDateFormat.parse(strDate);
			return startDate.getTime();
		} catch (ParseException e) {
			e.printStackTrace();
		}
		return -1;
	}

	public static long getTimeMiilis(String strDate) {
		if (strDate == null) {
			return System.currentTimeMillis();
		}
		SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd");
		try {
			Date startDate = simpleDateFormat.parse(strDate);
			return startDate.getTime();
		} catch (ParseException e) {
			e.printStackTrace();
		}
		return -1;
	}

	public static long getYYYYTimeMiilis(String strDate) {
		if (strDate == null) {
			return System.currentTimeMillis();
		}
		SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy");
		try {
			Date startDate = simpleDateFormat.parse(strDate);
			return startDate.getTime();
		} catch (ParseException e) {
			e.printStackTrace();
		}
		return -1;
	}

	public static String getTimeDescription(Date date) {
		Calendar c = Calendar.getInstance();
		// 年前年后
		int ret = c.get(Calendar.YEAR) - (date.getYear() + 1900);
		if (ret > 0) {
			int i = c.get(Calendar.YEAR) - date.getYear();
			return i + "年前";
		} else if (ret < 0) {
			int i = date.getYear() - c.get(Calendar.YEAR);
			return i + "年后";
		}

		ret = c.get(Calendar.MONTH) - date.getMonth();
		if (ret > 0) {
			return Math.abs(ret) + "个月前";
		} else if (ret < 0) {
			return Math.abs(ret) + "个月后";
		}

		ret = c.get(Calendar.DAY_OF_MONTH) - date.getDate();
		if (ret > 0) {
			return Math.abs(ret) + "天前";
		} else if (ret < 0) {
			return Math.abs(ret) + "天后";
		}

		ret = c.get(Calendar.HOUR_OF_DAY) - date.getHours();
		if (ret > 0) {
			return Math.abs(ret) + "小时前";
		} else if (ret < 0) {
			return Math.abs(ret) + "小时后";
		}

		ret = c.get(Calendar.MINUTE) - date.getMinutes();
		if (ret > 0) {
			return Math.abs(ret) + "分钟前";
		} else if (ret < 0) {
			return Math.abs(ret) + "分钟后";
		}

		return "刚刚";
	}

	public static String getDeadline(String strDate) {
		if (strDate == null) {
			return "刚刚";
		}

		SimpleDateFormat simpleDateFormat = new SimpleDateFormat(
				"yyyy-MM-dd HH:mm:ss");
		try {
			Date startDate = simpleDateFormat.parse(strDate);
			return getTimeDescription(startDate);
		} catch (ParseException e) {
			e.printStackTrace();
		}
		return "刚刚";
	}

	public static String getRequestDate(long millis) {
		if (millis < 0) {
			return null;
		}

		SimpleDateFormat simpleDateFormat = new SimpleDateFormat(
				"yyyy/MM/dd HH:mm:ss");
		return simpleDateFormat.format(new Date(millis));
	}

	public static String getRequestDate(long millis, String format) {
		if (millis < 0) {
			return null;
		}

		SimpleDateFormat simpleDateFormat = new SimpleDateFormat(format);
		return simpleDateFormat.format(new Date(millis));
	}

	public static Calendar longToDateTime(long time) {
		if (time == -1) {
			return null;
		}
		Calendar c = Calendar.getInstance();
		c.clear();
		c.setTimeInMillis(time);
		return c;
		// return new DateTime(c.get(Calendar.YEAR),
		// c.get(Calendar.MONTH) + 1,
		// c.get(Calendar.DAY_OF_MONTH));
	}

	public static long DateTimeToLong(Calendar date) {
		if (date == null) {
			return -1;
		}
		// Calendar c = Calendar.getInstance();
		// c.clear();
		// c.set(date.getYear(), date.getMonth() - 1,
		// date.getDayOfMonth(),0,0,0);
		return date.getTimeInMillis();
	}

	public static long getCalendar(int year, int month, int day, int hourOfDay,
			int minute, int second) {
		Calendar c = Calendar.getInstance();
		c.clear();
		c.set(year, month, day, hourOfDay, minute, second);
		return c.getTimeInMillis();
	}

	public static long getCalendar(int year, int month, int day) {
		Calendar c = Calendar.getInstance();
		c.clear();
		c.set(year, month, day, 0, 0, 0);
		return c.getTimeInMillis();
	}

	public static int getAgeBySouth(long time) {
		Calendar c = Calendar.getInstance();
		c.clear();
		c.setTimeInMillis(System.currentTimeMillis());
		int yearNow = c.get(Calendar.YEAR);

		c.clear();
		c.setTimeInMillis(time);
		int yearBirthday = c.get(Calendar.YEAR);
		return yearNow - yearBirthday + 1;
	}

	public static int getAgeByNorth(long time) {
		Calendar c = Calendar.getInstance();
		c.setTimeInMillis(System.currentTimeMillis());
		int yearNow = c.get(Calendar.YEAR);
		int monthNow = c.get(Calendar.MONTH) + 1;
		int dayNow = c.get(Calendar.DAY_OF_MONTH);

		c.setTimeInMillis(time);
		int yearBirthday = c.get(Calendar.YEAR);
		int monthBirthday = c.get(Calendar.MONTH) + 1;
		int dayBirthday = c.get(Calendar.DAY_OF_MONTH);

		boolean flag = true;
		if (monthNow < dayBirthday
				|| (monthNow == monthBirthday && dayNow <= dayBirthday)) {
			flag = false;
		}
		if (flag) {
			return yearNow - yearBirthday;
		} else {
			return yearNow - yearBirthday - 1;
		}
	}

	public static long reformTime(long time) {
		Calendar cal = Calendar.getInstance();
		long seconds = (long) (time / 1000);
		time = seconds * 1000;
		cal.setTimeInMillis(time);
		cal.set(cal.get(Calendar.YEAR), cal.get(Calendar.MONTH),
				cal.get(Calendar.DAY_OF_MONTH), 0, 0, 0);
		return cal.getTimeInMillis();
	}

	public static String getDisplayDate(long time, String timeFormat) {
		SimpleDateFormat sdf = new SimpleDateFormat(timeFormat);
		return sdf.format(new Date(time));
	}

	/**
	 * 获取某天的开始时间
	 * 
	 * @param time
	 * @return
	 */
	public static long getDayStartTimer(long time) {
		Calendar c = Calendar.getInstance();
		c.setTimeInMillis(time);
		c.set(Calendar.HOUR_OF_DAY, 0);
		c.set(Calendar.MINUTE, 0);
		c.set(Calendar.SECOND, 0);
		c.set(Calendar.MILLISECOND, 0);
		return c.getTime().getTime();
	}

	/**
	 * 获取某天的截止时间
	 * 
	 * @param time
	 * @return
	 */
	public static long getDayEndTimer(long time) {
		Calendar c = Calendar.getInstance();
		c.setTimeInMillis(time);
		c.set(Calendar.HOUR_OF_DAY, 23);
		c.set(Calendar.MINUTE, 59);
		c.set(Calendar.SECOND, 59);
		c.set(Calendar.MILLISECOND, 999);
		return c.getTime().getTime();
	}

	/**
	 * 获取某周的起始时间
	 * 
	 * @param time
	 * @return
	 */
	public static long getWeekStartTimer(long time) {
		// Calendar c = Calendar.getInstance();
		// c.setTimeInMillis(time);
		// c.set(Calendar.HOUR_OF_DAY, 0);
		// c.set(Calendar.MINUTE, 0);
		// c.set(Calendar.SECOND, 0);
		// c.set(Calendar.MILLISECOND, 0);
		// c.set(Calendar.DAY_OF_WEEK, Calendar.MONDAY);
		// c.setFirstDayOfWeek(Calendar.MONDAY);

		Calendar c = Calendar.getInstance();
		c.setTimeInMillis(time);
		int week = c.get(Calendar.DAY_OF_WEEK) - 1;
		if (week == 0) {
			return getDayStartTimer(time) - AlarmManager.INTERVAL_DAY * 6;
		} else {
			c.set(Calendar.HOUR_OF_DAY, 0);
			c.set(Calendar.MINUTE, 0);
			c.set(Calendar.SECOND, 0);
			c.set(Calendar.MILLISECOND, 0);
			c.set(Calendar.DAY_OF_WEEK, Calendar.MONDAY);
			c.setFirstDayOfWeek(Calendar.MONDAY);
		}
		return c.getTimeInMillis();
	}

	/**
	 * 获取某周的截止时间
	 * 
	 * @param time
	 * @return
	 */
	public static long getWeekEndTimer(long time) {
		// Calendar c = Calendar.getInstance();
		// c.setTimeInMillis(time);
		// c.set(Calendar.HOUR_OF_DAY, 23);
		// c.set(Calendar.MINUTE, 59);
		// c.set(Calendar.SECOND, 59);
		// c.set(Calendar.MILLISECOND, 999);
		// c.set(Calendar.DAY_OF_WEEK, Calendar.SUNDAY);
		// c.setFirstDayOfWeek(Calendar.MONDAY);
		// return c.getTimeInMillis();

		Calendar c = Calendar.getInstance();
		c.setTimeInMillis(time);
		int week = c.get(Calendar.DAY_OF_WEEK) - 1;
		if (week == 0) {
			return getDayEndTimer(time);
		} else {
			c.set(Calendar.HOUR_OF_DAY, 23);
			c.set(Calendar.MINUTE, 59);
			c.set(Calendar.SECOND, 59);
			c.set(Calendar.MILLISECOND, 999);
			c.set(Calendar.DAY_OF_WEEK, Calendar.SUNDAY);
			c.setFirstDayOfWeek(Calendar.MONDAY);
		}
		return c.getTimeInMillis();
	}

	/**
	 * 获取某月的起始时间
	 * 
	 * @param time
	 * @return
	 */
	public static long getMonthStartTimer(long time) {
		Calendar c = Calendar.getInstance();
		c.setTimeInMillis(time);
		c.set(Calendar.HOUR_OF_DAY, 0);
		c.set(Calendar.MINUTE, 0);
		c.set(Calendar.SECOND, 0);
		c.set(Calendar.MILLISECOND, 0);
		c.set(Calendar.DATE, 1);
		return c.getTimeInMillis();
	}

	/**
	 * 获取某月的截止时间
	 * 
	 * @param time
	 * @return
	 */
	public static long getMonthEndTimer(long time) {
		Calendar c = Calendar.getInstance();
		c.setTimeInMillis(time);
		c.set(Calendar.HOUR_OF_DAY, 23);
		c.set(Calendar.MINUTE, 59);
		c.set(Calendar.SECOND, 59);
		c.set(Calendar.MILLISECOND, 999);
		c.set(Calendar.DATE, 1);
		c.add(Calendar.MONTH, 1);
		c.add(Calendar.DATE, -1);
		return c.getTimeInMillis();
	}

	public static long getMonthStartTimer(int year, int month) {
		Calendar c = Calendar.getInstance();
		c.set(Calendar.YEAR, year);
		c.set(Calendar.MONTH, month);
		c.set(Calendar.HOUR_OF_DAY, 0);
		c.set(Calendar.MINUTE, 0);
		c.set(Calendar.SECOND, 0);
		c.set(Calendar.MILLISECOND, 0);
		c.set(Calendar.DATE, 1);
		return c.getTimeInMillis();
	}

	public static long getMonthEndTimer(int year, int month) {
		Calendar c = Calendar.getInstance();
		c.set(Calendar.YEAR, year);
		c.set(Calendar.MONTH, month);
		c.set(Calendar.HOUR_OF_DAY, 23);
		c.set(Calendar.MINUTE, 59);
		c.set(Calendar.SECOND, 59);
		c.set(Calendar.MILLISECOND, 999);
		c.set(Calendar.DATE, 1);
		c.add(Calendar.MONTH, 1);
		c.add(Calendar.DATE, -1);
		return c.getTimeInMillis();
	}
}
