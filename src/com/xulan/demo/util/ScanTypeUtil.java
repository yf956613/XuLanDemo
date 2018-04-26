package com.xulan.demo.util;

/** 
 * 扫描类型
 * 
 * @author yxx
 *
 * @date 20015-12-18 下午12:0000:46
 * 
 */
public class ScanTypeUtil {

	/**
	 * 起点
	 * 出库（1001）	单件录入	异常	退运	查询
	 */
	public static final String start_out = "1001";
	public static final String start_single = "1002";
	public static final String start_exception = "1003";
	public static final String start_back = "1004";
	public static final String start_query = "1005";

	/**
	 * 陆运
	 * 装车	到达	卸车	退运操作	异常	单件录入	查询
	 */
	public static final String land_loading = "2001";
	public static final String land_arrive = "2002";
	public static final String land_unloading = "2003";
	public static final String land_back = "2004";
	public static final String land_exception = "2005";
	public static final String land_single = "2006";
	public static final String land_query = "2007";

	/**
	 * 转点
	 * 入库	出库	单件录入	异常	退运	查询
	 */
	public static final String trans_in = "3001";
	public static final String trans_out = "3002";
	public static final String trans_single = "3003";
	public static final String trans_exception = "3004";
	public static final String trans_back = "3005";
	public static final String trans_query = "3006";

	/**
	 * 铁运
	 * 铁运装货	铁运到达	铁运卸货	异常	退运	单件录入	查询
	 */
	public static final String rail_loading = "4001";
	public static final String rail_arrive = "4002";
	public static final String rail_unloading = "4003";
	public static final String rail_exception = "4004";
	public static final String rail_back = "4005";
	public static final String rail_single = "4006";
	public static final String rail_query = "4007";

	/**
	 * 区域
	 * 入库	移库	出库	异常	单件录入	退运	查询
	 */
	public static final String area_in = "5001";
	public static final String area_move = "5002";
	public static final String area_out = "5003";
	public static final String area_exception = "5004";
	public static final String area_single = "5005";
	public static final String area_back = "5006";
	public static final String area_query = "5007";

	/**
	 * 装卸
	 * 装入	卸出	退装卸	异常	单件录入	查询	
	 */
	public static final String load_in = "6001";
	public static final String load_move = "6002";
	public static final String load_out = "6003";
	public static final String load_back = "6004";
	public static final String load_exception = "6005";
	public static final String load_single = "6006";
	public static final String load_query = "6007";

	/**
	 * 船运
	 * 装船扫描	船运到港	卸船	异常	单件录入	退运	查询
	 */
	public static final String ship_load = "7001";
	public static final String ship_arrive = "7002";
	public static final String ship_unload = "7003";
	public static final String ship_exception = "7004";
	public static final String ship_single = "7005";
	public static final String ship_back = "7006";
	public static final String ship_query = "7007";

	/**
	 * 打尺数据
	 * 数据对比
	 */
	public static final String ruler_compare = "8001";

	/**
	 * 空运
	 * 空运装货	空运到港	空运卸货	异常	退运	单件录入	查询
	 */
	public static final String air_load = "9001";
	public static final String air_arrive = "9002";
	public static final String air_unload = "9003";
	public static final String air_exception = "9004";
	public static final String air_back = "9005";
	public static final String air_single = "9006";
	public static final String air_query = "9007";

	/**
	 * 包装
	 * 包装	
	 */
	public static final String package_bag = "1101";

	/**
	 * 终点
	 * 入库	异常	退运	单件录入	查询
	 */
	public static final String end_in = "1201";
	public static final String end_exception = "1202";
	public static final String end_back = "1203";
	public static final String end_single = "1204";
	public static final String end_query = "1205";

	/**
	 * 检验
	 * 检验
	 */
	public static final String test_test = "1301";	

	/**
	 * 集装箱
	 * 进港	离港	到港	异常	退运	查询
	 */
	public static final String container_in = "1401";
	public static final String container_leave = "1402";
	public static final String container_arrive = "1403";
	public static final String container_exception = "1404";
	public static final String container_back = "1405";
	public static final String container_query = "1406";

	/**
	 * 延伸操作
	 * 下线信息	打包信息	安装信息
	 */
	public static final String extend_offline = "1501";
	public static final String extend_package = "1502";
	public static final String extend_install = "1503";
}
