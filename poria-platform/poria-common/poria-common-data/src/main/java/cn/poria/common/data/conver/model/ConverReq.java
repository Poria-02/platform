package cn.poria.common.data.conver.model;

import lombok.Data;

import java.util.List;
import java.util.Set;

@Data
public class ConverReq {

	private String keyField;
	private String valueField;

	private String table;
	private Set<String> keys;
	private String condition;

	public ConverReq() {

	}

	public ConverReq(String keyField, String valueField, String table, Set<String> keys,String condition) {
		this.keyField = keyField;
		this.valueField = valueField;
		this.table = table;
		this.keys = keys;
		this.condition = condition;
	}
}
