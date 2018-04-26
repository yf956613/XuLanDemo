package com.xulan.demo.activity;

import java.util.ArrayList;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.Toast;

import com.lidroid.xutils.ViewUtils;
import com.lidroid.xutils.view.annotation.ViewInject;
import com.xulan.demo.MyApplication;
import com.xulan.demo.R;
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
import com.xulan.demo.view.CustomProgress;

/** 
 * 陆运--装货卸货
 * 
 * @author yxx
 *
 * @date 2016-12-20 下午1:20:23
 * 
 */
public class TaskStateActivity extends BaseActivity {
	@ViewInject(R.id.task_state_listview) ListView mListView;

	private CommonAdapter<TaskState> commonAdapter;
	private List<TaskState> dataList = new ArrayList<TaskState>();//列表里的数据

	@Override
	protected void onBaseCreate(Bundle savedInstanceState) {
		setContentViewId(R.layout.activity_task_state, this);
		ViewUtils.inject(this);

	}
	
	@Override
	public void initData() {
		String action = getIntent().getStringExtra("action");
		setTitle(action);
		
		requestGetTaskState();
	}

	@Override
	public void initView() {
		mListView.setAdapter(commonAdapter = new CommonAdapter<TaskState>(mContext, dataList, R.layout.task_state_item) {

			@Override
			public void convert(ViewHolder helper, TaskState item) {

				EditText text = helper.getView(R.id.task_state_task_name);
				text.setText(item.getTaskName());
				
				ListView listview = helper.getView(R.id.item_listview);
				List<ScanData> list = item.getList();
				CommonAdapter<ScanData> adapter = new CommonAdapter<ScanData>(mContext, list, R.layout.task_state_item_item) {
					
					@Override
					public void convert(ViewHolder helper, ScanData item) {
						helper.setText(R.id.mode_type, item.getOperationLink());
						helper.setText(R.id.load_count, item.getPlanCount());
						helper.setText(R.id.scan_count, item.getPracticalCount());
						helper.setText(R.id.exception_number, item.getException_number());
						helper.setText(R.id.create_time, item.getCreateTime());
					}
				};
				
				listview.setAdapter(adapter);
				setListViewHeightBasedOnChildren(listview);
			}
		});
	}
	
	/**
	 * 重新计算ListView的高度
	 * @param listView
	 */
	public void setListViewHeightBasedOnChildren(ListView listView) {
	   ListAdapter listAdapter = listView.getAdapter();
	   if (listAdapter == null) {
	      return;
	   }

	   int totalHeight = 0;
	   for (int i = 0; i < listAdapter.getCount(); i++) {
	      View listItem = listAdapter.getView(i, null, listView);
	      listItem.measure(0, 0);
	      totalHeight += listItem.getMeasuredHeight();
	   }

	   ViewGroup.LayoutParams params = listView.getLayoutParams();
	   params.height = totalHeight+ (listView.getDividerHeight() * (listAdapter.getCount() - 1));
	   listView.setLayoutParams(params);
	}

	/**
	 * 获取海运信息
	 */
	protected LoadTextNetTask requestGetTaskState() {

		OnPostExecuteListener listener = new OnPostExecuteListener() {
			@Override
			public void onPostExecute(AsyncNetTask t, NetTaskResult result, Object tag) {

				CustomProgress.dissDialog();
				if (result.m_nResultCode == 0) {
					LoadTextResult mresult = (LoadTextResult) result;
					try {
						JSONObject jsonObj = new JSONObject(mresult.m_strContent);

						int success = jsonObj.getInt("success");
						if (success == 0) {
							JSONArray jsonArray = jsonObj.optJSONArray("data");
							try {
								for (int i = 0; i < jsonArray.length(); i++) {
									JSONObject jsonObject = jsonArray.getJSONObject(i);
									String task_name = jsonObject.optString("Task_Name");
									
									JSONArray ja = jsonObject.getJSONArray("link_data");
									
									List<ScanData> list = new ArrayList<ScanData>();
									for (int j = 0; j < ja.length(); j++) {
										JSONObject jo = ja.getJSONObject(j);
										String upload_time = jo.optString("Upload_Time");
										String scan_number = jo.optString("Scan_Number");
										String load_number = jo.optString("Load_Number");
										String exception_number = jo.optString("Exception_Number");
										String link_caption = jo.optString("Link_Caption");
										
										ScanData data = new ScanData();
										data.setOperationLink(link_caption);
										data.setPlanCount(load_number);
										data.setException_number(exception_number);
										data.setPracticalCount(scan_number);
										data.setCreateTime(upload_time);
										list.add(data);
									}
									
									TaskState state = new TaskState();
									state.setTaskName(task_name);
									state.setList(list);
	
									dataList.add(state);
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
						Toast.makeText(TaskStateActivity.this, "解析错误", 1).show();
						e.printStackTrace();
					}
				} else {
				}
			}
		};

		CustomProgress.showDialog(TaskStateActivity.this, getResources().getString(R.string.searching), false, null);
		MyApplication.getInstance();
		LoadTextNetTask task = UserService.getTaskState(listener, null);
		return task;
	}

	class TaskState {
		private String taskName;
		private List<ScanData> list;
		
		public String getTaskName() {
			return taskName;
		}
		public void setTaskName(String taskName) {
			this.taskName = taskName;
		}
		public List<ScanData> getList() {
			return list;
		}
		public void setList(List<ScanData> list) {
			this.list = list;
		}
	}
}
