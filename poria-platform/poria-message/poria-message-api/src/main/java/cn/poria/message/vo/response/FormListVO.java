package cn.poria.message.vo.response;

import lombok.Data;

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
