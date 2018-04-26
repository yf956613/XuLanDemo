package com.xulan.demo.pdascan;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.util.Timer;
import java.util.TimerTask;

import cn.pda.serialport.SerialPort;
import android.R.integer;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;

public class ScanThread extends Thread {

	private SerialPort mSerialPort ;
	private InputStream is ;
	private OutputStream os ;
	/*serialport parameter*/
	public static  int Port = 0;
	public static int Power = 2;
	public static int BaudRate = 9600;
	private int flags = 0;

	private Handler handler ;


	public static int SCAN = 1001;  //messege recv mode

	/**
	 * if throw exception, serialport initialize fail.
	 * @throws SecurityException
	 * @throws IOException
	 */
	public ScanThread(Handler handler) throws SecurityException, IOException{
		this.handler = handler;
		mSerialPort = new SerialPort(Port, BaudRate, flags);

		is = mSerialPort.getInputStream();
		os = mSerialPort.getOutputStream();
		//		mSerialPort.scaner_trigoff();
		switch (Power) {
		case SerialPort.Power_Scaner:
			mSerialPort.scaner_poweron();
			break;
		case SerialPort.Power_3v3:
			mSerialPort.power3v3on();	
			break;
		case SerialPort.Power_5v:
			mSerialPort.power_5Von();
			break;
		case SerialPort.Power_Psam:
			mSerialPort.psam_poweron();
			break;
		case SerialPort.Power_Rfid:
			mSerialPort.rfid_poweron();
			break;
		}
		mSerialPort.scaner_trigoff() ;
		//		mSerialPort.rfid_poweron();
		//		mSerialPort.psam_poweron() ;
		//		mSerialPort.zigbeepoweron() ;
		//		mSerialPort.p
		//		try {
		//			Thread.sleep(500);
		//		} catch (InterruptedException e) {
		//			// TODO Auto-generated catch block
		//			e.printStackTrace();
		//		}
		////		mSerialPort.rfidPoweron();
		//		mSerialPort.scaner_trigon();
		//		
		//
		//		
		//		try {
		//			Thread.sleep(500);
		//		} catch (InterruptedException e) {
		//			// TODO Auto-generated catch block
		//			e.printStackTrace();
		//		}
		//		/** clear useless data **/
		//		byte[] temp = new byte[128];
		//		is.read(temp);

	}

	@Override
	public void run() {
		try {
			int size = 0;
			byte[] buffer = new byte[4096];
			int available = 0;
			while(!isInterrupted()){
				available = is.available();
				if(available > 0){
					try {
						Thread.sleep(40);
					} catch (InterruptedException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
					size = is.read(buffer);
					if(size > 0){
						//						Log.e(""+size, Tools.Byt es2HexString(buffer, size));
						sendMessege(buffer, size, SCAN);
						if(mtimer != null){
							mtimer.cancel();
						}
					}
				}
			}
		} catch (IOException e) {
			//���ش�����Ϣ
			e.printStackTrace();
		}
		super.run();
	}


	private final byte START = 0x02 ;
	private final byte END = 0x03 ;
	private void sendMessege(byte[] data, int dataLen, int mode){
		//		if(data[0] == START && data[dataLen-1] == END){
		//			String dataStr = new String(data,0, dataLen );
		String dataStr = "";
		//			dataStr = Tools.Bytes2HexString(data, dataLen);
		try {
			dataStr = new String(data, 0 , dataLen, "GBK");
		} catch (UnsupportedEncodingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		Log.e("zd", dataStr);
		Bundle bundle = new Bundle();
		bundle.putString("data", dataStr);
		Message msg = new Message();
		msg.what = mode;
		msg.setData(bundle);
		handler.sendMessage(msg);
		//		}

	}

	Timer mtimer = null;

	public void scan(){
		if(mtimer != null){
			mtimer.cancel();
		}
		mtimer = new Timer();
		mtimer.schedule(new TimerTask() {

			@Override
			public void run() {
				mSerialPort.scaner_trigoff();

			}
		}, 5000);
		if(mSerialPort.scaner_trig_stat() == true){
			mSerialPort.scaner_trigoff();
			try {
				Thread.sleep(50);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		mSerialPort.scaner_trigon();
	}

	public void close(){
		if(mSerialPort != null){
			switch (Power) {
			case SerialPort.Power_Scaner:
				mSerialPort.scaner_poweroff();
				break;
			case SerialPort.Power_3v3:
				mSerialPort.power3v3off();	
				break;
			case SerialPort.Power_5v:
				mSerialPort.power_5Voff();
				break;
			case SerialPort.Power_Psam:
				mSerialPort.psam_poweroff();
				break;
			case SerialPort.Power_Rfid:
				mSerialPort.rfid_poweroff();
				break;
			}
			try {
				if(is != null){
					is.close();
				}
				if(os != null){
					os.close();
				}
			} catch (IOException e) {
				e.printStackTrace();
			}
			mSerialPort.close(Port);
		}
	}

}
