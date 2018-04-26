package com.xulan.demo.activity.action;

import java.util.ArrayList;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

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
import com.xulan.demo.data.TaskInfo;
import com.xulan.demo.util.CommandTools;
import com.xulan.demo.util.Constant;
import com.xulan.demo.util.DataUtilTools;
import com.xulan.demo.util.PostTools;
import com.xulan.demo.util.RequestUtil;
import com.xulan.demo.util.Res;
import com.xulan.demo.util.VoiceHint;
import com.xulan.demo.util.RequestUtil.RequestCallback;
import com.xulan.demo.view.SingleItemDialog;
import com.xulan.demo.view.SingleItemDialog.SingleItemCallBack;

/** 
 * 查询
 * 
 * @author yxx
 *
 * @date 2016-12-20 下午3:07:35
 * 
 */
public class InqueryActivity extends BaseActivity implements OnClickListener {
	@ViewInject(R.id.lv_public) ListView mListView;

	@ViewInject(R.id.inquery_edt_link) EditText edtLink;
	@ViewInject(R.id.inquery_edt_task_name) EditText edtTaskName;
	@ViewInject(R.id.inquery_edt_barcode) EditText edtBarcode;
	@ViewInject(R.id.inquery_edt_barno) EditText edtBarno;
	@ViewInject(R.id.inquery_edt_meno) EditText edtMemo;
	@ViewInject(R.id.inquery_edt_solution) EditText edtSolution;

	@ViewInject(R.id.inquery_tv_barcode) TextView tvBarcode;
	@ViewInject(R.id.inquery_tv_barno) TextView tvBarno;

	@ViewInject(R.id.inquery_item_tv2) TextView tvItemBarcode;
	@ViewInject(R.id.inquery_item_tv3) TextView tvItemBarno;
	
	@ViewInject(R.id.inquery_item_tv7) TextView tvItemGoodBarcode;
	@ViewInject(R.id.inquery_item_tv8) TextView tvItemGoodBarno;
	
	@ViewInject(R.id.item7_view) View itemView7;
	@ViewInject(R.id.item8_view) View itemView8;

	@ViewInject(R.id.inquery_edt_count_exception) EditText edtCount1;
	@ViewInject(R.id.inquery_edt_count_back) EditText edtCount2;
	@ViewInject(R.id.inquery_edt_count_load) EditText edtCount3;

	private RelativeLayout billCodeImg;

	private CommonAdapter<ScanData> commonAdapter;
	private List<ScanData> dataList = new ArrayList<ScanData>();
	private final List<LinkInfo> linkList = new ArrayList<LinkInfo>();
	private final List<TaskInfo> taskNameList = new ArrayList<TaskInfo>();

	private String task_id;
	private int link_num;

	@Override
	protected void onBaseCreate(Bundle savedInstanceState) {
		setContentViewId(R.layout.activity_inquery, this);
		ViewUtils.inject(this);
	}

	@Override
	public void initView() {
		billCodeImg = (RelativeLayout) findViewById(R.id.bill_code_img);
		mListView.setAdapter(commonAdapter = new CommonAdapter<ScanData>(mContext, dataList, R.layout.inquery_item) {

			@Override
			public void convert(ViewHolder helper, ScanData item) {
				
				helper.setText(R.id.inquery_item_tv1, commonAdapter.getIndex());
				
				helper.setText(R.id.inquery_item_tv2, item.getPackBarcode());
				helper.setText(R.id.inquery_item_tv3, item.getPackNumber());
				
				helper.setText(R.id.inquery_item_tv4, item.getScaned());
				helper.setText(R.id.inquery_item_tv5, item.getScanUser());
				helper.setText(R.id.inquery_item_tv6, item.getScanTime());
				
				if (MyApplication.m_scan_type.equals(Constant.SCAN_TYPE_PACK)) {
					
					helper.getView(R.id.inquery_item_tv7).setVisibility(View.VISIBLE);
					helper.getView(R.id.inquery_item_tv8).setVisibility(View.VISIBLE);
					helper.getView(R.id.item7_view).setVisibility(View.VISIBLE);
					helper.getView(R.id.item8_view).setVisibility(View.VISIBLE);
					
					helper.setText(R.id.inquery_item_tv7, item.getGoodsNo());
					helper.setText(R.id.inquery_item_tv8, item.getGoodsCode());
					
				}
			}
		});

		mListView.setOnItemClickListener(new OnItemClickListener() {
			
			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1, int arg2, long arg3) {
				
				ScanData scanData = dataList.get(arg2);

				edtBarcode.setText(scanData.getPackBarcode());
				edtBarno.setText(scanData.getPackNumber());

				if (scanData.getScaned().equals("退运")) {
					edtMemo.setText(scanData.getAbnormalCause());//退运原因
				} else if (scanData.getScaned().equals("异常")){
					edtMemo.setText(scanData.getAbnormalCause());//异常描述
					edtSolution.setText(scanData.getMemo());//解决方案
				} else {
					edtMemo.setText("");
					edtSolution.setText("");
				}
			}
		});

		billCodeImg.setOnClickListener(this);
	}

	@Override
	public void initData() {

		setTitle(getResources().getString(R.string.search));

		//包装显示的是商品条码、商品号码
		if(MyApplication.m_scan_type.equals(Constant.SCAN_TYPE_OFFLINE) || MyApplication.m_scan_type.equals(Constant.SCAN_TYPE_INSTALL)) {
			tvBarcode.setText(Res.getString(R.string.goods_barcode));
			tvBarno.setText(Res.getString(R.string.goods_no));

			tvItemBarcode.setText(Res.getString(R.string.goods_barcode));
			tvItemBarno.setText(Res.getString(R.string.goods_no));
		} else if (MyApplication.m_scan_type.equals(Constant.SCAN_TYPE_PACK)) {
			
			tvItemBarcode.setText(Res.getString(R.string.goods_barcode));
			tvItemBarno.setText(Res.getString(R.string.goods_no));

			tvItemGoodBarcode.setText(Res.getString(R.string.barcode));
			tvItemGoodBarno.setText(Res.getString(R.string.package_no));
			
			tvItemGoodBarcode.setVisibility(View.VISIBLE);
			tvItemGoodBarno.setVisibility(View.VISIBLE);
			itemView7.setVisibility(View.VISIBLE);
			itemView8.setVisibility(View.VISIBLE);
		}

		dataList.clear();
		linkList.clear();

		PostTools.getLink(mContext, linkList);
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.bill_code_img:
			// 扫描
			Intent openCameraIntent = new Intent(InqueryActivity.this, CaptureActivity.class);
			startActivityForResult(openCameraIntent, Constant.CAPTURE_BILLCODE);
			break;

		default:
			break;
		}
	}

	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		if (requestCode == Constant.CAPTURE_BILLCODE) {
			if (data == null) {
				return;
			}

			Bundle bundle = data.getExtras();
			String strBillcode = bundle.getString("result");

			ScanData scanData = DataUtilTools.checkScanData("查询", strBillcode, dataList);
			if (scanData != null) {

				edtBarcode.setText(scanData.getPackBarcode());
				edtBarno.setText(scanData.getPackNumber());
			} else {
				VoiceHint.playErrorSounds();
				CommandTools.showToast("条码不存在");
			}
			return;
		}
	}

	/**
	 * @param v
	 */
	public void chooseLink(View v){

		List<String> list = new ArrayList<String>();
		for(int i=0; i<linkList.size(); i++){

			list.add(linkList.get(i).getLinkName());
		}

		SingleItemDialog.showDialog(mContext, getResources().getString(R.string.mode_step), false, list, new SingleItemCallBack() {

			@Override
			public void callback(int pos) {

				task_id = null;

				link_num = linkList.get(pos).getLinkNum();
				edtLink.setText(linkList.get(pos).getLinkName());

				PostTools.getTaskName(mContext, link_num, taskNameList);
			}
		});
	}

	public void chooseTask(View v){

		List<String> list = new ArrayList<String>();
		for(int i=0; i < taskNameList.size(); i++) {
			list.add(taskNameList.get(i).getTask_name());
		}

		SingleItemDialog.showDialog(mContext, getResources().getString(R.string.task_list), false, list, new SingleItemCallBack() {
			@Override
			public void callback(int pos) {

				edtTaskName.setText(taskNameList.get(pos).getTask_name());
				task_id = taskNameList.get(pos).getTask_code();
			}
		});
	}

	/**
	 * 查询
	 * @param v
	 */
	public void queryData(View v) {
		if(TextUtils.isEmpty(edtTaskName.getText().toString())){
			CommandTools.showToast(getResources().getString(R.string.task_not_null));
			return;
		}

		JSONObject jsonObject = new JSONObject();
		try {
			jsonObject.put("task_id", task_id);
			jsonObject.put("user_id", MyApplication.m_userID);
			jsonObject.put("node_id", MyApplication.m_nodeId);
			jsonObject.put("link_num", link_num);
			jsonObject.put("node_num", MyApplication.m_node_num);
			jsonObject.put("flag", MyApplication.m_flag);
		} catch (JSONException e) {
			e.printStackTrace();
		}

		edtBarcode.setText("");
		edtBarno.setText("");
		edtSolution.setText("");
		edtMemo.setText("");
		edtCount1.setText("0");
		edtCount2.setText("0");
		edtCount3.setText("0");
		dataList.clear();
		commonAdapter.notifyDataSetChanged();

		String strUrl = "action/query/getgoodsdetail";
		RequestUtil.postDataIfToken(mContext, strUrl, jsonObject, false, new RequestCallback() {

			@Override
			public void callback(int res, String remark, JSONObject jsonObject) {

				CommandTools.showToast(remark);
				if(res != 0 ){
					return;
				}

				jsonObject = jsonObject.optJSONObject("data");
				edtCount1.setText(jsonObject.optInt("exception_num") + "");
				edtCount2.setText(jsonObject.optInt("withdraw_num") + "");
				edtCount3.setText(jsonObject.optInt("scan_goods_num") + "");

				JSONArray jsonArray = jsonObject.optJSONArray("detail");
				for(int i=0; i<jsonArray.length(); i++){

					jsonObject = jsonArray.optJSONObject(i);

					ScanData scanData = new ScanData();
					scanData.setPackBarcode(jsonObject.optString("Pack_Barcode"));
					scanData.setPackNumber(jsonObject.optString("Pack_No"));
					
					scanData.setGoodsCode(jsonObject.optString("Goods_Barcode"));
					scanData.setGoodsNo(jsonObject.optString("Goods_No"));
					
					scanData.setScanUser(jsonObject.optString("Scan_User_Name"));
					scanData.setScanTime(CommandTools.changeL2S(jsonObject.optString("Scan_Time")));

					scanData.setScaned(jsonObject.optString("status"));//用这个字段存储状态
					scanData.setAbnormalCause(jsonObject.optString("Except_Desc"));//异常描述、退运原因
					scanData.setMemo(jsonObject.optString("Solution"));//解决方案

					dataList.add(scanData);
				}

				commonAdapter.notifyDataSetChanged();
			}
		});
	}
}
