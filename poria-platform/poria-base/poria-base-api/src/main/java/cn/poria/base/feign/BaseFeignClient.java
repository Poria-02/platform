package cn.poria.base.feign;

import cn.poria.base.vo.request.FileUploadModel;
import cn.poria.common.core.constant.SecurityConstants;
import cn.poria.common.core.util.R;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

/**
 * @Author: yuhaitao
 * @Date 2020/5/27 9:37 下午
 */
public interface BaseFeignClient {
    /**
     * 获取单位问卷的访问路径(OSS存储的权限为私有读才需要)
     *
     * @param req
     * @return
     */
    @PostMapping("/inner/storage/access")
    R<String> access(AccessRequest req);

    /**
     * 获取多个文件的访问路径(OSS存储的权限为私有读才需要)
     *
     * @param req
     * @return
     */
    @PostMapping("/inner/storage/access_list")
    R<Map<String, String>> accessList(AccessListRequest req);


    @PostMapping("/inner/upload/open_upload")
    R<String> openUpload(@RequestBody FileUploadModel fileUploadModel,
                         @RequestHeader(SecurityConstants.FROM) String from);

    @PostMapping("/inner/upload/pri_upload")
    R<String> priUpload(@RequestBody FileUploadModel fileUploadModel,
                        @RequestHeader(SecurityConstants.FROM) String from);

    @PostMapping("/inner/upload/execl/pri_upload")
    R<String> execlPriUpload(@RequestBody FileUploadModel fileUploadModel,
                        @RequestHeader(SecurityConstants.FROM) String from);

    /**
     * 获取私有文件访问链接
     *
     * @param priUrl
     * @param from
     * @return
     */
    @GetMapping("/inner/upload/presigned/url/{priUrl}")
    R<String> getPresignedUrl(@PathVariable("priUrl") String priUrl, @RequestHeader(SecurityConstants.FROM) String from);

    @PostMapping("/inner/upload/private/transfer")
    R<String> privateTransferFile(@RequestParam("url") String url, @RequestParam("path") String path, @RequestParam("name") String name, @RequestParam("type") String type, @RequestHeader(SecurityConstants.FROM) String from);

    @GetMapping("/inner/upload/remove/public")
    R removePublic(@RequestParam("url") String url, @RequestHeader(SecurityConstants.FROM) String from);

    @GetMapping("/inner/upload/remove/private/{priUrl}")
    R removePrivate(@PathVariable String priUrl, @RequestHeader(SecurityConstants.FROM) String from);
}

