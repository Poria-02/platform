package cn.poria.message.service.im.bean;

import lombok.Data;

import java.util.List;

@Data
public class Tinfo {
    private String  icon;
    private String  announcement;
    private int     uptinfomode;
    private int     maxusers;
    private String  intro;
    private int     upcustommode;
    private String  notification;
    private String  tname;
    private int     beinvitemode;
    private int     joinmode;
    private String  joinOption;
    private long     tid;
    private String  groupId;
    private int     invitemode;
    private boolean mute;
    private String  custom;
    private String  clientCustom;
    private long    createtime;
    private long    updatetime;

    private Owner owner;
    private List<Admins> admins;
    private List<Members> members;
}
