package cn.poria.base.controller.inner;

import cn.hutool.core.codec.Base64;
import cn.hutool.core.io.FileUtil;
import cn.hutool.core.util.IdUtil;
import cn.hutool.core.util.StrUtil;
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

    @SneakyThrows
    @Inner
    @Operation(summary = "公共-上传文件")
    @PostMapping("/open_upload")
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

    @SneakyThrows
    @Inner
    @Operation(summary = "私有-上传文件")
    @PostMapping("/pri_upload")
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


    @Inner
    @Operation(summary = "获取私有文件访问链接", description = "获取私有文件访问链接")
    @GetMapping("/presigned/url/{priUrl}")
    public R<String> getPresignedUrl(@PathVariable("priUrl") @Parameter(name = "私有路径") String priUrl) {
        return R.ok(uploadService.getPresignedUrl(priUrl));
    }

    @SneakyThrows
    @Inner
    @PostMapping("/private/transfer")
    public R<String> privateTransferFile(@RequestParam("url") String fileUrl,
                                         @RequestParam("path") String path,
                                         @RequestParam(value = "name", required = false) String name,
                                         @RequestParam(value = "type", required = false) String type) {

        URL url = new URL(fileUrl);
        HttpURLConnection connection = (HttpURLConnection) url.openConnection();
        InputStream inputStream = connection.getInputStream();

        String fileName = null;
        //name -- 问卷后缀名  不指定文件后缀名传null
        if (StrUtil.isNotBlank(name)) {
            fileName = path + "/" + IdUtil.fastSimpleUUID() + "." + name;
            if (AssetFileTypeUtil.fileType(name)){
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

    @SneakyThrows
    @Inner(value = false)
    @Operation(summary = "私有-上传文件")
    @PostMapping("/execl/pri_upload")
    public R<String> execlPriUpload(@RequestBody FileUploadModel fileUploadModel) {
        BaseFile baseFile = baseFileService.saveBase64File(fileUploadModel.getFileName(), privateBucket, FileTypeConstant.PRIVATE);
        return R.ok(baseFile.getId());
    }


    @Inner(value = false)
    @Operation(summary = "阳曲录音转换清洗")
    @PostMapping("/yangqu/converter")
    public void yangqu(@RequestParam String id, @RequestParam String startTime, @RequestParam String endTime) {
        //id，查单条调试;
        uploadService.yangqu(id, startTime, endTime);
    }
    @SneakyThrows
    @Inner
    @Operation(summary = "删除公有文件")
    @GetMapping("/remove/public")
    public R removePublic(@RequestParam("url") String url) {
        URL urls = new URL(url);
        String path = urls.getPath();
        if (path.startsWith("/")) {
            path = path.substring(1);
        }
        template.removeObject(publicBucket, path);
        return R.ok();
    }

    @SneakyThrows
    @Inner
    @Operation(summary = "删除私有文件")
    @GetMapping("/remove/private/{priUrl}")
    public R removePrivate(@PathVariable String priUrl) {
        BaseFile baseFile = baseFileService.getById(priUrl);
        template.removeObject(privateBucket, baseFile.getFileName());
        return R.ok();
    }
}

