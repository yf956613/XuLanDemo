package com.xulan.demo.activity.action.land;

import java.util.ArrayList;
import java.util.List;

import org.json.JSONException;
import org.json.JSONObject;

import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.lidroid.xutils.ViewUtils;
import com.lidroid.xutils.view.annotation.ViewInject;
import com.xulan.demo.R;
import com.xulan.demo.activity.BaseActivity;
import com.xulan.demo.data.ScanData;
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

/** 
 * 陆运到达
 * 
 * @author yxx
 *
 * @date 2016-12-20 下午2:42:16
 * 
 */
public class LandArriveActivity extends BaseActivity {

	@ViewInject(R.id.land_arrive_edt_1) TextView tvTaskName;
	@ViewInject(R.id.land_arrive_edt_2) EditText edtCarNumber;
	@ViewInject(R.id.land_arrive_edt_3) EditText edtCarCount;
	@ViewInject(R.id.land_arrive_edt_3_1) EditText edtLinkMan;
	@ViewInject(R.id.land_arrive_edt_4) EditText edtPhone;
	@ViewInject(R.id.land_arrive_edt_5) EditText edtCount;
	@ViewInject(R.id.land_arrive_edt_4_1) EditText edtDriverPhone;

	private String link_no;
	private String taskId;
	private String link_man = "";
	private String link_phone = "";

	@Override
	protected void onBaseCreate(Bundle savedInstanceState) {
		setContentViewId(R.layout.activity_land_arrive, this);
		ViewUtils.inject(this);
	}

	@Override
	public void initView() {

		link_no = getIntent().getStringExtra("link_no");
		taskId = getIntent().getStringExtra("taskCode");
		tvTaskName.setText(getIntent().getStringExtra("taskName"));
		link_man = getIntent().getStringExtra("link_man");
		link_phone = getIntent().getStringExtra("link_phone");
		
		edtLinkMan.setText(link_man+"");
		edtPhone.setText(link_phone+"");
	}

	@Override
	public void initData() {

		setTitle(getResources().getString(R.string.arrival));
		getArriveData(taskId);
	}

	public void getArriveData(String task_id){

		JSONObject jsonObject = new JSONObject();
		try {
			jsonObject.put("task_id", task_id);
			jsonObject.put("type", Constant.SCAN_TYPE_LAND);
		} catch (JSONException e) {
			e.printStackTrace();
		}

		CustomProgress.showDialog(mContext, "数据获取中", false, null);
		RequestUtil.postDataIfToken(mContext, "action/arrive", jsonObject, false, new RequestCallback() {

			@Override
			public void callback(int res, String remark, JSONObject jsonObject) {
				// TODO 自动生成的方法存根
				CustomProgress.dissDialog();
				CommandTools.showToast(remark);
				if(res == 0){

					jsonObject = jsonObject.optJSONObject("data");
					Log.i("hexiuhui----", jsonObject.toString());

					edtCarNumber.setText(jsonObject.optString("License_Plate"));
					edtCarCount.setText(jsonObject.optString("Car_No"));
					edtCount.setText(jsonObject.optInt("GoodsCount") + "");
					edtDriverPhone.setText(jsonObject.optString("Driver_Phone"));
				}
			}
		});
	}

	/**
	 * 上传数据
	 */
	protected LoadTextNetTask requestUpload(List<ScanData> list, String taskId) {

		OnPostExecuteListener listener = new OnPostExecuteListener() {

			@Override
			public void onPostExecute(AsyncNetTask t, NetTaskResult result, Object tag) {

				CustomProgress.dissDialog();
				if (result.m_nResultCode == 0) {
					LoadTextResult mresult = (LoadTextResult) result;
					try {
						JSONObject jsonObj = new JSONObject(mresult.m_strContent);

						Logs.i("hexiuhui---", jsonObj.toString());

						int success = jsonObj.getInt("success");
						String message = jsonObj.getString("message");
						CommandTools.showToast(message);

						if (success == 0) {

							finish();
						} else {

						}
					} catch (JSONException e) {
						Toast.makeText(LandArriveActivity.this, "解析错误", 1).show();
						e.printStackTrace();
					}
				} else {
				}
			}
		};

		CustomProgress.showDialog(LandArriveActivity.this, getResources().getString(R.string.searching), false, null);
		LoadTextNetTask task = UserService.arrive(list, taskId, listener, null);
		return task;
	}

	/**
	 * 提交
	 * @param v
	 */
	public void sureArrive(View v){

		String strTaskName = tvTaskName.getText().toString();
		if(TextUtils.isEmpty(strTaskName)){
			CommandTools.showToast(getResources().getString(R.string.select_task));
			return;
		}

		String edt_CarNumber = edtCarNumber.getText().toString();
		String edt_CarCount = edtCarCount.getText().toString();
		String telperson_text_value = edtPhone.getText().toString();
		String count_txt_value = edtCount.getText().toString();

		ScanData data = new ScanData();
		data.setTaskName(strTaskName);
		data.setCacheId(CommandTools.getUUID());
		data.setTelPerson(telperson_text_value);
		data.setVehicleNumbers(edt_CarNumber);
		data.setTrain(edt_CarCount);
		data.setPlanCount(count_txt_value);
		data.setScanTime(CommandTools.getTime());
		data.setCreateTime(CommandTools.getTime());
		data.setScaned("1");
		data.setUploadStatus("0");
		data.setTaskId(taskId);

		List<ScanData> scanDataList = new ArrayList<ScanData>();
		scanDataList.add(data);

		requestUpload(scanDataList, taskId);
	}

	//	/**
	//	 * 任务名称选择
	//	 * @param v
	//	 */
	//	public void chooseTask(View v){
	//		Intent intent = new Intent(mContext, TaskListActivity.class);
	//		intent.putExtra("type", Constant.SCAN_TYPE_LAND);
	//		intent.putExtra("link_no", link_no);
	//		startActivityForResult(intent, Constant.SELECT_TASK);
	//	}
}
