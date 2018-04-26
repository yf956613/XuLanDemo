package com.xulan.demo.util;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.lang.reflect.Field;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.json.JSONArray;
import org.json.JSONObject;

import android.R.integer;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.graphics.Bitmap;
import android.graphics.Bitmap.CompressFormat;
import android.os.Build;
import android.telephony.TelephonyManager;
import android.text.TextUtils;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Toast;
import com.xulan.demo.MyApplication;
import com.xulan.demo.takephoto.Bimp;


/** 
 * 工具类
 *
 * @date 2016-5-20 下午2:04:19
 * 
 */
public class CommandTools {

	private static Dialog singleDialog = null;
	private static TelephonyManager telephonemanage;
	private static Toast toast;

	// 弹窗结果回调函数
	public static abstract class CommandToolsCallback {
		public abstract void callback(int position);
	}

	public static String getTimes() {
		Date date = new Date();
		SimpleDateFormat dateFormat = new SimpleDateFormat("yyyyMMddHHmmss");
		return dateFormat.format(date).toString();
	}

	public static String getTimeDate(){

		SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd");

		return sdf.format(new Date(System.currentTimeMillis()));
	}

	/**
	 * 将bitmap转换成base64字符�?
	 * 
	 * @param bitmap
	 * @return base64 字符�?
	 */
	public static String bitmap2String(Bitmap bitmap, int bitmapQuality) {

		String string = null;
		try {
			ByteArrayOutputStream bStream = new ByteArrayOutputStream();
			bitmap.compress(CompressFormat.JPEG, bitmapQuality, bStream);
			byte[] bytes = bStream.toByteArray();
			string = Base64.encodeToString(bytes, Base64.NO_WRAP);
		} catch (Exception e) {
			e.printStackTrace();
			return "";
		}
		return string;
	}

	public static String bitmapToBase64(Bitmap bitmap) {  

		String result = null;  
		ByteArrayOutputStream baos = null;  
		try {  
			if (bitmap != null) {  
				baos = new ByteArrayOutputStream();  
				bitmap.compress(Bitmap.CompressFormat.JPEG, 60, baos);  

				baos.flush();  
				baos.close();  

				byte[] bitmapBytes = baos.toByteArray();  
				result = Base64.encodeToString(bitmapBytes, Base64.DEFAULT);  
			}  
		} catch (IOException e) {  
			e.printStackTrace();  
		} finally {  
			try {  
				if (baos != null) {  
					baos.flush();  
					baos.close();  
				}  
			} catch (IOException e) {  
				e.printStackTrace();  
			}  
		}  
		return result;  
	}  

	/**
	 * 身份证号码验证
	 * @param text
	 * @return
	 * 我国当前的身份证号分为三种：
	 * �?�?15位身份证�?
	 * 二�??18位身份证号（�?17位位数字，最后一位为字母x/X�?
	 * 三�??18位身份证号（18位都是数字）
	 */
	public static boolean personIdValidation(String text) {

		if(text == null || text.equals("")){
			return false;
		}

		String regx = "[0-9]{17}x";
		String regx1 = "[0-9]{17}X";
		String regx2 = "[0-9]{15}";
		String regx3 = "[0-9]{18}";

		return text.matches(regx) || text.matches(regx1) || text.matches(regx2) || text.matches(regx3);
	}

	/**
	 * 获取PDA SN�?
	 * 
	 * @param context
	 * @return
	 */
	public static String getMIME(Context context) {
		telephonemanage = (TelephonyManager) context
				.getSystemService(Context.TELEPHONY_SERVICE);
		try {
			String sn = telephonemanage.getDeviceId();
			return sn;
		} catch (Exception e) {
			Logs.e("MIME", e.getMessage());
			return "00000000000";
		}
	}

	/**
	 * 弹出toast提示 防止覆盖
	 * 
	 * @param msg
	 */
	public static void showToast(String msg) {

		if (toast == null) {
			toast = Toast.makeText(MyApplication.getInstance(), msg + "", Toast.LENGTH_SHORT);
		} else {
			toast.setText(msg);
		}
		toast.show();
	}

	/**
	 * 强制关闭软键盘
	 */
	public static void hidenKeyboars(Context context, View view) {

		InputMethodManager imm = (InputMethodManager) context
				.getSystemService(Context.INPUT_METHOD_SERVICE);

		imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
	}

	/**
	 * 验证手机是否合法
	 * 
	 * @param mobiles
	 *            传入的手机号
	 * @return true 合法 false 不合�?
	 */
	public static boolean isMobileNO(String mobiles) {
		/*
		 * 移动�?134�?135�?136�?137�?138�?139�?150�?151�?157(TD)�?158�?159�?187�?188
		 * 联�?�：130�?131�?132�?152�?155�?156�?185�?186 电信�?133�?153�?180�?189、（1349卫�?�）
		 * 总结起来就是第一位必定为1，第二位必定�?3�?5�?8，其他位置的可以�?0-9
		 */
		String telRegex = "[1][34587]\\d{9}";// "[1]"代表�?1位为数字1�?"[358]"代表第二位可以为3�?5�?8中的�?个，"\\d{9}"代表后面是可以是0�?9的数字，�?9位�??
		if (TextUtils.isEmpty(mobiles))
			return false;
		else
			return mobiles.matches(telRegex);
	}

	/**
	 * yyyy-MM-dd HH:mm:ss
	 * @return
	 */
	public static String getTime() {

		Date nowdate = new Date(); // 当前时间
		SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		return dateFormat.format(nowdate);
	}

	/**
	 * 获取当前上下文
	 * @return
	 */
	public static Activity getGlobalActivity(){

		Class<?> activityThreadClass;
		try {
			activityThreadClass = Class.forName("android.app.ActivityThread");
			Object activityThread = activityThreadClass.getMethod("currentActivityThread").invoke(null);
			Field activitiesField = activityThreadClass.getDeclaredField("mActivities");
			activitiesField.setAccessible(true);
			Map<?, ?> activities = (Map<?, ?>) activitiesField.get(activityThread);
			for(Object activityRecord:activities.values()){
				Class<? extends Object> activityRecordClass = activityRecord.getClass();
				Field pausedField = activityRecordClass.getDeclaredField("paused");
				pausedField.setAccessible(true);
				if(!pausedField.getBoolean(activityRecord)) {
					Field activityField = activityRecordClass.getDeclaredField("activity");
					activityField.setAccessible(true);
					Activity activity = (Activity) activityField.get(activityRecord);
					return activity;
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}

		return null;
	}


	/**
	 * 根据手机的分辨率�? dp 的单�? 转成�? px(像素)
	 */
	public static int dip2px(Context context, float dpValue) {
		final float scale = context.getResources().getDisplayMetrics().density;
		return (int) (dpValue * scale + 0.5f);
	}

	/**
	 * 根据手机的分辨率�? px(像素) 的单�? 转成�? dp
	 */
	public static int px2dip(Context context, float pxValue) {
		final float scale = context.getResources().getDisplayMetrics().density;
		return (int) (pxValue / scale + 0.5f);
	}

	/**
	 * 获取1000以内随机�?
	 * @return
	 */
	public static String getRandomNumber(){

		Random random = new Random();
		return (random.nextInt(100)) + "";
	}

	// 获取版本号
	public static String getVersionName() {

		try {
			PackageManager pm = MyApplication.getInstance().getPackageManager();
			String versionName = "";
			try {
				PackageInfo packageInfo = pm.getPackageInfo(
						MyApplication.getInstance().getPackageName(), 0);
				versionName = packageInfo.versionName;
			} catch (NameNotFoundException e) {
				e.printStackTrace();
			}
			return versionName;
		} catch (Exception e) {
			return "1.0";
			// TODO: handle exception
		}

	}

	/**
	 * 获取版本�?
	 * @param context
	 * @return
	 */
	public static int getVersionCode() {

		Context context = MyApplication.getInstance();
		int versionCode = 1;
		try {
			PackageManager pm = context.getPackageManager();

			try {
				PackageInfo packageInfo = pm.getPackageInfo(
						context.getPackageName(), 0);
				versionCode = packageInfo.versionCode;
			} catch (NameNotFoundException e) {
				e.printStackTrace();
			}
			return versionCode;
		} catch (Exception e) {
			return versionCode;
		}

	}

	/**
	 * 获取PDA SN
	 * 
	 * @param context
	 * @return
	 */
	public static String getDevId() {
		Build bd = new Build();  
		String model = bd.MODEL;
		return model;
	}

	/**
	 * 用正则判断某个字符串是否为数
	 * 
	 * @param str
	 *            �? 判断的字符串
	 * @return true �?; false �?
	 */
	public static boolean isNumeric(String str) {
		Pattern pattern = Pattern.compile("[0-9]*");
		Matcher isNum = pattern.matcher(str);
		if (!isNum.matches()) {
			return false;
		}
		return true;
	}

	/**
	 * 弹出信息，需要手动关
	 * 
	 * @param context
	 * @param strMsg
	 */
	public static void showDialog(final Context context, final String strMsg) {

		Activity mActivity = (Activity) context;
		if (mActivity.isFinishing()) {
			Logs.v("zd", "当前activity界面已关闭，不能显示对话?");
			return;
		}

		//		new AlertDialog.Builder(context).setTitle("提示").setMessage(strMsg + "")
		//		.setPositiveButton("关闭", new DialogInterface.OnClickListener() {
		//			@Override
		//			public void onClick(DialogInterface dialog, int which) {
		//				dialog.dismiss();
		//			}
		//		}).show();

		if(singleDialog != null){
			singleDialog.dismiss();
			singleDialog = null;
		}

		singleDialog = new AlertDialog.Builder(context)
		.setTitle("提示")
		.setMessage(strMsg + "")
		.setPositiveButton("关闭", new OnClickListener() {

			@Override
			public void onClick(DialogInterface arg0, int arg1) {
				// TODO Auto-generated method stub
				arg0.dismiss();
			}
		}).show();

	}

	/**
	 * 弹出信息，需要手动关
	 * 
	 * @param context
	 * @param strMsg
	 */
	public static void showHotFixDialog(final Context context, String btn, final String strMsg, final CommandToolsCallback callback) {

		Activity mActivity = (Activity) context;
		if (mActivity == null || mActivity.isFinishing()) {
			Logs.v("zd", "当前activity界面已关闭，不能显示对话?");
			return;
		}

		if(singleDialog != null){
			singleDialog.dismiss();
			singleDialog = null;
		}

		singleDialog = new AlertDialog.Builder(context)
		.setTitle("提示")
		.setMessage(strMsg + "")
		.setCancelable(false)
		.setPositiveButton(btn, new OnClickListener() {

			@Override
			public void onClick(DialogInterface arg0, int arg1) {
				// TODO Auto-generated method stub
				arg0.dismiss();
				callback.callback(0);
			}
		}).show();

	}


	/**
	 * 弹出确认取消框
	 * @param context
	 * @param strMsg
	 */
	static Builder dialog;
	public static void showChooseDialog(final Context context, final String strMsg, final CommandToolsCallback callback) {

		Activity mActivity = (Activity) context;
		if (mActivity.isFinishing()) {
			return;
		}

		dialog = new AlertDialog.Builder(context);

		dialog.setTitle("提示");
		dialog.setMessage(strMsg + "");
		dialog.setPositiveButton("确认", new DialogInterface.OnClickListener(){  

			public void onClick(DialogInterface dialoginterface, int i){   

				callback.callback(0);
			}   
		});
		dialog.setNegativeButton("取消", new OnClickListener() {

			@Override
			public void onClick(DialogInterface arg0, int arg1) {
				// TODO Auto-generated method stub
				callback.callback(-1);
			}
		});

		dialog.show();
	}

	/**
	 * 获取主键 UUID
	 * @return
	 */
	public static String getUUID(){

		return java.util.UUID.randomUUID().toString();
	}


	/**
	 * 获取当前时间精确到毫秒
	 * 
	 * @return
	 */
	static String strLastTime;
	public static String initDataTime() {

		SimpleDateFormat fmt = new SimpleDateFormat("yyyyMMddHHmmssSSS"); // 精确到毫秒
		String suffix = fmt.format(new Date());

		strLastTime = MyApplication.getInstance().sendTime;

		boolean flag = strLastTime.equals(suffix);
		while (flag == true) {

			suffix = fmt.format(new Date());
			strLastTime = MyApplication.getInstance().sendTime;
			flag = strLastTime.equals(suffix);

			try {
				Thread.sleep(300);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}

		return suffix;
	}

	/**
	 * 时间转换格式
	 * @param time
	 * @return
	 */
	public static String changeL2S(String strTime){

		try{

			long time = Long.valueOf(strTime); 
			SimpleDateFormat sdf= new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");  

			return sdf.format(new Date(time));  
		}catch(Exception e){
			e.printStackTrace();
		}

		return "";
	}

	/**
	 * 拍照图片处理，
	 * 包括图片的分辨率都在这里统一修改
	 * @param picture
	 * @return
	 */
	public static JSONArray getPicJsonArray(List<String> picture){
		JSONObject jsonObject = null;
		JSONArray jsonArray = new JSONArray();

		try {
			for (int j = 0; j < picture.size(); j++) {
				jsonObject = new JSONObject();

				//				Bitmap bm = Bimp.equalRatioScale(picture.get(j), 600, 800);
				Bitmap bm = Bimp.resetBitmap(picture.get(j), 600, 800);

				String base64 = CommandTools.bitmapToBase64(bm);
				jsonObject.put("base64", base64);
				Log.v("zd", "base64.lenth = " + base64.length());
				jsonArray.put(jsonObject);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}

		return jsonArray;
	}

	public static String[] getStr(List<Integer> idList, Context context) {
		String [] str = new String[idList.size()];
		for (int i = 0; i < idList.size(); i++) {
			str[i] = context.getResources().getString(idList.get(i));
		}
		return str;
	}

	public static String getDeviceName(){

		return android.os.Build.MODEL;
	}
}


