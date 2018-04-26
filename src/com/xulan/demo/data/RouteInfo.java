package com.xulan.demo.data;

import java.io.Serializable;

/**
 * @author hexiuhui
 */
public class RouteInfo implements Serializable {
	private int route_type;
	private String route_name;
	private String route_id;

	public int getRoute_type() {
		return route_type;
	}

	public void setRoute_type(int route_type) {
		this.route_type = route_type;
	}

	public String getRoute_name() {
		return route_name;
	}

	public void setRoute_name(String route_name) {
		this.route_name = route_name;
	}

	public String getRoute_id() {
		return route_id;
	}

	public void setRoute_id(String route_id) {
		this.route_id = route_id;
	}
}
