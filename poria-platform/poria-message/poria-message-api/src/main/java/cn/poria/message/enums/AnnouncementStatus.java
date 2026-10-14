package cn.poria.message.enums;

import lombok.Getter;

/**
 * 公告发布状态
 */
@Getter
public enum AnnouncementStatus {

    /**
     * 草稿
     */
    DRAFT(0),

    /**
     * 已发布
     */
    PUBLISHED(1),

    /**
     * 已撤回
     */
    WITHDRAWN(2);

    /**
     * 状态编码
     */
    private final int code;

    AnnouncementStatus(int code) {
        this.code = code;
    }
}
