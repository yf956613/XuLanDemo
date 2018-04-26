package com.xulan.demo.activity.action;

import java.util.ArrayList;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.content.Intent;
import android.os.Bundle;
import android.os.Message;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.lidroid.xutils.ViewUtils;
import com.lidroid.xutils.view.annotation.ViewInject;
import com.xulan.demo.MyApplication;
import com.xulan.demo.R;
import com.xulan.demo.activity.BaseActivity;
import com.xulan.demo.activity.action.land.LandArriveActivity;
import com.xulan.demo.activity.air.ArriveActivity;
import com.xulan.demo.activity.trains.TrainsArriveActivity;
import com.xulan.demo.adapter.CommonAdapter;
import com.xulan.demo.adapter.ViewHolder;
import com.xulan.demo.data.TaskInfo;
import com.xulan.demo.net.AsyncNetTask;
import com.xulan.demo.net.LoadTextNetTask;
import com.xulan.demo.net.LoadTextResult;
import com.xulan.demo.net.NetTaskResult;
import com.xulan.demo.net.AsyncNetTask.OnPostExecuteListener;
import com.xulan.demo.service.UserService;
import com.xulan.demo.shipping.ArrivalBySeaActivity;
import com.xulan.demo.util.CommandTools;
import com.xulan.demo.util.Constant;
import com.xulan.demo.util.Logs;
import com.xulan.demo.view.CustomProgress;

/**
 * @author hexiuhui
 */
public class TaskListActivity extends BaseActivity {

	private LoadTextNetTask mTaskGetTaskList;
	private String type;
	private int linkNum;
	private int isArriver;

	private ListView mListView;
	private CommonAdapter<TaskInfo> commonAdapter;
	private List<TaskInfo> dataList = new ArrayList<TaskInfo>();

	@ViewInject(R.id.task_edt_search) EditText edtSearch;

	@ViewInject(R.id.task_list_item_tv_car_plate) TextView tvCarPlate;
	@ViewInject(R.id.task_list_item_tv_car_number) TextView tvCarNumber;
	@ViewInject(R.id.task_list_item_tv_shipping_space) TextView tvShippingSpace;

	@ViewInject(R.id.task_list_item_layout_car_plate) LinearLayout layoutCarPlate;
	@ViewInject(R.id.task_list_item_layout_car_number) LinearLayout layoutCarNumber;
	@ViewInject(R.id.task_list_item_layout_shipping_space) LinearLayout layoutShippingSpace;

	@Override
	protected void onBaseCreate(Bundle savedInstanceState) {
		setContentViewId(R.layout.activity_task_list, this);
		ViewUtils.inject(this);

		type = getIntent().getStringExtra("type");
		linkNum = getIntent().getIntExtra("link_no", 1);
		isArriver = getIntent().getIntExtra("isArriver", 0);

		layoutShippingSpace.setVisibility(View.GONE);
		if (type.equals(Constant.SCAN_TYPE_LAND)) {
			tvCarPlate.setText(getResources().getString(R.string.tractor_no));
			tvCarNumber.setText(getResources().getString(R.string.shift));
		} else if (type.equals(Constant.SCAN_TYPE_SEA)) {
			tvCarPlate.setText(getResources().getString(R.string.vessel_name));
			tvCarNumber.setText(getResources().getString(R.string.voyage));
			tvShippingSpace.setText(getResources().getString(R.string.holds));
			layoutShippingSpace.setVisibility(View.VISIBLE);
		}else if(type.equals(Constant.SCAN_TYPE_AIR)){
			tvCarPlate.setText(getResources().getString(R.string.flight));
			layoutCarNumber.setVisibility(View.GONE);
		}else if(type.equals(Constant.SCAN_TYPE_LOAD)){
			tvCarPlate.setText(getResources().getString(R.string.vendor));
			layoutCarNumber.setVisibility(View.GONE);
		}else if (type.equals(Constant.SCAN_TYPE_CONTAINER)) {
			tvCarPlate.setText(getResources().getString(R.string.cntr_no));
			tvCarNumber.setText(getResources().getString(R.string.vessel_name));
			tvShippingSpace.setText(getResources().getString(R.string.voyage));
			layoutShippingSpace.setVisibility(View.VISIBLE);
		}else if(type.equals(Constant.SCAN_TYPE_RAILEAY)){
			tvCarPlate.setText(getResources().getString(R.string.carriage_no));
			layoutCarNumber.setVisibility(View.GONE);
		}else if(type.equals(Constant.SCAN_TYPE_STORAGE)){
			tvCarPlate.setText(getResources().getString(R.string.field_no));
			layoutCarNumber.setVisibility(View.GONE);
		}else if(type.equals(Constant.SCAN_TYPE_STRAP)){
			tvCarPlate.setText(getResources().getString(R.string.lashing_vendor));
			layoutCarNumber.setVisibility(View.GONE);
		}else if(type.equals(Constant.SCAN_TYPE_PACK)){
			tvCarPlate.setText(getResources().getString(R.string.package_vendor));
			layoutCarNumber.setVisibility(View.GONE);
		}else if(type.equals(Constant.SCAN_TYPE_OFFLINE)){
			tvCarPlate.setText(getResources().getString(R.string.monitor_2));
			layoutCarNumber.setVisibility(View.GONE);
		}else if(type.equals(Constant.SCAN_TYPE_INSTALL)){
			tvCarPlate.setText("安装员");
			layoutCarNumber.setVisibility(View.GONE);
		} else if (type.equals(Constant.SCAN_TYPE_SCALE)) {
			tvCarPlate.setText(getResources().getString(R.string.mesurement_vendor));
			layoutCarPlate.setVisibility(View.VISIBLE);
			layoutCarNumber.setVisibility(View.GONE);
		} else if (type.equals(Constant.SCAN_TYPE_VERIFY)) {
			tvCarPlate.setText(getResources().getString(R.string.survey_vendor));
			layoutCarPlate.setVisibility(View.VISIBLE);
			layoutCarNumber.setVisibility(View.GONE);
		}
	}

	@Override
	public void initView() {

		mListView = (ListView) findViewById(R.id.lv_public);
		mListView.setAdapter(commonAdapter = new CommonAdapter<TaskInfo>(mContext, dataList, R.layout.task_list_item) {

			@Override
			public void convert(ViewHolder helper, TaskInfo item) {

				helper.setText(R.id.task_list_item_tv1, commonAdapter.getIndex());
				helper.setText(R.id.task_list_item_tv2, item.getTask_name());

				helper.setText(R.id.task_list_item_tv_car_plate, item.getCar_plate());
				helper.setText(R.id.task_list_item_tv_car_number, item.getCar_count());

				helper.setText(R.id.task_list_item_tv_shipping_space, item.getShipping_space());

				helper.setText(R.id.task_list_item_tv_link_man, item.getPerson());
				helper.setText(R.id.task_list_item_tv_phone, item.getTelPerson());

				helper.hideView(R.id.task_list_item_layout_shipping_space, true);
				if(type.equals(Constant.SCAN_TYPE_AIR)){

					helper.hideView(R.id.task_list_item_layout_car_number, true);
					helper.hideView(R.id.task_list_item_layout_shipping_space, true);
				}else if(type.equals(Constant.SCAN_TYPE_SEA)){

					helper.hideView(R.id.task_list_item_layout_shipping_space, false);
				}else if(type.equals(Constant.SCAN_TYPE_CONTAINER)){

					helper.hideView(R.id.task_list_item_layout_shipping_space, false);
				}else if(type.equals(Constant.SCAN_TYPE_LOAD)){

					helper.hideView(R.id.task_list_item_layout_car_number, true);
				}else if(type.equals(Constant.SCAN_TYPE_RAILEAY)){

					helper.hideView(R.id.task_list_item_layout_car_number, true);
				}else if(type.equals(Constant.SCAN_TYPE_STORAGE)){

					helper.hideView(R.id.task_list_item_layout_car_number, true);
				}else if(type.equals(Constant.SCAN_TYPE_STRAP)){

					helper.hideView(R.id.task_list_item_layout_car_number, true);
				}else if(type.equals(Constant.SCAN_TYPE_OFFLINE)){

					helper.hideView(R.id.task_list_item_layout_car_number, true);
				}else if(type.equals(Constant.SCAN_TYPE_PACK)){

					helper.hideView(R.id.task_list_item_layout_car_number, true);
				} else if (type.equals(Constant.SCAN_TYPE_SCALE)) {
					helper.hideView(R.id.task_list_item_layout_car_number, true);
				} else if (type.equals(Constant.SCAN_TYPE_VERIFY)) {
					helper.hideView(R.id.task_list_item_layout_car_number, true);
				} else if (type.equals(Constant.SCAN_TYPE_INSTALL)) {
					helper.hideView(R.id.task_list_item_layout_car_number, true);
				}
			}
		});

		mListView.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1, int arg2, long arg3) {
				if (isArriver == 1) {
					Class activity = null;

					if (type.equals(Constant.SCAN_TYPE_LAND)) {
						activity = LandArriveActivity.class;
					} else if (type.equals(Constant.SCAN_TYPE_SEA)) {
						activity = ArrivalBySeaActivity.class;
					} else if (type.equals(Constant.SCAN_TYPE_AIR)) {
						activity = ArriveActivity.class;
					}else if (type.equals(Constant.SCAN_TYPE_RAILEAY)) {
						activity = TrainsArriveActivity.class;
					}

					Intent taskIntent = new Intent(TaskListActivity.this, activity);
					taskIntent.putExtra("taskName", dataList.get(arg2).getTask_name());
					taskIntent.putExtra("taskCode", dataList.get(arg2).getTask_code());
					taskIntent.putExtra("car_plate", dataList.get(arg2).getCar_plate());
					taskIntent.putExtra("car_count", dataList.get(arg2).getCar_count());
					taskIntent.putExtra("shipping_space", dataList.get(arg2).getShipping_space());
					
					taskIntent.putExtra("link_man", dataList.get(arg2).getPerson());
					taskIntent.putExtra("link_phone", dataList.get(arg2).getTelPerson());
					
					startActivity(taskIntent);
				} else {
					Intent intent = new Intent();
					intent.putExtra("taskName", dataList.get(arg2).getTask_name());
					intent.putExtra("taskCode", dataList.get(arg2).getTask_code());
					intent.putExtra("car_plate", dataList.get(arg2).getCar_plate());
					intent.putExtra("car_count", dataList.get(arg2).getCar_count());
					intent.putExtra("shipping_space", dataList.get(arg2).getShipping_space());
					
					intent.putExtra("link_man", dataList.get(arg2).getPerson());
					intent.putExtra("link_phone", dataList.get(arg2).getTelPerson());

					setResult(RESULT_OK, intent);
				}

				finish();
			}
		});

		edtSearch.addTextChangedListener(new TextWatcher() {

			@Override
			public void onTextChanged(CharSequence arg0, int arg1, int arg2, int arg3) {
				// TODO Auto-generated method stub

			}

			@Override
			public void beforeTextChanged(CharSequence arg0, int arg1, int arg2, int arg3) {
				// TODO Auto-generated method stub

			}

			@Override
			public void afterTextChanged(Editable arg0) {
				// TODO Auto-generated method stub

			}
		});

		mTaskGetTaskList = requestGetTaskList(MyApplication.m_userID, MyApplication.m_nodeId, 1, type);
	}

	@Override
	public void initData() {
		setTitle(getResources().getString(R.string.task_list));
		hidenRightMenu();
	}

	/* (non-Javadoc)
	 * @see com.xulan.client.activity.BaseActivity#onEventMainThread(android.os.Message)
	 */
	public void onEventMainThread(Message msg) {

		if(msg.what == Constant.SCAN_DATA){

//			String strBillcode = (String) msg.obj;
		}
	}

	/**
	 * 获取任务列表
	 */
	protected LoadTextNetTask requestGetTaskList(String userId, String nodeID, int nodeNum, String type) {
		
		OnPostExecuteListener listener = new OnPostExecuteListener() {
			@Override
			public void onPostExecute(AsyncNetTask t, NetTaskResult result, Object tag) {
				mTaskGetTaskList = null;
				CustomProgress.dissDialog();
				if (result.m_nResultCode == 0) {
					LoadTextResult mresult = (LoadTextResult) result;
					try {
						JSONObject jsonObj = new JSONObject(mresult.m_strContent);
						Logs.i("hexiuhui---", jsonObj.toString());
						int success = jsonObj.getInt("success");
						if (success == 0) {
							JSONArray jsonArray = jsonObj.optJSONArray("data");
							try {

								for (int j = 0; j < jsonArray.length(); j++) {
									JSONObject jo = jsonArray.getJSONObject(j);
									String linkman = jo.optString("Linkmane");
									String task_name = jo.optString("Task_Name");
									String id = jo.getString("ID");

									String license_plate = jo.optString("car_plate");
									String number = jo.optString("car_number");
									String shipping_space = jo.optString("shipping_space");

									TaskInfo info = new TaskInfo();
									info.setCar_plate(license_plate);
									info.setCar_count(number);
									info.setTask_name(task_name);
									info.setTask_code(id);
									info.setShipping_space(shipping_space + "");

									info.setPerson(jo.optString("link_man"));
									info.setTelPerson(jo.optString("phone"));

									dataList.add(info);
								}

							} catch (JSONException e) {
								e.printStackTrace();
							}
							commonAdapter.notifyDataSetChanged();

						} else {
							String message = jsonObj.getString("message");

							CommandTools.showToast(message);
						}
					} catch (JSONException e) {
						Toast.makeText(TaskListActivity.this, "解析错误", 1).show();
						e.printStackTrace();
					}
				} else {
				}
			}
		};

		CustomProgress.showDialog(TaskListActivity.this, getResources().getString(R.string.searching), false, null);
		LoadTextNetTask task = UserService.getTaskList(userId, nodeID, nodeNum, type, listener, null);
		return task;
	}
}
