package cn.poria.base.vo.request;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class FileUploadModel {

    String file;
    String fileName;
    String path;

    /**
     text/plain	纯文本格式
     text/html	HTML格式
     text/xml	XML格式
     image/gif	gif图片格式
     image/jpeg	jpg图片格式
     image/png	png图片格式
     application/xhtml+xml	XHTML格式
     application/xml	XML数据格式
     application/atom+xml	Atom XML聚合格式
     application/json	JSON数据格式
     application/pdf	pdf格式
     application/msword	Word文档格式
     application/octet-stream	二进制流数据（如常见的文件下载）
     application/x-www-form-urlencoded	form表单数据被编码为key/value格式发送到服务器（表单默认的提交数据的格式）
     multipart/form-data	在表单中进行文件上传时
     */
    String contextType;

    Boolean randomName = true;


    public FileUploadModel(String file, String fileName, String path, String contextType) {
        this.file = file;
        this.fileName = fileName;
        this.path = path;
        this.contextType = contextType;
    }



}

