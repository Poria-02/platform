package cn.poria.message.service.im.bean;

import lombok.Data;

@Data
public class Members {
    private String  nick;
    private String  accid;
    private boolean mute;
    private String  custom;
    private long    createtime;
    private long    updatetime;
}
