package cn.poria.message.service.im.tx.request;

import cn.hutool.core.annotation.Alias;
import lombok.Data;

import java.util.List;

@Data
public class GroupDetailRequest {


    @Alias("GroupIdList")
    private List<String> groupIdList;

    /**
     * 包含三个过滤器：GroupBaseInfoFilter，MemberInfoFilter，AppDefinedDataFilter_Group，分别是基础信息字段过滤器，成员信息字段过滤器，群组维度的自定义字段过滤器
     */
    @Alias("ResponseFilter")
    private Object responseFilter;

    /**
     * 基础信息字段过滤器，指定需要获取的基础信息字段
     */
    @Alias("GroupBaseInfoFilter")
    private List<String> groupBaseInfoFilter;

    /**
     * 成员信息字段过滤器，指定需要获取的成员信息字段
     */
    @Alias("MemberInfoFilter")
    private List<String> memberInfoFilter;

    /**
     * 该字段用来群组维度的自定义字段过滤器，指定需要获取的群组维度的自定义字段
     */
    @Alias("AppDefinedDataFilter_Group")
    private List<String> appDefinedDataFilterGroup;

    /**
     * 该字段用来群成员维度的自定义字段过滤器，指定需要获取的群成员维度的自定义字段
     */
    @Alias("AppDefinedDataFilter_GroupMember")
    private List<String> appDefinedDataFilterGroupMember;

}
