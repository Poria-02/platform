package cn.poria.message.service.im.tx.request;

import cn.hutool.core.annotation.Alias;
import lombok.Data;

import java.util.Arrays;
import java.util.List;

@Data
public class AccountInfoRequest {

    @Alias("To_Account")
    private List<String> accountIds;

    @Alias("TagList")
    private List<String> tags = Arrays.asList("Tag_Profile_IM_Nick","Tag_Profile_IM_Image");
}
