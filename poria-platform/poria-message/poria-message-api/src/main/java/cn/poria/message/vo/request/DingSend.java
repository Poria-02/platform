package cn.poria.message.vo.request;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Map;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class DingSend {

    @NotBlank(message="key不能为空")
    private String key;

    private Long orgId;

    /**
     * 参数列表
     */
    private Map<String,String> param;


}
