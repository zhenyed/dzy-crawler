package io.zhenye.crawler.dao;

import io.zhenye.crawler.domain.data.GroupByBO;
import io.zhenye.crawler.domain.data.SmzdmItemDO;
import org.springframework.data.mongodb.repository.Aggregation;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface SmzdmItemRepository extends MongoRepository<SmzdmItemDO, String> {

    SmzdmItemDO findByPageId(Long pageId);

    @Aggregation({
            "{$match: {createTime: {$gte: ?0}}}",
            "{$group: {_id: {$dateToString: {format: '%m-%d', date: '$createTime', timezone: 'Asia/Shanghai'}}, value: {$sum: 1}}}",
            "{$sort: {_id: 1}}",
            "{$limit: 7}"})
    List<GroupByBO> biByCreateCountDaily(LocalDate beforeDate);

    @Aggregation({
            "{$match: {createTime: {$gte: ?0, $lte: ?1}}}",
            "{$group: {_id: $mallName, value: {$sum: 1}}}",
            "{$sort: {value: -1}}",
            "{$limit: 5}"})
    List<GroupByBO> countGroupByMall(LocalDateTime startDate, LocalDateTime endDate);

}
