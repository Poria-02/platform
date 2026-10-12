package cn.poria.message.controller.app;

import cn.poria.common.core.constant.SecurityConstants;
import cn.poria.common.security.annotation.Inner;
import cn.poria.message.api.RemoteTDuckFromApi;
import cn.poria.message.integration.form.Result;
import cn.poria.message.integration.form.model.FormListModel;
import cn.poria.message.integration.form.model.FormPatientIdQueryModel;
import cn.poria.message.integration.form.model.QueryFormItemRequest;
import cn.poria.message.integration.form.model.UserFormDataQueryModel;
import cn.poria.message.integration.form.vo.FormListVO;
import cn.poria.message.integration.form.vo.UserSendInfoModel;
import cn.poria.message.integration.form.vo.UserWriteDataVO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import cn.poria.common.core.util.R;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RestController
@Tag(name = "【App】- 问卷")
@RequestMapping("/app/questionnaire")
public class QuestionnaireAppController {

    @Autowired
    private RemoteTDuckFromApi remoteTDuckFromApi;



    @Operation(summary = "查询问卷列表",description = "查询问卷列表")
    @GetMapping("/list")
    public R<Page<FormListVO>> getFromList(FormListModel formListModel){
        formListModel.setStatus(2);
        return R.ok(remoteTDuckFromApi.list(formListModel, SecurityConstants.FROM_IN).getData());
    }

    /**
     * 获取用户回填信息
     * @param userFormDataQueryModel
     * @return
     */
    @Operation(summary = "获取用户回填信息",description = "获取用户回填信息")
    @PostMapping("/findData")
    public R<UserWriteDataVO> findUserWriteData(@RequestBody UserFormDataQueryModel userFormDataQueryModel){
        return R.ok(remoteTDuckFromApi.findUserWriteData(userFormDataQueryModel, SecurityConstants.FROM_IN).getData());
    }

    /**
     * 查询表单sn流水号
     */
    @Operation(summary = "查询表单sn流水号",description = "查询表单sn流水号")
    @PostMapping("/sn")
    public R<Long> getUserFormSN(@RequestBody UserSendInfoModel model) {

        Result<Long> result =  remoteTDuckFromApi.getFormSN(model, SecurityConstants.FROM_IN);
        return  result.isSuccess() ? R.ok(result.getData()): R.failed(result.getMsg());

    }

    /**
     * 查询患者patientId
     */
    @Operation(summary = "查询患者patientId",description = "查询患者patientId")
    @PostMapping("/patientId")
    public R<String> findUserPatientIdFromForm(@RequestBody FormPatientIdQueryModel model){
        return R.ok(remoteTDuckFromApi.findPatientId(model, SecurityConstants.FROM_IN).getData());
    }


    @GetMapping("/item/list")
    @Operation(summary = "表单项列表查询",description = "表单项列表查询")
    public R queryFormItems(QueryFormItemRequest request){
        return R.ok(remoteTDuckFromApi.queryFormItems(request,SecurityConstants.FROM_IN).getData());
    }

}
