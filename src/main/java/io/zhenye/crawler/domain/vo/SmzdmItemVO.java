package io.zhenye.crawler.domain.vo;

import com.fasterxml.jackson.annotation.JsonFormat;
import io.zhenye.crawler.domain.data.SmzdmItemDO;
import lombok.Data;
import org.springframework.beans.BeanUtils;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

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

    public static SmzdmItemVO of(SmzdmItemDO smzdmItemDO) {
        SmzdmItemVO res = new SmzdmItemVO();
        BeanUtils.copyProperties(smzdmItemDO, res);
        return res;
    }

    public static List<SmzdmItemVO> ofList(List<SmzdmItemDO> list) {
        return list.stream().filter(Objects::nonNull).map(SmzdmItemVO::of).collect(Collectors.toList());
    }

}
