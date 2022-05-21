package io.zhenye.crawler.pipeline;

import io.zhenye.crawler.dao.SmzdmItemRepository;
import io.zhenye.crawler.domain.dto.SmzdmParseDTO;
import io.zhenye.crawler.domain.data.SmzdmItemDO;
import io.zhenye.crawler.service.GridFsService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.stereotype.Component;
import us.codecraft.webmagic.ResultItems;
import us.codecraft.webmagic.Task;
import us.codecraft.webmagic.pipeline.Pipeline;

import java.time.LocalDateTime;

@Slf4j
@RequiredArgsConstructor
@Component
public class DbPipeLine implements Pipeline {

    private final SmzdmItemRepository smzdmItemRepository;
    private final GridFsService gridFsService;
    private final MongoTemplate mongoTemplate;

    @Override
    public void process(ResultItems resultItems, Task task) {
        SmzdmParseDTO dto = resultItems.get("dto");
        LocalDateTime now = LocalDateTime.now();

        SmzdmItemDO itemDO = smzdmItemRepository.findByPageId(dto.getPageId());
        // 上传图片
        gridFsService.uploadSmzdmPic(dto.getCoverUrl(), dto.getPageId());
        if (itemDO == null) {
            smzdmItemRepository.save(new SmzdmItemDO(dto));
        } else /*if (isEffective(itemDO))*/ {
            Query query = new Query().addCriteria(Criteria.where("pageId").is(dto.getPageId()));
            Update update = new Update()
                    .set("worthy", dto.getWorthy())
                    .set("unworthy", dto.getUnworthy())
                    .set("worthyPercent", dto.getWorthyPercent())
                    .set("createTime", dto.getCreateTime())
                    .set("updateTime", now);
            mongoTemplate.updateFirst(query, update, SmzdmItemDO.class);
//        } else {
//            log.info("PageId[{}] is noneffective. Skip it.", itemDO.getPageId());
        }
    }

    private boolean isEffective(SmzdmItemDO itemDO) {
        return itemDO.getCreateTime().isAfter(LocalDateTime.now().minusDays(1));
    }
}
