package cn.poria.message.vo.request.im;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * @author zhangchunlei
 * @date 2022年01月06日 2:15 下午
 */
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class AddGroupPersonModel {

    @Schema(description = "群组id")
    private String  groupId;

    @Schema(description = "群成员账号id")
    private List<String> members;

}
