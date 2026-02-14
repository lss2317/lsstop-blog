package com.lsstop.domain.dto;

import com.lsstop.enums.EmailTypeEnum;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serial;
import java.io.Serializable;
import java.util.Map;

/**
 * 邮件消息DTO
 *
 * @author lss
 * @date 2026/2/14
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class EmailDTO implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    /**
     * 邮件类型
     */
    private EmailTypeEnum type;

    /**
     * 收件人邮箱
     */
    private String to;

    /**
     * 模板参数
     */
    private Map<String, Object> params;
}
