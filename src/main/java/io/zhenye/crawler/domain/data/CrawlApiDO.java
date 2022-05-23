package io.zhenye.crawler.domain.data;

import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.List;

@Data
@NoArgsConstructor
@Document(collection = "crawl_api")
public class CrawlApiDO extends BaseDO {

    /**
     * 爬取地址
     */
    private List<String> url;
    /**
     * 模块
     */
    private String module;

}
