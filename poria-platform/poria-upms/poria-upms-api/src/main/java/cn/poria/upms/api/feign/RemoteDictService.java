package cn.poria.upms.api.feign;

import cn.poria.common.core.util.R;
import cn.poria.upms.api.entity.SysDictItem;
import cn.poria.upms.api.vo.TreeDictItemVo;
import java.util.List;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestHeader;

@FeignClient(
   name = "remoteDictService",
   url = "${PORIA_UPMS:http://poria-upms:4000}"
)
public interface RemoteDictService {
   @GetMapping({"/treedict/item/parent/{dictCode}/{value}"})
   R<TreeDictItemVo> getParentDictItem(@PathVariable("dictCode") String dictCode, @PathVariable("value") String value, @RequestHeader("from") String from);

   @GetMapping({"/treedict/item/list/{code}"})
   R<List<TreeDictItemVo>> dictItems(@PathVariable("code") String code);

   @GetMapping({"/dict/items/{type}"})
   R<List<SysDictItem>> getSysDictItems(@PathVariable("type") String type, @RequestHeader("from") String from);

   @GetMapping({"/dict/item/{dictCode}/{value}"})
   R<SysDictItem> findDictItem(@PathVariable("dictCode") String dictCode, @PathVariable("value") String value, @RequestHeader("from") String from);

   @GetMapping({"/treedict/item/peer/{dictCode}/{value}"})
   R<TreeDictItemVo> getPeerDictItem(@PathVariable("dictCode") String dictCode, @PathVariable("value") String value, @RequestHeader("from") String from);

   @GetMapping({"/treedict/item/findByValue/{value}"})
   R<TreeDictItemVo> findAuthenticate(@PathVariable("value") String value, @RequestHeader("from") String from);
}
