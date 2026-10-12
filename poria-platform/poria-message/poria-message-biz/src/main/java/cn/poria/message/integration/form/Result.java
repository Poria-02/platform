package cn.poria.message.integration.form;

import cn.hutool.core.util.ObjectUtil;
import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.*;
import lombok.experimental.Accessors;

import java.io.Serializable;

/**
 * 响应信息主体
 *
 * @param <T>
 * @author smalljop
 */
@ToString
@NoArgsConstructor
@AllArgsConstructor
@Accessors(chain = true)
public class Result<T> implements Serializable {
    private static final long serialVersionUID = 1L;

    @Getter
    @Setter
    private int code = 200;

    @Getter
    @Setter
    private String msg;


    @Getter
    @Setter
    private T data;

    public static <T> Result<T> success() {
        return restResult(null, 200, null);
    }


    public static <T> Result<T> success(T data) {
        return restResult(data, 200, null);
    }

    public static <T> Result<T> success(T data, String msg) {
        return restResult(data, 200, msg);
    }

    public static <T> Result<T> successMsg(String msg) {
        return restResult(null, 200, msg);
    }

    public static <T> Result<T> isSuccess(boolean flag) {
        return flag ? success() : failed();
    }

    public static <T> Result<T> failed() {
        return restResult(null, 500, null);
    }


    public static <T> Result<T> failed(int code, String msg) {
        return restResult(null, code, msg);
    }

    public static <T> Result<T> failed(String msg) {
        return restResult(null, 500, msg);
    }

    public static <T> Result<T> failed(String msg, T data) {
        return restResult(data, 500, msg);
    }


    public static <T> Result<T> restResult(T data, int code, String msg) {
        Result<T> apiResult = new Result<>();
        apiResult.setCode(code);
        apiResult.setData(data);
        apiResult.setMsg(msg);
        return apiResult;
    }


    /**
     * 判断是否成功
     */
    public Boolean isSuccess() {
        return ObjectUtil.equal(this.code, 200);
    }


    /**
     * 判断data是否为空
     *
     * @return
     */
    @JsonIgnore
    public Boolean isDataNull() {
        return ObjectUtil.isNull(data);
    }

}
