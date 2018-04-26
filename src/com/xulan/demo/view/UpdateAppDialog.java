package com.xulan.demo.view;

import com.xulan.demo.R;

import android.app.Dialog;
import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

public class UpdateAppDialog {

	public static boolean isShow = false;
	public static Dialog builder;

	public UpdateAppDialog() {

	}

	public static abstract class ResultCallBack {
		public abstract void callback(boolean flag);
	}

	public static abstract class CallBack {
		public abstract void callback(boolean flag);
	}

	public static void updateProgress(int total, int curr) {

		if (mContext == null) {
			return;
		}

		layout1.setVisibility(View.GONE);
		layout2.setVisibility(View.VISIBLE);

		Log.v("updateAPK", curr + "," + total);

		tvProgress1.setText(curr * 100 / total + "%");
		tvProgress2.setText(curr + "/" + total);

		mpProgressBar.setMax(total);
		mpProgressBar.setProgress(curr);
	}

	static Context mContext;
	static LinearLayout layout1;
	static LinearLayout layout2;
	static ProgressBar mpProgressBar;
	static View view;
	static TextView tvContent;
	static TextView tvTitle;
	static Button btnOk;
	static Button btnCancel;
	static TextView tvProgress1;
	static TextView tvProgress2;
	private static TextView tvType;

	/**
	 * @param context
	 * @param beforce 是否强制更新
	 * @param type
	 * @param title
	 * @param content
	 * @param resultCallBack
	 */
	public static void showDialog(final Context context, String beforce, String type, String title, String content, final ResultCallBack resultCallBack) {

		mContext = context;
		if (builder != null) {
			try {
				builder.dismiss();
			} catch (Exception e) {
				builder=null;
			}
		}

		view = LayoutInflater.from(context).inflate(R.layout.dialog_update_app, null);

		layout1 = (LinearLayout) view.findViewById(R.id.layout_update1);
		layout2 = (LinearLayout) view.findViewById(R.id.layout_update2);

		mpProgressBar = (ProgressBar) view.findViewById(R.id.progressBar1);

		tvTitle = (TextView) view.findViewById(R.id.tv_update_title);
		tvContent = (TextView) view.findViewById(R.id.tv_update_content);

		btnOk = (Button) view.findViewById(R.id.btn_update_ok);
		btnCancel = (Button) view.findViewById(R.id.btn_update_cancel);
		tvType = (TextView) view.findViewById(R.id.tv_dialog_item);

		tvProgress1 = (TextView) view.findViewById(R.id.tv_update1);
		tvProgress2 = (TextView) view.findViewById(R.id.tv_update2);

		//强制更新
		if(beforce.equals("1")){
			btnCancel.setText("关闭");
		}else{
			btnCancel.setText("取消");
		}
		
//		content = "1）简化签收流程11月18号.\n 2)简化签收流程11月19号.\n 3)简化签收流程11月20.";
		Log.v("zd", content);
		content = content.replaceAll("##", "\n");
		
		tvType.setText(type);
		tvTitle.setText(title+"");
		tvContent.setText(content);

		btnOk.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View arg0) {
				resultCallBack.callback(true);
				builder.dismiss();
				builder=null;
			}
		});

		btnCancel.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View arg0) {
				resultCallBack.callback(false);
				builder.dismiss();
				builder=null;
			}
		});

		builder = new Dialog(context, R.style.dialogSupply);
		builder.setContentView(view);
		builder.setCancelable(false);
		builder.setCanceledOnTouchOutside(false);
		builder.show();
	}

	/*
	 * 只有一个确认按钮的更新提示:
	 */
	public static void showCnfmDialog(final Context context, String strTitle,
			String remark, final ResultCallBack resultCallBack) {
		mContext = context;
		if (builder != null) {
			try {
				builder.dismiss();
			} catch (Exception e) {
				builder=null;
			}
		}

		view = LayoutInflater.from(context).inflate(R.layout.dialog_update_app_cnfm, null);

		layout1 = (LinearLayout) view.findViewById(R.id.layout_update1);
		layout2 = (LinearLayout) view.findViewById(R.id.layout_update2);

		mpProgressBar = (ProgressBar) view.findViewById(R.id.progressBar1);

		tvContent = (TextView) view.findViewById(R.id.tv_update_msg);
		tvType = (TextView) view.findViewById(R.id.tv_dialog_item);
		btnOk = (Button) view.findViewById(R.id.btn_update_ok);

		tvProgress1 = (TextView) view.findViewById(R.id.tv_update1);
		tvProgress2 = (TextView) view.findViewById(R.id.tv_update2);

		tvType.setText(strTitle);
//		tvTitle.setText(strTitle);
		tvContent.setText(remark);


		btnOk.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View arg0) {
				resultCallBack.callback(true);
				builder.dismiss();
				builder=null;
			}
		});

		//		btnCancel.setOnClickListener(new OnClickListener() {
		//			@Override
		//			public void onClick(View arg0) {
		//				resultCallBack.callback(false);
		//				builder.dismiss();
		//			}
		//		});

		builder = new Dialog(context, R.style.dialogSupply);
		builder.setContentView(view);
		builder.setCancelable(false);
		builder.setCanceledOnTouchOutside(false);
		builder.show();
	}
	/**
	 * 关闭窗口
	 */
	public static void dissDialog(){

		if(builder != null){
			builder.dismiss();
			builder=null;
		}
	}
}
