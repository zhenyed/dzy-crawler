package io.zhenye.crawler.dao;

import io.zhenye.crawler.domain.data.CrawlUrlDO;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CrawlUrlRepository extends MongoRepository<CrawlUrlDO, String> {

    @Query(value = "{enable:true}", fields = "{url:1}")
    List<CrawlUrlDO> findEnableUrl();

}
