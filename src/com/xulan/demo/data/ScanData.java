package com.xulan.demo.data;

import java.io.Serializable;
import java.util.List;

/**
 * 扫描表基本属性
 * 
 * @author hexiuhui
 * 
 * @date 2017-3-30 上午9:41:14
 * 
 */
public class ScanData implements Serializable {

	public String CacheId = "";// CacheId任务单号

	public String taskName = ""; // 任务名称
	public String taskId = ""; // 任务id

	public String ScanType = "";// 扫描类型
	public String ScanTime = "";// 扫描时间
	public String ScanUser = "";// 操作员
	public String createTime;
	public String packBarcode = ""; // 包装条码
	public String packNumber = ""; // 包装号码
	public String VehicleNumbers = "";// 车牌号
	public String train = ""; // 车次
	public String deiverPhone = ""; // 司机手机号
	public String Memo = ""; // 备注、说明
	public String loginId = ""; // 登录人id
	public String loginName = ""; // 登录人姓名
	public String PlanCount = "";// 载入数
	public String PracticalCount = "";// 扫描数
	public String exception_number = ""; //异常数

	// 陆运-单件录入
	public String operationLink = ""; // 操作环节
	public String packMode = ""; //包装方式
	public String GoodsName = "";// 品名
	public String PackName = "";//包装名称
	public String MainGoodsId = "";// 商品id
	public String goodsNo;   //商品号码
	public String goodsCode;  //商品条码
	public String Length = "";// 长
	public String Width = "";// 宽
	public String Height = "";// 高
	public String Weight = "";// 重量
	public String v3 = ""; //体积
	public String Charge_Ton = "";// 计费吨
	public String Message = "";// 物流信息
	public String markPic = "";// 唛头照片
	public String cargoPic = "";// 货物照片
	public String MarkFile = "";// 唛头照片路径
	public String CargoFile = "";// 货物照片路径
	public String Length_Old = "";// 原长
	public String Width_Old = "";// 原宽
	public String Height_Old = "";// 原高
	
	// 陆运异常

	public String AbnormalPic = "";// 异常图片
	public String AbnormalFile = "";// 异常图片路径

	// 陆运-退运
	public String ReturnedCargoCause = "";// 退运原因
	public String ReturnedCargoLink = "";// 退运环节

	// 货场入库
	public String libraryNumber = ""; // 库位号
	public String libraryAdamin = ""; // 库管员

	// 海运装货，卸货
	public String Saillings_name = ""; // 船名
	public String Saillings = "";// 航次
	public String shipping_space = ""; // 舱位

	// 空运装货，卸货
	public String flight = ""; // 航班
	public String voyage = ""; // 航次

	// 铁运装货，卸货
	public String wagonNumber = ""; // 车皮编号

	// 集装箱，做箱，拆箱
	private String container_no;
	private String freighter_name;
	private String sailing_no;

	// 装卸，装货，卸货
	public String company = ""; // 装卸公司
	public String company_id = ""; //装卸公司id
	public String TelPerson = "";// 联系人
	public String AbnormalLink = "";// 异常节点
	public String Scaned = "0";// 是否扫描
	public String AbnormalCause = "";// 异常原因
	public String ReturnedCargoPic = "";// 退运图片
	public String ReturnedCargoFile = "";// 退运图片路径
	public String UploadStatus = "";// 上传状态，默认为0
	
	//包装
	public String MinutePackBarcode = "";//分箱单条码
	public String MinutePackNumber = "";//分箱单号码

	public List<String> picture;  //拍照照片

	public String link;//环节
	public String node_id; //节点id
	
	public String rfid; //rfid标签号
	public String linkMan;//联系人
	public String linkPhone;//联系电话

	public String getGoodsNo() {
		return goodsNo;
	}

	public void setGoodsNo(String goodsNo) {
		this.goodsNo = goodsNo;
	}

	public String getGoodsCode() {
		return goodsCode;
	}

	public void setGoodsCode(String goodsCode) {
		this.goodsCode = goodsCode;
	}

	public String getNode_id() {
		return node_id;
	}

	public void setNode_id(String node_id) {
		this.node_id = node_id;
	}

	public String getPackMode() {
		return packMode;
	}

	public void setPackMode(String packMode) {
		this.packMode = packMode;
	}

	public String getException_number() {
		return exception_number;
	}

	public void setException_number(String exception_number) {
		this.exception_number = exception_number;
	}

	public String getV3() {
		return v3;
	}

	public void setV3(String v3) {
		this.v3 = v3;
	}

	public String getCharge_Ton() {
		return Charge_Ton;
	}

	public void setCharge_Ton(String charge_Ton) {
		Charge_Ton = charge_Ton;
	}

	public String getCompany_id() {
		return company_id;
	}

	public void setCompany_id(String company_id) {
		this.company_id = company_id;
	}

	public List<String> getPicture() {
		return picture;
	}

	public void setPicture(List<String> picture) {
		this.picture = picture;
	}

	public String getCreateTime() {
		return createTime;
	}

	public void setCreateTime(String createTime) {
		this.createTime = createTime;
	}

	public String getCacheId() {
		return CacheId;
	}

	public void setCacheId(String cacheId) {
		CacheId = cacheId;
	}

	public String getTaskName() {
		return taskName;
	}

	public void setTaskName(String taskName) {
		this.taskName = taskName;
	}

	public String getScanType() {
		return ScanType;
	}

	public void setScanType(String scanType) {
		ScanType = scanType;
	}

	public String getScanTime() {
		return ScanTime;
	}

	public void setScanTime(String scanTime) {
		ScanTime = scanTime;
	}

	public String getPackBarcode() {
		return packBarcode;
	}

	public void setPackBarcode(String packBarcode) {
		this.packBarcode = packBarcode;
	}

	public String getPackNumber() {
		return packNumber;
	}

	public void setPackNumber(String packNumber) {
		this.packNumber = packNumber;
	}

	public String getVehicleNumbers() {
		return VehicleNumbers;
	}

	public void setVehicleNumbers(String vehicleNumbers) {
		VehicleNumbers = vehicleNumbers;
	}

	public String getTrain() {
		return train;
	}

	public void setTrain(String train) {
		this.train = train;
	}

	public String getDeiverPhone() {
		return deiverPhone;
	}

	public void setDeiverPhone(String deiverPhone) {
		this.deiverPhone = deiverPhone;
	}

	public String getMemo() {
		return Memo;
	}

	public void setMemo(String memo) {
		Memo = memo;
	}

	public String getLoginId() {
		return loginId;
	}

	public void setLoginId(String loginId) {
		this.loginId = loginId;
	}

	public String getLoginName() {
		return loginName;
	}

	public void setLoginName(String loginName) {
		this.loginName = loginName;
	}

	public String getPlanCount() {
		return PlanCount;
	}

	public void setPlanCount(String planCount) {
		PlanCount = planCount;
	}

	public String getPracticalCount() {
		return PracticalCount;
	}

	public void setPracticalCount(String practicalCount) {
		PracticalCount = practicalCount;
	}

	public String getOperationLink() {
		return operationLink;
	}

	public void setOperationLink(String operationLink) {
		this.operationLink = operationLink;
	}

	public String getGoodsName() {
		return GoodsName;
	}

	public void setGoodsName(String goodsName) {
		GoodsName = goodsName;
	}

	public String getLength() {
		return Length;
	}

	public void setLength(String length) {
		Length = length;
	}

	public String getWidth() {
		return Width;
	}

	public void setWidth(String width) {
		Width = width;
	}

	public String getHeight() {
		return Height;
	}

	public void setHeight(String height) {
		Height = height;
	}

	public String getWeight() {
		return Weight;
	}

	public void setWeight(String weight) {
		Weight = weight;
	}

	public String getMessage() {
		return Message;
	}

	public void setMessage(String message) {
		Message = message;
	}

	public String getMarkPic() {
		return markPic;
	}

	public void setMarkPic(String markPic) {
		this.markPic = markPic;
	}

	public String getCargoPic() {
		return cargoPic;
	}

	public void setCargoPic(String cargoPic) {
		this.cargoPic = cargoPic;
	}

	public String getMarkFile() {
		return MarkFile;
	}

	public void setMarkFile(String markFile) {
		MarkFile = markFile;
	}

	public String getCargoFile() {
		return CargoFile;
	}

	public void setCargoFile(String cargoFile) {
		CargoFile = cargoFile;
	}

	public String getAbnormalPic() {
		return AbnormalPic;
	}

	public void setAbnormalPic(String abnormalPic) {
		AbnormalPic = abnormalPic;
	}

	public String getAbnormalFile() {
		return AbnormalFile;
	}

	public void setAbnormalFile(String abnormalFile) {
		AbnormalFile = abnormalFile;
	}

	public String getReturnedCargoCause() {
		return ReturnedCargoCause;
	}

	public void setReturnedCargoCause(String returnedCargoCause) {
		ReturnedCargoCause = returnedCargoCause;
	}

	public String getReturnedCargoLink() {
		return ReturnedCargoLink;
	}

	public void setReturnedCargoLink(String returnedCargoLink) {
		ReturnedCargoLink = returnedCargoLink;
	}

	public String getLibraryNumber() {
		return libraryNumber;
	}

	public void setLibraryNumber(String libraryNumber) {
		this.libraryNumber = libraryNumber;
	}

	public String getLibraryAdamin() {
		return libraryAdamin;
	}

	public void setLibraryAdamin(String libraryAdamin) {
		this.libraryAdamin = libraryAdamin;
	}

	public String getSaillings_name() {
		return Saillings_name;
	}

	public void setSaillings_name(String saillings_name) {
		Saillings_name = saillings_name;
	}

	public String getSaillings() {
		return Saillings;
	}

	public void setSaillings(String saillings) {
		Saillings = saillings;
	}

	public String getShipping_space() {
		return shipping_space;
	}

	public void setShipping_space(String shipping_space) {
		this.shipping_space = shipping_space;
	}

	public String getFlight() {
		return flight;
	}

	public void setFlight(String flight) {
		this.flight = flight;
	}

	public String getVoyage() {
		return voyage;
	}

	public void setVoyage(String voyage) {
		this.voyage = voyage;
	}

	public String getWagonNumber() {
		return wagonNumber;
	}

	public void setWagonNumber(String wagonNumber) {
		this.wagonNumber = wagonNumber;
	}

	public String getCompany() {
		return company;
	}

	public void setCompany(String company) {
		this.company = company;
	}

	public String getTelPerson() {
		return TelPerson;
	}

	public void setTelPerson(String telPerson) {
		TelPerson = telPerson;
	}

	public String getAbnormalLink() {
		return AbnormalLink;
	}

	public void setAbnormalLink(String abnormalLink) {
		AbnormalLink = abnormalLink;
	}

	public String getScaned() {
		return Scaned;
	}

	public void setScaned(String scaned) {
		Scaned = scaned;
	}

	public String getAbnormalCause() {
		return AbnormalCause;
	}

	public void setAbnormalCause(String abnormalCause) {
		AbnormalCause = abnormalCause;
	}

	public String getReturnedCargoPic() {
		return ReturnedCargoPic;
	}

	public void setReturnedCargoPic(String returnedCargoPic) {
		ReturnedCargoPic = returnedCargoPic;
	}

	public String getReturnedCargoFile() {
		return ReturnedCargoFile;
	}

	public void setReturnedCargoFile(String returnedCargoFile) {
		ReturnedCargoFile = returnedCargoFile;
	}

	public String getUploadStatus() {
		return UploadStatus;
	}

	public void setUploadStatus(String uploadStatus) {
		UploadStatus = uploadStatus;
	}

	public String getMainGoodsId() {
		return MainGoodsId;
	}

	public void setMainGoodsId(String mainGoodsId) {
		MainGoodsId = mainGoodsId;
	}

	public String getTaskId() {
		return taskId;
	}

	public void setTaskId(String taskId) {
		this.taskId = taskId;
	}

	public String getLink() {
		return link;
	}

	public void setLink(String link) {
		this.link = link;
	}

	public String getScanUser() {
		return ScanUser;
	}

	public void setScanUser(String scanUser) {
		ScanUser = scanUser;
	}

	public String getContainer_no() {
		return container_no;
	}

	public void setContainer_no(String container_no) {
		this.container_no = container_no;
	}

	public String getFreighter_name() {
		return freighter_name;
	}

	public void setFreighter_name(String freighter_name) {
		this.freighter_name = freighter_name;
	}

	public String getSailing_no() {
		return sailing_no;
	}

	public void setSailing_no(String sailing_no) {
		this.sailing_no = sailing_no;
	}

	public String getPackName() {
		return PackName;
	}

	public void setPackName(String packName) {
		PackName = packName;
	}

	public String getMinutePackBarcode() {
		return MinutePackBarcode;
	}

	public void setMinutePackBarcode(String minutePackBarcode) {
		MinutePackBarcode = minutePackBarcode;
	}

	public String getMinutePackNumber() {
		return MinutePackNumber;
	}

	public void setMinutePackNumber(String minutePackNumber) {
		MinutePackNumber = minutePackNumber;
	}

	public String getLength_Old() {
		return Length_Old;
	}

	public void setLength_Old(String length_Old) {
		Length_Old = length_Old;
	}

	public String getWidth_Old() {
		return Width_Old;
	}

	public void setWidth_Old(String width_Old) {
		Width_Old = width_Old;
	}

	public String getHeight_Old() {
		return Height_Old;
	}

	public void setHeight_Old(String height_Old) {
		Height_Old = height_Old;
	}

	public String getRfid() {
		return rfid;
	}

	public void setRfid(String rfid) {
		this.rfid = rfid;
	}

	public String getLinkMan() {
		return linkMan;
	}

	public void setLinkMan(String linkMan) {
		this.linkMan = linkMan;
	}

	public String getLinkPhone() {
		return linkPhone;
	}

	public void setLinkPhone(String linkPhone) {
		this.linkPhone = linkPhone;
	}
}
