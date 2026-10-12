package cn.poria.message.service.im.bean;

import lombok.Data;

@Data
public class Owner {
    private String  nick;
    private String  accid;
    private String  custom;
    private boolean mute;

    private long    createtime;
    private long    updatetime;
}
