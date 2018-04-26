package com.xulan.demo.pdascan;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;

public class ParaSave {
	
	public static void saveSymbology(Context context, String symbologys){
		SharedPreferences shared = context.getSharedPreferences("symbology", Context.MODE_PRIVATE) ;
		Editor editor = shared.edit() ;
		editor.putString("sym", symbologys) ;
		editor.commit() ;
	}
	
	
	public static String getSymbology(Context context){
		SharedPreferences shared = context.getSharedPreferences("symbology", Context.MODE_PRIVATE) ;
		return shared.getString("sym", "") ;
	}

}
