package com.xulan.demo.data;

import java.io.Serializable;

/**
 * @author hexiuhui
 */
public class NoteInfo implements Serializable {
	
	private String note_id;
	private String note_name;
	private String note_type;
	
	private boolean link_1;//装货
	private boolean link_2;//抵达
	private boolean link_3;//卸货
	
	public String getNote_id() {
		return note_id;
	}

	public void setNote_id(String note_id) {
		this.note_id = note_id;
	}

	public String getNote_name() {
		return note_name;
	}

	public void setNote_name(String note_name) {
		this.note_name = note_name;
	}

	public String getNote_type() {
		return note_type;
	}

	public void setNote_type(String note_type) {
		this.note_type = note_type;
	}

	public boolean isLink_1() {
		return link_1;
	}

	public void setLink_1(boolean link_1) {
		this.link_1 = link_1;
	}

	public boolean isLink_2() {
		return link_2;
	}

	public void setLink_2(boolean link_2) {
		this.link_2 = link_2;
	}

	public boolean isLink_3() {
		return link_3;
	}

	public void setLink_3(boolean link_3) {
		this.link_3 = link_3;
	}
}
