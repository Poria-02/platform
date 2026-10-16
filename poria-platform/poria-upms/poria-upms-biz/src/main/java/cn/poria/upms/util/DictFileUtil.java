package cn.poria.upms.util;

import cn.poria.common.core.exception.ServiceException;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONException;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.web.multipart.MultipartFile;

import java.io.InputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Locale;
import java.util.Objects;

/** 普通字典和分级字典共用的文件格式处理。 */
public final class DictFileUtil {
    private DictFileUtil() {
    }

    public static <T> List<T> read(MultipartFile file, String extension, Class<T> type) throws IOException {
        if (file == null || file.isEmpty()) {
            throw new ServiceException("文件不能为空");
        }
        String filename = file.getOriginalFilename();
        if (filename == null || !filename.toLowerCase(Locale.ROOT).endsWith("." + extension)) {
            throw new ServiceException("不支持的文件类型，请上传 ." + extension + " 文件");
        }
        String content;
        try (InputStream input = file.getInputStream()) {
            content = new String(input.readAllBytes(), StandardCharsets.UTF_8).strip();
        }
        if (content.startsWith("\uFEFF")) {
            content = content.substring(1).strip();
        }
        if (!content.startsWith("[") || !content.endsWith("]")) {
            throw new ServiceException("字典文件内容必须是 JSON 数组");
        }
        try {
            List<T> values = JSON.parseArray(content, type);
            if (values == null || values.stream().anyMatch(Objects::isNull)) {
                throw new ServiceException("字典文件不能包含空字典");
            }
            return values;
        } catch (JSONException exception) {
            ServiceException failure = new ServiceException("字典文件内容格式错误");
            failure.initCause(exception);
            throw failure;
        }
    }

    public static void write(List<?> values, String extension, HttpServletResponse response) throws IOException {
        byte[] content = JSON.toJSONString(values).getBytes(StandardCharsets.UTF_8);
        response.setContentType("application/octet-stream");
        response.setHeader("Content-Disposition", "attachment;filename=dictionary." + extension);
        response.getOutputStream().write(content);
    }
}
