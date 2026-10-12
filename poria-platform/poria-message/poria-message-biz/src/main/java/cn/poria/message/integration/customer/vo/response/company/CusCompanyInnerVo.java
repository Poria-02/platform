package cn.poria.message.integration.customer.vo.response.company;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.util.Date;


/**
 * 企业表(CusCompany)表实体类
 *
 * @author makejava
 * @since 2020-10-27 09:41:02
 */
@Data
public class CusCompanyInnerVo {

    /**
     * 企业id
     */
    @Schema(description = "企业id")
    private String id;


    /**
     * 客户等级
     */
    @Schema(description = "客户等级 ")
    private String level;

    /**
     * logo图标地址
     */
    @Schema(description = "logo图标地址")
    private String logo;

    /**
     * 公司名称
     */
    @Schema(description = "公司名称")
    private String name;

    /**
     * 公司简称
     */
    @Schema(description = "公司简称")
    private String brefName;

    /**
     * 联系电话
     */
    @Schema(description = "联系电话")
    private String mobile;

    /**
     * 企业地址
     */
    @Schema(description = "企业地址")
    private String address;

    /**
     * 经度
     */
    @Schema(description = "经度")
    private String longitude;

    /**
     * 纬度
     */
    @Schema(description = "纬度")
    private String latitude;

    /**
     * 企业介绍
     */
    @Schema(description = "企业介绍")
    private String instruction;

    private String createBy;

    private Date createTime;

    private Date updateTime;

    @Schema(description = "已删除 0:否 1:是")
    private Integer isDelete;



    /**
     * 机构id
     */
    @Schema(description = "机构id")
    private Long orgId;

    /**
     * AppId
     */
    @Schema(description = "AppId")
    private String appId;

    /**
     * 企业状态 0-未认证 1-已认证 2-停用
     */
    @Schema(description = "企业状态 0-未认证 1-已认证 2-停用")
    private Integer status;
}
