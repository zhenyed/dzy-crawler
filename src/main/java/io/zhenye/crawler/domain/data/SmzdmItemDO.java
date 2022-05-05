package io.zhenye.crawler.domain.data;

import lombok.Data;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

@Data
@Document(collection = "smzdm_item")
public class SmzdmItemDO extends BaseDO {
    /**
     * 什么值得买 id
     */
    @Indexed
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
}
