package cn.poria.message.controller.api;

import cn.poria.common.core.constant.SecurityConstants;
import cn.poria.common.core.util.R;
import cn.poria.message.api.RemoteTDuckFromApi;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import cn.poria.message.integration.form.model.FormListModel;
import cn.poria.message.integration.form.model.QueryFormItemRequest;
import cn.poria.message.integration.form.vo.FormListVO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * 管理平台问卷查询控制层
 */
@Slf4j
@RestController
@Tag(name = "【API】- 问卷")
@RequestMapping("/api/questionnaire")
public class QuestionnaireApiController {

    /**
     * 问卷服务调用接口
     */
    @Autowired
    private RemoteTDuckFromApi remoteTDuckFromApi;

    /**
     * 分页查询收集中的问卷
     *
     * @param formListModel 分页及筛选条件
     * @return 问卷分页数据
     */
    @GetMapping("/list")
    @PreAuthorize("@pms.hasPermission('message_questionnaire_get')")
    @Operation(summary = "分页查询收集中的问卷", description = "分页查询收集中的问卷 (权限：message_questionnaire_get)")
    public R<Page<FormListVO>> getFromList(FormListModel formListModel) {
        formListModel.setStatus(2);
        return R.ok(remoteTDuckFromApi.list(formListModel, SecurityConstants.FROM_IN).getData());
    }

    /**
     * 查询问卷表单项列表
     *
     * @param request 表单项查询条件
     * @return 表单项列表
     */
    @GetMapping("/item/list")
    @PreAuthorize("@pms.hasPermission('message_questionnaire_form')")
    @Operation(summary = "查询问卷表单项列表", description = "查询问卷表单项列表 (权限：message_questionnaire_form)")
    public R queryFormItems(QueryFormItemRequest request) {
        return R.ok(remoteTDuckFromApi.queryFormItems(request, SecurityConstants.FROM_IN).getData());
    }
}
