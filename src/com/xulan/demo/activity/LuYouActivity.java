package com.xulan.demo.activity;

import java.util.ArrayList;
import java.util.List;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;
import com.lidroid.xutils.ViewUtils;
import com.lidroid.xutils.view.annotation.ViewInject;
import com.xulan.demo.MyApplication;
import com.xulan.demo.R;
import com.xulan.demo.activity.action.MainMenuActivity;
import com.xulan.demo.data.ProjectInfo;
import com.xulan.demo.net.AsyncNetTask;
import com.xulan.demo.net.LoadTextNetTask;
import com.xulan.demo.net.LoadTextResult;
import com.xulan.demo.net.NetTaskResult;
import com.xulan.demo.net.AsyncNetTask.OnPostExecuteListener;
import com.xulan.demo.service.UserService;
import com.xulan.demo.util.CommandTools;
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

	final List<ProjectInfo> projectList = new ArrayList<ProjectInfo>();
	private LoadTextNetTask mTaskRequestGetProject;
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
	 * 选择项目
	 * @param v
	 */
	public void selectProgram(View v){
		
		SingleItemDialog.showProJectDialog(mContext, getResources().getString(R.string.select_project), false, projectList, new SingleItemCallBack() {

			@Override
			public void callback(int pos) {
				
				btnProgram.setText(projectList.get(pos).getProject_name());
				MyApplication.m_projectId = projectList.get(pos).getProject_id();
			}
		});
	}

	/**
	 * 确认
	 * @param v
	 */
	public void confirm(View v){
		
		MyApplication.m_userInfo.setLink_1(true);
		MyApplication.m_userInfo.setLink_2(true);
		
		strNodeType = "进口车";
		Intent intent = new Intent(LuYouActivity.this, MainMenuActivity.class);
		intent.putExtra("action", strNodeType);//根据节点类型判断
		startActivity(intent);
//		checkFirstNode();
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
