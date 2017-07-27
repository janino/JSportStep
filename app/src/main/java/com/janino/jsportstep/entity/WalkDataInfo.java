/**   
 * @Title: WalkDataInfo.java 
 * @Package: com.pajk.hm.sdk.android.entity 
 * @Description: TODO

 * @date 2014-12-2 下午3:09:25 
 */

package com.janino.jsportstep.entity;

import com.lidroid.xutils.db.annotation.Column;
import com.lidroid.xutils.db.annotation.Id;
import com.lidroid.xutils.db.annotation.NoAutoIncrement;
import com.lidroid.xutils.db.annotation.Table;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.Serializable;

@Table(name = "walkdata_info")
public class WalkDataInfo implements Serializable {

	/** @Fields serialVersionUID: */

	private static final long serialVersionUID = 8818101589895754246L;

	/**
	 * 步数
	 */
	@Column(column = "stepCount")
	public int stepCount;
	/**
	 * 目标数
	 */
	@Column(column = "targetStepCount")
	public int targetStepCount;
	/**
	 * 里程数
	 */
	@Column(column = "distance")
	public double distance;

	/**
	 * 消耗的卡路里
	 */
	@Column(column = "calories")
	public double calories;

	/**
	 * 客户端记步日期long时间戳
	 */
	@Column(column = "walkTime")
	@Id
	@NoAutoIncrement
	public long walkTime;

	/**
	 * 服务端记步日期yyyyMMdd
	 */
	@Column(column = "walkDate")
	public String walkDate;

	/**
	 * 反序列化函数，用于从json字符串反序列化本类型实例
	 */
	public static WalkDataInfo deserialize(String json) throws JSONException {
		if (json != null && !json.isEmpty()) {
			return deserialize(new JSONObject(json));
		}
		return null;
	}

	/**
	 * 反序列化函数，用于从json节点对象反序列化本类型实例
	 */
	public static WalkDataInfo deserialize(JSONObject json)
			throws JSONException {
		if (json != null && json != JSONObject.NULL && json.length() > 0) {
			WalkDataInfo result = new WalkDataInfo();

			// 步数
			result.stepCount = json.optInt("stepCount");
			// 目标数
			result.targetStepCount = json.optInt("targetStepCount");
			// 里程数
			result.distance = json.optDouble("distance");
			// 消耗的卡路里
			result.calories = json.optDouble("calories");
			// 客户端记步日期long时间戳
			result.walkTime = json.optLong("walkTime");
			// 服务端记步日期yyyyMMdd

			if (!json.isNull("walkDate")) {
				result.walkDate = json.optString("walkDate", null);
			}
			return result;
		}
		return null;
	}

	/*
	 * 序列化函数，用于从对象生成数据字典
	 */
	public JSONObject serialize() throws JSONException {
		JSONObject json = new JSONObject();

		// 步数
		json.put("stepCount", this.stepCount);

		// 目标数
		json.put("targetStepCount", this.targetStepCount);

		// 里程数
		json.put("distance", this.distance);

		// 消耗的卡路里
		json.put("calories", this.calories);

		// 客户端记步日期long时间戳
		json.put("walkTime", this.walkTime);

		// 服务端记步日期yyyyMMdd
		if (this.walkDate != null) {
			json.put("walkDate", this.walkDate);
		}

		return json;
	}
}
