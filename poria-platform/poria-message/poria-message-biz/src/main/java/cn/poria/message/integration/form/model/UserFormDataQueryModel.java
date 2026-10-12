package cn.poria.message.integration.form.model;

import lombok.Data;

@Data
public class UserFormDataQueryModel {
    private String patientId;

    private String formKey;

    private String sn;
}
