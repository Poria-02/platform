package cn.poria.message.vo.request.im;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;
import java.util.Map;

/**
 * @author zhangchunlei
 * @date 2022年01月06日 1:47 下午
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CreateGroupModel {

    @Schema(description = "群id 不传则自动生成")
    private String groupId;

    @Schema(description = "群名称")
    private String name;

    @Schema(description = "群头像")
    private String faceUrl;

    @Schema(description = "群简介")
    private String introduction;

    @Schema(description = "群公告")
    private String notification;

    @Schema(description = "群主账号id")
    private String  owner;

    @Schema(description = "群员账号id")
    private List<String> members;

    @Schema(description = "群自定义信息")
    private Map<String,Object> customer;

}
