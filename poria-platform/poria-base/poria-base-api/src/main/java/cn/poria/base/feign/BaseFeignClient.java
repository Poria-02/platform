package cn.poria.base.feign;

import cn.poria.base.vo.request.FileUploadModel;
import cn.poria.common.core.constant.SecurityConstants;
import cn.poria.common.core.util.R;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

/**
 * @Date 2020/5/27 9:37 下午
 */
public interface BaseFeignClient {
    /**
     * 获取单位问卷的访问路径(OSS存储的权限为私有读才需要)
     *
     * @param req 访问路径请求
     * @return 文件访问路径
     */
    @PostMapping("/inner/storage/access")
    R<String> access(AccessRequest req);

    /**
     * 获取多个文件的访问路径(OSS存储的权限为私有读才需要)
     *
     * @param req 批量访问路径请求
     * @return 文件标识与访问路径的对应关系
     */
    @PostMapping("/inner/storage/access_list")
    R<Map<String, String>> accessList(AccessListRequest req);

    /**
     * 上传公有文件
     *
     * @param fileUploadModel 文件内容、名称和存储目录
     * @param from 内部调用来源标识
     * @return 公有文件访问地址
     */
    @PostMapping("/inner/upload/open_upload")
    R<String> openUpload(@RequestBody FileUploadModel fileUploadModel,
                         @RequestHeader(SecurityConstants.FROM) String from);

    /**
     * 上传私有文件
     *
     * @param fileUploadModel 文件内容、名称和存储目录
     * @param from 内部调用来源标识
     * @return 私有文件标识
     */
    @PostMapping("/inner/upload/pri_upload")
    R<String> priUpload(@RequestBody FileUploadModel fileUploadModel,
                        @RequestHeader(SecurityConstants.FROM) String from);

    /**
     * 保存私有文件记录
     *
     * @param fileUploadModel 文件名称及相关信息
     * @param from 内部调用来源标识
     * @return 私有文件标识
     */
    @PostMapping("/inner/upload/execl/pri_upload")
    R<String> execlPriUpload(@RequestBody FileUploadModel fileUploadModel,
                        @RequestHeader(SecurityConstants.FROM) String from);

    /**
     * 获取私有文件访问链接
     *
     * @param priUrl 私有文件标识
     * @param from 内部调用来源标识
     * @return 临时访问链接
     */
    @GetMapping("/inner/upload/presigned/url/{priUrl}")
    R<String> getPresignedUrl(@PathVariable("priUrl") String priUrl, @RequestHeader(SecurityConstants.FROM) String from);

    /**
     * 将远程文件转存为私有文件
     *
     * @param url 远程文件地址
     * @param path 对象存储目录
     * @param name 可选文件后缀
     * @param type 可选文件内容类型
     * @param from 内部调用来源标识
     * @return 转存后的私有文件标识
     */
    @PostMapping("/inner/upload/private/transfer")
    R<String> privateTransferFile(@RequestParam("url") String url, @RequestParam("path") String path, @RequestParam("name") String name, @RequestParam("type") String type, @RequestHeader(SecurityConstants.FROM) String from);

    /**
     * 删除公有文件
     *
     * @param url 公有文件访问地址
     * @param from 内部调用来源标识
     * @return 删除结果
     */
    @GetMapping("/inner/upload/remove/public")
    R removePublic(@RequestParam("url") String url, @RequestHeader(SecurityConstants.FROM) String from);

    /**
     * 删除私有文件
     *
     * @param priUrl 私有文件标识
     * @param from 内部调用来源标识
     * @return 删除结果
     */
    @GetMapping("/inner/upload/remove/private/{priUrl}")
    R removePrivate(@PathVariable String priUrl, @RequestHeader(SecurityConstants.FROM) String from);
}
