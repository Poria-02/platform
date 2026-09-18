package cn.poria.upms.service.impl;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.collection.CollectionUtil;
import cn.poria.common.core.exception.ServiceException;
import cn.poria.common.core.util.Assert;
import cn.poria.upms.api.entity.SysTreeDict;
import cn.poria.upms.api.entity.SysTreeDictItem;
import cn.poria.upms.api.vo.ExportDictModel;
import cn.poria.upms.api.vo.SysTreeDictVO;
import cn.poria.upms.mapper.SysTreeDictItemMapper;
import cn.poria.upms.mapper.SysTreeDictMapper;
import cn.poria.upms.service.SysTreeDictItemService;
import cn.poria.upms.service.SysTreeDictService;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.core.conditions.Wrapper;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import jakarta.servlet.ServletOutputStream;
import jakarta.servlet.http.HttpServletResponse;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.lang.invoke.SerializedLambda;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.ArrayDeque;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

@Service
public class SysTreeDictItemServiceImpl extends ServiceImpl<SysTreeDictItemMapper, SysTreeDictItem> implements SysTreeDictItemService {
   private static final Logger log = LoggerFactory.getLogger(SysTreeDictItemServiceImpl.class);
   @Autowired
   private SysTreeDictService treeDictService;
   @Autowired
   private SysTreeDictItemMapper sysTreeDictItemMapper;
   @Autowired
   private SysTreeDictMapper sysTreeDictMapper;

   public SysTreeDictItem getParentDictItem(String dictCode, String value) {
      Optional<SysTreeDict> optional = this.treeDictService.findByCode(dictCode);
      Assert.isTrue(optional.isPresent(), "字典不存在{}", new Object[]{dictCode});
      String dictId = ((SysTreeDict)optional.get()).getId();
      SysTreeDictItem sysTreeDictItem = (SysTreeDictItem)this.getOne((Wrapper)((new LambdaQueryWrapper<SysTreeDictItem>()).eq(SysTreeDictItem::getDictId, dictId)).eq(SysTreeDictItem::getValue, value));
      Assert.notNull(sysTreeDictItem, "字典项不存在", new Object[0]);
      SysTreeDictItem parentItem = (SysTreeDictItem)this.getOne((Wrapper)((new LambdaQueryWrapper<SysTreeDictItem>()).eq(SysTreeDictItem::getDictId, dictId)).eq(SysTreeDictItem::getId, sysTreeDictItem.getPid()));
      Assert.notNull(parentItem, "上级字典科室不存在", new Object[0]);
      return parentItem;
   }

   @Transactional(rollbackFor = {Exception.class})
   public boolean removeTreeItemsById(String itemId) {
      SysTreeDictItem root = this.getById(itemId);
      if (root == null) {
         return false;
      }

      List<SysTreeDictItem> items = this.list((Wrapper)Wrappers.<SysTreeDictItem>lambdaQuery()
         .eq(SysTreeDictItem::getDictId, root.getDictId()));
      Map<String, List<String>> childrenByParentId = new HashMap<>();
      for (SysTreeDictItem item : items) {
         childrenByParentId.computeIfAbsent(item.getPid(), key -> new ArrayList<>()).add(item.getId());
      }

      Set<String> itemIds = new HashSet<>();
      ArrayDeque<String> pendingIds = new ArrayDeque<>();
      pendingIds.add(itemId);
      while (!pendingIds.isEmpty()) {
         String currentId = pendingIds.removeFirst();
         if (!itemIds.add(currentId)) {
            continue;
         }
         List<String> children = childrenByParentId.get(currentId);
         if (children != null) {
            pendingIds.addAll(children);
         }
      }

      return this.removeByIds(itemIds);
   }

   public SysTreeDictItem getPeerDictItem(String dictCode, String value) {
      Optional<SysTreeDict> optional = this.treeDictService.findByCode(dictCode);
      Assert.isTrue(optional.isPresent(), "字典不存在{}", new Object[]{dictCode});
      String dictId = ((SysTreeDict)optional.get()).getId();
      SysTreeDictItem sysTreeDictItem = (SysTreeDictItem)this.getOne((Wrapper)((new LambdaQueryWrapper<SysTreeDictItem>()).eq(SysTreeDictItem::getDictId, dictId)).eq(SysTreeDictItem::getValue, value));
      Assert.notNull(sysTreeDictItem, "字典项不存在", new Object[0]);
      return sysTreeDictItem;
   }

   public void exportDict(ExportDictModel exportDictModel, HttpServletResponse response) throws IOException {
      List<SysTreeDict> dict;
      List<SysTreeDictItem> dictItem;
      if (exportDictModel.getDictTreeCode().size() > 0 && exportDictModel.getDictTreeCode() != null) {
         dict = this.treeDictService.list((Wrapper)((new LambdaQueryWrapper<SysTreeDict>()).in(SysTreeDict::getCode, exportDictModel.getDictTreeCode())).eq(SysTreeDict::getIsDelete, 0));
         List<String> collect = (List)dict.stream().map(SysTreeDict::getId).collect(Collectors.toList());
         dictItem = this.list((Wrapper)((new LambdaQueryWrapper<SysTreeDictItem>()).in(SysTreeDictItem::getDictId, collect)).eq(SysTreeDictItem::getIsDelete, 0));
      } else {
         dict = this.treeDictService.list((Wrapper)(new LambdaQueryWrapper<SysTreeDict>()).eq(SysTreeDict::getIsDelete, 0));
         dictItem = this.list((Wrapper)(new LambdaQueryWrapper<SysTreeDictItem>()).eq(SysTreeDictItem::getIsDelete, 0));
         log.info("分级字典导出数量为:{}", dict.size());
         log.info("分级字典项导出数量为:{}", dictItem.size());
      }

      this.getFile(dict, dictItem, response);
   }

   private void getFile(List<SysTreeDict> dict, List<SysTreeDictItem> dictItem, HttpServletResponse response) throws IOException {
      List<SysTreeDictVO> vos = (List)dict.stream().map((info) -> {
         SysTreeDictVO sysTreeDictVO = new SysTreeDictVO();
         BeanUtil.copyProperties(info, sysTreeDictVO, new String[0]);
         ArrayList<SysTreeDictItem> list = new ArrayList();

         for(SysTreeDictItem item : dictItem) {
            if (item.getDictId().equals(info.getId())) {
               list.add(item);
            }
         }

         sysTreeDictVO.setTreeItems(list);
         return sysTreeDictVO;
      }).collect(Collectors.toList());
      String json = JSON.toJSONString(vos);
      String filename = "dictionary.treedict";
      response.setContentType("application/octet-stream");
      response.setHeader("Content-Disposition", "attachment;filename=" + filename);
      ServletOutputStream os = response.getOutputStream();
      os.write(json.getBytes(StandardCharsets.UTF_8));
   }

   @Transactional(
      rollbackFor = {Exception.class}
   )
   @CacheEvict(
      value = {"tree_dict_details_list", "tree_dict_details_tree", "tree_dict_details_city"},
      allEntries = true
   )
   public void importDict(MultipartFile file) throws IOException {
      if (file.isEmpty()) {
         throw new ServiceException("文件不能为空");
      } else {
         int begin = file.getOriginalFilename().indexOf(".") + 1;
         int end = file.getOriginalFilename().length();
         String fileType = file.getOriginalFilename().substring(begin, end);
         if (!fileType.equals("treedict")) {
            throw new ServiceException("不支持的文件类型");
         } else {
            new StringBuffer();
            BufferedReader reader = new BufferedReader(new InputStreamReader(file.getInputStream(), "UTF-8"));
            String line = null;

            String message;
            for(message = new String(); (line = reader.readLine()) != null; message = message + line) {
            }

            List<SysTreeDictVO> dictVOS = JSONObject.parseArray(message, SysTreeDictVO.class);

            try {
               for(SysTreeDictVO info : dictVOS) {
                  SysTreeDict sysDict = new SysTreeDict();
                  BeanUtil.copyProperties(info, sysDict, new String[0]);
                  SysTreeDict sysTreeDict = this.sysTreeDictMapper.selectByCode(info.getCode());
                  this.sysTreeDictMapper.deleteTreeDict(sysDict.getCode());
                  this.sysTreeDictItemMapper.deleteTreeDictItem(sysTreeDict.getId());
                  sysDict.setId((String)null);
                  this.treeDictService.save(sysDict);

                  for(SysTreeDictItem sysTreeDictItem : info.getTreeItems().stream().filter((i) -> i.getPid().equals("0")).collect(Collectors.toList())) {
                     String id = sysTreeDictItem.getId();
                     List<SysTreeDictItem> items = info.getTreeItems().stream().filter((i) -> i.getPid().equals(id)).collect(Collectors.toList());
                     sysTreeDictItem.setDictId(sysDict.getId());
                     sysTreeDictItem.setId((String)null);
                     this.save(sysTreeDictItem);
                     if (CollectionUtil.isNotEmpty(items)) {
                        this.importDictItem(info.getTreeItems(), sysTreeDictItem, id);
                     }
                  }
               }

               log.info("字典导入成功");
            } catch (Exception var19) {
               throw new ServiceException("导入字典失败");
            }
         }
      }
   }

   public void importDictItem(List<SysTreeDictItem> itemList, SysTreeDictItem sysTreeDictItem, String id) {
      for(SysTreeDictItem item : itemList.stream().filter((i) -> i.getPid().equals(id)).collect(Collectors.toList())) {
         List<SysTreeDictItem> items = itemList.stream().filter((i) -> i.getPid().equals(item.getId())).collect(Collectors.toList());
         item.setDictId(sysTreeDictItem.getDictId());
         item.setPid(sysTreeDictItem.getId());
         item.setId((String)null);
         this.save(item);
         if (CollectionUtil.isNotEmpty(items)) {
            this.importDictItem(itemList, item, item.getId());
         }
      }

   }

}
