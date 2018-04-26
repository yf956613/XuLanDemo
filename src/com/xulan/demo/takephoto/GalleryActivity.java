package com.xulan.demo.takephoto;

import java.util.ArrayList;

import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup.LayoutParams;
import android.widget.Button;
import android.widget.TextView;

import com.xulan.demo.R;
import com.xulan.demo.activity.BaseActivity;
import com.xulan.demo.util.FileUtils;

/** 
 * 图片浏览时的界面
 * 
 * 支持图片删除，包括SD卡目录的文件
 * 
 * @author yxx
 *
 * @date 2015-12-17 下午4:34:11
 * 
 */
public class GalleryActivity extends BaseActivity {
	private Intent intent;

	private Button del_bt;	//删除按钮

	private int position;	//获取前一个activity传过来的position
	private int location = 0;	//获取前一个activity传过来的position

	private ArrayList<View> listViews = null;
	private ViewPagerFixed pager;
	private MyPageAdapter adapter;

	private TextView tvPos;
	private TextView tvTotalCount;

	@Override
	protected void onBaseCreate(Bundle savedInstanceState) {
		setContentViewId(R.layout.plugin_camera_gallery, this);
		
		findViewById();
	}
	
	@Override
	public void initView() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void initData() {
		// TODO Auto-generated method stub
		setTitle("图片预览");
	}

	protected void findViewById() {

		del_bt = (Button)findViewById(R.id.gallery_del);
		del_bt.setOnClickListener(new DelListener());
		intent = getIntent();
		position = intent.getIntExtra("position", 1);

		tvPos = (TextView) findViewById(R.id.tv_gallery_postion);
		tvTotalCount = (TextView) findViewById(R.id.tv_gallery_total);

		tvPos.setText((position+1) + "");
		tvTotalCount.setText(Bimp.tempSelectBitmapList.size() + "");

		pager = (ViewPagerFixed) findViewById(R.id.gallery01);
		pager.setOnPageChangeListener(pageChangeListener);
		for (int i = 0; i < Bimp.tempSelectBitmapList.size(); i++) {
			initListViews( Bimp.tempSelectBitmapList.get(i).getBitmap() );
		}

		adapter = new MyPageAdapter(listViews);
		pager.setAdapter(adapter);
		pager.setPageMargin(1);
		int id = intent.getIntExtra("ID", 0);
		pager.setCurrentItem(id);
	}

	private OnPageChangeListener pageChangeListener = new OnPageChangeListener() {

		public void onPageSelected(int arg0) {
			location = arg0;
		}

		public void onPageScrolled(int arg0, float arg1, int arg2) {

		}

		public void onPageScrollStateChanged(int arg0) {

			tvPos.setText((location+1) + "");
			tvTotalCount.setText(Bimp.tempSelectBitmapList.size() + "");
		}
	};

	private void initListViews(Bitmap bm) {
		if (listViews == null)
			listViews = new ArrayList<View>();
		PhotoView img = new PhotoView(this);
		img.setBackgroundColor(0xff000000);
		img.setImageBitmap(bm);
		img.setLayoutParams(new LayoutParams(LayoutParams.MATCH_PARENT,
				LayoutParams.MATCH_PARENT));
		listViews.add(img);
	}

	// 删除按钮添加的监听器
	private class DelListener implements OnClickListener {

		public void onClick(View v) {

			String strPath = Bimp.tempSelectBitmapList.get(location).getImagePath();
			FileUtils.delFile(strPath);

			if (listViews.size() == 1) {
				Bimp.tempSelectBitmapList.clear();
				Bimp.max = 0;
				Intent intent = new Intent("data.broadcast.action");  
				sendBroadcast(intent);  
				finish();
			} else {
				Bimp.tempSelectBitmapList.remove(location);
				Bimp.max--;
				pager.removeAllViews();
				listViews.remove(location);
				adapter.setListViews(listViews);
				adapter.notifyDataSetChanged();

				tvPos.setText((location+1) + "");
				tvTotalCount.setText(Bimp.tempSelectBitmapList.size() + "");
			}

		}
	}

	/**
	 * 监听返回按钮
	 */
	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {

		if (keyCode == KeyEvent.KEYCODE_BACK) {
			finish();
		}
		return true;
	}


	class MyPageAdapter extends PagerAdapter {

		private ArrayList<View> listViews;

		private int size;
		public MyPageAdapter(ArrayList<View> listViews) {
			this.listViews = listViews;
			size = listViews == null ? 0 : listViews.size();
		}

		public void setListViews(ArrayList<View> listViews) {
			this.listViews = listViews;
			size = listViews == null ? 0 : listViews.size();
		}

		public int getCount() {
			return size;
		}

		public int getItemPosition(Object object) {
			return POSITION_NONE;
		}

		public void destroyItem(View arg0, int arg1, Object arg2) {
			((ViewPagerFixed) arg0).removeView(listViews.get(arg1 % size));
		}

		public void finishUpdate(View arg0) {
		}

		public Object instantiateItem(View arg0, int arg1) {
			try {
				((ViewPagerFixed) arg0).addView(listViews.get(arg1 % size), 0);

			} catch (Exception e) {
			}
			return listViews.get(arg1 % size);
		}

		public boolean isViewFromObject(View arg0, Object arg1) {
			return arg0 == arg1;
		}

	}

}
