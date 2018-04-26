package com.xulan.demo.data;

import java.io.Serializable;

/**
 * @author hexiuhui
 */
public class ProjectInfo implements Serializable {

	private static final long serialVersionUID = 1L;
	private String project_id;
	private String project_name;
	private String project_code;

	public String getProject_id() {
		return project_id;
	}

	public void setProject_id(String project_id) {
		this.project_id = project_id;
	}

	public String getProject_name() {
		return project_name;
	}

	public void setProject_name(String project_name) {
		this.project_name = project_name;
	}

	public String getProject_code() {
		return project_code;
	}

	public void setProject_code(String project_code) {
		this.project_code = project_code;
	}
}
