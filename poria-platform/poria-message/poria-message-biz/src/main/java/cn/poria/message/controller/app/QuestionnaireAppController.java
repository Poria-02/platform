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

/**
 * App端问卷查询控制层
 */
@Slf4j
@RestController
@Tag(name = "【App】- 问卷")
@RequestMapping("/app/questionnaire")
public class QuestionnaireAppController {

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
    @Operation(summary = "分页查询收集中的问卷", description = "分页查询收集中的问卷")
    public R<Page<FormListVO>> getFromList(FormListModel formListModel) {
        formListModel.setStatus(2);
        return R.ok(remoteTDuckFromApi.list(formListModel, SecurityConstants.FROM_IN).getData());
    }

    /**
     * 查询用户的问卷填写信息
     *
     * @param userFormDataQueryModel 用户及问卷查询条件
     * @return 用户问卷填写信息
     */
    @PostMapping("/findData")
    @Operation(summary = "查询用户的问卷填写信息", description = "查询用户的问卷填写信息")
    public R<UserWriteDataVO> findUserWriteData(@RequestBody UserFormDataQueryModel userFormDataQueryModel) {
        return R.ok(remoteTDuckFromApi.findUserWriteData(userFormDataQueryModel, SecurityConstants.FROM_IN).getData());
    }

    /**
     * 获取用户问卷流水号
     *
     * @param model 用户及问卷信息
     * @return 问卷流水号获取结果
     */
    @PostMapping("/sn")
    @Operation(summary = "获取用户问卷流水号", description = "获取用户问卷流水号")
    public R<Long> getUserFormSN(@RequestBody UserSendInfoModel model) {

        Result<Long> result = remoteTDuckFromApi.getFormSN(model, SecurityConstants.FROM_IN);
        return result.isSuccess() ? R.ok(result.getData()) : R.failed(result.getMsg());

    }

    /**
     * 查询问卷关联的患者ID
     *
     * @param model 患者ID查询条件
     * @return 患者ID
     */
    @PostMapping("/patientId")
    @Operation(summary = "查询问卷关联的患者ID", description = "查询问卷关联的患者ID")
    public R<String> findUserPatientIdFromForm(@RequestBody FormPatientIdQueryModel model) {
        return R.ok(remoteTDuckFromApi.findPatientId(model, SecurityConstants.FROM_IN).getData());
    }

    /**
     * 查询问卷表单项列表
     *
     * @param request 表单项查询条件
     * @return 表单项列表
     */
    @GetMapping("/item/list")
    @Operation(summary = "查询问卷表单项列表", description = "查询问卷表单项列表")
    public R queryFormItems(QueryFormItemRequest request) {
        return R.ok(remoteTDuckFromApi.queryFormItems(request, SecurityConstants.FROM_IN).getData());
    }
}
