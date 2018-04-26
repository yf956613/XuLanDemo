package com.xulan.demo.shipping;

import java.util.ArrayList;
import java.util.List;

import org.json.JSONException;
import org.json.JSONObject;

import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

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
 * 
 * <pre>
 * Description	海运抵达
 * Author:		hexiuhui
 * </pre>
 */
public class ArrivalBySeaActivity extends BaseActivity {

	private EditText ship_saillings;
	private EditText telperson_text;
	private EditText sea_edt_taskname;
	private EditText phone_number_text;
	private EditText ship_saillings_name;
	private EditText edtShip_space;
	private EditText edtCount;
	private String taskId;
	private String link_no;

	@Override
	protected void onBaseCreate(Bundle savedInstanceState) {
		setContentViewId(R.layout.activity_arrival_by_sea, this);
	}

	@Override
	public void initView() {

		ship_saillings = (EditText) findViewById(R.id.ship_saillings);
		telperson_text = (EditText) findViewById(R.id.telperson_text);
		sea_edt_taskname = (EditText) findViewById(R.id.sea_edt_taskname);
		phone_number_text = (EditText) findViewById(R.id.phone_number_text);
		ship_saillings_name = (EditText) findViewById(R.id.ship_saillings_name);
		edtShip_space = (EditText) findViewById(R.id.ship_space);

		edtCount = (EditText) findViewById(R.id.count_txt);

		link_no = getIntent().getStringExtra("link_no");
		taskId = getIntent().getStringExtra("taskCode");
		sea_edt_taskname.setText(getIntent().getStringExtra("taskName"));
	}

	@Override
	public void initData() {

		setTitle(getResources().getString(R.string.arrival));
		getArriveData(taskId);
	}

	//	/**
	//	 * 任务名称选择
	//	 * @param v
	//	 */
	//	public void chooseTask(View v){
	//		Intent intent = new Intent(mContext, TaskListActivity.class);
	//		intent.putExtra("type", Constant.SCAN_TYPE_SEA);
	//		intent.putExtra("link_no", 2);
	//		startActivityForResult(intent, Constant.SELECT_TASK);
	//	}

	public void getArriveData(String task_id){

		JSONObject jsonObject = new JSONObject();
		try {
			jsonObject.put("task_id", task_id);
			jsonObject.put("type", Constant.SCAN_TYPE_SEA);
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
					
					ship_saillings_name.setText(jsonObject.optString("Freighter_Name"));
					ship_saillings.setText(jsonObject.optString("Sailing_No"));
					edtShip_space.setText(jsonObject.optString("ClassNumber"));
					
					phone_number_text.setText(jsonObject.optString("Link_Phone"));
					telperson_text.setText(jsonObject.optString("Link_Name"));
					edtCount.setText(jsonObject.optInt("GoodsCount") + "");
				}
			}
		});
	}
	
	//	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
	//		if(requestCode == Constant.SELECT_TASK && resultCode == RESULT_OK) {
	//			sea_edt_taskname.setText(data.getStringExtra("taskName"));
	//			taskId = data.getStringExtra("taskCode");
	//			getArriveData(data.getStringExtra("taskCode"));
	//		}
	//	}

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
						Toast.makeText(ArrivalBySeaActivity.this, "解析错误", 1).show();
						e.printStackTrace();
					}
				} else {
				}
			}
		};

		CustomProgress.showDialog(ArrivalBySeaActivity.this, getResources().getString(R.string.searching), false, null);
		LoadTextNetTask task = UserService.arrive(list, taskId, listener, null);
		return task;
	}

	/**
	 * 提交
	 * @param v
	 */
	public void commit(View v){

		String strTaskName = sea_edt_taskname.getText().toString();
		if(TextUtils.isEmpty(strTaskName)){
			CommandTools.showToast(getResources().getString(R.string.select_task));
			return;
		}

		String ship_saillings_name_value = ship_saillings_name.getText().toString();
		String ship_saillings_value = ship_saillings.getText().toString();
		String telperson_text_value = telperson_text.getText().toString();
		String phone_number_text_value = phone_number_text.getText().toString();
		String count_txt_value = edtCount.getText().toString();

		ScanData data = new ScanData();
		data.setTaskId(taskId);
		data.setTaskName(strTaskName);
		data.setCacheId(CommandTools.getUUID());
		data.setShipping_space(ship_saillings_value);
		data.setSaillings_name(ship_saillings_name_value);
		data.setTelPerson(telperson_text_value);
		data.setDeiverPhone(phone_number_text_value);
		data.setPlanCount(count_txt_value);
		data.setScanTime(CommandTools.getTime());
		data.setCreateTime(CommandTools.getTime());
		data.setScaned("1");
		data.setUploadStatus("0");

		List<ScanData> scanDataList = new ArrayList<ScanData>();
		scanDataList.add(data);

		requestUpload(scanDataList, taskId);
	}
}
