package cn.poria.message.enums.im.type;

import cn.hutool.core.annotation.Alias;
import com.fasterxml.jackson.annotation.JsonTypeName;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;


@JsonTypeName("imMsgTypeTXT")
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class IMMsgTypeTXT implements IMMsgBody {


    @Override
    public String getMsgType() {
        return "TIMTextElem";
    }


    @Schema(description = "文本内容")
    @Alias("Text")
    private String content;


}
