package cn.poria.message.service.im.bean;

import lombok.Data;

import java.util.List;

@Data
public class TeamInfo {
    private String  tname;
    private String  announcement;
    private String  owner;
    private int     maxusers;
    private int     joinmode;
    private String  joinOption;
    private int     tid;
    private String  intro;
    private String  notification;
    private int     size;
    private String  custom;
    private String  clientCustom;
    private boolean mute;
    private String groupId;
    private long    createtime;
    private long    updatetime;

    private List<String> admins;
    private List<String> members;
}
