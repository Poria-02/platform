package cn.poria.message.enums;

import cn.hutool.core.annotation.Alias;
import lombok.*;

@Builder
@AllArgsConstructor
@NoArgsConstructor
@Data
@EqualsAndHashCode(callSuper = false)
public class OfflinePushInfo {

    private int PushFlag = 0 ;

    @Alias("Desc")
    private String desc ;

    @Alias("Ext")
    private String ext;

    @Alias("Title")
    private String title;


}
