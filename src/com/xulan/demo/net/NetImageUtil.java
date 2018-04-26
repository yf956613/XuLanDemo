package com.xulan.demo.net;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.widget.ImageView;

import com.xulan.demo.R;
import com.xulan.demo.net.AsyncNetTask.OnPostExecuteListener;
import com.xulan.demo.service.CommonService;
import com.xulan.demo.util.CommandTools;
import com.xulan.demo.util.Logs;

/**
 * @author HeXiuHui
 *
 */
public class NetImageUtil 
{
	private static class ImageTaskTag
	{
		public ImageView iv;
		public String strUrl;
	}
	
	/******************************************************************************************/
	/*                                         浠庣綉缁滆姹傚浘锟?                                                                                                      */
	/******************************************************************************************/
	public static LoadPicNetTask requestImageData(final String strUrl, final ImageView iv, final int nWidth, final int nHeight)
	{
		iv.setTag(R.id.tag_imageview_url, strUrl);

		ImageTaskTag tag = new ImageTaskTag();
		tag.iv = iv;
		tag.strUrl = strUrl;

		Logs.v("Test", strUrl);
		OnPostExecuteListener onPostExecuteListener = new OnPostExecuteListener(){
			public void onPostExecute(AsyncNetTask t, NetTaskResult result, Object tag)
			{
				Logs.e("Test", "requestImageData() onPostExecute result.m_nResultCode=" + result.m_nResultCode);

				if (result.m_nResultCode == 0)
				{
					LoadPicResult lpr = (LoadPicResult) result;

					ImageTaskTag itt = (ImageTaskTag) tag;

					// 鏍￠獙鎺т欢鏄惁宸叉敼锟?
					if (!itt.strUrl.equals((String)itt.iv.getTag(R.id.tag_imageview_url)))
					{
						Logs.e("Test", "requestImageData() onPostExecute 鎺т欢宸叉敼锟?");
						return;
					}
					
					boolean bKeepRatio = (itt.iv.getTag(R.id.tag_image_keep_ratio) != null);

					Bitmap bm = lpr.mBitmap;

					if (lpr.mBitmap == null)
					{
						//LoadPicParams lpp = new LoadPicParams();
						//lpp.m_strURL = strUrl;

						bm = ImageCache.get(strUrl, nWidth, nHeight, bKeepRatio);
					}

					if (bm == null)
					{
						Logs.e("Test", "requestImageData() onPostExecute bm==null");
						return;
					}

					if(!bKeepRatio)
					{
						itt.iv.setScaleType(ImageView.ScaleType.FIT_XY);
						itt.iv.setBackgroundColor(0);
					}
					
					Logs.e("Test", "requestImageData() onPostExecute setImageBitmap");
					itt.iv.setImageBitmap(bm);
				}
			}
		};

		LoadPicNetTask task = CommonService.getPicture(onPostExecuteListener, tag, strUrl);
		return task;
	}

	/******************************************************************************************/
	/* 							          璁剧疆ImageView鐨勫浘锟?											  */
	/******************************************************************************************/
	public static void setImageViewBitmap(	Context context,
											ImageView iv, 
											String strPicUri, 
											int nDefaultIconId,
											int nWidth,
											int nHeight)
	{
		if (strPicUri.startsWith("http:"))
		{
			if (ImageCache.contains(strPicUri))
			{
				setImageViewCachedBitmap(context, iv, strPicUri, nDefaultIconId, nWidth, nHeight);
			}
			else
			{
				if(nDefaultIconId > 0)
				{
					// 设为圆角
					Drawable drawable = context.getResources().getDrawable(nDefaultIconId);
					iv.setImageDrawable(drawable);
				}
				requestImageData(strPicUri, iv, nWidth, nHeight);
			}
		}
		else
		{
			if(nDefaultIconId > 0)
			{
				if(iv != null) {
					// 设为圆角
					Drawable drawable = context.getResources().getDrawable(nDefaultIconId);
					iv.setImageDrawable(drawable);
				}
			}
		}
	}
	
	/******************************************************************************************/
	/* 							          璁剧疆鏈湴宸茬紦瀛樼殑鍥剧墖鍒癐mageView 								  */
	/******************************************************************************************/	
	public static void setImageViewCachedBitmap(Context context,
												ImageView iv, 
												String strPicUri, 
												int nDefaultIconId,
												int nWidth,
												int nHeight)
	{
			boolean bKeepRatio = (iv.getTag(R.id.tag_image_keep_ratio) != null);
		
		Bitmap bm = ImageCache.get(strPicUri, nWidth, nHeight, bKeepRatio);
		
		// 设为圆角
		if (bm != null)
		{
			if(!bKeepRatio)
			{
				iv.setScaleType(ImageView.ScaleType.FIT_XY);
				iv.setBackgroundColor(0);
			}
			iv.setImageBitmap(bm);
			bm = null;
		}
		else
		{
			if(nDefaultIconId > 0)
			{
				// 设为圆角
				Drawable drawable = context.getResources().getDrawable(nDefaultIconId);
				iv.setImageDrawable(drawable);
			}
		}
	}
}