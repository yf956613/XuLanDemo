package com.xulan.demo.util;

import android.os.Environment;

public class Constant {

	public static int MAX_PHOTO_COUNT = 5;//最多拍照数量
	public static String userName = "";//登录用户名

	public static final int SCAN_DATA = 0x0001;
	public static final int SCAN_DATA_RFID = 0x0011;//rfid扫描
	public static final int SELECT_TASK = 0x0002;//任务名称选择
	public static final int SELECT_COMPANY = 0x0003;//装卸公司选择
	public static final int CAPTURE_BILLCODE = 0x0004; //扫描条码
	public static final int CHECK_RFID_DATA = 0x0005;//检查RFID数据

	public static String appKey = "hgjkbiuytfghj7865rtyfghvbnjhiuo908";

	//APP升级检测网址
//	public static String UPDATEURL = "http://47.92.77.121:8888/xl_appupdate/checkNew?os=android&type=app&ver=1.0&tag=normal&appname=xulandemo";
	public static String URL = "http://192.168.3.4:8080/";
	
	//APP升级检测网址---本地测试
	public static String UPDATEURL = "http://192.168.16.1:8080/xl_appupdate/checkNew?os=android&type=app&ver=1.0&tag=normal&appname=xulandemo";

	//		public static String TEST_URL = "http://192.168.34.101:8080/";//服务器地址
	//		public static String TEST_URL = "http://www.netcargo.cn:8888/datatest-service-pro/";//服务器地址
	//		public static String TEST_URL = "http://zdkj-sh-tech-1.imwork.net:17238/datatest-service/";//服务器地址--上海

	public static String TEST_URL = "http://www.netcargo.cn:8888/datatest-service-pre/";//阿里云-测试库
	public static String FORMAL_URL = "http://www.netcargo.cn:8888/datatest-service/";//阿里云-正式库

	public static String UPDATE_URL = "";//APP更新地址

	public static String FolderName = "XuLan";//文件夹名称
	public static final String SDPATH = Environment.getExternalStorageDirectory().getPath() + "/XuLan/";//图片存储路径

	public static String NODE_ID = "";//动作节点
	public static String SCAN_TYPE = "";
	public static int uploadSize = 0;//未上传数量

	public static double latitude = 0;//当前纬度
	public static double longitude = 0;//当前经度

	public static int SORT_DATA_NUMBER = 0;//排序类型，奇数-升序，偶数-降序
	public static int SORT_DATA_TYPE = 0;//0-pack_barcode,1-pack_no

	public static int LIKE_TYPE_LAND = 1; //陆运

	public static String SCAN_TYPE_LAND = "land"; //陆运
	public static String SCAN_TYPE_SEA = "sea"; //海运
	public static String SCAN_TYPE_AIR = "air"; //空运
	public static String SCAN_TYPE_RAILEAY = "railway"; //铁运
	public static String SCAN_TYPE_LOAD = "load"; //装卸
	public static String SCAN_TYPE_STORAGE = "storage"; //货场
	public static String SCAN_TYPE_CONTAINER = "container"; //集装箱

	public static String SCAN_TYPE_VERIFY = "verify"; //校验
	public static String SCAN_TYPE_SCALE = "scale"; //打尺
	public static String SCAN_TYPE_PACK = "pack"; //包装
	public static String SCAN_TYPE_STRAP = "strap"; //绑扎
	public static String SCAN_TYPE_OFFLINE = "offline"; //下线
	public static String SCAN_TYPE_INSTALL = "install"; //安装
	public static String SCAN_TYPE_TIEMAI = "stick"; //贴唛

	public static String SCAN_TYPE_IMPORTCAR = "import_car"; //进口车

	public static String SCAN_TYPE_SINGLE = "single"; //单件录入
	public static String SCAN_TYPE_INQUERY = "inquery"; //查询
	public static String SCAN_TYPE_BACK = "back"; //退运
	public static String SCAN_TYPE_ABNORMAL = "abnormal"; //异常
	public static String SCAN_TYPE_PHOTO = "photo"; //拍照
	public static String SCAN_TYPE_MENU = "menu"; //菜单栏

	public static String SP_LOGIN_NAME = "SP_LOGIN_NAME";
	public static String SP_LOGIN_PWD = "SP_LOGIN_PWD";
	public static String SP_SERVER_URL = "SP_SERVER_URL";
}
