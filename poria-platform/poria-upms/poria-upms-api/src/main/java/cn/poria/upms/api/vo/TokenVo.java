package cn.poria.upms.api.vo;
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
