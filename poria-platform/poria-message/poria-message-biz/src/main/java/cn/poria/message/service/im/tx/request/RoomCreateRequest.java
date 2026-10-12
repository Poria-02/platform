package cn.poria.message.service.im.tx.request;

import cn.hutool.core.annotation.Alias;
import lombok.Data;

import java.util.List;

/**
 * author qd
 * date 2024/12/10 18:04
 * description
 */
@Data
public class RoomCreateRequest {

    /**
     * 房间 ID，最长48个字节
     */
    @Alias("RoomId")
    private String roomId;

    /**
     * 房间名称，默认为房间 ID，最长100个字节
     */
    @Alias("RoomName")
    private String roomName;

    /**
     * 房间类型：Conference（会议房间）
     */
    @Alias("RoomType")
    private String roomType;

    /**
     * 房主 ID（需是 已导入 的账号），默认为接口调用者的用户 ID
     */
    @Alias("Owner_Account")
    private String ownerAccountId;

    /**
     * 最大房间成员数量，缺省时的默认值：付费套餐包上限，例如体验版是20，如果升级套餐包，需按照修改房间资料修改这个字段
     */
    @Alias("MaxMemberCount")
    private Integer maxMemberCount;

    /**
     * 预定会议开始时间，默认为当前时间
     */
    @Alias("ScheduleStartTime")
    private Integer scheduleStartTime;

    /**
     * 预定会议结束时间，默认为开始时间后1小时，会议最小时间不得低于5分钟，最长时间不得超过24h
     */
    @Alias("ScheduleEndTime")
    private Integer scheduleEndTime;

    /**
     * 全体禁画，默认 false
     */
    @Alias("IsVideoDisabled")
    private Boolean isVideoDisabled;

    /**
     * 全体禁音，默认 false
     */
    @Alias("IsAudioDisabled")
    private Boolean isAudioDisabled;

    /**
     * 禁止所有成员发文本消息，默认 false
     */
    @Alias("IsMessageDisabled")
    private Boolean isMessageDisabled;

    /**
     * 禁止屏幕共享，默认 false
     */
    @Alias("IsScreenSharingDisabled")
    private Boolean isScreenSharingDisabled;

    /**
     * 禁止云录制，默认 false
     */
    @Alias("IsCloudRecordingDisabled")
    private Boolean isCloudRecordingDisabled;

    /**
     * 自定义信息，最长500个字节
     */
    @Alias("CustomInfo")
    private String customInfo;

    /**
     * 是否支持麦位，默认不支持
     */
    @Alias("IsSeatEnabled")
    private Boolean isSeatEnabled;

    /**
     * 最大麦位数，默认为套餐包上限（默认最大20）
     */
    @Alias("MaxSeatCount")
    private Integer maxSeatCount;

    /**
     * 麦位模式：FreeToTake（自由上麦），ApplyToTake（申请上麦）
     */
    @Alias("TakeSeatMode")
    private String takeSeatMode;
}
