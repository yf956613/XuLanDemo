package com.xulan.demo.activity.carline;

import java.util.ArrayList;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import com.lidroid.xutils.ViewUtils;
import com.lidroid.xutils.view.annotation.ViewInject;
import com.xulan.demo.MyApplication;
import com.xulan.demo.R;
import com.xulan.demo.activity.BaseActivity;
import com.xulan.demo.data.LinkInfo;
import com.xulan.demo.data.ScanData;
import com.xulan.demo.util.CommandTools;
import com.xulan.demo.util.PostTools;
import com.xulan.demo.util.RequestUtil;
import com.xulan.demo.util.RequestUtil.RequestCallback;
import com.xulan.demo.view.CustomProgress;
import com.xulan.demo.view.SingleItemDialog;
import com.xulan.demo.view.SingleItemDialog.SingleItemCallBack;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;

/** 
 * 车辆登记
 * 
 * @author yxx
 *
 * @date 2018-4-26 下午2:52:49
 * 
 */
public class CarCheckingActivity extends BaseActivity {

	@ViewInject(R.id.car_checking_link_num) EditText edtLinkNum;
	@ViewInject(R.id.car_checking_task_no) EditText edtTaskNo;
	@ViewInject(R.id.car_checking_guest_no) EditText edtGuestNo;
	@ViewInject(R.id.car_checking_car_no) EditText edtCarNo;
	@ViewInject(R.id.car_checking_engine_no) EditText edtEngineNo;
	@ViewInject(R.id.car_checking_brand_no) EditText edtBrandNo;
	@ViewInject(R.id.car_checking_model_no) EditText edtModel;
	@ViewInject(R.id.car_checking_color) EditText edtColor;
	@ViewInject(R.id.car_checking_date) EditText edtDate;
	@ViewInject(R.id.car_checking_sits) EditText edtSits;
	@ViewInject(R.id.car_checking_wheel) EditText edtWheel;
	@ViewInject(R.id.car_checking_deploy) EditText edtDeploy;

	private int link_num;
	private final List<LinkInfo> linkList = new ArrayList<LinkInfo>();

	@Override
	protected void onBaseCreate(Bundle savedInstanceState) {
		setContentViewId(R.layout.activity_car_checking, this);
		ViewUtils.inject(this);

	}

	@Override
	public void initView() {
		// TODO Auto-generated method stub
		setTitle("车辆登记");
		setRightTitle("提交");
	}

	@Override
	public void initData() {
		// TODO Auto-generated method stub
		PostTools.getLink(mContext, linkList);
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

				link_num = linkList.get(pos).getLinkNum();
				edtLinkNum.setText(linkList.get(pos).getLinkName());
			}
		});
	}

	/**
	 * 提交
	 * @param v
	 */
	public void clickRight(View v){

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
			header.put("Device_Id", CommandTools.getMIME(this));
			header.put("flag", MyApplication.m_flag);

			jsonObject.put("header", header);

			JSONArray jsonArray = new JSONArray();

			ScanData data = new ScanData();
			JSONObject jo = new JSONObject();

			jo.put("scan_id", CommandTools.getUUID());
			jo.put("scan_detail_id", CommandTools.getUUID());

			jo.put("packBarcode", data.getPackBarcode());
			jo.put("packNumber", data.getPackNumber());
			jo.put("MainGoodsId", data.getMainGoodsId());

			jo.put("CacheId", data.getCacheId());
			jo.put("ScanType", data.getScanType());
			jo.put("ScanTime", data.getScanTime());
			jo.put("createTime", data.getCreateTime());
			jo.put("GoodsName", data.getGoodsName());
			jo.put("Memo", data.getMemo());

			jo.put("Length", data.getLength());
			jo.put("Width", data.getWidth());
			jo.put("Height", data.getHeight());
			jo.put("Weight", data.getWeight());

			jsonArray.put(jo);

			jsonObject.put("detail", jsonArray);
		} catch (JSONException e) {
			e.printStackTrace();
		}

		CustomProgress.showDialog(mContext, "数据提交中", false, null);
		String strUrl = "upload/single";
		RequestUtil.postDataIfToken(mContext, strUrl, jsonObject, false, new RequestCallback() {

			@Override
			public void callback(int res, String remark, JSONObject jsonObject) {

				CustomProgress.dissDialog();
				CommandTools.showToast(remark);
				if(res == 0){

					clearData();
				}
			}
		});
	}

	public void clearData(){

		edtBrandNo.setText("");
		edtCarNo.setText("");
		edtColor.setText("");
		edtDate.setText("");
		edtDeploy.setText("");
		edtEngineNo.setText("");
		edtGuestNo.setText("");
		edtModel.setText("");
		edtSits.setText("");
		edtWheel.setText("");
	}
}
