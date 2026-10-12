package cn.poria.message.enums.txim;

import lombok.Getter;

@Getter
public enum TxMsgTypeEnum {

    TXT(0,"TIMTextElem","文本消息"),
    IMG(1,"TIMImageElem","图像消息"),
    SOUND(2,"TIMSoundElem","语音消息"),
    VIDEO(3,"TIMVideoFileElem","视频消息"),
    LOCATION(4,"TIMLocationElem","地理位置消息"),
    CUSTOM(100,"TIMCustomElem","自定义消息"),
    FILE(6,"TIMFileElem","文件消息"),
    FACE(10,"TIMFaceElem","表情消息"),
    ;

    private Integer type;

    private String code;

    private String desc;

    TxMsgTypeEnum(Integer type, String code, String desc) {
        this.type = type;
        this.code = code;
        this.desc = desc;
    }


    //获取消息类型编码
    public static String getTypeCode(Integer type){
        for (TxMsgTypeEnum value : TxMsgTypeEnum.values()) {
            if (value.getType().equals(type)) {
                return value.getCode();
            }
        }
        return null;
    }

    //获取类型
    public static TxMsgTypeEnum getType(Integer type){
        for (TxMsgTypeEnum value : TxMsgTypeEnum.values()) {
            if (value.getType().equals(type)) {
                return value;
            }
        }
        return null;
    }

}
