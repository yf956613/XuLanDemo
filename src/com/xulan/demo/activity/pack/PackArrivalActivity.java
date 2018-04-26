package com.xulan.demo.activity.pack;

import java.util.List;

import org.json.JSONException;
import org.json.JSONObject;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.xulan.demo.MyApplication;
import com.xulan.demo.R;
import com.xulan.demo.activity.BaseActivity;
import com.xulan.demo.activity.action.load.LoadCompanyActivity;
import com.xulan.demo.camera.CaptureActivity;
import com.xulan.demo.data.ScanData;
import com.xulan.demo.db.dao.ScanDataDao;
import com.xulan.demo.net.AsyncNetTask;
import com.xulan.demo.net.LoadTextNetTask;
import com.xulan.demo.net.LoadTextResult;
import com.xulan.demo.net.NetTaskResult;
import com.xulan.demo.net.AsyncNetTask.OnPostExecuteListener;
import com.xulan.demo.service.UserService;
import com.xulan.demo.util.CommandTools;
import com.xulan.demo.util.Constant;
import com.xulan.demo.util.DataUtilTools;
import com.xulan.demo.util.Logs;
import com.xulan.demo.util.VoiceHint;
import com.xulan.demo.view.CustomProgress;

/**
 * 
 * <pre>
 * Description	包装操作
 * Author:		hexiuhui
 * </pre>
 */
public class PackArrivalActivity extends BaseActivity implements OnClickListener{

	private List<ScanData> list;
	private EditText pack_edt_company;
	private EditText pack_edt_mode;
	private EditText pack_code;
	private EditText pack_number;
	private EditText pack_name;
	private EditText lenght;
	private EditText widght;
	private EditText height;
	private EditText weight;
	private EditText want;

	private String companyId;
	private String companyName;
	private String taskId;
	
	private RelativeLayout billCodeImg;

	private int res;//用来标记分箱单是否已配置总箱单0-已配置 1-未配置

	@Override
	protected void onBaseCreate(Bundle savedInstanceState) {
		setContentViewId(R.layout.activity_arrival_by_pack, this);
	}

	@Override
	public void initView() {
		taskId = getIntent().getStringExtra("taskId");
		list = (List<ScanData>)getIntent().getSerializableExtra("upload");

		pack_edt_company = (EditText) findViewById(R.id.pack_edt_company);
		pack_edt_mode = (EditText) findViewById(R.id.pack_edt_mode);
		pack_code = (EditText) findViewById(R.id.pack_code);
		pack_number = (EditText) findViewById(R.id.pack_number);
		pack_name = (EditText) findViewById(R.id.pack_name);
		lenght = (EditText) findViewById(R.id.lenght);
		widght = (EditText) findViewById(R.id.width);
		height = (EditText) findViewById(R.id.height);
		weight = (EditText) findViewById(R.id.weight);
		want = (EditText) findViewById(R.id.want);
		billCodeImg = (RelativeLayout) findViewById(R.id.bill_code_img);
		
		billCodeImg.setOnClickListener(this);
	}

	@Override
	public void initData() {
		setTitle(getResources().getString(R.string.packing_operation));
		getArriveData(list);
		
		pack_edt_company.setText(list.get(0).getCompany() + "");
		companyId = list.get(0).getCompany_id();
	}

	public LoadTextNetTask getArriveData(List<ScanData> list){
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
							JSONObject jsonObject = jsonObj.getJSONObject("data");
							String company_id = jsonObject.optString("company_id");
							String company_name = jsonObject.optString("company_name");
							String pack_mode = jsonObject.optString("pack_mode");
							String pack_barcode = jsonObject.optString("pack_barcode");
							String pack_no = jsonObject.optString("pack_no");
							String parts_name = jsonObject.optString("parts_name");
							String length1 = jsonObject.optString("length");
							String width1 = jsonObject.optString("width");
							String height1 = jsonObject.optString("height");
							String gross_weight1 = jsonObject.optString("gross_weight");
							String remark = jsonObject.optString("remark");

							companyId = company_id;

							pack_edt_company.setText(company_name);
							pack_edt_mode.setText(pack_mode);
							pack_code.setText(pack_barcode);
							pack_number.setText(pack_no);
							pack_name.setText(parts_name);
							lenght.setText(length1);
							widght.setText(width1);
							height.setText(height1);
							weight.setText(gross_weight1);
							want.setText(remark);

							setEditData(false);
						} else {
							String code = jsonObj.getString("code");
							if (code.equals("100")) {
								CommandTools.showToast("当前页面禁止操作");
								PackArrivalActivity.this.finish();
							}
							setEditData(true);
						}

						res = success;
					} catch (JSONException e) {
						Toast.makeText(PackArrivalActivity.this, "解析错误", 1).show();
						e.printStackTrace();
					}
				} else {
				}
			}
		};

		CustomProgress.showDialog(PackArrivalActivity.this, getResources().getString(R.string.searching), false, null);
		LoadTextNetTask task = UserService.getsumpackageinfo(list, listener, null);
		return task;
	}

	/**
	 * 选择公司
	 * @param v
	 */
	public void chooseCompany(View v){
		
		Intent intent = new Intent(mContext, LoadCompanyActivity.class);
		intent.putExtra("type", Constant.SCAN_TYPE_PACK);
		intent.putExtra("link_no", MyApplication.m_link_num + "");
		startActivityForResult(intent, Constant.SELECT_COMPANY);
	}

	protected void onActivityResult(int requestCode, int resultCode, Intent data) {

		if(requestCode == Constant.SELECT_COMPANY && resultCode == RESULT_OK){

			String strCompanyName = data.getStringExtra("companyName");
			companyId = data.getStringExtra("company_id");

			pack_edt_company.setText(strCompanyName);
		} else if (requestCode == Constant.CAPTURE_BILLCODE) {
			if (data == null) {
				return;
			}
			
			Bundle bundle = data.getExtras();
			String strBillcode = bundle.getString("result");
			
			pack_code.setText(strBillcode);
			
			
			getSumpackageinfobybarcode(strBillcode);
			
//			ScanData scanData = DataUtilTools.checkScanData(strBillcode, dataList);
//			if (scanData != null) {
//				pack_goods_code.setText(scanData.getMinutePackBarcode());
//				pack_goods_number.setText(scanData.getMinutePackNumber());
//				pack_goods_name.setText(scanData.getGoodsName());
//				pack_informmation.setText(scanData.getMemo());
//				addData(null);
//			} else {
//				VoiceHint.playErrorSounds();
//				CommandTools.showToast("条码不存在");
//			}
			
			return;
		}
	}
	
	public LoadTextNetTask getSumpackageinfobybarcode(String barcode){
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
							JSONObject jsonObject = jsonObj.getJSONObject("data");
							String company_id = jsonObject.optString("company_id");
							String company_name = jsonObject.optString("company_name");
							String pack_type = jsonObject.optString("pack_type");
							String pack_barcode = jsonObject.optString("pack_barcode");
							String pack_no = jsonObject.optString("pack_no");
							String goods_name = jsonObject.optString("goods_name");
							String length1 = jsonObject.optString("length");
							String width1 = jsonObject.optString("width");
							String height1 = jsonObject.optString("height");
							String weight1 = jsonObject.optString("weight");
							String remark = jsonObject.optString("remark");

							companyId = company_id;

							pack_edt_company.setText(company_name);
							pack_edt_mode.setText(pack_type);
							pack_code.setText(pack_barcode);
							pack_number.setText(pack_no);
							pack_name.setText(goods_name);
							lenght.setText(length1);
							widght.setText(width1);
							height.setText(height1);
							weight.setText(weight1);
							want.setText(remark);

							setEditData(false);
						} else {
							String code = jsonObj.getString("code");
							if (code.equals("100")) {
								CommandTools.showToast("当前页面禁止操作");
								PackArrivalActivity.this.finish();
							}
							setEditData(true);
						}

						res = success;
					} catch (JSONException e) {
						Toast.makeText(PackArrivalActivity.this, "解析错误", 1).show();
						e.printStackTrace();
					}
				} else {
				}
			}
		};

		CustomProgress.showDialog(PackArrivalActivity.this, getResources().getString(R.string.searching), false, null);
		LoadTextNetTask task = UserService.getSumpackageinfobybarcode(barcode, listener, null);
		return task;
	}

	/**
	 * 上传数据
	 */
	protected LoadTextNetTask requestUpload(final List<ScanData> list, String taskId) {

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
							ScanDataDao mscanDao = new ScanDataDao();
							mscanDao.updateUploadState(list);
							setResult(RESULT_OK);
							finish();
						}
					} catch (JSONException e) {
						Toast.makeText(PackArrivalActivity.this, "解析错误", 1).show();
						e.printStackTrace();
					}
				} else {
				}
			}
		};

		CustomProgress.showDialog(PackArrivalActivity.this, getResources().getString(R.string.searching), false, null);
		LoadTextNetTask task = UserService.upload(list, taskId, Constant.SCAN_TYPE_PACK, listener, null);
		return task;
	}

	/**
	 * 提交
	 * @param v
	 */
	public void commit(View v){

		String strCompanyName = pack_edt_company.getText().toString();
		if(TextUtils.isEmpty(companyId)){
			CommandTools.showToast("包装公司不能为空");
			return;
		}

		String strPackMode = pack_edt_mode.getText().toString();
		String strPackCode = pack_code.getText().toString();
		String strPackNumber = pack_number.getText().toString();
		String strPackName = pack_name.getText().toString();

		if (TextUtils.isEmpty(strPackCode)) {
			CommandTools.showToast(getResources().getString(R.string.barcode_is_required));
			return;
		}

		if (TextUtils.isEmpty(strPackNumber)) {
			CommandTools.showToast("包装号码不能为空");
			return;
		}

		if (TextUtils.isEmpty(strPackName)) {
			CommandTools.showToast("包装品名不能为空");
			return;
		}

		String strLength = lenght.getText().toString();
		String strWidth = widght.getText().toString();
		String strHeight = height.getText().toString();
		String strWeight = weight.getText().toString();
		if(TextUtils.isEmpty(strLength) || TextUtils.isEmpty(strWidth) || TextUtils.isEmpty(strHeight) || TextUtils.isEmpty(strWeight)){
			CommandTools.showToast("长、宽、高、毛重均不能为空");
			return;
		}

		for (int i = 0; i < list.size(); i++) {
			ScanData scanData = list.get(i);
			scanData.setScanTime(CommandTools.getTime());
			scanData.setCreateTime(CommandTools.getTime());
			scanData.setScaned(res + "");//这个字段用来标记分箱单是否已分配总箱单，会影响后台数据的存储，别改
			scanData.setCompany(strCompanyName);
			scanData.setCompany_id(companyId);
			scanData.setPackMode(strPackMode);
			scanData.setPackName(strPackName);
			scanData.setLength(strLength);
			scanData.setWidth(strWidth);
			scanData.setHeight(strHeight);
			scanData.setWeight(strWeight);
			scanData.setMemo(want.getText().toString());
			scanData.setPackBarcode(strPackCode);
			scanData.setPackNumber(strPackNumber);
			scanData.setScaned("1");
			scanData.setUploadStatus("0");
			scanData.setTaskName(strCompanyName);
		}

		requestUpload(list, taskId);
	}

	/**
	 * 设置控件是否可编辑
	 * @param flag
	 */
	public void setEditData(boolean flag){

		if(flag){

			pack_edt_mode.setEnabled(true);
			pack_code.setEnabled(true);
			pack_number.setEnabled(true);
			pack_name.setEnabled(true);
			lenght.setEnabled(true);
			widght.setEnabled(true);
			height.setEnabled(true);
			weight.setEnabled(true);
			want.setEnabled(true);
		}else{

			pack_edt_mode.setEnabled(false);
			pack_code.setEnabled(false);
			pack_number.setEnabled(false);
			pack_name.setEnabled(false);
			lenght.setEnabled(false);
			widght.setEnabled(false);
			height.setEnabled(false);
			weight.setEnabled(false);
			want.setEnabled(false);
		}

	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.bill_code_img:
			// 扫描
			Intent openCameraIntent = new Intent(PackArrivalActivity.this, CaptureActivity.class);
			startActivityForResult(openCameraIntent, Constant.CAPTURE_BILLCODE);
			break;

		default:
			break;
		}
		
	}
}
