package cn.poria.message.vo.request;

import lombok.Data;

@Data
public class UserFormSNModel {
    private String formKey;
    private String patientId;
    private String accountId;
}
