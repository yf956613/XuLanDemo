package com.xulan.demo.util;

import java.util.ArrayList;
import java.util.List;

import android.content.Context;

import com.xulan.demo.MyApplication;
import com.xulan.demo.R;
import com.xulan.demo.activity.action.AbnormalActivity;
import com.xulan.demo.activity.action.ActionPhotoActivity;
import com.xulan.demo.activity.action.BackScanActivity;
import com.xulan.demo.activity.action.DoBoxScanActivity;
import com.xulan.demo.activity.action.InqueryActivity;
import com.xulan.demo.activity.action.OffLineScanActivity;
import com.xulan.demo.activity.action.SingleScanActivity;
import com.xulan.demo.activity.action.StrapScanActivity;
import com.xulan.demo.activity.action.TaskListActivity;
import com.xulan.demo.activity.action.land.LandActivity;
import com.xulan.demo.activity.action.load.LoadUnloadActivity;
import com.xulan.demo.activity.air.AirInstallActivity;
import com.xulan.demo.activity.carline.InStorageActivity;
import com.xulan.demo.activity.freightyard.StorageActivity;
import com.xulan.demo.activity.install.InstallActivity;
import com.xulan.demo.activity.pack.PackActivity;
import com.xulan.demo.activity.ruler.RulerActivity;
import com.xulan.demo.activity.stick.StickActivity;
import com.xulan.demo.activity.test.TestActivity;
import com.xulan.demo.activity.trains.TrainsInstallActivity;
import com.xulan.demo.data.MenuInfo;
import com.xulan.demo.shipping.ShippingActivity;

/** 
 * 根据节点选择动作，根据返回动作，去掉不需要的(没权限)
 * 
 * @author yxx
 *
 * @date 2015-12-30 下午8:49:24
 * 
 */
public class MenuInfoUtil {

	private String[] menuTexts;
	private Object[] menuActivity;
	private int[] menuDrawable;
	private String[] menuScanType;
	private boolean[] menuIsEnable;
	private Context mContext;

	public static List<String> linkList = new ArrayList<String>();

	/**
	 * 根据名称获取动作相关的信息，标题，图标，类
	 * @param dataList
	 * @param strTitle
	 * @return
	 */
	public List<MenuInfo> getMenuList(List<MenuInfo> dataList, String strTitle, Context context){
		mContext = context;
		if (strTitle.contains("陆运")) {
			initLandMenu();
		} else if (strTitle.contains("货场")) {
			initStorageMenu();
		} else if (strTitle.contains("铁运")) {
			initRailMenu();
		} else if (strTitle.contains("装卸")) {
			initLoadingMenu();
		} else if (strTitle.contains("海运")) {
			initShipMenu();
		} else if (strTitle.contains("空运")) {
			initAirMenu();
		} else if (strTitle.contains("打尺")) {
			initScaleMenu();
		} else if (strTitle.contains("检验")) {
			initVerifyMenu();
		} else if (strTitle.contains("安装")) {
			initInstallMenu();
		} else if (strTitle.contains("包装")) {
			initPackMenu();
		} else if (strTitle.contains("集装箱")) {
			initContainerMenu();
		} else if (strTitle.contains("绑扎")) {
			initStrapMenu();
		} else if (strTitle.contains("下线")) {
			initOfflineMenu();
		} else if (strTitle.contains("贴唛")) {
			initStickMenu();
		}else if (strTitle.contains("进口车")) {
			initImportCarMenu();
		}

		if (menuTexts == null || menuTexts.length == 0) {
			return dataList;
		}

		dataList.clear();
		for (int i = 0; i < menuTexts.length; i++) {

			//权限控制，不可操作的排除掉
			if(menuIsEnable[i] == false){
				continue;
			}

			MenuInfo info = new MenuInfo();
			info.setMenu(menuTexts[i]);
			info.setActivity(menuActivity[i]);
			info.setDrawable(menuDrawable[i]);
			info.setScanType(menuScanType[i]);
			info.setActionName(menuTexts[i]);
			info.setEnable(menuIsEnable[i]);

			info.setLinkNum(i+1);//操作环节

			dataList.add(info);
		}

		return dataList;
	}

	/**
	 * 铁路界面
	 */
	private void initRailMenu(){

		menuIsEnable = new boolean[]{
				MyApplication.m_userInfo.isLink_1(), MyApplication.m_userInfo.isLink_2(),
				MyApplication.m_userInfo.isLink_3(), true,
				true, true, 
				true, true
		};

		List<Integer> idList = new ArrayList<Integer>();
		idList.add(R.string.loading);
		idList.add(R.string.arrival);
		idList.add(R.string.discharge);
		idList.add(R.string.new_1);
		idList.add(R.string.problemcargo);
		idList.add(R.string.returncargo);
		idList.add(R.string.photo);
		idList.add(R.string.search);
		menuTexts = CommandTools.getStr(idList, mContext);

		menuActivity = new Object[]{
				TrainsInstallActivity.class, TaskListActivity.class,
				TrainsInstallActivity.class, SingleScanActivity.class,
				AbnormalActivity.class, BackScanActivity.class,
				ActionPhotoActivity.class, InqueryActivity.class};

		menuDrawable = new int[]{
				R.drawable.icons_06, R.drawable.icons_08,
				R.drawable.icons_21, R.drawable.icons_22,
				R.drawable.icons_19, R.drawable.icons_20,
				R.drawable.icons_31, R.drawable.icons_32
		};

		menuScanType = new String[]{
				Constant.SCAN_TYPE_RAILEAY, Constant.SCAN_TYPE_RAILEAY, 
				Constant.SCAN_TYPE_RAILEAY, Constant.SCAN_TYPE_SINGLE, 
				Constant.SCAN_TYPE_ABNORMAL, Constant.SCAN_TYPE_BACK, 
				Constant.SCAN_TYPE_PHOTO, Constant.SCAN_TYPE_INQUERY
		};
	}

	/**
	 * 货场
	 */
	private void initStorageMenu(){

		menuIsEnable = new boolean[]{
				MyApplication.m_userInfo.isLink_1(), MyApplication.m_userInfo.isLink_2(),
				true, true,
				true, true, 
				true
		};

		List<Integer> idList = new ArrayList<Integer>();
		idList.add(R.string.in);
		idList.add(R.string.out);
		idList.add(R.string.new_1);
		idList.add(R.string.problemcargo);
		idList.add(R.string.returncargo);
		idList.add(R.string.photo);
		idList.add(R.string.search);
		menuTexts = CommandTools.getStr(idList, mContext);

		menuActivity = new Object[] { StorageActivity.class,
				StorageActivity.class, SingleScanActivity.class,
				AbnormalActivity.class, BackScanActivity.class,
				ActionPhotoActivity.class, InqueryActivity.class };

		menuDrawable = new int[] { R.drawable.icons_10, R.drawable.icons_12,
				R.drawable.icons_22, R.drawable.icons_19, R.drawable.icons_20,
				R.drawable.icons_31, R.drawable.icons_32 };

		menuScanType = new String[] { Constant.SCAN_TYPE_STORAGE,
				Constant.SCAN_TYPE_STORAGE, Constant.SCAN_TYPE_SINGLE, 
				Constant.SCAN_TYPE_ABNORMAL, Constant.SCAN_TYPE_BACK, 
				Constant.SCAN_TYPE_PHOTO, Constant.SCAN_TYPE_INQUERY 
		};

	}

	/**
	 * 陆运界面
	 */
	private void initLandMenu(){

		menuIsEnable = new boolean[]{
				MyApplication.m_userInfo.isLink_1(), MyApplication.m_userInfo.isLink_2(),
				MyApplication.m_userInfo.isLink_3(), true,
				true, true, 
				true, true
		};

		List<Integer> idList = new ArrayList<Integer>();
		idList.add(R.string.loading);
		idList.add(R.string.arrival);
		idList.add(R.string.discharge);
		idList.add(R.string.new_1);
		idList.add(R.string.problemcargo);
		idList.add(R.string.returncargo);
		idList.add(R.string.photo);
		idList.add(R.string.search);
		menuTexts = CommandTools.getStr(idList, mContext);

		menuActivity = new Object[]{
				LandActivity.class, TaskListActivity.class,
				LandActivity.class, SingleScanActivity.class,
				AbnormalActivity.class, BackScanActivity.class,
				ActionPhotoActivity.class, InqueryActivity.class};

		menuDrawable = new int[]{
				R.drawable.icons_06, R.drawable.icons_08,
				R.drawable.icons_21, R.drawable.icons_22,
				R.drawable.icons_19, R.drawable.icons_20,
				R.drawable.icons_31, R.drawable.icons_32
		};

		menuScanType = new String[]{
				Constant.SCAN_TYPE_LAND, Constant.SCAN_TYPE_LAND, 
				Constant.SCAN_TYPE_LAND, Constant.SCAN_TYPE_SINGLE, 
				Constant.SCAN_TYPE_ABNORMAL, Constant.SCAN_TYPE_BACK, 
				Constant.SCAN_TYPE_PHOTO, Constant.SCAN_TYPE_INQUERY
		};

	}

	/**
	 * 包装
	 */
	private void initPackMenu(){

		menuIsEnable = new boolean[]{
				MyApplication.m_userInfo.isLink_1(), true, 
				true
		};

		List<Integer> idList = new ArrayList<Integer>();
		idList.add(R.string.packing_service);
		idList.add(R.string.photo);
		idList.add(R.string.search);
		menuTexts = CommandTools.getStr(idList, mContext);

		menuActivity = new Object[]{
				PackActivity.class,
				ActionPhotoActivity.class, InqueryActivity.class};

		menuDrawable = new int[]{
				R.drawable.icons_30,
				R.drawable.icons_31, R.drawable.icons_32
		};

		menuScanType = new String[]{
				Constant.SCAN_TYPE_PACK, 
				Constant.SCAN_TYPE_PACK, Constant.SCAN_TYPE_PACK
		};

	}

	/**
	 * 安装
	 */
	private void initInstallMenu(){

		menuIsEnable = new boolean[]{
				MyApplication.m_userInfo.isLink_1(), true,
				true, true
		};

		List<Integer> idList = new ArrayList<Integer>();
		idList.add(R.string.assembly);
		idList.add(R.string.photo);
		idList.add(R.string.search);
		menuTexts = CommandTools.getStr(idList, mContext);

		menuActivity = new Object[]{
				InstallActivity.class,
				ActionPhotoActivity.class, InqueryActivity.class};

		menuDrawable = new int[]{
				R.drawable.icons_33,
				R.drawable.icons_31, R.drawable.icons_32
		};

		menuScanType = new String[]{
				Constant.SCAN_TYPE_INSTALL, 
				Constant.SCAN_TYPE_PHOTO, Constant.SCAN_TYPE_INQUERY
		};

	}

	/**
	 * 检验
	 */
	private void initVerifyMenu(){

		menuIsEnable = new boolean[]{
				MyApplication.m_userInfo.isLink_1()
		};

		List<Integer> idList = new ArrayList<Integer>();
		idList.add(R.string.survey_service);
		menuTexts = CommandTools.getStr(idList, mContext);

		menuActivity = new Object[]{
				TestActivity.class
		};

		menuDrawable = new int[]{
				R.drawable.test
		};

		menuScanType = new String[]{
				Constant.SCAN_TYPE_VERIFY
		};

	}

	/**
	 * 打尺
	 */
	private void initScaleMenu() {

		menuIsEnable = new boolean[]{
				MyApplication.m_userInfo.isLink_1()
		};

		List<Integer> idList = new ArrayList<Integer>();
		idList.add(R.string.mesurement);
		menuTexts = CommandTools.getStr(idList, mContext);

		menuActivity = new Object[] {
				RulerActivity.class
		};

		menuDrawable = new int[] {
				R.drawable.scale
		};

		menuScanType = new String[] {
				Constant.SCAN_TYPE_SCALE
		};
	}

	/**
	 * 集装箱界面
	 */
	private void initContainerMenu(){

		menuIsEnable = new boolean[]{
				MyApplication.m_userInfo.isLink_1(), MyApplication.m_userInfo.isLink_2(), true,
				true, true, 
				true, true
		};

		List<Integer> idList = new ArrayList<Integer>();
		idList.add(R.string.containercargo_1);
		idList.add(R.string.containercargo_2);
		idList.add(R.string.new_1);
		idList.add(R.string.problemcargo);
		idList.add(R.string.returncargo);
		idList.add(R.string.photo);
		idList.add(R.string.search);
		menuTexts = CommandTools.getStr(idList, mContext);

		menuActivity = new Object[]{
				DoBoxScanActivity.class, DoBoxScanActivity.class, SingleScanActivity.class,
				AbnormalActivity.class, BackScanActivity.class,
				ActionPhotoActivity.class, InqueryActivity.class};

		menuDrawable = new int[]{
				R.drawable.icons_02, R.drawable.icons_04, R.drawable.icons_22,
				R.drawable.icons_19, R.drawable.icons_20,
				R.drawable.icons_31, R.drawable.icons_32
		};

		menuScanType = new String[]{
				Constant.SCAN_TYPE_CONTAINER, Constant.SCAN_TYPE_CONTAINER, Constant.SCAN_TYPE_SINGLE, 
				Constant.SCAN_TYPE_ABNORMAL, Constant.SCAN_TYPE_BACK, 
				Constant.SCAN_TYPE_PHOTO, Constant.SCAN_TYPE_INQUERY
		};
	}

	/**
	 * 空运界面
	 */
	private void initAirMenu(){

		menuIsEnable = new boolean[]{
				MyApplication.m_userInfo.isLink_1(), MyApplication.m_userInfo.isLink_2(),
				MyApplication.m_userInfo.isLink_3(), true,
				true, true, 
				true, true
		};

		List<Integer> idList = new ArrayList<Integer>();
		idList.add(R.string.loading);
		idList.add(R.string.arrival);
		idList.add(R.string.discharge);
		idList.add(R.string.new_1);
		idList.add(R.string.problemcargo);
		idList.add(R.string.returncargo);
		idList.add(R.string.photo);
		idList.add(R.string.search);
		menuTexts = CommandTools.getStr(idList, mContext);

		menuActivity = new Object[]{
				AirInstallActivity.class, TaskListActivity.class,
				AirInstallActivity.class, SingleScanActivity.class,
				AbnormalActivity.class, BackScanActivity.class,
				ActionPhotoActivity.class, InqueryActivity.class};

		menuDrawable = new int[]{
				R.drawable.icons_06, R.drawable.icons_08,
				R.drawable.icons_21, R.drawable.icons_22,
				R.drawable.icons_19, R.drawable.icons_20,
				R.drawable.icons_31, R.drawable.icons_32
		};

		menuScanType = new String[]{
				Constant.SCAN_TYPE_AIR, Constant.SCAN_TYPE_AIR, 
				Constant.SCAN_TYPE_AIR, Constant.SCAN_TYPE_SINGLE, 
				Constant.SCAN_TYPE_ABNORMAL, Constant.SCAN_TYPE_BACK, 
				Constant.SCAN_TYPE_PHOTO, Constant.SCAN_TYPE_INQUERY
		};

	}

	/**
	 * 装卸界面
	 */
	private void initLoadingMenu(){

		menuIsEnable = new boolean[]{
				MyApplication.m_userInfo.isLink_1(), MyApplication.m_userInfo.isLink_2(),
				true, true,
				true, true, 
				true
		};

		List<Integer> idList = new ArrayList<Integer>();
		idList.add(R.string.loading);
		idList.add(R.string.discharge);
		idList.add(R.string.new_1);
		idList.add(R.string.problemcargo);
		idList.add(R.string.returncargo);
		idList.add(R.string.photo);
		idList.add(R.string.search);
		menuTexts = CommandTools.getStr(idList, mContext);

		menuActivity = new Object[]{
				LoadUnloadActivity.class, LoadUnloadActivity.class,
				SingleScanActivity.class, AbnormalActivity.class,
				BackScanActivity.class, ActionPhotoActivity.class,
				InqueryActivity.class
		};

		menuDrawable = new int[] { R.drawable.icons_06, R.drawable.icons_21,
				R.drawable.icons_22, R.drawable.icons_19, R.drawable.icons_20,
				R.drawable.icons_31, R.drawable.icons_32 };

		menuScanType = new String[]{
				Constant.SCAN_TYPE_LOAD, Constant.SCAN_TYPE_LOAD, 
				Constant.SCAN_TYPE_SINGLE, Constant.SCAN_TYPE_ABNORMAL, 
				Constant.SCAN_TYPE_BACK, Constant.SCAN_TYPE_PHOTO, 
				Constant.SCAN_TYPE_INQUERY
		};

	}

	/**
	 * 船运界面
	 */
	private void initShipMenu() {

		menuIsEnable = new boolean[]{
				MyApplication.m_userInfo.isLink_1(), MyApplication.m_userInfo.isLink_2(),
				MyApplication.m_userInfo.isLink_3(), true,
				true, true, 
				true, true
		};

		List<Integer> idList = new ArrayList<Integer>();
		idList.add(R.string.loading);
		idList.add(R.string.arrival);
		idList.add(R.string.discharge);
		idList.add(R.string.new_1);
		idList.add(R.string.problemcargo);
		idList.add(R.string.returncargo);
		idList.add(R.string.photo);
		idList.add(R.string.search);
		menuTexts = CommandTools.getStr(idList, mContext);

		menuActivity = new Object[]{
				ShippingActivity.class, TaskListActivity.class,
				ShippingActivity.class, SingleScanActivity.class,
				AbnormalActivity.class, BackScanActivity.class,
				ActionPhotoActivity.class, InqueryActivity.class};

		menuDrawable = new int[]{
				R.drawable.icons_06, R.drawable.icons_08,
				R.drawable.icons_21, R.drawable.icons_22,
				R.drawable.icons_19, R.drawable.icons_20,
				R.drawable.icons_31, R.drawable.icons_32
		};

		menuScanType = new String[]{
				Constant.SCAN_TYPE_SEA, Constant.SCAN_TYPE_SEA,
				Constant.SCAN_TYPE_SEA, Constant.SCAN_TYPE_SINGLE, 
				Constant.SCAN_TYPE_ABNORMAL, Constant.SCAN_TYPE_BACK, 
				Constant.SCAN_TYPE_PHOTO, Constant.SCAN_TYPE_INQUERY
		};

	}

	/**
	 * 绑扎界面
	 */
	private void initStrapMenu() {

		menuIsEnable = new boolean[]{
				MyApplication.m_userInfo.isLink_1(), true,
				true, true,
				true
		};


		List<Integer> idList = new ArrayList<Integer>();
		idList.add(R.string.lashing_service);
		idList.add(R.string.new_1);
		idList.add(R.string.problemcargo);
		idList.add(R.string.photo);
		idList.add(R.string.search);
		menuTexts = CommandTools.getStr(idList, mContext);

		menuActivity = new Object[]{
				StrapScanActivity.class, SingleScanActivity.class,
				AbnormalActivity.class, ActionPhotoActivity.class,
				InqueryActivity.class};

		menuDrawable = new int[]{
				R.drawable.icons_23, R.drawable.icons_22,
				R.drawable.icons_19, R.drawable.icons_31,
				R.drawable.icons_32
		};

		menuScanType = new String[]{
				Constant.SCAN_TYPE_STRAP, Constant.SCAN_TYPE_SINGLE, 
				Constant.SCAN_TYPE_ABNORMAL, Constant.SCAN_TYPE_PHOTO, 
				Constant.SCAN_TYPE_INQUERY
		};

	}

	/**
	 * 下线界面
	 */
	private void initOfflineMenu() {
		menuIsEnable = new boolean[] {
				MyApplication.m_userInfo.isLink_1(), true,
				true
		};

		List<Integer> idList = new ArrayList<Integer>();
		idList.add(R.string.produced);
		idList.add(R.string.photo);
		idList.add(R.string.search);
		menuTexts = CommandTools.getStr(idList, mContext);

		menuActivity = new Object[]{
				OffLineScanActivity.class, ActionPhotoActivity.class,
				InqueryActivity.class};

		menuDrawable = new int[]{
				R.drawable.icons_29, R.drawable.icons_31,
				R.drawable.icons_32
		};

		menuScanType = new String[]{
				Constant.SCAN_TYPE_OFFLINE, Constant.SCAN_TYPE_PHOTO,
				Constant.SCAN_TYPE_INQUERY
		};
	}

	/**
	 * 贴唛界面
	 */
	private void initStickMenu() {
		menuIsEnable = new boolean[] {
				true, true
		};

		List<Integer> idList = new ArrayList<Integer>();
		idList.add(R.string.tiemai);
		idList.add(R.string.search);
		menuTexts = CommandTools.getStr(idList, mContext);

		menuActivity = new Object[]{
				StickActivity.class, InqueryActivity.class};

		menuDrawable = new int[]{
				R.drawable.stick, R.drawable.icons_32
		};

		menuScanType = new String[]{
				Constant.SCAN_TYPE_TIEMAI, Constant.SCAN_TYPE_INQUERY
		};
	}

	/**
	 * 进口车界面
	 */
	private void initImportCarMenu() {
		
		menuIsEnable = new boolean[] {
				MyApplication.m_userInfo.isLink_1(), MyApplication.m_userInfo.isLink_2(),
				true, true,
				true, true,
				true
		};

		List<Integer> idList = new ArrayList<Integer>();
		idList.add(R.string.in);
		idList.add(R.string.out);
		idList.add(R.string.new_1);
		idList.add(R.string.photo);
		idList.add(R.string.exceptional);
		idList.add(R.string.return_cargo);
		idList.add(R.string.search);
		menuTexts = CommandTools.getStr(idList, mContext);

		menuActivity = new Object[]{
				InStorageActivity.class, InStorageActivity.class,
				SingleScanActivity.class, ActionPhotoActivity.class,
				AbnormalActivity.class, BackScanActivity.class,
				InqueryActivity.class
		};

		menuDrawable = new int[]{
				R.drawable.stick, R.drawable.icons_32,
				R.drawable.stick, R.drawable.icons_32,
				R.drawable.stick, R.drawable.icons_32,
				R.drawable.stick
		};

		menuScanType = new String[]{
				Constant.SCAN_TYPE_IMPORTCAR, Constant.SCAN_TYPE_IMPORTCAR,
				Constant.SCAN_TYPE_SINGLE, Constant.SCAN_TYPE_PHOTO,
				Constant.SCAN_TYPE_ABNORMAL, Constant.SCAN_TYPE_BACK,
				Constant.SCAN_TYPE_INQUERY
		};
	}
}
