package com.xulan.demo.data;

public class EditSmsInfo {

	public String toTen; // 唯一值

	public String location; // 货位号

	public String phone; // 手机号

	public String billCode; // 运单号

	public String express_name; // 快递公司名字

	public String express_code; // 快递公司编号

	public String express_url; // 快递公司图片

	public String time; // 时间

	public String sign_state; // 0未签收 1已签收
	
	public String sign_name;  //签收人

	public String upload_state; // 上传 1-等待 2-成功 3-失败

	public int type;// 0 未选中 1选中

	public int ShowType; // 1 隐藏 2 显示

	public String templateName; // 短信模版名称
	
	public String templateId;  //短信模版ID
	
	public String smsCount;   //短信发送次数
	
	public int beVirtual;
	
	public String billcodeState;  //运单状态
	
	public String getBillcodeState() {
		return billcodeState;
	}

	public void setBillcodeState(String billcodeState) {
		this.billcodeState = billcodeState;
	}

	public String getSign_name() {
		return sign_name;
	}

	public void setSign_name(String sign_name) {
		this.sign_name = sign_name;
	}

	public int getBeVirtual() {
		return beVirtual;
	}

	public void setBeVirtual(int beVirtual) {
		this.beVirtual = beVirtual;
	}

	public String getTime() {
		return time;
	}

	public void setTime(String time) {
		this.time = time;
	}

	public String getSmsCount() {
		return smsCount;
	}

	public void setSmsCount(String smsCount) {
		this.smsCount = smsCount;
	}

	public String getTemplateName() {
		return templateName;
	}

	public void setTemplateName(String templateName) {
		this.templateName = templateName;
	}

	public String getTemplateId() {
		return templateId;
	}

	public void setTemplateId(String templateId) {
		this.templateId = templateId;
	}

	/**
	 * @return the showType
	 */
	public int getShowType() {
		return ShowType;
	}

	/**
	 * @param showType
	 *            the showType to set
	 */
	public void setShowType(int showType) {
		ShowType = showType;
	}

	public EditSmsInfo() {
		super();
	}

	public int getType() {
		return type;
	}

	public void setType(int type) {
		this.type = type;
	}

	public String getExpress_url() {
		return express_url;
	}

	public void setExpress_url(String express_url) {
		this.express_url = express_url;
	}

	public String getSign_state() {
		return sign_state;
	}

	public void setSign_state(String sign_state) {
		this.sign_state = sign_state;
	}

	public String getToTen() {
		return toTen;
	}

	public void setToTen(String toTen) {
		this.toTen = toTen;
	}

	public String getUpload_state() {
		return upload_state;
	}

	public void setUpload_state(String upload_state) {
		this.upload_state = upload_state;
	}

	public String getExpress_name() {
		return express_name;
	}

	public void setExpress_name(String express_name) {
		this.express_name = express_name;
	}

	public String getExpress_code() {
		return express_code;
	}

	public void setExpress_code(String express_code) {
		this.express_code = express_code;
	}

	public String getLocation() {
		return location;
	}

	public void setLocation(String location) {
		this.location = location;
	}

	public String getPhone() {
		return phone;
	}

	public void setPhone(String phone) {
		this.phone = phone;
	}

	public String getBillCode() {
		return billCode;
	}

	public void setBillCode(String billCode) {
		this.billCode = billCode;
	}

}
