package io.zhenye.crawler.domain.dto;

import cn.hutool.core.util.NumberUtil;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * "什么值得买" 页面解析 DTO
 */
@Data
public class SmzdmParseDTO {
    /**
     * 页面 id
     */
    private Long pageId;
    /**
     * 页面地址
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
     * 是否有效
     */
    private Boolean effective;
    /**
     * 创建时间（读取 smzdm 页面的值）
     */
    private LocalDateTime createTime;

    /**
     * 计算"值"比例
     */
    public int getWorthyPercent() {
        assert worthy >= 0;
        assert unworthy >= 0;
        if (worthy == 0) {
            return 0;
        } else {
            return (int) NumberUtil.mul(NumberUtil.div(worthy.intValue(), (worthy + unworthy)), 100);
        }
    }

}
