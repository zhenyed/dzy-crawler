package io.zhenye.crawler.domain.data;

import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.List;

@Data
@NoArgsConstructor
@Document(collection = "crawl_url")
public class CrawlUrlDO extends BaseDO {

    /**
     * 爬取地址
     */
    private List<String> url;
    /**
     * 名称
     */
    private String name;
    /**
     * 是否启用
     */
    private Boolean enable;

}
