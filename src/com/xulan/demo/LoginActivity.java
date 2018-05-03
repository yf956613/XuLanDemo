package com.xulan.demo;

import java.util.Locale;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlarmManager;
import android.app.AlertDialog;
import android.app.PendingIntent;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.content.res.Configuration;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnLongClickListener;
import android.view.Window;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.lidroid.xutils.ViewUtils;
import com.lidroid.xutils.view.annotation.ViewInject;
import com.xulan.demo.R;
import com.xulan.demo.activity.LuYouActivity;
import com.xulan.demo.activity.SettingsActivity;
import com.xulan.demo.net.AsyncNetTask;
import com.xulan.demo.net.HttpUtils;
import com.xulan.demo.net.LoadTextResult;
import com.xulan.demo.net.NetTaskResult;
import com.xulan.demo.net.AsyncNetTask.OnPostExecuteListener;
import com.xulan.demo.service.CommonService;
import com.xulan.demo.util.CommandTools;
import com.xulan.demo.util.Constant;
import com.xulan.demo.util.ExcelUtil;
import com.xulan.demo.util.Logs;
import com.xulan.demo.util.RequestUtil;
import com.xulan.demo.util.Res;
import com.xulan.demo.util.SharedPreferencesUtils;
import com.xulan.demo.util.CommandTools.CommandToolsCallback;
import com.xulan.demo.util.RequestUtil.RequestCallback;
import com.xulan.demo.view.CustomProgress;
import com.xulan.demo.view.UpdateAppDialog;
import com.xulan.demo.view.UpdateAppDialog.ResultCallBack;

/** 
 * 登录界面
 * 
 * @author yxx
 *
 * @date 2017-3-27 下午1:57:22
 * 
 */
@SuppressLint("NewApi")
public class LoginActivity extends Activity {

	//	@ViewInject(R.id.login_checkBox_name) CheckBox checkName;
	@ViewInject(R.id.login_checkBox_password)
	CheckBox checkPassword;
	@ViewInject(R.id.login_edt_name)
	EditText tvName;
	@ViewInject(R.id.login_edt_psd)
	EditText tvPsd;
	@ViewInject(R.id.login_tv_version)
	TextView tvVersion;
	private TextView cn_en_switch;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.activity_login);

		//		CommandTools.showDialog(this, CommandTools.getDeviceName());
		ViewUtils.inject(this);
		initData();
		
	}

	@SuppressLint("NewApi")
	private void initData() {
		
		cn_en_switch = (TextView) findViewById(R.id.cn_en_switch);
		tvVersion.setText(getResources().getString(R.string.versionID) + CommandTools.getVersionName());

		final String sta = getResources().getConfiguration().locale.getLanguage();
		if (sta.equals("zh")) {
			cn_en_switch.setBackground(getResources().getDrawable(R.drawable.login_en));
		} else {
			cn_en_switch.setBackground(getResources().getDrawable(R.drawable.login_cn));
		}

		cn_en_switch.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View arg0) {

				shiftLanguage(sta);
			}
		});

		String strName = SharedPreferencesUtils.getParam(LoginActivity.this, Constant.SP_LOGIN_NAME, "").toString();
		String strPwd = SharedPreferencesUtils.getParam(LoginActivity.this, Constant.SP_LOGIN_PWD, "").toString();

		tvName.setText(strName);
		tvPsd.setText(strPwd);

		if (!"".equals(strPwd)) {
			checkPassword.setChecked(true);
		}

		tvVersion.setOnLongClickListener(new OnLongClickListener() {

			@Override
			public boolean onLongClick(View arg0) {

				int pos = (Integer) SharedPreferencesUtils.getParam(MyApplication.getInstance(), Constant.SP_SERVER_URL, 0);

				AlertDialog.Builder mySortAlertDialog = new AlertDialog.Builder(LoginActivity.this);
				mySortAlertDialog.setTitle("服务器切换");
				String[] r = { "ECS", "9510" };
				mySortAlertDialog.setSingleChoiceItems(r, pos, new OnClickListener() {

					@Override
					public void onClick(DialogInterface arg0, int arg1) {
						// TODO Auto-generated method stub

					}
				});

				mySortAlertDialog.setPositiveButton("确认", new DialogInterface.OnClickListener() {
					@Override
					public void onClick(DialogInterface dialog, int which) {

						int pos = ((AlertDialog) dialog).getListView().getCheckedItemPosition();
						if (pos == 0) {
							Constant.URL = Constant.FORMAL_URL;
						} else {
							Constant.URL = Constant.TEST_URL;
						}

						SharedPreferencesUtils.setParam(LoginActivity.this, Constant.SP_SERVER_URL, pos);
						dialog.dismiss();
					}
				});

				mySortAlertDialog.create().show();
				return false;
			}
		});
	}

	public void shiftLanguage(String sta) {
		if (sta.equals("zh")) {
			Locale.setDefault(Locale.ENGLISH);
			Configuration config = getBaseContext().getResources().getConfiguration();
			config.locale = Locale.ENGLISH;
			getBaseContext().getResources().updateConfiguration(config, getBaseContext().getResources().getDisplayMetrics());
			refreshSelf();
			cn_en_switch.setBackground(getResources().getDrawable(R.drawable.login_cn));
		} else {
			Locale.setDefault(Locale.CHINESE);
			Configuration config = getBaseContext().getResources().getConfiguration();
			config.locale = Locale.CHINESE;
			getBaseContext().getResources().updateConfiguration(config, getBaseContext().getResources().getDisplayMetrics());
			refreshSelf();
			cn_en_switch.setBackground(getResources().getDrawable(R.drawable.login_en));
		}
	}

	// refresh self
	public void refreshSelf() {
		Intent intent = new Intent(this, LoginActivity.class);
		intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
		startActivity(intent);
	}

	//	/**
	//	 * 设置密码的隐藏和显示
	//	 * 
	//	 * @param v
	//	 */
	//	public void checkPassword(View v) {
	//		CheckBox cb = (CheckBox) findViewById(R.id.login_switchBtn);
	//		boolean flag = cb.isChecked();
	//
	//		if (flag == true) {
	//			tvPsd.setTransformationMethod(HideReturnsTransformationMethod.getInstance());
	//		} else {
	//			tvPsd.setTransformationMethod(PasswordTransformationMethod.getInstance());
	//		}
	//
	//		tvPsd.setSelection(tvPsd.getText().length());
	//	}

	/**
	 * 登录
	 * @param v
	 */
	public void login(View v) {
		
		Intent intent = new Intent(LoginActivity.this, LuYouActivity.class);
		intent.putExtra("platform_id", MyApplication.m_platform_id);
		startActivity(intent);
//		finish();

		if ("".equals(tvName.getText().toString()) || "".equals(tvPsd.getText().toString())) {
			CommandTools.showToast(getResources().getString(R.string.u_p_not_null));
			return;
		}

		JSONObject jsonObject = new JSONObject();
		try {
			jsonObject.put("username", tvName.getText().toString());
			jsonObject.put("userpwd", tvPsd.getText().toString());
		} catch (JSONException e) {
			e.printStackTrace();
		}

		CustomProgress.showDialog(LoginActivity.this, getResources().getString(R.string.loging), true, null);
		RequestUtil.postDataIfToken(LoginActivity.this, "user/login", jsonObject, false, new RequestCallback() {

			@Override
			public void callback(int res, String remark, JSONObject jsonObject) {
				CustomProgress.dissDialog();
				CommandTools.showToast(remark);
				if (res == 0) {
					MyApplication.getInstance().token = jsonObject.optString("token");

					JSONArray jsonArray = jsonObject.optJSONArray("data");
					jsonObject = jsonArray.optJSONObject(0);
					Boolean enabled = jsonObject.optBoolean("Is_PDA_Enabled");
					if (enabled) {
						MyApplication.m_userID = jsonObject.optString("id");
						MyApplication.m_userName = jsonObject.optString("Username");
						MyApplication.m_platform_id = jsonObject.optString("platform_id");

						SharedPreferencesUtils.setParam(LoginActivity.this, Constant.SP_LOGIN_NAME, tvName.getText().toString());
						if (checkPassword.isChecked()) {
							SharedPreferencesUtils.setParam(LoginActivity.this, Constant.SP_LOGIN_PWD, tvPsd.getText().toString());
						} else {
							SharedPreferences sp = getSharedPreferences(SharedPreferencesUtils.FILE_NAME, Context.MODE_PRIVATE);
							Editor edit = sp.edit();
							edit.remove(Constant.SP_LOGIN_PWD);
							edit.commit();
						}

						//正式库的检测版本更新
						if(Constant.URL.equals(Constant.FORMAL_URL)){
							checkUpdateAPP();
						}else{
							Intent intent = new Intent(LoginActivity.this, LuYouActivity.class);
							intent.putExtra("platform_id", MyApplication.m_platform_id);
							startActivity(intent);
						}

					} else {
						Toast.makeText(LoginActivity.this, Res.getString(R.string.PDA_User_is_not_Activate), 1).show();
					}

				} else {
					CommandTools.showToast(remark);
				}
			}
		});
	}

	public void setting(View v) {
		Intent intent = new Intent(LoginActivity.this, SettingsActivity.class);
		startActivity(intent);
	}

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if (keyCode == KeyEvent.KEYCODE_BACK) { // 获取 back键

			new AlertDialog.Builder(this).setTitle(getResources().getString(R.string.prompt)).setMessage(getResources().getString(R.string.backAll))
			.setPositiveButton(getResources().getString(R.string.yes), new DialogInterface.OnClickListener() {
				@Override
				public void onClick(DialogInterface dialog, int which) {
					MyApplication.finishAllActivities();
					finish();
				}
			}).setNegativeButton(getResources().getString(R.string.no), null).show();
		}
		return false;
	}

	//---------------------------------------------------------------------
	private String remark;//更新说明
	private int mClientVersion;// 客户端版本号
	private int mServerVersion;// 服务器端版本号

	/**
	 * 程序更新检查
	 */
	private void checkUpdateAPP() {
		
		mClientVersion = CommandTools.getVersionCode();
		OnPostExecuteListener listener = new OnPostExecuteListener() {
			@Override
			public void onPostExecute(AsyncNetTask t, NetTaskResult result, Object tag) {
				CustomProgress.dissDialog();
				if (result.m_nResultCode == 0) {
					LoadTextResult mresult = (LoadTextResult) result;
					try {
						JSONObject jsonObj = new JSONObject(mresult.m_strContent);
						Logs.i("result", jsonObj.toString() + "---");
						String vercode = jsonObj.optString("vercode");
						if (TextUtils.isEmpty(vercode)) {
							vercode = "0";
						}
						mServerVersion = Integer.parseInt(vercode);//服务器上版本号
						String strName = jsonObj.optString("vername");//服务器上版本名称
						final String downloadUrl = jsonObj.optString("url");
						final String beforce = jsonObj.optString("beforce") + "";//是否强制更新（0：可选 1：强制）
						remark = jsonObj.optString("remark");
						if (mClientVersion < mServerVersion) {
							UpdateAppDialog.showDialog(LoginActivity.this, beforce, "更新检测", "发现新版本号 " + strName + "，确定更新吗?", remark, new ResultCallBack() {
								@Override
								public void callback(boolean flag) {
									if (flag == true) {
										HttpUtils.download(LoginActivity.this, downloadUrl, mHandler);
									} else {
										Intent intent = new Intent(LoginActivity.this, LuYouActivity.class);
										intent.putExtra("platform_id", MyApplication.m_platform_id);
										startActivity(intent);
										return;
										//										//如果是强制更新，点击后退出程序
										//										if (beforce.equals("1")) {
										//											UpdateAppDialog.dissDialog();
										//											MyApplication.getInstance().finishAllActivities();
										//										}
									}
								}
							});
						} else {
							Intent intent = new Intent(LoginActivity.this, LuYouActivity.class);
							intent.putExtra("platform_id", MyApplication.m_platform_id);
							startActivity(intent);
							return;
						}
					} catch (JSONException e) {
						e.printStackTrace();
					}
				} else {
					// result.m_nResultCode + ""
					UpdateAppDialog.showCnfmDialog(LoginActivity.this, "更新失败:", result.m_strResultDesc, null);
				}
			}
		};

		CustomProgress.showDialog(LoginActivity.this, "正在检查程序更新", true, null);
		String strUrl = Constant.UPDATEURL;
		CommonService.getAppVersion(listener, strUrl, null);
	}

	public Handler mHandler = new Handler() {
		public void handleMessage(Message msg) {
			if (msg.what == 0x0001) {
				UpdateAppDialog.showDialog(LoginActivity.this, "1", "正在更新程序", "正在更新程序", "", null);
			} else if (msg.what == 0x0002) {
				CustomProgress.dissDialog();
			} else if (msg.what == 0x11) {
				Bundle bundle = msg.getData();
				int totalSize = bundle.getInt("totalSize");
				int curSize = bundle.getInt("curSize");
				if (curSize >= totalSize) {
					UpdateAppDialog.dissDialog();
					MyApplication.getInstance().finishAllActivities();
					return;
				}
				UpdateAppDialog.updateProgress(totalSize, curSize);
			}
		}
	};
}
