package cn.poria.upms.api.feign;

import cn.poria.common.core.util.R;
import cn.poria.upms.api.entity.SysDeptRelation;
import cn.poria.upms.api.entity.SysRole;
import java.util.List;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;

@FeignClient(name = "remoteDataScopeService", url = "${PORIA_UPMS:http://poria-upms:4000}")
public interface RemoteDataScopeService {
   @PostMapping({"/role/getRoleList"})
   R<List<SysRole>> getRoleList(@RequestBody List<String> roleIdList);

   @GetMapping({"/dept/getDescendantList/{deptId}"})
   R<List<SysDeptRelation>> getDescendantList(@PathVariable("deptId") Long deptId);

   @PostMapping({"/dept/batch/getDescendantList"})
   R<List<SysDeptRelation>> getDescendantListBatch(@RequestParam("deptIds") List<Long> deptIds);
}
