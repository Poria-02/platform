package cn.poria.message.integration.form.model;

import lombok.Data;

import java.util.List;

@Data
public class FormListModel {

    private List<String> formKeys;

    //表单所属文件夹
    private String folder;

    //表单名称
    private String name;

    //表单状态 1未发布  2收集中 3已结束
    private Integer status;

    private Integer current = 1;

    private Integer size = 10;
}
