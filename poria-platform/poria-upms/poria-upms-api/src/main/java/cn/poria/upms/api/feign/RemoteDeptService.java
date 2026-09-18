package cn.poria.upms.api.feign;

import cn.poria.common.core.util.R;
import cn.poria.upms.api.entity.SysDept;
import jakarta.validation.Valid;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;

@FeignClient(
   name = "remoteDeptService",
   url = "${PORIA_UPMS:http://poria-upms:4000}"
)
public interface RemoteDeptService {
   @PostMapping({"/dept"})
   R<SysDept> save(@RequestBody @Valid SysDept sysDept);

   @PostMapping({"/dept/inner/save"})
   R<SysDept> saveInner(@RequestBody @Valid SysDept sysDept, @RequestHeader("from") String from);

   @PutMapping({"/dept"})
   R update(@RequestBody @Valid SysDept sysDept);

   @PutMapping({"/dept/inner/update"})
   R updateInner(@RequestBody @Valid SysDept sysDept, @RequestHeader("from") String from);

   @GetMapping({"/dept/detail/{id}"})
   R<SysDept> getById(@PathVariable("id") Long id);

   @DeleteMapping({"/dept/{id}"})
   R removeById(@PathVariable("id") Long id);

   @DeleteMapping({"/dept/inner/{id}"})
   R removeByIdInner(@PathVariable("id") Long id, @RequestHeader("from") String from);
}
