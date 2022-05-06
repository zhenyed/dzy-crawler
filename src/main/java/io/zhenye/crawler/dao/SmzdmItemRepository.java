package io.zhenye.crawler.dao;

import io.zhenye.crawler.domain.data.SmzdmItemDO;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface SmzdmItemRepository extends MongoRepository<SmzdmItemDO, String> {

    SmzdmItemDO findByPageId(Long pageId);

    Page<SmzdmItemDO> findByPageId(Long pageId, Pageable pageable);

}
