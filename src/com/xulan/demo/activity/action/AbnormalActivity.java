package com.xulan.demo.activity.action;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import org.json.JSONException;
import org.json.JSONObject;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.Message;
import android.provider.MediaStore;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;
import android.widget.Toast;

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
import com.xulan.demo.net.AsyncNetTask;
import com.xulan.demo.net.LoadTextNetTask;
import com.xulan.demo.net.LoadTextResult;
import com.xulan.demo.net.NetTaskResult;
import com.xulan.demo.net.AsyncNetTask.OnPostExecuteListener;
import com.xulan.demo.service.UserService;
import com.xulan.demo.takephoto.Bimp;
import com.xulan.demo.util.CommandTools;
import com.xulan.demo.util.Constant;
import com.xulan.demo.util.FileUtils;
import com.xulan.demo.util.ImageTool;
import com.xulan.demo.util.Logs;
import com.xulan.demo.util.PostTools;
import com.xulan.demo.util.RequestUtil;
import com.xulan.demo.util.VoiceHint;
import com.xulan.demo.util.RequestUtil.RequestCallback;
import com.xulan.demo.view.CustomProgress;
import com.xulan.demo.view.SingleItemDialog;
import com.xulan.demo.view.SingleItemDialog.SingleItemCallBack;

/** 
 * 异常
 * 
 * @author yxx
 *
 * @date 2016-12-22 上午10:10:04
 * 
 */
public class AbnormalActivity extends BaseActivity implements OnClickListener {

	@ViewInject(R.id.lv_public) ListView mListView;
	@ViewInject(R.id.abnormal_tv_1) EditText edtLink;
	@ViewInject(R.id.abnormal_tv_2) EditText edtBillcode;
	@ViewInject(R.id.abnormal_tv_3) EditText edtNumber;
	@ViewInject(R.id.abnormal_tv_4) EditText edtRemark;
	@ViewInject(R.id.abnormal_tv_6) EditText edtCount;

	private RelativeLayout billCodeImg;

	private CommonAdapter<ScanData> commonAdapter;
	private List<ScanData> dataList = new ArrayList<ScanData>();
	private List<LinkInfo> linkList = new ArrayList<LinkInfo>();

	private int link_num = -1;
	private String strBarcode;

	private LinearLayout abnormal_layout;
	private GridView gridView;
	private GVAdapter adapter;
	private static final String IMG_ADD_TAG = "a";
	private List<String> photoList = new ArrayList<String>();

	private static final int TAKE_PICTURE = 0x000001;
	private static final int TAKE_FOLDER = 0x000002;

	private ScanDataDao mScanDataDao = new ScanDataDao();

	@Override
	protected void onBaseCreate(Bundle savedInstanceState) {
		setContentViewId(R.layout.activity_abnormal, this);
		ViewUtils.inject(this);

	}

	@Override
	public void initView() {
		billCodeImg = (RelativeLayout) findViewById(R.id.bill_code_img);

		dataList = mScanDataDao.getNotUploadDataList(MyApplication.m_scan_type, MyApplication.m_link_num + "", MyApplication.m_nodeId);
		mListView.setAdapter(commonAdapter = new CommonAdapter<ScanData>(mContext, dataList, R.layout.abnormal_item) {

			@Override
			public void convert(ViewHolder helper, ScanData item) {

				helper.setText(R.id.abnormal_item_tv0, commonAdapter.getIndex());
				helper.setText(R.id.abnormal_item_tv1, item.getPackBarcode());
				helper.setText(R.id.abnormal_item_tv2, item.getPackNumber());
				helper.setText(R.id.abnormal_item_tv3, item.getOperationLink());
				helper.setText(R.id.abnormal_item_tv4, item.getMemo());
				helper.setText(R.id.abnormal_item_tv5, item.getScanUser());
				helper.setText(R.id.abnormal_item_tv6, item.getScanTime());
			}
		});

		mListView.setOnItemClickListener(new OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1, int arg2, long arg3) {

			}
		});

		abnormal_layout = (LinearLayout) findViewById(R.id.abnormal_layout);
		gridView = (GridView) findViewById(R.id.gridview_abnormal);

		billCodeImg.setOnClickListener(this);
	}

	@Override
	public void initData() {

		setTitle(getResources().getString(R.string.problemcargo));
		setRightTitle(getResources().getString(R.string.submit));

		photoList.add(IMG_ADD_TAG);
		adapter = new GVAdapter();
		gridView.setAdapter(adapter);
		gridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
				if (photoList.get(position).equals(IMG_ADD_TAG)) {
					if (photoList.size() <= Constant.MAX_PHOTO_COUNT ) {
						new PopupWindows(mContext, abnormal_layout);
					} else
						Toast.makeText(AbnormalActivity.this, "最多只能选择" + Constant.MAX_PHOTO_COUNT + "张照片！", Toast.LENGTH_SHORT).show();
				}
			}
		});

		refreshAdapter();
		PostTools.getLink(mContext, linkList);
	}

	public void onEventMainThread(Message msg) {

		ScanInfo scanInfo = (ScanInfo) msg.obj;
		if(scanInfo.getWhat() == Constant.SCAN_DATA && scanInfo.getType().equals(Constant.SCAN_TYPE_ABNORMAL)){

			String strBillcode = scanInfo.getBarcode();
			edtBillcode.setText(strBillcode);
			addData(null);
		}
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.bill_code_img:
			// 扫描
			Intent openCameraIntent = new Intent(AbnormalActivity.this, CaptureActivity.class);
			startActivityForResult(openCameraIntent, Constant.CAPTURE_BILLCODE);
			break;

		default:
			break;
		}
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		String path = "";
		if (requestCode == TAKE_FOLDER) {
			if (data == null) {
				return;
			}

			final Uri uri = data.getData();
			path = ImageTool.getImageAbsolutePath(this, uri);

			if (photoList.size() == Constant.MAX_PHOTO_COUNT + 1) {
				removeItem();
				refreshAdapter();
				return;
			}
			removeItem();
			photoList.add(path);
			photoList.add(IMG_ADD_TAG);
			refreshAdapter();

		} else if (requestCode == TAKE_PICTURE) {
			if (resultCode == RESULT_OK) {
				path = strFolder;
			} else if (resultCode == RESULT_CANCELED) {
				return;
			}

			if (photoList.size() == Constant.MAX_PHOTO_COUNT + 1) {
				removeItem();
				refreshAdapter();
				return;
			}
			removeItem();
			photoList.add(path);
			photoList.add(IMG_ADD_TAG);
			refreshAdapter();
		} else if (requestCode == Constant.CAPTURE_BILLCODE) {
			if (data == null) {
				return;
			}

			Bundle bundle = data.getExtras();
			String strBillcode = bundle.getString("result");

			edtBillcode.setText(strBillcode);
			addData(null);

			//			ScanData scanData = DataUtilTools.checkScanData(strBillcode, dataList);
			//			if (scanData != null) {
			//
			//				edtBillcode.setText(scanData.getPackBarcode());
			//				edtNumber.setText(scanData.getPackNumber());
			//				addData(null);
			//			} else {
			//				VoiceHint.playErrorSounds();
			//				CommandTools.showToast("条码不存在");
			//			}
			return;
		}
	}

	private void removeItem() {
		if (photoList.size() != Constant.MAX_PHOTO_COUNT + 1) {
			if (photoList.size() != 0) {
				photoList.remove(photoList.size() - 1);
			}
		}
	}

	/**
	 * @param v
	 */
	public void chooseNode(View v) {

		List<String> list = new ArrayList<String>();
		for(int i=0; i<linkList.size(); i++){

			list.add(linkList.get(i).getLinkName());
		}

		SingleItemDialog.showDialog(mContext, getResources().getString(R.string.mode_step), false, list, new SingleItemCallBack() {

			@Override
			public void callback(int pos) {

				link_num = linkList.get(pos).getLinkNum();
				edtLink.setText(linkList.get(pos).getLinkName());
			}
		});
	}

	/**
	 * 添加
	 * 检查该货物是否存在，并获取goods_id
	 * @param v
	 */
	public void addData(View v) {

		final String edt_Link = edtLink.getText().toString();
		final String edt_Billcode = edtBillcode.getText().toString();
		final String edt_Number = edtNumber.getText().toString();
		final String edt_Remark = edtRemark.getText().toString();
		if (edt_Remark.equals("")) {
			CommandTools.showToast(getResources().getString(R.string.d_problem_is_required));
			return;
		}

		if (photoList.size() <= 1) {
			CommandTools.showToast(getResources().getString(R.string.img_not_null));
			return;
		}

		if (link_num == -1) {
			CommandTools.showToast(getResources().getString(R.string.select_mode_step));
			return;
		}

		if (TextUtils.isEmpty(edt_Billcode)) {
			CommandTools.showToast(getResources().getString(R.string.code_not_null));
			return;
		}

		JSONObject jsonObject = new JSONObject();
		try {
			jsonObject.put("project_id", MyApplication.m_projectId);
			jsonObject.put("node_id", MyApplication.m_nodeId);
			jsonObject.put("link_num", link_num);
			jsonObject.put("type", MyApplication.m_scan_type);
			jsonObject.put("pack_barcode", edt_Billcode);
		} catch (JSONException e) {
			e.printStackTrace();
		}

		CustomProgress.showDialog(mContext, getResources().getString(R.string.searching), true, null);
		String strUrl = "action/exception/checkbarcode";
		RequestUtil.postDataIfToken(mContext, strUrl, jsonObject, false, new RequestCallback() {

			@Override
			public void callback(int res, String remark, JSONObject jsonObject) {
				// TODO Auto-generated method stub

				CommandTools.showToast(remark);
				if(res != 0){
					return;
				}

				String goods_id = jsonObject.optJSONObject("data").optString("id");
				String pack_no = jsonObject.optJSONObject("data").optString("pack_no");

				edtNumber.setText(pack_no + ""); 

				ScanData scanData = new ScanData();

				scanData.setCacheId(CommandTools.getUUID());
				scanData.setOperationLink(edt_Link);

				scanData.setPackBarcode(edt_Billcode);
				scanData.setPackNumber(pack_no);
				scanData.setMainGoodsId(goods_id);//货物id

				scanData.setMemo(edt_Remark);
				scanData.setScanTime(CommandTools.getTime());
				scanData.setCreateTime(CommandTools.getTime());
				scanData.setScanType(MyApplication.m_scan_type);
				scanData.setLink(MyApplication.m_link_num + "");
				scanData.setScanUser(MyApplication.m_userName);
				scanData.setNode_id(MyApplication.m_nodeId);
				scanData.setScaned("1");
				scanData.setUploadStatus("0");

				List<String> list = new ArrayList<String>();
				for (int i = 0; i < photoList.size(); i++) {
					String str = photoList.get(i);

					if (!str.equals(IMG_ADD_TAG)) {
						list.add(str);
					}
				}
				scanData.setPicture(list);

				dataList.add(scanData);

				commonAdapter.notifyDataSetChanged();
				mScanDataDao.addData(scanData);
				mScanDataDao.addPicData(scanData);

				edtBillcode.setText("");
				edtNumber.setText("");
				edtRemark.setText("");

				edtCount.setText(dataList.size() + "");
			}
		});

	}

	/**
	 * 上传数据
	 */
	protected LoadTextNetTask requestUpload(final List<ScanData> list) {

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
							mScanDataDao.updateUploadState(list);
						}
						String message = jsonObj.getString("message");
						CommandTools.showToast(message);
						if (success == 0) {
							finish();
						}
					} catch (JSONException e) {
						Toast.makeText(AbnormalActivity.this, "解析错误", 1).show();
						e.printStackTrace();
					}
				} else {
				}
			}
		};

		CustomProgress.showDialog(AbnormalActivity.this, getResources().getString(R.string.searching), false, null);
		LoadTextNetTask task = UserService.exception(list, "", link_num, listener, null);
		return task;
	}

	/**
	 * 完成
	 * @param v
	 */
	public void clickRight(View v){

		if (dataList.size() <= 0) {
			CommandTools.showToast(getResources().getString(R.string.scan_records));
		} else {
			requestUpload(dataList);
		}
	}

	public class PopupWindows extends PopupWindow {
		public PopupWindows(Context mContext, View parent) {
			super(mContext);

			View view = View.inflate(mContext, R.layout.item_popupwindows, null);
			LinearLayout ll_popup = (LinearLayout) view.findViewById(R.id.ll_popup);
			getBackground().setAlpha(80);
			setAnimationStyle(R.style.AnimationFade);
			setWidth(LayoutParams.MATCH_PARENT);
			setHeight(LayoutParams.MATCH_PARENT);
			setFocusable(true);
			setOutsideTouchable(false);
			setContentView(view);
			showAtLocation(parent, Gravity.BOTTOM, 0, 0);
			update();

			RelativeLayout layout = (RelativeLayout) view.findViewById(R.id.layout_pop_window);
			Button bt1 = (Button) view.findViewById(R.id.item_popupwindows_camera);
			Button bt2 = (Button) view.findViewById(R.id.item_popupwindows_Photo);
			Button bt3 = (Button) view.findViewById(R.id.item_popupwindows_cancel);

			layout.setOnClickListener(new OnClickListener() {
				@Override
				public void onClick(View arg0) {
					dismiss();
				}
			});

			bt1.setOnClickListener(new OnClickListener() {
				public void onClick(View v) {

					getFromCamera();
					dismiss();
				}
			});

			bt2.setOnClickListener(new OnClickListener() {
				public void onClick(View v) {

					getFromFolder();
					dismiss();
				}
			});

			bt3.setOnClickListener(new OnClickListener() {
				public void onClick(View v) {
					dismiss();
				}
			});

		}
	}

	String strFolder;

	public void getFromCamera() {
		if(FileUtils.existSDCard() == false){
			VoiceHint.playErrorSounds();
			CommandTools.showToast("没有SD卡");
			return;
		}

		if(Bimp.tempSelectBitmapList.size() >= Constant.MAX_PHOTO_COUNT){
			VoiceHint.playErrorSounds();
			CommandTools.showDialog(mContext, "最多拍" + Constant.MAX_PHOTO_COUNT + "张照片");
			return;
		}

		String fileName = CommandTools.getTimes();
		strFolder = Environment.getExternalStorageDirectory().getPath() + "/" +  fileName + ".JPEG";
		Intent openCameraIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
		openCameraIntent.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(new File(strFolder)));
		startActivityForResult(openCameraIntent, TAKE_PICTURE);
	}

	public void getFromFolder() {
		Intent i = new Intent(Intent.ACTION_PICK, android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
		startActivityForResult(i, TAKE_FOLDER);
	}


	private class GVAdapter extends BaseAdapter {

		@Override
		public int getCount() {
			return photoList.size();
		}

		@Override
		public Object getItem(int position) {
			return null;
		}

		@Override
		public long getItemId(int position) {
			return 0;
		}

		@Override
		public View getView(final int position, View convertView, ViewGroup parent) {
			final ViewHolder holder;
			if (convertView == null) {
				convertView = LayoutInflater.from(getApplication()).inflate(R.layout.activity_add_photo_gv_items, parent, false);
				holder = new ViewHolder();
				holder.imageView = (ImageView) convertView.findViewById(R.id.main_gridView_item_photo);
				holder.checkBox = (ImageView) convertView.findViewById(R.id.main_gridView_item_cb);
				convertView.setTag(holder);
			} else {
				holder = (ViewHolder) convertView.getTag();
			}
			String s = photoList.get(position);
			if (!s.equals(IMG_ADD_TAG)) {
				holder.checkBox.setVisibility(View.VISIBLE);
				holder.imageView.setImageBitmap(ImageTool.createImageThumbnail(s));
			} else {
				holder.checkBox.setVisibility(View.GONE);
				holder.imageView.setImageResource(R.drawable.phonet);
			}
			holder.checkBox.setOnClickListener(new View.OnClickListener() {
				@Override
				public void onClick(View v) {
					photoList.remove(position);
					refreshAdapter();
				}
			});
			return convertView;
		}

		private class ViewHolder {
			ImageView imageView;
			ImageView checkBox;
		}

	}

	private void refreshAdapter() {
		if (photoList == null) {
			photoList = new ArrayList<String>();
		}
		if (adapter == null) {
			adapter = new GVAdapter();
		}
		adapter.notifyDataSetChanged();
	}

}
