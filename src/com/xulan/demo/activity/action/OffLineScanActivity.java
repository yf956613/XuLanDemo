package com.xulan.demo.activity.action;

import java.util.ArrayList;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.content.Intent;
import android.os.Bundle;
import android.os.Message;
import android.text.TextUtils;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.lidroid.xutils.ViewUtils;
import com.lidroid.xutils.view.annotation.ViewInject;
import com.xulan.demo.MyApplication;
import com.xulan.demo.R;
import com.xulan.demo.activity.BaseActivity;
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
 * 下线
 * 
 * @author yxx
 *
 * @date 2017-6-8 上午10:11:22
 * 
 */
public class OffLineScanActivity extends BaseActivity implements OnClickListener {

	@ViewInject(R.id.lv_public) ListView mListView;
	@ViewInject(R.id.offline_edt_taskname) EditText edtTaskName;//任务名称

	@ViewInject(R.id.offline_edt_barcode) EditText edtPackBarcode;//包装条码
	@ViewInject(R.id.offline_edt_barno) EditText edtPackNo;//包装号码
	@ViewInject(R.id.offline_edt_goods_name) EditText edtGoodsName;//商品名称

//	@ViewInject(R.id.offline_edt_len) EditText edtLength;//长
//	@ViewInject(R.id.offline_edt_width) EditText edtWidth;//宽
//	@ViewInject(R.id.offline_edt_height) EditText edtHeight;//高
	@ViewInject(R.id.offline_edt_memo) EditText edtRemark;//下线信息
	@ViewInject(R.id.offline_edt_count) TextView tvCount;//操作数
	
	private RelativeLayout billCodeImg;

	private int scan_num = 0;
	private String taskId;


	private CommonAdapter<ScanData> commonAdapter;
	private List<ScanData> dataList = new ArrayList<ScanData>();
	private List<ScanData> uploadList = new ArrayList<ScanData>();
	
	private ScanDataDao mScandataDao = new ScanDataDao();

	@Override
	protected void onBaseCreate(Bundle savedInstanceState) {
		setContentViewId(R.layout.activity_off_line_scan, this);

		ViewUtils.inject(this);

		if(MyApplication.m_flag == 0 && HandleDataTools.getFirstLinkNum() == MyApplication.m_physic_link_num){
			requestGetData(MyApplication.m_userID, taskId, 0);
		}
	}

	@Override
	public void initView() {
		billCodeImg = (RelativeLayout) findViewById(R.id.bill_code_img);
		//本地数据
		dataList = mScandataDao.getNotUploadDataList(MyApplication.m_scan_type, MyApplication.m_link_num + "", MyApplication.m_nodeId);
		
		scan_num = dataList.size();
		
		mListView.setAdapter(commonAdapter = new CommonAdapter<ScanData>(mContext, dataList, R.layout.dobox_item) {
			@Override
			public void convert(ViewHolder helper, ScanData item) {

				helper.setText(R.id.land_tv1, commonAdapter.getIndex());
				helper.setText(R.id.land_tv2, item.getPackBarcode());
				helper.setText(R.id.land_tv3, item.getPackNumber());
				helper.setText(R.id.land_tv4, item.getGoodsName());
				helper.setText(R.id.land_tv5, item.getScanUser());
				helper.setText(R.id.land_tv6, item.getScanTime());
			}
		});

		mListView.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1, int arg2, long arg3) {

				ScanData scanData = dataList.get(arg2);

				edtPackBarcode.setText(scanData.getPackBarcode());
				edtPackNo.setText(scanData.getPackNumber());
				edtGoodsName.setText(scanData.getGoodsName());
			}
		});
		
		billCodeImg.setOnClickListener(this);
	}

	@Override
	public void initData() {

		setTitle(getResources().getString(R.string.produced));
		setRightTitle(getResources().getString(R.string.submit));
	}

	public void onEventMainThread(Message msg) {
		
		ScanInfo scanInfo = (ScanInfo) msg.obj;
		if(scanInfo.getWhat() == Constant.SCAN_DATA && scanInfo.getType().equals(Constant.SCAN_TYPE_OFFLINE)){

			String strBillcode = scanInfo.getBarcode();
			edtPackBarcode.setText(strBillcode);

			ScanData scanData = DataUtilTools.checkScanData(Constant.SCAN_TYPE_OFFLINE, strBillcode, dataList);
			if(scanData != null){

				edtPackBarcode.setText(scanData.getPackBarcode());
				edtPackNo.setText(scanData.getPackNumber());
				edtGoodsName.setText(scanData.getGoodsName());
				addData(null);
			}else{
				VoiceHint.playErrorSounds();
				CommandTools.showToast("条码不存在");
			}
		}
	}
	
	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.bill_code_img:
			// 扫描
			Intent openCameraIntent = new Intent(OffLineScanActivity.this, CaptureActivity.class);
			startActivityForResult(openCameraIntent, Constant.CAPTURE_BILLCODE);
			break;

		default:
			break;
		}
	}

	/**
	 * 任务名称选择
	 * @param v
	 */
	public void chooseTask(View v){
		Intent intent = new Intent(mContext, TaskListActivity.class);
		intent.putExtra("type", Constant.SCAN_TYPE_OFFLINE);
		intent.putExtra("link_no", MyApplication.m_link_num + "");
		startActivityForResult(intent, Constant.SELECT_TASK);
	}

	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		if(requestCode == Constant.SELECT_TASK && resultCode == RESULT_OK) {
			scan_num = 0;
			edtTaskName.setText(data.getStringExtra("taskName"));
			taskId = data.getStringExtra("taskCode");

			String car_plate = data.getStringExtra("car_plate");
			String car_count = data.getStringExtra("car_count");

			edtTaskName.setText(car_plate);

			requestGetData(MyApplication.m_userID, taskId, MyApplication.m_flag);
		} else if (requestCode == Constant.CAPTURE_BILLCODE) {
			if (data == null) {
				return;
			}
			
			Bundle bundle = data.getExtras();
			String strBillcode = bundle.getString("result");
			
			ScanData scanData = DataUtilTools.checkScanData(Constant.SCAN_TYPE_OFFLINE, strBillcode, dataList);
			if (scanData != null) {

				edtPackBarcode.setText(scanData.getPackBarcode());
				edtPackNo.setText(scanData.getPackNumber());
				edtGoodsName.setText(scanData.getGoodsName());
				addData(null);
			} else {
				VoiceHint.playErrorSounds();
				CommandTools.showToast("条码不存在");
			}
			return;
		}
	}
	
	private int scan_count_num = 0;

	/**
	 * 获取下线信息
	 */
	protected LoadTextNetTask requestGetData(String userId, final String taskId, int flag) {
		PostTools.getLoadNumber(mContext, taskId, new ObjectCallback() {

			@Override
			public void callback(int res, String remark, Object object) {

				ScanNumInfo info = (ScanNumInfo) object;

				scan_count_num = info.getMust_scan_number();
				tvCount.setText(scan_num + " / " + info.getMust_scan_number());
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
							List<ScanData> list = new ArrayList<ScanData>();
							for (int i = 0; i < jsonArray.length(); i++) {

								JSONObject jsonObject = jsonArray.getJSONObject(i);
								String pack_number = jsonObject.optString("Pack_No");
								String pack_code = jsonObject.optString("Pack_BarCode");
								String goods_id = jsonObject.optString("ID");
								String goods_name = jsonObject.optString("goods_name");

								ScanData scanData = new ScanData();
								scanData.setCacheId(CommandTools.getUUID());
								scanData.setPackBarcode(pack_code);
								scanData.setPackNumber(pack_number);
								scanData.setMainGoodsId(goods_id);
								scanData.setGoodsName(goods_name);

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
						Toast.makeText(OffLineScanActivity.this, "解析错误", 1).show();
						e.printStackTrace();
					}
				} else {

				}
			}
		};

		CustomProgress.showDialog(OffLineScanActivity.this, getResources().getString(R.string.searching), false, null);
		LoadTextNetTask task = UserService.getLand(userId, taskId, flag, listener, null);
		return task;
	}

	/**
	 * 保存数据
	 * @param v
	 */
	public void addData(View v){

		String strTaskName = "";

		if(MyApplication.m_flag == 0 && HandleDataTools.getFirstLinkNum() == MyApplication.m_link_num){
			strTaskName = MyApplication.m_userName;
		} else {
			strTaskName = edtTaskName.getText().toString();
			if(TextUtils.isEmpty(strTaskName)){
				CommandTools.showToast(getResources().getString(R.string.task_not_null));
				return;
			}
		}

		String strPackageBarcode = edtPackBarcode.getText().toString();
		if (TextUtils.isEmpty(strPackageBarcode)) {

			VoiceHint.playErrorSounds();
			CommandTools.showToast(getResources().getString(R.string.code_not_null));
			return;
		}

		String strPackageNumber = edtPackNo.getText().toString();
		if (TextUtils.isEmpty(strPackageNumber)) {

			VoiceHint.playErrorSounds();
			CommandTools.showToast("请录入包装号码");
			return;
		}

		for (int i = 0; i < dataList.size(); i++) {

			ScanData data = dataList.get(i);

			if (data.getPackBarcode().equals(strPackageBarcode) && data.getScaned().equals("1")) {
				VoiceHint.playErrorSounds();
				CommandTools.showToast("条码已扫描");
				return;
			}

			if (data.getPackNumber().equals(strPackageNumber)) {
				data.setTaskName(strTaskName);
				data.setTaskId(taskId);
				data.setPackBarcode(strPackageBarcode);
				data.setMemo(edtRemark.getText().toString());
				data.setScanTime(CommandTools.getTime());
				data.setScanUser(MyApplication.m_userName);
				data.setLink(MyApplication.m_link_num + "");
				data.setScanType(Constant.SCAN_TYPE_OFFLINE);
//				data.setLength(edtLength.getText().toString());
//				data.setWidth(edtWidth.getText().toString());
//				data.setHeight(edtHeight.getText().toString());
				data.setGoodsName(edtGoodsName.getText().toString());
				data.setNode_id(MyApplication.m_nodeId);
				data.setScaned("1");
				data.setUploadStatus("0");
				
				mScandataDao.addData(data);  //保存数据
				CommandTools.showToast("保存成功");
				break;
			}
		}

		commonAdapter.notifyDataSetChanged();

		tvCount.setText(++scan_num + " / " + scan_count_num);
		edtPackBarcode.setText("");
		edtPackNo.setText("");
		edtGoodsName.setText("");
	}

	/**
	 * 提交
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
							
							PostTools.getLoadNumber(mContext, taskId, new ObjectCallback() {

								@Override
								public void callback(int res, String remark, Object object) {

									ScanNumInfo info = (ScanNumInfo) object;

									scan_count_num = info.getMust_scan_number();
									tvCount.setText(scan_num + " / " + info.getMust_scan_number());
								}
							});
						}
					} catch (JSONException e) {
						Toast.makeText(OffLineScanActivity.this, "解析错误", 1).show();
						e.printStackTrace();
					}
				} else {

				}
			}
		};

		CustomProgress.showDialog(OffLineScanActivity.this, getResources().getString(R.string.searching), false, null);
		LoadTextNetTask task = UserService.upload(list, taskId, Constant.SCAN_TYPE_OFFLINE, listener, null);
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
}
