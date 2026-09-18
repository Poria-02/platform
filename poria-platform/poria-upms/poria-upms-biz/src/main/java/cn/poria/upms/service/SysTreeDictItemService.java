package cn.poria.upms.service;

import cn.poria.upms.api.entity.SysTreeDictItem;
import cn.poria.upms.api.vo.ExportDictModel;
import com.baomidou.mybatisplus.extension.service.IService;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import org.springframework.web.multipart.MultipartFile;

public interface SysTreeDictItemService extends IService<SysTreeDictItem> {
   boolean removeTreeItemsById(String itemId);

   SysTreeDictItem getParentDictItem(String dictCode, String value);

   SysTreeDictItem getPeerDictItem(String dictCode, String value);

   void exportDict(ExportDictModel exportDictModel, HttpServletResponse response) throws IOException;

   void importDict(MultipartFile file) throws IOException;
}
