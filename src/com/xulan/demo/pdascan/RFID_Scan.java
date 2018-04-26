package com.xulan.demo.pdascan;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.KeyEvent;
import android.widget.Toast;
import cn.pda.serialport.SerialPort;

import com.xulan.demo.MyApplication;
import com.xulan.demo.util.CommandTools;
import com.xulan.demo.util.Constant;

public class RFID_Scan {

	private static ScanThread scanThread;
	private static KeyReceiver receiver;

	private static int count = 1;//初始值RFID是关闭状态
	public RFID_Scan(){

		Context context = MyApplication.getInstance();

		receiver = new KeyReceiver();
		IntentFilter intentFilter = new IntentFilter();
		intentFilter.addAction("android.rfid.FUN_KEY");
		context.registerReceiver(receiver, intentFilter);
	}

	public static void initRFIDScanner(Context context){

		ScanThread.BaudRate = SerialPort.baudrate9600;
		ScanThread.Port = SerialPort.com0;
		ScanThread.Power = SerialPort.Power_Scaner;

		try {
			scanThread = new ScanThread(mHandler);
		} catch (Exception e) {
			Toast.makeText(MyApplication.getInstance(), "serialport init fail", 0).show();
			return;
		}

		scanThread.start();
	}

	public static void closeRFIDScan(){

		if (scanThread != null) {
			scanThread.interrupt();
			scanThread.close();
		}
	}

	private static Handler mHandler = new Handler() {

		public void handleMessage(android.os.Message msg) {

			if (msg.what == ScanThread.SCAN) {
				String data = msg.getData().getString("data").trim().toString();

				Log.v("zd", "RFID机器扫描结果: " + data + "/" + data.length());

				Message message = new Message();
				message.what = Constant.SCAN_DATA;
				message.obj = data;
				MyApplication.getEventBus().post(message);
			}else if(msg.what == 0x0011){
				CommandTools.showToast((String) msg.obj);
			}else if(msg.what == 0x1001){
//				CommandTools.showToast("关闭RFID，开启扫描");
				CommandTools.showDialog(CommandTools.getGlobalActivity(), "已关闭RFID");
			}else if(msg.what == 0x1002){
				CommandTools.showDialog(CommandTools.getGlobalActivity(), "已开启RFID");
//				CommandTools.showToast("关闭扫描，开启RFID");
			}
		};
	};

	static class KeyReceiver extends BroadcastReceiver {

		@Override
		public void onReceive(Context context, Intent intent) {
			int keyCode = intent.getIntExtra("keyCode", 0) ;
			boolean keyDown = intent.getBooleanExtra("keydown", false) ;

			if(scanThread == null){
				return;
			}

			if(keyDown){
				switch (keyCode) {
				case KeyEvent.KEYCODE_F1:
				
					if(count % 2 == 0){
						RFID.closeRFID();
						mHandler.sendEmptyMessage(0x1001);
						Log.v("zd", "关闭RFID");
					}else{
						RFID.initRFID();
						RFID.startRFID();
						mHandler.sendEmptyMessage(0x1002);
						Log.v("zd", "开启RFID");
					}
					count++;
					break;
				case KeyEvent.KEYCODE_F2:
					scanThread.scan();
					break;
				case KeyEvent.KEYCODE_F3:

					scanThread.scan();
					break;
				case KeyEvent.KEYCODE_F5:
					scanThread.scan();
					break;
				case KeyEvent.KEYCODE_F4:
					scanThread.scan();
					break;
				}
			}
		}
	}

}
