package com.xulan.demo.data;


/**
 * @author hexiuhui
 */
public class TaskInfo {
	
	private String task_name;
	private String task_code;

	private String car_plate;
	private String car_count;
	
	public String shipping_space = ""; // 舱位
	
	public String startCity; // 起运城市
	public String endCity; // 目的城市
	public String TelPerson = "";// 联系人电话
	public String person = ""; // 联系人

	public int link_num;//操作环节

	public String getShipping_space() {
		return shipping_space;
	}

	public void setShipping_space(String shipping_space) {
		this.shipping_space = shipping_space;
	}

	public String getTask_code() {
		return task_code;
	}

	public void setTask_code(String task_code) {
		this.task_code = task_code;
	}

	public String getTask_name() {
		return task_name;
	}

	public void setTask_name(String task_name) {
		this.task_name = task_name;
	}

	public String getStartCity() {
		return startCity;
	}

	public void setStartCity(String startCity) {
		this.startCity = startCity;
	}

	public String getEndCity() {
		return endCity;
	}

	public void setEndCity(String endCity) {
		this.endCity = endCity;
	}

	public String getTelPerson() {
		return TelPerson;
	}

	public void setTelPerson(String telPerson) {
		TelPerson = telPerson;
	}

	public String getPerson() {
		return person;
	}

	public void setPerson(String person) {
		this.person = person;
	}

	public String getCar_plate() {
		return car_plate;
	}

	public void setCar_plate(String car_plate) {
		this.car_plate = car_plate;
	}

	public String getCar_count() {
		return car_count;
	}

	public void setCar_count(String car_count) {
		this.car_count = car_count;
	}

}
