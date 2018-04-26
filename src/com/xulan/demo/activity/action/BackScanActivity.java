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
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.RelativeLayout;

import com.lidroid.xutils.ViewUtils;
import com.lidroid.xutils.view.annotation.ViewInject;
import com.xulan.demo.MyApplication;
import com.xulan.demo.R;
import com.xulan.demo.activity.BaseActivity;
import com.xulan.demo.adapter.CommonAdapter;
import com.xulan.demo.adapter.ViewHolder;
import com.xulan.demo.camera.CaptureActivity;
import com.xulan.demo.data.LinkInfo;
import com.xulan.demo.data.ScanData;
import com.xulan.demo.data.ScanInfo;
import com.xulan.demo.db.dao.ScanDataDao;
import com.xulan.demo.util.CommandTools;
import com.xulan.demo.util.Constant;
import com.xulan.demo.util.DataUtilTools;
import com.xulan.demo.util.PostTools;
import com.xulan.demo.util.RequestUtil;
import com.xulan.demo.util.VoiceHint;
import com.xulan.demo.util.RequestUtil.RequestCallback;
import com.xulan.demo.view.CustomProgress;
import com.xulan.demo.view.SingleItemDialog;
import com.xulan.demo.view.SingleItemDialog.SingleItemCallBack;

/** 
 * 退运扫描
 * 
 * @author yxx
 *
 * @date 2016-12-16 上午10:38:30
 * 
 */
public class BackScanActivity extends BaseActivity implements OnClickListener {

	@ViewInject(R.id.lv_return_list) ListView mListView;
	@ViewInject(R.id.back_edt_1) EditText edtLinkName;//操作环节
	@ViewInject(R.id.back_edt_2) EditText edtPackageBillcode;//包装条码
	@ViewInject(R.id.back_edt_3) EditText edtPackageNumber;//包装号码
	@ViewInject(R.id.back_edt_4) EditText edtReason;//退运原因
	@ViewInject(R.id.back_edt_count) EditText edtScanCount;//扫描数

	private RelativeLayout billCodeImg;

	private CommonAdapter<ScanData> commonAdapter;
	private List<ScanData> dataList = new ArrayList<ScanData>();
	final List<LinkInfo> linkList = new ArrayList<LinkInfo>();

	private int link_num;

	private ScanDataDao mScanDataDao = new ScanDataDao();

	@Override
	protected void onBaseCreate(Bundle savedInstanceState) {
		setContentViewId(R.layout.activity_back_scan, this);
		ViewUtils.inject(this);

	}

	@Override
	public void initView() {
		billCodeImg = (RelativeLayout) findViewById(R.id.bill_code_img);

		dataList = mScanDataDao.getNotUploadDataList(Constant.SCAN_TYPE_LAND, MyApplication.m_link_num + "", MyApplication.m_nodeId);
		mListView.setAdapter(commonAdapter = new CommonAdapter<ScanData>(mContext, dataList, R.layout.return_list_item) {

			@Override
			public void convert(ViewHolder helper, ScanData item) {

				helper.setText(R.id.returtn_item_tv_0, commonAdapter.getIndex());
				helper.setText(R.id.returtn_item_tv_1, item.getPackBarcode());
				helper.setText(R.id.returtn_item_tv_2, item.getPackNumber());
				helper.setText(R.id.returtn_item_tv_3, item.getOperationLink());
				helper.setText(R.id.returtn_item_tv_4, item.getMemo());
				helper.setText(R.id.returtn_item_tv_5, item.getScanUser());
				helper.setText(R.id.returtn_item_tv_6, item.getScanTime());
			}
		});

		billCodeImg.setOnClickListener(this);
	}

	@Override
	public void initData() {
		// TODO Auto-generated method stub
		setTitle(getResources().getString(R.string.returncargo));
		setRightTitle(getResources().getString(R.string.submit));

		PostTools.getLink(mContext, linkList);
	}

	public void onEventMainThread(Message msg) {

		ScanInfo scanInfo = (ScanInfo) msg.obj;
		if(scanInfo.getWhat() == Constant.SCAN_DATA && scanInfo.getType().equals(Constant.SCAN_TYPE_BACK)){

			String strBillcode = scanInfo.getBarcode();
			edtPackageBillcode.setText(strBillcode);
			addData(null);
		}
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.bill_code_img:
			// 扫描
			Intent openCameraIntent = new Intent(BackScanActivity.this, CaptureActivity.class);
			startActivityForResult(openCameraIntent, Constant.CAPTURE_BILLCODE);
			break;

		default:
			break;
		}		
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		if (requestCode == Constant.CAPTURE_BILLCODE) {
			if (data == null) {
				return;
			}

			Bundle bundle = data.getExtras();
			String strBillcode = bundle.getString("result");

			edtPackageBillcode.setText(strBillcode);
			addData(null);
			return;
		}
	}

	public void chooseLink(View v){

		List<String> list = new ArrayList<String>();
		for(int i=0; i<linkList.size(); i++){

			list.add(linkList.get(i).getLinkName());
		}

		SingleItemDialog.showDialog(mContext, getResources().getString(R.string.mode_step), false, list, new SingleItemCallBack() {

			@Override
			public void callback(int pos) {

				link_num = linkList.get(pos).getLinkNum();
				edtLinkName.setText(linkList.get(pos).getLinkName());
			}
		});
	}

	/**
	 * 提交
	 * @param v
	 */
	public void clickRight(View v){

		if(dataList.size() == 0){
			CommandTools.showToast("数据不能为空");
			return;
		}

		JSONObject jsonObject = new JSONObject();
		try {
			jsonObject.put("operation_type", "upload_scandata");
			jsonObject.put("scan_type", MyApplication.m_scan_type);

			JSONObject header = new JSONObject();

			header.put("upload_time", CommandTools.getTime());
			header.put("link_no", link_num);
			header.put("node_no", MyApplication.m_node_num);
			header.put("route_points_id", MyApplication.m_nodeId);
			header.put("id", CommandTools.getUUID());
			header.put("scan_id", CommandTools.getUUID());
			header.put("scan_user", MyApplication.m_userName);
			header.put("scan_user_id", MyApplication.m_userID);
			header.put("scan_time", CommandTools.getTime());
			header.put("GPS_CoordX", "121.358297");
			header.put("GPS_CoordY", "31.226501");
			header.put("Device_Id", "8520198001");
			header.put("flag", MyApplication.m_flag);

			jsonObject.put("header", header);

			JSONArray jsonArray = new JSONArray();
			for (int i = 0; i < dataList.size(); i++) {

				ScanData data = dataList.get(i);
				JSONObject jo = new JSONObject();

				jo.put("scan_id", CommandTools.getUUID());
				jo.put("scan_detail_id", CommandTools.getUUID());

				jo.put("CacheId", data.getCacheId());
				jo.put("taskName", data.getTaskName());
				jo.put("taskId", data.getTaskId());
				jo.put("MainGoodsId", data.getMainGoodsId());
				jo.put("ScanType", data.getScanType());
				jo.put("ScanTime", data.getScanTime());
				jo.put("createTime", data.getCreateTime());
				jo.put("packBarcode", data.getPackBarcode());
				jo.put("packNumber", data.getPackNumber());
				jo.put("train", data.getTrain());
				jo.put("deiverPhone", data.getDeiverPhone());
				jo.put("Memo", data.getMemo());

				jo.put("AbnormalLink", data.getAbnormalLink());

				jsonArray.put(jo);
			}

			jsonObject.put("detail", jsonArray);

		} catch (JSONException e) {
			e.printStackTrace();
		}

		String strUrl = "upload/back";
		RequestUtil.postDataIfToken(mContext, strUrl, jsonObject, false, new RequestCallback() {

			@Override
			public void callback(int res, String remark, JSONObject jsonObject) {
				// TODO Auto-generated method stub
				CommandTools.showToast(remark);
				if(res == 0){
					boolean updateUploadState = mScanDataDao.updateUploadState(dataList);  //修改上传状态
					Log.i("hexiuhui---", updateUploadState + "修改");
					finish();
				}
			}
		});
	}

	/**
	 * 保存
	 * 调用接口，判断条码是否存在
	 * @param v
	 */
	public void addData(View v){

		if ("".equals(edtLinkName.getText().toString())) {
			CommandTools.showToast(getResources().getString(R.string.select_return_cargo_mode));
			return;
		}

		final String pack_barcode = edtPackageBillcode.getText().toString();
		if(TextUtils.isEmpty(pack_barcode)){
			CommandTools.showToast(getResources().getString(R.string.code_not_null));
			return;
		}

		JSONObject jsonObject = new JSONObject();
		try {
			jsonObject.put("project_id", MyApplication.m_projectId);
			jsonObject.put("node_id", MyApplication.m_nodeId);
			jsonObject.put("link_num", link_num);
			jsonObject.put("type", MyApplication.m_scan_type);
			jsonObject.put("pack_barcode", pack_barcode);
		} catch (JSONException e) {
			e.printStackTrace();
		}

		CustomProgress.showDialog(mContext, "数据获取中", true, null);
		String strUrl = "action/back/checkbarcode";
		RequestUtil.postDataIfToken(mContext, strUrl, jsonObject, false, new RequestCallback() {

			@Override
			public void callback(int res, String remark, JSONObject jsonObject){

				if(res != 0){
					CommandTools.showToast(remark);
					return;
				}

				String goods_id = jsonObject.optJSONObject("data").optString("id");
				String pack_no = jsonObject.optJSONObject("data").optString("pack_no");

				ScanData scanData = new ScanData();
				scanData.setCacheId(CommandTools.getUUID());
				scanData.setPackBarcode(pack_barcode);
				scanData.setPackNumber(pack_no);
				scanData.setOperationLink(edtLinkName.getText().toString());
				scanData.setScanType(MyApplication.m_scan_type);
				scanData.setMemo(edtReason.getText().toString());
				scanData.setLink(MyApplication.m_link_num + "");
				scanData.setScanUser(MyApplication.m_userName);
				scanData.setScanTime(CommandTools.getTime());
				scanData.setNode_id(MyApplication.m_nodeId);
				scanData.setMainGoodsId(goods_id);
				scanData.setScaned("1");
				scanData.setUploadStatus("0");

				dataList.add(scanData);
				commonAdapter.notifyDataSetChanged();

				mScanDataDao.addData(scanData);  //保存数据到本地

				edtScanCount.setText(dataList.size() + "");

				edtPackageBillcode.setText("");
				edtPackageNumber.setText("");
			}
		});
	}
}
