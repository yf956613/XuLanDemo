package com.xulan.demo.pdascan;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.List;
import android.annotation.SuppressLint;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.widget.Toast;

import com.android.rfid245G.SerialPort;
import com.xulan.demo.MyApplication;
import com.xulan.demo.data.RfidInfo;
import com.xulan.demo.util.DataUtilTools;

/** 
 * RFID模块，扫描头的初始化和数据采集
 * 
 * @author yxx
 *
 * @date 2017-7-19 上午9:52:02
 * 
 */
/** 
 * 
 * 
 * @author yxx
 *
 * @date 2017-7-19 上午11:28:18
 * 
 */
public class RFID {

	//RFID
	private static SerialPort mSerialPort;
	private static final int PORT = 13; //SerialPort---0
	protected static OutputStream mOutputStream;
	private static InputStream mInputStream;
	private static ReadThread mReadThread;

	private static boolean runFlag = false;
	public static List<RfidInfo> listTag = new ArrayList<RfidInfo>();;
	public static boolean readFlag = false;

	public static final byte DATA_HEAD = (byte) 0x55;//数据包头

	public RFID(){

	}

	public static void initRFID(){

		try {
			mSerialPort = new SerialPort(PORT, 57600, 0);
			//			mSerialPort = new SerialPort(13, 115200, 0);
		} catch (SecurityException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		if(mSerialPort == null){
			Toast.makeText(MyApplication.getInstance(), "串口初始化失败", Toast.LENGTH_LONG).show();
			return;
		}

		mSerialPort.rfid_poweron();
		try {
			Thread.sleep(150);
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} 
		mOutputStream = mSerialPort.getOutputStream();
		mInputStream = mSerialPort.getInputStream();

		//启动线程
		mReadThread = new ReadThread();
		mReadThread.start();
	}

	/**
	 * 开启RFID识别
	 * 此时集合清空
	 * runFlag置为true
	 */
	public static void startRFID(){

		if(mSerialPort == null){
			return;
		}

		listTag.clear();
		runFlag = true;

		mReadThread = new ReadThread();
		mReadThread.start();
		stopComm();
	}

	/**
	 * 关闭RFID识别
	 * 此时集合清空
	 * runFlag置为false
	 */
	public static void stopRFID(){

		if(mSerialPort == null){
			return;
		}

		runFlag = false;
		stopComm();
	}

	/**
	 * 退出程序，关闭RFID扫描
	 */
	public static void closeRFID(){

		if (mReadThread != null){
			runFlag = false;
		}

		if(mInputStream == null){
			return;
		}

		try {
			mInputStream.close();
			mOutputStream.close();
		} catch (IOException e) {
			e.printStackTrace();
		}

		mSerialPort.rfid_poweroff();
		mSerialPort.close(PORT);
		Log.e("", "close .......");
	}

	/**
	 * 逻辑处理
	 * @param tagId
	 */
	private static void handleData(String tagId){

		boolean flag = checkList(tagId);
		if(!flag){//不存在

			RfidInfo info = new RfidInfo();
			info.setId(tagId);
			info.setPack_barcode("");
			info.setLink_num(-1);
			listTag.add(info);

			Log.v("zd", tagId);

			Message msg = new Message();
			msg.obj = tagId;
			mHandler.sendMessage(msg);
		}

	}

	/**
	 * 检查集合中是否已存在该标签ID
	 * true 存在
	 * 0-每操作，添加
	 * 1-已操作，未保存成功
	 * 2-已操作，保存成功，跳过
	 * @param tagId
	 * @return
	 */
	private static boolean checkList(String tagId){

		int len = listTag.size();
		for(int i=0; i<len; i++){

			RfidInfo info = listTag.get(i);
			Log.v("rfid", "RFID扫描数据: " + info.getId() + "," + info.getPack_barcode() + "," + info.getLink_num());
			if(info.getId().equals(tagId)){
				return true;
			}

		}

		return false;
	}

	public static Handler mHandler = new Handler(){

		public void handleMessage(Message msg){

			String tagId = (String) msg.obj;
			DataUtilTools.getInfoByRFID(tagId);
		}
	};

	/**
	 * 从串口中读取完整的数据,返回数据包长度为21
	 * 因为数据包的包头和包尾为0x55；而数据包中间不出现0x55，
	 * 所以可以根据0x55来获取完整的数据包
	 * @return
	 */
	private static byte[] read(){
		int count;
		byte[] bytes = new byte[21];
		byte[] headFlag = new byte[1];
		byte[] headFlag2 = new byte[1];
		try {
			while(true){
				count = mInputStream.available();
				if(count > 20){
					//找包头
					mInputStream.read(headFlag);
					if(headFlag[0] == 0x55){
						//							Thread.sleep(30);
						mInputStream.read(headFlag2);
						if(headFlag2[0] == 0x55){
							int result = mInputStream.read(bytes, 1, 20);
							if(result != -1){
								bytes[0] = 0x55;
								bytes = readResult(bytes);
								return bytes;
							}
						}else{
							int result = mInputStream.read(bytes, 2, 19);
							if(result != -1){
								bytes[0] = 0x55;
								bytes[1] = headFlag2[0];
								bytes = readResult(bytes);
								return bytes;
							}
						}
					}
				}
			}
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		return bytes;
	}
	private static byte[] readResult(byte[] bytes) throws IOException{
		if (bytes[20]==0x55) {
			return bytes;
		}
		String strbytes = Bytes2HexString(bytes, bytes.length);
		String st1 = strbytes.replaceAll("5656", "55");
		String st2 = st1.replaceAll("5657", "56");

		byte[] endbytes = new byte[21-st2.length()/2];
		mInputStream.read(endbytes);
		byte[] bs = HexString2Bytes(st2+Bytes2HexString(endbytes, endbytes.length));
		return bs;
	}

	//解析返回的数据包
	private static String resolveData(byte[] resp){
		int length = resp.length;
		if(resp[0] != (byte) 0x55 && resp[length - 1] != (byte) 0x55){
			Log.e("Data error", "数据包有误");
			return null;
		}
		byte[] tag = new byte[8];
		System.arraycopy(resp, 9, tag, 0, 8);
		String tagStr = Bytes2HexString(tag, 8);
		return tagStr;
	}

	//Byte 数组转十六进制字符串
	@SuppressLint("DefaultLocale")
	public static String Bytes2HexString(byte[] b, int size) {
		String ret = "";
		for (int i = 0; i < size; i++) {
			String hex = Integer.toHexString(b[i] & 0xFF);
			if (hex.length() == 1) {
				hex = "0" + hex;
			}
			ret += hex.toUpperCase();
		}
		return ret;
	}

	public static byte uniteBytes(byte src0, byte src1) {
		byte _b0 = Byte.decode("0x" + new String(new byte[]{src0})).byteValue();
		_b0 = (byte)(_b0 << 4);
		byte _b1 = Byte.decode("0x" + new String(new byte[]{src1})).byteValue();
		byte ret = (byte)(_b0 ^ _b1);
		return ret;
	}


	//十六进制字符串转Bytes数组
	public static byte[] HexString2Bytes(String src){
		int len = src.length()/2;
		byte[] ret = new byte[len];
		byte[] tmp = src.getBytes();

		for(int i=0; i<len; i++){
			ret[i] = uniteBytes(tmp[i*2], tmp[i*2+1]);
		}
		return ret;
	}

	public static boolean crc(byte[] bytes)
	{
		byte a = 0;
		for (int i = 0; i < bytes.length-1; i++)
		{
			a = (byte) (bytes[i]+a);
		}
		a = (byte) (a&0xff);
		if (a == 0)
		{
			return true;
		}else {
			return false;
		}
	}
	public static long BytesToInt(byte[] bytes) {
		long sum = 0;
		long pow = 0;
		for (int i = 0; i < bytes.length; i++) {
			pow = (long) Math.pow(256,bytes.length-i-1);
			long c = bytes[i];
			if (c<0) {
				c = 256+c;
			}
			sum = sum + (long)c*pow;
		}

		return sum;
	}	

	private static void stopComm(){

		if(mOutputStream == null || mInputStream == null){
			return;
		}

		String stopComm = "550000A5065455";

		try {
			mOutputStream.write(HexString2Bytes(stopComm));
			mOutputStream.flush();
			mInputStream.read(new byte[4096]);
			//			mInputStream.
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	//读数据线程
	static class ReadThread extends Thread {
		@Override
		public void run() {
			super.run();
			while(runFlag) {
				byte[] resp = read();
				if(resp != null && resp[resp.length-1] == 0x55&&!crc(resp)){
					System.out.println(Bytes2HexString(resp, resp.length));
					String tag = resolveData(resp);
					if(tag != null&&tag.length()>9){
						//									updateUI(tag);
						String tagEnd = BytesToInt(HexString2Bytes(tag.substring(8)))+"";
						String tagEnd2 = tagEnd;
						for (int i = 0; i < 10-tagEnd.length(); i++) {
							tagEnd2 = "0"+tagEnd2;
						}
						tag = tag.substring(0,8)+ tagEnd2;
						handleData(tag);
					}
				}else {
					if (resp!=null) {
						Log.e("TAG", Bytes2HexString(resp, resp.length));
					}
				}
			}
		}
	}
}
