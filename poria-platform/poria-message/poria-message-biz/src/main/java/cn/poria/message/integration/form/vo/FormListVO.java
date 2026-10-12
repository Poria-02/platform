package cn.poria.message.integration.form.vo;

import lombok.Data;

import jakarta.validation.constraints.NotNull;

@Data
public class FormListVO {

    private String id;

    //表单名称
    private String name;


    //表单url
    private String url;


    //描述
    private String description;

    private String formKey;

    private String textName;

}
