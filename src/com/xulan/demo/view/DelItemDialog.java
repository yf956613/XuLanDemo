package com.xulan.demo.view;

import com.xulan.demo.R;

import android.app.Dialog;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.TextView;

/**
 * ListView长按删除列表对话框
 *
 * @author yxx
 *
 * 2015-8-5
 */
public class DelItemDialog{

	public static boolean isShow = false;
	static Dialog dialog;

	// 弹窗结果回调函数
	public static abstract class DelDialogCallback {
		public abstract void callback(boolean result);
	}

	public DelItemDialog(Context context){


	}

	/**
	 * 弹出窗口
	 * @param context
	 * @param str
	 * @return
	 */
	public static void showMyDialog(Context context, String str, final DelDialogCallback fcCallback){

		if(dialog != null){
			dialog.dismiss();
			dialog = null;
		}

		dialog = new Dialog(context, R.style.dialog);
		LayoutInflater inflater = dialog.getLayoutInflater();
		View layout = inflater.inflate(R.layout.dialog_del_msg, null);
		TextView tv = (TextView) layout.findViewById(R.id.textView_delete);

		dialog.setContentView(layout);

		tv.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub
				if (fcCallback != null) {
					dialog.dismiss();
					isShow = false;
					fcCallback.callback(true);
				}
			}
		});

		dialog.setCancelable(true);
		dialog.setCanceledOnTouchOutside(true);

		dialog.show();
		isShow = true;
	}

	/**
	 * 关闭窗口
	 */
	public static void closeDialog(){

		DelItemDialog.isShow = false;
		if(dialog != null){
			dialog.dismiss();
		}
	}

}
