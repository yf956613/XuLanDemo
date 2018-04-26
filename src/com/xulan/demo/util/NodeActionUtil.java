package com.xulan.demo.util;

/** 
 * 所有节点下的动作id,和后台保持一致，不可更改
 * 
 * @author yxx
 *
 * @date 2016-1-29 下午5:34:07
 * 
 */
public class NodeActionUtil {

	//异常、单件录入、查询
	//这三个不是后台传过来的，自己定义
	public static final String EXCEPTION = "111111";//异常
	public static final String SINGLE = "222222";//单件录入
	public static final String QUERY = "333333";//查询
	public static final String TAKEPHOTO = "444444";//查询

	public static final String seaBack = "431B4348-18F0-4A9B-A568-0E8BC72D1E06";//退回、海运
	public static final String seaIn = "81BBEBF5-9B9B-4959-A55D-21AA249EDBEB";//海入、海运
	public static final String seaArrive = "8A18F4A9-4B1E-4B7D-80A8-45C477E02C8B";//海到";//海运
	public static final String seaOut = "9B8528A9-EC99-42FB-8DDB-ADD12F5C35E2";//海出";//海运

	public static final String conOut = "CDF38D06-EF9F-45F8-905B-86B468E31B95";//出港";//集装箱
	public static final String conIn = "79906101-00EB-43FE-8CF0-42E283EC3DAA";//进港";//集装箱
	public static final String conArrive = "1820080A-AD00-4B85-9537-6D3D58AF3AFE";//到港";//集装箱

	public static final String airIn = "1B51EB0B-EAF9-4255-A3D2-B84CB6EDCD3E";//空入";//空运
	public static final String airArrive = "0518EA65-9279-4283-B042-905E7D24052E";//空到";//空运
	public static final String airBack = "09BC2772-15EE-4C24-BF92-4D2D7A40CABA";//退回";//空运
	public static final String airOut = "8C4633A4-2407-4722-B8D5-8FDC50A41D4D";//空出";//空运

	public static final String landBack = "713478EA-98B2-460B-94C4-87063DADBC2E";//退回";//陆运
	public static final String landIn = "853A038B-6CB3-4ED2-9A63-4D645CBE79ED";//陆入";//陆运
	public static final String landOut = "88F9B607-0522-4ADA-8666-8BEC33447AC3";//陆出";//陆运
	public static final String landArrive = "36DF2EAF-14AD-4651-801A-44E7B9DD4A4E";//陆到";//陆运

	public static final String startOut = "1D0705D6-1CAE-4754-A581-197DE9955AD0";//出库";//起点位置
	public static final String startBack = "D4F34E29-7A4E-4288-B8E7-33E6FA592391";//退回";//起点位置

	public static final String areaMove = "FB36A89B-8129-467D-9AC8-665B5783FAFC";//移库";//区域位置
	public static final String areaBack = "8D394CA1-0404-4965-BE53-CF365A4F92B0";//退回";//区域位置
	public static final String areaIn = "320BE037-856C-4CE8-8B48-B32EC5AB7577";//入库";//区域位置
	public static final String areaOut = "6E9D6F4C-A283-4F4F-9AA5-E92AA31C2B4E";//出库";//区域位置

	public static final String railIn = "70E43721-E442-43EE-9AEA-B4077AE552BD";//铁入";//铁运
	public static final String railArrive = "4C7C098F-4153-4366-8353-24CF7EFFB4AA";//铁到";//铁运
	public static final String railBack = "5DCEE059-5215-4A5F-BE33-60F03D9562A2";//退回";//铁运
	public static final String railOut = "08BAC825-E81A-4E67-95AA-D81FAEB345E7";//铁出";//铁运

	public static final String endBack = "D3712EFF-9069-4049-A212-EAA0630FF0C6";//退回";//终点位置
	public static final String endIn = "A9E02E99-4646-4D09-AB47-159F0FE09123";//入库";//终点位置

	public static final String transIn = "96E64B96-FAD3-47EF-A03F-574EDA349036";//入库";//转点位置
	public static final String transIOut = "98A817BF-972C-41BC-A117-78E36908F401";//出库";//转点位置
	public static final String transBack = "3121E267-5639-4B78-957A-95C3F3A4EC79";//退回";//转点位置

	public static final String loadBack = "26A9E4DC-FF46-4B1C-A718-1E5EC369BDF2";//退回";//装卸
	public static final String loadIn = "618140C0-07F4-4D40-B206-9ADF415984AB";//装入";//装卸
	public static final String loadOut = "6C836303-7876-4CB8-AD91-CEC440E84CCD";//卸出";//装卸
	
	//延伸操作
	public static final String extendOff = "extendoff";//下线
	public static final String extendPackage = "extendpackage";//打包
	public static final String extendInstall = "extendinstall";//安装
}
