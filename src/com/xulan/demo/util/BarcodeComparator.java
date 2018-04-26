package com.xulan.demo.util;

import java.util.Comparator;

import com.xulan.demo.MyApplication;
import com.xulan.demo.data.ScanData;

/** 
 * 按照pack_barcode、pack_no排序
 * 
 * @author yxx
 *
 * @date 2017-6-8 下午1:42:29
 * 
 */
public class BarcodeComparator implements Comparator<ScanData> {

	/* 
	 * 
	 * (non-Javadoc)
	 * @see java.util.Comparator#compare(java.lang.Object, java.lang.Object)
	 */
	public int compare(ScanData o1, ScanData o2) {

		if (o1.getPackBarcode().equals("@")
				|| o2.getPackBarcode().equals("#")) {
			return -1;
		} else if (o1.getPackBarcode().equals("#")
				|| o2.getPackBarcode().equals("@")) {
			return 1;
		} else {

			if(Constant.SORT_DATA_NUMBER % 2 == 0){

				if(Constant.SORT_DATA_TYPE == 0){

					if(MyApplication.m_scan_type.equals(Constant.SCAN_TYPE_PACK)){
						return o1.getMinutePackBarcode().compareTo(o2.getMinutePackBarcode());
					}else{
						return o1.getPackBarcode().compareTo(o2.getPackBarcode());
					}
				}else{

					if(MyApplication.m_scan_type.equals(Constant.SCAN_TYPE_PACK)){
						return o1.getMinutePackNumber().compareTo(o2.getMinutePackNumber());
					}else{
						return o1.getPackNumber().compareTo(o2.getPackNumber());
					}

				}

			}else{

				if(Constant.SORT_DATA_TYPE == 0){

					if(MyApplication.m_scan_type.equals(Constant.SCAN_TYPE_PACK)){
						return o2.getMinutePackBarcode().compareTo(o1.getMinutePackBarcode());
					}else{
						return o2.getPackBarcode().compareTo(o1.getPackBarcode());
					}
				}else{
					if(MyApplication.m_scan_type.equals(Constant.SCAN_TYPE_PACK)){
						return o2.getMinutePackNumber().compareTo(o1.getMinutePackNumber());
					}else{
						return o2.getPackNumber().compareTo(o1.getPackNumber());
					}

				}
			}

		}
	}

}
