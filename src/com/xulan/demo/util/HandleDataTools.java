package com.xulan.demo.util;

import java.util.List;

import android.content.Context;
import android.os.Message;
import android.widget.EditText;

import com.xulan.demo.MyApplication;
import com.xulan.demo.activity.stick.ListViewAdapter;
import com.xulan.demo.adapter.CommonAdapter;
import com.xulan.demo.data.ScanData;
import com.xulan.demo.data.ScanNumInfo;
import com.xulan.demo.data.UserInfo;
import com.xulan.demo.util.PostTools.ObjectCallback;

/**
 * 数据处理类
 * @author Administrator
 *
 */
public class HandleDataTools {


	/**
	 * 载入数量，刷新
	 * 初次加载及每次上传成功后调用
	 * @param mContext
	 * @param edtCount1
	 * @param edtCount2
	 * @param edtCount3
	 * @param edtCount4
	 * @param taskId
	 * @param scan_num
	 */
	public static void handleLoadNumber(Context mContext, final EditText edtCount1, final EditText edtCount2, final EditText edtCount3, final EditText edtCount4, 
			String taskId, final int scan_num){

		PostTools.getLoadNumber(mContext, taskId, new ObjectCallback() {

			@Override
			public void callback(int res, String remark, Object object) {

				ScanNumInfo info = (ScanNumInfo) object;

				edtCount1.setText(scan_num + "");
				edtCount2.setText(info.getMust_scan_number() + "");
				edtCount3.setText(info.getReal_load_number() + "");
				edtCount4.setText(info.getMust_load_number() + "");
			}
		});
	}

	/**
	 * 对上传成功后的数据进行删除
	 * @param commonAdapter
	 * @param dataList
	 * @param uploadList
	 */
	public static void handleUploadData(CommonAdapter<ScanData> commonAdapter, List<ScanData> dataList, List<ScanData> uploadList){

		int len2 = uploadList.size();
		for(int i=0; i<dataList.size(); i++){

			ScanData scanData = dataList.get(i);
			for(int k=0; k<len2; k++){

				ScanData scanData2 = uploadList.get(k);
				if(scanData.getPackBarcode().equals(scanData2.getPackBarcode())){
					dataList.remove(i);
					--i;
				}
			}
		}

		uploadList.clear();
		commonAdapter.notifyDataSetChanged();
	}
	
	/**
	 * 对上传成功后的数据进行删除
	 * @param commonAdapter
	 * @param dataList
	 * @param uploadList
	 */
	public static void handleUploadData(ListViewAdapter commonAdapter, List<ScanData> dataList, List<ScanData> uploadList){

		int len2 = uploadList.size();
		for(int i=0; i<dataList.size(); i++){

			ScanData scanData = dataList.get(i);
			for(int k=0; k<len2; k++){

				ScanData scanData2 = uploadList.get(k);
				if(scanData.getPackNumber().equals(scanData2.getPackNumber())){
					dataList.remove(i);
					--i;
				}
			}
		}

		uploadList.clear();
		commonAdapter.notifyDataSetChanged();
	}

	/**
	 * 判断当前节点第一个有效环节
	 * @return
	 */
	public static int getFirstLinkNum(){

		UserInfo info = MyApplication.m_userInfo;
		if(info.isLink_1()){
			return 1;
		}else if(info.isLink_2()){
			return 2;
		}else if(info.isLink_3()){
			return 3;
		}

		return 0;
	}

	public static void checkRFIDData(String pack_barcode){

		Message msg = new Message();
		msg.what = Constant.CHECK_RFID_DATA;
		msg.obj = pack_barcode;
		MyApplication.getEventBus().post(msg);
	}
}
