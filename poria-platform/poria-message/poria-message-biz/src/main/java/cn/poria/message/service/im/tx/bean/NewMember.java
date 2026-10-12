package cn.poria.message.service.im.tx.bean;

import cn.hutool.core.annotation.Alias;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class NewMember {

    /**
     * 待添加的群成员 UserID
     */
    @Alias("Member_Account")
    private String userId;


    /**
     * 角色
     */
    @Alias("Role")
    private String role;


    @Alias("AppMemberDefinedData")
    private List appMemberDefinedData;
}
