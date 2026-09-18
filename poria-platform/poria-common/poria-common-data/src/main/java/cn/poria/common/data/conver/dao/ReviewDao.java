package cn.poria.common.data.conver.dao;

import cn.poria.common.data.conver.model.ReviewReq;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.*;


/**
 * author qiaodi
 * date 2025/9/18 09:43
 * version 6.7.3
 * description
 */
@Mapper
public interface ReviewDao extends BaseMapper<ReviewReq> {

    @Select({"<script>",
            " select ${valueField} as `key` from ${table} where ${keyField} = ${businessId} ",
            " <if test='condition != null and condition != \"\"'>",
            " and ${condition} ",
            " </if>",
            "</script>"})
    String selectData(@Param("businessId") String businessId,
                                  @Param("table") String table,
                                  @Param("keyField") String keyField,
                                  @Param("valueField") String valueField,
                                  @Param("condition") String condition);

    @Insert({"<script>",
            "insert into review_content (id,type,account_id,machine_review_service,old_content,content,machine_review,org_id,biz_id,data,create_time,is_delete) ",
                    "values ('${id}', 1, '${req.accountId}','${req.reviewMethod}','${req.oldContent}','${req.content}','${req.machineReview}',#{req.orgId},'${req.businessId}','${req.data}',current_timestamp(),0)",
            " </script>"})
    void saveData(@Param("id") String id, @Param("req") ReviewReq req);


    @Update({"<script>",
            "update ${req.table} set ${req.valueField} = #{req.content} where ${req.keyField} = ${req.businessId}",
            " </script>"})
    void updateData(@Param("req") ReviewReq req);


    @Select({"<script>",
            "SELECT " +
            "    CASE " +
            "        WHEN (SELECT end_punish FROM review_punish LIMIT 1) >= (SELECT COUNT(*) FROM review_violation_record where account_id = ${req.accountId}) " +
            "        THEN FALSE " +
            "        ELSE TRUE " +
            "    END AS result",
            "</script>"})
    Integer count(@Param("req") ReviewReq req);
}
