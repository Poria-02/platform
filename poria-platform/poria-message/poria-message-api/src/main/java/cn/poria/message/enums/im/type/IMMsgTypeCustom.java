package cn.poria.message.enums.im.type;

import com.fasterxml.jackson.annotation.JsonTypeName;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Map;

@JsonTypeName("imMsgTypeCustom")
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class IMMsgTypeCustom implements IMMsgBody {


    @Schema(description = "信息内容")
    private Map<String,Object> content;

    @Override
    public String getMsgType() {
        return "TIMCustomElem";
    }
}
