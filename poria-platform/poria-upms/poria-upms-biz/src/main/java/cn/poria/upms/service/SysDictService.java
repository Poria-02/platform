package cn.poria.upms.service;

import cn.poria.common.core.util.R;
import cn.poria.upms.api.entity.SysDict;
import cn.poria.upms.api.vo.ExportDictModel;
import com.baomidou.mybatisplus.extension.service.IService;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import org.springframework.web.multipart.MultipartFile;

public interface SysDictService extends IService<SysDict> {
   R removeDict(String id);

   R updateDict(SysDict sysDict);

   void exportDict(ExportDictModel exportDictModel, HttpServletResponse response) throws IOException;

   void importDict(MultipartFile file) throws IOException;
}
