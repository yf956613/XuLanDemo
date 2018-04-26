package com.xulan.demo.activity.action.load;

import java.util.ArrayList;
import java.util.List;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;
import com.lidroid.xutils.ViewUtils;
import com.lidroid.xutils.view.annotation.ViewInject;
import com.xulan.demo.R;
import com.xulan.demo.activity.BaseActivity;
import com.xulan.demo.adapter.CommonAdapter;
import com.xulan.demo.adapter.ViewHolder;
import com.xulan.demo.data.ScanData;
import com.xulan.demo.net.AsyncNetTask;
import com.xulan.demo.net.LoadTextNetTask;
import com.xulan.demo.net.LoadTextResult;
import com.xulan.demo.net.NetTaskResult;
import com.xulan.demo.net.AsyncNetTask.OnPostExecuteListener;
import com.xulan.demo.service.UserService;
import com.xulan.demo.util.CommandTools;
import com.xulan.demo.util.Constant;
import com.xulan.demo.util.Logs;
import com.xulan.demo.view.CustomProgress;

/** 
 * 装卸公司选择
 * 
 * @author yxx
 *
 * @date 2017-3-29 下午5:16:53
 * 
 */
public class LoadCompanyActivity extends BaseActivity {

	private ListView mListView;
	private CommonAdapter<ScanData> commonAdapter;
	private List<ScanData> dataList = new ArrayList<ScanData>();

	@ViewInject(R.id.load_company_edt_search) EditText edtSearch;

	@Override
	protected void onBaseCreate(Bundle savedInstanceState) {
		setContentViewId(R.layout.activity_load_company, this);
		ViewUtils.inject(this);
	}

	@Override
	public void initView() {

		mListView = (ListView) findViewById(R.id.lv_public);
		mListView.setAdapter(commonAdapter = new CommonAdapter<ScanData>(mContext, dataList, R.layout.load_company_item) {

			@Override
			public void convert(ViewHolder helper, ScanData item) {

				helper.setText(R.id.load_company_item_tv1, (commonAdapter.getPosition() + 1) + "");
				helper.setText(R.id.load_company_item_tv2, item.getCompany());
				helper.setText(R.id.load_company_item_tv3, item.getTelPerson());
				helper.setText(R.id.load_company_item_tv4, item.getDeiverPhone());
			}
		});

		mListView.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1, int arg2, long arg3) {

				Intent intent = new Intent();
				intent.putExtra("companyName", dataList.get(arg2).getCompany());
				intent.putExtra("company_id", dataList.get(arg2).getCompany_id());
				setResult(RESULT_OK, intent);
				finish();
			}
		});

		edtSearch.addTextChangedListener(new TextWatcher() {

			@Override
			public void onTextChanged(CharSequence arg0, int arg1, int arg2, int arg3) {
				// TODO Auto-generated method stub

			}

			@Override
			public void beforeTextChanged(CharSequence arg0, int arg1, int arg2,
					int arg3) {
				// TODO Auto-generated method stub

			}

			@Override
			public void afterTextChanged(Editable arg0) {
				// TODO Auto-generated method stub

			}
		});

		getCompanyList();
	}

	@Override
	public void initData() {

		if(getIntent().getStringExtra("type").equals(Constant.SCAN_TYPE_PACK)){
			setTitle(getResources().getString(R.string.package_vendor));
		}else if(getIntent().getStringExtra("type").equals(Constant.SCAN_TYPE_LOAD)){
			setTitle(getResources().getString(R.string.vendor));
		}else if(getIntent().getStringExtra("type").equals(Constant.SCAN_TYPE_SCALE)){
			setTitle(getResources().getString(R.string.mesurement_vendor));
		}else if(getIntent().getStringExtra("type").equals(Constant.SCAN_TYPE_VERIFY)){
			setTitle(getResources().getString(R.string.survey_vendor));
		}else if(getIntent().getStringExtra("type").equals(Constant.SCAN_TYPE_STRAP)){
			setTitle(getResources().getString(R.string.lashing_vendor));
		}else{
			setTitle(getResources().getString(R.string.choose_company));
		}
	}

	/**
	 * 获取装卸公司
	 */
	protected LoadTextNetTask getCompanyList() {

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
							JSONArray jsonArray = jsonObj.getJSONArray("data");
							for (int i = 0; i < jsonArray.length(); i++) {
								JSONObject jsonObject = jsonArray.getJSONObject(i);
								String company_id = jsonObject.getString("company_id");
								String company_name = jsonObject.getString("company_name");
								String company_type = jsonObject.getString("company_type");

								String link_man = jsonObject.getString("link_man");
								String link_phone = jsonObject.getString("phone");

								ScanData data = new ScanData();
								data.setCompany(company_name);
								data.setCompany_id(company_id);
								data.setTelPerson(link_man);
								data.setDeiverPhone(link_phone);

								dataList.add(data);
							}

							commonAdapter.notifyDataSetChanged();
						}
					} catch (JSONException e) {
						Toast.makeText(LoadCompanyActivity.this, "解析错误", 1).show();
						e.printStackTrace();
					}
				} else {
				}
			}
		};

		CustomProgress.showDialog(LoadCompanyActivity.this, getResources().getString(R.string.searching), false, null);
		LoadTextNetTask task = UserService.getCompanylist(listener, null);
		return task;
	}

}
