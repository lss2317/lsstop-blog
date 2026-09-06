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

    HTTP_REQUEST("HTTP_REQUEST", "HTTP请求"),
    LOGIN_LOG("LOGIN_LOG", "登录日志"),
    TASK("TASK", "系统任务"),
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
