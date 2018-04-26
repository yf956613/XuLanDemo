package com.xulan.demo.util;

import org.json.JSONException;
import org.json.JSONObject;
import android.content.Context;
import android.text.TextUtils;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request.Method;
import com.android.volley.Response.ErrorListener;
import com.android.volley.Response.Listener;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.exam.net.VolleyErrorHelper;
import com.xulan.demo.MyApplication;
import com.xulan.demo.net.AsyncNetTask;
import com.xulan.demo.net.HttpSendType;
import com.xulan.demo.net.LoadTextResult;
import com.xulan.demo.net.NetTaskResult;
import com.xulan.demo.net.NetTaskUtil;
import com.xulan.demo.net.AsyncNetTask.OnPostExecuteListener;
import com.xulan.demo.view.CustomProgress;

/** 
 * 网络数据请求
 * 
 * @author yxx
 *
 * @date 2015-12-23 下午7:48:08
 * 
 */
public class RequestUtil{

	/**
	 * @param res (-1：服务器报错  0： 成功  -2：本地报错)
	 * @param remark 报错内容
	 * @param jsonArray  msg内的jsonArray数据
	 */
	public static abstract class RequestCallback {
		public abstract void callback(int res, String remark, JSONObject jsonObject);
	}

	public static abstract class ObjectCallback {
		public abstract void callback(int res, String remark, Object object);
	}

	public RequestUtil(Context context){

	}

	public static void getReuestData(final Context context, String strUrl, JSONObject jsonObject, final RequestCallback callback){

		Logs.v("requestData", strUrl);
		Logs.v("requestData", jsonObject.toString());

		JsonObjectRequest jsonObjectRequest  = new JsonObjectRequest(Method.POST, Constant.URL + strUrl, new Listener<JSONObject>() {

			@Override
			public void onResponse(JSONObject jsonObject) {

				Logs.e("requestData", jsonObject.toString());

				int res = 0;
				String remark = null;
				try {
					res = jsonObject.getInt("success");
					remark = jsonObject.getString("message");
				} catch (JSONException e) {
					e.printStackTrace();
				}finally{
					callback.callback(res, remark, jsonObject);
				}

			}
		}, new ErrorListener() {

			@Override
			public void onErrorResponse(VolleyError arg0) {

				String strErr= VolleyErrorHelper.getMessage(arg0, MyApplication.getInstance());
				callback.callback(-1, strErr, null);
				CustomProgress.dissDialog();
			}
		});

		jsonObjectRequest.setRetryPolicy(new DefaultRetryPolicy(5 * 1000, 1, 1.0f));
		MyApplication.getQueue().add(jsonObjectRequest);
	}

	/**
	 * 获取服务器端APK版本号
	 * @return
	 */
	public static void getVersionCode(final RequestCallback callback){

		Logs.e("upload", "---------------------------------------------");
		Logs.i("upload", "获取APP版本号: ");
		Logs.i("upload", Constant.UPDATE_URL);

		JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Method.GET, Constant.UPDATE_URL, new Listener<JSONObject>() {

			@Override
			public void onResponse(JSONObject jsonObject) {

				Logs.e("upload", jsonObject.toString());

				int res = 0;
				String strRemark = null;

				try {
					res = jsonObject.getInt("res");
					strRemark = jsonObject.getString("remark");

				} catch (JSONException e) {
					e.printStackTrace();
				}finally{
					callback.callback(res, strRemark, jsonObject);
				}
			}
		}, new ErrorListener() {

			@Override
			public void onErrorResponse(VolleyError arg0) {

				Logs.e("file", "ErrorListener: " + arg0.toString());
				String strErr= VolleyErrorHelper.getMessage(arg0, MyApplication.getInstance());
				callback.callback(-1, strErr, null);
			}
		});

		jsonObjectRequest.setRetryPolicy(new DefaultRetryPolicy(5 * 1000, 1, 1.0f));
		MyApplication.getQueue().add(jsonObjectRequest);
	}

	/**
	 * @param mContext
	 * @param strUrl
	 * @param token是否需要token
	 * @param jsonObject
	 * @param callback
	 */
	public static void postDataIfToken(final Context mContext, String strUrl, JSONObject jsonObject, boolean token, final RequestCallback callback) {

		OnPostExecuteListener listener = null;
		listener = new OnPostExecuteListener() {

			@Override
			public void onPostExecute(AsyncNetTask t, NetTaskResult result,
					Object tag) {
				// TODO Auto-generated method stub
				
				String remark;
				if (result.m_nResultCode == 0) {

					LoadTextResult mresult = (LoadTextResult) result;
					String strMsg = mresult.m_strContent;
					Logs.e("NetTaskUtil", strMsg + "");

					if (TextUtils.isEmpty(strMsg)) {
						return;
					}

					try {
						JSONObject jsonObject = new JSONObject(strMsg);
						int success = jsonObject.getInt("success");
						remark = jsonObject.getString("message");

						callback.callback(success, remark, jsonObject);
					} catch (JSONException e) {
						e.printStackTrace();
					}
				} else {
					
					if(result.m_nStatusCode == 404){
						remark = "服务器连接失败";
					}else{
						remark = result.m_strResultDesc;
					}
					
					callback.callback(1, remark, new JSONObject());
				}

				CustomProgress.dissDialog();
			}
		};

		if(strUrl.contains("http")){

		}else{
			strUrl = Constant.URL + strUrl;
		}

		NetTaskUtil.makeTextNetTask(strUrl, jsonObject.toString(), token, HttpSendType.HTTP_POST, listener, null);
	}

}
