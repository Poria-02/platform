package cn.poria.message.service.im.tx.response;

import cn.hutool.json.JSONArray;
import lombok.Data;

@Data
public class MsgRecordResponse {

    private int size;

    private JSONArray msgs;

}
