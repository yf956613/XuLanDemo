package com.xulan.demo.activity.ruler;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.os.Message;
import android.text.TextUtils;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.EncodeHintType;
import com.google.zxing.WriterException;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.qrcode.QRCodeWriter;
import com.lidroid.xutils.ViewUtils;
import com.xulan.demo.MyApplication;
import com.xulan.demo.R;
import com.xulan.demo.activity.BaseActivity;
import com.xulan.demo.activity.action.TaskListActivity;
import com.xulan.demo.activity.action.load.LoadCompanyActivity;
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
import com.xulan.demo.view.CustomPopDialog;
import com.xulan.demo.view.CustomProgress;

/** 
 * 打尺
 * 
 * @author hexiuhui
 *
 */
public class RulerActivity extends BaseActivity implements OnClickListener {

	private EditText scale_edt_taskname;
	private EditText scale_company;
	private EditText scale_edt_package_number;
	private EditText scale_edt_package_code;
	private EditText scale_supervisor;
	private EditText scale_user;
	private EditText scale_long;
	private EditText scale_wide;
	private EditText scale_hight;
	private EditText scale_weight;
	private EditText scale_scan_count;
	private Button addPcode;

	private RelativeLayout billCodeImg;

	private int scan_num = 0;
	private int scan_count_num = 0;

	private String taskId = "";
	private String company_id;
	private ListView mListView;
	private CommonAdapter<ScanData> commonAdapter;
	private List<ScanData> dataList = new ArrayList<ScanData>();
	private List<ScanData> uploadList = new ArrayList<ScanData>();

	private ScanData scanData;
	private final int CONTRAST_NUMBER = 2222;

	private ScanDataDao mScandataDao = new ScanDataDao();

	@Override
	protected void onBaseCreate(Bundle savedInstanceState) {
		setContentViewId(R.layout.activity_ruler_scan, this);
		ViewUtils.inject(this);

		requestGetWXURL();
	}

	private Bitmap generateBitmap(String content,int width, int height) {  
		QRCodeWriter qrCodeWriter = new QRCodeWriter();  
		Map<EncodeHintType, String> hints = new HashMap();  
		hints.put(EncodeHintType.CHARACTER_SET, "utf-8");  
		try {  
			BitMatrix encode = qrCodeWriter.encode(content, BarcodeFormat.QR_CODE, width, height, hints);  
			int[] pixels = new int[width * height];  
			for (int i = 0; i < height; i++) {  
				for (int j = 0; j < width; j++) {  
					if (encode.get(j, i)) {  
						pixels[i * width + j] = 0x00000000;  
					} else {  
						pixels[i * width + j] = 0xffffffff;  
					}  
				}  
			}  
			return Bitmap.createBitmap(pixels, 0, width, width, height, Bitmap.Config.RGB_565);  
		} catch (WriterException e) {  
			e.printStackTrace();  
		}  
		return null;  
	}  

	@Override
	public void initView() {
		billCodeImg = (RelativeLayout) findViewById(R.id.bill_code_img);
		mListView = (ListView) findViewById(R.id.lv_public);
		scale_edt_taskname = (EditText) findViewById(R.id.scale_edt_taskname);
		scale_company = (EditText) findViewById(R.id.scale_company);
		scale_edt_package_number = (EditText) findViewById(R.id.scale_edt_package_number);
		scale_edt_package_code = (EditText) findViewById(R.id.scale_edt_package_code);
		scale_supervisor = (EditText) findViewById(R.id.scale_supervisor);
		scale_user = (EditText) findViewById(R.id.scale_user);
		scale_long = (EditText) findViewById(R.id.scale_long);
		scale_wide = (EditText) findViewById(R.id.scale_wide);
		scale_hight = (EditText) findViewById(R.id.scale_hight);
		scale_weight = (EditText) findViewById(R.id.scale_weight);
		scale_scan_count = (EditText) findViewById(R.id.scale_scan_count);
		addPcode = (Button) findViewById(R.id.addPcode);

		//本地数据
		dataList = mScandataDao.getNotUploadDataList(MyApplication.m_scan_type, MyApplication.m_link_num + "", MyApplication.m_nodeId);

		scan_num = dataList.size();

		mListView.setAdapter(commonAdapter = new CommonAdapter<ScanData>(mContext, dataList, R.layout.ruler_item) {

			@Override
			public void convert(ViewHolder helper, ScanData item) {

				helper.setText(R.id.ruler_tv1, commonAdapter.getIndex());
				helper.setText(R.id.ruler_tv2, item.getPackBarcode());
				helper.setText(R.id.ruler_tv3, item.getPackNumber());

				helper.setText(R.id.ruler_tv4, item.getLength());
				helper.setText(R.id.ruler_tv5, item.getWidth());
				helper.setText(R.id.ruler_tv6, item.getHeight());

				helper.setText(R.id.ruler_tv7, item.getLength_Old());
				helper.setText(R.id.ruler_tv8, item.getWidth_Old());
				helper.setText(R.id.ruler_tv9, item.getHeight_Old());
				helper.setText(R.id.ruler_tv10, item.getScanUser());
				helper.setText(R.id.ruler_tv11, item.getScanTime());
			}
		});

		mListView.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1, int arg2, long arg3) {
				// TODO Auto-generated method stub
				scale_edt_package_code.setText(dataList.get(arg2).getPackBarcode());
				scale_edt_package_number.setText(dataList.get(arg2).getPackNumber());
				scale_long.setText(dataList.get(arg2).getLength_Old());
				scale_wide.setText(dataList.get(arg2).getWidth_Old());
				scale_hight.setText(dataList.get(arg2).getHeight_Old());
				scale_weight.setText(dataList.get(arg2).getWeight());
			}
		});

		billCodeImg.setOnClickListener(this);

		addPcode.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				Bitmap bitmap = generateBitmap(wxUrl, 400, 400);
				CustomPopDialog.Builder dialogBuild = new CustomPopDialog.Builder(RulerActivity.this);
				dialogBuild.setImage(bitmap);
				CustomPopDialog dialog = dialogBuild.create();
				dialog.setCanceledOnTouchOutside(true);// 点击外部区域关闭
				dialog.show();
			}
		});
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
		if(scanInfo.getWhat() == Constant.SCAN_DATA && scanInfo.getType().equals(Constant.SCAN_TYPE_SCALE)){

			String strBillcode = scanInfo.getBarcode();
			scale_edt_package_code.setText(strBillcode);

			checkData(strBillcode);
		}
	}

	/**
	 * 任务名称选择
	 * @param v
	 */
	public void chooseTask(View v) {

		Intent intent = new Intent(mContext, TaskListActivity.class);
		intent.putExtra("type", Constant.SCAN_TYPE_SCALE);
		intent.putExtra("link_no", 1 + "");
		startActivityForResult(intent, Constant.SELECT_TASK);
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.bill_code_img:
			// 扫描
			Intent openCameraIntent = new Intent(RulerActivity.this, CaptureActivity.class);
			startActivityForResult(openCameraIntent, Constant.CAPTURE_BILLCODE);
			break;

		default:
			break;
		}
	}

	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		if(requestCode == Constant.SELECT_TASK && resultCode == RESULT_OK) {
			scan_num = 0;
			scale_edt_taskname.setText(data.getStringExtra("taskName"));
			taskId = data.getStringExtra("taskCode");

			company_id = data.getStringExtra("car_count");

			scale_company.setText(data.getStringExtra("car_plate"));

			scale_edt_package_code.setText("");
			scale_edt_package_number.setText("");

			requestGetShip(MyApplication.m_userID, taskId, MyApplication.m_flag);
		} else if (requestCode == Constant.SELECT_COMPANY && resultCode == RESULT_OK) {
			scale_company.setText(data.getStringExtra("companyName"));
			company_id = data.getStringExtra("company_id");
		} else if (requestCode == CONTRAST_NUMBER && resultCode == RESULT_OK) {
			//			commonAdapter.notifyDataSetChanged();
			//			scale_edt_package_code.setText("");
			//			scale_edt_package_number.setText("");
			saveData();
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

		ScanData scanData = DataUtilTools.checkScanData(Constant.SCAN_TYPE_SCALE, billcode, dataList);
		if (scanData != null) {

			scale_edt_package_code.setText(scanData.getPackBarcode());
			scale_edt_package_number.setText(scanData.getPackNumber());
			scale_long.setText(scanData.getLength_Old());
			scale_wide.setText(scanData.getWidth_Old());
			scale_hight.setText(scanData.getHeight_Old());
			scale_weight.setText(scanData.getWeight());
			//					addData(null);
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
		String strTaskName = scale_edt_taskname.getText().toString();
		if(MyApplication.m_flag == 0 && HandleDataTools.getFirstLinkNum() == MyApplication.m_link_num){
			strTaskName = MyApplication.m_userName;
		}

		//		String strTaskName = scale_edt_taskname.getText().toString();
		if (TextUtils.isEmpty(strTaskName)) {

			VoiceHint.playErrorSounds();
			CommandTools.showToast(getResources().getString(R.string.select_task));
			return;
		}

		String strCompany = scale_company.getText().toString();
		if (TextUtils.isEmpty(strCompany)) {

			VoiceHint.playErrorSounds();
			CommandTools.showToast(getResources().getString(R.string.vendor_is_required));
			return;
		}

		String strPackageBarcode = scale_edt_package_code.getText().toString();
		if (TextUtils.isEmpty(strPackageBarcode)) {

			VoiceHint.playErrorSounds();
			CommandTools.showToast(getResources().getString(R.string.code_not_null));
			return;
		}

		String strPackageNumber = scale_edt_package_number.getText().toString();
		if (TextUtils.isEmpty(strPackageNumber)) {

			VoiceHint.playErrorSounds();
			CommandTools.showToast("请录入包装号码");
			return;
		}

		String lenght = scale_long.getText().toString();
		String width = scale_wide.getText().toString();
		String height = scale_hight.getText().toString();
		String weight = scale_weight.getText().toString();
		if (TextUtils.isEmpty(lenght) || TextUtils.isEmpty(width) || TextUtils.isEmpty(height) || TextUtils.isEmpty(weight)) {

			VoiceHint.playErrorSounds();
			CommandTools.showToast("长/宽/高/重量不能为空");
			return;
		}

		scanData = new ScanData();
		for (int i = 0; i < dataList.size(); i++) {

			ScanData mData = dataList.get(i);

			scanData.setCacheId(mData.getCacheId());
			scanData.setMainGoodsId(mData.getMainGoodsId());
			scanData.setPackBarcode(mData.getPackBarcode());
			scanData.setPackNumber(mData.getPackNumber());
			scanData.setScaned(mData.getScaned());
			scanData.setLength_Old(mData.getLength_Old());
			scanData.setWidth_Old(mData.getWidth_Old());
			scanData.setHeight_Old(mData.getHeight_Old());

			if (scanData.getPackBarcode().equals(strPackageBarcode) && scanData.getScaned().equals("1")) {
				VoiceHint.playErrorSounds();
				CommandTools.showToast("条码已扫描");
				return;
			}

			if (scanData.getPackNumber().equals(strPackageNumber)) {

				scanData.setTaskName(strTaskName);
				scanData.setTaskId(taskId);
				scanData.setPackBarcode(strPackageBarcode);
				scanData.setCompany(strCompany);
				scanData.setCompany_id(company_id);
				scanData.setLength(scale_long.getText().toString());
				scanData.setWidth(scale_wide.getText().toString());
				scanData.setHeight(scale_hight.getText().toString());
				scanData.setWeight(scale_weight.getText().toString());

				//计算体积
				Double lenghti = Double.parseDouble(lenght);
				Double widthi = Double.parseDouble(width);
				Double heighti = Double.parseDouble(height);
				Double value = lenghti / 1000.0 * widthi / 1000.0 * heighti / 1000.0;
				scanData.setV3(String.format("%.2f", value) + "");

				//计算计费吨
				Double parseDouble = Double.parseDouble(weight) / 1000.0;
				if (parseDouble > value) {
					scanData.setCharge_Ton(String.format("%.2f", parseDouble) + "");
				} else {
					scanData.setCharge_Ton(String.format("%.2f", value) + "");
				}

				scanData.setScanTime(CommandTools.getTime());
				scanData.setScanUser(MyApplication.m_userName);
				scanData.setLink(MyApplication.m_link_num + "");
				scanData.setScanType(Constant.SCAN_TYPE_SCALE);
				scanData.setNode_id(MyApplication.m_nodeId);
				scanData.setScaned("1");
				scanData.setUploadStatus("0");

				mScandataDao.addData(scanData);  //保存数据

				scale_scan_count.setText(++scan_num + " / " + scan_count_num);

				Intent intent = new Intent(RulerActivity.this, ContrastActivity.class);
				intent.putExtra("barcode", strPackageBarcode);
				intent.putExtra("data", scanData);
				startActivityForResult(intent, CONTRAST_NUMBER);
				return;
			}
		}


	}

	/**
	 * 确认后保存数据
	 * 操作逻辑:删掉原来的，把新的加上
	 */
	private void saveData(){

		for(int i=0; i<dataList.size(); i++) {

			ScanData mData = dataList.get(i);
			if(mData.getMainGoodsId().equals(scanData.getMainGoodsId())){
				dataList.remove(i);
				dataList.add(scanData);
				commonAdapter.notifyDataSetChanged();
				break;
			}
		}

		scale_edt_package_code.setText("");
		scale_edt_package_number.setText("");
		//		scale_scan_count.setText(scan_num + " / " + scan_count_num + "");
	}

	/**
	 * 获取海运信息
	 */
	protected LoadTextNetTask requestGetShip(String userId, final String taskId, int flag) {

		PostTools.getLoadNumber(mContext, taskId, new ObjectCallback() {

			@Override
			public void callback(int res, String remark, Object object) {

				ScanNumInfo info = (ScanNumInfo) object;
				scan_count_num = info.getMust_scan_number();
				scale_scan_count.setText(scan_num + " / " + info.getMust_scan_number() + "");
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
								String weight = jsonObject.optString("weight");
								String height = jsonObject.optString("height");
								String v3 = jsonObject.optString("v3");
								String width = jsonObject.optString("width");
								String length = jsonObject.optString("length");
								String charge_ton = jsonObject.optString("charge_ton");

								ScanData scanData = new ScanData();
								scanData.setCacheId(CommandTools.getUUID());
								scanData.setPackBarcode(pack_code);
								scanData.setPackNumber(pack_number);
								scanData.setMainGoodsId(goods_id);
								scanData.setWeight(weight);
								scanData.setLength_Old(length);
								scanData.setWidth_Old(width);
								scanData.setHeight_Old(height);
								scanData.setV3(v3);
								scanData.setCharge_Ton(charge_ton + "");

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
						Toast.makeText(RulerActivity.this, "解析错误", 1).show();
						e.printStackTrace();
					}
				} else {
				}
			}
		};

		CustomProgress.showDialog(RulerActivity.this, getResources().getString(R.string.searching), false, null);
		MyApplication.getInstance();
		LoadTextNetTask task = UserService.getLand(userId, taskId, flag, listener, null);
		return task;
	}

	String wxUrl = "";

	/**
	 * 获取海运信息
	 */
	protected LoadTextNetTask requestGetWXURL() {
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
							JSONObject jo = jsonObj.getJSONObject("data");
							wxUrl = jo.getString("url");
						} else {
							String message = jsonObj.getString("message");

							CommandTools.showToast(message);
						}

					} catch (JSONException e) {
						Toast.makeText(RulerActivity.this, "解析错误", 1).show();
						e.printStackTrace();
					}
				} else {
				}
			}
		};

		CustomProgress.showDialog(RulerActivity.this, getResources().getString(R.string.searching), false, null);
		LoadTextNetTask task = UserService.getWXURL(listener, null);
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
							PostTools.getLoadNumber(mContext, taskId, new ObjectCallback() {

								@Override
								public void callback(int res, String remark, Object object) {

									ScanNumInfo info = (ScanNumInfo) object;

									scale_scan_count.setText(scan_num + " / " + info.getMust_scan_number());
								}
							});
							//							HandleDataTools.handleLoadNumber(mContext, edtCount1, edtCount2, edtCount3, edtCount4, taskId, scan_num);
						}
					} catch (JSONException e) {
						Toast.makeText(RulerActivity.this, "解析错误", 1).show();
						e.printStackTrace();
					}
				} else {
				}
			}
		};

		CustomProgress.showDialog(RulerActivity.this, getResources().getString(R.string.searching), false, null);
		MyApplication.m_link_num = 1;
		MyApplication.m_node_num = 1;
		LoadTextNetTask task = UserService.upload(list, taskId, Constant.SCAN_TYPE_SCALE, listener, null);
		return task;
	}

	/**
	 * 装卸公司选择
	 * @param v
	 */
	public void chooseCompany(View v){

		Intent intent = new Intent(mContext, LoadCompanyActivity.class);
		intent.putExtra("type", Constant.SCAN_TYPE_SCALE);
		intent.putExtra("link_no", MyApplication.m_link_num + "");
		startActivityForResult(intent, Constant.SELECT_COMPANY);
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
