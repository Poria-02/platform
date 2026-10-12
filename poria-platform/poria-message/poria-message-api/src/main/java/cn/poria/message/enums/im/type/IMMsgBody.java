package cn.poria.message.enums.im.type;

import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;

@JsonTypeInfo(use = JsonTypeInfo.Id.NAME, include = JsonTypeInfo.As.PROPERTY)
@JsonSubTypes(
        {
                @JsonSubTypes.Type(IMMsgTypeTXT.class),
                @JsonSubTypes.Type(IMMsgTypeCustom.class),
                @JsonSubTypes.Type(IMMsgTypeFile.class),
                @JsonSubTypes.Type(IMMsgTypeIMG.class),
                @JsonSubTypes.Type(IMMsgTypeLocation.class),
                @JsonSubTypes.Type(IMMsgTypeTXT.class),
                @JsonSubTypes.Type(IMMsgTypeVideo.class),
                @JsonSubTypes.Type(IMMsgTypeVoice.class),
        }
)
public interface IMMsgBody {

    String getMsgType();

}
