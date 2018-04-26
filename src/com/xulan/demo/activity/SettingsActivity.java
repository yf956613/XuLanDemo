package com.xulan.demo.activity;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;

import com.lidroid.xutils.ViewUtils;
import com.xulan.demo.MyApplication;
import com.xulan.demo.R;
import com.xulan.demo.db.dao.ScanDataDao;
import com.xulan.demo.util.CommandTools;
import com.xulan.demo.util.DataCleanManager;

/** 
 * 设置
 * 
 * @author hexiuhui
 * 
 */
public class SettingsActivity extends BaseActivity implements OnClickListener{

	private Button mUpdatePassWord;
	private Button mCleanData;
	
	@Override
	protected void onBaseCreate(Bundle savedInstanceState) {
		setContentViewId(R.layout.activity_settings, this);
		ViewUtils.inject(this);
	}

	@Override
	public void initView() {
		mUpdatePassWord = (Button) findViewById(R.id.btn_update_password);
		mCleanData = (Button) findViewById(R.id.btn_clean_data);
		
		mUpdatePassWord.setOnClickListener(this);
		mCleanData.setOnClickListener(this);
	}

	@Override
	public void initData() {
		setTitle(getResources().getString(R.string.settings));
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.btn_update_password:
			Intent intent = new Intent(SettingsActivity.this, UpdatePassWordActivity.class);
			startActivity(intent);
			break;
		case R.id.btn_clean_data:
			new AlertDialog.Builder(this).setTitle(getResources().getString(R.string.prompt))
			.setMessage("清理数据将重启应用")
			.setPositiveButton(getResources().getString(R.string.yes), new DialogInterface.OnClickListener() {
				@Override
				public void onClick(DialogInterface dialog, int which) {
//					DataCleanManager.cleanApplicationData(SettingsActivity.this);
					new ScanDataDao().clearTable();
					CommandTools.showToast("清除成功");
					MyApplication.finishAllActivities();
					finish();
					
					Intent i = getBaseContext().getPackageManager() .getLaunchIntentForPackage(getBaseContext().getPackageName()); 
					i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP); 
					startActivity(i);
				}
			}).setNegativeButton(getResources().getString(R.string.no), null).show();
			break;

		default:
			break;
		}
	}

}
