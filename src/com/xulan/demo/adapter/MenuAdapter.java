package com.xulan.demo.adapter;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import com.xulan.demo.R;
import com.xulan.demo.data.MenuInfo;

import android.content.Context;
import android.content.Intent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

/**
 * GridView界面适配器
 *
 * @author yxx
 *
 * 2015-12-4
 */
public class MenuAdapter extends BaseAdapter{

	private Context mContext;
	private Intent intent;

	private String[] menuTexts;
	private Object[] menuActivity;
	private int[] menuDrawable;
	private String[] menuScanType;
	private boolean[] menuEnable;

	private String strTitle;

	private List<MenuInfo> dataList = new ArrayList<MenuInfo>();

	/**
	 * @param context 当前上下文
	 * @param onBottomClickListener 
	 * @param arrMenu 小标题
	 * @param arrActivity 对应的activity
	 * @param arrDrawable 背景
	 */
	public MenuAdapter(Context context, List<MenuInfo> list, String title, OnBottomClickListener onBottomClickListener) {

		this.mContext = context;
		this.mListener = onBottomClickListener;

		dataList.clear();
		dataList.addAll(list);

		strTitle = title;

		int len = list.size();
		menuScanType = new String[len];
		menuTexts = new String[len];
		menuActivity = new Object[len];
		menuDrawable = new int[len];
		menuEnable = new boolean[len];

		for(int i=0; i<len; i++){

			menuTexts[i] = list.get(i).getMenu();
			menuActivity[i] = list.get(i).getActivity();
			menuDrawable[i] = list.get(i).getDrawable();
			menuScanType[i] = list.get(i).getScanType();
			menuEnable[i] = list.get(i).isEnable();
		}

	}

	public int getCount() {

		return dataList.size();
	}

	@Override
	public Object getItem(int position) {
		return position;
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	@Override
	public View getView(final int position, View convertView, ViewGroup parent) {

		ViewHolder viewHolder;
		if (convertView == null) {

			convertView = View.inflate(mContext, R.layout.grid_menu_home_item, null);
			viewHolder = new ViewHolder();

			viewHolder.layout = (LinearLayout) convertView.findViewById(R.id.layout_grid_menu);
			viewHolder.btn = (Button) convertView.findViewById(R.id.btn_grid_menu_home_item_img);
			viewHolder.tvTitle = (TextView) convertView.findViewById(R.id.tv_grid_menu_home_item_title);

			convertView.setTag(viewHolder);
		} else {
			viewHolder = (ViewHolder) convertView.getTag();
		}

//		//权限设置
//		if(menuEnable[position] == true){
//			viewHolder.layout.setVisibility(View.VISIBLE);
//		}else{
//			viewHolder.layout.setVisibility(View.GONE);
//		}

		viewHolder.btn.setBackgroundResource(menuDrawable[position]);
		viewHolder.tvTitle.setText(menuTexts[position]);

		viewHolder.btn.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View arg0) {

				if(mListener != null){
					mListener.onBottomClick(arg0, position);
				}
			}
		});

		return convertView;
	}

	public class ViewHolder {

		public LinearLayout layout;
		public Button btn;
		public TextView tvTitle;
	}

	/**
	 * 单击事件监听器
	 */
	private OnBottomClickListener mListener = null;

	public void setOnBottomClickListener(OnBottomClickListener listener) {
		mListener = listener;
	}

	public interface OnBottomClickListener {
		void onBottomClick(View v, int position);
	}
}
