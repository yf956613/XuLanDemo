package com.xulan.demo.activity;

import org.json.JSONException;
import org.json.JSONObject;

import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.lidroid.xutils.ViewUtils;
import com.xulan.demo.R;
import com.xulan.demo.net.AsyncNetTask;
import com.xulan.demo.net.LoadTextNetTask;
import com.xulan.demo.net.LoadTextResult;
import com.xulan.demo.net.NetTaskResult;
import com.xulan.demo.net.AsyncNetTask.OnPostExecuteListener;
import com.xulan.demo.service.UserService;
import com.xulan.demo.util.CommandTools;
import com.xulan.demo.util.Logs;
import com.xulan.demo.view.CustomProgress;

/** 
 * 修改 密码
 * 
 * @author hexiuhui
 *
 * @date 2016-12-12 下午4:33:13
 * 
 */
public class UpdatePassWordActivity extends BaseActivity {

	private LoadTextNetTask mTaskRequestUpdate;
	
	private EditText mUserName;
	private EditText mPassword1;
	private EditText mPassword2;

	@Override
	protected void onBaseCreate(Bundle savedInstanceState) {
		setContentViewId(R.layout.activity_update_password, this);
		ViewUtils.inject(this);
	}

	@Override
	public void initView() {
		mUserName = (EditText) findViewById(R.id.user_name_edt);
		mPassword1 = (EditText) findViewById(R.id.password_1);
		mPassword2 = (EditText) findViewById(R.id.password_2);
	}

	@Override
	public void initData() {
		setTitle(getResources().getString(R.string.update_password));
	}

	/**
	 * 修改密码
	 */
	protected LoadTextNetTask requestUpdatePassword(String userName, String password1, String password2) {
		OnPostExecuteListener listener = new OnPostExecuteListener() {
			@Override
			public void onPostExecute(AsyncNetTask t, NetTaskResult result, Object tag) {
				mTaskRequestUpdate = null;
				CustomProgress.dissDialog();
				if (result.m_nResultCode == 0) {
					LoadTextResult mresult = (LoadTextResult) result;
					try {
						JSONObject jsonObj = new JSONObject(mresult.m_strContent);

						Logs.i("hexiuhui---", jsonObj.toString());

						int success = jsonObj.getInt("success");
						if (success == 0) {
							CommandTools.showToast("修改成功");
							finish();
						} else {
							String message = jsonObj.getString("message");

							CommandTools.showToast(message);
						}
					} catch (JSONException e) {
						Toast.makeText(UpdatePassWordActivity.this, "解析错误", 1).show();
						e.printStackTrace();
					}
				} else {
				}
			}
		};

		CustomProgress.showDialog(UpdatePassWordActivity.this, getResources().getString(R.string.searching), false, null);
		LoadTextNetTask task = UserService.updatePassword(userName, password1, password2, listener, null);
		return task;
	}
	
	public void confirm(View v) {
		String userName = mUserName.getText().toString();
		String pwd1 = mPassword1.getText().toString();
		String pwd2 = mPassword2.getText().toString();
		if (userName.equals("") || pwd1.equals("") || pwd2.equals("")) {
			CommandTools.showToast("用户名或密码不能为空");
			return;
		}
		
		mTaskRequestUpdate = requestUpdatePassword(userName, pwd1, pwd2);
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
		if (mTaskRequestUpdate != null) {
			mTaskRequestUpdate.cancel(true);
			mTaskRequestUpdate = null;
		}
	}
}
