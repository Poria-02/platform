package cn.poria.base.controller.api;

import cn.poria.base.service.UploadService;
import cn.poria.base.vo.request.FileSignatureModel;
import cn.poria.base.vo.response.FileSignatureVo;
import cn.poria.base.vo.response.SignUploadVo;
import cn.poria.common.core.util.R;
import cn.poria.common.log.annotation.SysLog;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.Map;

/**
 * 管理平台文件上传及访问链接控制层
 */
@Tag(name = "上传")
@RestController
@Slf4j
@RequestMapping("/api/file")
public class UploadController {

    @Autowired
    private UploadService uploadService;

    /**
     * 上传公有文件
     *
     * @param file 上传的文件
     * @param path 对象存储目录
     * @return 公有文件访问地址
     */
    @PostMapping("/upload/public")
    @Operation(summary = "上传共有文件", description = "上传共有文件")
    public R<String> uploadPublic(@RequestParam("file") @Parameter(name = "文件") MultipartFile file,
                                  @RequestParam("path") @Parameter(name = "文件路径") String path) {

        String url = uploadService.uploadPublic(file, path);
        return R.ok(url);
    }

    /**
     * 上传私有文件
     *
     * @param file 上传的文件
     * @param path 对象存储目录
     * @return 私有文件标识
     */
    @PostMapping("/upload/private")
    @Operation(summary = "上传私有文件", description = "上传私有文件")
    public R<String> uploadPrivate(@RequestParam("file") @Parameter(name = "文件") MultipartFile file,
                                   @RequestParam("path") @Parameter(name = "文件路径") String path) {

        String url = uploadService.uploadPrivate(file, path);
        return R.ok(url);
    }

    /**
     * 获取私有文件访问链接
     *
     * @param priUrl 私有文件标识
     * @return 临时访问链接
     */
    @GetMapping("/presigned/url")
    @Operation(summary = "获取私有文件访问链接", description = "获取私有文件访问链接")
    public R<String> getPresignedUrl(@RequestParam("priUrl") @Parameter(name = "私有路径") String priUrl) {
        return R.ok(uploadService.getPresignedUrl(priUrl));
    }

    /**
     * 批量获取私有文件访问链接
     *
     * @param priUrls 私有文件标识列表
     * @return 文件标识与临时访问链接的对应关系
     */
    @PostMapping("/list/presigned/url")
    @Operation(summary = "批量获取私有文件访问链接", description = "批量获取私有文件访问链接")
    public R<Map<String, String>> getPresignedListUrl(@RequestBody List<String> priUrls) {
        return R.ok(uploadService.getPresignedListUrl(priUrls));
    }

    /**
     * 获取文件上传签名链接
     *
     * @param url 目标文件地址
     * @return 上传签名链接及相关信息
     */
    @GetMapping("/sign/upload")
    @Operation(summary = "获取文件上传签名url", description = "获取文件上传签名url (1小时过期)")
    public R<SignUploadVo> getSignUploadUrl(@RequestParam("url") String url) {
        return R.ok(uploadService.getUploadPresignedUrl(url));
    }

    /**
     * 获取文件上传签名
     *
     * @param model 上传签名请求参数
     * @return 上传签名信息
     */
    @SysLog("获取上传签名")
    @GetMapping("/signature")
    @Operation(summary = "获取上传签名", description = "获取上传签名")
    public R<FileSignatureVo> getSignature(FileSignatureModel model) {
        return R.ok(uploadService.getSignature(model));
    }

}
