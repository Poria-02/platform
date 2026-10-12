package cn.poria.message.controller.inner;

import cn.poria.common.core.constant.SecurityConstants;
import cn.poria.common.core.exception.ServiceException;
import cn.poria.message.api.RemoteTDuckFromApi;
import cn.poria.message.vo.request.UserFormSNModel;
import cn.poria.message.vo.request.form.FormPatientQueryModel;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import cn.poria.message.integration.form.Result;
import cn.poria.message.integration.form.model.FormListModel;
import cn.poria.message.integration.form.model.FormPatientIdQueryModel;
import cn.poria.message.integration.form.model.QueryFormItemRequest;
import cn.poria.message.integration.form.vo.FormListVO;
import cn.poria.message.integration.form.vo.UserSendInfoModel;
import jakarta.validation.constraints.NotBlank;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import cn.poria.common.security.annotation.Inner;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import cn.poria.common.core.util.R;
import io.swagger.v3.oas.annotations.Hidden;

import java.util.List;
import java.util.Map;

//@Hidden
@RestController
@RequestMapping("/inner/questionnaire")
public class QuestionnaireInnerController {

    @Autowired
    private RemoteTDuckFromApi remoteTDuckFromApi;

    /**
     * 查询表单sn流水号
     */
    @Inner
    @Operation(summary = "查询表单sn流水号",description = "查询表单sn流水号")
    @PostMapping("/sn")
    public R<Long> getUserFormSN(@RequestBody UserFormSNModel model) {
        UserSendInfoModel snModel = new UserSendInfoModel();
        BeanUtils.copyProperties(model,snModel);
        Result<Long> formSN = remoteTDuckFromApi.getFormSN(snModel, SecurityConstants.FROM_IN);
       if(!formSN.isSuccess()){
           throw new ServiceException(formSN.getMsg());
       }
        return R.ok(formSN.getData());
    }


    @Inner
    @Operation(summary = "查询患者patientId",description = "查询患者patientId")
    @PostMapping("/patientId")
    public R<String> findUserPatientIdFromForm(@RequestBody FormPatientQueryModel model){
        FormPatientIdQueryModel formModel = new FormPatientIdQueryModel();
        BeanUtils.copyProperties(model,formModel);
        return R.ok(remoteTDuckFromApi.findPatientId(formModel, SecurityConstants.FROM_IN).getData());
    }

    @Inner
    @Operation(summary = "查询问卷列表",description = "查询问卷列表")
    @PostMapping("/list")
    public R<List<FormListVO>> getFromList(@RequestBody FormListModel formListModel){
        formListModel.setSize(999);
        return R.ok(remoteTDuckFromApi.list(formListModel, SecurityConstants.FROM_IN).getData().getRecords());
    }

    @Inner
    @GetMapping("/item/list/{formKey}")
    @Operation(summary = "表单项列表查询",description = "表单项列表查询")
    public R queryFormItems(@PathVariable @NotBlank(message = "formKey不能为空") String formKey){
        QueryFormItemRequest request = new QueryFormItemRequest();
        request.setKey(formKey);
        return R.ok(remoteTDuckFromApi.queryFormItems(request,SecurityConstants.FROM_IN).getData());
    }

    @Inner
    @GetMapping("/form/data/{sn}")
    @Operation(summary = "查询问卷填写信息",description = "查询问卷填写信息")
    public R<Map<String, Object>> queryFormDataBySn(@PathVariable @NotBlank(message = "sn不能为空") String sn){
        return R.ok(remoteTDuckFromApi.queryFormDataBySn(sn,SecurityConstants.FROM_IN).getData());
    }

}
