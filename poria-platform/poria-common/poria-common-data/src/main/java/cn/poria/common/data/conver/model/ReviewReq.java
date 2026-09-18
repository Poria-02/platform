package cn.poria.common.data.conver.model;

import lombok.Data;


/**
 * author qiaodi
 * date 2025/9/18 09:54
 * version 6.7.3
 * description
 */
@Data
public class ReviewReq {

    private String keyField;
    private String valueField;

    private String table;
    private String businessId;
    private String condition;
    private String accountId;
    private String reviewMethod;
    private String content;
    private String oldContent;
    private String machineReview;
    private Long orgId;
    private String data;

    public ReviewReq(){};

    public ReviewReq(String businessId, String table, String keyField, String valueField, String condition) {
        this.businessId = businessId;
        this.table = table;
        this.keyField = keyField;
        this.valueField = valueField;
        this.condition = condition;
    }
}
