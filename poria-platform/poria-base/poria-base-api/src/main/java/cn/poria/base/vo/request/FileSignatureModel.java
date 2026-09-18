package cn.poria.base.vo.request;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Data
public class FileSignatureModel {

    @Schema(description = "类型 public/private")
    public String type ;

    @Schema(description = "上传目录")
    public String path;


}

