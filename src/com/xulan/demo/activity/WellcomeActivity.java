package com.xulan.demo.activity;

import java.util.Timer;
import java.util.TimerTask;


import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.KeyEvent;
import android.widget.Toast;

import com.xulan.demo.LoginActivity;
import com.xulan.demo.R;
import com.xulan.demo.net.HttpUtils;


public class WellcomeActivity extends Activity {
	public static String TAG = "WellcomeActivity";

	// -----------------------------版本更新Handler用标识------------------------------------
	public static final int MESSAGE_FIND_NEWVERSION = 1;
	public static final int MESSAGE_SHOW_PROGRESS = 2;
	public static final int MESSAGE_HIDE_PROGRESS = 3;
	public static final int MESSAGE_JUMP = 4;
	public static final int ERROR_MESSAGE_JUMP = 5;
	public int clientVersion = 1;// 客户端版本号
	public int serverVersion = 1;// 服务端版本号
	private ProgressDialog dialog;// 进度条

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_splash);
		// setRingTong();//设置系统默认铃声
		//		new Thread(new ApkUpdateTask()).start();

	}

	public Handler handler = new Handler() {
		@Override
		public void dispatchMessage(Message msg) {
			switch (msg.what) {
			case MESSAGE_FIND_NEWVERSION:
				// 弹对话框,提示版本更新:
				alertUpdateDialog();
				break;
			// 显示进度条
			case MESSAGE_SHOW_PROGRESS:
				alertProgressDialog();
				break;
			// 隐藏进度条
			case MESSAGE_HIDE_PROGRESS:
				dialog.cancel();
				break;
			case MESSAGE_JUMP:
				// 跳转下个Activity
				Intent intent3 = new Intent(WellcomeActivity.this, LoginActivity.class);
				finish();//
				startActivity(intent3);
				break;
			case ERROR_MESSAGE_JUMP:
				String errorMsg = "网络或服务器异常，获取最新版本信息失败!";
				Toast.makeText(WellcomeActivity.this, errorMsg, Toast.LENGTH_SHORT).show();
				Intent intent4 = new Intent(WellcomeActivity.this, LoginActivity.class);
				finish();// 结束当前Acitivity，不能返回到这里
				startActivity(intent4);
				break;
			default:
				break;
			}
		}
	};

	// 服务端apk与客户端apk版本号比对
	/**
	 * 通知版本更新任务:
	 */
	private class ApkUpdateTask implements Runnable {
		@Override
		public void run() {
			try {

				// 下载服务器端apk文件
				HttpUtils.download(WellcomeActivity.this, handler);

				// handler.sendEmptyMessage(MESSAGE_FIND_NEWVERSION);
			} catch (Exception e) {
				e.printStackTrace();
				handler.sendEmptyMessage(ERROR_MESSAGE_JUMP);
				//				StringWriter sw = new StringWriter();
				//				e.printStackTrace(new PrintWriter(sw, true));
				//				String errorLog = sw.toString();
				//				ErrorLogUtils.writeErrorLog(TAG + ":" + errorLog);
			}
		}
	}

	/**
	 * 弹窗提示(版本更新)
	 */
	public void alertUpdateDialog() {
		// 弹对话框:
		AlertDialog.Builder builder = new AlertDialog.Builder(WellcomeActivity.this);
		builder.setTitle("版本更新");
		builder.setCancelable(false);
		// builder.setCancelable(false);
		builder.setMessage("版本更新了,是否更新？");
		builder.setPositiveButton("确定", new DialogInterface.OnClickListener() {
			@Override
			public void onClick(DialogInterface arg0, int arg1) {
				// 下载服务器端apk文件
				HttpUtils.download(WellcomeActivity.this, handler);
				Log.d("Appupdate", "测试：确认下载最新版本");
			}
		});

		builder.show();
	}

	/**
	 * 弹出下载进度条
	 */
	private void alertProgressDialog() {
		dialog = new ProgressDialog(this);
		dialog.setTitle("下载进度");
		dialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
		// 设置可取消：
		dialog.setCancelable(true);
		dialog.setMax(HttpUtils.length);

		dialog.show();
		// 不断的更新进度
		// 定时器，不停地隔一段时间执行
		// TimerTask要执行的任务
		// delay 第一次执行，是在200毫秒之后
		// period 每隔多少时间执行一次，200毫秒执行一次
		final Timer timer = new Timer();
		timer.schedule(new TimerTask() {
			@Override
			public void run() {
				// 不断的更新进度:
				dialog.setProgress(HttpUtils.downloaded);
				// 最大值=当前进度
				if (dialog.getMax() == dialog.getProgress()) {
					handler.sendEmptyMessage(MESSAGE_HIDE_PROGRESS);
					// 下载完成，定时器销毁
					timer.cancel();
				}
			}
		}, 0, 100);
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
	}

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		switch (keyCode) {
		// case KeyEvent.KEYCODE_BACK:
		// return true;
		case KeyEvent.KEYCODE_SEARCH:
			return true;
		}
		return super.onKeyDown(keyCode, event);
	}

}