package cn.poria.base.util;


public class AssetFileTypeUtil {

    public static Boolean fileType(String type) {
        return switch (type) {
            case "jpg", "jpeg", "png", "pdf", "mp3", "mp4", "mov","m4a","awb","wav","flac","amr","caf" -> false;
            default -> true;
        };
    }
}
