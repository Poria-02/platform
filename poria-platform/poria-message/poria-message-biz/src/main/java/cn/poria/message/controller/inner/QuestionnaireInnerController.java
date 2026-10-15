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

/**
 * 服务间问卷查询控制层
 */
//@Hidden
@RestController
@RequestMapping("/inner/questionnaire")
public class QuestionnaireInnerController {

    /**
     * 问卷服务调用接口
     */
    @Autowired
    private RemoteTDuckFromApi remoteTDuckFromApi;

    /**
     * 获取用户问卷流水号
     *
     * @param model 用户及问卷信息
     * @return 问卷流水号
     */
    @Inner
    @PostMapping("/sn")
    @Operation(summary = "获取用户问卷流水号", description = "获取用户问卷流水号")
    public R<Long> getUserFormSN(@RequestBody UserFormSNModel model) {
        UserSendInfoModel snModel = new UserSendInfoModel();
        BeanUtils.copyProperties(model, snModel);
        Result<Long> formSN = remoteTDuckFromApi.getFormSN(snModel, SecurityConstants.FROM_IN);
        if (!formSN.isSuccess()) {
            throw new ServiceException(formSN.getMsg());
        }
        return R.ok(formSN.getData());
    }

    /**
     * 查询问卷关联的患者ID
     *
     * @param model 患者ID查询条件
     * @return 患者ID
     */
    @Inner
    @PostMapping("/patientId")
    @Operation(summary = "查询问卷关联的患者ID", description = "查询问卷关联的患者ID")
    public R<String> findUserPatientIdFromForm(@RequestBody FormPatientQueryModel model) {
        FormPatientIdQueryModel formModel = new FormPatientIdQueryModel();
        BeanUtils.copyProperties(model, formModel);
        return R.ok(remoteTDuckFromApi.findPatientId(formModel, SecurityConstants.FROM_IN).getData());
    }

    /**
     * 查询问卷列表
     *
     * @param formListModel 问卷筛选条件
     * @return 问卷列表
     */
    @Inner
    @PostMapping("/list")
    @Operation(summary = "查询问卷列表", description = "查询问卷列表")
    public R<List<FormListVO>> getFromList(@RequestBody FormListModel formListModel) {
        formListModel.setSize(999);
        return R.ok(remoteTDuckFromApi.list(formListModel, SecurityConstants.FROM_IN).getData().getRecords());
    }

    /**
     * 通过表单Key查询表单项列表
     *
     * @param formKey 表单Key
     * @return 表单项列表
     */
    @Inner
    @GetMapping("/item/list/{formKey}")
    @Operation(summary = "通过表单Key查询表单项列表", description = "通过表单Key查询表单项列表")
    public R queryFormItems(@PathVariable @NotBlank(message = "formKey不能为空") String formKey) {
        QueryFormItemRequest request = new QueryFormItemRequest();
        request.setKey(formKey);
        return R.ok(remoteTDuckFromApi.queryFormItems(request, SecurityConstants.FROM_IN).getData());
    }

    /**
     * 通过流水号查询问卷填写信息
     *
     * @param sn 问卷流水号
     * @return 问卷填写信息
     */
    @Inner
    @GetMapping("/form/data/{sn}")
    @Operation(summary = "通过流水号查询问卷填写信息", description = "通过流水号查询问卷填写信息")
    public R<Map<String, Object>> queryFormDataBySn(@PathVariable @NotBlank(message = "sn不能为空") String sn) {
        return R.ok(remoteTDuckFromApi.queryFormDataBySn(sn, SecurityConstants.FROM_IN).getData());
    }
}
