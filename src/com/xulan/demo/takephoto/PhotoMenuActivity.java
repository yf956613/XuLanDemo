package com.xulan.demo.takephoto;

import com.xulan.demo.R;
import com.xulan.demo.activity.BaseActivity;
import com.xulan.demo.adapter.CommonAdapter;
import com.xulan.demo.adapter.ViewHolder;
import com.xulan.demo.util.CommandTools;
import com.xulan.demo.util.Constant;
import com.xulan.demo.util.FileUtils;
import com.xulan.demo.util.VoiceHint;
import com.xulan.demo.util.CommandTools.CommandToolsCallback;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.KeyEvent;
import android.view.View;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.GridView;

/** 
 * 拍照预览界面
 * 所拍照片保存在SD卡XuLan目录下
 * 
 * @author yxx
 *
 * @date 2015-12-17 下午4:16:34
 * 
 */
public class PhotoMenuActivity extends BaseActivity {

	private GridView noScrollgridview;

	private final int photoMaxSize = 5;//最多拍照数量
	private static final int TAKE_PICTURE = 0x000001;
	private String strPicName;

	private CommonAdapter<ImageItem> commonAdapter;

	@Override
	protected void onBaseCreate(Bundle savedInstanceState) {
		setContentViewId(R.layout.activity_selectimg, this);
		
	}

	@Override
	public void initView() {

		noScrollgridview = (GridView) findViewById(R.id.noScrollgridview);	
		noScrollgridview.setAdapter(commonAdapter = new CommonAdapter<ImageItem>(mContext, Bimp.tempSelectBitmapList, R.layout.item_published_grida) {

			@Override
			public void convert(ViewHolder helper, ImageItem item) {

				helper.setImageBitmap(R.id.item_grida_image, Bimp.tempSelectBitmapList.get(commonAdapter.getPosition()).getBitmap());
			}
		}); 

		noScrollgridview.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1, int arg2, long arg3) {

				Intent intent = new Intent(PhotoMenuActivity.this, GalleryActivity.class);
				intent.putExtra("position", arg2);
				intent.putExtra("ID", arg2);
				startActivity(intent);
			}
		});

	}

	@Override
	public void initData() {

		setTitle("拍照");
		try{
			Intent intent = getIntent();
			strPicName = intent.getStringExtra("picName");
		}catch(Exception e){
			e.printStackTrace();
		}

		Bimp.initPhotoList(strPicName);
		commonAdapter.notifyDataSetChanged();
	}

	protected void onRestart() {
		super.onRestart();

		commonAdapter.notifyDataSetChanged();
	}

	/**
	 * 调用系统拍照
	 */
	public void takephoto(View v) {

		if(FileUtils.existSDCard() == false){
			VoiceHint.playErrorSounds();
			CommandTools.showToast("没有SD卡");
			return;
		}

		if(Bimp.tempSelectBitmapList.size() >= photoMaxSize){
			VoiceHint.playErrorSounds();
			CommandTools.showDialog(mContext, "最多拍" + photoMaxSize + "张照片");
			return;
		}

		Intent openCameraIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
		startActivityForResult(openCameraIntent, TAKE_PICTURE);
	}

	protected void onActivityResult(int requestCode, int resultCode, Intent data) {

		switch (requestCode) {
		case TAKE_PICTURE:

			if (resultCode == RESULT_OK) {
				String fileName = strPicName + "_" + CommandTools.getTimes();

				Bitmap bm = (Bitmap) data.getExtras().get("data");
				FileUtils.saveBitmap(bm, fileName);

				ImageItem takePhoto = new ImageItem();
				takePhoto.setImagePath(Constant.SDPATH + "/" + CommandTools.getTimeDate() + "/" + fileName + ".JPEG");
				takePhoto.setBitmap(bm);
				Bimp.tempSelectBitmapList.add(takePhoto);

				commonAdapter.notifyDataSetChanged();
			}
			break;
		}
	}

	public boolean onKeyDown(int keyCode, KeyEvent event) {

		if (keyCode == KeyEvent.KEYCODE_BACK) {

			if(Bimp.tempSelectBitmapList.size() > 0){
				back(null);
				return false;
			}else{
				back(null);
			}

		}
		return true;
	}

	public void save(View v){

		StringBuilder sbPath = new StringBuilder();
		int len = Bimp.tempSelectBitmapList.size();
		for(int i=0; i<len; i++){

			ImageItem item = Bimp.tempSelectBitmapList.get(i);
			if(i < len - 1){
				sbPath.append(item.getImagePath()).append(",");
			}else{
				sbPath.append(item.getImagePath());
			}
		}

		final String strPath = sbPath.toString();

		Intent intent = new Intent();
		intent.putExtra("path", strPath);
		setResult(RESULT_OK, intent);
		PhotoMenuActivity.this.finish();
	}

	public void back(View v){

		CommandTools.showChooseDialog(mContext, "是否退出拍照", new CommandToolsCallback() {

			@Override
			public void callback(int position) {
				// TODO Auto-generated method stub
				if(position == 0){

					Bimp.tempSelectBitmapList.clear();
					PhotoMenuActivity.this.finish();
				}
			}
		});

	}

}

