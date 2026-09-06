package com.lsstop.enums;

import com.lsstop.enums.base.EnumLookup;
import com.lsstop.enums.base.ValueEnum;
import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * 通知事件类型枚举
 *
 * @author lishusheng
 * @date 2026/09/06
 */
@Getter
@AllArgsConstructor
public enum NotificationEventTypeEnum implements ValueEnum<String> {

    SYSTEM_EXCEPTION("SYSTEM_EXCEPTION", "系统异常"),
    SLOW_API("SLOW_API", "接口请求过慢"),
    LOGIN_RISK("LOGIN_RISK", "登录风险"),
    TASK_FAILURE("TASK_FAILURE", "任务执行失败"),
    SYSTEM_NOTICE("SYSTEM_NOTICE", "系统通知");

    /**
     * 事件类型
     */
    private final String type;

    /**
     * 事件描述
     */
    private final String desc;

    @Override
    public String getValue() {
        return type;
    }

    /**
     * 根据事件类型获取枚举
     *
     * @param type 事件类型
     * @return 对应枚举，不存在返回null
     */
    public static NotificationEventTypeEnum of(String type) {
        return EnumLookup.getOrNull(NotificationEventTypeEnum.class, type);
    }
}
