package com.xulan.demo.net;

import java.util.concurrent.ThreadPoolExecutor;

import android.content.Intent;

import com.xulan.demo.LoginActivity;
import com.xulan.demo.MyApplication;
import com.xulan.demo.util.Logs;

/**
 * 网络请求
 * 
 */
public class LoadTextNetTask extends AsyncNetTask {
	public LoadTextNetTask(ThreadPoolExecutor executor, LoadTextParams params) {
		super(executor, params);
		setType(AsyncNetTask.TaskType.GET_TEXT);
	}

	protected NetTaskResult doLoadTextWork(NetTaskParams... params) {
		LoadResult preResult = NetTaskUtil.doLoadWork(this, params);
		LoadTextResult pstResult = new LoadTextResult();
		pstResult.m_nStatusCode = preResult.m_nStatusCode;
		if (preResult.m_nResultCode == 0) {
			pstResult.m_strContent = new String(preResult.buf);
			pstResult.m_nResultCode = 0;
			pstResult.m_strResultDesc = "";
		} else {
			pstResult.m_nResultCode = preResult.m_nResultCode;
			pstResult.m_strResultDesc = preResult.m_strResultDesc;
		}

		return pstResult;
	}

	@Override
	protected NetTaskResult doInBackground(NetTaskParams... params) {
		publishProgress(0L);
		AsyncTaskManager.yieldIfNeeded(this);
		publishProgress(1L, 0L, 0L);
		// return doFakeTaskWork(params);
		return doLoadTextWork(params);
	}

	@Override
	protected void onPreExecute() {
		super.onPreExecute();
	}

	@Override
	protected void onPostExecute(NetTaskResult result) {
		
		if (result.m_nResultCode == 0) {
			LoadTextResult mresult = (LoadTextResult) result;

			Logs.e("post-data",mresult.m_strContent + "");
			Logs.i("NetTaskUtil", mresult.m_strContent + "");
		}
		super.onPostExecute(result);
	}

	@Override
	protected void onProgressUpdate(Long... values) {
		super.onProgressUpdate(values);
	}

	@Override
	protected void onCancelled() {
		super.onCancelled();
	}
}
