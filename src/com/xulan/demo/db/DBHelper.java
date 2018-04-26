package com.xulan.demo.db;

import java.util.List;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

/**
 * 数据库操作
 *
 * 表中字段修改后版本号 +1
 *
 * @author yxx
 *
 * 2015-12-3
 */
public final class DBHelper {

	public static SQLiteDBOpenHelper SQLiteDBHelper;
	public static final String DATABASE_NAME = "XuLan.db";// 旭蘭国际

	public static final int DATABASE_VERSION = 9;// 数据库版本号

	public static final String TABLE_SCANDATA = "ScanData";// 扫描表
	
	public static final String TABLE_PICTURE = "Picture";// 图片表

	/**
	 * 扫描表
	 */
	//陆运装货，卸货
	public static final String KEY_CacheID = "CacheId";//CacheId任务单号
	public static final String KEY_TASKNAME = "taskName";  //任务名称
	public static final String KEY_TASKID = "taskId"; // 任务id
	public static final String KEY_ScanType = "ScanType";//扫描类型
	public static final String KEY_ScanTime = "ScanTime";//扫描时间
	public static final String KEY_SCANUSER = "ScanUser";// 操作员
	public static final String KEY_CREATETIME = "createTime";
	public static final String KEY_PACKBARCODE = "packBarcode"; //包装条码
	public static final String KEY_PACKNUMBER = "packNumber"; //包装号码
	public static final String KEY_VehicleNumbers = "VehicleNumbers";//车牌号
	public static final String KEY_TRAIN = "train";  //车次
	public static final String KEY_DRIVERPHONE = "deiverPhone";  //司机手机号
	public static final String KEY_Memo = "Memo";  //备注、说明
	public static final String KEY_LOGIN_ID = "loginId";  //登录人id
	public static final String KEY_LOGIN_NAME = "loginName"; //登录人姓名
	public static final String KEY_PlanCount = "PlanCount";//载入数
	public static final String KEY_PracticalCount = "PracticalCount";//扫描数
	
	//陆运-单件录入
	public static final String KEY_OPERATION_LINK = "operationLink";  //操作环节
	public static final String KEY_MinutePackBarcode = "MinutePackBarcode";//分箱单条码
	public static final String KEY_MinutePackNumber = "MinutePackNumber";//分箱单号码
	public static final String KEY_PACKMODE = "packMode"; //包装方式
	public static final String KEY_GoodsName = "GoodsName";//品名
	public static final String KEY_PACKNAME = "PackName";//包装名称
	public static final String KEY_MAINGOODSID = "MainGoodsId";// 商品id
	public static final String KEY_Length = "Length";//长
	public static final String KEY_Width = "Width";//宽
	public static final String KEY_Height = "Height";//高
	public static final String KEY_Weight = "Weight";//重量
	public static final String KEY_V3 = "v3"; //体积
	public static final String KEY_CHARGE_TON = "Charge_Ton";// 计费吨
	public static final String KEY_Message = "Message";//物流信息
	public static final String KEY_MarkPic = "MarkPic";//唛头照片
	public static final String KEY_CargoPic = "CargoPic";//货物照片
	public static final String KEY_MarkFile = "MarkFile";//唛头照片路径
	public static final String KEY_CargoFile = "CargoFile";//货物照片路径
	
	public static final String KEY_Length_Old = "Length_Old";// 原长
	public static final String KEY_Width_Old = "Width_Old";// 原宽
	public static final String KEY_Height_Old = "Height_Old";// 原高
	
	//陆运异常
	public static final String KEY_AbnormalPic = "AbnormalPic";//异常图片
	public static final String KEY_AbnormalFile = "AbnormalFile";//异常图片路径
	
	//陆运-退运
	public static final String KEY_ReturnedCargoCause = "ReturnedCargoCause";//退运原因
	public static final String KEY_ReturnedCargoLink = "ReturnedCargoLink";//退运环节
	
	//货场入库
	public static final String KEY_LIBRARY_NUMBER = "libraryNumber"; //库位号
	public static final String KEY_LIBRARY_ADMIN = "libraryAdamin"; //库管员
	
	//海运装货，卸货
	public static final String Key_SAILLINGS_NAME = "Saillings_name";  //船名
	public static final String KEY_Saillings = "Saillings";//航次
	public static final String KEY_SHIPPING_SPACE = "shipping_space";  //舱位
	
	//空运装货，卸货
	public static final String KEY_AIR_FLIGHT = "flight";  //航班
	public static final String KEY_VOYAGE = "voyage";  //航次
	
	//铁运装货，卸货
	public static final String KEY_WAGON_NUMBER = "wagonNumber"; //车皮编号
	
	// 集装箱，做箱，拆箱
	public static final String KEY_CONTAINERNO = "container_no";
	public static final String KEY_FREIGHTERNAME = "freighter_name";
	public static final String KEY_SAILINGNO = "sailing_no";
	
	//装卸，装货，卸货
	public static final String KEY_COMPANY = "company";  //装卸公司
	public static final String KEY_COMPANYID = "company_id"; //装卸公司id
	public static final String KEY_TelPerson = "TelPerson";//联系人
	public static final String Key_AbnormalLink = "AbnormalLink";//异常节点
	public static final String KEY_Scaned = "Scaned";//是否扫描
	public static final String KEY_AbnormalCause = "AbnormalCause";//异常原因
	public static final String KEY_ReturnedCargoPic = "ReturnedCargoPic";//退运图片
	public static final String KEY_ReturnedCargoFile = "ReturnedCargoFile";//退运图片路径
	public static final String KEY_UploadStatus = "UploadStatus";//上传状态，默认为0
	
	public static final String KEY_LINK = "link";//环节
	public static final String KEY_NODE_ID = "node_id";  //节点id
	
	private static final String CREATE_SCAN_TABLE = "create table "
			+ TABLE_SCANDATA
			+ " ("
			+ KEY_CacheID + " nvarchar(100), "
			+ KEY_TASKNAME + " nvarchar(50), "
			+ KEY_TASKID + " nvarchar(50), "
			+ KEY_ScanType + " nvarchar(50), "
			+ KEY_ScanTime + " nvarchar(50), "
			+ KEY_SCANUSER + " nvarchar(50), "
			+ KEY_CREATETIME + " nvarchar(50), "
			+ KEY_PACKBARCODE + " nvarchar(50), "
			+ KEY_PACKNUMBER + " nvarchar(50), "
			+ KEY_VehicleNumbers + " nvarchar(50), "
			+ KEY_TRAIN + " nvarchar(50), "
			+ KEY_DRIVERPHONE + " nvarchar(50), "
			+ KEY_Memo + " nvarchar(50), "
			+ KEY_LOGIN_ID + " nvarchar(50), "
			+ KEY_LOGIN_NAME + " nvarchar(50), "
			+ KEY_PlanCount + " nvarchar(50), "
			+ KEY_PracticalCount + " nvarchar(50), "
			+ KEY_OPERATION_LINK + " nvarchar(50), "
			+ KEY_MinutePackBarcode + " nvarchar(50), "
			+ KEY_MinutePackNumber + " nvarchar(50), "
			+ KEY_PACKMODE + " nvarchar(50), "
			+ KEY_GoodsName + " nvarchar(50), "
			+ KEY_PACKNAME + " nvarchar(50), "
			+ KEY_MAINGOODSID + " nvarchar(50), "
			+ KEY_Length + " nvarchar(50), "
			+ KEY_Width + " nvarchar(50), "
			+ KEY_Height + " nvarchar(50), "
			+ KEY_Weight + " nvarchar(50), "
			+ KEY_V3 + " nvarchar(50), "
			+ KEY_CHARGE_TON + " nvarchar(50), "
			+ KEY_Message + " nvarchar(50), "
			+ KEY_MarkPic + " nvarchar(50), "
			+ KEY_CargoPic + " nvarchar(50), "
			+ KEY_MarkFile + " nvarchar(50), "
			+ KEY_CargoFile + " nvarchar(50), "
			+ KEY_AbnormalPic + " nvarchar(50), "
			+ KEY_AbnormalFile + " nvarchar(50), "
			+ KEY_ReturnedCargoCause + " nvarchar(50), "
			+ KEY_ReturnedCargoLink + " nvarchar(50), "
			+ KEY_LIBRARY_NUMBER + " nvarchar(50), "
			+ KEY_LIBRARY_ADMIN + " nvarchar(50), "
			+ KEY_Length_Old + " nvarchar(50), "
			+ KEY_Width_Old + " nvarchar(50), "
			+ KEY_Height_Old + " nvarchar(50), "
			+ KEY_NODE_ID + " nvarchar(50), "
			+ Key_SAILLINGS_NAME + " nvarchar(50), "
			+ KEY_Saillings + " nvarchar(50), "
			+ KEY_SHIPPING_SPACE + " nvarchar(50), "
			+ KEY_AIR_FLIGHT + " nvarchar(50), "
			+ KEY_VOYAGE + " nvarchar(50), "
			+ KEY_WAGON_NUMBER + " nvarchar(50), "
			+ KEY_CONTAINERNO + " nvarchar(50), "
			+ KEY_FREIGHTERNAME + " nvarchar(50), "
			+ KEY_SAILINGNO + " nvarchar(50), "
			+ KEY_COMPANY + " nvarchar(50), "
			+ KEY_COMPANYID + " nvarchar(50), "
			+ KEY_TelPerson + " nvarchar(50), "
			+ Key_AbnormalLink + " nvarchar(50), "
			+ KEY_Scaned + " nvarchar(50), "
			+ KEY_AbnormalCause + " nvarchar(50), "
			+ KEY_ReturnedCargoPic + " nvarchar(50), "
			+ KEY_UploadStatus + " nvarchar(50), "
			+ KEY_LINK + " nvarchar(50), "
			+ KEY_ReturnedCargoFile + " nvarchar(50)"+ ");";
	
	/**
	 * 图片表
	 */
	public static final String KEY_PIC_CacheID = "CacheId";//CacheId任务单号
	public static final String KEY_PICNAME = "picFile";  //图片地址
	
	private static final String CREATE_TABLE_PICTURE = "create table "
			+ TABLE_PICTURE
			+ " ("
			+ KEY_PIC_CacheID + " nvarchar(100), "
			+ KEY_PICNAME + " nvarchar(100)"+ ");";
	
	public DBHelper(Context context) {
		SQLiteDBHelper = new SQLiteDBOpenHelper(context);
	}

	public class SQLiteDBOpenHelper extends SQLiteOpenHelper {

		public SQLiteDBOpenHelper(Context context) {
			super(context, DATABASE_NAME, null, DATABASE_VERSION);
		}

		@Override
		public void onCreate(SQLiteDatabase db) {
			Log.v("upload", "onCreate");

			db.execSQL(CREATE_SCAN_TABLE);
			db.execSQL(CREATE_TABLE_PICTURE);
			Log.i("hexiuhui----", "CREATE_SCAN_TABLE===" + CREATE_SCAN_TABLE);
		}

		@Override
		public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

			Log.v("upload", "onUpgrade");
			try {

				db.execSQL("DROP TABLE IF EXISTS " + TABLE_SCANDATA);
				db.execSQL("DROP TABLE IF EXISTS " + TABLE_PICTURE);

				onCreate(db);

				if(oldVersion != newVersion) {
					Log.v("zd", "数据升级成功");
				} else {
					Log.v("zd", "数据库升级失败");
				}

			} catch(Exception e) {
				e.printStackTrace();
				Log.v("zd", "数据库更新异常");
			}
		}
	}
}
