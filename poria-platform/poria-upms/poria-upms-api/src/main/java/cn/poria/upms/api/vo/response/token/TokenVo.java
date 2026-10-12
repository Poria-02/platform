package cn.poria.upms.api.vo.response.token;
import lombok.Data;

@Data
public class TokenVo {

    private String id;

    private Long userId;

    private String clientId;

    private String username;

    private String accessToken;

    private String issuedAt;

    private String expiresAt;
}
