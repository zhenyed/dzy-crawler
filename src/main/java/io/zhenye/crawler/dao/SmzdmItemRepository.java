package io.zhenye.crawler.dao;

import io.zhenye.crawler.domain.data.SmzdmItemDO;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.Collection;
import java.util.List;

@Repository
public interface SmzdmItemRepository extends MongoRepository<SmzdmItemDO, String> {

    SmzdmItemDO findByPageId(Long pageId);

    @Query(value = "{pageId:{$in:?0}}",sort = "{worthy:-1,worthyPercent:-1}")
    List<SmzdmItemDO> findByPageIdsOrderByWorthyPercent(Collection<Long> pageId);

    @Query("{$and:[{createTime:{$gte:?0}},{worthyPercent:{$gte:70}},{worthy:{$gte:20}}]}.sort")
    SmzdmItemDO findByRanking(LocalDateTime startDate);

    Page<SmzdmItemDO> findByPageId(Long pageId, Pageable pageable);

}
