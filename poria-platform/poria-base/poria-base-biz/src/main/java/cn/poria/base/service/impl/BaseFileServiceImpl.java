package cn.poria.base.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import cn.poria.base.dao.BaseFileDao;
import cn.poria.base.entity.BaseFile;
import cn.poria.base.service.BaseFileService;
import cn.poria.base.vo.request.FileUploadModel;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

/**
 * 文件管理表(BaseFile)表服务实现类
 *
 * @author makejava
 * @since 2021-07-29 10:37:17
 */
@Service("baseFileService")
public class BaseFileServiceImpl extends ServiceImpl<BaseFileDao, BaseFile> implements BaseFileService {

    @Override
    public BaseFile saveBaseFile(MultipartFile file, String fileName,String bucketName,String type) {

        BaseFile baseFile = new BaseFile();
        baseFile.setBucketName(bucketName);
        baseFile.setFileName(fileName);
        baseFile.setOriginal(file ==null? "" : file.getOriginalFilename());
        baseFile.setFileSize(file ==null? 0 : file.getSize());
        baseFile.setType(type);
        this.save(baseFile);
        return baseFile;
    }

    @Override
    public BaseFile saveBase64File(String fileName, String bucketName, String type) {
        BaseFile baseFile = new BaseFile();
        baseFile.setBucketName(bucketName);
        baseFile.setFileName(fileName);
        baseFile.setType(type);
        this.save(baseFile);
        return baseFile;
    }
}

