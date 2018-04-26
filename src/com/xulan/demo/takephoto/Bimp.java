package com.xulan.demo.takephoto;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.lang.ref.WeakReference;
import java.util.ArrayList;

import com.xulan.demo.util.CommandTools;
import com.xulan.demo.util.Constant;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.media.ThumbnailUtils;
import android.util.Log;

public class Bimp {
	public static int max = 0;

	public static ArrayList<ImageItem> tempSelectBitmapList = new ArrayList<ImageItem>();   //选择的图片的临时列表

	/**
	 * 初始化图片
	 */
	public static void initPhotoList(String strCacheId){

		try{

			File folder = new File(Constant.SDPATH + CommandTools.getTimeDate());
			if(!folder.exists()){
				return;
			}

			Log.v("zd", "folder = " + folder.getPath());

			String[] tempList3 = folder.list();

			Bimp.tempSelectBitmapList.clear();
			for(int i=0; i<tempList3.length; i++){

				String fileName = folder.getPath() + "/" + tempList3[i];
				Log.v("zd", "file = " + fileName);

				Bitmap bm = getImageThumbnail(fileName, 150, 200);
				ImageItem takePhoto = new ImageItem();
				takePhoto.setImagePath(tempList3[i]);
				takePhoto.setBitmap(bm);

				Log.v("zd", "strCacheId = " + strCacheId);
				if(tempList3[i].contains(strCacheId)){
					Bimp.tempSelectBitmapList.add(takePhoto);
				}
			}

		}catch(Exception e){
			e.printStackTrace();
		}

	}

	/** 
	 * 根据指定的图像路径和大小来获取缩略图 
	 * 此方法有两点好处： 
	 * 1. 使用较小的内存空间，第一次获取的bitmap实际上为null，只是为了读取宽度和高度， 
	 *        第二次读取的bitmap是根据比例压缩过的图像，第三次读取的bitmap是所要的缩略图。 
	 * 2. 缩略图对于原图像来讲没有拉伸，这里使用了2.2版本的新工具ThumbnailUtils，使 
	 *        用这个工具生成的图像不会被拉伸。 
	 * @param imagePath 图像的路径 
	 * @param width 指定输出图像的宽度 
	 * @param height 指定输出图像的高度 
	 * @return 生成的缩略图 
	 */
	public static Bitmap getImageThumbnail(String imagePath, int width, int height) {  
		Bitmap bitmap = null;  
		BitmapFactory.Options options = new BitmapFactory.Options();  
		options.inJustDecodeBounds = true;  
		// 获取这个图片的宽和高，注意此处的bitmap为null  
		bitmap = BitmapFactory.decodeFile(imagePath, options);  
		options.inJustDecodeBounds = false; // 设为 false  
		// 计算缩放比  
		int h = options.outHeight;  
		int w = options.outWidth;  
		int beWidth = w / width;  
		int beHeight = h / height;  
		int be = 1;  
		if (beWidth < beHeight) {  
			be = beWidth;  
		} else {  
			be = beHeight;  
		}  
		if (be <= 0) {  
			be = 1;  
		}  
		options.inSampleSize = be;  
		// 重新读入图片，读取缩放后的bitmap，注意这次要把options.inJustDecodeBounds 设为 false  
		bitmap = BitmapFactory.decodeFile(imagePath, options);  
		// 利用ThumbnailUtils来创建缩略图，这里要指定要缩放哪个Bitmap对象  
		bitmap = ThumbnailUtils.extractThumbnail(bitmap, width, height,  
				ThumbnailUtils.OPTIONS_RECYCLE_INPUT);  
		return bitmap;  
	}

	public static Bitmap revitionImageSize(String path) throws IOException {

		Log.v("pic", "path = " + path);
		BufferedInputStream in = new BufferedInputStream(new FileInputStream(
				new File(path)));
		BitmapFactory.Options options = new BitmapFactory.Options();
		options.inJustDecodeBounds = true;
		BitmapFactory.decodeStream(in, null, options);
		in.close();
		int i = 0;
		Bitmap bitmap = null;
		while (true) {
			if ((options.outWidth >> i <= 1000)
					&& (options.outHeight >> i <= 1000)) {
				in = new BufferedInputStream(
						new FileInputStream(new File(path)));
				options.inSampleSize = (int) Math.pow(2.0D, i);
				options.inJustDecodeBounds = false;
				bitmap = BitmapFactory.decodeStream(in, null, options);
				break;
			}
			i += 1;
		}
		return bitmap;
	}

	/** 
	 * @param path 图片路径 
	 * @param targetSize 缩放后期待的长边(图片长和宽大的那一个边)的长度 
	 * @param targetW 期待的缩放后宽度 
	 * @param targetH 期待的缩放后高度 
	 * @return 
	 */  
	public static Bitmap equalRatioScale(String path,int targetW,int targetH){  
		// 获取option  
		BitmapFactory.Options options = new BitmapFactory.Options();  
		// inJustDecodeBounds设置为true,这样使用该option decode出来的Bitmap是null，  
		// 只是把长宽存放到option中  
		options.inJustDecodeBounds = true;  
		// 此时bitmap为null  
		Bitmap bitmap = BitmapFactory.decodeFile(path, options);  
		int inSampleSize = 1; // 1是不缩放  
		// 计算宽高缩放比例  
		int inSampleSizeW = options.outWidth / targetW;  
		int inSampleSizeH = options.outHeight / targetH;  
		// 最终取大的那个为缩放比例，这样才能适配，例如宽缩放3倍才能适配屏幕，而  
		// 高不缩放就可以，那样的话如果按高缩放，宽在屏幕内就显示不下了  
		if (inSampleSizeW > inSampleSizeH) {   
			inSampleSize = inSampleSizeW;  
		}else {  
			inSampleSize = inSampleSizeH;  
		}  
		// 设置缩放比例  
		options.inSampleSize = inSampleSize;  
		// 一定要记得将inJustDecodeBounds设为false，否则Bitmap为null  
		options.inJustDecodeBounds = false;  
		bitmap = BitmapFactory.decodeFile(path, options);  
		return bitmap;  
	}  

	/** 
	 * @param path 图片路径 
	 * @param targetSize 缩放后期待的长边(图片长和宽大的那一个边)的长度 
	 * @param targetW 期待的缩放后宽度 
	 * @param targetH 期待的缩放后高度 
	 * @return 
	 */  
	public static Bitmap resetBitmap(String path, int targetW,int targetH){  

		Bitmap bitMap = BitmapFactory.decodeFile(path);
		int width = bitMap.getWidth();
		int height = bitMap.getHeight();

		// 设置想要的大小 
		int newWidth = width * 800 / height;
		int newHeight = 800;

		if(width > height){

			newWidth = 800;
			newHeight = height * 800 / width;
		}

		// 计算缩放比例
		float scaleWidth = ((float) newWidth) / width;
		float scaleHeight = ((float) newHeight) / height;

		// 取得想要缩放的matrix参数
		Matrix matrix = new Matrix();
		matrix.postScale(scaleWidth, scaleHeight);

		// 得到新的图片
		bitMap = Bitmap.createBitmap(bitMap, 0, 0, width, height, matrix, true);

		return bitMap;
	}  

	public static Bitmap convertToBitmap(String path, int h) {
		BitmapFactory.Options opts = new BitmapFactory.Options();
		// 设置为ture只获取图片大小
		opts.inJustDecodeBounds = true;
		opts.inPreferredConfig = Bitmap.Config.ARGB_8888;
		// 返回为空
		BitmapFactory.decodeFile(path, opts);
		int width = opts.outWidth;
		int height = opts.outHeight;
		
		int sH = h / height;
		
		int sW = width * sH;
		
		float scaleWidth = 0.f, scaleHeight = 0.f;
		if (width > sW || height > h) {
			// 缩放
			scaleWidth = ((float) width) / sW;
			scaleHeight = ((float) height) / h;
		}
		opts.inJustDecodeBounds = false;
		float scale = Math.max(scaleWidth, scaleHeight);
		opts.inSampleSize = (int)scale;
		WeakReference<Bitmap> weak = new WeakReference<Bitmap>(BitmapFactory.decodeFile(path, opts));
		return Bitmap.createScaledBitmap(weak.get(), sW, h, true);
	}
}