package cn.poria.base.config;

import org.springframework.web.multipart.MultipartFile;

import javax.sql.rowset.serial.SerialBlob;
import java.io.File;

public interface AudioConverter {

    /**
     * 转换音频格式
     * @param sourceFile 源文件
     * @param targetFile 目标文件
     * @return 转换是否成功
     */
    boolean convert(File sourceFile, File targetFile, String suffix);

    /**
     * 转换音频格式到MP3
     * @param serialBlob 源文件
     * @param fileName 文件名
     * @return 转换后的MP3文件
     */
    File convertToMp3(SerialBlob serialBlob, String fileName, String suffix);


    /**
     * 转换音频格式到MP3
     * @param file 源文件
     * @param fileName 文件名
     * @return 转换后的MP3文件
     */
    File convertToMp3(MultipartFile file, String fileName, String suffix);

    /**
     * 检查是否支持该格式
     * @param format 文件格式
     * @return 是否支持
     */
    boolean isSupportedFormat(String format);
}
