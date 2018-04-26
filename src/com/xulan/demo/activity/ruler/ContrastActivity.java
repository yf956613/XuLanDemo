package com.xulan.demo.activity.ruler;

import java.util.ArrayList;
import java.util.List;

import org.json.JSONException;
import org.json.JSONObject;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

import com.lidroid.xutils.ViewUtils;
import com.xulan.demo.MyApplication;
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
import com.xulan.demo.util.Logs;
import com.xulan.demo.view.CustomProgress;

/** 
 * 打尺
 * 
 * @author hexiuhui
 *
 */
public class ContrastActivity extends BaseActivity {

	private EditText scale_edt_package_number;
	private EditText scale_edt_package_code;

	private String taskId = "";
	private ListView mListView;
	private CommonAdapter<Contrast> commonAdapter;
	private List<ScanData> dataList = new ArrayList<ScanData>();
	private List<Contrast> contrastList = new ArrayList<ContrastActivity.Contrast>();

	private String [] contrasts = {"长(mm)", "宽(mm)", "高(mm)", "体积(m³)", "毛重(kg)", "计费吨(fw)"};
	Contrast contrast;

	private ScanData scanData;

	@Override
	protected void onBaseCreate(Bundle savedInstanceState) {
		setContentViewId(R.layout.activity_contrast_scan, this);
		ViewUtils.inject(this);

		String barcode = getIntent().getStringExtra("barcode");

		requestGetShip(barcode);
	}

	@Override
	public void initView() {
		//添加新数据到集合
		final ScanData data = (ScanData) getIntent().getSerializableExtra("data");
		String s[] = {data.getLength(), data.getWidth(), data.getHeight(), data.getV3(), data.getWeight(), data.getCharge_Ton()};
		for (int i = 0; i < contrasts.length; i++) {
			contrast = new Contrast();
			contrast.setNewContrast(s[i]);
			contrastList.add(contrast);
		}

		mListView = (ListView) findViewById(R.id.lv_public);
		scale_edt_package_number = (EditText) findViewById(R.id.scale_edt_package_number);
		scale_edt_package_code = (EditText) findViewById(R.id.scale_edt_package_code);
		scale_edt_package_number.setText(data.getPackNumber());
		scale_edt_package_code.setText(data.getPackBarcode());

		mListView.setAdapter(commonAdapter = new CommonAdapter<Contrast>(mContext, contrastList, R.layout.contrast_item) {

			@Override
			public void convert(ViewHolder helper, Contrast item) {

				helper.setText(R.id.contrast_tv1, contrasts[commonAdapter.getPosition()]);
				helper.setText(R.id.contrast_tv2, item.getContrast());
				helper.setText(R.id.contrast_tv3, item.getNewContrast());

				double errData = 0;
				try{
					errData = Double.parseDouble(item.getNewContrast()) - Double.parseDouble(item.getContrast());
				}catch(Exception e){
					e.printStackTrace();
				}

				helper.setText(R.id.contrast_tv4, String.format("%.2f", errData) + "");
			}
		});
	}

	@Override
	public void initData() {
		setTitle(getIntent().getStringExtra("actionName"));
		setRightTitle(getResources().getString(R.string.ok));

	}

	class Contrast {
		private String contrast;
		private String newContrast;
		private String cy;

		public String getContrast() {
			return contrast;
		}

		public void setContrast(String contrast) {
			this.contrast = contrast;
		}

		public String getNewContrast() {
			return newContrast;
		}

		public void setNewContrast(String newContrast) {
			this.newContrast = newContrast;
		}

		public String getCy() {
			return cy;
		}

		public void setCy(String cy) {
			this.cy = cy;
		}
	}

	/**
	 * 获取海运信息
	 */
	protected LoadTextNetTask requestGetShip(final String barcode) {

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

							JSONObject jsonObject = jsonObj.getJSONObject("data");
							String Length = jsonObject.optString("Length");
							String Width = jsonObject.optString("Width");
							String Height = jsonObject.optString("Height");
							String V3 = jsonObject.optString("V3");
							String Gross_Weight = jsonObject.optString("Gross_Weight");
							String Charge_Ton = jsonObject.optString("Charge_Ton");

							scanData = new ScanData();
							scanData.setCacheId(CommandTools.getUUID());
							scanData.setLength(Length);
							scanData.setWidth(Width);
							scanData.setHeight(Height);
							scanData.setWeight(Gross_Weight);
							scanData.setV3(V3);
							scanData.setCharge_Ton(Charge_Ton);

//							//计算体积
//							int lenghti = Integer.parseInt(Length);
//							int widthi = Integer.parseInt(Width);
//							int heighti = Integer.parseInt(Height);
//							int value = lenghti / 1000 * widthi / 1000 * heighti / 1000;
//
//							//计算计费吨
//							Double parseDouble = Double.parseDouble(Gross_Weight);
//							Double charge;
//							if (parseDouble > value) {
//								charge = parseDouble;
//							} else {
//								charge = Double.parseDouble(value + "");
//							}

							String s [] = {Length, Width, Height, V3 + "", Gross_Weight, Charge_Ton + ""};

							//							dataList.add(scanData);

							for (int i = 0; i < contrastList.size(); i++) {
								Contrast contrast2 = contrastList.get(i);
								contrast2.setContrast(s[i]);
							}

							commonAdapter.notifyDataSetChanged();

						} else {
							String message = jsonObj.getString("message");

							CommandTools.showToast(message);
						}

					} catch (JSONException e) {
						Toast.makeText(ContrastActivity.this, "解析错误", 1).show();
						e.printStackTrace();
					}
				} else {
				}
			}
		};

		CustomProgress.showDialog(ContrastActivity.this, getResources().getString(R.string.searching), false, null);
		MyApplication.getInstance();
		LoadTextNetTask task = UserService.getolddata(barcode, listener, null);
		return task;
	}

	/**
	 * 完成
	 * @param v
	 */
	public void clickRight(View v){
		Intent intent = new Intent();
		intent.putExtra("Length_Old", scanData.getLength());
		intent.putExtra("Width_Old", scanData.getWidth());
		intent.putExtra("Height_Old", scanData.getHeight());
		setResult(RESULT_OK, intent);
		finish();
	}
}
