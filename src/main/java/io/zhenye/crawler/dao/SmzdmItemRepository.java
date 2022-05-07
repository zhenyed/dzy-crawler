package io.zhenye.crawler.dao;

import io.zhenye.crawler.domain.data.SmzdmItemDO;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;

@Repository
public interface SmzdmItemRepository extends MongoRepository<SmzdmItemDO, String> {

    SmzdmItemDO findByPageId(Long pageId);

    @Query("{$and:[{createTime:{$gte:?0}},{worthyPercent:{$gte:70}},{worthy:{$gte:20}}]}.sort")
    SmzdmItemDO findByRanking(LocalDateTime startDate);

    Page<SmzdmItemDO> findByPageId(Long pageId, Pageable pageable);

}
