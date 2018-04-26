package com.xulan.demo.activity;

import java.util.List;

import com.xulan.demo.MyApplication;
import com.xulan.demo.R;
import com.xulan.demo.data.RfidInfo;
import com.xulan.demo.pdascan.RFID;
import com.xulan.demo.util.Constant;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.os.Message;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.Window;
import android.widget.LinearLayout;
import android.widget.LinearLayout.LayoutParams;
import android.widget.TextView;
import de.greenrobot.event.EventBus;

/** 
 * 顶部基类
 * 
 * @author 
 *
 * @date 2016-12-12 下午4:13:35
 * 
 */
public abstract class BaseActivity extends Activity {

	public Context mContext;

	private LinearLayout layoutBody;
	protected View contentView;

	private TextView tvLeft;
	private TextView tvTitle;
	private TextView tvRight;

	private EventBus mEventBus;
	public String rfidTag;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.activity_base);

		mContext = this;
		MyApplication.getInstance().addActivity(this);
		layoutBody = (LinearLayout) findViewById(R.id.baseAct_body);

		if(mEventBus == null) {
			mEventBus = EventBus.getDefault();
			mEventBus.register(this);
		}

		onBaseCreate(savedInstanceState);
		findViewById();
		initView();
		initData();
	}

	public void setContentViewId(int layoutId, final Activity activity) {

		contentView = getLayoutInflater().inflate(layoutId, null);
		if (layoutBody.getChildCount() > 0) {
			layoutBody.removeAllViews();
		}

		if (contentView != null) {
			LayoutParams params = new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT);
			layoutBody.addView(contentView, params);
		}
	}

	private void findViewById() {

		tvLeft = (TextView) findViewById(R.id.tv_item_left);
		tvTitle = (TextView) findViewById(R.id.tv_item_center);
		tvRight = (TextView) findViewById(R.id.tv_item_right);
	}

	/**
	 * @param msg
	 */
	public void onEventMainThread(Message msg) {

		//针对RFID操作，如果该货物成功保存，则该货物对应的集合saved置为true,避免下次重复请求
		if(msg.what == Constant.CHECK_RFID_DATA){

			List<RfidInfo> listTag = RFID.listTag;

			String pack_barcode = (String) msg.obj;
			if(listTag != null && listTag.size() > 0){

				int len = listTag.size();
				for(int i=0; i<len; i++){

					RfidInfo info = listTag.get(i);
					Log.v("rfid", "保存后查看数据: " + info.getId() + "," + info.getPack_barcode() + "," + info.getLink_num());
					if(info.getPack_barcode().equals(pack_barcode)){
						//						info.setSaved(true);
						break;
					}
				}
			}
		}
	}

	/**
	 * 创建界面
	 * 
	 * @param savedInstanceState
	 */
	protected abstract void onBaseCreate(Bundle savedInstanceState);

	/**
	 * 初始化控件
	 */
	public abstract void initView();

	/**
	 * 初始化数据
	 */
	public abstract void initData();

	/**
	 * 设置标题
	 * 
	 * @param mReturn
	 */
	public void setTitle(String mTitle) {
		tvTitle.setText(mTitle);
	}

	/**
	 * 设置右侧标题(主要数字显示)
	 * @param mTitle
	 */
	public void setRightTitle(String mTitle){
		tvRight.setText(mTitle);
	}

	/**
	 * 隐藏左侧返回键
	 */
	public void hidenLeftMenu(){
		tvLeft.setVisibility(View.INVISIBLE);
	}

	/**
	 * 隐藏左侧返回键
	 */
	public void hidenRightMenu(){
		tvRight.setVisibility(View.INVISIBLE);
	}

	public void back(View v){

		MyApplication.getEventBus().unregister(this);
		finish();
	}

	/**
	 * 右侧按钮点击事件
	 * 子类直接复写该方法即可
	 * @param v
	 */
	public void clickRight(View v){

	}

	/**
	 * 按照包装条码排序
	 * @param v
	 */
	public void sortByPackBarcode(View v){

	}

	/**
	 * 按照包装号码排序
	 * @param v
	 */
	public void sortByPackNo(View v){

	}

	/**
	 * 
	 */
	public void onDestory(){
		super.onDestroy();

	}

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		
		if (keyCode == KeyEvent.KEYCODE_BACK) { // 获取 back键

			back(null);
		}
		
		return false;
	}

}
