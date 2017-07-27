package com.janino.jsportstep.db;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.text.format.Time;

import com.janino.jsportstep.Constants;
import com.janino.jsportstep.entity.WalkDataInfo;
import com.janino.jsportstep.entity.WalkDataInfoPerHour;
import com.janino.jsportstep.step.Pedometer;
import com.janino.jsportstep.utils.TimeUtil;
import com.lidroid.xutils.DbUtils;
import com.lidroid.xutils.db.sqlite.Selector;
import com.lidroid.xutils.exception.DbException;
import com.lidroid.xutils.util.LogUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.TimeZone;

/**
 * @Description
 */

public class StepDBManger {
	public static DbUtils mDbUtils;
	public static Context mContext;

	public static DbUtils getDefaultDbUtils(Context context) {
		if (mDbUtils == null) {
			mContext = context;
			mDbUtils = DbUtils.create(context, Constants.DB_NAME);
			mDbUtils.configAllowTransaction(true);
		}
		return mDbUtils;
	}

	/**
	 * @Description 查询所有历史每日数据
	 */

	public static List<WalkDataInfo> loadWalkDataInfoList(long lastStepDayTime,
														  long lastSynTime) {
		if (null == mDbUtils) {
			return null;
		}
		try {
			if (mDbUtils.tableIsExist(WalkDataInfo.class)) {
				List<WalkDataInfo> list = mDbUtils.findAll(Selector
						.from(WalkDataInfo.class)
						.where("walkTime", "<", lastStepDayTime)
						.and("walkTime", ">", lastSynTime));
				return list;
			}
		} catch (DbException e) {
			e.printStackTrace();
		}
		return null;
	}

	/**
	 * @Description 查询30天历史记录
	 */

	public static List<WalkDataInfo> loadWalkDataInfoList() {
		if (null == mDbUtils) {
			return null;
		}
		try {
			List<WalkDataInfo> list = new ArrayList<WalkDataInfo>();
			if (mDbUtils.tableIsExist(WalkDataInfo.class)) {
				list = mDbUtils.findAll(Selector.from(WalkDataInfo.class)
						.where("walkTime", ">=", TimeUtil.getThirtyDayTime())
						.orderBy("walkTime"));
				// 最后的数据补全，数据为0时补0size()
				if (list != null && list.size() > 0) {
					perHour2Day(list, TimeUtil.getTimefromStr(list.get(list
							.size() - 1).walkDate), TimeUtil.getNowTime());
				}
			}
			// 某日数据为0时补0
			fillDayList(list, TimeUtil.getThirtyDayTime(),
					TimeUtil.getNowTime());

			return list;

		} catch (DbException e) {
			e.printStackTrace();
		}
		return null;
	}

	/**
	 * @Description 数据汇总 每小时数据汇总 每日数据，数据为0时补0
	 */

	private static void perHour2Day(List<WalkDataInfo> list, long starttime,
			long endtime) {
		if (list == null)
			list = new ArrayList<WalkDataInfo>();

		List<WalkDataInfoPerHour> tmplist = loadWalkDataInfoPerHourAll(
				starttime, endtime);
		if (tmplist == null || tmplist.size() <= 0)
			return;

		// 倒叙，最后的时间点
		long finddatetime = tmplist.get(tmplist.size() - 1).walkTime + 1;
		int i = 0;
		for (i = tmplist.size() - 1; i >= 0; i--) {
			WalkDataInfoPerHour tmp = tmplist.get(i);

			if (tmp.walkTime < finddatetime) {// 保留数据
			// if(finddatetime == TimeUtil.getDayTime(finddatetime)){
			// finddatetime -= 24 * 60 * 60 * 1000;
			// } else {
				finddatetime = TimeUtil.getDayTime(finddatetime);
				// }
			} else {
				tmplist.remove(i);
			}
		}

		if (tmplist != null && tmplist.size() > 0 && list != null
				&& list.size() > 0) {
			if (Time.getJulianDay(tmplist.get(0).walkTime, TimeZone
					.getDefault().getRawOffset() / 1000) == Time.getJulianDay(
					list.get(list.size() - 1).walkTime, TimeZone.getDefault()
							.getRawOffset() / 1000)) {
				// 本地数据与服务器数据进行目标天数同步
				tmplist.get(0).targetStepCount = (list.get(list.size() - 1)).targetStepCount;
				list.remove(list.size() - 1);
			}
		}

		list.addAll(DataChange(tmplist));
	}

	/**
	 * @Description 填补数据，数据为0时补0
	 */

	private static void fillDayList(List<WalkDataInfo> list, long starttime,
			long endtime) {
		// 后一天的0：00
		long lastdatetime = TimeUtil.getDayTime(endtime) + 24 * 60 * 60 * 1000;
		int i = 0;
		if (list != null && list.size() > 0) {
			for (i = list.size() - 1; i >= 0;) {
				WalkDataInfo tmp = list.get(i);
				if (tmp.walkTime < starttime) {
					break;
				}
				// 判断是否在后天的0点之前，和当天的0点之后(包括0点)。
				if (tmp.walkTime < lastdatetime
						&& (lastdatetime - 24 * 60 * 60 * 1000) <= tmp.walkTime) {
					lastdatetime -= 24 * 60 * 60 * 1000;
					i--;
				} else if (tmp.walkTime < lastdatetime) {
					lastdatetime -= 24 * 60 * 60 * 1000;
					WalkDataInfo tmpnew = new WalkDataInfo();
					tmpnew.calories = 0;
					tmpnew.distance = 0;
					tmpnew.stepCount = 0;
					// 找到当日当天的0:00 进行数据填充
					tmpnew.walkTime = lastdatetime;
					tmpnew.targetStepCount = 0;
					tmpnew.walkDate = TimeUtil.getstrfromDate(lastdatetime);
					list.add(i + 1, tmpnew);
				} else {
					list.remove(i);
					i--;
				}
			}
			if (i >= 0) {
				for (int j = 0; j > i; i++) {
					list.remove(0);
				}
			}
		} else {
			list = null;
			list = new ArrayList<WalkDataInfo>();
			lastdatetime -= 24 * 60 * 60 * 1000;
			WalkDataInfo tmpnew = new WalkDataInfo();
			tmpnew.calories = 0;
			tmpnew.distance = 0;
			tmpnew.stepCount = 0;
			tmpnew.walkTime = lastdatetime;
			tmpnew.targetStepCount = 0;
			tmpnew.walkDate = TimeUtil.getstrfromDate(lastdatetime);
			list.add(tmpnew);
		}
	}

	/**
	 * @Description 保存WalkDataInfoList 单挑记录---未使用
	 */

	public static boolean saveWalkDataInfo(int stepcount, double distance,
			long walkTime, double calories) {
		if (null == mDbUtils) {
			return false;
		}
		try {
			WalkDataInfo info = new WalkDataInfo();
			info.stepCount = stepcount;
			info.distance = distance;
			info.walkTime = walkTime;
			info.calories = calories;
			mDbUtils.saveOrUpdate(info);
			return true;
		} catch (DbException e) {
			e.printStackTrace();
		}
		return false;
	}

	public static class SaveWalkDataInfoTask extends
			AsyncTask<Void, Void, Boolean> {
		int stepcount;
		double distance;
		long walkTime;
		double calories;

		public SaveWalkDataInfoTask(int st, double di, long wt, double cal) {
			stepcount = st > 0 ? st : 0;
			distance = di > 0 ? di : 0;
			walkTime = wt > 0 ? wt : 0;
			calories = cal > 0 ? cal : 0;
		}

		@Override
		protected Boolean doInBackground(Void... params) {
			Boolean result = saveWalkDataInfo(stepcount, distance, walkTime,
					calories);
			return result;
		}

		@Override
		protected void onPostExecute(Boolean result) {
			LogUtils.e("SaveWalkDataInfoListTask onPostExecute");
		}
	}

	/**
	 * @Description 保存saveWalkDataInfoPerHour 保存小时时间到前一毫秒 0:00-0:59 作为一个时间段。
	 *              23:00-23:59作为一个时间段。
	 */

	public static boolean saveWalkDataInfoPerHour(int stepcount,
			double distance, long walkTime, double calories) {
		if (null == mDbUtils) {
			return false;
		}
		try {
			WalkDataInfoPerHour info = new WalkDataInfoPerHour();
			info.stepCount = stepcount;
			info.distance = distance;
			info.walkTime = walkTime;
			info.calories = calories;
			info.targetStepCount = Pedometer.getTargetStep(mContext);
			mDbUtils.saveOrUpdate(info);
			return true;
		} catch (DbException e) {
			e.printStackTrace();
		}
		return false;
	}

	/**
	 * @Description 当日数据保存 保存saveWalkDataInfoPerHour
	 */

	public static boolean saveWalkDataInfoDaily(int stepcount, double distance,
			long walkTime, double calories) {
		if (null == mDbUtils) {
			return false;
		}
		try {
			WalkDataInfoPerHour info = new WalkDataInfoPerHour();
			info.stepCount = stepcount;
			info.distance = distance;
			// 整点小时时间 == 当日时间 即（23点或者0点 == 0点）
			info.walkTime = TimeUtil.getHourTime(walkTime) == TimeUtil
					.getDayTime(walkTime) ? TimeUtil.getDayTime(walkTime) - 1
					: walkTime;
			// 隔天判断后造成，小于5分钟，即 23:55，作为前一日数据进行存储
			if (walkTime - TimeUtil.getDayTime(walkTime) > 5 * 60 * 1000
					&& walkTime - TimeUtil.getDayTime(walkTime) < 24 * 60 * 60
							* 1000 - 5 * 60 * 1000) {
				info.walkTime = TimeUtil.getDayTime(walkTime) - 1;
			}
			info.calories = calories;
			info.targetStepCount = Pedometer.getTargetStep(mContext);
			mDbUtils.saveOrUpdate(info);
			return true;
		} catch (DbException e) {
			e.printStackTrace();
		}
		return false;
	}

	public static class SaveWalkDataInfoPerHourTask extends
			AsyncTask<Void, Void, Boolean> {
		int stepcount;
		double distance;
		long walkTime;
		double calories;
		boolean isdaily = false;

		public SaveWalkDataInfoPerHourTask(int st, double di, long wt,
				double cal) {
			stepcount = st > 0 ? st : 0;
			distance = di > 0 ? di : 0;
			walkTime = wt > 0 ? wt : 0;
			calories = cal > 0 ? cal : 0;
		}

		public void setDailySave() {
			isdaily = true;
		}

		@Override
		protected Boolean doInBackground(Void... params) {
			Boolean result;
			if (isdaily) {
				result = saveWalkDataInfoDaily(stepcount, distance, walkTime,
						calories);
			} else {
				result = saveWalkDataInfoPerHour(stepcount, distance, walkTime,
						calories);
			}
			return result;
		}

		@Override
		protected void onPostExecute(Boolean result) {
			LogUtils.e("SaveWalkDataInfoPerHourTask onPostExecute");
		}
	}

	public static List<WalkDataInfoPerHour> loadWalkDataInfoPerHourAll(
			long starttime, long endtime) {
		if (null == mDbUtils) {
			return null;
		}
		try {
			if (mDbUtils.tableIsExist(WalkDataInfoPerHour.class)) {
				List<WalkDataInfoPerHour> list = mDbUtils.findAll(Selector
						.from(WalkDataInfoPerHour.class)
						.where("walkTime", "<", endtime)
						.and("walkTime", ">", starttime).orderBy("walkTime"));
				return list;
			}
		} catch (DbException e) {
			e.printStackTrace();
		}
		return null;
	}

	/**
	 * @Description 保存saveWalkDataInfoList 网络更新后同步本地数据 以服务器数据为准进行更新
	 */

	public static boolean saveWalkDataInfoList(List<WalkDataInfo> alist) {
		if (null == mDbUtils) {
			return false;
		}
		try {
			if (alist == null || alist.size() < 0) {
				cleanWalkDataInfoList(0, 0);
				return false;
			} else {
				// cleanList(
				// TimeUtil.getTimefromStr(alist.get(0).walkDate),
				// TimeUtil.getTimefromStr(alist.get(alist.size() -
				// 1).walkDate));
				cleanWalkDataInfoList(0, 0);
			}
			mDbUtils.saveOrUpdateAll(alist);
			return true;
		} catch (DbException e) {
			e.printStackTrace();
		}
		return false;
	}

	/**
	 * @Description 按照日期清空已有的DB数据 -- WalkDataInfo
	 * @param timefromStr
	 *            --- 目前未使用，考虑是否需要按照某些时间条件进行删除
	 */

	private static void cleanWalkDataInfoList(long timefromStr,
			long timefromStr2) {
		if (null == mDbUtils) {
			return;
		}
		try {
			if (mDbUtils.tableIsExist(WalkDataInfo.class)) {
				// mDbUtils.deleteAll(mDbUtils.findAll(Selector.from(WalkDataInfo.class)));
				mDbUtils.dropTable(WalkDataInfo.class);
			}
			return;
		} catch (DbException e) {
			e.printStackTrace();
		}
	}

	/**
	 * @Description 清空带上传的DB数据-- WalkDataInfo
	 */

	public static void cleanWalkDataInfoPerHourList() {
		if (null == mDbUtils) {
			return;
		}
		try {
			if (mDbUtils.tableIsExist(WalkDataInfoPerHour.class)) {
				// mDbUtils.deleteAll(mDbUtils.findAll(Selector.from(WalkDataInfo.class)));
				mDbUtils.dropTable(WalkDataInfoPerHour.class);
			}
			return;
		} catch (DbException e) {
			e.printStackTrace();
		}
	}

	/**
	 * @Description 保存记步数据--------前台网络请求数据以后执行
	 */

	public static class saveWalkDataInfoListTask extends
			AsyncTask<Void, Void, Boolean> {
		List<WalkDataInfo> mlist;

		public saveWalkDataInfoListTask(List<WalkDataInfo> alist) {
			mlist = alist;
		}

		@Override
		protected Boolean doInBackground(Void... params) {
			Boolean result = saveWalkDataInfoList(mlist);
			return result;
		}

		@Override
		protected void onPostExecute(Boolean result) {
			LogUtils.e("saveWalkDataInfoListTask onPostExecute");
		}
	}

	/**
	 * @Description 保存并获得记步数据--------前台网络请求数据以后执行
	 */

	public static class saveAndLoadWalkDataInfoListTask extends
			AsyncTask<Void, Void, Boolean> {
		List<WalkDataInfo> mlist;
		LoadWalkDataInfoListTaskListener mlsn;

		public saveAndLoadWalkDataInfoListTask(List<WalkDataInfo> alist,
				LoadWalkDataInfoListTaskListener lsn) {
			mlist = alist;
			mlsn = lsn;
		}

		@Override
		protected Boolean doInBackground(Void... params) {
			Boolean result = saveWalkDataInfoList(mlist);
			return result;
		}

		@Override
		protected void onPostExecute(Boolean result) {
			LogUtils.e("saveAndLoadWalkDataInfoListTask onPostExecute");
			new LoadWalkDataInfoListTask(mlsn).execute();
		}
	}

	/**
	 * @Description 查询历史每日数据--------前台使用
	 */

	public static class LoadWalkDataInfoListTask extends
			AsyncTask<Void, Void, List<WalkDataInfo>> {
		long mlastStepDayTime = 0;
		long mlastSynTime = 0;
		LoadWalkDataInfoListTaskListener mlsn;

		public LoadWalkDataInfoListTask(long lastStepDayTime, long lastSynTime,
				LoadWalkDataInfoListTaskListener lsn) {
			mlastStepDayTime = lastStepDayTime > 0 ? lastStepDayTime : 0;
			mlastSynTime = lastSynTime > 0 ? lastSynTime : 0;
			mlsn = lsn;
		}

		public LoadWalkDataInfoListTask(LoadWalkDataInfoListTaskListener lsn) {
			mlsn = lsn;
		}

		@Override
		protected List<WalkDataInfo> doInBackground(Void... params) {
			List<WalkDataInfo> result = new ArrayList<WalkDataInfo>();
			if (mlastStepDayTime != 0 || mlastSynTime != 0) {
				result = loadWalkDataInfoList(mlastStepDayTime, mlastSynTime);
			} else {
				result = loadWalkDataInfoList();
			}
			return result;
		}

		@Override
		protected void onPostExecute(List<WalkDataInfo> result) {
			LogUtils.e("LoadWalkDataInfoListTask onPostExecute");
			if (mlsn != null) {
				mlsn.onPost(result);
			}
		}
	}

	public static interface LoadWalkDataInfoListTaskListener {
		void onPost(List<WalkDataInfo> result);
	}

	/**
	 * @Description 查询所有需要更新的每小时数据---最大查询25*6条
	 */

	public static List<WalkDataInfoPerHour> LoadWalkDataInfoPerHour(
			long lastStepDayTime, long lastSynTime) {
		if (null == mDbUtils) {
			return null;
		}
		try {
			if (mDbUtils.tableIsExist(WalkDataInfoPerHour.class)) {
				List<WalkDataInfoPerHour> list = mDbUtils.findAll(Selector
						.from(WalkDataInfoPerHour.class)
						.where("walkTime", "<=", lastStepDayTime)
						.and("walkTime", ">", lastSynTime)
						.orderBy("walkTime", true).limit(25 * 6).offset(0));
				return list;
			}
		} catch (DbException e) {
			e.printStackTrace();
		}
		return null;
	}

	public static class LoadWalkDataInfoPerHourTask extends
			AsyncTask<Void, Void, List<WalkDataInfo>> {
		long mlastStepDayTime = 0;
		long mlastSynTime = 0;
		LoadWalkDataInfoListTaskListener mlsn;

		public LoadWalkDataInfoPerHourTask(long lastStepDayTime,
				long lastSynTime, LoadWalkDataInfoListTaskListener lsn) {
			mlastStepDayTime = lastStepDayTime > 0 ? lastStepDayTime : 0;
			mlastSynTime = lastSynTime > 0 ? lastSynTime : 0;
			mlsn = lsn;
		}

		@Override
		protected List<WalkDataInfo> doInBackground(Void... params) {
			List<WalkDataInfo> result = new ArrayList<WalkDataInfo>();
			LogUtils.d("LoadWalkDataInfoPerHourTask mlastStepDayTime:"
					+ mlastStepDayTime + ", mlastSynTime:" + mlastSynTime);
			if (mlastStepDayTime != 0 || mlastSynTime != 0) {
				// 转换服务器更新时需要的结构体
				// perHour2Day( result,mlastStepDayTime, mlastSynTime );
				result = DataChange(LoadWalkDataInfoPerHour(mlastStepDayTime,
						mlastSynTime));
			}
			return result;
		}

		@Override
		protected void onPostExecute(List<WalkDataInfo> result) {
			LogUtils.e("LoadWalkDataInfoPerHourTask onPostExecute");
			if (mlsn != null) {
				mlsn.onPost(result);
			}
		}
	}

	public static List<WalkDataInfo> DataChange(List<WalkDataInfoPerHour> data) {
		List<WalkDataInfo> datalist = new ArrayList<WalkDataInfo>();

		if (data == null || data.size() <= 0)
			return datalist;

		for (WalkDataInfoPerHour tmp : data) {
			WalkDataInfo tmpdata = new WalkDataInfo();
			// targetStepCount?
			tmpdata.calories = tmp.calories;
			tmpdata.distance = tmp.distance;
			tmpdata.stepCount = tmp.stepCount;
			tmpdata.walkTime = tmp.walkTime;
			tmpdata.targetStepCount = tmpdata.targetStepCount;
			datalist.add(tmpdata);
		}
		return datalist;
	}

	public static long getLocalTotleDistance(Context ctx,
			List<WalkDataInfo> alist) {
		if (alist == null || alist.size() <= 1) {
			return 0;
		}

		SharedPreferences editor = ctx.getSharedPreferences("state", 0);
		long lastSynTime = editor.getLong("lastSynTime", -1);

		if (lastSynTime == -1)
			return 0;

		int pos = alist.size() - 2;
		long distance = 0;
		for (pos = alist.size() - 2; pos > 0; pos--) {
			if (alist.get(pos).walkTime >= TimeUtil.getDayTime(lastSynTime)) {
				distance += alist.get(pos).distance;
			} else {
				break;
			}
		}
		return distance;
	}

}
