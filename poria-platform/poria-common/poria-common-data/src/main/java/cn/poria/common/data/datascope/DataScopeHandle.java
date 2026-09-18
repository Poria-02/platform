package cn.poria.common.data.datascope;

/**
 * @author shanxincd
 * @date 2019-09-07
 * <p>
 * data scope 判断处理器,抽象服务扩展
 */
public interface DataScopeHandle {

	/**
	 * 计算用户数据权限
	 * @param dataScope 数据权限设置
	 * @return 返回true表示无需进行数据过滤处理，返回false表示需要进行数据过滤
	 */
	Boolean calcScope(DataScope dataScope);

}
