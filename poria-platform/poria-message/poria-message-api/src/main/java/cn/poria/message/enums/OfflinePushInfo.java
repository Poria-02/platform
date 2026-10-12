package cn.poria.message.enums;

import cn.hutool.core.annotation.Alias;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Builder
@AllArgsConstructor
@NoArgsConstructor
@Data
public class OfflinePushInfo {

    private int PushFlag = 0 ;

    @Alias("Desc")
    private String desc ;

    @Alias("Ext")
    private String ext;

    @Alias("Title")
    private String title;


}
