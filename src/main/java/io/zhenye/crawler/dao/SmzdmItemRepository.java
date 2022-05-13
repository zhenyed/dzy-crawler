package io.zhenye.crawler.dao;

import io.zhenye.crawler.domain.data.DailyCreateCountBO;
import io.zhenye.crawler.domain.data.SmzdmItemDO;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.repository.Aggregation;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

@Repository
public interface SmzdmItemRepository extends MongoRepository<SmzdmItemDO, String> {

    SmzdmItemDO findByPageId(Long pageId);

    Page<SmzdmItemDO> findByPageId(Long pageId, Pageable pageable);

    @Aggregation({
            "{$match: {createTime: {$gt: ?0}}}",
            "{$group: {_id: {$dateToString: {format: '%m-%d', date: '$createTime'}}, value: {$sum: 1}}}",
            "{$sort: {_id: 1}}"})
    List<DailyCreateCountBO> BiByCreateCountDaily(LocalDate beforeDate);

}
