package cn.poria.message.service.im.tx.response;

import lombok.Data;

@Data
public class CallBackResponse {

    private String ActionStatus;

    private String ErrorInfo;

    private Integer ErrorCode;


    public static CallBackResponse ok(){
        return restResult("OK","",0);
    }

    public static CallBackResponse fail(String errorInfo){
        return restResult("FAIL",errorInfo,1);
    }


    private static CallBackResponse restResult(String status, String errorInfo, Integer errorCode) {
        CallBackResponse response = new CallBackResponse();
        response.setActionStatus(status);
        response.setErrorInfo(errorInfo);
        response.setErrorCode(errorCode);
        return response;
    }
}
