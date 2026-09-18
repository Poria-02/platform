package cn.poria.upms.service.impl;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.json.JSONObject;
import cn.hutool.json.JSONUtil;
import cn.poria.common.core.constant.enums.DictTypeEnum;
import cn.poria.common.core.exception.ServiceException;
import cn.poria.common.core.util.R;
import cn.poria.upms.api.entity.SysDict;
import cn.poria.upms.api.entity.SysDictItem;
import cn.poria.upms.api.vo.ExportDictModel;
import cn.poria.upms.api.vo.SysDictVO;
import cn.poria.upms.mapper.SysDictItemMapper;
import cn.poria.upms.mapper.SysDictMapper;
import cn.poria.upms.service.SysDictItemService;
import cn.poria.upms.service.SysDictService;

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
import java.util.List;
import java.util.stream.Collectors;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

@Service
public class SysDictServiceImpl extends ServiceImpl<SysDictMapper, SysDict> implements SysDictService {
   private static final Logger log = LoggerFactory.getLogger(SysDictServiceImpl.class);
   private final SysDictItemService sysDictItemService;
   private final SysDictItemMapper sysDictItemMapper;
   private final SysDictMapper sysDictMapper;

   @CacheEvict(value = {"dict_details"}, allEntries = true)
   @Transactional(rollbackFor = {Exception.class})
   public R removeDict(String id) {
      SysDict dict = (SysDict)this.getById(id);
      if (DictTypeEnum.SYSTEM.getType().equals(dict.getSystem())) {
         return R.failed("系统内置字典不能删除");
      } else {
         ((SysDictMapper)this.baseMapper).deleteById(id);
         this.sysDictItemService.remove((Wrapper)Wrappers.<SysDictItem>lambdaQuery().eq(SysDictItem::getDictId, id));
         return R.ok();
      }
   }

   public R updateDict(SysDict dict) {
      SysDict sysDict = (SysDict)this.getById(dict.getId());
      return DictTypeEnum.SYSTEM.getType().equals(sysDict.getSystem()) ? R.failed("系统内置字典不能修改") : R.ok(this.updateById(dict));
   }

   public void exportDict(ExportDictModel exportDictModel, HttpServletResponse response) throws IOException {
      List<SysDict> dict;
      List<SysDictItem> dictItem;
      if (exportDictModel.getDictTypes().size() > 0 && exportDictModel.getDictTypes() != null) {
         dict = this.list((Wrapper)((new LambdaQueryWrapper<SysDict>()).in(SysDict::getType, exportDictModel.getDictTypes())).eq(SysDict::getDelFlag, 0));
         dictItem = this.sysDictItemService.list((Wrapper)((new LambdaQueryWrapper<SysDictItem>()).in(SysDictItem::getType, exportDictModel.getDictTypes())).eq(SysDictItem::getDelFlag, 0));
      } else {
         dict = this.list((Wrapper)(new LambdaQueryWrapper<SysDict>()).eq(SysDict::getDelFlag, 0));
         dictItem = this.sysDictItemService.list((Wrapper)(new LambdaQueryWrapper<SysDictItem>()).eq(SysDictItem::getDelFlag, 0));
         log.info("字典导出数量为:{}", dict.size());
         log.info("字典项导出数量为:{}", dictItem.size());
      }

      this.getFile(dict, dictItem, response);
   }

   private void getFile(List<SysDict> dict, List<SysDictItem> dictItem, HttpServletResponse response) throws IOException {
      List<SysDictVO> vos = (List)dict.stream().map((info) -> {
         SysDictVO sysDictVO = new SysDictVO();
         BeanUtil.copyProperties(info, sysDictVO, new String[0]);
         ArrayList<SysDictItem> list = new ArrayList();

         for(SysDictItem item : dictItem) {
            if (item.getType().equals(info.getType())) {
               list.add(item);
            }
         }

         sysDictVO.setItems(list);
         return sysDictVO;
      }).collect(Collectors.toList());
      String json = JSONUtil.toJsonStr(vos);
      String filename = "dictionary.dict";
      response.setContentType("application/octet-stream");
      response.setHeader("Content-Disposition", "attachment;filename=" + filename);
      ServletOutputStream os = response.getOutputStream();
      os.write(json.getBytes(StandardCharsets.UTF_8));
   }

   @Transactional(
      rollbackFor = {Exception.class}
   )
   @CacheEvict(
      value = {"dict_details"},
      allEntries = true
   )
   public void importDict(MultipartFile file) throws IOException {
      if (file.isEmpty()) {
         throw new ServiceException("文件不能为空");
      } else {
         int begin = file.getOriginalFilename().indexOf(".") + 1;
         int end = file.getOriginalFilename().length();
         String fileType = file.getOriginalFilename().substring(begin, end);
         if (!fileType.equals("dict")) {
            throw new ServiceException("不支持的文件类型");
         } else {
//            BufferedReader reader = new BufferedReader(new InputStreamReader(file.getInputStream(), "UTF-8"));
//            String line = null;
//
//            String message;
//            for(message = new String(); (line = reader.readLine()) != null; message = message + line) {
//            }
//
//            List<SysDictVO> dictVOS = JSONObject.parseArray(message, SysDictVO.class);
//            ArrayList<SysDictItem> sysDictItems = new ArrayList();
//
//            try {
//               for(SysDictVO info : dictVOS) {
//                  SysDict sysDict = new SysDict();
//                  BeanUtil.copyProperties(info, sysDict, new String[0]);
//                  String id = sysDict.getId();
//                  this.sysDictMapper.deleteDict(sysDict.getType());
//                  sysDict.setId((String)null);
//                  this.save(sysDict);
//                  this.sysDictItemMapper.deleteDictItem(sysDict.getType());
//
//                  for(SysDictItem item : info.getItems()) {
//                     if (item.getDictId().equals(id)) {
//                        SysDictItem sysDictItem = new SysDictItem();
//                        BeanUtil.copyProperties(item, sysDictItem, new String[0]);
//                        sysDictItem.setId((String)null);
//                        sysDictItem.setDictId(sysDict.getId());
//                        sysDictItems.add(sysDictItem);
//                     }
//                  }
//               }
//
//               this.sysDictItemService.saveBatch(sysDictItems);
//               log.info("字典导入成功");
//            } catch (Exception var17) {
//               throw new ServiceException("导入字典失败");
//            }
         }
      }
   }

   public SysDictServiceImpl(final SysDictItemService sysDictItemService, final SysDictItemMapper sysDictItemMapper, final SysDictMapper sysDictMapper) {
      this.sysDictItemService = sysDictItemService;
      this.sysDictItemMapper = sysDictItemMapper;
      this.sysDictMapper = sysDictMapper;
   }

}
