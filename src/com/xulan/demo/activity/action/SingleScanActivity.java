package com.xulan.demo.activity.action;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import org.json.JSONArray;
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
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.lidroid.xutils.ViewUtils;
import com.lidroid.xutils.view.annotation.ViewInject;
import com.xulan.demo.MyApplication;
import com.xulan.demo.R;
import com.xulan.demo.activity.BaseActivity;
import com.xulan.demo.data.LinkInfo;
import com.xulan.demo.data.ScanData;
import com.xulan.demo.data.ScanInfo;
import com.xulan.demo.takephoto.Bimp;
import com.xulan.demo.util.CommandTools;
import com.xulan.demo.util.Constant;
import com.xulan.demo.util.FileUtils;
import com.xulan.demo.util.ImageTool;
import com.xulan.demo.util.PostTools;
import com.xulan.demo.util.RequestUtil;
import com.xulan.demo.util.VoiceHint;
import com.xulan.demo.util.RequestUtil.RequestCallback;
import com.xulan.demo.view.CustomProgress;
import com.xulan.demo.view.SingleItemDialog;
import com.xulan.demo.view.SingleItemDialog.SingleItemCallBack;

/** 
 * 单件录入
 * 
 * @author yxx
 *
 * @date 2016-12-16 上午9:51:35
 * 
 */
public class SingleScanActivity extends BaseActivity{

	@ViewInject(R.id.single_edt_1) EditText edtLink;
	@ViewInject(R.id.single_edt_2) EditText edtPackageNumber;
	@ViewInject(R.id.single_edt_3) EditText edtPackageBillcode;
	@ViewInject(R.id.single_edt_4) EditText edtGoodsName;
	@ViewInject(R.id.single_edt_5) EditText edtLength;
	@ViewInject(R.id.single_edt_6) EditText edtWidth;
	@ViewInject(R.id.single_edt_7) EditText edtHeight;
	@ViewInject(R.id.single_edt_8) EditText edtWeight;
	@ViewInject(R.id.single_edt_9) EditText edtRemark;

	private List<ScanData> dataList = new ArrayList<ScanData>();
	private final List<LinkInfo> linkList = new ArrayList<LinkInfo>();

	private LinearLayout photo_layout;
	private GridView gridView;
	private GVAdapter adapter;
	private static final String IMG_ADD_TAG = "a";
	private List<String> photoList = new ArrayList<String>();
	private static final int TAKE_PICTURE = 0x000001;
	private static final int TAKE_FOLDER = 0x000002;

	private int link_num;
	private String strBarcode;

	@Override
	protected void onBaseCreate(Bundle savedInstanceState) {
		setContentViewId(R.layout.activity_single_scan, this);
		ViewUtils.inject(this);

	}

	@Override
	public void initView() {
		photo_layout = (LinearLayout) findViewById(R.id.single_layout);
		gridView = (GridView) findViewById(R.id.gridview_single);

		photoList.add(IMG_ADD_TAG);
		adapter = new GVAdapter();
		gridView.setAdapter(adapter);
		gridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

				if (photoList.get(position).equals(IMG_ADD_TAG)) {

					if (photoList.size() <= Constant.MAX_PHOTO_COUNT ) {
						new PopupWindows(mContext, photo_layout);
					} else
						Toast.makeText(SingleScanActivity.this, "最多只能选择" + Constant.MAX_PHOTO_COUNT + "张照片！", Toast.LENGTH_SHORT).show();
				}
			}
		});

		refreshAdapter();
	}

	@Override
	public void initData() {
		setTitle(getResources().getString(R.string.new_1));
		setRightTitle(getResources().getString(R.string.submit));

		dataList.clear();
		linkList.clear();

		PostTools.getLink(mContext, linkList);
	}

	public void onEventMainThread(Message msg) {
		
		ScanInfo scanInfo = (ScanInfo) msg.obj;
		if(scanInfo.getWhat() == Constant.SCAN_DATA && scanInfo.getType().equals(Constant.SCAN_TYPE_SINGLE)){

			String strBillcode = scanInfo.getBarcode();
			edtPackageBillcode.setText(strBillcode);
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

		} else if (requestCode == TAKE_PICTURE) {
			if (resultCode == RESULT_OK) {
				path = strFolder;
			} else if (resultCode == RESULT_CANCELED) {
				return;
			}
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
	public void chooseLink(View v){
		
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
	 * 保存数据
	 * @param v
	 */
	public void saveData(View v){

		final String pack_barcode = edtPackageBillcode.getText().toString();
		final String pack_no = edtPackageNumber.getText().toString();

		if(TextUtils.isEmpty(pack_no)){
			VoiceHint.playErrorSounds();
			CommandTools.showToast("请录入包装号码");
			return;
		}

		for(int i=0; i<dataList.size(); i++){

			ScanData scanData = dataList.get(i);
			if(scanData.getPackBarcode().equals(pack_barcode)){
				VoiceHint.playErrorSounds();
				CommandTools.showToast("不能重复录入");
				return;
			}
		}

		String strLen = edtLength.getText().toString();
		String strWidth = edtWidth.getText().toString();
		String strHeight = edtHeight.getText().toString();
		String strWeight = edtWeight.getText().toString();
		if(TextUtils.isEmpty(strLen) || TextUtils.isEmpty(strWidth) || TextUtils.isEmpty(strHeight) || TextUtils.isEmpty(strWeight)){
			VoiceHint.playErrorSounds();
			CommandTools.showToast("长/宽/高/毛重均不能为空");
			return;
		}

		JSONObject jsonObject = new JSONObject();
		try {
			jsonObject.put("pack_barcode", pack_barcode);
		} catch (JSONException e) {
			e.printStackTrace();
		}

		String strUrl = "action/single/checkbarcode";
		RequestUtil.postDataIfToken(mContext, strUrl, jsonObject, false, new RequestCallback() {

			@Override
			public void callback(int res, String remark, JSONObject jsonObject) {

				CommandTools.showToast(remark);
				if(res != 0){
					return;
				}

				dataList.clear();
				jsonObject = jsonObject.optJSONObject("data");

				ScanData scanData = new ScanData();
				scanData.setCacheId(CommandTools.getUUID());
				scanData.setPackBarcode(pack_barcode);
				scanData.setPackNumber(pack_no);
				scanData.setMainGoodsId(CommandTools.getUUID());
				scanData.setScaned("0");//暂时用这个字段保存

				scanData.setGoodsName(edtGoodsName.getText().toString());
				scanData.setMemo(edtRemark.getText().toString());

				scanData.setLength(edtLength.getText().toString());
				scanData.setWidth(edtWidth.getText().toString());
				scanData.setHeight(edtHeight.getText().toString());
				scanData.setWeight(edtWeight.getText().toString());

				scanData.setScanUser(MyApplication.m_userName);
				scanData.setScanTime(CommandTools.getTime());

				scanData.setLink(link_num + "");

				List<String> list = new ArrayList<String>();
				for (int i = 0; i < photoList.size(); i++) {
					String str = photoList.get(i);

					if (!str.equals(IMG_ADD_TAG)) {
						list.add(str);
					}
				}
				scanData.setPicture(list);

				dataList.add(scanData);
				refreshAdapter();
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

				jo.put("markPic", CommandTools.getPicJsonArray(data.getPicture()));//拍照

				jsonArray.put(jo);
			}

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
					dataList.clear();
					finish();
				}
			}
		});
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
}
