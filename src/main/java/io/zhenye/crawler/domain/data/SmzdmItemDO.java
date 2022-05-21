package io.zhenye.crawler.domain.data;

import io.zhenye.crawler.domain.dto.SmzdmParseDTO;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.beans.BeanUtils;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@Document(collection = "smzdm_item")
public class SmzdmItemDO extends BaseDO {
    /**
     * 页面 id
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

    public SmzdmItemDO(SmzdmParseDTO dto) {
        LocalDateTime now = LocalDateTime.now();
        BeanUtils.copyProperties(dto, this);
        this.setUpdateTime(now);
    }
}
