package com.xulan.demo.util;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.os.Message;
import android.text.TextUtils;
import android.util.Log;

import com.xulan.demo.MyApplication;
import com.xulan.demo.activity.stick.ListViewAdapter;
import com.xulan.demo.adapter.CommonAdapter;
import com.xulan.demo.data.RfidInfo;
import com.xulan.demo.data.ScanData;
import com.xulan.demo.pdascan.RFID;
import com.xulan.demo.takephoto.Bimp;
import com.xulan.demo.takephoto.ImageItem;
import com.xulan.demo.util.RequestUtil.RequestCallback;

/** 
 * 数据处理类
 * 
 * @author yxx
 *
 * @date 2017-3-30 上午11:14:04
 * 
 */
public class DataUtilTools {

	/**
	 * 判断集合中是否有该数据，如果有则删除重新添加，没有则添加
	 * @param scanData
	 * @param dataList
	 * @param commonAdapter
	 */
	public static void checkDataList(ScanData scanData, List<ScanData> dataList, CommonAdapter<ScanData> commonAdapter){

		int pos = dataList.size();

		int len = dataList.size();
		for(int i=0; i<len; i++){

			ScanData data = dataList.get(i);
			if(data.getCacheId().equals(scanData.getCacheId())){
				pos = i;
				dataList.remove(i);
				break;
			}
		}

		dataList.add(pos, scanData);
		commonAdapter.notifyDataSetChanged();
	}

	/**
	 * 获取拍照转换的base64数组
	 * 通过barcode关联
	 * @param barcode
	 * @return
	 */
	public static JSONArray getBaseJsonArray(String barcode){

		JSONArray jsonArray = new JSONArray();

		Bimp.initPhotoList(barcode);

		int len = Bimp.tempSelectBitmapList.size();
		for(int i=0; i<len; i++){

			JSONObject jsonObject = new JSONObject();
			ImageItem item = Bimp.tempSelectBitmapList.get(i);
			String base64 = CommandTools.bitmapToBase64(item.getBitmap());

			try {
				jsonObject.put("base64", base64);
				jsonArray.put(jsonObject);
			} catch (JSONException e) {
				e.printStackTrace();
			}
		}

		Log.v("post-data", "本次上传图片张数: " + jsonArray.length());

		return jsonArray;
	}

	/**
	 * 按照包装条码排序
	 * @param dataList
	 * @param commonAdapter
	 */
	public static void sortByPackBarCode(List<ScanData> dataList, CommonAdapter<ScanData> commonAdapter){

		Constant.SORT_DATA_TYPE = 0;

		List<ScanData> filterData = new ArrayList<ScanData>();
		filterData.clear();
		filterData.addAll(dataList);
		Collections.sort(filterData, new BarcodeComparator());

		dataList.clear();
		dataList.addAll(0, filterData);

		++Constant.SORT_DATA_NUMBER;
		commonAdapter.notifyDataSetChanged();
	}

	/**
	 * 按照包装号码排序
	 * @param dataList
	 * @param commonAdapter
	 */
	public static void sortByPackNo(List<ScanData> dataList, CommonAdapter<ScanData> commonAdapter){

		Constant.SORT_DATA_TYPE = 1;

		List<ScanData> filterData = new ArrayList<ScanData>();
		filterData.clear();
		filterData.addAll(dataList);
		Collections.sort(filterData, new BarcodeComparator());

		dataList.clear();
		dataList.addAll(0, filterData);

		++Constant.SORT_DATA_NUMBER;
		commonAdapter.notifyDataSetChanged();
	}
	
	/**
	 * 按照包装条码排序
	 * @param dataList
	 * @param commonAdapter
	 */
	public static void sortByPackBarCode(List<ScanData> dataList, ListViewAdapter commonAdapter){

		Constant.SORT_DATA_TYPE = 0;

		List<ScanData> filterData = new ArrayList<ScanData>();
		filterData.clear();
		filterData.addAll(dataList);
		Collections.sort(filterData, new BarcodeComparator());

		dataList.clear();
		dataList.addAll(0, filterData);

		++Constant.SORT_DATA_NUMBER;
		commonAdapter.notifyDataSetChanged();
	}

	/**
	 * 按照包装号码排序
	 * @param dataList
	 * @param commonAdapter
	 */
	public static void sortByPackNo(List<ScanData> dataList, ListViewAdapter commonAdapter){

		Constant.SORT_DATA_TYPE = 1;

		List<ScanData> filterData = new ArrayList<ScanData>();
		filterData.clear();
		filterData.addAll(dataList);
		Collections.sort(filterData, new BarcodeComparator());

		dataList.clear();
		dataList.addAll(0, filterData);

		++Constant.SORT_DATA_NUMBER;
		commonAdapter.notifyDataSetChanged();
	}

	/**
	 * 扫描条码比对
	 * @param barcode
	 * @param dataList
	 * @return
	 */
	public static ScanData checkScanData(String scanType, String barcode, List<ScanData> dataList){

		int len = dataList.size();
		for(int i=0; i<len; i++){

			ScanData mScanData = dataList.get(i);
			Log.v("result", scanType + "---" + i + "---" + barcode + "/" + mScanData.getPackBarcode().toString());

			//第二个判断是针对包装分单号
			if(mScanData.getPackBarcode().equals(barcode) || mScanData.getMinutePackBarcode().equals(barcode)){
				return mScanData;
			}
		}

		return null;
	} 

	/**
	 * 根据RFID标识tag获取绑定的货物
	 * @param rfid
	 */
	public static void getInfoByRFID(String tagId){

		if(TextUtils.isEmpty(tagId) || TextUtils.isEmpty(MyApplication.m_projectId)){
			return;
		}

		JSONObject jsonObject = new JSONObject();

		try {

			jsonObject.put("project_id", MyApplication.m_projectId);
			jsonObject.put("type", MyApplication.m_scan_type);
			jsonObject.put("rfid", tagId);
		} catch (JSONException e) {
			e.printStackTrace();
		}

		String strUrl = "action/rfid/getgoodsinfo";
		RequestUtil.postDataIfToken(MyApplication.getInstance(), strUrl, jsonObject, false, new RequestCallback() {

			@Override
			public void callback(int res, String remark, JSONObject jsonObject) {

				if(res != 0){
					return;
				}

				jsonObject = jsonObject.optJSONObject("data");
				if(jsonObject == null){
					return;
				}
				
				String pack_barcode = jsonObject.optString("pack_barcode");
				String rfid = jsonObject.optString("rfid");
				Message msg = new Message();
				msg.what = Constant.SCAN_DATA;
				msg.obj = pack_barcode;

				MyApplication.getEventBus().post(msg);

				List<RfidInfo> listTag = RFID.listTag;
				if(listTag != null && listTag.size() > 0){

					int len = listTag.size();
					for(int i=0; i<len; i++){

						RfidInfo info = listTag.get(i);
						Log.v("rfid", "从后台取到数据: " + info.getId() + "," + info.getPack_barcode() + "," + info.getLink_num());
						if(info.getId().equals(rfid)){
							info.setPack_barcode(pack_barcode);
							break;
						}
					}
				}
			}
		});
	}

}
