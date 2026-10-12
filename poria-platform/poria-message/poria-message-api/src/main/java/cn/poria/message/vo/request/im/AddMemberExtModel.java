package cn.poria.message.vo.request.im;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class AddMemberExtModel {

    @Schema(description = "群组id")
    private String groupId;

    @Schema(description = "群成员")
    private List<MemberExt> members;
}
