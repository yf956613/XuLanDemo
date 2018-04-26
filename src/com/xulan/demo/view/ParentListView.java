package com.xulan.demo.view;

import android.content.Context;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.widget.ListView;

public class ParentListView extends ListView {

	public ParentListView(Context context) {
		super(context);
		// TODO Auto-generated constructor stub
	}

	public ParentListView(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);

	}

	public ParentListView(Context context, AttributeSet attrs) {
		super(context, attrs);
	}


	@Override
	public boolean onInterceptTouchEvent(MotionEvent ev) {
		// TODO Auto-generated method stub
		return super.onInterceptTouchEvent(ev);
	}
	
}
