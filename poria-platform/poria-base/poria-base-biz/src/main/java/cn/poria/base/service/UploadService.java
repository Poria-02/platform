package cn.poria.base.service;


import cn.hutool.core.io.FileUtil;
import cn.hutool.core.util.IdUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.http.HttpRequest;
import cn.hutool.http.HttpResponse;
import cn.hutool.http.HttpUtil;
import cn.hutool.json.JSONObject;
import cn.hutool.json.JSONUtil;
import cn.poria.base.oss.service.OssTemplate;
import cn.poria.base.config.AudioConverter;
import cn.poria.base.constant.FileTypeConstant;
import cn.poria.base.entity.BaseFile;
import cn.poria.base.service.dto.ReservePushDto;
import cn.poria.base.util.AssetFileTypeUtil;
import cn.poria.base.vo.request.FileSignatureModel;
import cn.poria.base.vo.response.FileSignatureVo;
import cn.poria.base.vo.response.SignUploadVo;
import cn.poria.common.core.exception.ServiceException;
import cn.poria.common.core.util.Assert;
import cn.poria.common.data.util.EncryptTypeUtil;
import com.alibaba.excel.EasyExcel;
import com.alibaba.excel.ExcelWriter;
import com.alibaba.excel.context.AnalysisContext;
import com.alibaba.excel.read.listener.ReadListener;
import com.alibaba.excel.support.ExcelTypeEnum;
import com.alibaba.excel.write.metadata.WriteSheet;
import com.alibaba.excel.write.style.column.LongestMatchColumnWidthStyleStrategy;
import com.aliyun.oss.OSSClient;
import com.aliyun.oss.common.utils.BinaryUtil;
import com.aliyun.oss.model.MatchMode;
import com.aliyun.oss.model.PolicyConditions;
import com.amazonaws.HttpMethod;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import javax.sql.rowset.serial.SerialBlob;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.sql.Date;
import java.util.ArrayList;
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
        if (AssetFileTypeUtil.fileType(extension)) {
            throw new ServiceException("上传失败,不支持该文件类型上传");
        }

        template.createBucket(publicBucket);
        if (audioConverter.isSupportedFormat(extension)) {
            File targetfile = audioConverter.convertToMp3(file, simpleUUID, extension);
            if (targetfile == null) {
                throw new ServiceException("音频转换失败");
            }
            fileName = path + "/" + simpleUUID + ".mp3";
            ;

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
        if (AssetFileTypeUtil.fileType(extension)) {
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
        String url = template.getObjectURL(privateBucket, baseFile.getFileName(), 1);

        return url;
    }

    /**
     * @param
     * @author dongruipeng
     * @date 2023/3/8
     * @description 批量获取私有文件访问地址
     */
    public Map<String, String> getPresignedListUrl(List<String> fileId) {
        List<BaseFile> baseFileList = baseFileService.getBaseMapper().selectBatchIds(fileId);
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

        OSSClient client = new OSSClient(endpoint, accessKey, secretKey);

        String host = "https://" + ("public".equals(model.getType()) ? publicBucket : privateBucket) + "." + endpoint.replace("https://", "");

        long expireTime = 3600;
        long expireEndTime = System.currentTimeMillis() + expireTime * 1000;
        Date expiration = new Date(expireEndTime);
        PolicyConditions policyConds = new PolicyConditions();
        policyConds.addConditionItem(PolicyConditions.COND_CONTENT_LENGTH_RANGE, 0, 1048576000);
        policyConds.addConditionItem(MatchMode.StartWith, PolicyConditions.COND_KEY, model.getPath());
        String postPolicy = client.generatePostPolicy(expiration, policyConds);
        byte[] binaryData = postPolicy.getBytes("utf-8");
        String encodedPolicy = BinaryUtil.toBase64String(binaryData);
        String postSignature = client.calculatePostSignature(postPolicy);

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

