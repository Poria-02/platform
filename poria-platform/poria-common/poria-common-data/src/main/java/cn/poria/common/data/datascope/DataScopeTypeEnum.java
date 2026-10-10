package cn.poria.common.data.datascope;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
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
	 * 本级
	 */
	OWN_LEVEL(2, "本级"),

	/** 本部门及所有下级部门。 */
	DEPT_LEVEL(3, "本级及以下");

	/**
	 * 类型
	 */
	private final int type;

	/**
	 * 描述
	 */
	private final String description;

}
