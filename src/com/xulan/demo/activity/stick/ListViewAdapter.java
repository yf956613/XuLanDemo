package com.xulan.demo.activity.stick;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import android.content.Context;
import android.graphics.Color;
import android.text.Spannable;
import android.text.SpannableStringBuilder;
import android.text.style.ForegroundColorSpan;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Filter;
import android.widget.TextView;

import com.xulan.demo.R;
import com.xulan.demo.data.ScanData;

public class ListViewAdapter extends BaseAdapter {
	private NameFilter mNameFilter;
	private List<ScanData> mArrayList, copyList;
	private List<ScanData> mFilteredArrayList;
	private LayoutInflater mLayoutInflater;
	public static String searchContent;

	public ListViewAdapter(Context context, List<ScanData> arrayList) {
		mArrayList = arrayList;
		mLayoutInflater = LayoutInflater.from(context);
		mFilteredArrayList = new ArrayList<ScanData>();
		// copyList是暂存原来所用的数据，当筛选内容为空时，显示所有数据，并且必须new 一个对象，
		// 而不能copyList=arrayList,这样的话当arrayList改变时copyList也就改变了
		copyList = new ArrayList<ScanData>();
		
//		copyList.addAll(arrayList);
	}

	@Override
	public int getCount() {
		if (mArrayList == null) {
			return 0;
		} else {
			return (mArrayList.size());
		}

	}

	@Override
	public Object getItem(int position) {
		if (mArrayList == null) {
			return null;
		} else {
			return mArrayList.get(position);
		}
	}

	@Override
	public long getItemId(int position) {
		return position;
	}
	
	public void changCopyList(List<ScanData> arrayList) {
		copyList.clear();
		copyList.addAll(arrayList);
	}

	public void changeList(List<ScanData> arrayList) {
		mArrayList = arrayList;
	}
	
	public List<ScanData> getDataList() {
		return mArrayList;
	}

	public Filter getFilter() {
		if (mNameFilter == null) {
			mNameFilter = new NameFilter();
		}
		return mNameFilter;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		ViewHolder viewHolder = null;
		if (convertView == null) {
			convertView = mLayoutInflater.inflate(R.layout.stick_item, null);
			viewHolder = new ViewHolder();
			viewHolder.textView1 = (TextView) convertView.findViewById(R.id.stick_tv1);
			viewHolder.textView2 = (TextView) convertView.findViewById(R.id.stick_tv2);
			viewHolder.textView3 = (TextView) convertView.findViewById(R.id.stick_tv3);
			viewHolder.textView4 = (TextView) convertView.findViewById(R.id.stick_tv4);
			viewHolder.textView10 = (TextView) convertView.findViewById(R.id.stick_tv10);
			viewHolder.textView11 = (TextView) convertView.findViewById(R.id.stick_tv11);
			convertView.setTag(viewHolder);
		} else {
			viewHolder = (ViewHolder) convertView.getTag();
		}

		if (mArrayList != null) {
			ScanData info = mArrayList.get(position);
			viewHolder.textView1.setText(position + 1 + "");
			viewHolder.textView2.setText(info.getPackBarcode());
			viewHolder.textView3.setText(info.getPackNumber());
			viewHolder.textView4.setText(info.getGoodsName());
			viewHolder.textView10.setText(info.getScanUser());
			viewHolder.textView11.setText(info.getScanTime());
			if (searchContent != null) {
				if (info.getPackNumber().indexOf(searchContent) != -1) {
					SpannableStringBuilder style = new SpannableStringBuilder(info.getPackNumber());
					style.setSpan(
							new ForegroundColorSpan(Color.parseColor("#00cc00")),
							info.getPackNumber().indexOf(searchContent),
							info.getPackNumber().indexOf(searchContent)+ searchContent.length(),
							Spannable.SPAN_EXCLUSIVE_INCLUSIVE); // 设置指定位置文字的颜色
//					viewHolder.textView.setText(style);
				}
			}
		}

		return convertView;
	}

	private class ViewHolder {
		TextView textView1;
		TextView textView2;
		TextView textView3;
		TextView textView4;
		TextView textView10;
		TextView textView11;
	}

	// 异步过滤数据，避免数据多耗时长堵塞主线程
	class NameFilter extends Filter {
		// 执行筛选
		@Override
		protected FilterResults performFiltering(CharSequence charSequence) {
			searchContent = charSequence.toString();
			FilterResults filterResults = new FilterResults();
			if (charSequence == null || charSequence.length() == 0) {
//				mFilteredArrayList = copyList;
				mFilteredArrayList.clear();
				mFilteredArrayList.addAll(copyList);
			} else {
				mFilteredArrayList.clear();
				for (int i = 0; i < copyList.size(); i++) {
					String code = copyList.get(i).getPackNumber();
					if (code.contains(charSequence)) {
						mFilteredArrayList.add(copyList.get(i));
					}
				}
			}
			
			filterResults.values = mFilteredArrayList;
			return filterResults;
		}

		// 筛选结果
		@Override
		protected void publishResults(CharSequence arg0, FilterResults results) {
			mArrayList = (List<ScanData>) results.values;
			if (mArrayList.size() > 0) {
				notifyDataSetChanged();
			} else {
				notifyDataSetInvalidated();
			}

		}
	}

}