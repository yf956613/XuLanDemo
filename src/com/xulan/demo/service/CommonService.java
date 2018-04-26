package com.xulan.demo.service;

import java.util.ArrayList;

import org.apache.http.NameValuePair;

import com.xulan.demo.net.AsyncNetTask;
import com.xulan.demo.net.AsyncTaskManager;
import com.xulan.demo.net.HttpSendType;
import com.xulan.demo.net.LoadPicNetTask;
import com.xulan.demo.net.LoadPicParams;
import com.xulan.demo.net.LoadTextNetTask;
import com.xulan.demo.net.LoadTextParams;
import com.xulan.demo.net.NetTaskParamsMaker;
import com.xulan.demo.net.AsyncNetTask.OnPostExecuteListener;
import com.xulan.demo.util.Logs;

/**
 * 
 * @author HeXiuHui
 * 
 */
public class CommonService {

	// 从图片服务器获取图片
	public static LoadPicNetTask getPicture(OnPostExecuteListener listener, Object tag, String strPicUrl) {
		LoadPicParams params = NetTaskParamsMaker.makeLoadPictureParams(strPicUrl);

		LoadPicNetTask task = (LoadPicNetTask) AsyncTaskManager.createAsyncTask(AsyncNetTask.TaskType.GET_PIC, params);
		task.addOnPostExecuteListener(listener, tag);
		task.executeMe();

		return task;
	}
	
	/** 获取服务器端APK版本号 **/
	public static LoadTextNetTask getAppVersion(OnPostExecuteListener listener, String strUrl, Object tag) {
		final ArrayList<NameValuePair> listArg = new ArrayList<NameValuePair>();
		
		Logs.v("checkupdate", "APP更新检查: " + strUrl);
		LoadTextParams params = NetTaskParamsMaker.makeLoadTextParams(strUrl,
				listArg, HttpSendType.HTTP_GET);
		LoadTextNetTask task = (LoadTextNetTask) AsyncTaskManager
				.createAsyncTask(AsyncNetTask.TaskType.GET_TEXT, params);

		task.addOnPostExecuteListener(listener, tag);
		task.executeMe();
		
		return task;
	}
	
}
