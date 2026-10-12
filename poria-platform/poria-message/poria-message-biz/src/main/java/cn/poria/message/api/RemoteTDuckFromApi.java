package cn.poria.message.api;

import cn.poria.common.core.constant.SecurityConstants;
import cn.poria.common.core.util.R;
import cn.poria.message.integration.form.Result;
import cn.poria.message.integration.form.model.*;
import cn.poria.message.integration.form.vo.*;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import io.swagger.v3.oas.annotations.Hidden;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.*;
import java.util.List;
import java.util.Map;

/** 消息服务使用的远程接口契约，保留原有 HTTP 路径和响应格式。 */
@Hidden
@FeignClient(name = "remoteTDuckFromApi", url = "${PORIA_TDUCK:http://poria-tduck:8998}")
public interface RemoteTDuckFromApi {
    @PostMapping("/tduck-api/inner/user/form/list")
    Result<Page<FormListVO>> list(@RequestBody FormListModel model,@RequestHeader("from") String from);

    @PostMapping("/tduck-api/inner/user/write/data")
    Result<UserWriteDataVO> findUserWriteData(@RequestBody UserFormDataQueryModel model,@RequestHeader("from") String from);

    @PostMapping("/tduck-api/inner/form/sn")
    Result<Long> getFormSN(@RequestBody UserSendInfoModel model,@RequestHeader("from") String from);

    @GetMapping("/tduck-api/inner/form/data/{sn}")
    Result<Map<String, Object>> queryFormDataBySn(@PathVariable("sn")String sn,@RequestHeader("from") String from);

    @PostMapping("/tduck-api/inner/user/data/patientId")
    Result<String> findPatientId(@RequestBody FormPatientIdQueryModel model,@RequestHeader("from") String from);

    @PostMapping("/tduck-api/inner/user/form/item/list")
    Result queryFormItems(@RequestBody QueryFormItemRequest request,@RequestHeader("from") String from);
}
