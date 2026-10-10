package cn.poria.upms.api.feign;

import cn.poria.common.core.constant.ServiceNameConstants;
import cn.poria.common.core.util.R;
import cn.poria.upms.api.entity.SysDictItem;
import cn.poria.upms.api.vo.TreeDictItemVo;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestHeader;

import java.util.List;

@FeignClient(name = "remoteDictService",url = ServiceNameConstants.UPMS_SERVICE)
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
