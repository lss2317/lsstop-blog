package com.lsstop.constant;

/**
 * 聊天消息模块常量
 *
 * @author lishusheng
 * @date 2026/04/04
 */
public class ChatConst {

    /**
     * 默认每页消息数量
     */
    public static final int DEFAULT_PAGE_SIZE = 50;

    /**
     * 消息内容最大长度
     */
    public static final int MAX_CONTENT_LENGTH = 500;

    /**
     * 最大图片数量
     */
    public static final int MAX_IMAGE_COUNT = 9;

    /**
     * 消息内容不能为空
     */
    public static final String MESSAGE_CONTENT_EMPTY = "消息内容不能为空";

    /**
     * 无效的消息ID
     */
    public static final String INVALID_LAST_ID = "无效的消息ID";

    /**
     * WebSocket消息类型：聊天消息
     */
    public static final String WS_TYPE_MESSAGE = "message";

    /**
     * WebSocket消息类型：在线用户数
     */
    public static final String WS_TYPE_ONLINE_USER_COUNT = "onlineUserCount";

    /**
     * WebSocket消息类型：错误提示
     */
    public static final String WS_TYPE_ERROR = "error";

    /**
     * 消息格式错误
     */
    public static final String INVALID_MESSAGE_FORMAT = "消息格式错误";

    /**
     * 消息内容过长
     */
    public static final String CONTENT_TOO_LONG = "消息内容过长";

    /**
     * 图片数量超出限制
     */
    public static final String TOO_MANY_IMAGES = "图片数量超出限制";

    /**
     * WebSocket心跳ping
     */
    public static final String WS_PING = "ping";

    /**
     * WebSocket心跳pong
     */
    public static final String WS_PONG = "pong";
}
