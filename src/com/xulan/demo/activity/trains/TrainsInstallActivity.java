package com.xulan.demo.activity.trains;

import java.util.ArrayList;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.content.Intent;
import android.os.Bundle;
import android.os.Message;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.lidroid.xutils.ViewUtils;
import com.xulan.demo.MyApplication;
import com.xulan.demo.R;
import com.xulan.demo.activity.BaseActivity;
import com.xulan.demo.activity.action.TaskListActivity;
import com.xulan.demo.adapter.CommonAdapter;
import com.xulan.demo.adapter.ViewHolder;
import com.xulan.demo.camera.CaptureActivity;
import com.xulan.demo.data.ScanData;
import com.xulan.demo.data.ScanInfo;
import com.xulan.demo.data.ScanNumInfo;
import com.xulan.demo.db.dao.ScanDataDao;
import com.xulan.demo.net.AsyncNetTask;
import com.xulan.demo.net.LoadTextNetTask;
import com.xulan.demo.net.LoadTextResult;
import com.xulan.demo.net.NetTaskResult;
import com.xulan.demo.net.AsyncNetTask.OnPostExecuteListener;
import com.xulan.demo.pdascan.RFID;
import com.xulan.demo.service.UserService;
import com.xulan.demo.util.CommandTools;
import com.xulan.demo.util.Constant;
import com.xulan.demo.util.DataUtilTools;
import com.xulan.demo.util.HandleDataTools;
import com.xulan.demo.util.Logs;
import com.xulan.demo.util.PostTools;
import com.xulan.demo.util.VoiceHint;
import com.xulan.demo.util.PostTools.ObjectCallback;
import com.xulan.demo.view.CustomProgress;

/** 
 * 铁运 装货扫描
 * 
 * @author hexiuhui
 *
 */
public class TrainsInstallActivity extends BaseActivity implements OnClickListener {
	private EditText trains_edt_1;
	private EditText trains_edt_2;
	private EditText trains_edt_3;
	private EditText trains_edt_4;
	private EditText trains_edt_5;

	private RelativeLayout billCodeImg;

	private String taskId = "";
	private ListView mListView;
	private CommonAdapter<ScanData> commonAdapter;
	private List<ScanData> dataList = new ArrayList<ScanData>();
	private List<ScanData> uploadList = new ArrayList<ScanData>();

	private int scan_num = 0;
	private EditText edtCount1;
	private EditText edtCount2;
	private EditText edtCount3;
	private EditText edtCount4;

	private ScanDataDao mScandataDao = new ScanDataDao();

	@Override
	protected void onBaseCreate(Bundle savedInstanceState) {
		setContentViewId(R.layout.activity_trains_install_scan, this);
		ViewUtils.inject(this);

	}

	@Override
	public void initView() {
		billCodeImg = (RelativeLayout) findViewById(R.id.bill_code_img);
		mListView = (ListView) findViewById(R.id.lv_public);
		trains_edt_1 = (EditText) findViewById(R.id.trains_edt_1);
		trains_edt_2 = (EditText) findViewById(R.id.trains_edt_2);
		trains_edt_3 = (EditText) findViewById(R.id.trains_edt_3);
		trains_edt_4 = (EditText) findViewById(R.id.trains_edt_4);
		trains_edt_5 = (EditText) findViewById(R.id.trains_edt_5);

		edtCount1 = (EditText) findViewById(R.id.scan_count_1);
		edtCount2 = (EditText) findViewById(R.id.scan_count_2);
		edtCount3 = (EditText) findViewById(R.id.scan_count_3);
		edtCount4 = (EditText) findViewById(R.id.scan_count_4);

		//本地数据
		dataList = mScandataDao.getNotUploadDataList(MyApplication.m_scan_type, MyApplication.m_link_num + "", MyApplication.m_nodeId);

		scan_num = dataList.size();

		mListView.setAdapter(commonAdapter = new CommonAdapter<ScanData>(mContext, dataList, R.layout.land_item) {
			@Override
			public void convert(ViewHolder helper, ScanData item) {
				helper.setText(R.id.land_tv1, commonAdapter.getIndex());
				helper.setText(R.id.land_tv2, item.getPackBarcode());
				helper.setText(R.id.land_tv3, item.getPackNumber());
				helper.setText(R.id.land_tv4, item.getScanUser());
				helper.setText(R.id.land_tv5, item.getScanTime());
			}
		});

		mListView.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1, int arg2, long arg3) {

				ScanData scanData = dataList.get(arg2);

				trains_edt_4.setText(scanData.getPackBarcode());
				trains_edt_5.setText(scanData.getPackNumber());
			}
		});

		billCodeImg.setOnClickListener(this);
	}

	@Override
	public void initData() {
		setTitle(getIntent().getStringExtra("actionName"));

		setRightTitle(getResources().getString(R.string.submit));

		if(MyApplication.m_flag == 0 && HandleDataTools.getFirstLinkNum() == MyApplication.m_physic_link_num){
			requestGetShip(MyApplication.m_userID, taskId, 0);
		}
	}

	public void onEventMainThread(Message msg) {

		ScanInfo scanInfo = (ScanInfo) msg.obj;
		if(scanInfo.getWhat() == Constant.SCAN_DATA && scanInfo.getType().equals(Constant.SCAN_TYPE_RAILEAY)){

			String strBillcode = scanInfo.getBarcode();
			trains_edt_4.setText(strBillcode);

			ScanData scanData = DataUtilTools.checkScanData(Constant.SCAN_TYPE_RAILEAY, strBillcode, dataList);
			if (scanData != null) {
				trains_edt_4.setText(scanData.getPackBarcode());
				trains_edt_5.setText(scanData.getPackNumber());
				addData(null);
			} else {
				VoiceHint.playErrorSounds();
				CommandTools.showToast("条码不存在");
			}
		}
	}

	/**
	 * 任务名称选择
	 * @param v
	 */
	public void chooseTask(View v){
		Intent intent = new Intent(mContext, TaskListActivity.class);
		intent.putExtra("type", Constant.SCAN_TYPE_RAILEAY);
		intent.putExtra("link_no", MyApplication.m_link_num + "");
		startActivityForResult(intent, Constant.SELECT_TASK);
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.bill_code_img:
			// 扫描
			Intent openCameraIntent = new Intent(TrainsInstallActivity.this, CaptureActivity.class);
			startActivityForResult(openCameraIntent, Constant.CAPTURE_BILLCODE);
			break;

		default:
			break;
		}
	}

	protected void onActivityResult(int requestCode, int resultCode, Intent data) {

		if(requestCode == Constant.SELECT_TASK && resultCode == RESULT_OK) {
			scan_num = 0;
			trains_edt_1.setText(data.getStringExtra("taskName"));
			taskId = data.getStringExtra("taskCode");

			String car_plate = data.getStringExtra("car_plate");
			String car_count = data.getStringExtra("car_count");

			trains_edt_2.setText(car_plate);
			trains_edt_3.setText(car_count);

			trains_edt_4.setText("");
			trains_edt_5.setText("");

			requestGetShip(MyApplication.m_userID, taskId, MyApplication.m_flag);
		} else if (requestCode == Constant.CAPTURE_BILLCODE) {
			if (data == null) {
				return;
			}

			Bundle bundle = data.getExtras();
			String strBillcode = bundle.getString("result");

			checkData(strBillcode);
			return;
		}
	}

	public void checkData(String billcode){

		ScanData scanData = DataUtilTools.checkScanData(Constant.SCAN_TYPE_RAILEAY, billcode, dataList);
		if (scanData != null) {

			trains_edt_4.setText(scanData.getPackBarcode());
			trains_edt_5.setText(scanData.getPackNumber());
			addData(null);
		} else {
			VoiceHint.playErrorSounds();
			CommandTools.showToast("条码不存在");
		}
	}

	/**
	 * 保存数据
	 * @param v
	 */
	public void addData(View v){

		String trains_edt_1_value = trains_edt_1.getText().toString();
		String trains_edt_2_value = trains_edt_2.getText().toString();
		String trains_edt_3_value = trains_edt_3.getText().toString();
		String trains_edt_4_value = trains_edt_4.getText().toString();
		String trains_edt_5_value = trains_edt_5.getText().toString();

		String strTaskName = trains_edt_2_value + trains_edt_3_value;
		if (MyApplication.m_link_num == 3) {// 第三个环节直接取任务列表名字
			strTaskName = trains_edt_1_value;
		} else if (TextUtils.isEmpty(trains_edt_2_value)) {
			CommandTools.showToast(getResources().getString(R.string.carriage_no_is_required));
			return;
		}
		if (TextUtils.isEmpty(strTaskName)) {

			VoiceHint.playErrorSounds();
			CommandTools.showToast(getResources().getString(R.string.select_task));
			return;
		}

		if (TextUtils.isEmpty(trains_edt_4_value)) {

			VoiceHint.playErrorSounds();
			CommandTools.showToast(getResources().getString(R.string.code_not_null));
			return;
		}

		if (TextUtils.isEmpty(trains_edt_5_value)) {

			VoiceHint.playErrorSounds();
			CommandTools.showToast("请录入包装号码");
			return;
		}

		for (int i = 0; i < dataList.size(); i++) {

			ScanData data = dataList.get(i);

			if (data.getPackBarcode().equals(trains_edt_4_value) && data.getScaned().equals("1")) {
				VoiceHint.playErrorSounds();
				CommandTools.showToast("条码已扫描");
				return;
			}

			if (data.getPackNumber().equals(trains_edt_5_value)) {

				data.setTaskName(strTaskName);
				data.setTaskId(taskId);
				data.setPackBarcode(trains_edt_4_value);
				data.setWagonNumber(trains_edt_2_value);
				data.setTrain(trains_edt_3_value);
				data.setScanTime(CommandTools.getTime());
				data.setScanUser(MyApplication.m_userName);
				data.setLink(MyApplication.m_link_num + "");
				data.setScanType(Constant.SCAN_TYPE_RAILEAY);
				data.setNode_id(MyApplication.m_nodeId);
				data.setScaned("1");
				data.setUploadStatus("0");

				mScandataDao.addData(data);  //保存数据
				CommandTools.showToast("保存成功");
			}
		}

		commonAdapter.notifyDataSetChanged();

		edtCount1.setText(++scan_num + "");
		trains_edt_4.setText("");
		trains_edt_5.setText("");
	}

	/**
	 * 获取海运信息
	 */
	protected LoadTextNetTask requestGetShip(String userId, final String taskId, int flag) {

		PostTools.getLoadNumber(mContext, taskId, new ObjectCallback() {

			@Override
			public void callback(int res, String remark, Object object) {

				ScanNumInfo info = (ScanNumInfo) object;

				edtCount1.setText(scan_num + "");
				edtCount2.setText(info.getMust_scan_number() + "");
				edtCount3.setText(info.getReal_load_number() + "");
				edtCount4.setText(info.getMust_load_number() + "");
			}
		});

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
						if (success == 0) {
							JSONArray jsonArray = jsonObj.getJSONArray("data");
							dataList.clear();
							uploadList.clear();
							List<ScanData> list = new ArrayList<ScanData>();
							for (int i = 0; i < jsonArray.length(); i++) {
								JSONObject jsonObject = jsonArray.getJSONObject(i);
								String pack_number = jsonObject.optString("Pack_No");
								String pack_code = jsonObject.optString("Pack_BarCode");
								String goods_id = jsonObject.optString("ID");

								ScanData scanData = new ScanData();
								scanData.setCacheId(CommandTools.getUUID());
								scanData.setPackBarcode(pack_code);
								scanData.setPackNumber(pack_number);
								scanData.setMainGoodsId(goods_id);

								list.add(scanData);
							}
							List<ScanData> notUploadDataList = mScandataDao.getNotUploadDataList(MyApplication.m_scan_type, MyApplication.m_link_num + "", MyApplication.m_nodeId, taskId);
							dataList.addAll(notUploadDataList);

							//去除重复数据
							for (int j = 0; j < list.size(); j++) {
								for (int i = 0; i < dataList.size(); i++) {
									ScanData scanData = dataList.get(i);
									if (scanData.getPackNumber().equals(list.get(j).getPackNumber())) {
										list.remove(j);
										--j;
										break;
									}
								}
							}
							dataList.addAll(list);
							commonAdapter.notifyDataSetChanged();

							RFID.startRFID();
						} else {
							String message = jsonObj.getString("message");

							CommandTools.showToast(message);
						}

					} catch (JSONException e) {
						Toast.makeText(TrainsInstallActivity.this, "解析错误", 1).show();
						e.printStackTrace();
					}
				} else {
				}
			}
		};

		CustomProgress.showDialog(TrainsInstallActivity.this, getResources().getString(R.string.searching), false, null);
		MyApplication.getInstance();
		LoadTextNetTask task = UserService.getLand(userId, taskId, flag, listener, null);
		return task;
	}

	/**
	 * 完成
	 * @param v
	 */
	public void clickRight(View v){

		uploadList.clear();
		for (int i = 0; i < dataList.size(); i++) {
			if (!TextUtils.isEmpty(dataList.get(i).getScanTime())) {
				uploadList.add(dataList.get(i));
			}
		}

		if (uploadList.size() <= 0) {
			CommandTools.showToast(getResources().getString(R.string.scan_records));
		} else {
			requestUpload(uploadList, taskId);
		}
	}

	/**
	 * 上传数据
	 */
	protected LoadTextNetTask requestUpload(final List<ScanData> list, final String taskId) {

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
							//修改上传状态
							mScandataDao.updateUploadState(list);
							HandleDataTools.handleUploadData(commonAdapter, dataList, uploadList);
							HandleDataTools.handleLoadNumber(mContext, edtCount1, edtCount2, edtCount3, edtCount4, taskId, scan_num);
						}
					} catch (JSONException e) {
						Toast.makeText(TrainsInstallActivity.this, "解析错误", 1).show();
						e.printStackTrace();
					}
				} else {
				}
			}
		};

		CustomProgress.showDialog(TrainsInstallActivity.this, getResources().getString(R.string.searching), false, null);
		LoadTextNetTask task = UserService.upload(list, taskId, Constant.SCAN_TYPE_RAILEAY, listener, null);
		return task;
	}

	/* (non-Javadoc)
	 * @see com.xulan.client.activity.BaseActivity#sortByPackBarcode(android.view.View)
	 */
	public void sortByPackBarcode(View v){

		DataUtilTools.sortByPackBarCode(dataList, commonAdapter);
	}

	/* (non-Javadoc)
	 * @see com.xulan.client.activity.BaseActivity#sortByPackNo(android.view.View)
	 */
	public void sortByPackNo(View v){

		DataUtilTools.sortByPackNo(dataList, commonAdapter);
	}

	/* (non-Javadoc)
	 * @see android.app.Activity#onStop()
	 */
	public void onStop(){
		super.onStop();

		RFID.stopRFID();
	}

	public void onDestory(){
		super.onDestory();

		dataList.clear();
		uploadList.clear();
	}
}
