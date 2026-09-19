package com.lsstop.enums;

import com.lsstop.enums.base.EnumLookup;
import com.lsstop.enums.base.ValueEnum;
import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * 通知来源类型枚举
 *
 * @author lishusheng
 * @date 2026/09/06
 */
@Getter
@AllArgsConstructor
public enum NotificationSourceTypeEnum implements ValueEnum<String> {

    HTTP_REQUEST("HTTP_REQUEST", "接口请求"),
    LOGIN_LOG("LOGIN_LOG", "认证日志模块"),
    TASK("TASK", "定时任务"),
    MQ_CONSUMER("MQ_CONSUMER", "消息队列消费"),
    WEBSOCKET("WEBSOCKET", "实时通信"),
    SYSTEM("SYSTEM", "系统内部");

    /**
     * 来源类型
     */
    private final String type;

    /**
     * 来源描述
     */
    private final String desc;

    @Override
    public String getValue() {
        return type;
    }

    /**
     * 根据来源类型获取枚举
     *
     * @param type 来源类型
     * @return 对应枚举，不存在返回null
     */
    public static NotificationSourceTypeEnum of(String type) {
        return EnumLookup.getOrNull(NotificationSourceTypeEnum.class, type);
    }
}
