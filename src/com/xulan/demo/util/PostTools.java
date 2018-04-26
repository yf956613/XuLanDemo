package com.xulan.demo.util;

import java.util.List;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.xulan.demo.MyApplication;
import com.xulan.demo.data.LinkInfo;
import com.xulan.demo.data.ScanNumInfo;
import com.xulan.demo.data.TaskInfo;
import com.xulan.demo.util.RequestUtil.RequestCallback;
import com.xulan.demo.view.CustomProgress;

import android.content.Context;
import android.text.TextUtils;

public class PostTools {

	public static abstract class ObjectCallback {
		public abstract void callback(int res, String remark, Object object);
	}

	/**
	 * 获取应载入数，实际载入数，应扫码数
	 * @param mContext
	 * @param taskId	任务ID
	 * @param objectCallback
	 */
	public static void getLoadNumber(Context mContext, String taskId, final ObjectCallback objectCallback){

		JSONObject jsonObject = new JSONObject();
		try {

			jsonObject.put("user_id", MyApplication.m_userID);
			jsonObject.put("node_id", MyApplication.m_nodeId);
			jsonObject.put("task_id", taskId);
			jsonObject.put("link_num", MyApplication.m_physic_link_num);
			jsonObject.put("node_num", MyApplication.m_node_num);
			jsonObject.put("flag", MyApplication.m_flag);
		} catch (JSONException e) {
			e.printStackTrace();
		}

		String strUrl = "action/getloadnum";
		RequestUtil.postDataIfToken(mContext, strUrl, jsonObject, false, new RequestCallback() {

			@Override
			public void callback(int res, String remark, JSONObject jsonObject) {

				if(res != 0){
					return;
				}

				jsonObject = jsonObject.optJSONObject("data");

				ScanNumInfo info = new ScanNumInfo();
				info.setMust_load_number(jsonObject.optInt("must_load_number", 0));
				info.setReal_load_number(jsonObject.optInt("real_load_number", 0));
				info.setMust_scan_number(jsonObject.optInt("must_scan_number", 0));

				objectCallback.callback(0, remark, info);
			}
		});
	}

	/**
	 * 查询可操作节点
	 * @param mContext
	 * @param linkList
	 */
	public static void getLink(Context mContext, final List<LinkInfo> linkList){

		JSONObject jsonObject = new JSONObject();
		try {
			jsonObject.put("node_id", MyApplication.m_nodeId);
		} catch (JSONException e) {
			e.printStackTrace();
		}

		String strUrl = "action/query/getlink";
		CustomProgress.showDialog(mContext, "数据获取中", true, null);
		RequestUtil.postDataIfToken(mContext, strUrl, jsonObject, false, new RequestCallback() {

			@Override
			public void callback(int res, String remark, JSONObject jsonObject) {

				CustomProgress.dissDialog();
				if(res != 0){
					return;
				}

				JSONArray jsonArray = jsonObject.optJSONArray("data");
				linkList.clear();
				for(int i=0; i<jsonArray.length(); i++){

					jsonObject = jsonArray.optJSONObject(i);

					String link_name = MyApplication.m_points_type + "-" + jsonObject.optString("link_name");//这里进行数据的拼接，如陆运-装车
					int link_num = Integer.parseInt(jsonObject.optString("link_no", "1"));

					LinkInfo info = new LinkInfo();
					info.setLinkName(link_name);
					info.setLinkNum(link_num);

					if(!TextUtils.isEmpty(link_name)){
						linkList.add(info);
					}
				}
			}
		});
	}

	/**
	 * 查询任务列表
	 * @param mContext
	 * @param linkList
	 */
	public static void getTaskName(Context mContext,int link_num, final List<TaskInfo> linkList){

		JSONObject jsonObject = new JSONObject();
		try {
			jsonObject.put("node_id", MyApplication.m_nodeId);
			jsonObject.put("link_num", link_num);
		} catch (JSONException e) {
			e.printStackTrace();
		}

		String strUrl = "action/query/gettaskname";
		CustomProgress.showDialog(mContext, "数据获取中", true, null);
		RequestUtil.postDataIfToken(mContext, strUrl, jsonObject, false, new RequestCallback() {

			@Override
			public void callback(int res, String remark, JSONObject jsonObject) {

				CustomProgress.dissDialog();
				if(res != 0){
					return;
				}

				JSONArray jsonArray = jsonObject.optJSONArray("data");
				linkList.clear();
				for(int i=0; i<jsonArray.length(); i++){

					jsonObject = jsonArray.optJSONObject(i);

					TaskInfo info = new TaskInfo();
					info.setTask_name(jsonObject.optString("Task_Name"));
					info.setTask_code(jsonObject.optString("Task_ID"));

					linkList.add(info);
				}
			}
		});
	}
}
