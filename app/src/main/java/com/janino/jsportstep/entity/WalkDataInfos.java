/**   

 */

package com.janino.jsportstep.entity;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class WalkDataInfos {

	/**
	 * 个人每天记步数据集合
	 */
	public List<WalkDataInfo> walkDataInfos;
	/**
	 * 总共跑的路程
	 */
	public double totalDistance;

	/**
	 * 活动描述前缀
	 */
	public String activityPrefix;

	/**
	 * 路程可兑换的钱
	 */
	public double money;

	/**
	 * 活动描述后缀
	 */
	public String activitySuffix;

	/**
	 * 反序列化函数，用于从json字符串反序列化本类型实例
	 */
	public static WalkDataInfos deserialize(String json) throws JSONException {
		if (json != null && !json.isEmpty()) {
			return deserialize(new JSONObject(json));
		}
		return null;
	}

	/**
	 * 反序列化函数，用于从json节点对象反序列化本类型实例
	 */
	public static WalkDataInfos deserialize(JSONObject json)
			throws JSONException {
		if (json != null && json != JSONObject.NULL && json.length() > 0) {
			WalkDataInfos result = new WalkDataInfos();

			// 个人每天记步数据集合
			JSONArray walkDataInfosArray = json.optJSONArray("walkDataInfos");
			if (walkDataInfosArray != null) {
				int len = walkDataInfosArray.length();
				result.walkDataInfos = new ArrayList<WalkDataInfo>(len);
				for (int i = 0; i < len; i++) {
					JSONObject jo = walkDataInfosArray.optJSONObject(i);
					if (jo != null && jo != JSONObject.NULL && jo.length() > 0) {
						result.walkDataInfos.add(WalkDataInfo.deserialize(jo));
					}
				}
			}

			// 总共跑的路程
			result.totalDistance = json.optDouble("totalDistance");
			// 活动描述前缀

			if (!json.isNull("activityPrefix")) {
				result.activityPrefix = json.optString("activityPrefix", null);
			}
			// 路程可兑换的钱
			result.money = json.optDouble("money");
			// 活动描述后缀

			if (!json.isNull("activitySuffix")) {
				result.activitySuffix = json.optString("activitySuffix", null);
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

		// 个人每天记步数据集合
		if (this.walkDataInfos != null) {
			JSONArray walkDataInfosArray = new JSONArray();
			for (WalkDataInfo value : this.walkDataInfos) {
				if (value != null) {
					walkDataInfosArray.put(value.serialize());
				}
			}
			json.put("walkDataInfos", walkDataInfosArray);
		}

		// 总共跑的路程
		json.put("totalDistance", this.totalDistance);

		// 活动描述前缀
		if (this.activityPrefix != null) {
			json.put("activityPrefix", this.activityPrefix);
		}

		// 路程可兑换的钱
		json.put("money", this.money);

		// 活动描述后缀
		if (this.activitySuffix != null) {
			json.put("activitySuffix", this.activitySuffix);
		}

		return json;
	}

}
