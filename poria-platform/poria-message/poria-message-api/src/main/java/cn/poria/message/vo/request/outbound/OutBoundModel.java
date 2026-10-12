package cn.poria.message.vo.request.outbound;

import lombok.Data;

import java.util.Map;

@Data
public class OutBoundModel {

    private String mobile;

    private String accountId;

    private String templateKey;

    private Map<String,String> params;

    private String patientName;

    private String planName;

    private String businessId;


}
