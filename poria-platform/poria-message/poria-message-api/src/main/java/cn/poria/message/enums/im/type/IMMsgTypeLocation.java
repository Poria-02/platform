package cn.poria.message.enums.im.type;

import cn.hutool.core.annotation.Alias;
import com.fasterxml.jackson.annotation.JsonTypeName;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@JsonTypeName("imMsgTypeLocation")
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class IMMsgTypeLocation implements IMMsgBody {


    @Override
    public String getMsgType() {
        return "TIMLocationElem";
    }

    @Schema(description = "地理位置描述信息")
    @Alias("Desc")
    private String desc;

    @Schema(description = "纬度")
    @Alias("Latitude")
    private Long latitude;

    @Schema(description = "经度")
    @Alias("Longitude")
    private Long longitude;



}
