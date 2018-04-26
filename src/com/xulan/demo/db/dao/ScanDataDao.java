package com.xulan.demo.db.dao;

import java.util.ArrayList;
import java.util.List;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.text.TextUtils;
import android.util.Log;

import com.xulan.demo.data.ScanData;
import com.xulan.demo.db.DBHelper;
import com.xulan.demo.util.CommandTools;
import com.xulan.demo.util.Constant;
import com.xulan.demo.util.ScanTypeUtil;

public class ScanDataDao {

	public static final int ABN_PIC = 0;// 异常图片类型
	public static final int RET_PIC = 1;// 退运图片类型
	public static final int MARK_PIC = 2;// 唛头图片类型
	public static final int CARGO_PIC = 3;// 货物图片类型

	private SQLiteDatabase db = null;

	/**
	 * 判断表中是否有该数据 根据扫描类型、单号
	 * 
	 * @param scanData
	 * @return
	 */
	public boolean checkData(ScanData scanData) {

		boolean flag = false;

		String strParm = " where CacheId = '" + scanData.getCacheId() + "'";

		// 集装箱
		if (Constant.SCAN_TYPE.startsWith("14")) {
			strParm = " where CacheId = '" + scanData.getCacheId() + "'"
					+ " and SubBoxNum = '" + scanData.getMinutePackBarcode() + "'";
		}

		// 包装
		else if (Constant.SCAN_TYPE.startsWith("11")) {
			strParm = " where SubBoxNum = '" + scanData.getMinutePackBarcode() + "'";
		}

		// //检验
		// else if(Constant.SCAN_TYPE.startsWith("13")){
		// strParm = " where TestNode = '" + scanData.getTestNode() + "'"
		// + " and TestOrganization = '" + scanData.getTestOrganization() + "'";
		// }

		try {
			db = DBHelper.SQLiteDBHelper.getReadableDatabase();
			String sql = "select * from " + DBHelper.TABLE_SCANDATA
					+ " where ScanType = '" + scanData.getScanType() + "'";

			Log.v("zdsql", sql);
			Cursor cursor = db.rawQuery(sql, new String[] {});

			if (cursor.moveToNext()) {
				flag = true;
			}
			cursor.close();
		} catch (Exception e) {
			e.printStackTrace();
		}

		return flag;
	}

	//保存图片
	public boolean addPicData(ScanData scanData) {
		boolean flag = false;
		try {

			db = DBHelper.SQLiteDBHelper.getWritableDatabase();
			db.beginTransaction();

			List<String> picture = scanData.getPicture();
			for (int i = 0; i < picture.size(); i++) {
				ContentValues cv = new ContentValues();
				cv.put(DBHelper.KEY_PIC_CacheID, scanData.getCacheId());
				cv.put(DBHelper.KEY_PICNAME, picture.get(i));

				long count = db.insert(DBHelper.TABLE_PICTURE, null, cv);
				if (count > 0) {
					flag = true;
					Log.i("hexiuhui---", "添加图片数据成功");
				}
			}

			db.setTransactionSuccessful();
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			db.endTransaction();
		}

		return flag;

	}

	/**
	 * 添加数据
	 * 
	 * @param list
	 * @return
	 */
	public boolean addData(ScanData scanData) {
		boolean flag = false;
		try {

			db = DBHelper.SQLiteDBHelper.getWritableDatabase();
			db.beginTransaction();

			ContentValues cv = new ContentValues();

			cv.put(DBHelper.KEY_CacheID, scanData.getCacheId());
			cv.put(DBHelper.KEY_TASKNAME, scanData.getTaskName());
			cv.put(DBHelper.KEY_TASKID, scanData.getTaskId());
			cv.put(DBHelper.KEY_ScanType, scanData.getScanType());
			cv.put(DBHelper.KEY_ScanTime, scanData.getScanTime());
			cv.put(DBHelper.KEY_SCANUSER, scanData.getScanUser());
			cv.put(DBHelper.KEY_CREATETIME, scanData.getCreateTime());
			cv.put(DBHelper.KEY_PACKBARCODE, scanData.getPackBarcode());
			cv.put(DBHelper.KEY_PACKNUMBER, scanData.getPackNumber());
			cv.put(DBHelper.KEY_TRAIN, scanData.getTrain());
			cv.put(DBHelper.KEY_DRIVERPHONE, scanData.getDeiverPhone());
			cv.put(DBHelper.KEY_Memo, scanData.getMemo());
			cv.put(DBHelper.KEY_LOGIN_ID, scanData.getLoginId());
			cv.put(DBHelper.KEY_LOGIN_NAME, scanData.getLoginName());

			cv.put(DBHelper.KEY_PlanCount, scanData.getPlanCount());
			cv.put(DBHelper.KEY_PracticalCount, scanData.getPracticalCount());
			cv.put(DBHelper.KEY_Length_Old, scanData.getLength_Old());
			cv.put(DBHelper.KEY_Width_Old, scanData.getWidth_Old());
			cv.put(DBHelper.KEY_Height_Old, scanData.getHeight_Old());
			cv.put(DBHelper.KEY_OPERATION_LINK, scanData.getOperationLink());
			cv.put(DBHelper.KEY_MinutePackBarcode, scanData.getMinutePackBarcode());
			cv.put(DBHelper.KEY_MinutePackNumber, scanData.getMinutePackNumber());
			cv.put(DBHelper.KEY_NODE_ID, scanData.getNode_id());
			cv.put(DBHelper.KEY_PACKMODE, scanData.getPackMode());
			cv.put(DBHelper.KEY_GoodsName, scanData.getGoodsName());
			cv.put(DBHelper.KEY_PACKNAME, scanData.getPackName());
			cv.put(DBHelper.KEY_MAINGOODSID, scanData.getMainGoodsId());
			cv.put(DBHelper.KEY_Length, scanData.getLength());
			cv.put(DBHelper.KEY_Width, scanData.getWidth());
			cv.put(DBHelper.KEY_Height, scanData.getHeight());
			cv.put(DBHelper.KEY_Weight, scanData.getWeight());
			cv.put(DBHelper.KEY_V3, scanData.getV3());
			cv.put(DBHelper.KEY_CHARGE_TON, scanData.getCharge_Ton());
			cv.put(DBHelper.KEY_Message, scanData.getMessage());
			cv.put(DBHelper.KEY_MarkPic, scanData.getMarkPic());
			cv.put(DBHelper.KEY_CargoPic, scanData.getCargoPic());
			cv.put(DBHelper.KEY_MarkFile, scanData.getMarkFile());
			cv.put(DBHelper.KEY_CargoFile, scanData.getCargoFile());
			cv.put(DBHelper.KEY_AbnormalPic, scanData.getAbnormalPic());
			cv.put(DBHelper.KEY_AbnormalFile, scanData.getAbnormalFile());
			cv.put(DBHelper.KEY_ReturnedCargoPic, scanData.getReturnedCargoPic());
			cv.put(DBHelper.KEY_ReturnedCargoFile, scanData.getReturnedCargoFile());
			cv.put(DBHelper.KEY_LIBRARY_NUMBER, scanData.getLibraryNumber());
			cv.put(DBHelper.KEY_LIBRARY_ADMIN, scanData.getLibraryAdamin());
			cv.put(DBHelper.KEY_Saillings, scanData.getSaillings());
			cv.put(DBHelper.Key_SAILLINGS_NAME, scanData.getSaillings_name());
			cv.put(DBHelper.KEY_SHIPPING_SPACE, scanData.getShipping_space());
			cv.put(DBHelper.KEY_AIR_FLIGHT, scanData.getFlight());
			cv.put(DBHelper.KEY_VOYAGE, scanData.getVoyage());

			cv.put(DBHelper.KEY_WAGON_NUMBER, scanData.getWagonNumber());

			cv.put(DBHelper.KEY_CONTAINERNO, scanData.getContainer_no());
			cv.put(DBHelper.KEY_FREIGHTERNAME, scanData.getFreighter_name());
			cv.put(DBHelper.KEY_SAILINGNO, scanData.getSailing_no());

			cv.put(DBHelper.KEY_COMPANY, scanData.getCompany());
			cv.put(DBHelper.KEY_COMPANYID, scanData.getCompany_id());
			cv.put(DBHelper.KEY_TelPerson, scanData.getTelPerson());
			cv.put(DBHelper.Key_AbnormalLink, scanData.getAbnormalLink());
			cv.put(DBHelper.KEY_Scaned, scanData.getScaned());
			cv.put(DBHelper.KEY_AbnormalCause, scanData.getAbnormalCause());
			cv.put(DBHelper.KEY_ReturnedCargoCause, scanData.getReturnedCargoCause());
			cv.put(DBHelper.KEY_ReturnedCargoLink, scanData.getReturnedCargoLink());
			cv.put(DBHelper.KEY_UploadStatus, scanData.getUploadStatus());

			cv.put(DBHelper.KEY_LINK, scanData.getLink());

			long count = db.insert(DBHelper.TABLE_SCANDATA, null, cv);
			if (count > 0) {
				flag = true;
				Log.i("hexiuhui---", "添加数据成功");
			}

			db.setTransactionSuccessful();
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			db.endTransaction();
		}

		return flag;
	}


	/**
	 * 修改上传状态
	 */
	public boolean updateUploadState1(List<ScanData> list) {

		ContentValues values = new ContentValues();
		values.put(DBHelper.KEY_UploadStatus, "1");
		db = DBHelper.SQLiteDBHelper.getWritableDatabase();
		int b = 0;
		for (int i = 0; i < list.size(); i++) {
			ScanData scanData = list.get(i);
			String whereClause = DBHelper.KEY_CacheID + "=?" + " and "
					+ DBHelper.KEY_UploadStatus + "=?";
			b = db.update(
					DBHelper.TABLE_SCANDATA,
					values,
					whereClause,
					new String[] { scanData.getCacheId(),
							scanData.getUploadStatus() });
		}
		return b > 0;
	}

	public boolean updateUploadState(List<ScanData> list) {

		db = DBHelper.SQLiteDBHelper.getWritableDatabase();
		int b = 0;
		for (int i = 0; i < list.size(); i++) {
			ScanData scanData = list.get(i);
			String whereClause = DBHelper.KEY_CacheID + "=?";

			b = db.delete(
					DBHelper.TABLE_SCANDATA, 
					whereClause, 
					new String[] { scanData.getCacheId()}
					);
		}
		
		return b > 0;
	}

	/**
	 * 获取未上传数量
	 * 
	 * @param scanData
	 * @return
	 */
	public int getNotUploadDataSize(String sanType) {

		int count = 0;

		db = DBHelper.SQLiteDBHelper.getReadableDatabase();
		String sql = "select * from " + DBHelper.TABLE_SCANDATA
				+ " where ScanType='" + sanType + "' and UploadStatus <> 1";
		Cursor cursor = db.rawQuery(sql, null);

		count = cursor.getCount();
		cursor.close();

		return count;
	}

	/**
	 * 获取未上传数量
	 * 
	 * @return
	 */
	public int getNotUploadDataSize() {

		int count = 0;

		db = DBHelper.SQLiteDBHelper.getReadableDatabase();
		String sql = "select * from " + DBHelper.TABLE_SCANDATA
				+ " where UploadStatus <> 1";
		Cursor cursor = db.rawQuery(sql, null);

		count = cursor.getCount();
		cursor.close();

		return count;
	}

	/**
	 * 根据扫描类型获取未上传数据 返回List  按1条分组查询
	 * 
	 * @param scanData
	 * @return
	 */
	public List<ScanData> getNotUploadDataList2(String sanType, String link, String node_id) {
		List<ScanData> list = new ArrayList<ScanData>();

		db = DBHelper.SQLiteDBHelper.getReadableDatabase();
		String sql = "select * from " + DBHelper.TABLE_SCANDATA
				+ " where ScanType='" + sanType + "'"
				+ " and UploadStatus=0 and node_id = '" + node_id + "' and link = '" + link + "' order by ScanTime asc limit " + 1 + " offset 0 ";
		Cursor cursor = db.rawQuery(sql, null);

		list = getCursorData(cursor);
		//		if (cursor != null) {
		//			cursor.close();
		//		}

		return list;
	}

	/**
	 * 根据扫描类型获取未上传数据 返回List
	 * 
	 * @param scanData
	 * @return
	 */
	public List<ScanData> getNotUploadDataList(String sanType, String link, String node_id) {
		List<ScanData> list = new ArrayList<ScanData>();

		db = DBHelper.SQLiteDBHelper.getReadableDatabase();
		String sql = "select * from " + DBHelper.TABLE_SCANDATA
				+ " where ScanType='" + sanType + "'"
				+ " and UploadStatus=0 and node_id = '" + node_id + "' and link = '" + link + "'";
		Cursor cursor = db.rawQuery(sql, null);

		list = getCursorData(cursor);
		if (cursor != null) {
			cursor.close();
		}

		return list;
	}

	/**
	 * 根据扫描类型获，任务id，取未上传数据 返回List
	 * 
	 * @param scanData
	 * @return
	 */
	public List<ScanData> getNotUploadDataList(String sanType, String link, String node_id, String taskId) {
		List<ScanData> list = new ArrayList<ScanData>();

		db = DBHelper.SQLiteDBHelper.getReadableDatabase();
		String sql = "select * from " + DBHelper.TABLE_SCANDATA
				+ " where ScanType='" + sanType + "'"
				+ " and UploadStatus=0 and taskId = '"+ taskId + "' and node_id = '" + node_id + "' and link = '" + link + "'";
		Cursor cursor = db.rawQuery(sql, null);

		list = getCursorData(cursor);
		if (cursor != null) {
			cursor.close();
		}

		return list;
	}

	/**
	 * 对cursor进行封装处理 所有对cursor取得数据从这里开始
	 * 
	 * @param cursor
	 * @return
	 */
	private List<ScanData> getCursorData(Cursor cursor) {
		List<ScanData> list = new ArrayList<ScanData>();

		try {
			while (cursor.moveToNext()) {

				ScanData scanData = new ScanData();

				scanData.setCacheId(cursor.getString(cursor.getColumnIndex(DBHelper.KEY_CacheID)));

				//查询图片数据
				db = DBHelper.SQLiteDBHelper.getReadableDatabase();
				String sql = "select * from " + DBHelper.TABLE_PICTURE
						+ " where CacheId='" + cursor.getString(cursor.getColumnIndex(DBHelper.KEY_CacheID)) + "'";
				Cursor cs = db.rawQuery(sql, null);
				List<String> picList = new ArrayList<String>();
				while (cs.moveToNext()) {
					picList.add(cs.getString(cs.getColumnIndex(DBHelper.KEY_PICNAME)));
				}
				scanData.setPicture(picList);

				scanData.setTaskName(cursor.getString(cursor.getColumnIndex(DBHelper.KEY_TASKNAME)));
				scanData.setTaskId(cursor.getString(cursor.getColumnIndex(DBHelper.KEY_TASKID)));
				scanData.setScanType(cursor.getString(cursor.getColumnIndex(DBHelper.KEY_ScanType)));
				scanData.setScanTime(cursor.getString(cursor.getColumnIndex(DBHelper.KEY_ScanTime)));
				scanData.setScanUser(cursor.getString(cursor.getColumnIndex(DBHelper.KEY_SCANUSER)));
				scanData.setCreateTime(cursor.getString(cursor.getColumnIndex(DBHelper.KEY_CREATETIME)));
				scanData.setPackBarcode(cursor.getString(cursor.getColumnIndex(DBHelper.KEY_PACKBARCODE)));
				scanData.setPackNumber(cursor.getString(cursor.getColumnIndex(DBHelper.KEY_PACKNUMBER)));
				scanData.setVehicleNumbers(cursor.getString(cursor.getColumnIndex(DBHelper.KEY_VehicleNumbers)));
				scanData.setTrain(cursor.getString(cursor.getColumnIndex(DBHelper.KEY_TRAIN)));
				scanData.setDeiverPhone(cursor.getString(cursor.getColumnIndex(DBHelper.KEY_DRIVERPHONE)));
				scanData.setMemo(cursor.getString(cursor.getColumnIndex(DBHelper.KEY_Memo)));
				scanData.setLoginId(cursor.getString(cursor.getColumnIndex(DBHelper.KEY_LOGIN_ID)));
				scanData.setLoginName(cursor.getString(cursor.getColumnIndex(DBHelper.KEY_LOGIN_NAME)));
				scanData.setPlanCount(cursor.getString(cursor.getColumnIndex(DBHelper.KEY_PlanCount)));
				scanData.setPracticalCount(cursor.getString(cursor.getColumnIndex(DBHelper.KEY_PracticalCount)));
				scanData.setOperationLink(cursor.getString(cursor.getColumnIndex(DBHelper.KEY_OPERATION_LINK)));
				scanData.setMinutePackBarcode(cursor.getString(cursor.getColumnIndex(DBHelper.KEY_MinutePackBarcode)));
				scanData.setMinutePackNumber(cursor.getString(cursor.getColumnIndex(DBHelper.KEY_MinutePackNumber)));
				scanData.setPackMode(cursor.getString(cursor.getColumnIndex(DBHelper.KEY_PACKMODE)));
				scanData.setGoodsName(cursor.getString(cursor.getColumnIndex(DBHelper.KEY_GoodsName)));
				scanData.setPackName(cursor.getString(cursor.getColumnIndex(DBHelper.KEY_PACKNAME)));
				scanData.setMainGoodsId(cursor.getString(cursor.getColumnIndex(DBHelper.KEY_MAINGOODSID)));
				scanData.setLength(cursor.getString(cursor.getColumnIndex(DBHelper.KEY_Length)));
				scanData.setWidth(cursor.getString(cursor.getColumnIndex(DBHelper.KEY_Width)));
				scanData.setHeight(cursor.getString(cursor.getColumnIndex(DBHelper.KEY_Height)));
				scanData.setWeight(cursor.getString(cursor.getColumnIndex(DBHelper.KEY_Weight)));
				scanData.setV3(cursor.getString(cursor.getColumnIndex(DBHelper.KEY_V3)));
				scanData.setCharge_Ton(cursor.getString(cursor.getColumnIndex(DBHelper.KEY_CHARGE_TON)));
				scanData.setMessage(cursor.getString(cursor.getColumnIndex(DBHelper.KEY_Message)));
				scanData.setMarkPic(cursor.getString(cursor.getColumnIndex(DBHelper.KEY_MarkPic)));
				scanData.setCargoPic(cursor.getString(cursor.getColumnIndex(DBHelper.KEY_CargoPic)));
				scanData.setMarkFile(cursor.getString(cursor.getColumnIndex(DBHelper.KEY_MarkFile)));
				scanData.setCargoFile(cursor.getString(cursor.getColumnIndex(DBHelper.KEY_CargoFile)));
				scanData.setAbnormalPic(cursor.getString(cursor.getColumnIndex(DBHelper.KEY_AbnormalPic)));
				scanData.setAbnormalFile(cursor.getString(cursor.getColumnIndex(DBHelper.KEY_AbnormalFile)));
				scanData.setReturnedCargoCause(cursor.getString(cursor.getColumnIndex(DBHelper.KEY_ReturnedCargoCause)));
				scanData.setReturnedCargoLink(cursor.getString(cursor.getColumnIndex(DBHelper.KEY_ReturnedCargoLink)));
				scanData.setLibraryNumber(cursor.getString(cursor.getColumnIndex(DBHelper.KEY_LIBRARY_NUMBER)));
				scanData.setLibraryAdamin(cursor.getString(cursor.getColumnIndex(DBHelper.KEY_LIBRARY_ADMIN)));
				scanData.setLength_Old(cursor.getString(cursor.getColumnIndex(DBHelper.KEY_Length_Old)));
				scanData.setWidth_Old(cursor.getString(cursor.getColumnIndex(DBHelper.KEY_Width_Old)));
				scanData.setHeight_Old(cursor.getString(cursor.getColumnIndex(DBHelper.KEY_Height_Old)));
				scanData.setSaillings_name(cursor.getString(cursor.getColumnIndex(DBHelper.Key_SAILLINGS_NAME)));
				scanData.setSaillings(cursor.getString(cursor.getColumnIndex(DBHelper.KEY_Saillings)));
				scanData.setShipping_space(cursor.getString(cursor.getColumnIndex(DBHelper.KEY_SHIPPING_SPACE)));
				scanData.setFlight(cursor.getString(cursor.getColumnIndex(DBHelper.KEY_AIR_FLIGHT)));
				scanData.setVoyage(cursor.getString(cursor.getColumnIndex(DBHelper.KEY_VOYAGE)));
				scanData.setContainer_no(cursor.getString(cursor.getColumnIndex(DBHelper.KEY_CONTAINERNO)));
				scanData.setFreighter_name(cursor.getString(cursor.getColumnIndex(DBHelper.KEY_FREIGHTERNAME)));
				scanData.setSailing_no(cursor.getString(cursor.getColumnIndex(DBHelper.KEY_SAILINGNO)));
				scanData.setCompany(cursor.getString(cursor.getColumnIndex(DBHelper.KEY_COMPANY)));
				scanData.setCompany_id(cursor.getString(cursor.getColumnIndex(DBHelper.KEY_COMPANYID)));
				scanData.setTelPerson(cursor.getString(cursor.getColumnIndex(DBHelper.KEY_TelPerson)));
				scanData.setAbnormalLink(cursor.getString(cursor.getColumnIndex(DBHelper.Key_AbnormalLink)));
				scanData.setScaned(cursor.getString(cursor.getColumnIndex(DBHelper.KEY_Scaned)));
				scanData.setAbnormalCause(cursor.getString(cursor.getColumnIndex(DBHelper.KEY_AbnormalCause)));
				scanData.setReturnedCargoPic(cursor.getString(cursor.getColumnIndex(DBHelper.KEY_ReturnedCargoPic)));
				scanData.setReturnedCargoFile(cursor.getString(cursor.getColumnIndex(DBHelper.KEY_ReturnedCargoFile)));
				scanData.setUploadStatus(cursor.getString(cursor.getColumnIndex(DBHelper.KEY_UploadStatus)));
				scanData.setLink(cursor.getString(cursor.getColumnIndex(DBHelper.KEY_LINK)));
				scanData.setNode_id(cursor.getString(cursor.getColumnIndex(DBHelper.KEY_NODE_ID)));

				list.add(scanData);
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			cursor.close();
		}

		return list;
	}

	/**
	 * 清空表中数据
	 * 
	 * @return
	 */
	public boolean clearTable() {

		boolean flag = false;

		try {

			db = DBHelper.SQLiteDBHelper.getWritableDatabase();
			String sql = "delete from " + DBHelper.TABLE_SCANDATA;
			db.execSQL(sql);

			flag = true;
		} catch (Exception e) {
			e.printStackTrace();
		}

		return flag;
	}

	/**
	 * 根据单号，扫描类型获取ScanData
	 * 
	 * @param hawbId
	 *            单号
	 * @param scanType
	 *            扫描类型
	 * 
	 * @return
	 */

	public ScanData getPic(String hawbId, String scanType) {
		ScanData scanData = null;

		db = DBHelper.SQLiteDBHelper.getReadableDatabase();

		String sql = "select * from " + DBHelper.TABLE_SCANDATA + " where "
				+ DBHelper.KEY_ScanType + "=?";
		Cursor cursor = db.rawQuery(sql, new String[] { hawbId, scanType });

		if (cursor != null) {

			List<ScanData> cursorData = getCursorData(cursor);
			if (cursorData != null && cursorData.size() > 0) {
				scanData = cursorData.get(0);
			}

		}

		return scanData;
	}

	/**
	 * 修改图片
	 * 
	 * @param hawbId
	 *            单号
	 * @param scanType
	 *            扫描类型
	 * @param picType
	 *            图片类型： 异常类型：{@link #ABN_PIC} </br> 退运类型：{@link #RET_PIC} </br>
	 *            唛头类型：{@link #MARK_PIC} </br> 货物类型：{@link #CARGO_PIC}
	 * @param picFile
	 *            图片的路径
	 * @return
	 */
	public boolean upDataPic(String hawbId, String scanType, int picType,
			String picFile) {
		db = DBHelper.SQLiteDBHelper.getWritableDatabase();

		ContentValues values = new ContentValues();
		String whereClause = DBHelper.KEY_ScanType + "=?";

		switch (picType) {
		case ABN_PIC:
			// 异常图片类型
			values.put(DBHelper.KEY_AbnormalPic, picFile);
			break;
		case RET_PIC:
			// 退运图片类型
			values.put(DBHelper.KEY_ReturnedCargoPic, picFile);
			break;
		case MARK_PIC:
			// 唛头图片类型
			values.put(DBHelper.KEY_MarkPic, picFile);
			break;
		case CARGO_PIC:
			// 货物图片类型
			values.put(DBHelper.KEY_CargoPic, picFile);
			break;

		default:
			break;
		}

		db.update(DBHelper.TABLE_SCANDATA, values, whereClause, new String[] {
				hawbId, scanType });

		return false;
	}

	/**
	 * 根据扫描类型，获取查询页面的数据
	 * 
	 * @param scanData
	 *            ScanData
	 * @param scanType
	 *            扫描类型
	 * 
	 * @return
	 */
	public List<ScanData> getInquireScanData(ScanData scanData,
			String scanType, boolean isLibs) {

		db = DBHelper.SQLiteDBHelper.getReadableDatabase();
		String sql = "";
		String[] where = null;
		if (TextUtils.equals(scanType, ScanTypeUtil.start_query)) {
			// 起点
			if (isLibs) {
				sql = "select * from " + DBHelper.TABLE_SCANDATA + " where "
						+ DBHelper.KEY_ScanType + "=?";
				where = new String[] { scanData.getScanType() };
			} else {
				sql = "select * from " + DBHelper.TABLE_SCANDATA + " where "
						+ DBHelper.KEY_ScanType + "=?";
				where = new String[] { scanData.getScanType() };
			}

		} else if (TextUtils.equals(scanType, ScanTypeUtil.land_query)) {
			// 陆运

			sql = "select * from " + DBHelper.TABLE_SCANDATA + " where "
					+ DBHelper.KEY_VehicleNumbers + "=? and "
					+ DBHelper.KEY_ScanType + "=?";
			where = new String[] { scanData.getVehicleNumbers(),
					scanData.getScanType() };

		} else if (TextUtils.equals(scanType, ScanTypeUtil.trans_query)) {
			// 转点
			if (isLibs) {
				sql = "select * from " + DBHelper.TABLE_SCANDATA + " where "
						+ DBHelper.KEY_ScanType + "=?";
				where = new String[] { scanData.getScanType() };
			} else {
				sql = "select * from " + DBHelper.TABLE_SCANDATA + " where "
						+ DBHelper.KEY_ScanType + "=?";
				where = new String[] { scanData.getScanType() };
			}

		} else if (TextUtils.equals(scanType, ScanTypeUtil.rail_query)) {
			// 铁路

			sql = "select * from " + DBHelper.TABLE_SCANDATA + " where "
					+ DBHelper.KEY_VehicleNumbers + "=? and "
					+ DBHelper.KEY_TelPerson + "=? and "
					+ DBHelper.KEY_ScanType + "=?";
			where = new String[] { scanData.getVehicleNumbers(),
					scanData.getTelPerson(), scanData.getScanType() };
		} else if (TextUtils.equals(scanType, ScanTypeUtil.area_query)) {
			// 区域
			if (isLibs) {
				sql = "select * from " + DBHelper.TABLE_SCANDATA + " where "
						+ DBHelper.KEY_ScanType + "=?";
				where = new String[] { scanData.getScanType() };
			} else {
				sql = "select * from " + DBHelper.TABLE_SCANDATA + " where "
						+ DBHelper.KEY_ScanType + "=?";
				where = new String[] { scanData.getScanType() };
			}

		} else if (TextUtils.equals(scanType, ScanTypeUtil.load_query)) {
			// 装卸

			sql = "select * from " + DBHelper.TABLE_SCANDATA + " where "
					+ DBHelper.KEY_ScanType + "=?";
			where = new String[] { scanData.getScanType() };
		} else if (TextUtils.equals(scanType, ScanTypeUtil.ship_query)) {
			// 船运

			sql = "select * from " + DBHelper.TABLE_SCANDATA + " where "
					+ DBHelper.KEY_VehicleNumbers + "=? and "
					+ DBHelper.KEY_Saillings + "=? and "
					+ DBHelper.KEY_ScanType + "=?";
			where = new String[] { scanData.getVehicleNumbers(),
					scanData.getSaillings(), scanData.getScanType() };
		} else if (TextUtils.equals(scanType, ScanTypeUtil.air_query)) {
			// 空运

			sql = "select * from " + DBHelper.TABLE_SCANDATA + " where "
					+ DBHelper.KEY_VehicleNumbers + "=? and "
					+ DBHelper.KEY_ScanType + "=?";
			where = new String[] { scanData.getVehicleNumbers(),
					scanData.getScanType() };
		} else if (TextUtils.equals(scanType, ScanTypeUtil.end_query)) {
			// 终点
			if (isLibs) {
				sql = "select * from " + DBHelper.TABLE_SCANDATA + " where "
						+ DBHelper.KEY_ScanType + "=?";
				where = new String[] { scanData.getScanType() };
			} else {
				sql = "select * from " + DBHelper.TABLE_SCANDATA + " where "
						+ DBHelper.KEY_ScanType + "=?";
				where = new String[] { scanData.getScanType() };
			}

		} else if (TextUtils.equals(scanType, ScanTypeUtil.container_query)) {
			// 集装箱

			sql = "select * from " + DBHelper.TABLE_SCANDATA + " where "
					+ DBHelper.KEY_MinutePackBarcode + "=? and "
					+ DBHelper.KEY_ScanType + "=?";
			where = new String[] { scanData.getMinutePackBarcode(),
					scanData.getScanType() };
		}
		Cursor cursor = db.rawQuery(sql, where);

		return getCursorData(cursor);
	}

	/**
	 * 图片处理
	 * 
	 * @param strPath
	 * @return
	 */
	public String checkPicture(String strPath) {

		StringBuilder sb = new StringBuilder();
		if (strPath == null || strPath.equals("")) {
			return sb.toString();
		}

		Log.v("zd", strPath);
		String[] arrPath = strPath.split(",");
		for (int i = 0; i < arrPath.length; i++) {

			Log.v("pic", arrPath[i] + "");
			Bitmap bitmap = BitmapFactory.decodeFile(arrPath[i]);
			String strBase64 = CommandTools.bitmap2String(bitmap, 80);

			if (i < arrPath.length - 1) {
				sb.append(strBase64);
				sb.append(";");
			} else {
				sb.append(strBase64);
			}

		}

		return sb.toString();
	}

	/**
	 * 删除数据
	 */
	public void deleteData(ScanData scanData) {

		db = DBHelper.SQLiteDBHelper.getWritableDatabase();

		String sql = "delete from " + DBHelper.TABLE_SCANDATA
				+ " where CacheId = '" + scanData.getCacheId() + "'"
				+ " and ScanType = '" + scanData.getScanType() + "'";

		db.execSQL(sql);
	}

	/**
	 * 数据上传后更新UploadStatus字段 标记数据已上传 这里需要注意总条码变化 不是总箱号
	 * 
	 * @param scanData
	 */
	public void updateScandata(ScanData scanData) {

		db = DBHelper.SQLiteDBHelper.getWritableDatabase();
		try {
			String sql = "UPDATE  " + DBHelper.TABLE_SCANDATA
					+ " SET UploadStatus = '1'  WHERE " + DBHelper.KEY_CacheID
					+ " = '" + scanData.getCacheId() + "'" + " and "
					+ DBHelper.KEY_ScanType + " = '" + scanData.getScanType()
					+ "'" + "";
			db.execSQL(sql);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

}
