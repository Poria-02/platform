package cn.poria.message.vo.response.im;

import lombok.Data;

/**
 * author qd
 * date 2024/12/13 10:57
 * description
 */
@Data
public class RoomInfoVo {

    /**
     * 房间 ID，最长48个字节
     */
    private String roomId;

    /**
     * 房间名称，默认为房间 ID，最长100个字节
     */
    private String roomName;

    /**
     * 房间类型：Conference（会议房间）
     */
    private String roomType;

    /**
     * 房主 ID（需是 已导入 的账号），默认为接口调用者的用户 ID
     */
    private String ownerAccountId;

    /**
     * 最大房间成员数量，缺省时的默认值：付费套餐包上限，例如体验版是20，如果升级套餐包，需按照修改房间资料修改这个字段
     */
    private Integer maxMemberCount;

    /**
     * 预定会议开始时间，默认为当前时间
     */
    private Integer scheduleStartTime;

    /**
     * 预定会议结束时间，默认为开始时间后1小时，会议最小时间不得低于5分钟，最长时间不得超过24h
     */
    private Integer scheduleEndTime;

    /**
     * 全体禁画，默认 false
     */
    private Boolean isVideoDisabled;

    /**
     * 全体禁音，默认 false
     */
    private Boolean isAudioDisabled;

    /**
     * 禁止所有成员发文本消息，默认 false
     */
    private Boolean isMessageDisabled;

    /**
     * 禁止屏幕共享，默认 false
     */
    private Boolean isScreenSharingDisabled;

    /**
     * 禁止云录制，默认 false
     */
    private Boolean isCloudRecordingDisabled;

    /**
     * 自定义信息，最长500个字节
     */
    private String customInfo;

    /**
     * 是否支持麦位，默认不支持
     */
    private Boolean isSeatEnabled;

    /**
     * 最大麦位数，默认为套餐包上限（默认最大20）
     */
    private Integer maxSeatCount;

    /**
     * 麦位模式：FreeToTake（自由上麦），ApplyToTake（申请上麦）
     */
    private String takeSeatMode;

    /**
     * 房间状态：None（关闭），NotStart（待开始），Running（进行中）
     */
    private String roomStatus;

    /**
     * 预定会议开始时间
     */
    private Integer createTime;

    /**
     * 房间成员数量
     */
    private Integer memberCount;

}
