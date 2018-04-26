package com.xulan.demo.view;

import java.util.List;
import com.xulan.demo.R;
import com.xulan.demo.adapter.CommonAdapter;
import com.xulan.demo.adapter.ViewHolder;
import com.xulan.demo.data.NoteInfo;
import com.xulan.demo.data.ProjectInfo;
import com.xulan.demo.data.RouteInfo;

import android.app.Dialog;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;
import android.widget.TextView;

/**
 * 带回调的单选框对话框
 * 
 * @author yxx
 * 
 *         2015-12-3
 */
public class SingleItemDialog {

	public static boolean isShow = false;
	public static Dialog builder;

	public SingleItemDialog() {

	}

	// 弹窗结果回调函数
	public static abstract class SingleItemCallBack {
		public abstract void callback(int pos);
	}

	/**
	 * @param context
	 * @param strTitle
	 * @param flag
	 *            点击外部是否可以关闭对话框
	 * @param list
	 * @param resultCallBack
	 */
	public static void showDialog(final Context context, String strTitle, boolean flag, List<String> list,
			final SingleItemCallBack resultCallBack) {

		if (builder != null) {
			builder.dismiss();
		}

		View view = LayoutInflater.from(context).inflate(R.layout.dialog_item, null);

		TextView tvTitle = (TextView) view.findViewById(R.id.tv_dialog_item);
		ListView listView = (ListView) view.findViewById(R.id.listView_dialog);

		tvTitle.setText(strTitle);

		try {

			CommonAdapter<String> commonAdapter = new CommonAdapter<String>(context, list, R.layout.layout_single_item) {
				@Override
				public void convert(ViewHolder helper, String item) {
					helper.setText(R.id.tv_dialog_single_item, item);
				}
			};
			listView.setAdapter(commonAdapter);

			listView.setOnItemClickListener(new OnItemClickListener() {
				@Override
				public void onItemClick(AdapterView<?> arg0, View arg1, int arg2, long arg3) {
					resultCallBack.callback(arg2);
					builder.dismiss();
				}
			});

			builder = new Dialog(context, R.style.dialogSupply);
			builder.setContentView(view);

			if (flag == true) {
				builder.setCanceledOnTouchOutside(true);
			}
			builder.show();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	//选择项目
	public static void showProJectDialog(final Context context, String strTitle, boolean flag, List<ProjectInfo> list,
			final SingleItemCallBack resultCallBack) {

		if (builder != null) {
			builder.dismiss();
		}

		View view = LayoutInflater.from(context).inflate(R.layout.dialog_item, null);

		TextView tvTitle = (TextView) view.findViewById(R.id.tv_dialog_item);
		ListView listView = (ListView) view.findViewById(R.id.listView_dialog);

		tvTitle.setText(strTitle);

		try {

			CommonAdapter<ProjectInfo> commonAdapter = new CommonAdapter<ProjectInfo>(context, list, R.layout.layout_single_item) {
				@Override
				public void convert(ViewHolder helper, ProjectInfo item) {
					helper.setText(R.id.tv_dialog_single_item, item.getProject_name());
				}
			};
			listView.setAdapter(commonAdapter);

			listView.setOnItemClickListener(new OnItemClickListener() {
				@Override
				public void onItemClick(AdapterView<?> arg0, View arg1, int arg2, long arg3) {
					resultCallBack.callback(arg2);
					builder.dismiss();
				}
			});

			builder = new Dialog(context, R.style.dialogSupply);
			builder.setContentView(view);

			if (flag == true) {
				builder.setCanceledOnTouchOutside(true);
			}
			builder.show();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	//选择路由
	public static void showRouteDialog(final Context context, String strTitle, boolean flag, List<RouteInfo> list,
			final SingleItemCallBack resultCallBack) {

		if (builder != null) {
			builder.dismiss();
		}

		View view = LayoutInflater.from(context).inflate(R.layout.dialog_item, null);

		TextView tvTitle = (TextView) view.findViewById(R.id.tv_dialog_item);
		ListView listView = (ListView) view.findViewById(R.id.listView_dialog);

		tvTitle.setText(strTitle);

		try {

			CommonAdapter<RouteInfo> commonAdapter = new CommonAdapter<RouteInfo>(context, list, R.layout.layout_single_item) {
				@Override
				public void convert(ViewHolder helper, RouteInfo item) {
					helper.setText(R.id.tv_dialog_single_item, item.getRoute_name());
				}
			};
			listView.setAdapter(commonAdapter);

			listView.setOnItemClickListener(new OnItemClickListener() {
				@Override
				public void onItemClick(AdapterView<?> arg0, View arg1, int arg2, long arg3) {
					resultCallBack.callback(arg2);
					builder.dismiss();
				}
			});

			builder = new Dialog(context, R.style.dialogSupply);
			builder.setContentView(view);

			if (flag == true) {
				builder.setCanceledOnTouchOutside(true);
			}
			builder.show();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	//选择节点
	public static void showNoteDialog(final Context context, String strTitle, boolean flag, List<NoteInfo> list,
			final SingleItemCallBack resultCallBack) {

		if (builder != null) {
			builder.dismiss();
		}

		View view = LayoutInflater.from(context).inflate(R.layout.dialog_item, null);

		TextView tvTitle = (TextView) view.findViewById(R.id.tv_dialog_item);
		ListView listView = (ListView) view.findViewById(R.id.listView_dialog);

		tvTitle.setText(strTitle);

		try {

			CommonAdapter<NoteInfo> commonAdapter = new CommonAdapter<NoteInfo>(context, list, R.layout.layout_single_item) {
				@Override
				public void convert(ViewHolder helper, NoteInfo item) {
					helper.setText(R.id.tv_dialog_single_item, item.getNote_name());
				}
			};
			listView.setAdapter(commonAdapter);

			listView.setOnItemClickListener(new OnItemClickListener() {
				@Override
				public void onItemClick(AdapterView<?> arg0, View arg1, int arg2, long arg3) {
					resultCallBack.callback(arg2);
					builder.dismiss();
				}
			});

			builder = new Dialog(context, R.style.dialogSupply);
			builder.setContentView(view);

			if (flag == true) {
				builder.setCanceledOnTouchOutside(true);
			}
			builder.show();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

}
