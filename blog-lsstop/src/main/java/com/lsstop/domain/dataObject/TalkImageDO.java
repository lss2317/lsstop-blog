package com.lsstop.domain.dataObject;

import com.lsstop.domain.BaseData;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * 说说图片DO
 *
 * @author lishusheng
 * @date 2025/12/30
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TalkImageDO implements BaseData {

    /**
     * 图片id
     */
    private Integer id;

    /**
     * 说说id
     */
    private Integer talkId;

    /**
     * 图片地址
     */
    private String imageUrl;

    /**
     * 图片顺序
     */
    private Integer sort;

    /**
     * 是否删除
     */
    private Integer isDelete;

    /**
     * 创建时间
     */
    private LocalDateTime createTime;

    /**
     * 更新时间
     */
    private LocalDateTime updateTime;

}
