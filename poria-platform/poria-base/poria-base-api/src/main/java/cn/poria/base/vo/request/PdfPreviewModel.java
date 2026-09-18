package cn.poria.base.vo.request;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class PdfPreviewModel {

    @NotBlank(message = "地址不能为空")
    private String url;

}

