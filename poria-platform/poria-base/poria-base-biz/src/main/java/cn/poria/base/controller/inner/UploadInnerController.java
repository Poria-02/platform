package cn.poria.base.controller.inner;

import cn.hutool.core.codec.Base64;
import cn.hutool.core.io.FileUtil;
import cn.hutool.core.util.IdUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.core.util.URLUtil;
import cn.poria.base.oss.service.OssTemplate;
import cn.poria.base.constant.FileTypeConstant;
import cn.poria.base.entity.BaseFile;
import cn.poria.base.service.BaseFileService;
import cn.poria.base.service.UploadService;
import cn.poria.base.util.AssetFileTypeUtil;
import cn.poria.base.vo.request.FileUploadModel;
import cn.poria.common.core.exception.ServiceException;
import cn.poria.common.core.util.R;
import cn.poria.common.security.annotation.Inner;
import io.swagger.v3.oas.annotations.Hidden;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import lombok.SneakyThrows;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.*;

import javax.sql.rowset.serial.SerialBlob;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;

/**
 * 服务间文件上传及对象存储控制层
 */
@Hidden
@RestController
@RequestMapping("/inner/upload")
public class UploadInnerController {

    @Autowired
    private UploadService uploadService;

    @Autowired
    private OssTemplate template;

    @Value("${bucket.publicBucket}")
    public String publicBucket;

    @Value("${bucket.privateBucket}")
    public String privateBucket;

    @Autowired
    private BaseFileService baseFileService;

    /**
     * 上传Base64编码的公有文件
     *
     * @param fileUploadModel 文件内容、名称和存储目录
     * @return 公有文件访问地址
     */
    @SneakyThrows
    @Inner
    @PostMapping("/open_upload")
    @Operation(summary = "公共-上传文件", description = "公共-上传文件")
    public R<String> openUpload(@RequestBody FileUploadModel fileUploadModel) {

        byte[] imageByte = Base64.decode(fileUploadModel.getFile());
        SerialBlob serialBlob = new SerialBlob(imageByte);

        String fileName = fileUploadModel.getPath() + "/" + IdUtil.fastSimpleUUID() + "." + FileUtil.extName(fileUploadModel.getFileName());
        if (AssetFileTypeUtil.fileType(FileUtil.extName(fileUploadModel.getFileName()))) {
            throw new ServiceException("上传失败,不支持该文件类型上传");
        }
        template.createBucket(publicBucket);

        template.putObject(publicBucket, fileName, serialBlob.getBinaryStream(), imageByte.length,
                StrUtil.isBlank(fileUploadModel.getContextType()) ? "application/octet-stream" : fileUploadModel.getContextType());

        String url = template.getObjectURL(publicBucket, fileName, 7);

        String[] urls = url.split("\\?");

        baseFileService.saveBase64File(fileName, publicBucket, FileTypeConstant.PUBLIC);

        return R.ok(urls[0]);

//        return R.ok(ossService.builder()
//                .uploadInputStrean(serialBlob.getBinaryStream())
//                .path(fileUploadModel.getPath())
//                .extName(FileUtil.extName(fileUploadModel.getFileName()))
//                .upload());
    }

    /**
     * 上传Base64编码的私有文件
     *
     * @param fileUploadModel 文件内容、名称和存储目录
     * @return 私有文件标识
     */
    @SneakyThrows
    @Inner
    @PostMapping("/pri_upload")
    @Operation(summary = "私有-上传文件", description = "私有-上传文件")
    public R<String> priUpload(@RequestBody FileUploadModel fileUploadModel) throws IOException {

        byte[] imageByte = Base64.decode(fileUploadModel.getFile());
        SerialBlob serialBlob = new SerialBlob(imageByte);

//        String objName = ossService.builder()
//                .uploadInputStrean(serialBlob.getBinaryStream())
//                .path(fileUploadModel.getPath())
//                .extName(FileUtil.extName(fileUploadModel.getFileName()))
//                .isPri()
//                .upload();
//
//        return R.ok("//" + objName);
        String fileName = fileUploadModel.getPath() + "/" + IdUtil.fastSimpleUUID() + "." + FileUtil.extName(fileUploadModel.getFileName());
        if (AssetFileTypeUtil.fileType(FileUtil.extName(fileUploadModel.getFileName()))) {
            throw new ServiceException("上传失败,不支持该文件类型上传");
        }
        template.createBucket(privateBucket);
        template.putObject(privateBucket, fileName, serialBlob.getBinaryStream(), imageByte.length,
                StrUtil.isBlank(fileUploadModel.getContextType()) ? "application/octet-stream" : fileUploadModel.getContextType());
        BaseFile baseFile = baseFileService.saveBase64File(fileName, privateBucket, FileTypeConstant.PRIVATE);
        return R.ok(baseFile.getId());
    }

    /**
     * 获取私有文件访问链接
     *
     * @param priUrl 私有文件标识
     * @return 临时访问链接
     */
    @Inner
    @GetMapping("/presigned/url/{priUrl}")
    @Operation(summary = "获取私有文件访问链接", description = "获取私有文件访问链接")
    public R<String> getPresignedUrl(@PathVariable("priUrl") @Parameter(name = "私有路径") String priUrl) {
        return R.ok(uploadService.getPresignedUrl(priUrl));
    }

    /**
     * 将远程文件转存为私有文件
     *
     * @param fileUrl 远程文件地址
     * @param path 对象存储目录
     * @param name 可选文件后缀
     * @param type 可选文件内容类型
     * @return 转存后的私有文件标识
     */
    @SneakyThrows
    @Inner
    @PostMapping("/private/transfer")
    @Operation(summary = "将远程文件转存为私有文件", description = "将远程文件转存为私有文件")
    public R<String> privateTransferFile(@RequestParam("url") String fileUrl,
                                         @RequestParam("path") String path,
                                         @RequestParam(value = "name", required = false) String name,
                                         @RequestParam(value = "type", required = false) String type) {

        URL url = URLUtil.toUrlForHttp(fileUrl);
        HttpURLConnection connection = (HttpURLConnection) url.openConnection();
        InputStream inputStream = connection.getInputStream();

        String fileName;
        //name -- 问卷后缀名  不指定文件后缀名传null
        if (StrUtil.isNotBlank(name)) {
            fileName = path + "/" + IdUtil.fastSimpleUUID() + "." + name;
            if (AssetFileTypeUtil.fileType(name)) {
                throw new ServiceException("上传失败,不支持该文件类型上传");
            }
        } else {
            fileName = path + "/" + IdUtil.fastSimpleUUID() + "." + FileUtil.extName(fileUrl);
            if (AssetFileTypeUtil.fileType(FileUtil.extName(fileUrl))) {
                throw new ServiceException("上传失败,不支持该文件类型上传");
            }
        }

        // 将文件上传到S3
        template.putObject(privateBucket, fileName, inputStream, connection.getContentLength(), StrUtil.isBlank(type) ? "application/octet-stream" : type);
        inputStream.close();

        BaseFile baseFile = baseFileService.saveBase64File(fileName, privateBucket, FileTypeConstant.PRIVATE);
        return R.ok(baseFile.getId());

    }

    /**
     * 保存私有文件记录
     *
     * @param fileUploadModel 文件名称及相关信息
     * @return 私有文件标识
     */
    @SneakyThrows
    @Inner(value = false)
    @PostMapping("/execl/pri_upload")
    @Operation(summary = "私有-上传文件", description = "私有-上传文件")
    public R<String> execlPriUpload(@RequestBody FileUploadModel fileUploadModel) {
        BaseFile baseFile = baseFileService.saveBase64File(fileUploadModel.getFileName(), privateBucket, FileTypeConstant.PRIVATE);
        return R.ok(baseFile.getId());
    }

    /**
     * 删除公有文件
     *
     * @param url 公有文件访问地址
     * @return 删除结果
     */
    @SneakyThrows
    @Inner
    @GetMapping("/remove/public")
    @Operation(summary = "删除公有文件", description = "删除公有文件")
    public R removePublic(@RequestParam("url") String url) {
        URL urls = URLUtil.toUrlForHttp(url);
        String path = urls.getPath();
        if (path.startsWith("/")) {
            path = path.substring(1);
        }
        template.removeObject(publicBucket, path);
        return R.ok();
    }

    /**
     * 删除私有文件
     *
     * @param priUrl 私有文件标识
     * @return 删除结果
     */
    @SneakyThrows
    @Inner
    @GetMapping("/remove/private/{priUrl}")
    @Operation(summary = "删除私有文件", description = "删除私有文件")
    public R removePrivate(@PathVariable String priUrl) {
        BaseFile baseFile = baseFileService.getById(priUrl);
        template.removeObject(privateBucket, baseFile.getFileName());
        return R.ok();
    }
}
