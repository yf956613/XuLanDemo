package com.xulan.demo.util;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import org.json.JSONException;
import org.json.JSONObject;

import com.xulan.demo.MyApplication;
import com.xulan.demo.activity.freightyard.StorageActivity;
import com.xulan.demo.data.ScanData;
import com.xulan.demo.net.AsyncNetTask;
import com.xulan.demo.net.LoadTextResult;
import com.xulan.demo.net.NetTaskResult;
import com.xulan.demo.net.AsyncNetTask.OnPostExecuteListener;
import com.xulan.demo.service.UserService;
import com.xulan.demo.view.CustomProgress;

import android.util.Log;
import android.widget.Toast;

import jxl.Cell;
import jxl.Sheet;
import jxl.Workbook;

/** 
 * Excel文件读取
 * 
 * @author yxx
 *
 * @date 2017-11-15 上午10:23:30
 * 
 */
public class ExcelUtil {

	public static void readExcel() {  

		try {  

			/** 
			 * 后续考虑问题,比如Excel里面的图片以及其他数据类型的读取 
			 * 
			 * 1--13
			 * 15、16、19、21
			 * 
			 * 8
			 **/  
//			InputStream is = new FileInputStream("mnt/sdcard/test1.xls");  

			Workbook book = Workbook.getWorkbook(new File("mnt/sdcard/test21.xls"));  
			book.getNumberOfSheets();  
			// 获得第一个工作表对象  
			Sheet sheet = book.getSheet(0);  
			int Rows = sheet.getRows();  
			int Cols = sheet.getColumns();  
 
			MyApplication.m_link_num = 1;
			MyApplication.m_node_num = 1;
			MyApplication.m_physic_link_num = 1;
			MyApplication.m_projectId = "5A00F8D6-0978-4518-B36E-8EA689686EA7";
			MyApplication.m_nodeId = "BF39D723-C497-4DB1-AC1A-3C03A78B01A3";
			MyApplication.m_scan_type = Constant.SCAN_TYPE_STORAGE;
			MyApplication.m_flag = 0;
			MyApplication.m_userName = "lili-nc";
			MyApplication.m_userID = "6FCC4705-D95E-47F2-AF16-4092C1A99633";

			List<ScanData> dataList = new ArrayList<ScanData>();

			for (int i = 0; i < Rows; i++) {  

				//每一行进行操作
				ScanData scanData = new ScanData();
				scanData.setCacheId(CommandTools.getUUID());

				scanData.setTaskId(null);
				scanData.setMainGoodsId(sheet.getCell(7, i).getContents().toString());
				scanData.setPackNumber(sheet.getCell(8, i).getContents().toString());
				scanData.setPackBarcode(sheet.getCell(9, i).getContents().toString());
				scanData.setTaskName(sheet.getCell(10, i).getContents().toString());
				scanData.setLibraryNumber(sheet.getCell(10, i).getContents().toString());
				scanData.setLibraryAdamin(sheet.getCell(11, i).getContents().toString());
//				scanData.setScanTime(sheet.getCell(13, i).getContents().toString());
				
				String strDate = sheet.getCell(13, i).getContents().toString();
				if(strDate.contains("上午")){
					strDate = strDate.replace("上午", "AM");
				}else if(strDate.contains("下午")){
					strDate = strDate.replace("下午", "PM");
				}
				
				scanData.setScanTime(strDate);

				dataList.add(scanData);
			}  
			// 得到第一列第一行的单元格  
			Cell cell1 = sheet.getCell(0, 0);  
			String result = cell1.getContents();  
			System.out.println(result);  
			book.close();  

			OnPostExecuteListener listener = new OnPostExecuteListener() {
				@Override
				public void onPostExecute(AsyncNetTask t, NetTaskResult result, Object tag) {
					if (result.m_nResultCode == 0) {
						LoadTextResult mresult = (LoadTextResult) result;
						try {
							JSONObject jsonObj = new JSONObject(mresult.m_strContent);

							int success = jsonObj.getInt("success");
							String message = jsonObj.getString("message");
							CommandTools.showToast(message);

						} catch (JSONException e) {
							e.printStackTrace();
						}
					} else {
					}
				}
			};

			UserService.upload(dataList, null, Constant.SCAN_TYPE_STORAGE, listener, null);
		} catch (Exception e) {  
			e.printStackTrace();
			System.out.println(e);  
		}  
	}  
}
