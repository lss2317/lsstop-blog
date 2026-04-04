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
     * WebSocket消息类型：在线人数
     */
    public static final String WS_TYPE_ONLINE_COUNT = "onlineCount";
}
