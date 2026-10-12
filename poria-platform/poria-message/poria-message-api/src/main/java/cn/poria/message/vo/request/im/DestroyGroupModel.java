package cn.poria.message.vo.request.im;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author zhangchunlei
 * @date 2022年01月06日 2:46 下午
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class DestroyGroupModel {

    @Schema(description = "群组id")
    private String groupId;

}
