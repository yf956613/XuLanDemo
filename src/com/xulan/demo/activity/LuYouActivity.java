package com.xulan.demo.activity;

import java.util.ArrayList;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.lidroid.xutils.ViewUtils;
import com.lidroid.xutils.view.annotation.ViewInject;
import com.xulan.demo.MyApplication;
import com.xulan.demo.R;
import com.xulan.demo.activity.action.MainMenuActivity;
import com.xulan.demo.data.NoteInfo;
import com.xulan.demo.data.ProjectInfo;
import com.xulan.demo.data.RouteInfo;
import com.xulan.demo.net.AsyncNetTask;
import com.xulan.demo.net.LoadTextNetTask;
import com.xulan.demo.net.LoadTextResult;
import com.xulan.demo.net.NetTaskResult;
import com.xulan.demo.net.AsyncNetTask.OnPostExecuteListener;
import com.xulan.demo.service.UserService;
import com.xulan.demo.util.CommandTools;
import com.xulan.demo.util.Constant;
import com.xulan.demo.util.Logs;
import com.xulan.demo.util.RequestUtil;
import com.xulan.demo.util.RequestUtil.RequestCallback;
import com.xulan.demo.view.CustomProgress;
import com.xulan.demo.view.SingleItemDialog;
import com.xulan.demo.view.SingleItemDialog.SingleItemCallBack;

/** 
 * 项目路由选择
 * 
 * @author hexiuhui
 *
 * @date 2016-12-12 下午4:33:13
 * 
 */
public class LuYouActivity extends BaseActivity {

	@ViewInject(R.id.btn_luyou_program) Button btnProgram;
	@ViewInject(R.id.btn_luyou_luyou) Button btnLuYou;
	@ViewInject(R.id.btn_luyou_node) Button btnNode;

	final List<NoteInfo> noteList = new ArrayList<NoteInfo>();
	final List<RouteInfo> routeList = new ArrayList<RouteInfo>();
	final List<ProjectInfo> projectList = new ArrayList<ProjectInfo>();
	private LoadTextNetTask mTaskRequestGetProject;
	private LoadTextNetTask mTaskGetLuYou;
	private LoadTextNetTask mTaskGetNote;
	private String mPlatFormId;
	private String strNodeType;

	@Override
	protected void onBaseCreate(Bundle savedInstanceState) {
		setContentViewId(R.layout.activity_lu_you, this);
		ViewUtils.inject(this);
	}

	@Override
	public void initView() {
		mPlatFormId = getIntent().getStringExtra("platform_id");
	}

	@Override
	public void initData() {
		setTitle(getResources().getString(R.string.project_route));
		mTaskRequestGetProject = requestGetProject(MyApplication.getInstance().m_userID, mPlatFormId);
	}

	/**
	 * 获取项目信息
	 */
	protected LoadTextNetTask requestGetProject(String userId, String mPlatFormId) {
		OnPostExecuteListener listener = new OnPostExecuteListener() {
			@Override
			public void onPostExecute(AsyncNetTask t, NetTaskResult result, Object tag) {
				mTaskRequestGetProject = null;
				CustomProgress.dissDialog();
				if (result.m_nResultCode == 0) {
					LoadTextResult mresult = (LoadTextResult) result;
					try {
						JSONObject jsonObj = new JSONObject(mresult.m_strContent);

						Logs.i("hexiuhui---", jsonObj.toString());

						int success = jsonObj.getInt("success");
						if (success == 0) {
							JSONArray jsonArray = jsonObj.getJSONArray("data");
							for (int i = 0; i < jsonArray.length(); i++) {
								JSONObject jsonObject = jsonArray.getJSONObject(i);
								String projectId = jsonObject.getString("id");
								String projectName = jsonObject.getString("project_name");
								String projectCode = jsonObject.getString("project_code");

								ProjectInfo info = new ProjectInfo();
								info.setProject_code(projectCode);
								info.setProject_id(projectId);
								info.setProject_name(projectName);

								projectList.add(info);
							}
						} else {
							String message = jsonObj.getString("message");

							CommandTools.showToast(message);
						}
					} catch (JSONException e) {
						Toast.makeText(LuYouActivity.this, "解析错误", 1).show();
						e.printStackTrace();
					}
				} else {
				}
			}
		};

		CustomProgress.showDialog(LuYouActivity.this, getResources().getString(R.string.searching), false, null);
		LoadTextNetTask task = UserService.getProject(userId, mPlatFormId, listener, null);
		return task;
	}

	/**
	 * 获取路由信息
	 */
	protected LoadTextNetTask requestGetLuYou(String userId, String projectId) {
		OnPostExecuteListener listener = new OnPostExecuteListener() {
			@Override
			public void onPostExecute(AsyncNetTask t, NetTaskResult result, Object tag) {
				mTaskGetLuYou = null;
				CustomProgress.dissDialog();
				if (result.m_nResultCode == 0) {
					LoadTextResult mresult = (LoadTextResult) result;
					try {
						JSONObject jsonObj = new JSONObject(mresult.m_strContent);

						Logs.i("hexiuhui---", jsonObj.toString());

						int success = jsonObj.getInt("success");
						if (success == 0) {
							JSONArray jsonArray = jsonObj.getJSONArray("data");
							for (int i = 0; i < jsonArray.length(); i++) {
								JSONObject jsonObject = jsonArray.getJSONObject(i);
								int route_type = jsonObject.getInt("route_type");
								String route_name = jsonObject.getString("route_name");
								String route_id = jsonObject.getString("route_id");

								RouteInfo info = new RouteInfo();
								info.setRoute_id(route_id);
								info.setRoute_name(route_name);
								info.setRoute_type(route_type);

								routeList.add(info);
							}

						} else {
							String message = jsonObj.getString("message");

							CommandTools.showToast(message);
						}
					} catch (JSONException e) {
						Toast.makeText(LuYouActivity.this, "解析错误", 1).show();
						e.printStackTrace();
					}
				} else {
				}
			}
		};

		CustomProgress.showDialog(LuYouActivity.this, getResources().getString(R.string.searching), false, null);
		LoadTextNetTask task = UserService.getLuYou(userId, projectId, listener, null);
		return task;
	}

	/**
	 * 获取节点信息
	 */
	protected LoadTextNetTask requestGetNote(String userId, String routeId) {
		OnPostExecuteListener listener = new OnPostExecuteListener() {
			@Override
			public void onPostExecute(AsyncNetTask t, NetTaskResult result, Object tag) {
				mTaskGetNote = null;
				CustomProgress.dissDialog();
				if (result.m_nResultCode == 0) {
					LoadTextResult mresult = (LoadTextResult) result;
					try {
						JSONObject jsonObj = new JSONObject(mresult.m_strContent);

						Logs.i("hexiuhui---", jsonObj.toString());

						int success = jsonObj.getInt("success");
						if (success == 0) {
							JSONArray jsonArray = jsonObj.getJSONArray("data");
							for (int i = 0; i < jsonArray.length(); i++) {
								JSONObject jsonObject = jsonArray.getJSONObject(i);
								String points_type = jsonObject.getString("points_type");
								String points_name = jsonObject.getString("points_name");
								String id = jsonObject.getString("id");

								boolean link_1 = jsonObject.optBoolean("Link_1", false);
								boolean link_2 = jsonObject.optBoolean("Link_2", false);
								boolean link_3 = jsonObject.optBoolean("Link_3", false);

								NoteInfo info = new NoteInfo();
								info.setNote_id(id);
								info.setNote_name(points_name);
								info.setNote_type(points_type);

								info.setLink_1(link_1);
								info.setLink_2(link_2);
								info.setLink_3(link_3);

								noteList.add(info);
							}

						} else {
							String message = jsonObj.getString("message");

							CommandTools.showToast(message);
						}
					} catch (JSONException e) {
						Toast.makeText(LuYouActivity.this, "解析错误", 1).show();
						e.printStackTrace();
					}
				} else {
				}
			}
		};

		CustomProgress.showDialog(LuYouActivity.this, getResources().getString(R.string.searching), false, null);
		LoadTextNetTask task = UserService.getNote(userId, routeId, listener, null);
		return task;
	}

	/**
	 * 选择项目
	 * @param v
	 */
	public void selectProgram(View v){
		SingleItemDialog.showProJectDialog(mContext, getResources().getString(R.string.select_project), false, projectList, new SingleItemCallBack() {

			@Override
			public void callback(int pos) {
				btnProgram.setText(projectList.get(pos).getProject_name());
				MyApplication.m_projectId = projectList.get(pos).getProject_id();
				routeList.clear();
				mTaskGetLuYou = requestGetLuYou(MyApplication.getInstance().m_userID, projectList.get(pos).getProject_id());
			}
		});
	}

	/**
	 * 选择路由
	 * @param v
	 */
	public void selectLuYou(View v){

		if (TextUtils.isEmpty(MyApplication.m_projectId)) {
			Toast.makeText(LuYouActivity.this, getResources().getString(R.string.select_project), 1).show();
		} else {
			SingleItemDialog.showRouteDialog(mContext, getResources().getString(R.string.msg_select_luyou), false, routeList, new SingleItemCallBack() {

				@Override
				public void callback(int pos) {
					btnLuYou.setText(routeList.get(pos).getRoute_name());
					MyApplication.m_routeId = routeList.get(pos).getRoute_id();
					noteList.clear();
					mTaskGetNote = requestGetNote(MyApplication.m_userID, MyApplication.m_routeId);
				}
			});
		}

	}

	/**
	 * 选择节点
	 * @param v
	 */
	public void selectNode(View v){

		if (TextUtils.isEmpty(MyApplication.m_routeId)) {
			Toast.makeText(LuYouActivity.this, getResources().getString(R.string.msg_select_luyou), 1).show();
		} else {
			SingleItemDialog.showNoteDialog(mContext, getResources().getString(R.string.msg_select_node), false, noteList, new SingleItemCallBack() {

				@Override
				public void callback(int pos) {
					NoteInfo nodeInfo = noteList.get(pos);
					MyApplication.m_nodeId = nodeInfo.getNote_id();
					MyApplication.m_points_type = nodeInfo.getNote_type();
					strNodeType = nodeInfo.getNote_type();
					btnNode.setText(nodeInfo.getNote_name());
					
					if (strNodeType.contains("陆运")) {
						MyApplication.m_scan_type = Constant.SCAN_TYPE_LAND;
					} else if (strNodeType.contains("货场")) {
						MyApplication.m_scan_type = Constant.SCAN_TYPE_STORAGE;
					} else if (strNodeType.contains("铁运")) {
						MyApplication.m_scan_type = Constant.SCAN_TYPE_RAILEAY;
					} else if (strNodeType.contains("装卸")) {
						MyApplication.m_scan_type = Constant.SCAN_TYPE_LOAD;
					} else if (strNodeType.contains("海运")) {
						MyApplication.m_scan_type = Constant.SCAN_TYPE_SEA;
					} else if (strNodeType.contains("空运")) {
						MyApplication.m_scan_type = Constant.SCAN_TYPE_AIR;
					} else if (strNodeType.contains("打尺")) {
						MyApplication.m_scan_type = Constant.SCAN_TYPE_SCALE;
					} else if (strNodeType.contains("检验")) {
						MyApplication.m_scan_type =Constant.SCAN_TYPE_SCALE;
					} else if (strNodeType.contains("安装")) {
						MyApplication.m_scan_type = Constant.SCAN_TYPE_INSTALL;
					} else if (strNodeType.contains("包装")) {
						MyApplication.m_scan_type = Constant.SCAN_TYPE_PACK;
					} else if (strNodeType.contains("集装箱")) {
						MyApplication.m_scan_type = Constant.SCAN_TYPE_CONTAINER;
					} else if (strNodeType.contains("绑扎")) {
						MyApplication.m_scan_type = Constant.SCAN_TYPE_STRAP;
					} else if (strNodeType.contains("下线")) {
						MyApplication.m_scan_type = Constant.SCAN_TYPE_OFFLINE;
					}

					//设置环节
					MyApplication.m_userInfo.setLink_1(nodeInfo.isLink_1());
					MyApplication.m_userInfo.setLink_2(nodeInfo.isLink_2());
					MyApplication.m_userInfo.setLink_3(nodeInfo.isLink_3());
				}
			});
		}
	}

	/**
	 * 确认
	 * @param v
	 */
	public void confirm(View v){

		checkFirstNode();
	}

	/**
	 * 判断当前登陆人flag标志
	 */
	public void getUserFlag(){

		JSONObject jsonObject = new JSONObject();
		try {
			jsonObject.put("user_id", MyApplication.m_userID);
			jsonObject.put("node_id", MyApplication.m_nodeId);
		} catch (JSONException e) {
			e.printStackTrace();
		}

		RequestUtil.postDataIfToken(mContext, "action/getflag", jsonObject, false, new RequestCallback() {

			@Override
			public void callback(int res, String remark, JSONObject jsonObject) {
				// TODO 自动生成的方法存根
				CommandTools.showToast(remark);
				if(res == 0){

					jsonObject = jsonObject.optJSONObject("data");
					MyApplication.m_flag = jsonObject.optInt("flag");

					Intent intent = new Intent(LuYouActivity.this, MainMenuActivity.class);
					intent.putExtra("action", strNodeType);//根据节点类型判断
					startActivity(intent);
				}
			}
		});

	}

	/**
	 * 判断是否第一个节点
	 */
	public void checkFirstNode(){

		JSONObject jsonObject = new JSONObject();
		try {
			jsonObject.put("route_id", MyApplication.m_routeId);
			jsonObject.put("node_id", MyApplication.m_nodeId);
		} catch (JSONException e) {
			e.printStackTrace();
		}

		CustomProgress.showDialog(mContext, "数据获取中", false, null);
		RequestUtil.postDataIfToken(mContext, "node/checkfirstnode", jsonObject, false, new RequestCallback() {

			@Override
			public void callback(int res, String remark, JSONObject jsonObject) {
				// TODO 自动生成的方法存根
				CustomProgress.dissDialog();
				if(res == 0){

					jsonObject = jsonObject.optJSONObject("data");
					MyApplication.m_node_num = jsonObject.optInt("node_num");
					getUserFlag();
				}
			}
		});
	}

	/**
	 * 任务状态跟踪
	 * @param v
	 */
	public void taskFollowing(View v) {
		if (MyApplication.m_projectId.equals("") || MyApplication.m_routeId.equals("") || MyApplication.m_nodeId.equals("")) {
			CommandTools.showToast(getResources().getString(R.string.select_project_route_mode));
		} else{ 
			Intent intent = new Intent(LuYouActivity.this, TaskStateActivity.class);
			intent.putExtra("action", strNodeType);//根据节点类型判断
			startActivity(intent);
		}
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
		if (mTaskRequestGetProject != null) {
			mTaskRequestGetProject.cancel(true);
			mTaskRequestGetProject = null;
		}
	}
}
