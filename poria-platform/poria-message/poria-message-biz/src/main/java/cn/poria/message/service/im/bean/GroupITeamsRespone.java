package cn.poria.message.service.im.bean;

import lombok.Data;

import java.util.List;

@Data
public class GroupITeamsRespone {
    private int code;
    private int count;

    private List<ITeamInfo> infos;
}
