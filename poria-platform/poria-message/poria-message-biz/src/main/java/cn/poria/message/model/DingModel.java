package cn.poria.message.model;

import lombok.Data;

@Data
public class DingModel {

    /**
     * 默认text,如果有其他需求再说
     */
    private String  msgtype = "text";

    private Text text = new Text();

    private At at = new At();

    @Data
   public static class At{
        String[] atMobiles;

        boolean isAtAll;
    }

    @Data
   public static class Text {
        private String content;
    }

}
