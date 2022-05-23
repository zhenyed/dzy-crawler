package io.zhenye.crawler.dao;

import io.zhenye.crawler.domain.data.CrawlApiDO;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CrawlApiRepository extends MongoRepository<CrawlApiDO, String> {

    @Query(value = "{module:?0}", fields = "{url: 1}")
    List<CrawlApiDO> findApiUrl(String module);

}
