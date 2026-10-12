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

@Slf4j
@RestController
@Tag(name = "【API】- 问卷")
@RequestMapping("/api/questionnaire")
public class QuestionnaireApiController {

    @Autowired
    private RemoteTDuckFromApi remoteTDuckFromApi;



    @Operation(summary = "查询问卷列表",description = "查询问卷列表(message_questionnaire_get)")
    @GetMapping("/list")
    @PreAuthorize("@pms.hasPermission('message_questionnaire_get')")
    public R<Page<FormListVO>> getFromList(FormListModel formListModel){
        formListModel.setStatus(2);
        return R.ok(remoteTDuckFromApi.list(formListModel, SecurityConstants.FROM_IN).getData());
    }


    @GetMapping("/item/list")
    @Operation(summary = "表单项列表查询",description = "表单项列表查询(message_questionnaire_form)")
    @PreAuthorize("@pms.hasPermission('message_questionnaire_form')")
    public R queryFormItems(QueryFormItemRequest request){
        return R.ok(remoteTDuckFromApi.queryFormItems(request,SecurityConstants.FROM_IN).getData());
    }

}
