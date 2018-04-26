package com.xulan.demo.activity.stick;

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
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
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
 * 贴唛
 * 
 * @author hexiuhui
 *
 */
public class StickActivity extends BaseActivity implements OnClickListener {

	private EditText stick_edt_taskname;
	private EditText stick_edt_package_number;
	private EditText stick_edt_package_code;
	private EditText stick_scan_count;
	private EditText stick_pack_name;

	private RelativeLayout billCodeImg;

	private int scan_num = 0;
	private int scan_count_num = 0;

	private String taskId = "";
	private String company_id;
	private ListView mListView;
//	private CommonAdapter<ScanData> commonAdapter;
	private ListViewAdapter commonAdapter;
	private List<ScanData> dataList = new ArrayList<ScanData>();
	private List<ScanData> uploadList = new ArrayList<ScanData>();

	private ScanDataDao mScandataDao = new ScanDataDao();

	@Override
	protected void onBaseCreate(Bundle savedInstanceState) {
		setContentViewId(R.layout.activity_stick, this);
		ViewUtils.inject(this);

//		requestGetWXURL();
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
		stick_edt_taskname = (EditText) findViewById(R.id.stick_edt_taskname);
		stick_edt_package_number = (EditText) findViewById(R.id.stick_edt_package_number);
		stick_edt_package_code = (EditText) findViewById(R.id.stick_edt_package_code);
		stick_scan_count = (EditText) findViewById(R.id.stick_scan_count);
		stick_pack_name = (EditText) findViewById(R.id.stick_pack_name);
		
		//本地数据
		dataList = mScandataDao.getNotUploadDataList(MyApplication.m_scan_type, MyApplication.m_link_num + "", MyApplication.m_nodeId);
		
		scan_num = dataList.size();
		
		commonAdapter = new ListViewAdapter(mContext, dataList);
		
		mListView.setAdapter(commonAdapter);
		
//		mListView.setAdapter(commonAdapter = new CommonAdapter<ScanData>(mContext, dataList, R.layout.stick_item) {
//
//			@Override
//			public void convert(ViewHolder helper, ScanData item) {
//
//				helper.setText(R.id.stick_tv1, commonAdapter.getIndex());
//				helper.setText(R.id.stick_tv2, item.getPackBarcode());
//				helper.setText(R.id.stick_tv3, item.getPackNumber());
//				helper.setText(R.id.stick_tv4, item.getGoodsName());
//
//				helper.setText(R.id.stick_tv10, item.getScanUser());
//				helper.setText(R.id.stick_tv11, item.getScanTime());
//			}
//			
//		});
		
		mListView.setOnItemClickListener(new OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1, int arg2, long arg3) {
				// TODO Auto-generated method stub
				List<ScanData> dataList2 = commonAdapter.getDataList();
				stick_edt_package_code.setText(dataList2.get(arg2).getPackBarcode());
				stick_edt_package_number.setText(dataList2.get(arg2).getPackNumber());
				stick_pack_name.setText(dataList2.get(arg2).getGoodsName());
			}
		});
		
		stick_edt_package_number.addTextChangedListener(new TextWatcher(){  
            @Override  
            public void afterTextChanged(Editable arg0) {  
            }  
  
            @Override  
            public void beforeTextChanged(CharSequence arg0, int arg1, int arg2, int arg3) {  
            }  
  
            @Override  
            public void onTextChanged(CharSequence arg0, int arg1, int arg2, int arg3) {  
                // TODO Auto-generated method stub  
            	commonAdapter.getFilter().filter(arg0);  
//               也可以在这里筛选数据,但这不是异步的，有隐患，最好用系统提供的Filter类                
//               for (Iterator<String> iterator = mArrayList.iterator(); iterator  
//                          .hasNext();) {  
//                      String name = iterator.next();  
//  
//                      if (name.contains(arg0)) {  
//                          mFilteredArrayList.add(name);  
//                      }  
//               mListViewAdapter.changeList(mFilteredArrayList);  
//               mListViewAdapter.notifyDataSetChanged();  
            }  
              
        });  
		
		billCodeImg.setOnClickListener(this);
	}
	
	 public  void onDestroy(){  
         super.onDestroy();  
         ListViewAdapter.searchContent="";  
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
		if(scanInfo.getWhat() == Constant.SCAN_DATA && scanInfo.getType().equals(Constant.SCAN_TYPE_TIEMAI)){

			String strBillcode = scanInfo.getBarcode();
			stick_edt_package_code.setText(strBillcode);

			checkData(strBillcode);
		}
	}

	/**
	 * 任务名称选择
	 * @param v
	 */
	public void chooseTask(View v) {

		Intent intent = new Intent(mContext, TaskListActivity.class);
		intent.putExtra("type", Constant.SCAN_TYPE_TIEMAI);
		intent.putExtra("link_no", 1 + "");
		startActivityForResult(intent, Constant.SELECT_TASK);
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.bill_code_img:
			// 扫描
			Intent openCameraIntent = new Intent(StickActivity.this, CaptureActivity.class);
			startActivityForResult(openCameraIntent, Constant.CAPTURE_BILLCODE);
			break;

		default:
			break;
		}
	}

	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		if(requestCode == Constant.SELECT_TASK && resultCode == RESULT_OK) {
			scan_num = 0;
			stick_edt_taskname.setText(data.getStringExtra("taskName"));
			taskId = data.getStringExtra("taskCode");

			company_id = data.getStringExtra("car_count");

			stick_edt_package_code.setText("");
			stick_edt_package_number.setText("");

			requestGetShip(MyApplication.m_userID, taskId, MyApplication.m_flag);
		} else if (requestCode == Constant.SELECT_COMPANY && resultCode == RESULT_OK) {
			company_id = data.getStringExtra("company_id");
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
		
		ScanData scanData = DataUtilTools.checkScanData(Constant.SCAN_TYPE_TIEMAI, billcode, dataList);
		if (scanData != null) {

			stick_edt_package_code.setText(scanData.getPackBarcode());
			stick_edt_package_number.setText(scanData.getPackNumber());
//								addData(null);
		} else {
			stick_edt_package_code.setText(billcode);
		}
	}

	/**
	 * 保存数据
	 * @param v
	 */
	public void addData(View v) {
		String strGoodName = stick_pack_name.getText().toString();
		String strTaskName = stick_edt_taskname.getText().toString();
		if(MyApplication.m_flag == 0 && HandleDataTools.getFirstLinkNum() == MyApplication.m_link_num){
			strTaskName = MyApplication.m_userName;
		}

		//		String strTaskName = scale_edt_taskname.getText().toString();
		if (TextUtils.isEmpty(strTaskName)) {

			VoiceHint.playErrorSounds();
			CommandTools.showToast(getResources().getString(R.string.select_task));
			return;
		}

		String strPackageBarcode = stick_edt_package_code.getText().toString();

		String strPackageNumber = stick_edt_package_number.getText().toString();
		if (TextUtils.isEmpty(strPackageNumber)) {

			VoiceHint.playErrorSounds();
			CommandTools.showToast("请录入包装号码");
			return;
		}

		for (int i = 0; i < dataList.size(); i++) {

			ScanData mData = dataList.get(i);

			if (mData.getPackBarcode().equals(strPackageBarcode) && mData.getScaned().equals("1")) {
				VoiceHint.playErrorSounds();
				CommandTools.showToast("条码已扫描");
				return;
			}

			if (mData.getPackNumber().equals(strPackageNumber)) {
				mData.setGoodsName(strGoodName);
				mData.setTaskName(strTaskName);
				mData.setTaskId(taskId);
				mData.setPackBarcode(strPackageBarcode);
				mData.setCompany_id(company_id);

				mData.setScanTime(CommandTools.getTime());
				mData.setScanUser(MyApplication.m_userName);
				mData.setLink(MyApplication.m_link_num + "");
				mData.setScanType(Constant.SCAN_TYPE_TIEMAI);
				mData.setNode_id(MyApplication.m_nodeId);
				mData.setScaned("1");
				mData.setUploadStatus("0");

				mScandataDao.addData(mData);  //保存数据
				
				stick_scan_count.setText(++scan_num + " / " + scan_count_num);

				CommandTools.showToast("保存成功");
			}
		}

		commonAdapter.notifyDataSetChanged();

		stick_scan_count.setText(++scan_num + " / " + scan_count_num);
		stick_edt_package_code.setText("");
		stick_edt_package_number.setText("");
		stick_pack_name.setText("");
		
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
				Log.i("hexiuhui--", info.getMust_scan_number() + "");
				stick_scan_count.setText(scan_num + " / " + info.getMust_scan_number() + "");
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
								String goods_name = jsonObject.optString("goods_name");
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
								scanData.setGoodsName(goods_name);
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
							
							commonAdapter.changCopyList(dataList);

							RFID.startRFID();
						} else {
							String message = jsonObj.getString("message");

							CommandTools.showToast(message);
						}

					} catch (JSONException e) {
						Toast.makeText(StickActivity.this, "解析错误", 1).show();
						e.printStackTrace();
					}
				} else {
				}
			}
		};

		CustomProgress.showDialog(StickActivity.this, getResources().getString(R.string.searching), false, null);
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
							PostTools.getLoadNumber(mContext, taskId, new ObjectCallback() {

								@Override
								public void callback(int res, String remark, Object object) {

									ScanNumInfo info = (ScanNumInfo) object;

									stick_scan_count.setText(scan_num + " / " + info.getMust_scan_number());
								}
							});
							//							HandleDataTools.handleLoadNumber(mContext, edtCount1, edtCount2, edtCount3, edtCount4, taskId, scan_num);
						}
					} catch (JSONException e) {
						Toast.makeText(StickActivity.this, "解析错误", 1).show();
						e.printStackTrace();
					}
				} else {
				}
			}
		};

		CustomProgress.showDialog(StickActivity.this, getResources().getString(R.string.searching), false, null);
		MyApplication.m_link_num = 1;
		MyApplication.m_node_num = 1;
		LoadTextNetTask task = UserService.upload(list, taskId, Constant.SCAN_TYPE_TIEMAI, listener, null);
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
