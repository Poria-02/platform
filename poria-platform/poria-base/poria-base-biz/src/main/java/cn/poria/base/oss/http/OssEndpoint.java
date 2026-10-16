

package cn.poria.base.oss.http;

import cn.poria.base.oss.service.OssTemplate;
import com.amazonaws.services.s3.model.Bucket;
import com.amazonaws.services.s3.model.S3Object;
import com.amazonaws.services.s3.model.S3ObjectSummary;
import io.swagger.v3.oas.annotations.Operation;
import lombok.AllArgsConstructor;
import lombok.SneakyThrows;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * aws 对外提供服务端点
 *
 * @author 858695266
 * <p>
 * oss.info
 */
@RestController
@AllArgsConstructor
@RequestMapping("/oss")
public class OssEndpoint {

    private final OssTemplate template;

    /**
     * Bucket Endpoints
     *
     * @param bucketName 存储桶名称
     *
     * @return 存储桶信息
     */
    @SneakyThrows
    @PostMapping("/bucket/{bucketName}")
    @Operation(summary = "创建存储桶并查询信息", description = "创建存储桶并查询信息")
    public Bucket createBucker(@PathVariable String bucketName) {

        template.createBucket(bucketName);
        return template.getBucket(bucketName).get();

    }

    /**
     * 查询全部存储桶
     *
     * @return 存储桶列表
     */
    @SneakyThrows
    @GetMapping("/bucket")
    @Operation(summary = "查询全部存储桶", description = "查询全部存储桶")
    public List<Bucket> getBuckets() {
        return template.getAllBuckets();
    }

    /**
     * 查询指定存储桶
     *
     * @param bucketName 存储桶名称
     * @return 存储桶信息
     */
    @SneakyThrows
    @GetMapping("/bucket/{bucketName}")
    @Operation(summary = "查询指定存储桶", description = "查询指定存储桶")
    public Bucket getBucket(@PathVariable String bucketName) {
        return template.getBucket(bucketName).orElseThrow(() -> new IllegalArgumentException("Bucket Name not found!"));
    }

    /**
     * 删除存储桶
     *
     * @param bucketName 存储桶名称
     */
    @SneakyThrows
    @ResponseStatus(HttpStatus.ACCEPTED)
    @DeleteMapping("/bucket/{bucketName}")
    @Operation(summary = "删除存储桶", description = "删除存储桶")
    public void deleteBucket(@PathVariable String bucketName) {
        template.removeBucket(bucketName);
    }

    /**
     * Object Endpoints
     *
     * @param object 上传的文件
     * @param bucketName 存储桶名称
     *
     * @return 上传对象的信息
     */
    @SneakyThrows
    @PostMapping("/object/{bucketName}")
    @Operation(summary = "上传对象并查询信息", description = "上传对象并查询信息")
    public S3Object createObject(@RequestBody MultipartFile object, @PathVariable String bucketName) {
        String name = object.getOriginalFilename();
        template.putObject(bucketName, name, object.getInputStream(), object.getSize(), object.getContentType());
        return template.getObjectInfo(bucketName, name);

    }

    /**
     * 上传对象并查询信息
     *
     * @param object 上传的文件
     * @param bucketName 存储桶名称
     * @param objectName 指定的对象名称
     * @return 上传对象的信息
     */
    @SneakyThrows
    @PostMapping("/object/{bucketName}/{objectName}")
    @Operation(summary = "上传对象并查询信息", description = "上传对象并查询信息")
    public S3Object createObject(@RequestBody MultipartFile object, @PathVariable String bucketName,
            @PathVariable String objectName) {
        template.putObject(bucketName, objectName, object.getInputStream(), object.getSize(), object.getContentType());
        return template.getObjectInfo(bucketName, objectName);

    }

    /**
     * 按前缀查询存储对象
     *
     * @param bucketName 存储桶名称
     * @param objectName 对象名称前缀
     * @return 匹配的存储对象列表
     */
    @SneakyThrows
    @GetMapping("/object/{bucketName}/{objectName}")
    @Operation(summary = "按前缀查询存储对象", description = "按前缀查询存储对象")
    public List<S3ObjectSummary> filterObject(@PathVariable String bucketName, @PathVariable String objectName) {

        return template.getAllObjectsByPrefix(bucketName, objectName, true);

    }

    /**
     * 生成存储对象的临时访问地址
     *
     * @param bucketName 存储桶名称
     * @param objectName 对象名称
     * @param expires 访问链接有效期
     * @return 对象名称、访问地址及有效期
     */
    @SneakyThrows
    @GetMapping("/object/{bucketName}/{objectName}/{expires}")
    @Operation(summary = "生成存储对象的临时访问地址", description = "生成存储对象的临时访问地址")
    public Map<String, Object> getObject(@PathVariable String bucketName, @PathVariable String objectName,
            @PathVariable Integer expires) {
        Map<String, Object> responseBody = new HashMap<>(8);
        // Put Object info
        responseBody.put("bucket", bucketName);
        responseBody.put("object", objectName);
        responseBody.put("url", template.getObjectURL(bucketName, objectName, expires));
        responseBody.put("expires", expires);
        return responseBody;
    }

    /**
     * 删除存储对象
     *
     * @param bucketName 存储桶名称
     * @param objectName 对象名称
     */
    @SneakyThrows
    @ResponseStatus(HttpStatus.ACCEPTED)
    @DeleteMapping("/object/{bucketName}/{objectName}/")
    @Operation(summary = "删除存储对象", description = "删除存储对象")
    public void deleteObject(@PathVariable String bucketName, @PathVariable String objectName) {

        template.removeObject(bucketName, objectName);
    }

}
