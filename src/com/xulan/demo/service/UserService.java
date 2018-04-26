package com.xulan.demo.service;

import java.util.List;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.graphics.Bitmap;
import android.util.Log;

import com.xulan.demo.MyApplication;
import com.xulan.demo.data.ScanData;
import com.xulan.demo.net.LoadTextNetTask;
import com.xulan.demo.net.NetTaskUtil;
import com.xulan.demo.net.NetUtil;
import com.xulan.demo.net.AsyncNetTask.OnPostExecuteListener;
import com.xulan.demo.takephoto.Bimp;
import com.xulan.demo.util.CommandTools;
import com.xulan.demo.util.Constant;
import com.xulan.demo.util.DataUtilTools;


/**
 * 
 * @author HeXiuHui
 * 
 */
public class UserService {

	/** 获取项目列表 **/
	public static LoadTextNetTask getProject(String userId, String mPlatFormId, OnPostExecuteListener listener, Object tag) {
		String url = "project/getprojectlist";

		JSONObject object = new JSONObject();
		try {
			object.put("user_id", userId);
			object.put("platform_id", mPlatFormId);
		} catch (Exception e) {
			e.printStackTrace();
		}

		return NetTaskUtil.makeTextNetTask(Constant.URL + url, object.toString(), NetUtil.NOT_TOKEN, listener, tag);
	}

	/** 获取路由列表 **/
	public static LoadTextNetTask getLuYou(String userId, String projectId, OnPostExecuteListener listener, Object tag) {
		String url = "route/getroutelist";

		JSONObject object = new JSONObject();
		try {
			object.put("user_id", userId);
			object.put("project_id", projectId);
		} catch (Exception e) {
			e.printStackTrace();
		}

		return NetTaskUtil.makeTextNetTask(Constant.URL + url, object.toString(), NetUtil.NOT_TOKEN, listener, tag);
	}
	
	/** 获取节点列表 **/
	public static LoadTextNetTask getNote(String userId, String routeId, OnPostExecuteListener listener, Object tag) {
		String url = "node/getnodelist";

		JSONObject object = new JSONObject();
		try {
			object.put("user_id", userId);
			object.put("route_id", routeId);
		} catch (Exception e) {
			e.printStackTrace();
		}

		return NetTaskUtil.makeTextNetTask(Constant.URL + url, object.toString(), NetUtil.NOT_TOKEN, listener, tag);
	}

	/** 陆运--装货 **/
	public static LoadTextNetTask getLand(String userId, String task_id, int flag, OnPostExecuteListener listener, Object tag) {
		String url = "action/getgoodsdetail";

		JSONObject object = new JSONObject();
		try {
			object.put("type", MyApplication.m_scan_type);
			object.put("user_id", userId);
			object.put("project_id", MyApplication.m_projectId);
			object.put("node_id", MyApplication.m_nodeId);
			object.put("task_id", task_id);
			object.put("link_num", MyApplication.m_link_num);
			object.put("node_num", MyApplication.m_node_num);
			object.put("flag", MyApplication.m_flag);

		} catch (Exception e) {
			e.printStackTrace();
		}

		return NetTaskUtil.makeTextNetTask(Constant.URL + url, object.toString(), NetUtil.NOT_TOKEN, listener, tag);
	}

	/** 获取任务状态跟踪数据 **/
	public static LoadTextNetTask getTaskState(OnPostExecuteListener listener, Object tag) {
		String url = "node/task/gettaskstatuslist";

		JSONObject object = new JSONObject();
		try {
			object.put("type", MyApplication.m_scan_type);
			object.put("user_id", MyApplication.m_userID);
			object.put("node_id", MyApplication.m_nodeId);
			object.put("route_id", MyApplication.m_routeId);
			object.put("link_num", MyApplication.m_link_num);
			object.put("node_num", MyApplication.m_node_num);

		} catch (Exception e) {
			e.printStackTrace();
		}

		return NetTaskUtil.makeTextNetTask(Constant.URL + url, object.toString(), NetUtil.NOT_TOKEN, listener, tag);
	}

	/** 获取任务列表 **/
	public static LoadTextNetTask getTaskList(String userId, String nodeID, int nodeNum, String type, OnPostExecuteListener listener, Object tag) {
		String strUrl = "action/gettasklist";
		JSONObject jsonObject = new JSONObject();
		try {

			jsonObject.put("user_id", userId);// 用户id
			jsonObject.put("route_id", MyApplication.m_routeId);// 路由id
			jsonObject.put("node_id", MyApplication.m_nodeId);// 节点id
			jsonObject.put("type", type);
			jsonObject.put("link_num", MyApplication.m_physic_link_num);// 操作环节
			jsonObject.put("node_num", MyApplication.m_node_num);// 当前是否第一节点
			jsonObject.put("flag", MyApplication.m_flag);// 
		} catch (JSONException e) {
			e.printStackTrace();
		}

		return NetTaskUtil.makeTextNetTask(Constant.URL + strUrl, jsonObject.toString(), NetUtil.NOT_TOKEN, listener, tag);
	}

	/** 上传数据**/
	public static LoadTextNetTask upload(List<ScanData> list, String taskId, String scan_type, OnPostExecuteListener listener, Object tag) {
		String strUrl = "upload/scandata";
		JSONObject jsonObject = new JSONObject();
		try {
			jsonObject.put("operation_type", "upload_scandata");
			jsonObject.put("scan_type", MyApplication.m_scan_type);
			JSONObject header = new JSONObject();
			header.put("route_task_id", taskId);
			header.put("upload_time", CommandTools.getTime());
			header.put("load_number", list.size());
			header.put("link_no", MyApplication.m_physic_link_num);
			header.put("node_no", MyApplication.m_node_num);
			header.put("route_points_id", MyApplication.m_nodeId);
			header.put("id", CommandTools.getUUID());
			header.put("scan_id", CommandTools.getUUID());
			header.put("scan_user", MyApplication.m_userName);
			header.put("scan_user_id", MyApplication.m_userID);
			header.put("scan_time", CommandTools.getTime());
			header.put("gps_coordx", Constant.longitude);
			header.put("gps_coordy", Constant.latitude);
			header.put("device_id", CommandTools.getMIME(MyApplication.getInstance()));
			header.put("project_id", MyApplication.m_projectId);
			header.put("flag", MyApplication.m_flag);

			jsonObject.put("header", header);

			JSONArray jsonArray = new JSONArray();
			for (int i = 0; i < list.size(); i++) {

				ScanData data = list.get(i);
				JSONObject jo = new JSONObject();

				jo.put("scan_id", CommandTools.getUUID());
				jo.put("scan_detail_id", CommandTools.getUUID());

				jo.put("CacheId", data.getCacheId());
				jo.put("taskName", data.getTaskName());
				jo.put("taskId", data.getTaskId());
				jo.put("ScanType", data.getScanType());
				jo.put("ScanTime", data.getScanTime());// NetUtil.getToken()
				jo.put("createTime", data.getCreateTime());
				jo.put("packBarcode", data.getPackBarcode());
				jo.put("packNumber", data.getPackNumber());
				jo.put("VehicleNumbers", data.getVehicleNumbers());
				jo.put("train", data.getTrain());
				jo.put("deiverPhone", data.getDeiverPhone());
				jo.put("Memo", data.getMemo());
				jo.put("loginId", data.getLoginId());
				jo.put("MainGoodsId", data.getMainGoodsId());
				jo.put("GoodsName", data.getPackName());
				jo.put("MinutePackBarcode", data.getMinutePackBarcode());
				jo.put("MinutePackNumber", data.getMinutePackNumber());
				jo.put("packMode", data.getPackMode());
				jo.put("loginName", data.getLoginName());
				jo.put("PlanCount", data.getPlanCount());
				jo.put("PracticalCount", data.PracticalCount);
				jo.put("libraryNumber", data.libraryNumber);
				jo.put("libraryAdamin", data.getLibraryAdamin());
				jo.put("Saillings_name", data.getSaillings_name());
				jo.put("Saillings", data.getSaillings());
				jo.put("shipping_space", data.getShipping_space());
				jo.put("flight", data.getFlight());
				jo.put("voyage", data.getVoyage());
				jo.put("wagonNumber", data.wagonNumber);
				jo.put("container_no", data.getContainer_no());
				jo.put("freighter_name", data.getFreighter_name());
				jo.put("sailing_no", data.getSailing_no());
				jo.put("Length", data.getLength());
				jo.put("Width", data.getWidth());
				jo.put("Height", data.getHeight());
				jo.put("Weight", data.getWeight());
				jo.put("V3", data.getV3());
				jo.put("ChargeTon", data.getCharge_Ton());

				jo.put("container", data.getContainer_no());
				jo.put("company", data.getCompany());
				jo.put("company_id", data.getCompany_id());
				jo.put("TelPerson", data.getTelPerson());
				jo.put("AbnormalLink", data.getAbnormalLink());
				jo.put("Scaned", data.getScaned());
				jo.put("AbnormalCause", data.getAbnormalCause());
				jo.put("ReturnedCargoPic", data.getReturnedCargoPic());
				jo.put("ReturnedCargoFile", data.getReturnedCargoFile());
				jo.put("UploadStatus", data.getUploadStatus());

				jo.put("linkMan", data.getLinkMan());
				jo.put("linkPhone", data.getLinkPhone());

				jsonArray.put(jo);
			}

			jsonObject.put("detail", jsonArray);
		} catch (JSONException e) {
			e.printStackTrace();
		}

		return NetTaskUtil.makeTextNetTask(Constant.URL + strUrl, jsonObject.toString(), NetUtil.NOT_TOKEN, listener, tag);
	}

	/** 上传数据**/
	public static LoadTextNetTask arrive(List<ScanData> list, String taskId, OnPostExecuteListener listener, Object tag) {
		String strUrl = "upload/arrive";
		JSONObject jsonObject = new JSONObject();
		try {
			jsonObject.put("operation_type", "upload_scandata");
			jsonObject.put("scan_type", MyApplication.m_scan_type);
			JSONObject header = new JSONObject();
			header.put("route_task_id", taskId);
			header.put("upload_time", CommandTools.getTime());
			header.put("load_number", list.size());
			header.put("link_no", MyApplication.m_physic_link_num);
			header.put("node_no", MyApplication.m_node_num);
			header.put("route_points_id", MyApplication.m_nodeId);
			header.put("id", CommandTools.getUUID());
			header.put("scan_id", CommandTools.getUUID());
			header.put("scan_user", MyApplication.m_userName);
			header.put("scan_user_id", MyApplication.m_userID);
			header.put("scan_time", CommandTools.getTime());
			header.put("gps_coordx", Constant.longitude);
			header.put("gps_coordy", Constant.latitude);
			header.put("device_id", CommandTools.getMIME(MyApplication.getInstance()));
			header.put("flag", MyApplication.m_flag);

			jsonObject.put("header", header);

			JSONArray jsonArray = new JSONArray();
			for (int i = 0; i < list.size(); i++) {
				ScanData data = list.get(i);
				JSONObject jo = new JSONObject();

				jo.put("scan_id", CommandTools.getUUID());
				jo.put("scan_detail_id", CommandTools.getUUID());

				jo.put("CacheId", data.getCacheId());
				jo.put("taskName", data.getTaskName());
				jo.put("taskId", data.getTaskId());
				jo.put("ScanType", data.getScanType());
				jo.put("ScanTime", data.getCreateTime());// NetUtil.getToken()
				jo.put("createTime", data.getCreateTime());
				jo.put("packBarcode", data.getPackBarcode());
				jo.put("packNumber", data.getPackNumber());
				jo.put("VehicleNumbers", data.getVehicleNumbers());
				jo.put("train", data.getTrain());
				jo.put("deiverPhone", data.getDeiverPhone());
				jo.put("Memo", data.getMemo());
				jo.put("loginId", data.getLoginId());

				jo.put("MainGoodsId", data.getMainGoodsId());

				jo.put("loginName", data.getLoginName());
				jo.put("PlanCount", data.getPlanCount());
				jo.put("PracticalCount", data.PracticalCount);
				jo.put("libraryNumber", data.libraryNumber);
				jo.put("libraryAdamin", data.getLibraryAdamin());
				jo.put("Saillings_name", data.getSaillings_name());
				jo.put("Saillings", data.getSaillings());
				jo.put("shipping_space", data.getShipping_space());
				jo.put("flight", data.getFlight());
				jo.put("voyage", data.getVoyage());
				jo.put("wagonNumber", data.wagonNumber);

				jo.put("container", data.getContainer_no());
				jo.put("company", data.getCompany());
				jo.put("TelPerson", data.getTelPerson());
				jo.put("AbnormalLink", data.getAbnormalLink());
				jo.put("Scaned", data.getScaned());
				jo.put("AbnormalCause", data.getAbnormalCause());
				jo.put("ReturnedCargoPic", data.getReturnedCargoPic());
				jo.put("ReturnedCargoFile", data.getReturnedCargoFile());
				jo.put("UploadStatus", data.getUploadStatus());

				jsonArray.put(jo);
			}

			jsonObject.put("detail", jsonArray);

		} catch (JSONException e) {
			e.printStackTrace();
		}

		return NetTaskUtil.makeTextNetTask(Constant.URL + strUrl, jsonObject.toString(), NetUtil.NOT_TOKEN, listener, tag);
	}

	/** 上传数据**/
	public static LoadTextNetTask exception(List<ScanData> list, String taskId, int link_num, OnPostExecuteListener listener, Object tag) {
		String strUrl = "upload/exception";

		JSONObject jsonObject = new JSONObject();
		try {
			jsonObject.put("operation_type", "upload_scandata");
			jsonObject.put("scan_type", MyApplication.m_scan_type);
			JSONObject header = new JSONObject();
			header.put("route_task_id", taskId);
			header.put("upload_time", CommandTools.getTime());
			header.put("load_number", list.size());
			header.put("link_no", link_num);
			header.put("route_points_id", MyApplication.m_nodeId);
			header.put("id", CommandTools.getUUID());
			header.put("scan_id", CommandTools.getUUID());
			header.put("scan_user", MyApplication.m_userName);
			header.put("scan_user_id", MyApplication.m_userID);
			header.put("scan_time", CommandTools.getTime());
			header.put("gps_coordx", Constant.longitude);
			header.put("gps_coordy", Constant.latitude);
			header.put("device_id", CommandTools.getMIME(MyApplication.getInstance()));

			jsonObject.put("header", header);

			JSONArray jsonArray = new JSONArray();
			for (int i = 0; i < list.size(); i++) {
				ScanData data = list.get(i);
				JSONObject jo = new JSONObject();

				jo.put("scan_id", CommandTools.getUUID());
				jo.put("scan_detail_id", CommandTools.getUUID());

				jo.put("CacheId", data.getCacheId());
				jo.put("taskName", data.getTaskName());
				jo.put("ScanType", data.getScanType());
				jo.put("ScanTime", data.getCreateTime());
				jo.put("createTime", data.getCreateTime());

				jo.put("packBarcode", data.getPackBarcode());
				jo.put("packNumber", data.getPackNumber());

				jo.put("VehicleNumbers", data.getVehicleNumbers());
				jo.put("train", data.getTrain());
				jo.put("deiverPhone", data.getDeiverPhone());
				jo.put("Memo", data.getMemo());

				jo.put("MainGoodsId", data.getMainGoodsId());

				jo.put("PlanCount", data.getPlanCount());
				jo.put("PracticalCount", data.PracticalCount);
				jo.put("libraryNumber", data.libraryNumber);
				jo.put("libraryAdamin", data.getLibraryAdamin());
				jo.put("Saillings_name", data.getSaillings_name());
				jo.put("Saillings", data.getSaillings());
				jo.put("shipping_space", data.getShipping_space());
				jo.put("flight", data.getFlight());

				jo.put("TelPerson", data.getTelPerson());
				jo.put("AbnormalLink", data.getAbnormalLink());
				jo.put("Scaned", data.getScaned());

				jo.put("markPic", CommandTools.getPicJsonArray(data.getPicture()));//拍照

				jsonArray.put(jo);
			}

			jsonObject.put("detail", jsonArray);

		} catch (JSONException e) {
			e.printStackTrace();
		}

		return NetTaskUtil.makeTextNetTask(Constant.URL + strUrl, jsonObject.toString(), NetUtil.NOT_TOKEN, listener, tag);
	}
	

	/** 拍照上传**/
	public static LoadTextNetTask takephoto(List<ScanData> list, int link_num, OnPostExecuteListener listener, Object tag) {

		String strUrl = "upload/takephoto";

		JSONObject jsonObject = new JSONObject();
		try {
			jsonObject.put("operation_type", "upload_scandata");
			jsonObject.put("scan_type", MyApplication.m_scan_type);
			JSONObject header = new JSONObject();
			header.put("route_task_id", "");
			header.put("upload_time", CommandTools.getTime());
			header.put("load_number", list.size());
			header.put("link_no", link_num);
			header.put("route_points_id", MyApplication.m_nodeId);
			header.put("id", CommandTools.getUUID());
			header.put("scan_id", CommandTools.getUUID());
			header.put("scan_user", MyApplication.m_userName);
			header.put("scan_user_id", MyApplication.m_userID);
			header.put("scan_time", CommandTools.getTime());
			header.put("gps_coordx", Constant.longitude);
			header.put("gps_coordy", Constant.latitude);
			header.put("device_id", CommandTools.getMIME(MyApplication.getInstance()));

			jsonObject.put("header", header);

			JSONArray jsonArray = new JSONArray();
			for (int i = 0; i < list.size(); i++) {
				ScanData data = list.get(i);
				JSONObject jo = new JSONObject();

				jo.put("scan_id", CommandTools.getUUID());
				jo.put("scan_detail_id", CommandTools.getUUID());

				jo.put("CacheId", data.getCacheId());
				jo.put("taskName", data.getTaskName());
				jo.put("ScanType", data.getScanType());
				jo.put("ScanTime", data.getScanTime());
				jo.put("createTime", data.getCreateTime());

				jo.put("packBarcode", data.getPackBarcode());
				jo.put("packNumber", data.getPackNumber());

				jo.put("VehicleNumbers", data.getVehicleNumbers());
				jo.put("train", data.getTrain());
				jo.put("deiverPhone", data.getDeiverPhone());
				jo.put("Memo", data.getMemo());

				jo.put("MainGoodsId", data.getMainGoodsId());

				jo.put("PlanCount", data.getPlanCount());
				jo.put("PracticalCount", data.PracticalCount);
				jo.put("libraryNumber", data.libraryNumber);
				jo.put("libraryAdamin", data.getLibraryAdamin());
				jo.put("Saillings_name", data.getSaillings_name());
				jo.put("Saillings", data.getSaillings());
				jo.put("shipping_space", data.getShipping_space());
				jo.put("flight", data.getFlight());

				jo.put("TelPerson", data.getTelPerson());
				jo.put("AbnormalLink", data.getAbnormalLink());
				jo.put("Scaned", data.getScaned());

				jo.put("markPic", CommandTools.getPicJsonArray(data.getPicture()));//拍照

				jsonArray.put(jo);
			}

			jsonObject.put("detail", jsonArray);

		} catch (JSONException e) {
			e.printStackTrace();
		}

		return NetTaskUtil.makeTextNetTask(Constant.URL + strUrl, jsonObject.toString(), NetUtil.NOT_TOKEN, listener, tag);
	}

	/** 获取应载入数，应扫描数，等几个数量 **/
	public static LoadTextNetTask getloadNum(String task_id, OnPostExecuteListener listener, Object tag) {
		String url = "action/getloadnum";

		JSONObject object = new JSONObject();
		try {

			object.put("user_id", MyApplication.m_userID);
			object.put("node_id", MyApplication.m_nodeId);
			object.put("task_id", task_id);
			object.put("link_num", MyApplication.m_link_num);
			object.put("node_num", MyApplication.m_node_num);
			object.put("flag", MyApplication.m_flag);
		} catch (Exception e) {
			e.printStackTrace();
		}

		return NetTaskUtil.makeTextNetTask(Constant.URL + url, object.toString(), NetUtil.NOT_TOKEN, listener, tag);
	}

	/** 获取装卸公司**/
	public static LoadTextNetTask getCompanylist(OnPostExecuteListener listener, Object tag) {
		String url = "action/getcompanylist";

		JSONObject object = new JSONObject();
		try {

			object.put("platform_id", MyApplication.m_platform_id);
		} catch (Exception e) {
			e.printStackTrace();
		}

		return NetTaskUtil.makeTextNetTask(Constant.URL + url, object.toString(), NetUtil.NOT_TOKEN, listener, tag);
	}

	/** 获取打尺数据比对**/
	public static LoadTextNetTask getolddata(String barcode, OnPostExecuteListener listener, Object tag) {
		String url = "service/scale/getolddata";

		JSONObject object = new JSONObject();
		try {
			object.put("pack_barcode", barcode);
			object.put("project_id", MyApplication.m_projectId);
		} catch (Exception e) {
			e.printStackTrace();
		}

		return NetTaskUtil.makeTextNetTask(Constant.URL + url, object.toString(), NetUtil.NOT_TOKEN, listener, tag);
	}

	/** 获取打尺数据比对**/
	public static LoadTextNetTask getsumpackageinfo(List<ScanData> list, OnPostExecuteListener listener, Object tag) {
		String url = "action/pack/getsumpackageinfo";

		JSONObject jsonObject = new JSONObject();
		try {
			JSONObject header = new JSONObject();
			header.put("project_id", MyApplication.m_projectId);
			header.put("node_id", MyApplication.m_nodeId);

			jsonObject.put("header", header);

			JSONArray jsonArray = new JSONArray();
			for (int i = 0; i < list.size(); i++) {

				ScanData data = list.get(i);
				JSONObject jo = new JSONObject();
				jo.put("packBarcode", data.getMinutePackBarcode());
				jo.put("packNumber", data.getMinutePackNumber());

				jsonArray.put(jo);
			}

			jsonObject.put("detail", jsonArray);
		} catch (JSONException e) {
			e.printStackTrace();
		}

		return NetTaskUtil.makeTextNetTask(Constant.URL + url, jsonObject.toString(), NetUtil.NOT_TOKEN, listener, tag);
	}

	/** 获取打尺数据比对**/
	public static LoadTextNetTask updatePassword(String userName, String password1, String password2, OnPostExecuteListener listener, Object tag) {
		String url = "user/updatepassword";

		JSONObject object = new JSONObject();
		try {

			object.put("username", userName);
			object.put("userpwd", password1);
			object.put("new_userpwd", password2);
		} catch (Exception e) {
			e.printStackTrace();
		}

		return NetTaskUtil.makeTextNetTask(Constant.URL + url, object.toString(), NetUtil.NOT_TOKEN, listener, tag);
	}

	/** 获取微信扫码URL**/
	public static LoadTextNetTask getWXURL(OnPostExecuteListener listener, Object tag) {
		String url = "action/ruler/wxurl";

		JSONObject object = new JSONObject();
		try {
			object.put("node_id", MyApplication.m_nodeId);

		} catch (Exception e) {
			e.printStackTrace();
		}

		return NetTaskUtil.makeTextNetTask(Constant.URL + url, object.toString(), NetUtil.NOT_TOKEN, listener, tag);
	}
	
	/** 获取微信扫码URL**/
	public static LoadTextNetTask getSumpackageinfobybarcode(String barcode, OnPostExecuteListener listener, Object tag) {
		String url = "action/pack/getsumpackageinfobybarcode";

		JSONObject jsonObject = new JSONObject();
		try {
			JSONObject header = new JSONObject();
			header.put("project_id", MyApplication.m_projectId);

			jsonObject.put("header", header);

			JSONArray jsonArray = new JSONArray();
			JSONObject jo = new JSONObject();
			jo.put("packBarcode", barcode);

			jsonArray.put(jo);

			jsonObject.put("detail", jsonArray);
		} catch (JSONException e) {
			e.printStackTrace();
		}

		return NetTaskUtil.makeTextNetTask(Constant.URL + url, jsonObject.toString(), NetUtil.NOT_TOKEN, listener, tag);
	}
	
}
