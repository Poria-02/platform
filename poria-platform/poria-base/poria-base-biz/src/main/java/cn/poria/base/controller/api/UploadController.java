package cn.poria.base.controller.api;

import cn.poria.base.service.UploadService;
import cn.poria.base.vo.request.FileSignatureModel;
import cn.poria.base.vo.response.FileSignatureVo;
import cn.poria.base.vo.response.SignUploadVo;
import cn.poria.common.core.util.R;
import cn.poria.common.log.annotation.SysLog;
import cn.poria.common.security.annotation.Inner;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.Map;

@Tag(name = "上传")
@RestController
@Slf4j
@RequestMapping("/api/file")
public class UploadController {

    @Autowired
    private UploadService uploadService;

    @Operation(summary = "上传共有文件)", description = "上传共有文件")
    @PostMapping("/upload/public")
    public R<String> uploadPublic(@RequestParam("file") @Parameter(name = "文件") MultipartFile file,
                                  @RequestParam("path") @Parameter(name = "文件路径") String path) {

        String url = uploadService.uploadPublic(file, path);
        return R.ok(url);
    }

    @Operation(summary = "上传私有文件)", description = "上传私有文件")
    @PostMapping("/upload/private")
    public R<String> uploadPrivate(@RequestParam("file") @Parameter(name = "文件") MultipartFile file,
                                   @RequestParam("path") @Parameter(name = "文件路径") String path) {

        String url = uploadService.uploadPrivate(file, path);
        return R.ok(url);
    }

    @Operation(summary = "获取私有文件访问链接", description = "获取私有文件访问链接")
    @GetMapping("/presigned/url")
    public R<String> getPresignedUrl(@RequestParam("priUrl") @Parameter(name = "私有路径") String priUrl) {
        return R.ok(uploadService.getPresignedUrl(priUrl));
    }

    @Operation(summary = "批量获取私有文件访问链接", description = "批量获取私有文件访问链接")
    @PostMapping("/list/presigned/url")
    public R<Map<String, String>> getPresignedListUrl(@RequestBody List<String> priUrls) {
        return R.ok(uploadService.getPresignedListUrl(priUrls));
    }

    @Operation(summary = "获取文件上传签名url", description = "获取文件上传签名url (1小时过期)（base_upload_sign）")
    @GetMapping("/sign/upload")
    public R<SignUploadVo> getSignUploadUrl(@RequestParam("url") String url) {
        return R.ok(uploadService.getUploadPresignedUrl(url));
    }

    @SysLog("获取上传签名")
    @GetMapping("/signature")
    @Operation(summary = "获取上传签名", description = "获取上传签名（base_upload_get）")
    public R<FileSignatureVo> getSignature(FileSignatureModel model) {
        return R.ok(uploadService.getSignature(model));
    }


}

