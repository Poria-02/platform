package cn.poria.base.config;


import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import javax.sql.rowset.serial.SerialBlob;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.sql.SQLException;
import java.util.Set;

@Component
@Slf4j
public class FfmpegAudioConverter implements AudioConverter {

    private static final Set<String> SUPPORTED_FORMATS = Set.of("awb", "flac", "amr", "caf", "wav", "aac");

    @Value("${audio.convert.ffmpeg.path}")
    private String ffmpegPath;

    @Value("${audio.convert.ffmpeg.temp-path}")
    private String tempPath;


    @Override
    public boolean convert(File sourceFile, File targetFile, String suffix) {

        log.info("源文件地址: {}", sourceFile.getAbsolutePath());
        log.info("目标文件地址: {}", targetFile.getAbsolutePath());
        if (!sourceFile.exists()) {
            log.error("源文件不存在: {}", sourceFile.getAbsolutePath());
            return false;
        }


        try {
            ProcessBuilder processBuilder = switch (suffix) {
                case "awb","amr" -> new ProcessBuilder(
                        ffmpegPath,
                        "-f", "amr",
                        "-i", sourceFile.getAbsolutePath(),
                        "-c:a", "libmp3lame",// 编码器
                        "-y", // 覆盖输出文件
                        targetFile.getAbsolutePath()
                );
                default  -> new ProcessBuilder(
                        ffmpegPath,
                        "-i", sourceFile.getAbsolutePath(),
                        "-codec:a", "libmp3lame",// 编码器
                        "-qscale:a", "2",
                        "-y", // 覆盖输出文件
                        targetFile.getAbsolutePath()
                );
            };

            Process process = processBuilder.start();
            int exitCode = process.waitFor();

            if (exitCode == 0) {
                log.info("音频转换成功: {} -> {}", sourceFile.getName(), targetFile.getName());
                return true;
            } else {
                log.error("音频转换失败，退出码: {}", exitCode);
                return false;
            }
        } catch (Exception e) {
            log.error("音频转换异常", e);
            return false;
        }
    }

    @Override
    public File convertToMp3(SerialBlob serialBlob, String fileName, String suffix) {
        File inputFile = null;
        File targetFile = null;
        try {
            // 1. 创建临时输入文件
            Path uploadPath = Paths.get(tempPath);
            // 确保目录存在
            if (!Files.exists(uploadPath)) {
                Files.createDirectories(uploadPath);
            }

            inputFile = new File(tempPath, fileName + "." + suffix);

            // 2. 将SerialBlob写入临时文件
            writeBlobToFile(serialBlob, inputFile);

            // 3. 使用FFmpeg转换
            targetFile = new File(tempPath, fileName + ".mp3");

            if (convert(inputFile, targetFile, suffix)) {
                return targetFile;
            }
            return null;
        } catch (Exception e) {
            cleanupTempFiles(inputFile, targetFile);
            throw new RuntimeException(e);
        } finally {
            if (inputFile != null && inputFile.exists()) {
                inputFile.delete();
            }
        }
    }

    @Override
    public File convertToMp3(MultipartFile file, String fileName, String suffix) {
        File inputFile = null;
        File targetFile = null;
        try {
            // 1. 创建临时输入文件
            Path uploadPath = Paths.get(tempPath);

            // 确保目录存在
            if (!Files.exists(uploadPath)) {
                Files.createDirectories(uploadPath);
            }

            inputFile = new File(tempPath, fileName + "." + suffix);

            // 2. 将multipartFile写入临时文件
            file.transferTo(inputFile);

            // 3. 创建目标文件
            targetFile = new File(tempPath, fileName + ".mp3");

            // 4. 使用FFmpeg转换
            if (convert(inputFile, targetFile, suffix)) {
                return targetFile;
            }
            return null;
        } catch (Exception e) {
            cleanupTempFiles(inputFile, targetFile);
            throw new RuntimeException(e);
        } finally {
            if (inputFile != null && inputFile.exists()) {
                inputFile.delete();
            }
        }
    }

    @Override
    public boolean isSupportedFormat(String format) {
        return SUPPORTED_FORMATS.contains(format.toLowerCase());
    }

    /**
     * 将SerialBlob写入文件
     */
    private void writeBlobToFile(SerialBlob blob, File outputFile) throws IOException, SQLException {
        try (InputStream inputStream = blob.getBinaryStream();
             FileOutputStream outputStream = new FileOutputStream(outputFile)) {

            byte[] buffer = new byte[8192];
            int bytesRead;

            while ((bytesRead = inputStream.read(buffer)) != -1) {
                outputStream.write(buffer, 0, bytesRead);
            }
        }
    }

    /**
     * 清理临时文件
     */
    private void cleanupTempFiles(File... files) {
        for (File file : files) {
            if (file != null && file.exists()) {
                if (!file.delete()) {
                    log.warn("无法删除临时文件: {}", file.getAbsolutePath());
                }
            }
        }
    }
}
