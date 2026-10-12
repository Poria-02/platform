package cn.poria.message.service.im.bean;

import lombok.Data;

@Data
public class ITeamInfo {
    private String tid;
    private int size;
    private int maxusers;

    private String owner;
    private String tname;
    private String custom;
}
