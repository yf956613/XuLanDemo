package com.xulan.demo;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import android.app.Activity;
import android.app.AlarmManager;
import android.app.Application;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.device.ScanManager;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import com.amap.api.location.AMapLocation;
import com.amap.api.location.AMapLocationClient;
import com.amap.api.location.AMapLocationClientOption;
import com.amap.api.location.AMapLocationClientOption.AMapLocationMode;
import com.amap.api.location.AMapLocationListener;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.Volley;
import com.taobao.sophix.PatchStatus;
import com.taobao.sophix.SophixManager;
import com.taobao.sophix.listener.PatchLoadStatusListener;
import com.xulan.demo.data.ScanInfo;
import com.xulan.demo.data.UserInfo;
import com.xulan.demo.db.DBHelper;
import com.xulan.demo.net.NetSettings;
import com.xulan.demo.pdascan.RFID;
import com.xulan.demo.pdascan.RFID_Scan;
import com.xulan.demo.pdascan.ScanThread;
import com.xulan.demo.util.CommandTools;
import com.xulan.demo.util.Constant;
import com.xulan.demo.util.FileUtils;
import com.xulan.demo.util.Logs;
import com.xulan.demo.util.RequestManager;
import com.xulan.demo.util.SharedPreferencesUtils;
import com.xulan.demo.util.VoiceHint;
import com.xulan.demo.util.CommandTools.CommandToolsCallback;

import de.greenrobot.event.EventBus;

/**
 * 
 * 
 * @author yxx
 * 
 * @date 2017-3-28 上午9:40:42
 * 
 */
public class MyApplication extends Application {

	public static MyApplication instance;

	public NetSettings m_netSettings = new NetSettings();

	public static String m_userID;
	public static String m_userName;
	public static String m_platform_id;//平台ID
	public static String m_projectId = "";
	public static String m_routeId = "";
	public static String m_nodeId = "";
	public static String m_scan_type = "";
	public static String m_points_type="";//节点类型(陆运、海运等)

	public List<Activity> activityList = new LinkedList<Activity>();

	private static EventBus eventBus;
	public static RequestQueue mRequestQueue;

	public static int m_flag;
	public static int m_node_num = 0;//当前是否第一 节点，1，是，其它不是
	public static int m_link_num = 0;//当前操作环节
	public static int m_physic_link_num = 1;//操作环节，物理上的，这个是根据接口返回的

	public String sendTime = "35916884898255966"; // 加密time
	public String token = "";//token

	public static UserInfo m_userInfo = new UserInfo();

	private int scanCount = 0;

	private ScanDataReceiver scanDataReceiver;

	private ScanManager mScanManager;
	private final static String SCAN_ACTION = "urovo.rcv.message";//扫描结束action

	public void onCreate() {
		super.onCreate();
		instance = this;
		
		initHotfix();
		new DBHelper(this);
		DBHelper.SQLiteDBHelper.getWritableDatabase();
	
		VoiceHint.initSound(this);
		initCurrAddress();

		initURL();
		RequestManager.initVolley(this);
		mRequestQueue = Volley.newRequestQueue(this);
		mRequestQueue.start();

		if (eventBus == null) {
			eventBus = EventBus.getDefault();
			eventBus.register(this);
		}

		// 删除SD卡中不是今天的文件夹及里面的照片
		if (FileUtils.existSDCard()) {
			new FileUtils().delFolder(Constant.SDPATH);
		}

		initPDAScan_Urovo();
		initPDAScanner_C6000();

		//RFID，设备名字比较怪，居然是Android
		if(CommandTools.getDeviceName().equals("Android")){
			new RFID_Scan();
			RFID_Scan.initRFIDScanner(MyApplication.getInstance());
			//			RFID.initRFID();
			//			RFID.startRFID();
		}
	}

	/**
	 * 初始化服务器
	 */
	public void initURL(){

		int pos = (Integer) SharedPreferencesUtils.getParam(MyApplication.getInstance(), Constant.SP_SERVER_URL, 0);
		if(pos == 0){
			Constant.URL = Constant.FORMAL_URL;
		}else{
			Constant.URL = Constant.TEST_URL;
		}
	}

	/**
	 * 初始化热修复相关信息
	 */
	private void initHotfix() {

//		int serverPos = (Integer) SharedPreferencesUtils.getParam(MyApplication.getInstance(), Constant.SP_SERVER_URL, 0);
//		if(serverPos == 0){//0-正式库 1-测试库
//			return;
//		}

		String appVersion;
		try {
			appVersion = this.getPackageManager().getPackageInfo(this.getPackageName(), 0).versionName;
		} catch (Exception e) {
			appVersion = "1.0.0";
		}

		SophixManager.getInstance().setContext(this)
		.setAppVersion(appVersion)
		.setAesKey(null)
		//.setAesKey("0123456789123456")
		.setEnableDebug(true)
		.setPatchLoadStatusStub(new PatchLoadStatusListener() {
			@Override
			public void onLoad(final int mode, final int code, final String info, final int handlePatchVersion) {

				String msg = "";

				// 补丁加载回调通知
				if (code == PatchStatus.CODE_LOAD_SUCCESS) {
					// 表明补丁加载成功

					msg =  "onLoad: 成功";
				} else if (code == PatchStatus.CODE_LOAD_RELAUNCH) {
					// 表明新补丁生效需要重启. 开发者可提示用户或者强制重启;
					// 建议: 用户可以监听进入后台事件, 然后应用自杀
					msg = "onLoad: 生效需要重启";
					
					Message message = new Message();
					message.what = 0x0012;
					message.obj = msg;
					mHandler.sendMessage(message);
				} else if (code == PatchStatus.CODE_LOAD_FAIL) {
					// 内部引擎异常, 推荐此时清空本地补丁, 防止失败补丁重复加载
					SophixManager.getInstance().cleanPatches();
				} else {
					// 其它错误信息, 查看PatchStatus类说明
					msg = "onLoad: 其它错误信息" + code + "/" + msg;
				}
				
			}
		}).initialize();

		SophixManager.getInstance().queryAndLoadNewPatch();
	}

	/**
	 * 设备初始化
	 * 如果不是该对应的扫描头设备，初始化会异常
	 */
	public void initPDAScan_Urovo(){

		//设备判断，否则初始化会崩溃
		if(!CommandTools.getDeviceName().equals("SQ51")){
			return;
		}

		try{

			mScanManager = new ScanManager();
			mScanManager.openScanner();

			mScanManager.switchOutputMode( 0);

			IntentFilter filter = new IntentFilter();
			filter.addAction(SCAN_ACTION);
			registerReceiver(mScanReceiver, filter);
		}catch(Exception e){
			e.printStackTrace();
		}

	}

	/**
	 * 获取单例
	 * 
	 * @return
	 */
	public static MyApplication getInstance() {
		
		return instance;
	}

	public static RequestQueue getQueue() {

		if (mRequestQueue == null) {
			mRequestQueue = Volley.newRequestQueue(instance);
		}

		return mRequestQueue;
	}

	/**
	 * 添加Activity到容器中
	 * 
	 * @param activity
	 */
	public void addActivity(Activity activity) {

		if (!MyApplication.getInstance().activityList.contains(activity)) {
			MyApplication.getInstance().activityList.add(activity);
		}
	}

	/**
	 * 释放所有的Activity
	 */
	public static void finishAllActivities() {

		RFID.closeRFID();

		for (Activity activity : MyApplication.getInstance().activityList) {
			if (activity != null) {
				activity.finish();
			}
		}

		MyApplication.getInstance().activityList.clear();
		System.exit(0);
	}

	/**
	 * 回收activity
	 */
	public static void clearActivityList() {

		for (Activity activity : MyApplication.getInstance().activityList) {
			if (activity != null) {
				activity.finish();
			}
		}
		MyApplication.getInstance().activityList.clear();
	}

	public static EventBus getEventBus() {

		if (eventBus == null) {
			eventBus.register(MyApplication.getInstance());
		}

		return eventBus;
	}

	/**
	 * 高德地图初始化位置，获取经纬度
	 */
	private void initCurrAddress() {

		AMapLocationClient mLocationClient = new AMapLocationClient(
				getApplicationContext());
		// 设置定位回调监听
		mLocationClient.setLocationListener(mLocationListener);

		// 初始化定位参数
		AMapLocationClientOption mLocationOption = new AMapLocationClientOption();
		// 设置定位模式为高精度模式，Battery_Saving为低功耗模式，Device_Sensors是仅设备模式
		mLocationOption.setLocationMode(AMapLocationMode.Hight_Accuracy);
		// 设置是否返回地址信息（默认返回地址信息）
		mLocationOption.setNeedAddress(true);
		// 设置是否只定位一次,默认为false
		mLocationOption.setOnceLocation(true);
		// 设置是否强制刷新WIFI，默认为强制刷新
		mLocationOption.setWifiActiveScan(true);
		// 设置是否允许模拟位置,默认为false，不允许模拟位置
		mLocationOption.setMockEnable(false);
		// 设置定位间隔,单位毫秒
		mLocationOption.setInterval(1000 * 60 * 30);//默认30分钟定位一次
		// 给定位客户端对象设置定位参数
		mLocationClient.setLocationOption(mLocationOption);
		// 启动定位
		mLocationClient.startLocation();
	}

	// 声明定位回调监听器
	public AMapLocationListener mLocationListener = new AMapLocationListener() {

		@Override
		public void onLocationChanged(AMapLocation amapLocation) {

			if (amapLocation != null) {

				if (amapLocation.getErrorCode() == 0) {

					// 定位成功回调信息，设置相关消息
					amapLocation.getLocationType();// 获取当前定位结果来源，如网络定位结果，详见定位类型表
					amapLocation.getLatitude();// 获取纬度
					amapLocation.getLongitude();// 获取经度
					amapLocation.getAccuracy();// 获取精度信息
					SimpleDateFormat df = new SimpleDateFormat(
							"yyyy-MM-dd HH:mm:ss");
					Date date = new Date(amapLocation.getTime());
					df.format(date);// 定位时间
					amapLocation.getAddress();// 地址，如果option中设置isNeedAddress为false，则没有此结果，网络定位结果中会有地址信息，GPS定位不返回地址信息。
					amapLocation.getCountry();// 国家信息
					amapLocation.getProvince();// 省信息
					amapLocation.getCity();// 城市信息
					amapLocation.getDistrict();// 城区信息
					amapLocation.getStreet();// 街道信息
					amapLocation.getStreetNum();// 街道门牌号信息
					amapLocation.getCityCode();// 城市编码
					amapLocation.getAdCode();// 地区编码

					Constant.latitude = amapLocation.getLatitude();
					Constant.longitude = amapLocation.getLongitude();

					String strAddress = amapLocation.getCity() + ":"
							+ amapLocation.getLongitude() + "/"
							+ amapLocation.getLatitude();
					Logs.v("zd", strAddress);
				} else {
					// 显示错误信息ErrCode是错误码，errInfo是错误信息，详见错误码表。
					Logs.v("zd", amapLocation.getErrorInfo());
					//					CommandTools.showToast("定位失败: " + amapLocation.getErrorInfo());
				}
			}

		}
	};


	/**
	 * C6000 注册扫描广播
	 */
	public void initPDAScanner_C6000() {

		//设备判断，否则初始化会崩溃
		if(!CommandTools.getDeviceName().equals("C6000")){
			return;
		}

		IntentFilter scanDataIntentFilter = new IntentFilter();
		// scanDataIntentFilter.addAction("com.android.scancontext"); //前台输出
		scanDataIntentFilter.addAction("com.android.scanservice.scancontext"); // 后台输出
		scanDataIntentFilter.setPriority(500);

		scanDataReceiver = new ScanDataReceiver();
		registerReceiver(scanDataReceiver, scanDataIntentFilter);
	}

	private class ScanDataReceiver extends BroadcastReceiver {

		@Override
		public void onReceive(Context context, Intent intent) {
			String strBarcode = null;
			String action = intent.getAction();

//			VoiceHint.playRightSounds();
			if (action.equals("com.android.scanservice.scancontext")) {
				String str = intent.getStringExtra("Scan_context");
				//
				Logs.v("zd", "后台广播输出: " + str);
				strBarcode = str;
				strBarcode = strBarcode.trim();

				final Message msg = new Message();
				msg.what = Constant.SCAN_DATA;
				msg.obj = strBarcode;
				MyApplication.getEventBus().post(msg);
				
//				ScanInfo scanInfo = new ScanInfo();
//				scanInfo.setType(MyApplication.m_scan_type);
//				scanInfo.setBarcode(strBarcode);
//				scanInfo.setWhat(Constant.SCAN_DATA);
//
//				Message message = new Message();
//				message.what = Constant.SCAN_DATA;
//				message.obj = scanInfo;
//				MyApplication.getEventBus().post(message);

				return;
			} else if (action.equals("com.android.scancontext")) {
				// “前台输出”不打勾时，不会发送此Intent
				String str = intent.getStringExtra("Scan_context");
				Logs.v("zd", "前台输出: " + str);
				// strBarcode = "";
				// final Message lMsg = Message.obtain(scanHandler, 0,
				// strBarcode);
				// lMsg.sendToTarget();
				// return;
			}
		}
	};

	// 不能删，否则扫描有问题
	public void onEventMainThread(Message msg) {

	}

	private Handler mHandler = new Handler() {

		public void handleMessage(Message msg) {

			if (msg.what == ScanThread.SCAN) {
				String data = msg.getData().getString("data").trim().toString();

				Log.v("zd", "RFID机器扫描结果: " + data + "/" + data.length());

				Message message = new Message();
				message.what = Constant.SCAN_DATA;
				message.obj = data;
				MyApplication.getEventBus().post(message);
			}else if(msg.what == 0x0011){
				//				CommandTools.showToast((String) msg.obj);
			}else if(msg.what == 0x0012){

				CommandTools.showHotFixDialog(CommandTools.getGlobalActivity(), "重启", "热修复成功，重启生效", new CommandToolsCallback() {

					@Override
					public void callback(int position) {

						if(position == 0){
							//							restartApp(getInstance());
							Intent intent = getBaseContext().getPackageManager().getLaunchIntentForPackage(getBaseContext().getPackageName());
							PendingIntent restartIntent = PendingIntent.getActivity(CommandTools.getGlobalActivity(), 0, intent, PendingIntent.FLAG_ONE_SHOT);
							AlarmManager mgr = (AlarmManager)getSystemService(Context.ALARM_SERVICE);
							mgr.set(AlarmManager.RTC, System.currentTimeMillis() + 500, restartIntent); // 1秒钟后重启应用

							finishAllActivities();
						}
					}
				});
			}
		};
	};

	private BroadcastReceiver mScanReceiver = new BroadcastReceiver() {

		@Override
		public void onReceive(Context context, Intent intent) {

			byte[] barcode = intent.getByteArrayExtra("barocode");
			//byte[] barcode = intent.getByteArrayExtra("barcode");
			int barocodelen = intent.getIntExtra("length", 0);
			byte temp = intent.getByteExtra("barcodeType", (byte) 0);
			android.util.Log.i("debug", "----codetype--" + temp);
			String barcodeStr = new String(barcode, 0, barocodelen);
			
			String regEx="[`~!@#$%^&*()+=|{}':;',\\[\\].<>/?~！@#￥%……&*（）——+|{}【】‘；：”“’。，、？]"; 
			Pattern p = Pattern.compile(regEx); 
			Matcher m = p.matcher(barcodeStr);
			barcodeStr = m.replaceAll("").trim();
			
//			Message message = new Message();
//			message.what = Constant.SCAN_DATA;
//			message.obj = barcodeStr;
//			MyApplication.getEventBus().post(message);
			
			ScanInfo scanInfo = new ScanInfo();
			scanInfo.setType(MyApplication.m_scan_type);
			scanInfo.setBarcode(barcodeStr);
			scanInfo.setWhat(Constant.SCAN_DATA);

			Message message = new Message();
			message.what = Constant.SCAN_DATA;
			message.obj = scanInfo;
			MyApplication.getEventBus().post(message);
		}
	};

	public void restartApp(Context context){

		Intent intent1=new Intent(context, killSelfService.class);
		intent1.putExtra("PackageName",context.getPackageName());
		intent1.putExtra("Delayed", 2000);
		context.startService(intent1);
		android.os.Process.killProcess(android.os.Process.myPid());
	}

}
