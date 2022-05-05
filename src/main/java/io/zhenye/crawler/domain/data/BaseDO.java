package io.zhenye.crawler.domain.data;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class BaseDO {
    /**
     * 主键
     */
    private String id;
    /**
     * 创建时间
     */
    private LocalDateTime createTime;
    /**
     * 更新时间
     */
    private LocalDateTime updateTime;

}
