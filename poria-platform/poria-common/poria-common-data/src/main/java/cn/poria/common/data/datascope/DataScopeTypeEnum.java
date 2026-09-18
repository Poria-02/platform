package cn.poria.common.data.datascope;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * @author shanxincd
 * @date 2018/12/26
 * <p>
 * 数据权限类型
 */
@Getter
@AllArgsConstructor
public enum DataScopeTypeEnum {

	/**
	 * 查询全部数据
	 */
	ALL(0, "全部"),

    /**
     * 自定义
     */
    CUSTOM(1, "自定义"),

    /**
     * 医联体
     */
    MC_LEVEL(2,"医联体"),

	/**
	 * 本级
	 */
	OWN_LEVEL(3, "本级"),



    ;

	/**
	 * 类型
	 */
	private final int type;

	/**
	 * 描述
	 */
	private final String description;

}
