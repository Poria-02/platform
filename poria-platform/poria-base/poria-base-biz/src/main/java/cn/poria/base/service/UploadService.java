package cn.poria.base.service;


import cn.hutool.core.codec.Base64;
import cn.hutool.core.io.FileUtil;
import cn.hutool.core.util.IdUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.json.JSONObject;
import cn.poria.base.oss.service.OssTemplate;
import cn.poria.base.config.AudioConverter;
import cn.poria.base.constant.FileTypeConstant;
import cn.poria.base.entity.BaseFile;
import cn.poria.base.util.AssetFileTypeUtil;
import cn.poria.base.vo.request.FileSignatureModel;
import cn.poria.base.vo.response.FileSignatureVo;
import cn.poria.base.vo.response.SignUploadVo;
import cn.poria.common.core.exception.ServiceException;
import cn.poria.common.core.util.Assert;
import com.aliyun.oss.OSS;
import com.aliyun.oss.OSSClientBuilder;
import com.aliyun.oss.model.MatchMode;
import com.aliyun.oss.model.PolicyConditions;
import com.amazonaws.HttpMethod;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.FileInputStream;
import java.nio.charset.StandardCharsets;
import java.sql.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author zhangchunlei
 * @date 2021年07月28日 2:18 下午
 */
@Service
@Slf4j
public class UploadService {

    @Autowired
    private BaseFileService baseFileService;

    @Autowired
    private OssTemplate template;

    @Value("${bucket.publicBucket}")
    public String publicBucket;

    @Value("${bucket.privateBucket}")
    public String privateBucket;

    @Value("${oss.endpoint}")
    public String endpoint;

    @Value("${oss.access-key}")
    public String accessKey;

    @Value("${oss.secret-key}")
    public String secretKey;

    @Autowired
    private AudioConverter audioConverter;

    /**
     * 上传公共文件
     *
     * @param file 资源
     * @return R(bucketName, filename)
     */
    @SneakyThrows
    public String uploadPublic(MultipartFile file, String path) {

        checkFile(file.getContentType());
        String extension = FileUtil.extName(file.getOriginalFilename());
        String simpleUUID = IdUtil.fastSimpleUUID();

        String fileName = path + "/" + simpleUUID + "." + extension;
        if (extension != null && AssetFileTypeUtil.fileType(extension)) {
            throw new ServiceException("上传失败,不支持该文件类型上传");
        }

        template.createBucket(publicBucket);
        if (audioConverter.isSupportedFormat(extension)) {
            File targetfile = audioConverter.convertToMp3(file, simpleUUID, extension);
            if (targetfile == null) {
                throw new ServiceException("音频转换失败");
            }
            fileName = path + "/" + simpleUUID + ".mp3";


            FileInputStream fileInputStream = new FileInputStream(targetfile);
            if (targetfile.exists()) {
                targetfile.delete();
                log.info("删除成功");
            }
            template.putObject(publicBucket, fileName, fileInputStream, file.getSize(), file.getContentType());
        } else {
            template.putObject(publicBucket, fileName, file.getInputStream(), file.getSize(), file.getContentType());
        }

        String url = template.getObjectURL(publicBucket, fileName, 7);

        String[] urls = url.split("\\?");

        baseFileService.saveBaseFile(file, fileName, publicBucket, FileTypeConstant.PUBLIC);

        return urls[0];

    }

    /**
     * 上传私有文件
     *
     * @param file 资源
     * @return R(bucketName, filename)
     */
    @SneakyThrows
    public String uploadPrivate(MultipartFile file, String path) {

        checkFile(file.getContentType());

        String extension = FileUtil.extName(file.getOriginalFilename());
        String simpleUUID = IdUtil.fastSimpleUUID();

        String fileName = path + "/" + simpleUUID + "." + extension;
        if (extension != null && AssetFileTypeUtil.fileType(extension)) {
            throw new ServiceException("上传失败,不支持该文件类型上传");
        }
        template.createBucket(privateBucket);

        if (audioConverter.isSupportedFormat(extension)) {
            File targetfile = audioConverter.convertToMp3(file, simpleUUID, extension);
            if (targetfile == null) {
                throw new ServiceException("音频转换失败");
            }
            fileName = path + "/" + simpleUUID + ".mp3";

            FileInputStream fileInputStream = new FileInputStream(targetfile);
            if (targetfile.exists()) {
                targetfile.delete();
                log.info("删除成功");
            }
            template.putObject(privateBucket, fileName, fileInputStream, file.getSize(), file.getContentType());
        } else {
            template.putObject(privateBucket, fileName, file.getInputStream(), file.getSize(), file.getContentType());
        }


        BaseFile baseFile = baseFileService.saveBaseFile(file, fileName, privateBucket, FileTypeConstant.PRIVATE);

        return baseFile.getId();

    }

    /**
     * 获取私有文件访问地址
     *
     * @author zhangchunlei
     * @date 2021/7/28 10:20 下午
     */
    public String getPresignedUrl(String fileId) {

        BaseFile baseFile = baseFileService.getById(fileId);
        Assert.notNull(baseFile, "文件不存在");

        return template.getObjectURL(privateBucket, baseFile.getFileName(), 1);
    }

    /**
     * @param
     * @author dongruipeng
     * @date 2023/3/8
     * @description 批量获取私有文件访问地址
     */
    public Map<String, String> getPresignedListUrl(List<String> fileId) {
        List<BaseFile> baseFileList = baseFileService.getBaseMapper().selectByIds(fileId);
        if (baseFileList.isEmpty()) return null;

        HashMap<String, String> map = new HashMap<>();
        for (BaseFile baseFile : baseFileList) {
            String url = template.getObjectURL(privateBucket, baseFile.getFileName(), 1);
            map.putIfAbsent(baseFile.getId(), url);
        }
        return map;
    }

    public SignUploadVo getUploadPresignedUrl(String url) {
        BaseFile baseFile = new BaseFile();
        baseFile.setFileName(url);
        baseFile.setType("2");
        baseFile.setBucketName(privateBucket);
        baseFile.setOriginal("");
        baseFileService.save(baseFile);
        String result = template.getObjectURL(privateBucket, url, 1, HttpMethod.PUT);
        return new SignUploadVo(baseFile.getId(), result);
    }

    /**
     * 获取上传签名
     *
     * @param model
     * @return
     */
    @SneakyThrows
    public FileSignatureVo getSignature(FileSignatureModel model) {

        OSS client = new OSSClientBuilder().build(endpoint, accessKey, secretKey);

        String host = "https://" + ("public".equals(model.getType()) ? publicBucket : privateBucket) + "." + endpoint.replace("https://", "");

        long expireTime = 3600;
        long expireEndTime = System.currentTimeMillis() + expireTime * 1000;
        Date expiration = new Date(expireEndTime);
        PolicyConditions policyConds = new PolicyConditions();
        policyConds.addConditionItem(PolicyConditions.COND_CONTENT_LENGTH_RANGE, 0, 1048576000);
        policyConds.addConditionItem(MatchMode.StartWith, PolicyConditions.COND_KEY, model.getPath());
        String postPolicy;
        String postSignature;
        try {
            postPolicy = client.generatePostPolicy(expiration, policyConds);
            postSignature = client.calculatePostSignature(postPolicy);
        } finally {
            client.shutdown();
        }
        String encodedPolicy = Base64.encode(postPolicy.getBytes(StandardCharsets.UTF_8));

        JSONObject respMap = new JSONObject();
        respMap.set("accessid", accessKey);
        respMap.set("policy", encodedPolicy);
        respMap.set("signature", postSignature);
        respMap.set("dir", model.getPath());
        respMap.set("host", host);
        respMap.set("expire", String.valueOf(expireEndTime / 1000));

        if ("public".equals(model.getType())) {
            //生成访问链接
            return new FileSignatureVo(host + "/" + model.getPath(), respMap.toString());
        } else {
            BaseFile baseFile = baseFileService.saveBaseFile(null, model.getPath(), privateBucket, FileTypeConstant.PRIVATE);
            return new FileSignatureVo(baseFile.getId(), respMap.toString());
        }
    }

    private void checkFile(String contentType) {
        if (!(StrUtil.isBlank(contentType) || contentType.startsWith("image/") || contentType.startsWith("video/") ||
                contentType.startsWith("audio/") || contentType.startsWith("application/pdf"))) {
            log.error("不支持的文件类型");
            throw new ServiceException("上传失败,不支持该文件上传");
        }
    }
}

