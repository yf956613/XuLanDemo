package com.xulan.demo.util;

import android.app.ActivityManager;
import android.content.Context;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.ImageLoader;
import com.android.volley.toolbox.ImageRequest;
import com.android.volley.toolbox.Volley;

public class RequestManager {
	private static RequestQueue mRequestQueue;
	private static ImageLoader mImageLoader;
	public RequestManager() {
		// TODO Auto-generated constructor stub
	}
	
	public static void initVolley(Context context){
		mRequestQueue = Volley.newRequestQueue(context);
		//get the app's available memory  given by system,note that its unit is MB 
		int memClass = ((ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE))
				.getMemoryClass();
		
		//or you can get the memory value by this 
		//memClass = (int) Runtime.getRuntime().maxMemory() ; 
		// Use 1/8th of the available memory for this memory cache.
		int cacheSize = 1024 * 1024 * memClass / 8;
		mImageLoader = new ImageLoader(mRequestQueue, new BitmapLruCache(cacheSize));
	}
	
	/**
	 * get mRequestQueue
	 */
	public static RequestQueue getRequestQueue(){
		if(mRequestQueue != null){
			return mRequestQueue;
		}else{
			throw new IllegalStateException("the RequestQueue is null and not be initialized! ");
		}
	}
	
	/**
	 * add a request
	 */
	public static void addRequest(ImageRequest request){
		if(mRequestQueue != null){
			mRequestQueue.add(request);
		}else{
			throw new IllegalStateException("the RequestQueue is null and not be initialized! ");
		}
	}
	
	/**
	 * cancel all the Request 
	 */
	public static void cancelAll(Object tag){
		if(mRequestQueue != null){
			if (tag == null) {
	            throw new IllegalArgumentException("Cannot cancelAll with a null tag");
	        }else{
	        	mRequestQueue.cancelAll(tag);	
	        }
		}else{
			throw new IllegalStateException("the RequestQueue is null and not be initialized! ");
		}
	}
	
	/**
	 * get imageLoader 
	 */
	public static ImageLoader getImageLoader(){
		if(mImageLoader != null){
			return mImageLoader;
		}else{
			throw new IllegalStateException("the ImageLoader is null and not be initialized! ");
		}
	}
}
