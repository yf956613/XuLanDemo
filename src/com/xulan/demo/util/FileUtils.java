package com.xulan.demo.util;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.CursorLoader;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Log;

/** 
 * 文件相关操作
 * 
 * @author yxx
 *
 * @date 2015-12-17 下午5:03:10
 * 
 */
@SuppressLint("NewApi")
public class FileUtils {

	/**
	 * 判断手机是否有SD卡。
	 * 
	 * @return 有SD卡返回true，没有返回false。
	 */
	public static boolean existSDCard() {

		return Environment.MEDIA_MOUNTED.equals(Environment
				.getExternalStorageState());
	}

	/**
	 * 初始化文件
	 * 主目录为XuLan每次拍照按照当天日期为文件夹存在里面
	 * 初始化时如果不是当天的文件夹则删除
	 */
	public void initFile(){

		try{

			File folder = new File(Constant.SDPATH + CommandTools.getTimeDate());
			if(!folder.exists()){
				return;
			}

			File folder2 = new File(Environment.getExternalStorageDirectory()
					.getAbsolutePath() + "/" + Constant.FolderName + "/");
			String[] tempList = folder2.list();

			Log.v("zd", "folder = " + folder.getPath());

			for(int i=0; i<tempList.length; i++){

				String str1 = folder2.getPath() + "/" + tempList[i];

				Log.v("zd", "name = " + str1);
				if(str1.equals(folder.getPath()) == false){

					Log.v("zd", "删除文件夹------" + str1);
					delFolder(str1);
				}
			}
		}catch(Exception e){
			e.printStackTrace();
		}

	}

	/**
	 * 删除指定文件夹下所有文件
	 * @param path 文件夹完整绝对路径
	 * @return
	 * @return
	 */
	public boolean delAllFile(String path) {

		boolean bea = false;
		File file = new File(path);
		if (!file.exists()) {
			return bea;
		}
		if (!file.isDirectory()) {
			return bea;
		}
		String[] tempList = file.list();
		File temp = null;
		for (int i = 0; i < tempList.length; i++) {
			if (path.endsWith(File.separator)) {
				temp = new File(path + tempList[i]);
			}else{
				temp = new File(path + File.separator + tempList[i]);
			}
			if (temp.isFile()) {
				temp.delete();
			}
			if (temp.isDirectory()) {
				delAllFile(path+"/"+ tempList[i]);//先删除文件夹里面的文件
				delFolder(path+"/"+ tempList[i]);//再删除空文件夹
				bea = true;
			}
		}
		return bea;
	}

	/**
	 * 删除文件夹
	 * @param folderPath 文件夹完整绝对路径
	 * @return
	 */
	public void delFolder(String folderPath) {

		try {
			delAllFile(folderPath); //删除完里面所有内容
			String filePath = folderPath;
			filePath = filePath.toString();
			java.io.File myFilePath = new java.io.File(filePath);
			myFilePath.delete(); //删除空文件夹
		}catch (Exception e) {
			Log.v("zd", "删除文件夹操作出错");
		}
	}

	public static void saveBitmap(Bitmap bm, String picName) {

		try {
			String strFolder = Constant.SDPATH + CommandTools.getTimeDate();

			File folder = new File(strFolder); 
			if (!folder.exists()) {
				folder.mkdirs();
			}

			File file = new File(folder.getAbsolutePath(), picName + ".JPEG"); 

			FileOutputStream out = new FileOutputStream(file);
			bm.compress(Bitmap.CompressFormat.JPEG, 90, out);
			out.flush();
			out.close();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	public static String saveBitmap2(Context context, Bitmap bm, String picName) {

		File file = null;
		try {
			String strFolder = Constant.SDPATH + CommandTools.getTimeDate();

			File folder = new File(strFolder); 
			if (!folder.exists()) {
				folder.mkdirs();
			}

			file = new File(folder.getAbsolutePath(), picName + ".JPEG");

			FileOutputStream out = new FileOutputStream(file);
			bm.compress(Bitmap.CompressFormat.JPEG, 90, out);
			out.flush();
			out.close();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		// 其次把文件插入到系统图库
	    try {
	        MediaStore.Images.Media.insertImage(context.getContentResolver(),
					file.getAbsolutePath(), picName + ".JPEG", null);
	    } catch (FileNotFoundException e) {
	        e.printStackTrace();
	    }
	    // 最后通知图库更新
	    context.sendBroadcast(new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE, Uri.parse("file://" + file.getPath())));
		return file.getPath();
	}
	
	//根据uri获取图片路径
	public static String getPathFromUri(Context mContext,Uri contentUri){
	   String[] proj = { MediaStore.Images.Media.DATA };
	   CursorLoader loader = new CursorLoader(mContext, contentUri, proj, null, null, null);
	   Cursor cursor = loader.loadInBackground();
	   int column_index = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
	   cursor.moveToFirst();
	   return cursor.getString(column_index);
	}

	/**
	 * 根据图片名称删除图片
	 * 当天文件夹
	 * @param fileName
	 */
	public static void delFile(String fileName){

		File file = new File(Constant.SDPATH + CommandTools.getTimeDate() + "/" + fileName);

		Log.v("file", "图片路径 = " + file.getPath());
		if(file.isFile()){
			file.delete();
			Log.v("file", "图片删除成功");
		}

	}

	/**
	 * 直接写入到SD卡根目录
	 * 
	 * @param data
	 */
	public static void writeToFile(String data) {

		
		String strFolder = Constant.SDPATH;

		File folder = new File(strFolder); 
		if (!folder.exists()) {
			folder.mkdirs();
		}
		
		File file = new File(folder.getAbsolutePath(), "file.txt");//根目录下
		
		BufferedWriter bw = null;
		try {
			FileOutputStream fos = new FileOutputStream(file.getAbsoluteFile(),true);//true 为添加 不覆盖
			bw = new BufferedWriter(new OutputStreamWriter (fos, "UTF-8"));
			bw.write(data);
			bw.flush();
			bw.close();
			
			Log.v("zd", "文件保存成功： " + file.getAbsolutePath());
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	/**
	 * 以行为单位读取文件，常用于读面向行的格式化文件
	 */
	public static String readFileByLines(String fileName) {

		File file = new File(Environment.getExternalStorageDirectory(), "file.txt");//根目录下
		//		File file = new File(fileName);
		BufferedReader reader = null;
		StringBuilder sb = new StringBuilder();
		try {

			System.out.println("以行为单位读取文件内容，一次读一整行：");

			//中文乱码处理，UTF-8与GB2312
			//reader = new BufferedReader(new FileReader(file));
			InputStreamReader isr = new InputStreamReader(new FileInputStream(file), "UTF-8");
			reader = new BufferedReader(isr);

			String tempString = null;
			int line = 1;
			// 一次读入一行，直到读入null为文件结束
			while ((tempString = reader.readLine()) != null) {
				// 显示行号
				System.out.println("line " + line + ": " + tempString);
				sb.append(tempString);
				line++;
			}
			
			reader.close();
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			if (reader != null) {
				try {
					reader.close();
				} catch (IOException e1) {
				}
			}
		}

		return sb.toString();
	}

}
