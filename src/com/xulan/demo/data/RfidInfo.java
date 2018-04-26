package com.xulan.demo.data;

/** 
 * RFID对象
 * 
 * @author yxx
 *
 * @date 2017-7-20 下午2:05:43
 * 
 */
public class RfidInfo {

	private String id;//卡号
	private String pack_barcode;//对应的货物条码
	private int link_num;
	
	public String getId() {
		return id;
	}
	public void setId(String id) {
		this.id = id;
	}
	public String getPack_barcode() {
		return pack_barcode;
	}
	public void setPack_barcode(String pack_barcode) {
		this.pack_barcode = pack_barcode;
	}
	public int getLink_num() {
		return link_num;
	}
	public void setLink_num(int link_num) {
		this.link_num = link_num;
	}
	
	
}
