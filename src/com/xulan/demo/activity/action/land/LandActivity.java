package com.xulan.demo.activity.action.land;

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
import com.lidroid.xutils.view.annotation.ViewInject;
import com.xulan.demo.MyApplication;
import com.xulan.demo.R;
import com.xulan.demo.activity.BaseActivity;
import com.xulan.demo.activity.action.TaskListActivity;
import com.xulan.demo.adapter.CommonAdapter;
import com.xulan.demo.adapter.ViewHolder;
import com.xulan.demo.camera.CaptureActivity;
import com.xulan.demo.data.ScanData;
import com.xulan.demo.data.ScanInfo;
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
import com.xulan.demo.util.ToolUtils;
import com.xulan.demo.util.VoiceHint;
import com.xulan.demo.view.CustomProgress;

/** 
 * 陆运--装货卸货
 * 
 * @author yxx
 *
 * @date 2016-12-20 下午1:20:23
 * 
 */
public class LandActivity extends BaseActivity implements OnClickListener {

	@ViewInject(R.id.lv_public) ListView mListView;
	@ViewInject(R.id.land_edt_taskname) EditText edtTaskName;//任务名称
	@ViewInject(R.id.land_edt_package_billcode) EditText edtPackageBarcode;//包装条码
	@ViewInject(R.id.land_edt_package_number) EditText edtPackageNumber;//包装号码
	@ViewInject(R.id.land_edt_car_number) EditText edtCarNumber;//车牌号
	@ViewInject(R.id.land_edt_car_count) EditText edtCarCount;//车次
	@ViewInject(R.id.land_edt_driver_phone) EditText edtPhone;//司机手机号
	@ViewInject(R.id.land_edt_remark) EditText edtRemark;//备注

	@ViewInject(R.id.scan_count_1) EditText edtCount1;//当前扫描数
	@ViewInject(R.id.scan_count_2) EditText edtCount2;//应扫描数
	@ViewInject(R.id.scan_count_3) EditText edtCount3;//实际载入数
	@ViewInject(R.id.scan_count_4) EditText edtCount4;//应载入数

	private RelativeLayout billCodeImg;

	private CommonAdapter<ScanData> commonAdapter;
	private List<ScanData> dataList = new ArrayList<ScanData>();//列表里的数据
	private List<ScanData> uploadList = new ArrayList<ScanData>();//上传数据

	private int scan_num = 0;
	private String taskId = "";
	private ScanDataDao mScandataDao = new ScanDataDao();

	private String link_man = "";
	private String link_phone = "";

	@Override
	protected void onBaseCreate(Bundle savedInstanceState) {
		setContentViewId(R.layout.activity_land, this);
		ViewUtils.inject(this);
		Log.v("zd", "land---onBaseCreate");

		if(MyApplication.m_flag == 0 && HandleDataTools.getFirstLinkNum() == MyApplication.m_physic_link_num){
			requestGetLand(MyApplication.m_userID, taskId, 0);
		}
	}

	@Override
	public void initView() {

		billCodeImg = (RelativeLayout) findViewById(R.id.bill_code_img);
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

				edtPackageBarcode.setText(scanData.getPackBarcode());
				edtPackageNumber.setText(scanData.getPackNumber());

				edtPhone.setText(scanData.getDeiverPhone());
				edtRemark.setText(scanData.getMemo());
			}
		});

		billCodeImg.setOnClickListener(this);
	}

	@Override
	public void initData() {
		setTitle(getIntent().getStringExtra("actionName"));
		setRightTitle(getResources().getString(R.string.submit));
	}

	public void onResume(){
		super.onResume();

	}

	public void onEventMainThread(Message msg) {

		ScanInfo scanInfo = (ScanInfo) msg.obj;
		if(scanInfo.getWhat() == Constant.SCAN_DATA && scanInfo.getType().equals(Constant.SCAN_TYPE_LAND)){

			String strBillcode = scanInfo.getBarcode();
			edtPackageBarcode.setText(strBillcode);

			checkData(strBillcode);
		}
	}

	/**
	 * 获取陆运信息
	 */
	protected LoadTextNetTask requestGetLand(String userId, final String taskId, int flag) {
		HandleDataTools.handleLoadNumber(mContext, edtCount1, edtCount2, edtCount3, edtCount4, taskId, scan_num);

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
								String pack_number = jsonObject.getString("Pack_No");
								String pack_code = jsonObject.getString("Pack_BarCode");
								String goods_id = jsonObject.optString("ID");

								ScanData scanData = new ScanData();
								scanData.setCacheId(CommandTools.getUUID());
								scanData.setPackBarcode(pack_code);
								scanData.setPackNumber(pack_number);
								scanData.setMainGoodsId(goods_id);

								scanData.setDeiverPhone(jsonObject.optString("phone", ""));
								scanData.setMemo(jsonObject.optString("memo", ""));

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
						Toast.makeText(LandActivity.this, "解析错误", 1).show();
						e.printStackTrace();
					}
				} else {
				}
			}
		};

		CustomProgress.showDialog(LandActivity.this, getResources().getString(R.string.searching), false, null);
		LoadTextNetTask task = UserService.getLand(userId, taskId, flag, listener, null);
		return task;
	}

	/**
	 * 保存数据
	 * @param v
	 */
	public void addData(View v){

		Log.v("test", "addData");
		String strCarNumber = edtCarNumber.getText().toString();
		String strCarCount = edtCarCount.getText().toString();

		String strTaskName = strCarNumber + "-" + strCarCount;
		if(MyApplication.m_link_num == 3){//第三个环节直接取任务列表名字
			strTaskName = edtTaskName.getText().toString();
		}else if(TextUtils.isEmpty(strCarNumber) || TextUtils.isEmpty(strCarCount)){
			VoiceHint.playErrorSounds();
			CommandTools.showToast(getResources().getString(R.string.tractor_LIC_shift_is_required));
			return;
		}

		String strPackageBarcode = edtPackageBarcode.getText().toString();
		if (TextUtils.isEmpty(strPackageBarcode)) {

			VoiceHint.playErrorSounds();
			CommandTools.showToast(getResources().getString(R.string.code_not_null));
			return;
		}

		String strPackageNumber = edtPackageNumber.getText().toString();
		if (TextUtils.isEmpty(strPackageNumber)) {

			VoiceHint.playErrorSounds();
			CommandTools.showToast("请录入包装号码");
			return;
		}

		for (int i = 0; i < dataList.size(); i++) {
			ScanData data = dataList.get(i);

			if (data.getPackBarcode().equals(strPackageBarcode) && data.getScaned().equals("1")) {
				VoiceHint.playErrorSounds();
				//				CommandTools.showToast("条码已扫描");
				Toast.makeText(this, "条码已扫描", Toast.LENGTH_LONG).show();
				break;
			}else if (data.getPackBarcode().equals(strPackageBarcode)) {

				data.setTaskName(strTaskName);

				data.setTaskId(taskId);
				data.setPackBarcode(strPackageBarcode);
				data.setVehicleNumbers(strCarNumber);
				data.setTrain(strCarCount);
				data.setScanTime(CommandTools.getTime());
				data.setScanUser(MyApplication.m_userName);
				data.setScanType(Constant.SCAN_TYPE_LAND);
				data.setDeiverPhone(edtPhone.getText().toString());
				data.setMemo(edtRemark.getText().toString());
				data.setCreateTime(CommandTools.getTime());
				data.setLink(MyApplication.m_link_num + "");
				data.setNode_id(MyApplication.m_nodeId);
				data.setScaned("1");
				data.setUploadStatus("0");

				data.setLinkMan(link_man);
				data.setLinkPhone(link_phone);

				mScandataDao.addData(data);//保存数据
				//				CommandTools.showToast("保存成功");
				Toast.makeText(this, "保存成功", Toast.LENGTH_LONG).show();

				commonAdapter.notifyDataSetChanged();

				edtCount1.setText(++scan_num + "");
				edtPackageBarcode.setText("");
				edtPackageNumber.setText("");
				break;
			}
		}

	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.bill_code_img:
			// 扫描
			Intent openCameraIntent = new Intent(LandActivity.this, CaptureActivity.class);
			startActivityForResult(openCameraIntent, Constant.CAPTURE_BILLCODE);
			break;

		default:
			break;
		}
	}

	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		if(requestCode == Constant.SELECT_TASK && resultCode == RESULT_OK) {

			scan_num = 0;
			edtTaskName.setText(data.getStringExtra("taskName"));
			int flag = data.getIntExtra("flag", 0);
			taskId = data.getStringExtra("taskCode");

			String car_plate = data.getStringExtra("car_plate");
			String car_count = data.getStringExtra("car_count");

			link_man = data.getStringExtra("link_man");
			link_phone = data.getStringExtra("link_phone");

			edtCarNumber.setText(car_plate + "");
			edtCarCount.setText(car_count + "");

			edtPackageBarcode.setText("");
			edtPackageNumber.setText("");
			edtPhone.setText("");
			edtRemark.setText("");

			requestGetLand(MyApplication.m_userID, taskId, flag);
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

		Log.v("test", "checkData" + billcode);
		ScanData scanData = DataUtilTools.checkScanData(Constant.SCAN_TYPE_LAND, billcode, dataList);
		if (scanData != null) {

			edtPackageBarcode.setText(scanData.getPackBarcode());
			edtPackageNumber.setText(scanData.getPackNumber());

			if(!TextUtils.isEmpty(scanData.getDeiverPhone())){
				edtPhone.setText(scanData.getDeiverPhone());
			}

			if(!TextUtils.isEmpty(scanData.getMemo())){
				edtRemark.setText(scanData.getMemo());
			}

			addData(null);
		} else {
			VoiceHint.playErrorSounds();
			CommandTools.showToast("条码不存在");
		}

	}

	/**
	 * 任务名称选择
	 * @param v
	 */
	public void chooseTask(View v){

		Intent intent = new Intent(mContext, TaskListActivity.class);
		intent.putExtra("type", Constant.SCAN_TYPE_LAND);
		intent.putExtra("link_no", MyApplication.m_link_num);
		startActivityForResult(intent, Constant.SELECT_TASK);
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
						Toast.makeText(LandActivity.this, "解析错误", 1).show();
						e.printStackTrace();
					}
				} else {
				}
			}
		};

		CustomProgress.showDialog(LandActivity.this, getResources().getString(R.string.searching), false, null);
		LoadTextNetTask task = UserService.upload(list, taskId, Constant.SCAN_TYPE_LAND, listener, null);
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

	/* (non-Javadoc)
	 * @see com.xulan.client.activity.BaseActivity#onDestory()
	 */
	public void onDestory(){
		super.onDestory();
		Log.v("zd", "land---onDestory");

		dataList.clear();
		uploadList.clear();
	}

}
