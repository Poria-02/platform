package cn.poria.base.service;

import com.baomidou.mybatisplus.extension.service.IService;
import cn.poria.base.entity.BaseFile;
import cn.poria.base.vo.request.FileUploadModel;
import org.springframework.web.multipart.MultipartFile;

/**
 * 文件管理表(BaseFile)表服务接口
 *
 * @author makejava
 * @since 2021-07-29 10:37:17
 */
public interface BaseFileService extends IService<BaseFile> {

    public BaseFile saveBaseFile(MultipartFile file, String fileName,String bucketName,String type);

    public BaseFile saveBase64File( String fileName, String bucketName, String type);
}

