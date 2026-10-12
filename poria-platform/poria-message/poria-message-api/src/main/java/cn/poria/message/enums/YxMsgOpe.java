package cn.poria.message.enums;

import lombok.Getter;

/**
 * 0：点对点个人消息，1：群消息（高级群），其他返回414
 */
@Getter
public enum YxMsgOpe {

  PERSONAL(0,"点对点个人消息"),
  GROUP(1,"群消息（高级群）")
  ;

  private Integer type;
  private String desc;

  YxMsgOpe(Integer type,String desc){
    this.type = type;
    this.desc = desc;
  }
}
