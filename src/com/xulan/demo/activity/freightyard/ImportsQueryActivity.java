package com.xulan.demo.activity.freightyard;

import java.util.ArrayList;
import java.util.List;

import android.os.Bundle;
import android.view.View;

import com.lidroid.xutils.ViewUtils;
import com.xulan.demo.R;
import com.xulan.demo.activity.BaseActivity;
import com.xulan.demo.adapter.CommonAdapter;
import com.xulan.demo.data.ScanData;

/** 
 * 进出口查询
 * 
 * @author hexiuhui
 *
 * @date 2016-12-20 下午3:07:35
 * 
 */
public class ImportsQueryActivity extends BaseActivity {

	private CommonAdapter<ScanData> commonAdapter;
	private List<ScanData> dataList = new ArrayList<ScanData>();

	@Override
	protected void onBaseCreate(Bundle savedInstanceState) {
		setContentViewId(R.layout.activity_imporys_query, this);
		ViewUtils.inject(this);
	}

	@Override
	public void initView() {

	}

	@Override
	public void initData() {
		// TODO Auto-generated method stub
		setTitle(getResources().getString(R.string.search));

		for(int i=0; i<10; i++){
			ScanData scanData = new ScanData();
//			scanData.setScanHawb(CommandTools.getTime());
			dataList.add(scanData);
		}

		commonAdapter.notifyDataSetChanged();
	}
	

	/**
	 * 查询
	 * @param v
	 */
	public void queryData(View v){

	}
}
