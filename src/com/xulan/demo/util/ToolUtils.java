package com.xulan.demo.util;

public class ToolUtils {

	private static long lastClickTime;
	private final static int SPACE_TIME = 1000;

	public static void initLastClickTime() {
		lastClickTime = 0;
	}

	public synchronized static boolean isDoubleClick() {
		
		long currentTime = System.currentTimeMillis();
		boolean isClick2;
		if (currentTime - lastClickTime >
		SPACE_TIME) {
			isClick2 = false;
		} else {
			isClick2 = true;
		}
		lastClickTime = currentTime;
		return isClick2;
	}
}
