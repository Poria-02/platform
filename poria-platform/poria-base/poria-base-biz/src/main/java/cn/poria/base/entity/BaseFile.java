package cn.poria.base.entity;

import com.baomidou.mybatisplus.annotation.*;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.util.Date;


/**
 * 文件管理表(BaseFile)表实体类
 *
 * @author makejava
 * @since 2021-07-29 10:37:16
 */
@Data
public class BaseFile {

    /**
     * ID
     */
    @TableId(type = IdType.ASSIGN_ID)
    @Schema(description = "ID")
    private String id;

    /**
     * 文件名
     */

    @Schema(description = "文件名")
    private String fileName;

    /**
     * 桶名称
     */

    @Schema(description = "桶名称")
    private String bucketName;

    /**
     * 原文件名
     */

    @Schema(description = "原文件名")
    private String original;

    /**
     * 1-公共文件,2-私有文件
     */

    @Schema(description = "1-公共文件,2-私有文件")
    private String type;

    /**
     * 文件大小
     */

    @Schema(description = "文件大小")
    private Long fileSize;

    /**
     * 上传人
     */
    @TableField(fill = FieldFill.INSERT)
    @Schema(description = "上传人")
    private String createBy;

    /**
     * 上传时间
     */
    @TableField(fill = FieldFill.INSERT)
    @Schema(description = "上传时间")
    private Date createTime;

    /**
     * 删除标识
     */
    @TableLogic(value = "0", delval = "1")
    @TableField(fill = FieldFill.INSERT)
    @Schema(description = "删除标识")
    private Integer isDelete;

    /**
     * 所属租户
     */

    @Schema(description = "所属租户")
    private Integer tenantId;
}
