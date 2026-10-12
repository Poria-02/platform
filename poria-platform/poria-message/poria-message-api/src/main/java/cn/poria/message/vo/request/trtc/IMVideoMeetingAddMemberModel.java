package cn.poria.message.vo.request.trtc;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.util.ArrayList;
import java.util.List;

@Data
public class IMVideoMeetingAddMemberModel {

    private String roomId;

    private List<Member> members = new ArrayList<>();

    @Data
    public static class Member {

        @Schema(description = "账号id")
        private String accountId;

        @Schema(description = "昵称")
        private String nickName;

        @Schema(description = "角色")
        private String role;

    }

}
