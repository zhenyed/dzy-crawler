package io.zhenye.crawler.service;

import io.zhenye.crawler.domain.data.SmzdmItemDO;
import io.zhenye.crawler.domain.dto.SmzdmQueryDTO;
import io.zhenye.crawler.dao.SmzdmItemRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Slf4j
@RequiredArgsConstructor
@Service
public class SmzdmItemService {

    private final SmzdmItemRepository smzdmItemRepository;
    private final MongoTemplate mongoTemplate;

    public Page<SmzdmItemDO> list(SmzdmQueryDTO dto) {
        if (StringUtils.isNotEmpty(dto.getTitle())) {
            Query query = Query
                    .query(Criteria.where("title").regex(dto.getTitle()))
                    .with(Sort.by(Sort.Direction.DESC, "createTime"))
                    .limit(dto.getPerPage())
                    .skip((long) (dto.getPage() - 1) * dto.getPerPage());
            return new PageImpl<>(mongoTemplate.find(query, SmzdmItemDO.class));
        }
        PageRequest pageRequest = PageRequest.of(dto.getPage() - 1, dto.getPerPage(), Sort.Direction.DESC, "createTime");
        return smzdmItemRepository.findAll(pageRequest);
    }

    public SmzdmItemDO get(Long pageId) {
        return smzdmItemRepository.findByPageId(pageId);
    }

    public List<SmzdmItemDO> listRanking() {
        Query query = Query
                .query(new Criteria()
                        .and("createTime").gte(LocalDateTime.now().minusDays(1))
                        .and("worthyPercent").gte(70)
                        .and("worthy").gte(20)
                ).with(Sort.by(Sort.Direction.DESC, "worthy", "worthyPercent"))
                .limit(10);
        return mongoTemplate.find(query, SmzdmItemDO.class);
    }
}
