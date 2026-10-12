package cn.poria.message.enums;

import lombok.Getter;

/**
 * 0 表示文本消息,
 * 1 表示图片，
 * 2 表示语音，
 * 3 表示视频，
 * 4 表示地理位置信息，
 * 6 表示文件，
 * 10 表示提示消息，
 * 100 自定义消息类型（特别注意，对于未对接易盾反垃圾功能的应用，该类型的消息不会提交反垃圾系统检测）
 */
@Getter
public enum YxMsgType {

  TEXT(0,"文本消息"),
  IMAGE(1,"图片"),
  VOICE(2,"语音"),
  VIDEO(3,"视频"),
  LOCATION(4,"地理位置"),
  FILE(6,"文件"),
  HINT(10,"提示消息"),
  CUSTOMIZE(100,"自定义消息"),
  ;

  private Integer type;
  private String desc;


  YxMsgType(Integer type,String desc){
    this.type = type;
    this.desc = desc;
  }

}
