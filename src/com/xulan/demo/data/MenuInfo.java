package com.xulan.demo.data;

import java.io.Serializable;

/** 
 * 
 * 
 * @author yxx
 *
 * @date 2015-12-30 下午8:46:41
 * 
 */
public class MenuInfo implements Serializable{

	private static final long serialVersionUID = 1L;
	private String menu;
	private Object activity;
	private int drawable;
	private String scanType;

	private String taskNum;//任务单号
	private String actionName;//动作名称
	private String actionId;//动作id
	
	private boolean isEnable;//是否可操作
	private int linkNum;//操作环节

	public int getLinkNum() {
		return linkNum;
	}
	public void setLinkNum(int linkNum) {
		this.linkNum = linkNum;
	}
	public String getMenu() {
		return menu;
	}
	public void setMenu(String menu) {
		this.menu = menu;
	}
	public Object getActivity() {
		return activity;
	}
	public void setActivity(Object activity) {
		this.activity = activity;
	}
	public int getDrawable() {
		return drawable;
	}
	public void setDrawable(int drawable) {
		this.drawable = drawable;
	}
	public String getScanType() {
		return scanType;
	}
	public void setScanType(String scanType) {
		this.scanType = scanType;
	}
	public String getTaskNum() {
		return taskNum;
	}
	public void setTaskNum(String taskNum) {
		this.taskNum = taskNum;
	}
	public String getActionName() {
		return actionName;
	}
	public void setActionName(String actionName) {
		this.actionName = actionName;
	}
	public String getActionId() {
		return actionId;
	}
	public void setActionId(String actionId) {
		this.actionId = actionId;
	}
	public boolean isEnable() {
		return isEnable;
	}
	public void setEnable(boolean isEnable) {
		this.isEnable = isEnable;
	}


}
