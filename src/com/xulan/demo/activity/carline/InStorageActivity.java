package com.xulan.demo.activity.carline;

import java.util.ArrayList;
import java.util.List;
import org.json.JSONException;
import org.json.JSONObject;
import com.lidroid.xutils.ViewUtils;
import com.lidroid.xutils.view.annotation.ViewInject;
import com.xulan.demo.MyApplication;
import com.xulan.demo.R;
import com.xulan.demo.activity.BaseActivity;
import com.xulan.demo.activity.action.land.LandActivity;
import com.xulan.demo.adapter.CommonAdapter;
import com.xulan.demo.adapter.ViewHolder;
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
import com.xulan.demo.util.HandleDataTools;
import com.xulan.demo.util.Logs;
import com.xulan.demo.util.VoiceHint;
import com.xulan.demo.view.CustomProgress;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

/** 
 * 平行车---出库、入库
 * 
 * @author yxx
 *
 * @date 2018-4-26 上午9:43:01
 * 
 */
public class InStorageActivity extends BaseActivity {

	@ViewInject(R.id.lv_public) ListView mListView;
	@ViewInject(R.id.carline_edt_taskno) EditText edtTaskNo;//业务编号
	@ViewInject(R.id.carline_edt_carno) EditText edtCarNo;//车架号
	@ViewInject(R.id.carline_edt_storageno) EditText edtStorageNo;//库位号
	@ViewInject(R.id.carline_edt_user) EditText edtUser;//操作员
	@ViewInject(R.id.carline_edt_count) EditText edtCount;//扫描数

	private CommonAdapter<ScanData> commonAdapter;
	private List<ScanData> dataList = new ArrayList<ScanData>();//列表里的数据
	private List<ScanData> uploadList = new ArrayList<ScanData>();//上传数据

	private int scan_num = 0;
	private String taskId = "";
	private ScanDataDao mScandataDao = new ScanDataDao();

	@Override
	protected void onBaseCreate(Bundle savedInstanceState) {
		setContentViewId(R.layout.activity_in_storage, this);
		ViewUtils.inject(this);

	}

	@Override
	public void initView() {
		// TODO Auto-generated method stub

		dataList = mScandataDao.getNotUploadDataList(MyApplication.m_scan_type, MyApplication.m_link_num + "", MyApplication.m_nodeId);
		scan_num = dataList.size();

		mListView.setAdapter(commonAdapter = new CommonAdapter<ScanData>(mContext, dataList, R.layout.linecar_item_title) {

			@Override
			public void convert(ViewHolder helper, ScanData item) {

				helper.setText(R.id.linecar_item_title_1, commonAdapter.getIndex());
				helper.setText(R.id.linecar_item_title_2, item.getTaskId());
				helper.setText(R.id.linecar_item_title_3, item.getPackBarcode());
				helper.setText(R.id.linecar_item_title_4, item.getPackNumber());
				helper.setText(R.id.linecar_item_title_5, item.getScanUser());
				helper.setText(R.id.linecar_item_title_6, item.getScanTime());
			}
		});

	}

	@Override
	public void initData() {
		// TODO Auto-generated method stub
		setTitle(getIntent().getStringExtra("actionName"));
		setRightTitle(getResources().getString(R.string.submit));
	}

	public void scan(View v){

		Intent openCameraIntent = new Intent(InStorageActivity.this, CaptureActivity.class);
		startActivityForResult(openCameraIntent, Constant.CAPTURE_BILLCODE);
	}

	protected void onActivityResult(int requestCode, int resultCode, Intent data) {

		if (requestCode == Constant.CAPTURE_BILLCODE) {
			if (data == null) {
				return;
			}

			Bundle bundle = data.getExtras();
			String strBillcode = bundle.getString("result");
			edtCarNo.setText(strBillcode);

			addData(null);
			return;
		}
	}

	/**
	 * 保存数据
	 * @param v
	 */
	public void addData(View v){

		String strCarNo = edtCarNo.getText().toString();
		String strStorageNo = edtStorageNo.getText().toString();

		String strTaskName = strCarNo + "-" + strStorageNo;
		if(TextUtils.isEmpty(strCarNo) || TextUtils.isEmpty(strStorageNo)){
			VoiceHint.playErrorSounds();
			CommandTools.showToast("车架号或库位号不能为空");
			return;
		}

		for (int i = 0; i < dataList.size(); i++) {
			ScanData data = dataList.get(i);

			if (data.getPackBarcode().equals(strCarNo) && data.getScaned().equals("1")) {
				VoiceHint.playErrorSounds();
				CommandTools.showToast("条码已扫描");
				break;
			}else if (data.getPackBarcode().equals(strCarNo)) {

				data.setTaskName(strTaskName);

				data.setTaskId(taskId);
				data.setPackBarcode(strCarNo);
				data.setPackNumber(strStorageNo);
				data.setScanTime(CommandTools.getTime());
				data.setScanUser(MyApplication.m_userName);
				data.setScanType(Constant.SCAN_TYPE_LAND);
				data.setCreateTime(CommandTools.getTime());
				data.setLink(MyApplication.m_link_num + "");
				data.setNode_id(MyApplication.m_nodeId);
				data.setScaned("1");
				data.setUploadStatus("0");

				data.setLinkMan(MyApplication.m_userName);

				mScandataDao.addData(data);//保存数据

				CommandTools.showToast("保存成功");
				commonAdapter.notifyDataSetChanged();

				edtCount.setText(++scan_num + "");
				break;
			}
		}
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
						}
					} catch (JSONException e) {
						Toast.makeText(InStorageActivity.this, "解析错误", 1).show();
						e.printStackTrace();
					}
				} else {
				}
			}
		};

		CustomProgress.showDialog(InStorageActivity.this, getResources().getString(R.string.searching), false, null);
		LoadTextNetTask task = UserService.upload(list, taskId, Constant.SCAN_TYPE_LAND, listener, null);
		return task;
	}


}
