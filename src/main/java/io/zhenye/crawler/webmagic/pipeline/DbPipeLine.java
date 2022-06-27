package io.zhenye.crawler.webmagic.pipeline;

import io.zhenye.crawler.dao.SmzdmItemRepository;
import io.zhenye.crawler.domain.dto.SmzdmParseDTO;
import io.zhenye.crawler.domain.data.SmzdmItemDO;
import io.zhenye.crawler.service.GridFsService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.redisson.api.RBucket;
import org.redisson.api.RedissonClient;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.stereotype.Component;
import us.codecraft.webmagic.ResultItems;
import us.codecraft.webmagic.Task;
import us.codecraft.webmagic.pipeline.Pipeline;

import java.time.LocalDateTime;
import java.util.concurrent.TimeUnit;

@Slf4j
@RequiredArgsConstructor
@Component
public class DbPipeLine implements Pipeline {

    private final SmzdmItemRepository smzdmItemRepository;
    private final GridFsService gridFsService;
    private final MongoTemplate mongoTemplate;
    private final RedissonClient redissonClient;

    @Override
    public void process(ResultItems resultItems, Task task) {
        SmzdmParseDTO dto = resultItems.get("dto");
        LocalDateTime now = LocalDateTime.now();

        SmzdmItemDO itemDO = smzdmItemRepository.findByPageId(dto.getPageId());
        // 上传图片
        gridFsService.uploadSmzdmPic(dto.getCoverUrl(), dto.getPageId());
        if (itemDO == null) {
            // 插入时加锁，避免重复插入导致报错
            RBucket<Long> bucket = redissonClient.getBucket("smzdm:create:" + dto.getPageId());
            boolean lock = bucket.trySet(System.currentTimeMillis(), 1, TimeUnit.MINUTES);
            if (lock) {
                smzdmItemRepository.save(new SmzdmItemDO(dto));
            } else {
                log.info("PageId[{}] is creating", dto.getPageId());
            }
        } else if (needUpdate(itemDO)) {
            Query query = new Query().addCriteria(Criteria.where("pageId").is(dto.getPageId()));
            Update update = new Update()
                    .set("worthy", dto.getWorthy())
                    .set("unworthy", dto.getUnworthy())
                    .set("worthyPercent", dto.getWorthyPercent())
                    .set("updateTime", now);
            mongoTemplate.updateFirst(query, update, SmzdmItemDO.class);
        } else {
            log.info("PageId[{}] is noneffective. Skip it.", dto.getPageId());
        }
    }

    /**
     * 7天内的才更新
     */
    private boolean needUpdate(SmzdmItemDO itemDO) {
        return itemDO.getCreateTime().isAfter(LocalDateTime.now().minusDays(7));
    }
}
