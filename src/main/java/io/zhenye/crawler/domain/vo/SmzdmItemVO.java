package io.zhenye.crawler.domain.vo;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

import java.time.LocalDateTime;

@Data
public class SmzdmItemVO {
    /**
     * 什么值得买 id
     */
    private Long pageId;
    /**
     * 源地址
     */
    private String pageUrl;
    /**
     * 标题
     */
    private String title;
    /**
     * 电商名字
     */
    private String mallName;
    /**
     * 价格
     */
    private String price;
    /**
     * 封面地址
     */
    private String coverUrl;
    /**
     * 购物链接
     */
    private String buyUrl;
    /**
     * 值
     */
    private Integer worthy;
    /**
     * 不值
     */
    private Integer unworthy;
    /**
     * 值比例
     */
    private Integer worthyPercent;
    /**
     * 创建时间
     */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "Asia/Shanghai")
    private LocalDateTime createTime;
    /**
     * 更新时间
     */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "Asia/Shanghai")
    private LocalDateTime updateTime;

}
