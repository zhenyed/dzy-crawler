package io.zhenye.crawler.dao;

import io.zhenye.crawler.domain.data.SmzdmItemDO;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface SmzdmItemRepository extends MongoRepository<SmzdmItemDO, String> {

    boolean existsByPageId(Long pageId);

}
